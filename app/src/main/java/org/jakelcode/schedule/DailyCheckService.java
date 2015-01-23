package org.jakelcode.schedule;

import android.app.IntentService;
import android.content.Intent;

/**
 * @author Pin Khe "Jake" Loo (22 January, 2015)
 */
public class DailyCheckService extends IntentService {

    public DailyCheckService() {
        super("daily-check-service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Look through database for the day

        // Setup alarm for the day

        DailyCheckReceiver.completeWakefulIntent(intent);
    }
}
