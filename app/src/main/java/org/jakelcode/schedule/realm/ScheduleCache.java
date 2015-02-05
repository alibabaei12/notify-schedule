package org.jakelcode.schedule.realm;

/**
 * Description...
 *
 * @author Pin Khe "Jake" Loo (04 February, 2015)
 */
public class ScheduleCache {
    private long uniqueId;

    private String title;
    private String location;

    private String description;

    private long startTerm;
    private long endTerm;

    private int startHour; // 24 hours
    private int startMinute;

    private int endHour; // 24 hours
    private int endMinute;

    private long disableTimestamp;

    private String days;

    public ScheduleCache(Schedule s) {
        this.uniqueId = s.getUniqueId();
        this.title = s.getTitle();
        this.location = s.getLocation();
        this.description = s.getDescription();
        this.startTerm = s.getStartTerm();
        this.endTerm = s.getEndTerm();
        this.startHour = s.getStartHour();
        this.startMinute = s.getStartMinute();
        this.endHour = s.getEndHour();
        this.endMinute = s.getEndMinute();
        this.disableTimestamp = s.getDisableTimestamp();
        this.days = s.getDays();
    }

    public long getUniqueId() {
        return uniqueId;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public long getStartTerm() {
        return startTerm;
    }

    public long getEndTerm() {
        return endTerm;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public long getDisableTimestamp() {
        return disableTimestamp;
    }

    public String getDays() {
        return days;
    }
}
