package com.codepath.hansel.models;

import android.database.Cursor;

import com.codepath.hansel.utils.DatabaseHelper;
import com.directions.route.Route;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.Math.sqrt;

/**
 * Created by ryamada on 11/22/15.
 */
public class User {
    private int MAX_SAMPLE_POINTS = 3;

    private long id;
    private String firstName;
    private String lastName;
    private ArrayList<Pebble> pebbles;
    private Route route;
    private int color;

    public User() {
    }

    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public static User fromDB(Cursor cursor) {
        User user = new User();

        try {
            user.id = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.KEY_USER_ID));
            user.firstName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_USER_FIRST_NAME));
            user.lastName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_USER_LAST_NAME));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }

    public String toString() {
        return String.valueOf(id);
    }

    public long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public ArrayList<Pebble> getPebbles() {
        return pebbles;
    }

    public void setPebbles(ArrayList<Pebble> pebbles) {
        this.pebbles = pebbles;
    }

    public Date getEarliestDate() {
        return pebbles.get(0).getDate();
    }

    public ArrayList<LatLng> getLatLngs() {
        ArrayList<LatLng> latLngs = new ArrayList<>();
        for (Pebble pebble : pebbles) {
            latLngs.add(pebble.getLatLng());
        }
        return latLngs;
    }

    public double getRouteScore(Route sampleRoute) {
        double score = 0;
        ArrayList<LatLng> route1 = new ArrayList<>();
        ArrayList<LatLng> route2 = new ArrayList<>();
        if (route == null) {
            route1.add(pebbles.get(0).getLatLng());
            route1.add(pebbles.get(pebbles.size() - 1).getLatLng());

            List<LatLng> points = sampleRoute.getPoints();
            route2.add(points.get(0));
            route2.add(points.get(points.size() - 1));
        } else {
            route1 = new ArrayList<>(route.getPoints().subList(0, MAX_SAMPLE_POINTS));
            route2 = new ArrayList<>(sampleRoute.getPoints().subList(0, MAX_SAMPLE_POINTS));
        }

        for (int i = 0; i < route1.size(); i++) {
            score += getPointScore(route1.get(i), route2.get(i));
        }
        return score;
    }

    private double getPointScore(LatLng point1, LatLng point2) {
        double latDiff = point1.latitude - point2.latitude;
        double lngDiff = point1.longitude - point2.longitude;
        return sqrt(latDiff * latDiff + lngDiff * lngDiff);
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

}
