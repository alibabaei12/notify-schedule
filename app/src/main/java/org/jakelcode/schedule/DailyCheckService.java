package org.jakelcode.schedule;

import android.app.IntentService;
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
    @Inject Realm mRealm;

    public DailyCheckService() {
        super("daily-check-service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Dependency Injection
        ScheduleApplication.inject(this);

        final NotifyReceiver notifyReceiver = new NotifyReceiver();
        final List<Schedule> dataList = new ArrayList<Schedule>();
        final Calendar calendar = Calendar.getInstance();
        final int curDay = calendar.get(Calendar.DAY_OF_WEEK);

        // Looking through database
        mRealm.beginTransaction();
        RealmResults<Schedule> realmResult = mRealm.where(Schedule.class)
                .greaterThan("startTimestamp", calendar.getTimeInMillis()) //Only look through the active ones
                .lessThan("endTimestamp", calendar.getTimeInMillis())
                .findAll();

        while (realmResult.iterator().hasNext()) {
            Schedule s = realmResult.iterator().next();

            // If it is active today
            if (s.getDays().where().equalTo("value", curDay).findFirst() != null) {
                dataList.add(s);
            }
        }
        mRealm.cancelTransaction();

        for (Schedule data : dataList) {
            notifyReceiver.addAlarm(this, data.getUniqueId(), data.getStartTimestamp(), data.getEndTimestamp());
        }

        DailyCheckReceiver.completeWakefulIntent(intent);
    }
}
