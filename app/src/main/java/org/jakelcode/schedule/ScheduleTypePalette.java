package org.jakelcode.schedule;

import android.content.SharedPreferences;

/**
 * @author Pin Khe "Jake" Loo (18 February, 2015)
 */
public class ScheduleTypePalette {
    private SharedPreferences mPrefs;

    private static final String mDefaultNormalColor = "#a3e9a4"; // green_100
    private static final String mDefaultExpireColor = "#f9bdbb"; // red_100
    private static final String mDefaultFutureColor = "#fff9c4"; // yellow_100
    private static final String mDefaultDisableColor = "#d1c4e9"; // purple_100

    public ScheduleTypePalette(SharedPreferences sp) {
        mPrefs = sp;
    }

    public String getNormalColor() {
        return mPrefs.getString(Utils.PREF_COLORS_NORMAL, mDefaultNormalColor);
    }

    public String getExpireColor() {
        return mPrefs.getString(Utils.PREF_COLORS_EXPIRE, mDefaultExpireColor);
    }

    public String getFutureColor() {
        return mPrefs.getString(Utils.PREF_COLORS_FUTURE, mDefaultFutureColor);
    }

    public String getDisableColor() {
        return mPrefs.getString(Utils.PREF_COLORS_DISABLE, mDefaultDisableColor);
    }

    public void setNormalColor(String c) {
        mPrefs.edit().putString(Utils.PREF_COLORS_NORMAL, c).apply();
    }

    public void setExpireColor(String c) {
        mPrefs.edit().putString(Utils.PREF_COLORS_EXPIRE, c).apply();
    }

    public void setFutureColor(String c) {
        mPrefs.edit().putString(Utils.PREF_COLORS_FUTURE, c).apply();
    }

    public void setDisableColor(String c) {
        mPrefs.edit().putString(Utils.PREF_COLORS_DISABLE, c).apply();
    }

    public void setColor(String normal, String expire, String future, String disable) {
        mPrefs.edit()
                .putString(Utils.PREF_COLORS_NORMAL, normal)
                .putString(Utils.PREF_COLORS_EXPIRE, expire)
                .putString(Utils.PREF_COLORS_FUTURE, future)
                .putString(Utils.PREF_COLORS_DISABLE, disable)
                .apply();
    }
}
