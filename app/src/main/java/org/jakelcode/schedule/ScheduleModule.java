package org.jakelcode.schedule;

import android.app.AlarmManager;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Pin Khe "Jake" Loo (11 January, 2015)
 */
@Module(
      injects = {
              MainActivity.class,
              SettingActivity.class
      }
)
public class ScheduleModule {
    //private final String PREFERENCE_NAME = "schedule";

    private final ScheduleApplication application;

    public ScheduleModule(ScheduleApplication a) {
        application = a;
    }

//    @Provides @Singleton Context provideContext() {
//        return application;
//    }

//    @Provides @Singleton AlarmManager provideAlarmManager() {
//        return (AlarmManager) application.getSystemService(Context.ALARM_SERVICE);
//    }
}
