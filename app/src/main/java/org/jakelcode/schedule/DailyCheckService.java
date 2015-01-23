package org.jakelcode.schedule;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author Pin Khe "Jake" Loo (22 January, 2015)
 */
public class DailyCheckService extends IntentService {

    public DailyCheckService() {
        super("daily-check-service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        NotifyReceiver notifyReceiver = new NotifyReceiver();
        // Look through database for the day
            //Probably realm...?

        // dataList is temporary, until database is working
        List<ScheduleData> dataList = new ArrayList<ScheduleData>();

        final Calendar calendar = Calendar.getInstance();
        final long curTime = System.currentTimeMillis();
        final int curDay = calendar.get(Calendar.DAY_OF_WEEK);

        // Get all the schedule data, make sure it is within the term period.
        // Add alarms that are supposed to work on the day.
        for (ScheduleData data : dataList) {
            if (data.getStartTerm() > curTime && curTime < data.getEndTerm()) {
                if (data.getActiveDays().contains(curDay)) {
                    notifyReceiver.addAlarm(this, data.getUniqueId(), data.getStartTimestamp(), data.getEndTimestamp());
                }
            }
        }

        DailyCheckReceiver.completeWakefulIntent(intent);
    }
}
