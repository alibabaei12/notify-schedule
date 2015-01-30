package org.jakelcode.schedule.event;

import org.jakelcode.schedule.realm.Schedule;

import java.util.List;

/**
 * @author Pin Khe "Jake" Loo (25 January, 2015)
 */
public class ReceiveScheduleEvent {
    private final List<Schedule> mScheduleList;

    public ReceiveScheduleEvent(List<Schedule> list) {
        mScheduleList = list;
    }

    public List<Schedule> getScheduleList() {
        return mScheduleList;
    }
}
