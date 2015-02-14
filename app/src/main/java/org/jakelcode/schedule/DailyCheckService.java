package org.jakelcode.schedule;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
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
                    .equalTo("startTerm", -1)
                    .or()
                    .beginGroup()
                        .greaterThan("endTerm", calendar.getTimeInMillis()) //Only look through the active ones (in terms)
                        .lessThan("startTerm", calendar.getTimeInMillis())
                    .endGroup()
                    .findAll();

            long startTimeMillis;
            long endTimeMillis;
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            for (Schedule s : realmResults) {
                if (s.getDays().contains(Integer.toString(curDay)) || s.getDays().equals("-1")) { // If it is active today
                    // Time millis returns the hour for the day.
                    calendar.set(Calendar.HOUR_OF_DAY, s.getStartHour());
                    calendar.set(Calendar.MINUTE, s.getStartMinute());
                    calendar.set(Calendar.SECOND, 0);
                    startTimeMillis = calendar.getTimeInMillis();

                    calendar.set(Calendar.HOUR_OF_DAY, s.getEndHour());
                    calendar.set(Calendar.MINUTE, s.getEndMinute());
                    calendar.set(Calendar.SECOND, 0);
                    endTimeMillis = calendar.getTimeInMillis();

                    Notification notification = new Notification.Builder(mAppContext)
                            .setSmallIcon(R.drawable.ic_alarm_grey600_24dp)
                            .setContentTitle("Add Schedule ID : " + s.getUniqueId())
                            .setContentText("Start Time : " + Utils.formatShowTime(mAppContext, startTimeMillis))
                            .setAutoCancel(true)
                            .build();

                    notificationManager.notify(12243, notification);

                    notifyReceiver.addAlarm(this, s.getUniqueId(), startTimeMillis, endTimeMillis);
                }
            }

            DailyCheckReceiver.completeWakefulIntent(intent);
        } finally {
            if (mRealm != null) {
                mRealm.close();
            }
        }
    }
}
