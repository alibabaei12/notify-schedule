package org.jakelcode.schedule;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import org.jakelcode.schedule.realm.Schedule;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;


public class EditActivity extends ActionBarActivity {
    private static final String TAG = EditActivity.class.getName();

    private static final String PREF_DAILY_CHECK = "daily-check";

    @Inject SharedPreferences mPreferences;
    @Inject Realm mRealm;
    @Inject Context mAppContext;
    @Inject DailyCheckReceiver mDailyCheckReceiver;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScheduleApplication.inject(this);

        setContentView(R.layout.activity_edit);
        ButterKnife.inject(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.include_toolbar_long);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_confirm) {
            if (validateData()) {
                if (!saveData(true)) {
                    Toast.makeText(mAppContext, "Failed to save data", Toast.LENGTH_LONG).show();
                } else {
                    finish();
                }
            } else {
//                drawValidation();
            }
        } else if (id == R.id.action_delete) {

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        //TODO if data has been modified send a confirmation
        return super.onSupportNavigateUp();
    }

    public boolean validateData() {
        return true;
    }

    public boolean saveData(boolean newData) {
        //Feed fake data for now :)
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.HOUR, 5);
        cal.set(Calendar.MINUTE, 45);
        final long startTime = cal.getTimeInMillis();

        cal.set(Calendar.HOUR, 8);
        cal.set(Calendar.MINUTE, 20);
        final long endTime = cal.getTimeInMillis();

        mRealm.beginTransaction();
        Schedule realmSchedule = mRealm.createObject(Schedule.class);
            realmSchedule.setUniqueId(-1);
            realmSchedule.setTitle("Title 1");
            realmSchedule.setLocation("Location 1");
            realmSchedule.setDescription("Short desc");
            realmSchedule.setStartTerm(-1);
            realmSchedule.setEndTerm(-1);
            realmSchedule.setStartTimestamp(startTime);
            realmSchedule.setEndTimestamp(endTime);
            realmSchedule.setDisableTimestamp(-1);
            realmSchedule.setDays("-1");
        mRealm.commitTransaction();

        return true;
    }

    public void setDailyCheckService(boolean enable) {
        final boolean dailyServiceActive = mPreferences.getBoolean(PREF_DAILY_CHECK, false);

        // If preferences is disable and asking to disable = return or
        // If preference is enable and asking to enable = return
        if ((!dailyServiceActive && !enable) || (dailyServiceActive && enable)) {
            return;
        }

        if (enable) {
            mDailyCheckReceiver.setDailyAlarm(mAppContext);
        } else {
            mDailyCheckReceiver.removeDailyAlarm(mAppContext);
        }

        mPreferences.edit().putBoolean(PREF_DAILY_CHECK, enable).apply();
    }

    @OnClick({R.id.edit_time_start_text, R.id.edit_time_end_text})
    public void promptClock(final View v) {
        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.setListener(new TimePickerFragment.Listener() {
            @Override
            public void onTimeReceive(int hour_of_day, int minutes) {
                ((MaterialEditText) v).setText(Utils.formatShowTime(mAppContext, hour_of_day, minutes));

                if (v.getId() == R.id.edit_time_start_text) {
                    findViewById(R.id.edit_time_end_text).performClick();
                }
            }
        });
        newFragment.show(getSupportFragmentManager(), "time_picker");
    }

    @OnClick({R.id.edit_term_start_text, R.id.edit_term_end_text})
    public void promptCalendar(final View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setListener(new DatePickerFragment.Listener() {
            @Override
            public void onDateReceive(int year, int month, int day) {
                ((MaterialEditText) v).setText(Utils.formatShowDate(mAppContext, year, month, day));

                if (v.getId() == R.id.edit_term_start_text) {
                    findViewById(R.id.edit_term_end_text).performClick();
                }
            }
        });
        newFragment.show(getSupportFragmentManager(), "date_picker");
    }

    public static final class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        private Listener listener;

        @Override @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }


        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            listener.onDateReceive(year, month, day);
        }

        public void setListener(Listener l) {
            listener = l;
        }

        interface Listener {
            void onDateReceive(int year, int month, int day);
        }
    }


    public static final class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {
        private Listener listener;

        @Override @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int hour_of_day = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour_of_day, minute, false);
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            listener.onTimeReceive(hourOfDay, minute);
        }

        public void setListener(Listener l) {
            listener = l;
        }

        interface Listener {
            void onTimeReceive(int hourOfDay, int minute);
        }
    }
}
