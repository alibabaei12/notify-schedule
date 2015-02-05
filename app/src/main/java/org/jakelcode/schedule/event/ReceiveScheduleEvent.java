package org.jakelcode.schedule.event;

import org.jakelcode.schedule.realm.Schedule;
import org.jakelcode.schedule.realm.ScheduleCache;

import java.util.List;

import io.realm.RealmResults;

/**
 * @author Pin Khe "Jake" Loo (25 January, 2015)
 */
public class ReceiveScheduleEvent {
    private final List<ScheduleCache> mScheduleList;

    public ReceiveScheduleEvent(List<ScheduleCache> list) {
        mScheduleList = list;
    }

    public List<ScheduleCache> getScheduleList() {
        return mScheduleList;
    }
}
