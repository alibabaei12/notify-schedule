package org.jakelcode.schedule;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import org.jakelcode.schedule.realm.Schedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * @author Pin Khe "Jake" Loo (22 January, 2015)
 */
public class DailyCheckService extends IntentService {
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
            final int curDay = calendar.get(Calendar.DAY_OF_WEEK);

            // Looking through database
            RealmResults<Schedule> realmResults = mRealm.where(Schedule.class)
                    .greaterThan("startTimestamp", calendar.getTimeInMillis()) //Only look through the active ones
                    .lessThan("endTimestamp", calendar.getTimeInMillis())
                    .findAll();

            for (Schedule s : realmResults) {
                // If it is active today
                if (s.getDays().where().equalTo("value", curDay).findFirst() != null) {
                    //dataList.add(s);
                    notifyReceiver.addAlarm(this, s.getUniqueId(), s.getStartTimestamp(), s.getEndTimestamp());
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
