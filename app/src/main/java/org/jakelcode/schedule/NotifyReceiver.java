package org.jakelcode.schedule;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.widget.Toast;

import java.util.Calendar;

import javax.inject.Inject;

/**
 * @author Pin Khe "Jake" Loo (12 January, 2015)
 */
public class NotifyReceiver extends WakefulBroadcastReceiver {
    public static final String ALARM_ID = "ALARM_ID";
    public static final String ALARM_MODE = "ALARM_MODE";

    public static final int RINGER_NORMAL = 0;
    public static final int RINGER_SILENT = 1;

    private AlarmManager mAlarmManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, NotifyService.class);
        serviceIntent.putExtra(ALARM_ID, intent.getLongExtra(ALARM_ID, -1));
        serviceIntent.putExtra(ALARM_MODE, intent.getIntExtra(ALARM_MODE, RINGER_NORMAL)); // weird

        startWakefulService(context, serviceIntent);
    }

    public void addAlarm(Context context, long uniqueId, long startTimestamp, long endTimestamp) {
        if (mAlarmManager == null) {
            mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }
        Intent mIntent = new Intent(context, NotifyReceiver.class);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis()); //startTimestamp
        calendar.add(Calendar.SECOND, 5);

        // Alarm that set ringer from normal to silent
        mIntent.putExtra(ALARM_ID, uniqueId);
        mIntent.putExtra(ALARM_MODE, RINGER_SILENT);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, mIntent, 0);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);

        // Alarm that set ringer from silent to normal
        mIntent = new Intent(context, NotifyReceiver.class);
        mIntent.putExtra(ALARM_ID, uniqueId);
        mIntent.putExtra(ALARM_MODE, RINGER_NORMAL);
        pIntent = PendingIntent.getBroadcast(context, 0, mIntent, 0);
        calendar.setTimeInMillis(System.currentTimeMillis()); //endTimestamp
        calendar.add(Calendar.SECOND, 10);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
    }

    public void removeAlarm(Context context, long uniqueId) {
        if (mAlarmManager == null) {
            mAlarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        }

        Intent mIntent = new Intent(context, NotifyReceiver.class);
        mIntent.putExtra(ALARM_ID, uniqueId);

        // Pending intent that reset phone from normal to silent mode
        mIntent.putExtra(ALARM_MODE, RINGER_SILENT);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, mIntent, 0);
        mAlarmManager.cancel(pendingIntent);

        // Pending intent that reset phone from silent to normal mode
        mIntent.putExtra(ALARM_MODE, RINGER_NORMAL);
        pendingIntent = PendingIntent.getBroadcast(context, 0, mIntent, 0);
        mAlarmManager.cancel(pendingIntent);
    }
}
