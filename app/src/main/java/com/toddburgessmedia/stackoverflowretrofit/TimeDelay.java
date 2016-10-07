package com.toddburgessmedia.stackoverflowretrofit;

import org.joda.time.DateTime;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 17/06/16.
 */
public class TimeDelay {

//    private TimeDelay() {
//        throw new IllegalAccessError("Can not be instaniated");
//    }

    public static final int TODAY = 4;
    public static final int YESTERDAY = 3;
    public static final int THISMONTH = 2;
    public static final int THISYEAR = 1;
    public static final int ALLTIME = 0;

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

        DateTime now = new DateTime().withTimeAtStartOfDay();

        return now.getMillis() / 1000L;
    }

    private long getYesterday() {

        DateTime yesterday = new DateTime().minusDays(1);

        return yesterday.getMillis() / 1000L;
    }

    private static long getMonth() {

        DateTime month = new DateTime().withDayOfMonth(1);

        return month.getMillis() / 1000L;
    }

    private long getYear() {

        DateTime year = new DateTime().withDayOfYear(1);

        return year.getMillis() / 1000L;
    }


}
