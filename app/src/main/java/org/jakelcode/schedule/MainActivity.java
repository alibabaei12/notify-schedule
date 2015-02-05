package org.jakelcode.schedule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.path.android.jobqueue.JobManager;

import org.jakelcode.schedule.event.ReceiveScheduleEvent;
import org.jakelcode.schedule.job.LoadingScheduleJob;
import org.jakelcode.schedule.realm.ScheduleCache;

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
    private static final String PREF_DAILY_CHECK = "daily-check";

    @Inject EventBus mEventBus;
    @Inject Context mAppContext;
    @Inject JobManager mJobManager;
    @Inject SharedPreferences mPreferences;
    private DailyCheckReceiver mDailyCheckReceiver = new DailyCheckReceiver();

    @InjectView(R.id.schedule_recycle_view) RecyclerView mRecyclerView;

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

        //Initialize empty adapters
        setupRecycleView(new ScheduleAdapter(mAppContext, new ArrayList<ScheduleCache>()));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mJobManager.addJobInBackground(new LoadingScheduleJob(this));
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent checkReceiverIntent = new Intent(mAppContext, DailyCheckReceiver.class);
            checkReceiverIntent.setAction(DailyCheckReceiver.ACTION_DAILY_CHECK);
            sendBroadcast(checkReceiverIntent);

            return true;
        } if (id == R.id.edit_activity) {
            startActivity(new Intent(mAppContext, EditActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setupRecycleView(ScheduleAdapter adapter) {
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
        if (d.getScheduleList().size() > 0) {
            // Initialize the unique identifier.
            ScheduleUID.set(d.getScheduleList().get(0).getUniqueId());

            setDailyCheckService(true);
        } else {
            // Cancel daily check service if no alarm exists
            setDailyCheckService(false);
        }

        ScheduleAdapter adapter = new ScheduleAdapter(mAppContext, d.getScheduleList());
        if (mRecyclerView.getAdapter() != null) {
            mRecyclerView.swapAdapter(adapter, true);
        } else {
            mRecyclerView.setAdapter(adapter);
        }
    }


    public void setDailyCheckService(boolean enable) {
        final boolean dailyServiceActive = mPreferences.getBoolean(PREF_DAILY_CHECK, false);

        // If preferences is disable and asking to disable = return or
        // If preference is enable and asking to enable = return
        if ((!dailyServiceActive && !enable) || (dailyServiceActive && enable)) {
            return;
        }

        if (enable) {
            Intent checkReceiverIntent = new Intent();
            checkReceiverIntent.setAction(DailyCheckReceiver.ACTION_DAILY_CHECK);
            sendBroadcast(checkReceiverIntent);

            mDailyCheckReceiver.setDailyAlarm(mAppContext);
        } else {
            mDailyCheckReceiver.removeDailyAlarm(mAppContext);
        }

        mPreferences.edit().putBoolean(PREF_DAILY_CHECK, enable).apply();
    }

    final static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.schedule_card_title) TextView title;
        @InjectView(R.id.schedule_card_location) TextView location;
        @InjectView(R.id.schedule_card_desc) TextView description;
        @InjectView(R.id.schedule_card_time) TextView time;

        public ScheduleViewHolder(View itemView) {
            super(itemView);

            ButterKnife.inject(this, itemView);
        }
    }

    final class ScheduleAdapter extends RecyclerView.Adapter<ScheduleViewHolder> {
        private final List<ScheduleCache> mModelList;
        private final Context mContext;

        public ScheduleAdapter(Context c, List<ScheduleCache> models) {
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
            ScheduleCache model = mModelList.get(position);

            holder.title.setText(model.getTitle());
            AutofitHelper.create(holder.title).setMaxLines(2); // Fitting a bunch of text into 2 lines

            holder.location.setText(model.getLocation());

            holder.description.setText(model.getDescription());

            holder.time.setText(Utils.formatShowTime(mContext, model.getStartHour(), model.getStartMinute()) + " ~ "
                    + Utils.formatShowTime(mContext, model.getEndHour(), model.getEndMinute()));
        }

        @Override
        public int getItemCount() {
            return mModelList.size();
        }
    }
}
