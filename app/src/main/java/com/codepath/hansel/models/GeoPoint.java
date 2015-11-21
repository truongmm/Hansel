package com.codepath.hansel.models;

import com.google.android.gms.maps.model.LatLng;

public class GeoPoint {

    private String name;
    private double latitude;
    private double longitude;
    private String timestamp;
    private LatLng latLng;

    public GeoPoint() {
    }

    public GeoPoint(String name, double latitude, double longitude, String timestamp) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.latLng = new LatLng(latitude, longitude);
    }

    public String getName() {
        return name;
    }

    public String getGeolocation() {
        return latitude + ", " + longitude;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public LatLng getLatLng() {
        return latLng;
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
