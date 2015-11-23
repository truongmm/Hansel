package com.codepath.hansel.models;

import android.database.Cursor;

import com.codepath.hansel.utils.DatabaseHelper;
import com.google.android.gms.maps.model.LatLng;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Pebble {
    private long id;
    private User user;
    private Date date;
    private double latitude;
    private double longitude;
    private LatLng latLng;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Pebble(){}

    public Pebble(User user, double latitude, double longitude){
        this.user = user;
        this.latitude = latitude;
        this.longitude = longitude;
        this.latLng = new LatLng(latitude, longitude);
    }

    public static Pebble fromDB(Cursor cursor) {
        Pebble pebble = new Pebble();

        try {
            pebble.id = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.KEY_PEBBLE_ID));
            pebble.date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_PEBBLE_TIMESTAMP)));
            pebble.latitude = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.KEY_PEBBLE_LATITUDE));
            pebble.longitude = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.KEY_PEBBLE_LONGITUDE));
            pebble.latLng = new LatLng(pebble.latitude, pebble.longitude);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pebble;
    }

//    public Pebble(long id, User user, Timestamp timestamp, double latitude, double longitude, Timestamp createdAt, Timestamp updatedAt) {
//        this.id = id;
//        this.user = user;
//        this.timestamp = timestamp;
//        this.latitude = latitude;
//        this.longitude = longitude;
//        this.latLng = new LatLng(latitude, longitude);
//        this.createdAt = createdAt;
//        this.updatedAt = updatedAt;
//    }

    public long getId(){
        return id;
    }

    public User getUser(){
        return user;
    }

    public void setUser(User user){
        this.user = user;
    }

    public double getLatitude(){
        return latitude;
    }

    public double getLongitude(){
        return longitude;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public Date getDate() {
        return date;
    }
}
