package org.jakelcode.schedule.job;

import android.content.Context;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import org.jakelcode.schedule.event.ReceiveScheduleEvent;
import org.jakelcode.schedule.realm.Schedule;
import org.jakelcode.schedule.realm.ScheduleCache;

import java.util.ArrayList;
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

            // Sort it descending, so the latest ones will be displayed on top
            // Also it is easier to obtain the last uniqueId exists in the database
            results.sort("uniqueId", RealmResults.SORT_ORDER_DESCENDING);

            List<ScheduleCache> caches = new ArrayList<>();

            for (Schedule s : results) {
                caches.add(new ScheduleCache(s));
            }

            if (results.size() > 0) {
                // Passing the LAST UNIQUE ID found in data and the list of schedules
                mEventBus.post(new ReceiveScheduleEvent(caches));
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
}
