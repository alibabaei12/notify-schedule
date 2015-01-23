package org.jakelcode.schedule;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.util.Calendar;

/**
 * @author Pin Khe "Jake" Loo (12 January, 2015)
 */
public class NotifyReceiver extends WakefulBroadcastReceiver {
    private static final String TAG = NotifyReceiver.class.getName();

    private AlarmManager mAlarmManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, NotifyService.class);
//        serviceIntent.setAction(intent.getAction());
        serviceIntent.setData(intent.getData());

        startWakefulService(context, serviceIntent);
    }

    public void addAlarm(Context context, long uniqueId, long startTimestamp, long endTimestamp) {
        if (mAlarmManager == null) {
            mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }
        Intent mIntent = new Intent(context, NotifyReceiver.class);
        Calendar calendar = Calendar.getInstance();

        mIntent.setData(Uri.parse("jsched://" + uniqueId + "/silent" ));
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, mIntent, 0);
        calendar.setTimeInMillis(System.currentTimeMillis()); //startTimestamp
        calendar.add(Calendar.SECOND, 5);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);

        mIntent.setData(Uri.parse("jsched://" + uniqueId + "/normal"));
        pIntent = PendingIntent.getBroadcast(context, 0, mIntent, 0);
        calendar.setTimeInMillis(System.currentTimeMillis()); //endTimestamp
        calendar.add(Calendar.SECOND, 15);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
    }

    public void removeAlarm(Context context, long uniqueId) {
        if (mAlarmManager == null) {
            mAlarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        }

        Intent mIntent = new Intent(context, NotifyReceiver.class);

        // Pending intent that reset phone from normal to silent mode
        mIntent.setData(Uri.parse("jsched://" + uniqueId + "/silent"));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, mIntent, 0);
        mAlarmManager.cancel(pendingIntent);

        // Pending intent that reset phone from silent to normal mode
        mIntent.setData(Uri.parse("jsched://" + uniqueId + "/normal"));
        pendingIntent = PendingIntent.getBroadcast(context, 0, mIntent, 0);
        mAlarmManager.cancel(pendingIntent);
    }
}
