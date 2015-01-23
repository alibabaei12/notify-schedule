package org.jakelcode.schedule;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.util.TimeUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.internal.ButterKnifeProcessor;
import io.fabric.sdk.android.Fabric;


public class MainActivity extends ActionBarActivity {
    private final NotifyReceiver receiver = new NotifyReceiver();
    private int Alarm_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((ScheduleApplication) getApplication()).inject(this);

        if (!BuildConfig.DEBUG)
            Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.include_toolbar);
        setSupportActionBar(toolbar);


        // Testing cards.
        ScheduleData testScheduleData = new ScheduleData("Awesome Club Meeting Pineapple Sweet awesome", "Room 101", "mini mini long stuffs.", 0, 0, 1100, 1220, null);
        ScheduleData testScheduleData2 = new ScheduleData("Awesome Club Meeting", "Room 201", "mini mini long stuffs.", 0, 0, 1100, 1220, null);
        List<ScheduleData> scheduleList = new ArrayList<ScheduleData>();

        scheduleList.add(testScheduleData);
        scheduleList.add(testScheduleData2);

        ScheduleListAdapter adapter = new ScheduleListAdapter(getApplicationContext(), scheduleList);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.schedule_recycle_view);

        // improve performance if you know that changes in content
        // do not change the size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(adapter);
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
        } else if (id == R.id.add_alarm) {
            AlarmOperation(true);
            return true;
        } else if (id == R.id.remove_alarm) {
            AlarmOperation(false);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void AlarmOperation(boolean mode) {
        if (mode) { // adding
            Alarm_ID++;
            receiver.addAlarm(getApplicationContext(), Alarm_ID, 0, 0);
            Toast.makeText(getApplicationContext(), "Alarm Added : " + Alarm_ID, Toast.LENGTH_SHORT).show();
        } else {
            receiver.removeAlarm(getApplicationContext(), Alarm_ID);
            Toast.makeText(getApplicationContext(), "Alarm Removed : " + Alarm_ID, Toast.LENGTH_SHORT).show();
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

            final int HORIZONTAL_DRAG_THRESHOLD = 100;
            final int VERTICAL_DRAG_THRESHOLD = 50;

            itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: // Short and Long click
                            break;
                    }
                    return false;
                }
            });
            itemView.setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View v, DragEvent event) {
                    switch (event.getAction()) {
                        case DragEvent.ACTION_DRAG_ENDED:
                            if (Math.abs(event.getX()) > HORIZONTAL_DRAG_THRESHOLD && Math.abs(event.getY()) < VERTICAL_DRAG_THRESHOLD) {

                            }
                            break;
                    }
                    return false;
                }
            });

            return new ScheduleViewHolder(itemView);
        }


        @Override
        public void onBindViewHolder(ScheduleViewHolder holder, int position) {
            //Set the information... modellist -> holder
            ScheduleData model = mModelList.get(position);

            holder.image.setBackgroundColor(getResources().getColor(R.color.light_blue_900));

            holder.title.setText(model.getTitle());

            holder.location.setText(model.getLocation());

            holder.description.setText(model.getDescription());

//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
//            simpleDateFormat.format(new Date(System.currentTimeMillis()));

            holder.time.setText(DateUtils.formatDateTime(mContext, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_DATE) + " ~ " + model.getEndTimestamp());
        }

        @Override
        public int getItemCount() {
            return mModelList.size();
        }
    }

    private static int getLineCount(CharSequence text, TextPaint paint, float size, float width,
                                    DisplayMetrics displayMetrics) {
        paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, size,
                displayMetrics));
        StaticLayout layout = new StaticLayout(text, paint, (int)width,
                Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);

        return layout.getLineCount();
    }
}
