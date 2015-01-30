package org.jakelcode.schedule.job;

import android.content.Context;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import org.jakelcode.schedule.ScheduleData;
import org.jakelcode.schedule.event.ReceiveScheduleEvent;
import org.jakelcode.schedule.realm.Schedule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A job that loads alarms from database
 *
 * @author Pin Khe "Jake" Loo (23 January, 2015)
 */
public class LoadingScheduleJob extends Job {
    private final static String TAG = LoadingScheduleJob.class.getName();

    private Context mContext;
    @Inject EventBus mEventBus;

    public LoadingScheduleJob(final Context context) {
        super(new Params(1));
        mContext = context;
    }

    @Override
    public void onAdded() {
    }

    @Override
    public void onRun() throws Throwable {
        Realm mRealm = null;
        try {
            mRealm = Realm.getInstance(mContext);

            // Loading Schedule from database
            RealmResults<Schedule> results = mRealm.where(Schedule.class).findAll();

            // Convert Schedule into POJO (ScheduleData) and pass it through eventBus
            List<ScheduleData> dataList = new ArrayList<ScheduleData>();
            ScheduleData scheduleData;

            for (Schedule schedule : results) {
                scheduleData = new ScheduleData(schedule.getUniqueId(), schedule.getTitle(), schedule.getLocation(), schedule.getDescription(),
                        schedule.getStartTerm(), schedule.getEndTerm(), schedule.getStartTimestamp(), schedule.getEndTimestamp(),
                        convertToIntList(schedule.getDays()));

                dataList.add(scheduleData);
            }

            if (dataList.size() > 0) {
                mEventBus.post(new ReceiveScheduleEvent(dataList));
            }
        } finally {
            if (mRealm != null) {
                mRealm.close();
            }
        }
    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }

    private List<Integer> convertToIntList(String s) {
        List<Integer> newList = new ArrayList<>();

        s = s.substring(1, s.length() - 1); // discard '[' and ']'
        String[] parts = s.split(",");

        for (String single : parts) {
            newList.add(Integer.parseInt(single.trim()));
        }

        return newList;
    }
}
