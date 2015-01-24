package org.jakelcode.schedule;

import android.app.Application;

import dagger.ObjectGraph;

/**
 * @author Pin Khe "Jake" Loo (11 January, 2015)
 */
public class ScheduleApplication extends Application {
    private static ObjectGraph objectGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        objectGraph = ObjectGraph.create(new ScheduleModule(this));
    }

    public static void inject(Object o) {
        objectGraph.inject(o);
    }
}
