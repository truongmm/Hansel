package com.codepath.hansel.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.codepath.hansel.services.FetchPebblesService;

public class FetchPebblesReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 3;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, FetchPebblesService.class);
        context.startService(i);
    }
}