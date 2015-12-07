package com.codepath.hansel.utils;

import android.app.Application;
import android.widget.Toast;

import com.codepath.hansel.models.Pebble;
import com.codepath.hansel.models.User;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
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
        User ruichuan = new User("Ruichuan", "Tong", "https://cloud.githubusercontent.com/assets/1873465/11618577/a8d77f44-9c51-11e5-9611-280047a55303.jpg");

        List<User> users = new ArrayList<>();
        users.add(ray);
        users.add(melody);
        users.add(calvin);
        users.add(ruichuan);

        ParseObject.saveAllInBackground(users, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(getApplicationContext(), "Users added", Toast.LENGTH_LONG).show();
            }
        });

        Pebble p1 = new Pebble(ray, 37.425237, -122.136492, getDate(new Date(System.currentTimeMillis() - 3600 * 1000)), "sent");
        Pebble p2 = new Pebble(ray, 37.422618, -122.132306, getDate(new Date(System.currentTimeMillis() - 3200 * 1000)), "sent");
        Pebble p3 = new Pebble(ray, 37.420556, -122.129216, getDate(new Date(System.currentTimeMillis() - 2800 * 1000)), "sent");
        Pebble p4 = new Pebble(ray, 37.416874, -122.129838, getDate(new Date(System.currentTimeMillis() - 2400 * 1000)), "sent");
        Pebble p5 = new Pebble(ray, 37.414676, -122.125826, getDate(new Date(System.currentTimeMillis() - 2000 * 1000)), "sent");
        Pebble p6 = new Pebble(ray, 37.415068, -122.123294, getDate(new Date(System.currentTimeMillis() - 1600 * 1000)), "sent");
        Pebble p7 = new Pebble(ray, 37.414897, -122.118444, getDate(new Date(System.currentTimeMillis() - 1200 * 1000)), "sent");
        Pebble p8 = new Pebble(ray, 37.416363, -122.116106, getDate(new Date(System.currentTimeMillis() - 800 * 1000)), "sent");
        Pebble p9 = new Pebble(ray, 37.418408, -122.113037, getDate(new Date(System.currentTimeMillis() - 400 * 1000)), "sent");
        Pebble p10 = new Pebble(ray, 37.420283, -122.110355, getDate(new Date(System.currentTimeMillis() - 0 * 1000)), "sent");

        Pebble p11 = new Pebble(melody, 37.425276, -122.136619, getDate(new Date(System.currentTimeMillis() - 3590 * 1000)), "sent");
        Pebble p12 = new Pebble(melody, 37.422941, -122.139516, getDate(new Date(System.currentTimeMillis() - 3190 * 1000)), "sent");
        Pebble p13 = new Pebble(melody, 37.420658, -122.137606, getDate(new Date(System.currentTimeMillis() - 2790 * 1000)), "sent");
        Pebble p14 = new Pebble(melody, 37.418868, -122.134473, getDate(new Date(System.currentTimeMillis() - 2390 * 1000)), "sent");
        Pebble p15 = new Pebble(melody, 37.418187, -122.132864, getDate(new Date(System.currentTimeMillis() - 1990 * 1000)), "sent");
        Pebble p16 = new Pebble(melody, 37.415238, -122.134366, getDate(new Date(System.currentTimeMillis() - 1590 * 1000)), "sent");
        Pebble p17 = new Pebble(melody, 37.412903, -122.136276, getDate(new Date(System.currentTimeMillis() - 1190 * 1000)), "sent");
        Pebble p18 = new Pebble(melody, 37.410671, -122.137992, getDate(new Date(System.currentTimeMillis() - 790 * 1000)), "sent");
        Pebble p19 = new Pebble(melody, 37.408898, -122.134666, getDate(new Date(System.currentTimeMillis() - 390 * 1000)), "sent");
        Pebble p20 = new Pebble(melody, 37.405966, -122.133744, getDate(new Date(System.currentTimeMillis() - 10 * 1000)), "sent");

        Pebble p21 = new Pebble(calvin, 37.425140, -122.136426, getDate(new Date(System.currentTimeMillis() - 3580 * 1000)), "sent");
        Pebble p22 = new Pebble(calvin, 37.425838, -122.138078, getDate(new Date(System.currentTimeMillis() - 3180 * 1000)), "sent");
        Pebble p23 = new Pebble(calvin, 37.424390, -122.140782, getDate(new Date(System.currentTimeMillis() - 2780 * 1000)), "sent");
        Pebble p24 = new Pebble(calvin, 37.423844, -122.143056, getDate(new Date(System.currentTimeMillis() - 2380 * 1000)), "sent");
        Pebble p25 = new Pebble(calvin, 37.424816, -122.146168, getDate(new Date(System.currentTimeMillis() - 1980 * 1000)), "sent");
        Pebble p26 = new Pebble(calvin, 37.422004, -122.148399, getDate(new Date(System.currentTimeMillis() - 1580 * 1000)), "sent");
        Pebble p27 = new Pebble(calvin, 37.422737, -122.153485, getDate(new Date(System.currentTimeMillis() - 1180 * 1000)), "sent");
        Pebble p28 = new Pebble(calvin, 37.419158, -122.156467, getDate(new Date(System.currentTimeMillis() - 780 * 1000)), "sent");
        Pebble p29 = new Pebble(calvin, 37.422481, -122.161274, getDate(new Date(System.currentTimeMillis() - 380 * 1000)), "sent");
        Pebble p30 = new Pebble(calvin, 37.423589, -122.165136, getDate(new Date(System.currentTimeMillis() - 20 * 1000)), "sent");

        Pebble p31 = new Pebble(ruichuan, 37.425429, -122.136426, getDate(new Date(System.currentTimeMillis() - 3570 * 1000)), "sent");
        Pebble p32 = new Pebble(ruichuan, 37.427423, -122.139773, getDate(new Date(System.currentTimeMillis() - 3170 * 1000)), "sent");
        Pebble p33 = new Pebble(ruichuan, 37.429263, -122.142134, getDate(new Date(System.currentTimeMillis() - 2770 * 1000)), "sent");
        Pebble p34 = new Pebble(ruichuan, 37.431359, -122.141576, getDate(new Date(System.currentTimeMillis() - 2370 * 1000)), "sent");
        Pebble p35 = new Pebble(ruichuan, 37.435244, -122.147906, getDate(new Date(System.currentTimeMillis() - 1970 * 1000)), "sent");
        Pebble p36 = new Pebble(ruichuan, 37.439316, -122.147884, getDate(new Date(System.currentTimeMillis() - 1570 * 1000)), "sent");
        Pebble p37 = new Pebble(ruichuan, 37.441752, -122.151725, getDate(new Date(System.currentTimeMillis() - 1170 * 1000)), "sent");
        Pebble p38 = new Pebble(ruichuan, 37.444785, -122.156789, getDate(new Date(System.currentTimeMillis() - 770 * 1000)), "sent");
        Pebble p39 = new Pebble(ruichuan, 37.447068, -122.160480, getDate(new Date(System.currentTimeMillis() - 370 * 1000)), "sent");
        Pebble p40 = new Pebble(ruichuan, 37.444904, -122.162561, getDate(new Date(System.currentTimeMillis() - 30 * 1000)), "sent");

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
        pebbles.add(p15);
        pebbles.add(p16);
        pebbles.add(p17);
        pebbles.add(p18);
        pebbles.add(p19);
        pebbles.add(p20);
        pebbles.add(p21);
        pebbles.add(p22);
        pebbles.add(p23);
        pebbles.add(p24);
        pebbles.add(p25);
        pebbles.add(p26);
        pebbles.add(p27);
        pebbles.add(p28);
        pebbles.add(p29);
        pebbles.add(p30);
        pebbles.add(p31);
        pebbles.add(p32);
        pebbles.add(p33);
        pebbles.add(p34);
        pebbles.add(p35);
        pebbles.add(p36);
        pebbles.add(p37);
        pebbles.add(p38);
        pebbles.add(p39);
        pebbles.add(p40);

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
