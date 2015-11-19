package com.codepath.hansel.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.hansel.R;
import com.codepath.hansel.fragments.FriendsGeolocationFragment;
import com.codepath.hansel.fragments.GeolocationFragment;
import com.codepath.hansel.fragments.YouGeolocationFragment;

public class GeolocationActivity extends AppCompatActivity {

    private GeolocationFragment geolocationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geolocation);

        setupViews(savedInstanceState);
    }

    private void setupViews(Bundle savedInstanceState) {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new GeolocationPagerAdapter(getSupportFragmentManager()));

        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabStrip.setViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_geolocation, menu);
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
            Intent intent = new Intent(GeolocationActivity.this, MapActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public class GeolocationPagerAdapter extends FragmentPagerAdapter {

        private String[] tabTitles = {"Friends", "You"};

        public GeolocationPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // The order and creation of fragments within the pager
        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return new FriendsGeolocationFragment();
            else if (position == 1)
                return new YouGeolocationFragment();
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
