package org.jakelcode.schedule.realm;

import io.realm.RealmObject;

/**
 * @author Pin Khe "Jake" Loo (23 January, 2015)
 */
public class Schedule extends RealmObject {
    private long uniqueId;

    private String title;
    private String location;

    private String description;

    private long startTerm; // time millis
    private long endTerm; // time millis

    private int startHour; // 24 hours
    private int startMinute;

    private int endHour; // 24 hours
    private int endMinute;

    /**
     * -1 = Event is active
     * 0 = Event is permanently inactive
     * Event is disabled until disableTimestamp is surpass
     */
    private long disableTimestamp;

    // Require to use Realm Integer because RealmList doesn't accept Integer
    private String days;

    public long getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(long uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getStartTerm() {
        return startTerm;
    }

    public void setStartTerm(long startTerm) {
        this.startTerm = startTerm;
    }

    public long getEndTerm() {
        return endTerm;
    }

    public void setEndTerm(long endTerm) {
        this.endTerm = endTerm;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public long getDisableTimestamp() {
        return disableTimestamp;
    }

    public void setDisableTimestamp(long disableTimestamp) {
        this.disableTimestamp = disableTimestamp;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }
}
