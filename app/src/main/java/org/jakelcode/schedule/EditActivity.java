package org.jakelcode.schedule;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.TimeUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import org.jakelcode.schedule.realm.Schedule;
import org.jakelcode.schedule.ui.CheckableButton;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;
import io.realm.Realm;


public class EditActivity extends ActionBarActivity {
    private static final String TAG = EditActivity.class.getName();

    private Realm mRealm;
    @Inject Context mAppContext;

    @InjectView(R.id.edit_title_text) MaterialEditText mTitleText;
    @InjectView(R.id.edit_description_text) MaterialEditText mDescriptionText;
    @InjectView(R.id.edit_time_start_text) MaterialEditText mStartTimeText;
    @InjectView(R.id.edit_time_end_text) MaterialEditText mEndTimeText;
    @InjectView(R.id.edit_term_start_text) MaterialEditText mStartTermText;
    @InjectView(R.id.edit_term_end_text) MaterialEditText mEndTermText;
    @InjectView(R.id.edit_location_text) MaterialEditText mLocationText;

    @InjectViews({R.id.edit_days_sun, R.id.edit_days_m, R.id.edit_days_t, R.id.edit_days_w,
            R.id.edit_days_th, R.id.edit_days_f, R.id.edit_days_sat})
    List<CheckableButton> mRepeatDayList;

    private long mUniqueId = -1;

    private long mStartTerm = -1;
    private long mEndTerm = -1;
    private long mDisableMillis = -1; // Partially implemented

    private int mStartHour = -1;
    private int mStartMinute = -1;
    private int mEndHour = -1;
    private int mEndMinute = -1;

    private String mRepeatDays = "-1";
    @Override
    protected void onStart() {
        super.onStart();

        final ScheduleCache model = getIntent().getParcelableExtra(Utils.PARCEL_SCHEDULE);

        if (model != null) { // Edit
            // Initialize variables
            mUniqueId = model.getUniqueId();
            mStartHour = model.getStartHour();
            mStartMinute = model.getStartMinute();
            mEndHour = model.getEndHour();
            mEndMinute = model.getEndMinute();
            mStartTerm = model.getStartTerm();
            mEndTerm = model.getEndTerm();
            mDisableMillis = model.getDisableMillis();
            mRepeatDays = model.getDays();

            // Rendering UIs
            mTitleText.setText(model.getTitle());
            mDescriptionText.setText(model.getDescription());
            mLocationText.setText(model.getLocation());
            mStartTimeText.setText(Utils.formatShowTime(mAppContext, mStartHour, mStartMinute));
            mEndTimeText.setText(Utils.formatShowTime(mAppContext, mEndHour, mEndMinute));
            if (mStartTerm != -1) {
                mStartTermText.setText(Utils.formatShowDate(mAppContext, mStartTerm));
                mEndTermText.setText(Utils.formatShowDate(mAppContext, mEndTerm));
            }
            // Render days.
            if (!mRepeatDays.equals("-1")) {
                String[] parts = mRepeatDays.split(",");
                for (String s : parts) {
                    mRepeatDayList.get(Integer.parseInt(s.trim()) - 1).setChecked(true);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScheduleApplication.inject(this);

        setContentView(R.layout.activity_edit);
        ButterKnife.inject(this);

        mRealm = Realm.getInstance(mAppContext);

        Toolbar toolbar = (Toolbar) findViewById(R.id.include_toolbar_long);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_close_white_24dp));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);

        // Only shows the delete button when it's in edit mode.
        boolean isEditMode = (mUniqueId != -1);
        menu.findItem(R.id.menu_action_delete).setVisible(isEditMode);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_action_save) {
            if (validateData()) {
                boolean newData = (mUniqueId == -1);

                // Save data
                saveData(newData);

                // End this Activity.
                finish();
            }
        } else if (id == R.id.menu_action_delete) {
            new AlertDialog.Builder(this)
                    .setMessage("Delete schedule?")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Remove data from database
                            deleteData();

                            // End this activity
                            finish();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
    }

    public boolean validateData() {
        if (mTitleText.getText().length() < 1 || mTitleText.getText().length() > 40) {
            mTitleText.setError("Title must be 1~40 characters");
            return false;
        } else if (mStartTerm != -1 && mEndTerm == -1) {
            mStartTermText.setError("End term is required");
            return false;
        } else if (mStartTerm == -1 && mEndTerm != -1) {
            mEndTermText.setError("Start term is required");
            return false;
        } else if (mStartHour != -1 && mEndHour == -1) {
            mStartTimeText.setError("End time is required");
            return false;
        } else if (mStartHour == -1 && mEndHour != -1) {
            mEndTimeText.setError("Start time is required");
            return false;
        } else if (mStartTerm != mEndTerm && mStartTerm >= mEndTerm) {
            mEndTermText.setError("Must be after start term");
            return false;
        } else if (mStartHour >= mEndHour && mStartMinute >= mEndMinute) {
            mEndTimeText.setError("Must be after start time");
            return false;
        }
        return true;
    }

    /**
     * Delete the current data from database and remove alarm for the schedule.
     */
    public void deleteData() {
        mRealm.beginTransaction();
        Schedule realmSchedule = mRealm.where(Schedule.class)
                .equalTo("uniqueId", mUniqueId).findFirst();
        realmSchedule.removeFromRealm();
        mRealm.commitTransaction();

        // Remove alarm from notifyReceiver
        NotifyReceiver notifyReceiver = new NotifyReceiver();
        notifyReceiver.removeAlarm(mAppContext, mUniqueId);
    }

    /**
     * Save current data to database or update it and activate the alarm for the schedule.
     *
     * @param newData if true then create new object in db else update
     */
    public void saveData(boolean newData) {
        mRealm.beginTransaction();
        Schedule realmSchedule;
        if (newData) {
            //Create object for new data and generate uid
            realmSchedule = mRealm.createObject(Schedule.class);

            // Generate UniqueID
            mUniqueId = ScheduleUID.get();
            realmSchedule.setUniqueId(mUniqueId);
        } else {
            //Fetch the information from database and identify by unique id
            realmSchedule = mRealm.where(Schedule.class)
                    .equalTo("uniqueId", mUniqueId).findFirst();
        }
        realmSchedule.setTitle(mTitleText.getText().toString());
        realmSchedule.setLocation(mLocationText.getText().toString());
        realmSchedule.setDescription(mDescriptionText.getText().toString());
        realmSchedule.setStartTerm(mStartTerm);
        realmSchedule.setEndTerm(mEndTerm);
        realmSchedule.setStartHour(mStartHour);
        realmSchedule.setStartMinute(mStartMinute);
        realmSchedule.setEndHour(mEndHour);
        realmSchedule.setEndMinute(mEndMinute);
        realmSchedule.setDays(getRepeatingDays());
        realmSchedule.setDisableMillis(-1);
        mRealm.commitTransaction();

        // Activate this schedule
        // If the time of this schedule already past, then DailyCheckService will handle it for the next day.
        NotifyReceiver notifyReceiver = new NotifyReceiver();
        long startTimeMillis = Utils.getTimeMillis(mStartHour, mStartMinute);
        long endTimeMillis = Utils.getTimeMillis(mEndHour, mEndMinute);

        // Reset the alarm because the data is changed
        notifyReceiver.removeAlarm(mAppContext, mUniqueId);

        if ((System.currentTimeMillis() < endTimeMillis) && // Either starts now or later.
                (System.currentTimeMillis() > mStartTerm && System.currentTimeMillis() < mEndTerm) && // Is in term
                mDisableMillis == -1 && // It is NOT disable
                Utils.isDayOfWeek(mRepeatDays)) { // It is active for today
            notifyReceiver.addAlarm(mAppContext, mUniqueId, startTimeMillis, endTimeMillis);
        }
    }

    private String getRepeatingDays() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < mRepeatDayList.size(); i++) {
            if (mRepeatDayList.get(i).isChecked()) {
                sb.append((i + 1)).append(", ");
            }
        }

        if (sb.length() == 0) {
            sb.append("-1");
        } else {
            sb.deleteCharAt(sb.lastIndexOf(", "));
        }
        return sb.toString();
    }


    @OnClick({R.id.edit_time_start_text, R.id.edit_time_end_text})
    public void promptClock(final View v) {
        Utils.hideKeyboard(this);

        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.setListener(new TimePickerFragment.Listener() {
            @Override
            public void onTimeReceive(long millis, int hour_of_day, int minutes) {
                ((MaterialEditText) v).setText(Utils.formatShowTime(mAppContext, millis));
                if (v.getId() == R.id.edit_time_start_text) {
                    mStartHour = hour_of_day;
                    mStartMinute = minutes;
                    mEndTimeText.performClick();
                } else if (v.getId() == R.id.edit_time_end_text) {
                    mEndHour = hour_of_day;
                    mEndMinute = minutes;
                }
            }
        });
        newFragment.show(getSupportFragmentManager(), "time_picker");
    }

    @OnClick({R.id.edit_term_start_text, R.id.edit_term_end_text})
    public void promptCalendar(final View v) {
        Utils.hideKeyboard(this);

        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setListener(new DatePickerFragment.Listener() {
            @Override
            public void onDateReceive(long millis, int year, int month, int day) {
                ((MaterialEditText) v).setText(Utils.formatShowDate(mAppContext, millis));

                if (v.getId() == R.id.edit_term_start_text) {
                    mStartTerm = millis;
                    mEndTermText.performClick();
                } else if (v.getId() == R.id.edit_term_end_text) {
                    mEndTerm = millis;
                }
            }
        });
        newFragment.show(getSupportFragmentManager(), "date_picker");
    }

    public static final class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private Listener listener;
        final Calendar c = Calendar.getInstance();

        @Override @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }


        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            c.setTimeInMillis(System.currentTimeMillis());
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, day);

            // Make the millis fair
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            listener.onDateReceive(c.getTimeInMillis(), year, month, day);
        }

        public void setListener(Listener l) {
            listener = l;
        }

        interface Listener {
            void onDateReceive(long millis, int year, int month, int day);
        }
    }

    public static final class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        private Listener listener;
        final Calendar c = Calendar.getInstance();

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
            c.setTimeInMillis(System.currentTimeMillis());
            c.set(Calendar.HOUR_OF_DAY, hourOfDay);
            c.set(Calendar.MINUTE, minute);

            listener.onTimeReceive(c.getTimeInMillis(), hourOfDay, minute);
        }

        public void setListener(Listener l) {
            listener = l;
        }

        interface Listener {
            void onTimeReceive(long millis, int hourOfDay, int minute);
        }
    }
}
