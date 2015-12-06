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
import android.util.Log;
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
import com.codepath.hansel.receivers.PebbleReceiver;
import com.codepath.hansel.utils.DatabaseHelper;
import com.parse.ParseException;
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
        loadUsers();
        loadPebbles();
    }

    private void loadUsers() {
        boolean isUsersTableEmpty = dbHelper.isUsersTableEmpty();
        if (!isUsersTableEmpty)
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

        List<Pebble> dbPebbles = dbHelper.getAllPebbles();
        String latestTimestamp = dbHelper.getLatestPebbleTimestamp();
        ParseQuery query = new ParseQuery("Pebble");
        query.whereGreaterThan("timestamp", latestTimestamp);
        try {
            List<Pebble> parsePebbles = query.find();
            for (Pebble parsePebble : parsePebbles)
                dbHelper.addPebble(parsePebble, true);
            dbPebbles = dbHelper.getAllPebbles();
            pebbles.addAll(dbHelper.getAllPebbles());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        latestTimestamp = dbHelper.getLatestPebbleTimestamp();
        Log.d("timestamp", latestTimestamp);
    }

    private void constructMapper() {
        List<User> users = new ArrayList<>();
        for (User user : dbHelper.getAllUsers()) {
            ArrayList<Pebble> pebbles = dbHelper.getPebblesForUsers(new User[]{user}, false);
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
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        if (sharedPreferences.getBoolean("enable_tracking", false))
            schedulePebbleDrops();
        else
            stopPebbleDrops();
    }

    public void schedulePebbleDrops() {
        Intent intent = new Intent(MainActivity.this, PebbleReceiver.class);
        final PendingIntent pebbleDropIntent = PendingIntent.getBroadcast(MainActivity.this, PebbleReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager pebbleDropAlarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int pebbleDropInterval = 1000 * sharedPreferences.getInt("pebble_drop_interval", 15);
        Toast.makeText(MainActivity.this, "Pebble service interval is " + (pebbleDropInterval / 1000) + " secs", Toast.LENGTH_SHORT).show();
        pebbleDropAlarm.setRepeating(AlarmManager.RTC_WAKEUP, firstMillis, pebbleDropInterval, pebbleDropIntent);
    }

    public void stopPebbleDrops() {
        Intent intent = new Intent(MainActivity.this, PebbleReceiver.class);
        final PendingIntent pebbleDropIntent = PendingIntent.getBroadcast(MainActivity.this, PebbleReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager pebbleDropAlarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Toast.makeText(MainActivity.this, "Pebble service stopped", Toast.LENGTH_SHORT).show();
        pebbleDropAlarm.cancel(pebbleDropIntent);
    }
}