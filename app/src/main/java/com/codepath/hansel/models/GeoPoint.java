package com.codepath.hansel.models;

import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class GeoPoint {

    private String name;
    private long longitude;
    private long latitude;
    private String timestamp;

    public GeoPoint() {
    }

    public GeoPoint(String name, long longitude, long latitude, String timestamp) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public String getGeolocation() {
        return longitude + ", " + latitude;
    }

    public String getTimestamp() {
        return timestamp;
    }

//    public String getRelativeTimeAgo(String rawJsonDate) {
//        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
//        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
//        sf.setLenient(true);
//
//        String relativeDate = "";
//        try {
//            long dateMillis = sf.parse(rawJsonDate).getTime();
//            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
//                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
//        } catch (java.text.ParseException e) {
//            e.printStackTrace();
//        }
//
//        return relativeDate;
//    }
}
