package com.codepath.hansel.models;

import com.codepath.hansel.R;
import com.directions.route.Route;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Mapper {
    private static Mapper instance;
    private List<User> users;
    private Date earliestDate = null;
    private int[] colors = new int[]{R.color.route_orange, R.color.route_red, R.color.route_green, R.color.route_blue, R.color.route_purple, R.color.route_yellow, R.color.route_cyan};
    private float[] hues = new float[]{BitmapDescriptorFactory.HUE_ORANGE, BitmapDescriptorFactory.HUE_RED, BitmapDescriptorFactory.HUE_GREEN, BitmapDescriptorFactory.HUE_BLUE, BitmapDescriptorFactory.HUE_VIOLET, BitmapDescriptorFactory.HUE_YELLOW, BitmapDescriptorFactory.HUE_CYAN};
    private HashMap<Integer,Integer> userColor;

    public Mapper() {
    }

    public static Mapper getInstance() {
        if (instance == null) {
            instance = new Mapper();
        }
        return instance;
    }

    public void setUsers(List<User> users) {
        this.users = users;
        assignColorsAndHues();
        setEarliestDate();
    }

    private void assignColorsAndHues() {
        userColor = new HashMap<>();
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            int index = i % colors.length;
            user.setColor(colors[index]);
            user.setHue(hues[index]);
            userColor.put((int) user.getId(), colors[index]);
        }
    }

    public int getColorForUser(User user){
        return userColor.get((int) user.getId());
    }

    public void setEarliestDate() {
        for (User user : users) {
            Date userEarliestDate = user.getEarliestDate();
            if (earliestDate == null || earliestDate.after(userEarliestDate)) {
                earliestDate = userEarliestDate;
            }
        }
    }

    public Date getEarliestDate() {
        return earliestDate;
    }

    public List<User> getUsers() {
        return users;
    }

    public User getBestUserForRoute(Route route) {
        double bestScore = Double.MAX_VALUE;
        double score;
        User bestUser = null;
        for (User user : users) {
            score = user.getRouteScore(route);
            if (score < bestScore) {
                bestScore = score;
                bestUser = user;
            }
        }
        return bestUser;
    }

}
