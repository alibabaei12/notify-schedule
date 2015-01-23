package org.jakelcode.schedule;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.Calendar;

/**
 * @author Pin Khe "Jake" Loo (22 January, 2015)
 */
public class DailyCheckReceiver extends WakefulBroadcastReceiver {
    private static final String TAG = DailyCheckReceiver.class.getName();
    public static final String ACTION_DAILY_CHECK = "org.jakelcode.schedule.action.DAILY_CHECK";

    // Boot complete -> run daily alarm service -> check and set alarm for the day
    // daily alarm -> check and set alarm for the day
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) &&
                !intent.getAction().equals(ACTION_DAILY_CHECK)) {
            Log.i(TAG, "Invalid Actions");
            return;
        }

        Intent serviceIntent = new Intent(context, DailyCheckService.class);

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            setDailyAlarm(context);
        }

        startWakefulService(context, serviceIntent);
    }

    public void setDailyAlarm(Context c) {
        AlarmManager mAlarmManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.AM_PM, Calendar.AM);

        Intent intent = new Intent(c, DailyCheckReceiver.class);
        intent.setAction(ACTION_DAILY_CHECK);

        PendingIntent pendingIntent = PendingIntent.getService(c, 0, intent, 0);

        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}
