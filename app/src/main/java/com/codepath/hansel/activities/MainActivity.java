package com.codepath.hansel.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.hansel.R;
import com.codepath.hansel.fragments.MapFragment;
import com.codepath.hansel.fragments.SettingsFragment;
import com.codepath.hansel.fragments.TimelineFragment;
import com.codepath.hansel.models.Mapper;
import com.codepath.hansel.models.Pebble;
import com.codepath.hansel.models.User;
import com.codepath.hansel.receivers.DropPebbleReceiver;
import com.codepath.hansel.receivers.FetchPebblesReceiver;
import com.codepath.hansel.receivers.SendPebblesReceiver;
import com.codepath.hansel.utils.DatabaseHelper;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;

    private DatabaseHelper dbHelper;
    private Mapper mapper;
    private ArrayList<Pebble> pebbles;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(R.string.app_name);
        setupDrawer();
        loadDatabase();
        loadMapFragment();
        constructMapper();
        restoreSharedPreferences();
    }

    private void loadDatabase() {
        dbHelper = DatabaseHelper.getInstance(MainActivity.this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        loadUsers();
        loadPebbles();
    }

    private void loadUsers() {
        if (!dbHelper.isUsersTableEmpty())
            return;

        ParseQuery<User> query = ParseQuery.getQuery(User.class);
        try {
            List<User> users = query.find();
            for (User user: users)
               dbHelper.addOrUpdateUser(user);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void loadPebbles() {
        pebbles = new ArrayList<>();
        User currentUser = dbHelper.getUser(sharedPreferences.getInt("user_id", 1));
        ParseObject parseUser = ParseObject.createWithoutData("User", currentUser.getParseId());
        String latestTimestamp = dbHelper.getLatestPebbleTimestamp(new User[]{currentUser});

        ParseQuery query = new ParseQuery("Pebble");
        query.whereGreaterThan("timestamp", latestTimestamp);
        try {
            List<Pebble> parsePebbles = query.find();
            for (Pebble parsePebble : parsePebbles)
                dbHelper.addPebble(parsePebble, true);
            pebbles.addAll(dbHelper.getAllPebbles());
            Toast.makeText(MainActivity.this, "Pebbles fetched", Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        schedulePebblesFetching();
    }

    private void constructMapper() {
        List<User> users = new ArrayList<>();
        for (User user : dbHelper.getAllUsers()) {
            ArrayList<Pebble> pebbles = dbHelper.getPebblesForUsers(new User[]{user}, false, false);
            if (!pebbles.isEmpty()) {
                user.setPebbles(pebbles);
                users.add(user);
            }
        }
        mapper = Mapper.getInstance();
        mapper.setUsers(users);
    }

    private void loadMapFragment() {
        nvDrawer.getMenu().getItem(0).setChecked(true);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, new MapFragment()).commit();
    }

    private void setupDrawer() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        mDrawer.setDrawerListener(drawerToggle);

        setupDrawerContent();
    }

    private void setupDrawerContent() {
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        nvDrawer.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;

        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.nav_map:
                fragmentClass = MapFragment.class;
                break;
            case R.id.nav_timeline:
                fragmentClass = TimelineFragment.class;
                break;
            case R.id.nav_settings:
                fragmentClass = SettingsFragment.class;
                break;
            default:
                fragmentClass = MapFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void restoreSharedPreferences() {
        if (sharedPreferences.getBoolean("enable_tracking", false)) {
            schedulePebbleDrops();
            schedulePebblesSending();
        }
        else {
            stopPebbleDrops();
            stopPebblesSending();
        }
    }

    public void schedulePebbleDrops() {
        Intent intent = new Intent(MainActivity.this, DropPebbleReceiver.class);
        final PendingIntent pebbleDropIntent = PendingIntent.getBroadcast(MainActivity.this, DropPebbleReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager pebbleDropAlarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int pebbleDropInterval = 1000 * sharedPreferences.getInt("pebble_drop_interval", 15);
        Toast.makeText(MainActivity.this, "Drop pebble service interval is " + (pebbleDropInterval / 1000) + " secs", Toast.LENGTH_SHORT).show();
        pebbleDropAlarm.setRepeating(AlarmManager.RTC_WAKEUP, firstMillis, pebbleDropInterval, pebbleDropIntent);
    }

    public void stopPebbleDrops() {
        Intent intent = new Intent(MainActivity.this, DropPebbleReceiver.class);
        final PendingIntent pebbleDropIntent = PendingIntent.getBroadcast(MainActivity.this, DropPebbleReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager pebbleDropAlarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Toast.makeText(MainActivity.this, "Drop pebble service stopped", Toast.LENGTH_SHORT).show();
        pebbleDropAlarm.cancel(pebbleDropIntent);
    }

    public void schedulePebblesSending() {
        Intent intent = new Intent(MainActivity.this, SendPebblesReceiver.class);
        final PendingIntent pebbleDropIntent = PendingIntent.getBroadcast(MainActivity.this, SendPebblesReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager sendPebblesAlarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int sendPebblesInterval = 1000 * sharedPreferences.getInt("send_pebbles_interval", 15);
        Toast.makeText(MainActivity.this, "Send pebbles service interval is " + (sendPebblesInterval / 1000) + " secs", Toast.LENGTH_SHORT).show();
        sendPebblesAlarm.setRepeating(AlarmManager.RTC_WAKEUP, firstMillis, sendPebblesInterval, pebbleDropIntent);
    }

    public void stopPebblesSending() {
        Intent intent = new Intent(MainActivity.this, SendPebblesReceiver.class);
        final PendingIntent pebbleDropIntent = PendingIntent.getBroadcast(MainActivity.this, SendPebblesReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager sendPebblesAlarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Toast.makeText(MainActivity.this, "Send pebbles service stopped", Toast.LENGTH_SHORT).show();
        sendPebblesAlarm.cancel(pebbleDropIntent);
    }

    public void schedulePebblesFetching() {
        Intent intent = new Intent(MainActivity.this, FetchPebblesReceiver.class);
        final PendingIntent pebbleDropIntent = PendingIntent.getBroadcast(MainActivity.this, FetchPebblesReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager fetchPebblesAlarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int fetchPebblesInterval = 1000 * sharedPreferences.getInt("fetch_pebbles_interval", 15);
        Toast.makeText(MainActivity.this, "Fetch pebbles service interval is " + (fetchPebblesInterval / 1000) + " secs", Toast.LENGTH_SHORT).show();
        fetchPebblesAlarm.setRepeating(AlarmManager.RTC_WAKEUP, firstMillis, fetchPebblesInterval, pebbleDropIntent);
    }

    public void stopPebblesFetching() {
        Intent intent = new Intent(MainActivity.this, FetchPebblesReceiver.class);
        final PendingIntent pebbleDropIntent = PendingIntent.getBroadcast(MainActivity.this, FetchPebblesReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager fetchPebblesAlarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Toast.makeText(MainActivity.this, "Fetch pebbles service stopped", Toast.LENGTH_SHORT).show();
        fetchPebblesAlarm.cancel(pebbleDropIntent);
    }
}