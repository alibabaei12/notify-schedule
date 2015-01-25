package org.jakelcode.schedule.job;

import android.content.Context;

import com.path.android.jobqueue.BaseJob;
import com.path.android.jobqueue.config.Configuration;
import com.path.android.jobqueue.di.DependencyInjector;
import com.path.android.jobqueue.network.NetworkUtil;

import org.jakelcode.schedule.ScheduleApplication;

import javax.inject.Singleton;

/**
 * @author Pin Khe "Jake" Loo (23 January, 2015)
 */
@Singleton
public class JobManager extends com.path.android.jobqueue.JobManager{
    public JobManager(final Context context) {
        super(context, new Configuration.Builder(context)
                .injector(new DependencyInjector() {
                    @Override
                    public void inject(BaseJob job) {
                        ScheduleApplication.inject(job);
                    }
                })
                .build());
    }
}
