package org.jakelcode.schedule.job;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

/**
 * A job that loads alarms from database
 *
 * @author Pin Khe "Jake" Loo (23 January, 2015)
 */
public class LoadingAlarmJob extends Job {
    protected LoadingAlarmJob() {
        super(new Params(1));
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {

    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }
}
