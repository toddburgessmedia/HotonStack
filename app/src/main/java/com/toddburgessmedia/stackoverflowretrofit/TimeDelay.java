package com.toddburgessmedia.stackoverflowretrofit;

import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.Duration;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 17/06/16.
 */
public class TimeDelay {

//    private TimeDelay() {
//        throw new IllegalAccessError("Can not be instaniated");
//    }

    public static final int TODAY = 0;
    public static final int YESTERDAY = 1;
    public static final int THISMONTH = 2;
    public static final int THISYEAR = 3;

    public long getTimeDelay(int timedelay) {

        switch (timedelay) {
            case TODAY:
                return getToday();
            case YESTERDAY:
                return getYesterday();
            case THISMONTH:
                return getMonth();
            case THISYEAR:
                return getYear();
        }
        return 0L;

    }

    private long getToday() {

        DateTime now = new DateTime();
        DateTime midnight = now.withTimeAtStartOfDay();
        Duration duration = new Duration(midnight,now);

        Log.d(MainActivity.TAG, "getToday: " + duration.toStandardSeconds().getSeconds());
        //return duration.toStandardSeconds().getSeconds();

        return (now.getSecondOfDay() - duration.toStandardSeconds().getSeconds()) * 1000;
    }

    private long getYesterday() {

        DateTime yesterday = new DateTime().minusDays(1);
        Log.d(MainActivity.TAG, "getYesterday: " + yesterday.getMillis() / 1000);

        return yesterday.getMillis() / 1000L;

    }

    private static long getMonth() {

        DateTime month = new DateTime().withDayOfMonth(1);
//        DateTime month = now.withDayOfMonth(1);

        return month.getMillis() / 1000L;
    }

    private long getYear() {

        DateTime year = new DateTime().withDayOfYear(1);

        return year.getMillis() / 1000L;

    }


}
