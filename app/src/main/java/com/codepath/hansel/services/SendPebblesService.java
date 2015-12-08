package com.codepath.hansel.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.codepath.hansel.models.Pebble;
import com.codepath.hansel.models.User;
import com.codepath.hansel.utils.DatabaseHelper;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.List;

public class SendPebblesService extends Service {

    private DatabaseHelper dbHelper;
    private User currentUser;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        // showToast("Send pebbles service started");

        dbHelper = DatabaseHelper.getInstance(getApplicationContext());
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        currentUser = dbHelper.getUser(sharedPreferences.getInt("user_id", 1));
        List<Pebble> dbPebbles = dbHelper.getPebblesForUsers(new User[]{currentUser}, false, true);

        if (dbPebbles.size() > 0) {
            for (Pebble pebble: dbPebbles)
            {
                // Mark pebble as sent
                dbHelper.updatePebbleStatus(pebble);

                // Set pointer to parse user and send to parse
                User user = pebble.getUser();
                ParseObject parseUser = ParseObject.createWithoutData("User", user.getParseId());
                pebble.put("user", parseUser);
                try {
                    pebble.save();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                pebble.setUser(user);
            }

            // Toast.makeText(getApplicationContext(), "DB pebbles added", Toast.LENGTH_LONG).show();
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
