package com.codepath.hansel.utils;

import android.app.Application;
import android.widget.Toast;

import com.codepath.hansel.models.Pebble;
import com.codepath.hansel.models.User;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HanselApplication extends Application {
    public static final String YOUR_APPLICATION_ID = "G90AKylnBXgF83xjYOkertN6WDxzjWqltOVVaKeG";
    public static final String YOUR_CLIENT_KEY = "aaXiWmC5P0l3o50QWYpKmXWONvuJ1Yv4dNWB9sVL";

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Pebble.class);
        ParseObject.registerSubclass(User.class);

        Parse.initialize(this, YOUR_APPLICATION_ID, YOUR_CLIENT_KEY);

        // stubParseData();
    }

    public void stubParseData() {
        User ray = new User("Ray", "Yamada", "https://scontent-lax3-1.xx.fbcdn.net/hprofile-xta1/v/t1.0-1/c2.0.597.597/s160x160/10440731_10102381960663565_7531332407190893695_n.jpg?oh=d1e2105305aed67ce1285dcbe41b5b58&oe=56EADF76");
        User melody = new User("Melody", "Truong", "https://scontent-lax3-1.xx.fbcdn.net/hprofile-xfa1/v/t1.0-1/p160x160/12196191_907745375939172_2342649154846833771_n.jpg?oh=70c8b096a894a43df4fcea52af91f303&oe=56F1B9C0");
        User calvin = new User("Calvin", "Liang", "https://scontent-lax3-1.xx.fbcdn.net/hprofile-xtp1/v/t1.0-1/c2.172.716.716/s160x160/1520727_10202480658391510_5012412434444932969_n.jpg?oh=a6ce9c8146b391ec0557493bbec8d626&oe=56D653AB");

        List<User> users = new ArrayList<>();
        users.add(ray);
        users.add(melody);
        users.add(calvin);

        ParseObject.saveAllInBackground(users, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(getApplicationContext(), "Users added", Toast.LENGTH_LONG).show();
            }
        });

        Pebble p1 = new Pebble(ray, 37.425105, -122.136588, getDate(new Date(System.currentTimeMillis() - 3600 * 1000)));
        Pebble p2 = new Pebble(ray, 37.423725, -122.134335, getDate(new Date(System.currentTimeMillis() - 2700 * 1000)));
        Pebble p3 = new Pebble(ray, 37.419652, -122.135054, getDate(new Date(System.currentTimeMillis() - 1300 * 1000)));
        Pebble p4 = new Pebble(ray, 37.417240, -122.130484, getDate(new Date(System.currentTimeMillis() - 500 * 1000)));
        Pebble p5 = new Pebble(ray, 37.414479, -122.126450, getDate(new Date(System.currentTimeMillis() - 20 * 1000)));

        Pebble p6 = new Pebble(melody, 37.424977, -122.136441, getDate(new Date(System.currentTimeMillis() - 3550 * 1000)));
        Pebble p7 = new Pebble(melody, 37.428351, -122.143050, getDate(new Date(System.currentTimeMillis() - 2640 * 1000)));
        Pebble p8 = new Pebble(melody, 37.424406, -122.144155, getDate(new Date(System.currentTimeMillis() - 1250 * 1000)));
        Pebble p9 = new Pebble(melody, 37.430055, -122.135000, getDate(new Date(System.currentTimeMillis() - 400 * 1000)));
        Pebble p10 = new Pebble(melody, 37.426255, -122.134657, getDate(new Date(System.currentTimeMillis() - 10 * 1000)));

        Pebble p11 = new Pebble(calvin, 37.425005, -122.136841, getDate(new Date(System.currentTimeMillis() - 3250 * 1000)));
        Pebble p12 = new Pebble(calvin, 37.426655, -122.150271, getDate(new Date(System.currentTimeMillis() - 2500 * 1000)));
        Pebble p13 = new Pebble(calvin, 37.420619, -122.149583, getDate(new Date(System.currentTimeMillis() - 1400 * 1000)));
        Pebble p14 = new Pebble(calvin, 37.411322, -122.139422, getDate(new Date(System.currentTimeMillis() - 30 * 1000)));

        List<Pebble> pebbles = new ArrayList<>();
        pebbles.add(p1);
        pebbles.add(p2);
        pebbles.add(p3);
        pebbles.add(p4);
        pebbles.add(p5);
        pebbles.add(p6);
        pebbles.add(p7);
        pebbles.add(p8);
        pebbles.add(p9);
        pebbles.add(p10);
        pebbles.add(p11);
        pebbles.add(p12);
        pebbles.add(p13);
        pebbles.add(p14);

        ParseObject.saveAllInBackground(pebbles, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(getApplicationContext(), "Pebbles added", Toast.LENGTH_LONG).show();
            }
        });
    }

    public String getDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }
}
