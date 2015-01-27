package org.jakelcode.schedule.event;

import org.jakelcode.schedule.ScheduleData;

import java.util.List;

/**
 * @author Pin Khe "Jake" Loo (25 January, 2015)
 */
public class ReceiveScheduleEvent {
    private final List<ScheduleData> mScheduleList;

    public ReceiveScheduleEvent(List<ScheduleData> list) {
        mScheduleList = list;
    }

    public List<ScheduleData> getScheduleList() {
        return mScheduleList;
    }
}
