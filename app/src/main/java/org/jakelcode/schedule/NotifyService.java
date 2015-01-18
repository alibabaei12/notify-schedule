package org.jakelcode.schedule;

import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.IBinder;

import javax.inject.Inject;

/**
 * @author Pin Khe "Jake" Loo (11 January, 2015)
 */
public class NotifyService extends IntentService {
    public NotifyService() {
        super("notify-service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
//    private final String SERVICE_PATH = "org.jakelcode.service.NOTIFY";
//
//    private final BroadcastReceiver receiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals(SERVICE_PATH)) {
//                AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//
//                if (am.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
//                    // Check setting preferences... Vibrate / Silent
//                    am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
//                }
//            }
//        }
//    };
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(SERVICE_PATH);
//
//        registerReceiver(receiver, filter);
//        return Service.START_STICKY;
//    }
//
//    @Override
//    public void onDestroy() {
//        unregisterReceiver(receiver);
//    }

}
