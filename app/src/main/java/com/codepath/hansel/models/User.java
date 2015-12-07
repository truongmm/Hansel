package com.codepath.hansel.models;

import android.database.Cursor;
import android.location.Location;

import com.codepath.hansel.utils.DatabaseHelper;
import com.directions.route.Route;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.Math.sqrt;

@ParseClassName("User")
public class User extends ParseObject {
    final private int MAX_SAMPLE_POINTS = 3;
    final private double MINIMUM_DISTANCE_TO_NEW_POINT = 0.0005;

    private int id;
    private String parseId;
    private String firstName;
    private String lastName;
    private String imageUrl;
    private ArrayList<Pebble> pebbles;
    private Route route;
    private int color;
    private float hue;

    public User() {
        super();
    }

    public User(String firstName, String lastName, String imageUrl) {
        super();

        setFirstName(firstName);
        setLastName(lastName);
        setImageUrl(imageUrl);
    }

    public static User fromDB(Cursor cursor) {
        User user = new User();

        try {
            user.id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_USER_ID));
            user.parseId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_USER_PARSE_ID));
            user.setFirstName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_USER_FIRST_NAME)));
            user.setLastName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_USER_LAST_NAME)));
            user.setImageUrl(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_USER_IMAGE_URL)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }

    public String toString() {
        return String.valueOf(id);
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return getString("first_name");
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        put("first_name", firstName);
    }

    public String getLastName() {
        return getString("last_name");
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        put("last_name", lastName);
    }

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    public String getImageUrl() {
        return getString("image_url");
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        put("image_url", imageUrl);
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

    public float getHue() {
        return hue;
    }

    public void setHue(float hue) {
        this.hue = hue;
    }

    public boolean isValidNewLocation(Location newLocation) {
        if(pebbles.isEmpty()) return true;
        LatLng newPoint = new LatLng(newLocation.getLatitude(), newLocation.getLongitude());
        LatLng lastPoint = pebbles.get(pebbles.size() - 1).getLatLng();
        return getPointScore(lastPoint,newPoint) > MINIMUM_DISTANCE_TO_NEW_POINT;
    }
}
