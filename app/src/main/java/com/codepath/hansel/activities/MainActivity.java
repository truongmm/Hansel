package com.codepath.hansel.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.codepath.hansel.R;
import com.codepath.hansel.fragments.MapFragment;
import com.codepath.hansel.fragments.TimelineFragment;
import com.codepath.hansel.models.Pebble;
import com.codepath.hansel.models.User;
import com.codepath.hansel.utils.DatabaseHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;

    private DatabaseHelper dbHelper;
    private ArrayList<Pebble> pebbles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(R.string.app_name);
        setupDrawer();
        loadMapFragment();
        stubData();
    }

    private void stubData() {
        dbHelper = DatabaseHelper.getInstance(MainActivity.this);
        pebbles = new ArrayList<>();
        dbHelper.deleteAllTables();

        long rayId = dbHelper.addOrUpdateUser(new User("Ray", "Yamada"));
        User ray = dbHelper.getUser(rayId);
        dbHelper.addPebble(new Pebble(ray, 37.414479, -122.126450));
        dbHelper.addPebble(new Pebble(ray, 37.417240, -122.130484));
        dbHelper.addPebble(new Pebble(ray, 37.419652, -122.135054));
        dbHelper.addPebble(new Pebble(ray, 37.423725, -122.134335));
        dbHelper.addPebble(new Pebble(ray, 37.425105, -122.136588));

        long melodyId = dbHelper.addOrUpdateUser(new User("Melody", "Truong"));
        User melody = dbHelper.getUser(melodyId);
        dbHelper.addPebble(new Pebble(melody, 37.426255, -122.134657));
        dbHelper.addPebble(new Pebble(melody, 37.430055, -122.135000));
        dbHelper.addPebble(new Pebble(melody, 37.424406, -122.144155));
        dbHelper.addPebble(new Pebble(melody, 37.428351, -122.143050));
        dbHelper.addPebble(new Pebble(melody, 37.424977, -122.136441));

        long calvinId = dbHelper.addOrUpdateUser(new User("Calvin", "Liang"));
        User calvin = dbHelper.getUser(calvinId);
        dbHelper.addPebble(new Pebble(calvin, 37.411322, -122.139422));
        dbHelper.addPebble(new Pebble(calvin, 37.420619, -122.149583));
        dbHelper.addPebble(new Pebble(calvin, 37.426655, -122.150271));
        dbHelper.addPebble(new Pebble(calvin, 37.424977, -122.136441));
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
            case R.id.nav_timeline:
                fragmentClass = TimelineFragment.class;
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
}