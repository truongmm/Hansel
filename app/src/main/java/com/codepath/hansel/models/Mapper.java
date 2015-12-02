package com.codepath.hansel.models;

import java.util.Date;
import java.util.List;

public class Mapper {
    private List<User> users;

    public Mapper(List<User> users){
        this.users = users;
    }

    public Date getEarliestDate(){
        Date earliestDate = null;
        for (User user : users) {
            Date userEarliestDate = user.getEarliestDate();
            if (earliestDate == null || earliestDate.after(userEarliestDate)) {
                earliestDate = userEarliestDate;
            }
        }
        return earliestDate;
    }

    public List<User> getUsers() {
        return users;
    }
}
