package com.codepath.hansel.utils;

import android.text.format.DateUtils;

import java.util.Date;

public class TimeHelper {
    public static String getRelativeTimeAgo(Date date) {
        String relativeDate = "";
        try {
            long dateMillis = date.getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
//            relativeDate = abbrRelativeTime(relativeDate);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    private static String abbrRelativeTime(String relativeTime){
        String[] time;
        time = relativeTime.split(" ");

        if(time.length==3) relativeTime = time[0]+time[1].substring(0,1);

        return relativeTime;
    }
}
