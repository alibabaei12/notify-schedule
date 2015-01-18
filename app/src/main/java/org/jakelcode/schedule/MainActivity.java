package org.jakelcode.schedule;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;

import java.util.List;

import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;


public class MainActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScheduleApplication app = (ScheduleApplication) getApplication();
        app.inject(app);

        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
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
        }

        return super.onOptionsItemSelected(item);
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
                    return true;
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
                    return true;
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
