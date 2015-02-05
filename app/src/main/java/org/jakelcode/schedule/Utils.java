package org.jakelcode.schedule;

import android.content.Context;
import android.text.format.DateUtils;

import java.util.Calendar;

/**
 * @author Pin Khe "Jake" Loo (29 January, 2015)
 */
public class Utils {

    public static String formatShowDate(Context c, long millis) {
        return DateUtils.formatDateTime(c, millis, DateUtils.FORMAT_SHOW_DATE);
    }

    public static String formatShowTime(Context c, long millis) {
        return DateUtils.formatDateTime(c, millis, DateUtils.FORMAT_SHOW_TIME);
    }

    public static String formatShowTime(Context c, int hour_of_day, int minutes) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour_of_day);
        cal.set(Calendar.MINUTE, minutes);
        return DateUtils.formatDateTime(c, cal.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME);
    }

    public static long getDateMillis(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);

        return cal.getTimeInMillis();
    }

    public static long getTimeMillis(int hours, int minutes) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hours);
        cal.set(Calendar.MINUTE, minutes);

        return cal.getTimeInMillis();
    }

    // Specifically for schedule
//    public boolean isDisable(long disableTimeMillis) {
//        return disableTimeMillis == 0 || (disableTimeMillis - System.currentTimeMillis()) > 0;
//    }
//
//    public long getDisableTimeLeftInMillis(long disableTimeMillis) {
//        return (disableTimeMillis - System.currentTimeMillis() > 0) ?
//                (disableTimeMillis - System.currentTimeMillis()) : (disableTimeMillis);
//    }

}
