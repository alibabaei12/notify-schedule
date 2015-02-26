package org.jakelcode.schedule;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.Calendar;

/**
 * @author Pin Khe "Jake" Loo (22 January, 2015)
 */
public class DailyCheckReceiver extends WakefulBroadcastReceiver {
    private static final String TAG = DailyCheckReceiver.class.getName();
    public static final String ACTION_DAILY_CHECK = "org.jakelcode.schedule.action.DAILY_CHECK";
    private static final String DATA_DAILY_CHECK = "jsched://all/action/daily-check";

    // Boot complete -> run daily alarm service -> check and set alarm for the day
    // daily alarm -> check and set alarm for the day
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, DailyCheckService.class);

        String action = intent.getAction();

        // Because the automatic clock system keep syncs the clock.
        if (action.equals(Intent.ACTION_TIME_CHANGED) && isNetworkTime(context)) {
            return;
        }

        if (!action.equals(ACTION_DAILY_CHECK)) {
            setDailyAlarm(context);
        }

        startWakefulService(context, serviceIntent);
    }

    // The alarm that is set on every 00:00 to check for schedules for the day.
    public void setDailyAlarm(Context c) {
        // Prevent duplicate daily alarms.
        removeDailyAlarm(c);

        AlarmManager mAlarmManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 1);
        cal.set(Calendar.MILLISECOND, 0);

        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, getCheckingIntent(c));
    }

    public void removeDailyAlarm(Context c) {
        AlarmManager mAlarmManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);

        mAlarmManager.cancel(getCheckingIntent(c));
    }

    private static PendingIntent getCheckingIntent(Context c) {
        Intent intent = new Intent(c, DailyCheckReceiver.class);
        intent.setAction(ACTION_DAILY_CHECK);
        intent.setData(Uri.parse(DATA_DAILY_CHECK));

        return PendingIntent.getBroadcast(c, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private boolean isNetworkTime(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return (Settings.Global.getInt(c.getContentResolver(), Settings.Global.AUTO_TIME, 0) == 1);
        } else {
            return (Settings.System.getInt(c.getContentResolver(), Settings.System.AUTO_TIME, 0) == 1);
        }
    }
}
