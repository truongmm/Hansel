package com.codepath.hansel.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.codepath.hansel.services.DropPebbleService;

public class PebbleReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;

    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, DropPebbleService.class);
        context.startService(i);
    }
}