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

    public static String getShortRelativeTimeAgo(Date date) {
        return shortRelativeTime(getRelativeTimeAgo(date));
    }

    private static String shortRelativeTime(String relativeTime){
        return relativeTime.replace("second","sec").replace("minute","min");
    }

    private static String abbrRelativeTime(String relativeTime){
        String[] time;
        time = relativeTime.split(" ");

        if(time.length==3) {
            relativeTime = time[0]+time[1].substring(0,1);
            if(time[2].equals("ago")){
                relativeTime = "-" + relativeTime;
            }
        }

        return relativeTime;
    }
}
