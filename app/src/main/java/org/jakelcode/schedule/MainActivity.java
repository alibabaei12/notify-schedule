package org.jakelcode.schedule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
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

import com.crashlytics.android.Crashlytics;
import com.melnykov.fab.FloatingActionButton;
import com.path.android.jobqueue.JobManager;

import org.jakelcode.schedule.event.ReceiveScheduleEvent;
import org.jakelcode.schedule.job.LoadingScheduleJob;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import io.fabric.sdk.android.Fabric;
import me.grantland.widget.AutofitHelper;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = MainActivity.class.getName();

    @Inject EventBus mEventBus;
    @Inject Context mAppContext;
    @Inject JobManager mJobManager;
    @Inject SharedPreferences mPreferences;

    @InjectView(R.id.schedule_recycle_view) RecyclerView mRecyclerView;
    @InjectView(R.id.schedule_fab_add) FloatingActionButton mFloatActionButton;

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

        //Initialize empty adapters, no listener cause no cards there.
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

        mFloatActionButton.attachToRecyclerView(mRecyclerView);
    }

    public void onEventMainThread(ReceiveScheduleEvent d) {
        if (d.getScheduleList().size() > 0) {
            // Initialize the unique identifier.
            ScheduleUID.set(d.getScheduleList().get(0).getUniqueId());

            setDailyCheckService(true);
        } else {
            // Disable daily check service if no alarm exists
            setDailyCheckService(false);
        }

        ScheduleAdapter newAdapter = new ScheduleAdapter(mAppContext, d.getScheduleList());
        if (mRecyclerView.getAdapter().getItemCount() > 0) {
            ScheduleAdapter oldAdapter = (ScheduleAdapter) mRecyclerView.getAdapter();

            if (newAdapter.getItemCount() > oldAdapter.getItemCount()) {
                for (int i = 0; i < newAdapter.getItemCount(); i++) {
                    // Comparing two items in parallel
                    if (i == oldAdapter.getItemCount() ||
                            newAdapter.getItem(i).getUniqueId() != oldAdapter.getItem(i).getUniqueId()) {
                        oldAdapter.getModelList().add(i, newAdapter.getItem(i));
                        oldAdapter.notifyItemInserted(i);
                        break; // Because only 1 new card can be added
                    }
                }
            } else if (newAdapter.getItemCount() < oldAdapter.getItemCount()) {
                for (int i = 0; i < oldAdapter.getItemCount(); i++) {
                    // Comparing two items in parallel
                    if (i == newAdapter.getItemCount() ||
                            newAdapter.getItem(i).getUniqueId() != oldAdapter.getItem(i).getUniqueId()) {
                        oldAdapter.getModelList().remove(i);
                        oldAdapter.notifyItemRemoved(i);
                        break; // Currently, only able to remove 1 card
                    }
                }
            } else { // Same length, data edit.
                for (int i = 0; i < newAdapter.getItemCount(); i++) {
                    if (newAdapter.getItem(i) != oldAdapter.getItem(i)) {
                        oldAdapter.getModelList().set(i, newAdapter.getItem(i));

                        oldAdapter.notifyItemChanged(i);
                        break; // Only change 1 card each time
                    }
                }
            }
        } else {
            mRecyclerView.setAdapter(newAdapter);
        }
    }

    public void setDailyCheckService(boolean enable) {
        final boolean dailyServiceActive = mPreferences.getBoolean(Utils.PREF_DAILY_CHECK, false);

        // Return if the setting is same as what will be set.
        if (dailyServiceActive == enable) {
            return;
        }

        final DailyCheckReceiver dailyReceiver = new DailyCheckReceiver();

        if (enable) {
            dailyReceiver.setDailyAlarm(mAppContext);
        } else {
            dailyReceiver.removeDailyAlarm(mAppContext);
        }

        mPreferences.edit().putBoolean(Utils.PREF_DAILY_CHECK, enable).apply();
    }

    @OnClick(R.id.schedule_fab_add)
    public void openEditActivity() {
        startActivity(new Intent(mAppContext, EditActivity.class));
    }

    final static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.schedule_card_title) TextView title;
        @InjectView(R.id.schedule_card_location) TextView location;
        @InjectView(R.id.schedule_card_desc) TextView description;
        @InjectView(R.id.schedule_card_time) TextView time;
        @InjectView(R.id.schedule_card_label) ImageView label;

        public ScheduleViewHolder(View itemView) {
            super(itemView);

            ButterKnife.inject(this, itemView);
        }

    }

    public final class ScheduleAdapter extends RecyclerView.Adapter<ScheduleViewHolder> {
        private final List<ScheduleCache> mModelList;
        private final Context mContext;


        public ScheduleAdapter(Context c, List<ScheduleCache> models) {
            mContext = c;
            mModelList = models;

            setHasStableIds(true);
        }

        public List<ScheduleCache> getModelList() {
            return mModelList;
        }

        public ScheduleCache getItem(int pos) {
            return mModelList.get(pos);
        }

        @Override
        public long getItemId(int position) {
            return mModelList.get(position).getUniqueId();
        }

        @Override
        public ScheduleViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
            //Load the layout for each card/entry.
            final View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.view_schedule_card, parent, false);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int holderPosition = ((RecyclerView) parent).getChildPosition(v);

                    Intent i = new Intent(mAppContext, EditActivity.class);
                    i.putExtra(Utils.PARCEL_SCHEDULE, mModelList.get(holderPosition));

                    startActivity(i);
                }
            });

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

            // Colors the card by its type.
            switch (model.getType()) {
                case ScheduleCache.NORMAL:
                    holder.label.setColorFilter(getResources().getColor(R.color.green_100), PorterDuff.Mode.SRC_IN);
                    break;
                case ScheduleCache.EXPIRED:
                    holder.label.setColorFilter(getResources().getColor(R.color.red_100), PorterDuff.Mode.SRC_IN);
                    break;
                case ScheduleCache.DISABLED:
                    holder.label.setColorFilter(getResources().getColor(R.color.blue_grey_100), PorterDuff.Mode.SRC_IN);
                    break;
                case ScheduleCache.FUTURE:
                    holder.label.setColorFilter(getResources().getColor(R.color.yellow_100), PorterDuff.Mode.SRC_IN);
                    break;
                default: break;
            }
        }

        @Override
        public int getItemCount() {
            return mModelList.size();
        }
    }
}
