package com.codepath.hansel.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.codepath.hansel.activities.MainActivity;
import com.codepath.hansel.models.Pebble;
import com.codepath.hansel.models.User;
import com.codepath.hansel.utils.DatabaseHelper;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class FetchPebblesService extends Service {

    private DatabaseHelper dbHelper;
    private User currentUser;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        // showToast("Fetch pebbles service started");

        dbHelper = DatabaseHelper.getInstance(getApplicationContext());
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        currentUser = dbHelper.getUser(sharedPreferences.getInt("user_id", 1));
        ParseObject parseUser = ParseObject.createWithoutData("User", currentUser.getParseId());
        String latestTimestamp = dbHelper.getLatestPebbleTimestamp(new User[]{currentUser});

        ParseQuery query = new ParseQuery("Pebble");
        query.whereNotEqualTo("user", parseUser);
        query.whereGreaterThan("timestamp", latestTimestamp);
        try {
            List<Pebble> parsePebbles = query.find();
            for (Pebble parsePebble : parsePebbles)
                dbHelper.addPebble(parsePebble, true);
            // showToast("Pebbles fetched successfully");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
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
}
