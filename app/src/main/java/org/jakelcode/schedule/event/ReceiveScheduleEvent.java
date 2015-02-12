package org.jakelcode.schedule.event;

import org.jakelcode.schedule.ScheduleCache;

import java.util.List;

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
