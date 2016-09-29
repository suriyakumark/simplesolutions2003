package com.simplesolutions2003.happybabycare.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.simplesolutions2003.happybabycare.MainActivity;
import com.simplesolutions2003.happybabycare.R;
import com.simplesolutions2003.happybabycare.Utilities;

/**
 * Created by SuriyaKumar on 9/10/2016.
 */
public class ActivitiesWidgetProvider extends AppWidgetProvider {

    private static final String LOG_TAG = ActivitiesWidgetProvider.class.getSimpleName();

    public static final String UPDATE_ACTION = "android.appwidget.action.APPWIDGET_UPDATE";

    public static final String OPEN_ACTION = "APPWIDGET_OPEN_APP";
    public static final String REFRESH_ACTION = "APPWIDGET_REFRESH_WIDGET";

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.v(LOG_TAG, "onUpdate");

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.widget_activities);

        // Create an Intent to launch MainActivity
        Intent intentOpen = new Intent(context, MainActivity.class);
        //intentOpen.setAction(OPEN_ACTION);
        PendingIntent pendingIntentOpen = PendingIntent.getActivity(context, 0, intentOpen, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_content, pendingIntentOpen);

        // Create an Intent to Refresh widget
        Intent intentRefresh = new Intent(context, ActivitiesWidgetProvider.class);
        intentRefresh.setAction(REFRESH_ACTION);
        PendingIntent pendingIntentRefresh = PendingIntent.getBroadcast(context, 0, intentRefresh, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_refresh, pendingIntentRefresh);

        remoteViews.setTextViewText(R.id.widget_touch_to_refresh, context.getString(R.string.text_touch_to_refresh));
        remoteViews.setTextViewText(R.id.widget_last_refresh, "Updated : " + new Utilities().getCurrentDateTimeDisp());

        for (int appWidgetId : appWidgetIds) {
            Log.v(LOG_TAG, "widget - " + appWidgetIds.toString());
        }

        setRemoteAdapter(context, remoteViews);
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widgetAdapterViewFlipper);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        Log.v(LOG_TAG, "onReceive");
        super.onReceive(context, intent);

        String action = intent.getAction();

        Log.v(LOG_TAG,"action - " + action);

        if (action.equals(UPDATE_ACTION)  | action.equals(REFRESH_ACTION)) {

            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.widget_activities);
            views.setTextViewText(R.id.widget_touch_to_refresh, context.getString(R.string.text_touch_to_refresh));
            views.setTextViewText(R.id.widget_last_refresh, "Updated : " + new Utilities().getCurrentDateTimeDisp());

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            ComponentName componentName = new ComponentName(context, getClass());
            appWidgetManager.updateAppWidget(componentName, views);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widgetAdapterViewFlipper);
        }

    }

    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        Log.v(LOG_TAG, "setRemoteAdapter");
        views.setRemoteAdapter(R.id.widgetAdapterViewFlipper,
                new Intent(context, ActivitiesWidgetRemoteViewsService.class));
    }

}
