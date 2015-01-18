package org.jakelcode.schedule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Pin Khe "Jake" Loo (11 January, 2015)
 */
@Module(
      injects = {
              NotifyService.class,
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
}
