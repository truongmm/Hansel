package com.codepath.hansel.models;

import android.database.Cursor;

import com.codepath.hansel.utils.DatabaseHelper;
import com.codepath.hansel.utils.TimeHelper;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@ParseClassName("Pebble")
public class Pebble extends ParseObject {
    final DecimalFormat decimalFormat = new DecimalFormat("#.###");
    private int id;
    private User user;
    private Date date;
    private double latitude;
    private double longitude;
    private LatLng latLng;
    private String timestamp;
    private String status;

    public Pebble() {
        super();
    }

    public Pebble(User user, double latitude, double longitude, String timestamp, String status) {
        super();

        this.latLng = new LatLng(latitude, longitude);
        this.status = status;

        setUser(user);
        setLatitude(latitude);
        setLongitude(longitude);
        setTimestamp(timestamp);
    }

    public static Pebble fromDB(Cursor cursor) {
        Pebble pebble = new Pebble();

        try {
            pebble.id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_PEBBLE_ID));
            pebble.setTimestamp(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_PEBBLE_TIMESTAMP)));
            pebble.date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_PEBBLE_TIMESTAMP)));
            pebble.setLatitude(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.KEY_PEBBLE_LATITUDE)));
            pebble.setLongitude(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.KEY_PEBBLE_LONGITUDE)));
            pebble.latLng = new LatLng(pebble.latitude, pebble.longitude);
            pebble.status = cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_PEBBLE_STATUS));
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

    public int getId(){
        return id;
    }

    public User getUser(){
        return (User) getParseObject("user");
    }

    public void setUser(User user) {
        this.user = user;
        put("user", user);
    }

    public String getUserImageUrl() {
        return getUser().getImageUrl();
    }

    public double getLatitude(){
        return getDouble("latitude");
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
        put("latitude", latitude);
    }

    public double getLongitude(){
        return getDouble("longitude");
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
        put("longitude", longitude);
    }

    public String getTimestamp() {
        return getString("timestamp");
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        put("timestamp", timestamp);
    }

    public LatLng getLatLng() { return latLng; }

    public String getCoordinate() {
        decimalFormat.setRoundingMode(RoundingMode.CEILING);
        return decimalFormat.format(latitude) + ", " + decimalFormat.format(longitude);
    }

    public Date getDate() {
        return date;
    }

    public String getRelativeTimeAgo(){
        return TimeHelper.getRelativeTimeAgo(date);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
