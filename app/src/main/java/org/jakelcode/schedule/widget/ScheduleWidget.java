package org.jakelcode.schedule.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import com.path.android.jobqueue.JobManager;

import org.jakelcode.schedule.EditActivity;
import org.jakelcode.schedule.MainActivity;
import org.jakelcode.schedule.R;
import org.jakelcode.schedule.ScheduleCache;
import org.jakelcode.schedule.Utils;
import org.jakelcode.schedule.event.ReceiveScheduleEvent;
import org.jakelcode.schedule.job.LoadingScheduleJob;
import org.jakelcode.schedule.realm.Schedule;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmResults;


/**
 * Implementation of App Widget functionality.
 */
public class ScheduleWidget extends AppWidgetProvider {
    protected final static String TOAST_ACTION = "org.jakelcode.schedule.widget.TOAST_ACTION";
    protected final static String EXTRA_ITEM_POS = "org.jakelcode.schedule.widget.EXTRA_ITEM_POS";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public static void notifyDataChange(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, ScheduleWidget.class));

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // Sets up the intent that points to the StackViewService that will
        // provide the views for this collection.
        Intent intent = new Intent(context, ScheduleWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        // When intents are compared, the extras are ignored, so we need to embed the extras
        // into the data so that the extras will not be ignored.
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_schedule_layout);
        rv.setRemoteAdapter(R.id.widget_list_view, intent);

        // The empty view is displayed when the collection has no items. It should be a sibling
        // of the collection view.
        rv.setEmptyView(R.id.widget_list_view, R.id.widget_empty_view);

        // This section makes it possible for items to have individualized behavior.
        // It does this by setting up a pending intent template. Individuals items of a collection
        // cannot set up their own pending intents. Instead, the collection as a whole sets
        // up a pending intent template, and the individual items set a fillInIntent
        // to create unique behavior on an item-by-item basis.
        Intent toastIntent = new Intent(context, ScheduleWidget.class);

        // Set the action for the intent.
        // When the user touches a particular view, it will have the effect of
        // broadcasting TOAST_ACTION.
        toastIntent.setAction(ScheduleWidget.TOAST_ACTION);
        toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setPendingIntentTemplate(R.id.widget_list_view, toastPendingIntent);

        Intent newIntent = new Intent(context, EditActivity.class);
        PendingIntent pendingNewIntent = PendingIntent.getActivity(context, 0, newIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setOnClickPendingIntent(R.id.widget_schedule_add_button, pendingNewIntent);

        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        if (intent.getAction().equals(TOAST_ACTION)) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            int viewIndex = intent.getIntExtra(EXTRA_ITEM_POS, 0);
            Toast.makeText(context, "Touched view " + viewIndex, Toast.LENGTH_SHORT).show();
        }
        super.onReceive(context, intent);
    }

    static class ScheduleWidgetFactory implements RemoteViewsService.RemoteViewsFactory {
        private static final String TAG = ScheduleWidgetFactory.class.getName();
        private List<ScheduleCache> mItems = new ArrayList<>();
        private Context mContext;
        private int mAppWidgetId;

        public ScheduleWidgetFactory(Context context, Intent intent) {
            mContext = context;
            mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        @Override
        public void onCreate() {
            List<ScheduleCache> results = loadFromDatabase();

            mItems.addAll(results);
        }

        @Override
        public void onDataSetChanged() {
            // This is triggered when you call AppWidgetManager notifyAppWidgetViewDataChanged
            // on the collection view corresponding to this factory. You can do heaving lifting in
            // here, synchronously. For example, if you need to process an image, fetch something
            // from the network, etc., it is ok to do it here, synchronously. The widget will remain
            // in its current state while work is being done here, so you don't need to worry about
            // locking up the widget.
            List<ScheduleCache> results = loadFromDatabase();

            mItems.clear();
            mItems.addAll(results);
        }

        @Override
        public void onDestroy() {
            mItems.clear();
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            ScheduleCache item = mItems.get(position);

            RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_schedule_item);
            remoteViews.setTextViewText(R.id.widget_schedule_item_title, item.getTitle());
            remoteViews.setTextViewText(R.id.widget_schedule_item_location, " @ " + item.getLocation());

            String scheduleTime = Utils.formatShowTime(mContext, item.getStartHour(), item.getStartMinute()) + " ~ " +
                    Utils.formatShowTime(mContext, item.getEndHour(), item.getEndMinute());
            remoteViews.setTextViewText(R.id.widget_schedule_item_time, scheduleTime);

            // Next, set a fill-intent, which will be used to fill in the pending intent template
            // that is set on the collection view in StackWidgetProvider.
            Bundle extras = new Bundle();
            extras.putInt(ScheduleWidget.EXTRA_ITEM_POS, position);

            Intent fillInIntent = new Intent();
            fillInIntent.putExtras(extras);

            // Make it possible to distinguish the individual on-click
            // action of a given item
            remoteViews.setOnClickFillInIntent(R.id.widget_schedule_item_layout, fillInIntent);
            return remoteViews;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return mItems.get(position).getUniqueId();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        public List<ScheduleCache> loadFromDatabase() {
            List<ScheduleCache> results = new ArrayList<ScheduleCache>();

            Realm mRealm = null;
            try {
                mRealm = Realm.getInstance(mContext);

                // Loading Schedule from database
                RealmResults<Schedule> realmResults = mRealm.where(Schedule.class).findAll();

                // Sort it descending, so the latest ones will be displayed on top
                // Also it is easier to obtain the last uniqueId exists in the database
                realmResults.sort("uniqueId", RealmResults.SORT_ORDER_DESCENDING);

                for (Schedule s : realmResults) {
                    results.add(new ScheduleCache(s));
                }

            } finally {
                if (mRealm != null) {
                    mRealm.close();
                }
            }

            return results;
        }
    }

    public static class ScheduleWidgetService extends RemoteViewsService {
        @Override
        public RemoteViewsFactory onGetViewFactory(Intent intent) {
            return new ScheduleWidgetFactory(this.getApplicationContext(), intent);
        }
    }

}


