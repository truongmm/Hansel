package com.codepath.hansel.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.codepath.hansel.R;
import com.codepath.hansel.receivers.PebbleReceiver;

public class SettingsFragment extends DialogFragment {
    final String[] VALID_USER_IDS = {"1 (Ray)", "2 (Melody)", "3 (Calvin)"};
    final String[] VALID_PEBBLE_DROP_INTERVALS = {"15 secs", "30 secs", "1 min", "5 mins", "15 mins"};

    SharedPreferences sharedPreferences;
    Switch swtchTracking;
    Spinner spnrUserId;
    Spinner spnrPebbleDropInterval;
    Button btnSave;

    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        setupViews(view);
        getDialog().setTitle("Settings");
        return view;
    }

    private void setupViews(View view) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        swtchTracking = (Switch) view.findViewById(R.id.swtchTracking);
        swtchTracking.setChecked(sharedPreferences.getBoolean("enable_tracking", false));

        spnrUserId = (Spinner) view.findViewById(R.id.spnrUserId);
        ArrayAdapter<String> userIdAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, VALID_USER_IDS);
        spnrUserId.setAdapter(userIdAdapter);
        spnrUserId.setSelection(getSavedUserId());

        spnrPebbleDropInterval = (Spinner) view.findViewById(R.id.spnrPebbleDropInterval);
        ArrayAdapter<String> pebbleDropIntervalAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, VALID_PEBBLE_DROP_INTERVALS);
        spnrPebbleDropInterval.setAdapter(pebbleDropIntervalAdapter);
        spnrPebbleDropInterval.setSelection(getSavedPebbleDropInterval());

        btnSave = (Button) view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePreferences();
                getDialog().dismiss();
            }
        });
    }

    private int getSelectedUserId() {
        int userId = 1;
        switch (spnrUserId.getSelectedItem().toString()) {
            case "1 (Ray)":
                userId = 1;
                break;
            case "2 (Melody)":
                userId = 2;
                break;
            case "3 (Calvin)":
                userId = 3;
                break;
        }
        return userId;
    }

    private int getSavedUserId() {
        return sharedPreferences.getInt("user_id", 1) - 1;
    }

    private int getSelectedPebbleDropInterval() {
        int pebbleDropInterval = 15;
        switch (spnrPebbleDropInterval.getSelectedItem().toString()) {
            case "15 secs":
                pebbleDropInterval = 15;
                break;
            case "30 secs":
                pebbleDropInterval = 30;
                break;
            case "1 min":
                pebbleDropInterval = 60;
                break;
            case "5 mins":
                pebbleDropInterval = 300;
                break;
            case "15 mins":
                pebbleDropInterval = 900;
                break;
        }
        return pebbleDropInterval;
    }

    private int getSavedPebbleDropInterval() {
        int pebbleDropInterval = sharedPreferences.getInt("pebble_drop_interval", 15);
        int dropDownPosition = 0;
        switch (pebbleDropInterval) {
            case 15:
                dropDownPosition = 0;
                break;
            case 30:
                dropDownPosition = 1;
                break;
            case 60:
                dropDownPosition = 2;
                break;
            case 300:
                dropDownPosition = 3;
                break;
            case 900:
                dropDownPosition = 4;
                break;
        }
        return dropDownPosition;
    }

    private void savePreferences() {
        boolean enableTracking = swtchTracking.isChecked();
        int userId = getSelectedUserId();
        int pebbleDropInterval = getSelectedPebbleDropInterval();

        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
        preferencesEditor.putBoolean("enable_tracking", enableTracking);
        preferencesEditor.putInt("user_id", userId);
        preferencesEditor.putInt("pebble_drop_interval", pebbleDropInterval);
        preferencesEditor.commit();

        if (sharedPreferences.getBoolean("enable_tracking", false))
            schedulePebbleDrops();
        else
            stopPebbleDrops();
    }

    private void schedulePebbleDrops() {
        Intent intent = new Intent(getContext(), PebbleReceiver.class);
        final PendingIntent pebbleDropIntent = PendingIntent.getBroadcast(getContext(), PebbleReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager pebbleDropAlarm = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        int pebbleDropInterval = 1000 * sharedPreferences.getInt("pebble_drop_interval", 15);
        Toast.makeText(getActivity(), "Pebble service interval is " + (pebbleDropInterval/1000) + " secs", Toast.LENGTH_SHORT).show();
        pebbleDropAlarm.setRepeating(AlarmManager.RTC_WAKEUP, firstMillis, pebbleDropInterval, pebbleDropIntent);
    }

    private void stopPebbleDrops() {
        Intent intent = new Intent(getContext(), PebbleReceiver.class);
        final PendingIntent pebbleDropIntent = PendingIntent.getBroadcast(getContext(), PebbleReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager pebbleDropAlarm = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Toast.makeText(getActivity(), "Pebble service stopped", Toast.LENGTH_SHORT).show();
        pebbleDropAlarm.cancel(pebbleDropIntent);
    }
}
