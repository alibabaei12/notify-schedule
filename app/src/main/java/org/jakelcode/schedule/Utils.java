package org.jakelcode.schedule;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.Calendar;

/**
 * @author Pin Khe "Jake" Loo (29 January, 2015)
 */
public class Utils {
    public static final String PARCEL_SCHEDULE = "parcel_schedule";
    // Prefs...
    public static final String PREF_DAILY_CHECK = "pref-daily-check"; // True = Daily Check Alarm set.
    public static final String PREF_INTERRUPT = "pref-interrupt";  // True = Schedule works in Silent.
    public static final String PREF_NOTIFICATION_MINUTE = "pref-notification-minutes"; // 10 = 10 minutes notification prior schedule.
    // Prefs colors legend
    public static final String PREF_COLORS = "pref-main-colors";

    public static String formatShowDate(Context c, long millis) {
        return DateUtils.formatDateTime(c, millis, DateUtils.FORMAT_SHOW_DATE);
    }

    public static String formatShowTime(Context c, long millis) {
        return DateUtils.formatDateTime(c, millis, DateUtils.FORMAT_SHOW_TIME);
    }

    public static String formatShowTime(Context c, int hour_of_day, int minutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, hour_of_day);
        cal.set(Calendar.MINUTE, minutes);
        return DateUtils.formatDateTime(c, cal.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME);
    }

    public static long getDateMillis(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);

        // Make the millis cleaner
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        return cal.getTimeInMillis();
    }

    public static long getTimeMillis(int hours, int minutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, hours);
        cal.set(Calendar.MINUTE, minutes);
        cal.set(Calendar.SECOND, 0); // Make the millis cleaner

        return cal.getTimeInMillis();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getRootView().getWindowToken(), 0);
    }
}
