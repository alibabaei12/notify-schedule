package org.jakelcode.schedule;

import android.content.Context;
import android.content.SharedPreferences;

import com.path.android.jobqueue.JobManager;

import org.jakelcode.schedule.job.LoadingScheduleJob;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

/**
 * @author Pin Khe "Jake" Loo (11 January, 2015)
 */
@Module(
        injects = {
                MainActivity.class,
                EditActivity.class,
                SettingActivity.class,

                LoadingScheduleJob.class,
                DailyCheckService.class,
                ScheduleTypePalette.class
        }
)
public class ScheduleModule {
    private static final String PREFERENCE_NAME = "jsched";

    private final ScheduleApplication application;

    public ScheduleModule(ScheduleApplication a) {
        application = a;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return application;
    }

    @Provides
    @Singleton
    JobManager provideJobManager() {
        return new org.jakelcode.schedule.job.JobManager(application);
    }

    @Provides
    @Singleton
    EventBus provideEventBus() {
        return EventBus.getDefault();
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences() {
        return application.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    ScheduleTypePalette provideScheduleTypePalette(SharedPreferences s) {
        return new ScheduleTypePalette(s);
    }

}
