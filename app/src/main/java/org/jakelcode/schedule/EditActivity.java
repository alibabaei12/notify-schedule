package org.jakelcode.schedule;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.jakelcode.schedule.realm.Schedule;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.ButterKnife;
import io.realm.Realm;


public class EditActivity extends ActionBarActivity {
    private static final String TAG = EditActivity.class.getName();
    @Inject Realm mRealm;
    @Inject Context mAppContext;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScheduleApplication.inject(this);
        ButterKnife.inject(this);

        setContentView(R.layout.activity_edit);

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
        mRealm.commitTransaction();

        return true;
    }
}
