package com.codepath.hansel.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.codepath.hansel.services.SendPebblesService;

public class SendPebblesReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 2;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, SendPebblesService.class);
        context.startService(i);
    }
}