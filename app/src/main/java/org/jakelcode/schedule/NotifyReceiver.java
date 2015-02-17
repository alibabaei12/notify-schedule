package org.jakelcode.schedule;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * @author Pin Khe "Jake" Loo (12 January, 2015)
 */
public class NotifyReceiver extends WakefulBroadcastReceiver {
    private static final String TAG = NotifyReceiver.class.getName();
    private static final String NOTIFY_ACTION = "org.jakelcode.schedule.NOTIFY_ACTION";

    private AlarmManager mAlarmManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, NotifyService.class);
        serviceIntent.setAction(intent.getAction());
        serviceIntent.setData(intent.getData());

        startWakefulService(context, serviceIntent);
    }

    public void addAlarm(Context context, long uniqueId, long startTimestamp, long endTimestamp) {
        if (mAlarmManager == null) {
            mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }
        buildNotification(context, "Add alarm", "jsched://" + uniqueId);

        Intent mIntent = new Intent(context, NotifyReceiver.class);
        mIntent.setAction(NOTIFY_ACTION);
        mIntent.setData(Uri.parse("jsched://" + uniqueId + "/silent"));
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, mIntent, 0);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, startTimestamp, pIntent);

        mIntent.setAction(NOTIFY_ACTION);
        mIntent.setData(Uri.parse("jsched://" + uniqueId + "/normal"));
        pIntent = PendingIntent.getBroadcast(context, 0, mIntent, 0);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, endTimestamp, pIntent);
    }

    public void removeAlarm(Context context, long uniqueId) {
        if (mAlarmManager == null) {
            mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }
        buildNotification(context, "Delete alarm", "jsched://" + uniqueId);

        Intent mIntent = new Intent(context, NotifyReceiver.class);

        // Pending intent that reset phone from normal to silent mode
        mIntent.setAction(NOTIFY_ACTION);
        mIntent.setData(Uri.parse("jsched://" + uniqueId + "/silent"));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, mIntent, 0);
        mAlarmManager.cancel(pendingIntent);

        // Pending intent that reset phone from silent to normal mode
        mIntent.setAction(NOTIFY_ACTION);
        mIntent.setData(Uri.parse("jsched://" + uniqueId + "/normal"));
        pendingIntent = PendingIntent.getBroadcast(context, 0, mIntent, 0);
        mAlarmManager.cancel(pendingIntent);
    }

    private void buildNotification(Context context, String title, String content) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_alarm_grey600_24dp)
                .setContentTitle("[NotifyRecv] " + title)
                .setContentText(content) //"Start Time : " + Utils.formatShowTime(context, startTimeMillis))
                .setAutoCancel(true)
                .build();
        notificationManager.notify(12243, notification);
    }
}
