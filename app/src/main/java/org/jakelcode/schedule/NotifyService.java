package org.jakelcode.schedule;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
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

        if (dataString.contains("normal")) {
            Log.d(TAG, "Received Ringer Normal from " + dataString);
            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        } else if (dataString.contains("silent")) {
            Log.d(TAG, "Received Ringer Silent from " + dataString);
            am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        }

        NotifyReceiver.completeWakefulIntent(intent);
    }
}
