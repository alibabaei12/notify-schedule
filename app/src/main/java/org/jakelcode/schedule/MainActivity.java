package org.jakelcode.schedule;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.path.android.jobqueue.JobManager;

import org.jakelcode.schedule.event.ReceiveScheduleEvent;
import org.jakelcode.schedule.job.LoadingScheduleJob;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import io.fabric.sdk.android.Fabric;
import me.grantland.widget.AutofitHelper;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = MainActivity.class.getName();

    @Inject EventBus mEventBus;
    @Inject Context mAppContext;
    @Inject NotifyReceiver mNotifyReceiver;
    @Inject JobManager mJobManager;

    @InjectView(R.id.schedule_recycle_view) RecyclerView mRecyclerView;

    private int Alarm_ID = 0; // Just for debug purpose.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScheduleApplication.inject(this);
        mEventBus.register(this);

        if (!BuildConfig.DEBUG)
            Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.include_toolbar);
        setSupportActionBar(toolbar);
        setupRecycleView();

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Look at Android Activity Life Cycle to understand why load in onStart
        // Loading schedules from database
        mJobManager.addJobInBackground(new LoadingScheduleJob(mAppContext));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEventBus.unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        // FOR DEBUG PURPOSE.
        // adding button to screen is ugly and 'more' work.
        } else if (id == R.id.add_alarm) {
            AlarmOperation(true);
            return true;
        } else if (id == R.id.remove_alarm) {
            AlarmOperation(false);
            return true;
        } else if (id == R.id.edit_activity) {
            startActivity(new Intent(mAppContext, EditActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // FOR DEBUG PURPOSE.
    // A test for receiver.addAlarm and receiver.removeAlarm
    public void AlarmOperation(boolean mode) {
        if (mode) { // adding
            Alarm_ID++;
            mNotifyReceiver.addAlarm(getApplicationContext(), Alarm_ID, 0, 0);
            Toast.makeText(getApplicationContext(), "Alarm Added : " + Alarm_ID, Toast.LENGTH_SHORT).show();
        } else {
            mNotifyReceiver.removeAlarm(getApplicationContext(), Alarm_ID);
            Toast.makeText(getApplicationContext(), "Alarm Removed : " + Alarm_ID, Toast.LENGTH_SHORT).show();
        }
    }

    public void setupRecycleView() {
        final List<ScheduleData> dataList = new ArrayList<>();

        ScheduleListAdapter adapter = new ScheduleListAdapter(mAppContext, dataList);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.schedule_recycle_view);

        // improve performance if you know that changes in content
        // do not change the size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mAppContext);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(adapter);
    }

    public void onEventMainThread(ReceiveScheduleEvent d) {
        ScheduleListAdapter adapter = new ScheduleListAdapter(mAppContext, d.getScheduleList());
        if (mRecyclerView.getAdapter() != null) {
            mRecyclerView.swapAdapter(adapter, true);
        } else {
            mRecyclerView.setAdapter(adapter);
        }
    }

    final static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.schedule_card_image) ImageView image;
        @InjectView(R.id.schedule_card_title) TextView title;
        @InjectView(R.id.schedule_card_location) TextView location;
        @InjectView(R.id.schedule_card_desc) TextView description;
        @InjectView(R.id.schedule_card_time) TextView time;

        public ScheduleViewHolder(View itemView) {
            super(itemView);

            ButterKnife.inject(this, itemView);
        }
    }

    final class ScheduleListAdapter extends RecyclerView.Adapter<ScheduleViewHolder> {
        private final List<ScheduleData> mModelList;
        private final Context mContext;

        public ScheduleListAdapter(Context c, List<ScheduleData> models) {
            mContext = c;
            mModelList = models;
        }

        @Override
        public ScheduleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //Load the layout for each card/entry.
            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.view_schedule_card, parent, false);

            return new ScheduleViewHolder(itemView);
        }


        @Override
        public void onBindViewHolder(ScheduleViewHolder holder, int position) {
            //Set the information... modellist -> holder
            ScheduleData model = mModelList.get(position);

            holder.image.setBackgroundColor(getResources().getColor(R.color.light_blue_900));

            holder.title.setText(model.getTitle());
            AutofitHelper.create(holder.title).setMaxLines(2); // Fitting a bunch of text into 2 lines

            holder.location.setText(model.getLocation());

            holder.description.setText(model.getDescription());

            holder.time.setText(model.getStartTimeString(mContext) + " ~ " + model.getEndTimeString(mContext));
        }

        @Override
        public int getItemCount() {
            return mModelList.size();
        }
    }
}
