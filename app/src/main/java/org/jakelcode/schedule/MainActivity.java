package org.jakelcode.schedule;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
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
//        @InjectView(R.id.card_title_text) EditText title;
//        @InjectView(R.id.card_location_text) EditText location;
//        @InjectView(R.id.card_description_text) EditText description;

        public ScheduleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(itemView);
        }
    }

    final static class ScheduleListAdapter extends RecyclerView.Adapter<ScheduleViewHolder> {
        private final List<ScheduleData> mModelList;

        public ScheduleListAdapter(List<ScheduleData> models) {
            mModelList = models;
        }

        @Override
        public ScheduleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //Load the layout for each card/entry.
            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.abc_search_view, parent, false);

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


        }

        @Override
        public int getItemCount() {
            return mModelList.size();
        }
    }
}
