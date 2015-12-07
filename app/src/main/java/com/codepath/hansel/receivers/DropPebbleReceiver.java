package com.codepath.hansel.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.codepath.hansel.services.DropPebbleService;

public class DropPebbleReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, DropPebbleService.class);
        context.startService(i);
    }
}