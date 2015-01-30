package org.jakelcode.schedule;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
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
            Log.d(TAG, "DailyCheckService hits the ground!");
            final NotifyReceiver notifyReceiver = new NotifyReceiver();
            final Calendar calendar = Calendar.getInstance();
            final int curDay = calendar.get(Calendar.DAY_OF_WEEK);

            // Looking through database
            RealmResults<Schedule> realmResults = mRealm.where(Schedule.class)
                    .beginGroup()
                    .greaterThan("startTerm", calendar.getTimeInMillis()) //Only look through the active ones (in terms)
                    .lessThan("endTerm", calendar.getTimeInMillis())
                    .endGroup()
                    .or()
                    .equalTo("startTerm", -1)
                    .findAll();

            for (Schedule s : realmResults) {
                if (s.getDays().contains(Integer.toString(curDay)) || s.getDays().equals("-1")) { // If it is active today
                    notifyReceiver.addAlarm(this, s.getUniqueId(), s.getStartTimestamp(), s.getEndTimestamp());
                    Log.d(TAG, "Adding alarms : Id -> " + s.getUniqueId());
                }
            }

            DailyCheckReceiver.completeWakefulIntent(intent);
        } finally {
            if (mRealm != null) {
                mRealm.close();
            }
        }
        Log.d(TAG, "DailyCheckService just end the ruling.");
    }
}
