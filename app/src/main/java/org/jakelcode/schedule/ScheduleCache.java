package org.jakelcode.schedule;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import org.jakelcode.schedule.realm.Schedule;

/**
 * @author Pin Khe "Jake" Loo (04 February, 2015)
 */
public class ScheduleCache implements Parcelable {
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

    private long disableMillis;

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
        this.disableMillis = s.getDisableMillis();
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

    public long getDisableMillis() {
        return disableMillis;
    }

    public String getDays() {
        return days;
    }


    @IntDef({NORMAL, EXPIRED, FUTURE, DISABLED})
    public @interface ScheduleType{}

    public static final int NORMAL = 0;
    public static final int EXPIRED = 1;
    public static final int FUTURE = 2;
    public static final int DISABLED = 3;

    @ScheduleType
    public int getType() {
        if (endTerm > 0 && System.currentTimeMillis() > endTerm) {
            return EXPIRED;
        } else if (System.currentTimeMillis() < startTerm) {
            return FUTURE;
        } else if (disableMillis > -1) {
            return DISABLED;
        }
        return NORMAL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScheduleCache)) return false;

        ScheduleCache that = (ScheduleCache) o;

        if (disableMillis != that.disableMillis) return false;
        if (endHour != that.endHour) return false;
        if (endMinute != that.endMinute) return false;
        if (endTerm != that.endTerm) return false;
        if (startHour != that.startHour) return false;
        if (startMinute != that.startMinute) return false;
        if (startTerm != that.startTerm) return false;
        if (uniqueId != that.uniqueId) return false;
        if (days != null ? !days.equals(that.days) : that.days != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null)
            return false;
        if (location != null ? !location.equals(that.location) : that.location != null)
            return false;
        if (!title.equals(that.title)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (uniqueId ^ (uniqueId >>> 32));
        result = 31 * result + title.hashCode();
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (int) (startTerm ^ (startTerm >>> 32));
        result = 31 * result + (int) (endTerm ^ (endTerm >>> 32));
        result = 31 * result + startHour;
        result = 31 * result + startMinute;
        result = 31 * result + endHour;
        result = 31 * result + endMinute;
        result = 31 * result + (int) (disableMillis ^ (disableMillis >>> 32));
        result = 31 * result + (days != null ? days.hashCode() : 0);
        return result;
    }

    //Parcelable implementation
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.uniqueId);
        dest.writeString(this.title);
        dest.writeString(this.location);
        dest.writeString(this.description);
        dest.writeLong(this.startTerm);
        dest.writeLong(this.endTerm);
        dest.writeInt(this.startHour);
        dest.writeInt(this.startMinute);
        dest.writeInt(this.endHour);
        dest.writeInt(this.endMinute);
        dest.writeLong(this.disableMillis);
        dest.writeString(this.days);
    }

    private ScheduleCache(Parcel in) {
        this.uniqueId = in.readLong();
        this.title = in.readString();
        this.location = in.readString();
        this.description = in.readString();
        this.startTerm = in.readLong();
        this.endTerm = in.readLong();
        this.startHour = in.readInt();
        this.startMinute = in.readInt();
        this.endHour = in.readInt();
        this.endMinute = in.readInt();
        this.disableMillis = in.readLong();
        this.days = in.readString();
    }

    public static final Creator<ScheduleCache> CREATOR = new Creator<ScheduleCache>() {
        public ScheduleCache createFromParcel(Parcel source) {
            return new ScheduleCache(source);
        }

        public ScheduleCache[] newArray(int size) {
            return new ScheduleCache[size];
        }
    };
}
