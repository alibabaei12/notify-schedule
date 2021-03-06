package org.jakelcode.schedule;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.provider.Settings;
import android.text.format.DateUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.Calendar;

import io.fabric.sdk.android.services.common.SystemCurrentTimeProvider;

/**
 * @author Pin Khe "Jake" Loo (29 January, 2015)
 */
public class Utils {
    public static final String PARCEL_SCHEDULE = "parcel_schedule";
    // Prefs...
    public static final String PREF_INTERRUPT = "pref-interrupt";  // True = Schedule works in Silent.
    public static final String PREF_NOTIFICATION_MINUTE = "pref-notification-minutes"; // 10 = 10 minutes notification prior schedule.
    // Prefs colors legend
    public static final String PREF_COLORS_NORMAL = "pref-colors-normal";
    public static final String PREF_COLORS_EXPIRE = "pref-colors-expire";
    public static final String PREF_COLORS_FUTURE = "pref-colors-future";
    public static final String PREF_COLORS_DISABLE = "pref-colors-disable";

    public static final Calendar cal = Calendar.getInstance();

    public static String formatShowDate(Context c, long millis) {
        return DateUtils.formatDateTime(c, millis, DateUtils.FORMAT_SHOW_DATE);
    }

    public static String formatShowTime(Context c, long millis) {
        return DateUtils.formatDateTime(c, millis, DateUtils.FORMAT_SHOW_TIME);
    }

    public static String formatShowTime(Context c, int hour_of_day, int minutes) {
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, hour_of_day);
        cal.set(Calendar.MINUTE, minutes);
        return DateUtils.formatDateTime(c, cal.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME);
    }

    public static long getDateMillis(int year, int month, int day) {
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

    public static boolean isDayOfWeek(String dayList) {
        cal.setTimeInMillis(System.currentTimeMillis());
        int currentDay = cal.get(Calendar.DAY_OF_WEEK);

        return dayList.equals("-1") || dayList.contains(Integer.toString(currentDay));
    }
}
