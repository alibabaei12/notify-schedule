package org.jakelcode.schedule.realm;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * @author Pin Khe "Jake" Loo (23 January, 2015)
 */
public class Schedule extends RealmObject {
    private long uniqueId;

    private String title;
    private String location;

    private String description;

    private long startTerm;
    private long sndTerm;

    private long startTimestamp;
    private long endTimestamp;

    // Require to use Realm Integer because RealmList doesn't accept Integer
    private RealmList<RealmInt> days;

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

    public long getSndTerm() {
        return sndTerm;
    }

    public void setSndTerm(long sndTerm) {
        this.sndTerm = sndTerm;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public RealmList<RealmInt> getDays() {
        return days;
    }

    public void setDays(RealmList<RealmInt> days) {
        this.days = days;
    }
}
