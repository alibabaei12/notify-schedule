package org.jakelcode.schedule;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * @author Pin Khe "Jake" Loo (11 January, 2015)
 */
public class NotifyService extends IntentService {
    private static final String TAG = NotifyService.class.getName();

    public NotifyService() {
        super("notify-service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final String dataString = intent.getDataString();

        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        if (am.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) { // Not in  normal..
            buildNotification("Alarm failed to add. " + dataString);
            NotifyReceiver.completeWakefulIntent(intent);
            return;
        }

        if (dataString.contains("normal")) {
            Log.d(TAG, "Received Ringer Normal from " + dataString);
            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        } else if (dataString.contains("silent")) {
            Log.d(TAG, "Received Ringer Silent from " + dataString);
            am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        }

        buildNotification("Alarm burst. " + dataString);
        NotifyReceiver.completeWakefulIntent(intent);
    }

    private void buildNotification(String dataString) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_alarm_grey600_24dp)
                .setContentTitle("NotifyService")
                .setContentText("Data : " + dataString)
                .setAutoCancel(true);
        notificationManager.notify(23442, nBuilder.build());
    }
}
