package org.jakelcode.schedule.job;

import android.content.Context;
import android.content.IntentFilter;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import org.jakelcode.schedule.ScheduleData;
import org.jakelcode.schedule.realm.RealmInt;
import org.jakelcode.schedule.realm.Schedule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A job that loads alarms from database
 *
 * @author Pin Khe "Jake" Loo (23 January, 2015)
 */
public class LoadingAlarmJob extends Job {
    @Inject Realm mRealm;

    protected LoadingAlarmJob() {
        super(new Params(1));
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        // Loading Schedule from database
        mRealm.beginTransaction();
        RealmResults<Schedule> results = mRealm.where(Schedule.class).findAll();
        mRealm.cancelTransaction();

        // Convert Schedule into POJO (ScheduleData) and pass it through eventBus
        List<ScheduleData> dataList = new ArrayList<ScheduleData>();
        Schedule schedule;
        ScheduleData scheduleData;
        while (results.iterator().hasNext()) {
            schedule = results.iterator().next();
            scheduleData = new ScheduleData(schedule.getTitle(), schedule.getLocation(), schedule.getDescription(),
                    schedule.getStartTerm(), schedule.getEndTerm(), schedule.getStartTimestamp(), schedule.getEndTimestamp(),
                    convertToIntList(schedule.getDays()));

            dataList.add(scheduleData);
        }

    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }

    private List<Integer> convertToIntList(List<RealmInt> list) {
        List<Integer> newList = new ArrayList<>(list.size());

        for (RealmInt ri : list) {
            newList.add(ri.getValue());
        }
        return newList;
    }
}
