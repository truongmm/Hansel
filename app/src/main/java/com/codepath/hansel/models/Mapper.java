package com.codepath.hansel.models;

import com.codepath.hansel.R;
import com.directions.route.Route;

import java.util.Date;
import java.util.List;

public class Mapper {
    private static Mapper instance;
    private List<User> users;
    private Date earliestDate = null;
    private int[] colors = new int[]{R.color.route_blue, R.color.route_orange, R.color.route_purple, R.color.route_red, R.color.route_teal, R.color.route_yellow, R.color.route_blue_grey};

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
        assignColors();
        setEarliestDate();
    }

    private void assignColors() {
        for (int i = 0; i < users.size(); i++) {
            users.get(i).setColor(colors[i % colors.length]);
        }
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
