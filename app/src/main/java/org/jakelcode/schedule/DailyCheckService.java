package org.jakelcode.schedule;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.jakelcode.schedule.realm.Schedule;

import java.util.Calendar;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * @author Pin Khe "Jake" Loo (22 January, 2015)
 */
public class DailyCheckService extends IntentService {
    private static final String TAG = DailyCheckService.class.getName();
    @Inject Context mAppContext;

    public DailyCheckService() {
        super("daily-check-service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Dependency Injection
        ScheduleApplication.inject(this);

        final Realm mRealm = Realm.getInstance(mAppContext);
        try {
            final NotifyReceiver notifyReceiver = new NotifyReceiver();

            final Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());

            final int curDay = calendar.get(Calendar.DAY_OF_WEEK);

            // Looking through database
            RealmResults<Schedule> realmResults = mRealm.where(Schedule.class)
                    .equalTo("disableMillis", -1) // Is not disable
                    .beginGroup() // Days check. Make sure it should be activated today check
                        .equalTo("days", "-1")
                        .or()
                        .contains("days", Integer.toString(curDay))
                    .endGroup()
                    .beginGroup() // Term check. It is in the working term
                        .equalTo("startTerm", -1)
                        .or()
                        .beginGroup()
                            .greaterThan("endTerm", calendar.getTimeInMillis()) //Only look through the active ones (in terms)
                            .lessThan("startTerm", calendar.getTimeInMillis())
                        .endGroup()
                    .endGroup()
                    .findAll();

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.ic_alarm_grey600_24dp)
                    .setContentTitle("[DailyCheckService] Adding new alarm")
                    .setContentText("Size : " + realmResults.size())
                    .setAutoCancel(true);
            notificationManager.notify(23242, nBuilder.build());

            long startTimeMillis;
            long endTimeMillis;

            for (Schedule s : realmResults) {
                // Time millis returns the hour for the day.
                calendar.set(Calendar.HOUR_OF_DAY, s.getStartHour());
                calendar.set(Calendar.MINUTE, s.getStartMinute());
                calendar.set(Calendar.SECOND, 0);
                startTimeMillis = calendar.getTimeInMillis();

                // Skip if it's already happened.
                if (System.currentTimeMillis() > startTimeMillis) {
                    continue;
                }

                calendar.set(Calendar.HOUR_OF_DAY, s.getEndHour());
                calendar.set(Calendar.MINUTE, s.getEndMinute());
                calendar.set(Calendar.SECOND, 0);
                endTimeMillis = calendar.getTimeInMillis();

                notifyReceiver.addAlarm(this, s.getUniqueId(), startTimeMillis, endTimeMillis);
            }

            DailyCheckReceiver.completeWakefulIntent(intent);
        } finally {
            if (mRealm != null) {
                mRealm.close();
            }
        }
    }
}
