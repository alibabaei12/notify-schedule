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
    private static final String ACTION_NOTIFY_ALARM = "org.jakelcode.schedule.action.ACTION_NOTIFY_ALARM";

    // Data.
    private static final String DATA_NOTIFY_PREFIX = "jsched://";
    public static final String DATA_ACTION_SILENT = "silent";
    public static final String DATA_ACTION_NORMAL = "normal";

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

        // Set the phone to silent mode
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, startTimestamp, getSilentIntent(context, uniqueId));

        // Set the phone to normal mode
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, endTimestamp, getNormalIntent(context, uniqueId));
    }

    public void removeAlarm(Context context, long uniqueId) {
        if (mAlarmManager == null) {
            mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }
        buildNotification(context, "Delete alarm", "jsched://" + uniqueId);

        Intent mIntent = new Intent(context, NotifyReceiver.class);

        // Pending intent that reset phone from normal to silent mode
        mAlarmManager.cancel(getSilentIntent(context, uniqueId));

        // Pending intent that reset phone from silent to normal mode
        mAlarmManager.cancel(getNormalIntent(context, uniqueId));
    }

    // In the future will make a Builder class
    // jsched:// 'id'  / 'type' / 'ops'
    private Uri getDataUri(long uniqueId, String action) {
        return Uri.parse(DATA_NOTIFY_PREFIX + Long.toString(uniqueId) + "/" + "action" + "/" + action);
    }

    private PendingIntent getSilentIntent(Context c, long uniqueId) {
        Intent intent = new Intent(c, NotifyReceiver.class);
        intent.setAction(ACTION_NOTIFY_ALARM);
        intent.setData(getDataUri(uniqueId, DATA_ACTION_SILENT));

        return PendingIntent.getBroadcast(c, 0, intent, 0);
    }

    private PendingIntent getNormalIntent(Context c, long uniqueId) {
        Intent intent = new Intent(c, NotifyReceiver.class);
        intent.setAction(ACTION_NOTIFY_ALARM);
        intent.setData(getDataUri(uniqueId, DATA_ACTION_NORMAL));

        return PendingIntent.getBroadcast(c, 0, intent, 0);
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
