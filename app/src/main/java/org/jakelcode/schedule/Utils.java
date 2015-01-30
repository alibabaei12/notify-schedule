package org.jakelcode.schedule;

import android.content.Context;
import android.text.format.DateUtils;

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
