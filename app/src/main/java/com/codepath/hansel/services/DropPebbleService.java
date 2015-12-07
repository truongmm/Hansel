package com.codepath.hansel.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.codepath.hansel.models.Pebble;
import com.codepath.hansel.models.User;
import com.codepath.hansel.utils.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DropPebbleService extends Service implements LocationListener {

    private DatabaseHelper dbHelper;
    private LocationManager locationManager;
    private Location currentLocation;
    private User currentUser;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        showToast("Pebble drop service started");

        dbHelper = DatabaseHelper.getInstance(getApplicationContext());
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        currentUser = dbHelper.getUser(sharedPreferences.getInt("user_id", 1));
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, this);

        if (currentLocation != null) {
            ArrayList<Pebble> pebbles = dbHelper.getPebblesForUsers(new User[]{currentUser}, false, false);
            if (!pebbles.isEmpty()) {
                currentUser.setPebbles(pebbles);
            }
            if(currentUser.isValidNewLocation(currentLocation)){
                String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                Pebble pebble = new Pebble(currentUser, currentLocation.getLatitude(), currentLocation.getLongitude(), currentTime, "pending");
                pebble.setUser(currentUser);
                dbHelper.addPebble(pebble, false);
                showToast("Pebble dropped: " + pebble.getCoordinate());
            }else{
                showToast("Pebble too close to last location.");
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void showToast(final String message) {
        Handler mHandler = new Handler(getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
