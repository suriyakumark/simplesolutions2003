package com.simplesolutions2003.happybabycare.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.simplesolutions2003.happybabycare.MainActivity;
import com.simplesolutions2003.happybabycare.R;
import com.simplesolutions2003.happybabycare.SignInFragment;
import com.simplesolutions2003.happybabycare.Utilities;
import com.simplesolutions2003.happybabycare.data.AppContract;

import java.util.Date;

/**
 * Created by SuriyaKumar on 9/10/2016.
 */
public class ActivitiesWidgetRemoteViewsService extends RemoteViewsService {

    private static final String TAG = ActivitiesWidgetRemoteViewsService.class.getSimpleName();

    long activeUserId = -1;
    String activeUser = null;

    private static final String[] USER_COLUMNS = {
            AppContract.UserEntry.TABLE_NAME + "." + AppContract.UserEntry._ID,
            AppContract.UserEntry.TABLE_NAME + "." + AppContract.UserEntry.COLUMN_USER_ID
    };

    private static final String[] BABY_COLUMNS = {
            AppContract.BabyEntry.TABLE_NAME + "." + AppContract.BabyEntry._ID,
            AppContract.BabyEntry.TABLE_NAME + "." + AppContract.BabyEntry.COLUMN_USER_ID,
            AppContract.BabyEntry.TABLE_NAME + "." + AppContract.BabyEntry.COLUMN_NAME
    };

    private static final String[] ACTIVITY_COLUMNS = {
            AppContract.ActivitiesEntry.COLUMN_ACTIVITY_TYPE,
            AppContract.ActivitiesEntry.COLUMN_ACTIVITY_ID,
            AppContract.ActivitiesEntry.COLUMN_USER_ID,
            AppContract.ActivitiesEntry.COLUMN_BABY_ID,
            AppContract.ActivitiesEntry.COLUMN_TIMESTAMP,
            AppContract.ActivitiesEntry.COLUMN_DATE,
            AppContract.ActivitiesEntry.COLUMN_TIME,
            AppContract.ActivitiesEntry.COLUMN_SUMMARY
    };

    private static final String ACTIVITY_SORT =
            AppContract.ActivitiesEntry.COLUMN_TIME + " DESC";

    private static final int COL_ID = 0;
    private static final int COL_USER_ID = 1;

    private static final int COL_BABY_ID = 0;
    private static final int COL_BABY_USER_ID = 1;
    private static final int COL_BABY_NAME = 2;

    private static final int COL_ACTIVITY_TYPE = 0;
    private static final int COL_ACTIVITY_ID = 1;
    private static final int COL_ACTIVITY_USER_ID = 2;
    private static final int COL_ACTIVITY_BABY_ID = 3;
    private static final int COL_ACTIVITY_TIMESTAMP = 4;
    private static final int COL_ACTIVITY_DATE = 5;
    private static final int COL_ACTIVITY_TIME = 6;
    private static final int COL_ACTIVITY_SUMMARY = 7;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d(TAG, "onGetViewFactory()");
        return new ViewFactory(intent);
    }

    private class ViewFactory implements RemoteViewsService.RemoteViewsFactory {

        private int mInstanceId = AppWidgetManager.INVALID_APPWIDGET_ID;
        private Cursor data = null;

        public ViewFactory(Intent intent) {
            mInstanceId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID );
        }

        @Override
        public void onCreate() {
            Log.i(TAG, "onCreate()");

        }

        @Override
        public void onDataSetChanged() {
            Log.i(TAG, "onDataSetChanged()");
            if (data != null) {
                data.close();
            }

            final long identityToken = Binder.clearCallingIdentity();

            Uri query_user_uri = AppContract.UserEntry.CONTENT_URI;
            String sSelection = AppContract.UserEntry.TABLE_NAME + "." + AppContract.UserEntry.COLUMN_ACTIVE + " = ? ";
            String[] sSelectionArgs = new String[]{Long.toString(SignInFragment.ACTIVE)};
            Cursor userCursor = getContentResolver().query(query_user_uri,USER_COLUMNS,sSelection,sSelectionArgs,null);

            if(userCursor!=null) {
                if (userCursor.getCount() == 1) {
                    userCursor.moveToFirst();
                    activeUserId = userCursor.getLong(COL_ID);
                    activeUser = userCursor.getString(COL_USER_ID);
                }
            }

            if(activeUser != null){
                Uri query_baby_uri = AppContract.BabyEntry.buildBabyByUserIdUri(activeUser);
                data = getContentResolver().query(query_baby_uri,BABY_COLUMNS,null,null,null);
            }

            Binder.restoreCallingIdentity(identityToken);
        }

        @Override
        public void onDestroy() {
            Log.i(TAG, "onDestroy()");
            if (data != null) {
                data.close();
                data = null;
            }
        }

        @Override
        public int getCount() {
            Log.i(TAG, "getCount() " + 0);
            return data == null ? 0 : data.getCount();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            Log.i(TAG, "getViewAt()" + position);

            if (position == AdapterView.INVALID_POSITION ||
                    data == null || !data.moveToPosition(position)) {
                return null;
            }

            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.widget_activities_item);

            remoteViews.setTextViewText(R.id.widget_baby, data.getString(COL_BABY_NAME));
            long babyId = data.getLong(COL_BABY_ID);
            String activityDate = new Utilities().getCurrentDateDB();
            Uri activitiesUri = AppContract.ActivitiesEntry.buildActivitiesByUserIdBabyIdUri(activeUser,babyId,activityDate);
            Cursor activitiesCursor = getContentResolver().query(activitiesUri,ACTIVITY_COLUMNS,null,null,ACTIVITY_SORT);
            remoteViews.setTextViewText(R.id.last_feeding, "None today");
            remoteViews.setTextViewText(R.id.last_diaper, "None today");
            remoteViews.setTextViewText(R.id.last_sleeping, "None today");
            remoteViews.setTextViewText(R.id.last_health, "None today");

            if(activitiesCursor != null) {
                Log.i(TAG, "activitiesCursor" + activitiesCursor.getCount());
                if (activitiesCursor.getCount() > 0) {

                    boolean bFeedingFound = false;
                    boolean bDiaperFound = false;
                    boolean bSleepingFound = false;
                    boolean bHealthFound = false;

                    for (int i = 0; i < activitiesCursor.getCount(); i++) {
                        activitiesCursor.moveToNext();
                        if (!bFeedingFound & activitiesCursor.getString(COL_ACTIVITY_TYPE).equals("Feeding")) {
                            remoteViews.setTextViewText(R.id.last_feeding, activitiesCursor.getString(COL_ACTIVITY_TIME) + " " +
                                    activitiesCursor.getString(COL_ACTIVITY_SUMMARY));
                            bFeedingFound = true;
                        }
                        if (!bDiaperFound & activitiesCursor.getString(COL_ACTIVITY_TYPE).equals("Diaper")) {
                            remoteViews.setTextViewText(R.id.last_diaper, activitiesCursor.getString(COL_ACTIVITY_TIME) + " " +
                                    activitiesCursor.getString(COL_ACTIVITY_SUMMARY));
                            bDiaperFound = true;
                        }
                        if (!bSleepingFound & activitiesCursor.getString(COL_ACTIVITY_TYPE).equals("Sleeping")) {
                            remoteViews.setTextViewText(R.id.last_sleeping, activitiesCursor.getString(COL_ACTIVITY_TIME) + " " +
                                    activitiesCursor.getString(COL_ACTIVITY_SUMMARY));
                            bSleepingFound = true;
                        }
                        if (!bHealthFound & activitiesCursor.getString(COL_ACTIVITY_TYPE).equals("Health")) {
                            remoteViews.setTextViewText(R.id.last_health, activitiesCursor.getString(COL_ACTIVITY_TIME) + " " +
                                    activitiesCursor.getString(COL_ACTIVITY_SUMMARY));
                            bHealthFound = true;
                        }
                    }
                    activitiesCursor.close();
                }
            }
            return remoteViews;
        }

        @Override
        public RemoteViews getLoadingView() {
            Log.i(TAG, "getLoadingView()");
            return new RemoteViews(getPackageName(), R.layout.widget_activities_item);
        }

        @Override
        public int getViewTypeCount() {
            Log.i(TAG, "getViewTypeCount()");
            return 1;
        }

        @Override
        public long getItemId(int position) {
            Log.i(TAG, "getItemId()");
            return position;
        }

        @Override
        public boolean hasStableIds() {
            Log.i(TAG, "hasStableIds()");
            return true;
        }

    }

}
