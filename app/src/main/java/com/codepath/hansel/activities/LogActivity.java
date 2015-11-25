package com.codepath.hansel.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.hansel.R;
import com.codepath.hansel.fragments.FriendsLogFragment;
import com.codepath.hansel.fragments.LogFragment;
import com.codepath.hansel.fragments.YouLogFragment;
import com.codepath.hansel.models.Pebble;
import com.codepath.hansel.models.User;
import com.codepath.hansel.utils.DatabaseHelper;

import java.util.ArrayList;

public class LogActivity extends AppCompatActivity {
    private LogFragment logFragment;
    private DatabaseHelper dbHelper;
    private ArrayList<Pebble> pebbles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        dbHelper = DatabaseHelper.getInstance(this);
        pebbles = new ArrayList<>();
        dbHelper.deleteAllTables();
        stubData();
        setupViews(savedInstanceState);
    }

    private void stubData() {
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

    private void setupViews(Bundle savedInstanceState) {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new LogPagerAdapter(getSupportFragmentManager()));

        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabStrip.setViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if(id == R.id.action_map) {
            Intent intent = new Intent(LogActivity.this, MapActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public class LogPagerAdapter extends FragmentPagerAdapter {

        private String[] tabTitles = {"Friends", "You"};

        public LogPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // The order and creation of fragments within the pager
        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return new FriendsLogFragment();
            else if (position == 1)
                return new YouLogFragment();
            else
                return null;
        }

        // Return the tab title
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        // How many fragments there are to swipe between
        @Override
        public int getCount() {
            return tabTitles.length;
        }
    }
}
