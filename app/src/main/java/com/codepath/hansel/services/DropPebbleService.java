package com.codepath.hansel.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.codepath.hansel.models.Pebble;
import com.codepath.hansel.models.User;
import com.codepath.hansel.utils.DatabaseHelper;

public class DropPebbleService extends Service implements LocationListener {

    private DatabaseHelper dbHelper;
    private LocationManager locationManager;
    private User currentUser;
    private Location currentLocation;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        dbHelper = DatabaseHelper.getInstance(getApplicationContext());
        currentUser = dbHelper.getUser(1);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, this);

        if (currentLocation != null) {
            dbHelper.addPebble(new Pebble(currentUser, currentLocation.getLatitude(), currentLocation.getLongitude()));
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

//    public void showToast(final String message) {
//        Handler mHandler = new Handler(getMainLooper());
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

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
