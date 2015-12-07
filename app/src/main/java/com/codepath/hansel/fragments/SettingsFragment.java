package com.codepath.hansel.fragments;

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
import com.codepath.hansel.activities.MainActivity;
import com.codepath.hansel.models.User;
import com.codepath.hansel.utils.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SettingsFragment extends DialogFragment {
    HashMap<String,Integer> userNamesAndIds;
    final String[] VALID_PEBBLE_SERVICE_INTERVALS = {"5 secs", "15 secs", "30 secs", "1 min", "5 mins", "15 mins"};

    DatabaseHelper dbHelper;
    SharedPreferences sharedPreferences;
    Switch swtchTracking;
    Spinner spnrUserId;
    Spinner spnrPebbleDropInterval;
    Spinner spnrSendPebblesInterval;
    Spinner spnrFetchPebblesInterval;
    Button btnSave;

    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = DatabaseHelper.getInstance(getActivity());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        setupViews(view);
        return view;
    }

    private void setupViews(View view) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        swtchTracking = (Switch) view.findViewById(R.id.swtchTracking);
        swtchTracking.setChecked(sharedPreferences.getBoolean("enable_tracking", false));

        spnrUserId = (Spinner) view.findViewById(R.id.spnrUserId);
        userNamesAndIds = getUsersInfo();
        List<String> userNames = new ArrayList<>(userNamesAndIds.keySet());
        ArrayAdapter<String> userIdAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, userNames);
        spnrUserId.setAdapter(userIdAdapter);
        spnrUserId.setSelection(getSelectedUserPosition());

        spnrPebbleDropInterval = (Spinner) view.findViewById(R.id.spnrPebbleDropInterval);
        setupPebbleServiceSpinner(spnrPebbleDropInterval, "pebble_drop_interval");

        spnrSendPebblesInterval = (Spinner) view.findViewById(R.id.spnrSendPebblesInterval);
        setupPebbleServiceSpinner(spnrSendPebblesInterval, "send_pebbles_interval");

        spnrFetchPebblesInterval = (Spinner) view.findViewById(R.id.spnrFetchPebblesInterval);
        setupPebbleServiceSpinner(spnrFetchPebblesInterval, "fetch_pebbles_interval");

        btnSave = (Button) view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePreferences();
                Toast.makeText(getActivity(), "Successfully Saved", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setupPebbleServiceSpinner(Spinner spinner, String servicePreferenceName) {
        ArrayAdapter<String> pebbleServiceIntervalAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, VALID_PEBBLE_SERVICE_INTERVALS);
        spinner.setAdapter(pebbleServiceIntervalAdapter);
        spinner.setSelection(getSavedPebbleServiceInterval(servicePreferenceName));
    }

    public HashMap<String,Integer> getUsersInfo() {
        HashMap<String,Integer> userNamesAndIds = new HashMap<>();
        for (User user: dbHelper.getAllUsers())
            userNamesAndIds.put(user.getFirstName(), user.getId());
        return userNamesAndIds;
    }

    private int getSelectedUserId() {
        return userNamesAndIds.get(spnrUserId.getSelectedItem().toString());
    }

    private int getSelectedUserPosition() {
        List<Integer> userIds = new ArrayList<>(userNamesAndIds.values());
        return userIds.indexOf(sharedPreferences.getInt("user_id", 1));
    }

    private int getSelectedPebbleServiceInterval(Spinner spinner) {
        int pebbleServiceInterval = 15;
        switch (spinner.getSelectedItem().toString()) {
            case "5 secs":
                pebbleServiceInterval = 5;
                break;
            case "15 secs":
                pebbleServiceInterval = 15;
                break;
            case "30 secs":
                pebbleServiceInterval = 30;
                break;
            case "1 min":
                pebbleServiceInterval = 60;
                break;
            case "5 mins":
                pebbleServiceInterval = 300;
                break;
            case "15 mins":
                pebbleServiceInterval = 900;
                break;
        }
        return pebbleServiceInterval;
    }

    private int getSavedPebbleServiceInterval(String servicePreferenceName) {
        int pebbleServiceInterval = sharedPreferences.getInt(servicePreferenceName, 5);
        int dropDownPosition = 0;
        switch (pebbleServiceInterval) {
            case 5:
                dropDownPosition = 0;
                break;
            case 15:
                dropDownPosition = 1;
                break;
            case 30:
                dropDownPosition = 2;
                break;
            case 60:
                dropDownPosition = 3;
                break;
            case 300:
                dropDownPosition = 4;
                break;
            case 900:
                dropDownPosition = 5;
                break;
        }
        return dropDownPosition;
    }

    private void savePreferences() {
        boolean enableTracking = swtchTracking.isChecked();
        int userId = getSelectedUserId();
        int pebbleDropInterval = getSelectedPebbleServiceInterval(spnrPebbleDropInterval);
        int sendPebblesInterval = getSelectedPebbleServiceInterval(spnrSendPebblesInterval);
        int fetchPebblesInterval = getSelectedPebbleServiceInterval(spnrFetchPebblesInterval);

        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
        preferencesEditor.putBoolean("enable_tracking", enableTracking);
        preferencesEditor.putInt("user_id", userId);
        preferencesEditor.putInt("pebble_drop_interval", pebbleDropInterval);
        preferencesEditor.putInt("send_pebbles_interval", sendPebblesInterval);
        preferencesEditor.putInt("fetch_pebbles_interval", fetchPebblesInterval);
        preferencesEditor.commit();

        MainActivity mainActivity = (MainActivity) getContext();
        if (sharedPreferences.getBoolean("enable_tracking", false)) {
            mainActivity.schedulePebbleDrops();
            mainActivity.schedulePebblesSending();
            mainActivity.schedulePebblesFetching();
        }
        else {
            mainActivity.stopPebbleDrops();
            mainActivity.stopPebblesSending();
            mainActivity.stopPebblesFetching();
        }
    }
}
