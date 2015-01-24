package org.jakelcode.schedule;

import android.content.Context;
import android.os.SystemClock;
import android.text.format.DateUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Pin Khe "Jake" Loo (11 January, 2015)
 */
public class ScheduleData {
    private long mUniqueId;

    private String mTitle;
    private String mLocation;

    // Short description
    private String mDescription;

    // Day of week that event happens
    private List<Integer> mDays;

    private long mStartTerm;
    private long mEndTerm;

    private long mStartTimestamp;
    private long mEndTimestamp;

    /**
    * -1 = Event is active
    * 0 = Event is permanently inactive
    * Event is disabled until disableTimestamp is surpass
    */
    private long mDisableTimestamp = -1;

    public ScheduleData(String title, String location, String desc,
                        long startTerm, long endTerm, long startTime, long endTime,
                        List<Integer> days) {
        this.mTitle = title;
        this.mLocation = location;
        this.mDescription = desc;
        this.mStartTerm = startTerm;
        this.mEndTerm = endTerm;
        this.mStartTimestamp = startTime;
        this.mEndTimestamp = endTime;
        this.mDays = days;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getLocation() {
        return mLocation;
    }

    public String getDescription() {
         return mDescription;
    }

    public List<Integer> getDayList() {
        return mDays;
    }

    public long getStartTerm() {
        return mStartTerm;
    }

    public String getStartTermString(Context c) {
        return DateUtils.formatDateTime(c, mStartTerm, DateUtils.FORMAT_SHOW_DATE);
    }

    public long getEndTerm() {
        return mEndTerm;
    }

    public String getEndTermString(Context c) {
        return DateUtils.formatDateTime(c, mEndTerm, DateUtils.FORMAT_SHOW_DATE);
    }

    public long getStartTimestamp() {
        return mStartTimestamp;
    }

    public String getStartTimeString(Context c) {
        return DateUtils.formatDateTime(c, mStartTimestamp, DateUtils.FORMAT_SHOW_TIME);
    }

    public long getEndTimestamp() {
        return mEndTimestamp;
    }

    public String getEndTimeString(Context c) {
        return DateUtils.formatDateTime(c, mEndTimestamp, DateUtils.FORMAT_SHOW_TIME);
    }

    public boolean isEnded() {
        return mEndTerm - mStartTerm > 0;
    }

    public boolean isDisabled() {
        return mDisableTimestamp == 0 || (mDisableTimestamp - System.currentTimeMillis()) > 0;
    }

    public long getDisableTimeLeftInMillis() {
        return (mDisableTimestamp - System.currentTimeMillis() > 0) ?
                (mDisableTimestamp - System.currentTimeMillis()) : (mDisableTimestamp);
    }

    public long getUniqueId() {
        return mUniqueId;
    }
}
