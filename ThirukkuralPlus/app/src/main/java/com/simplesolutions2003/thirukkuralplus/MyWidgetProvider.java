package com.simplesolutions2003.thirukkuralplus;

/**
 * Created by simpl on 29-Dec-17.
 */

import java.util.Random;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.simplesolutions2003.thirukkuralplus.data.AppContract.KuralsEntry;

public class MyWidgetProvider extends AppWidgetProvider {
    public final static String TAG = MyWidgetProvider.class.getSimpleName();
    private static final String ACTION_CLICK = "ACTION_CLICK";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        // Get all ids
        ComponentName thisWidget = new ComponentName(context,
                MyWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds) {
            // create some random data
            int number = (new Random().nextInt(300));
            number++;

            String title_num = "";
            String app_menu_title_small = "";
            String app_menu_subtitle_small = "";
            String extra_info3 = "";
            String extra_subinfo3 = "";

            Cursor widgetRow = context.getContentResolver().query(
                    KuralsEntry.buildKuralsUri(number),KuralsFragment.KURALS_COLUMNS,
                    null,
                    null,
                    null);

            if(widgetRow != null){
                if(widgetRow.getCount() > 0) {
                    widgetRow.moveToFirst();
                    title_num = widgetRow.getString(KuralsFragment.COL_KURALS_ID);
                    app_menu_title_small = widgetRow.getString(KuralsFragment.COL_KURALS_NAME);
                    app_menu_subtitle_small = widgetRow.getString(KuralsFragment.COL_KURALS_NAME_ENG);
                    extra_info3 = widgetRow.getString(KuralsFragment.COL_CHAPTER_NAME);
                    extra_subinfo3 = widgetRow.getString(KuralsFragment.COL_CHAPTER_NAME_ENG);
                }
                widgetRow.close();
            }


            Log.w(TAG,"widgetRow " + widgetRow.getCount());
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_layout);
            // Set the text
            remoteViews.setTextViewText(R.id.title_num, title_num);
            remoteViews.setTextViewText(R.id.app_menu_title_small, app_menu_title_small);
            remoteViews.setTextViewText(R.id.app_menu_subtitle_small, app_menu_subtitle_small);
            remoteViews.setTextViewText(R.id.extra_info3, extra_info3);
            remoteViews.setTextViewText(R.id.extra_subinfo3, extra_subinfo3);

            if(!new Utilities().isEnglishEnabled(context)) {
                remoteViews.setViewVisibility(R.id.app_menu_subtitle_small, View.GONE);
                remoteViews.setViewVisibility(R.id.extra_subinfo3, View.GONE);
            }else{
                remoteViews.setViewVisibility(R.id.app_menu_subtitle_small, View.VISIBLE);
                remoteViews.setViewVisibility(R.id.extra_subinfo3, View.VISIBLE);
            }

            remoteViews.setTextColor(R.id.title_num,new Utilities().getWidgetColor(context));
            remoteViews.setTextColor(R.id.app_menu_title_small,new Utilities().getWidgetColor(context));
            remoteViews.setTextColor(R.id.app_menu_subtitle_small,new Utilities().getWidgetColor(context));
            remoteViews.setTextColor(R.id.extra_info3,new Utilities().getWidgetColor(context));
            remoteViews.setTextColor(R.id.extra_subinfo3,new Utilities().getWidgetColor(context));

            // Register an onClickListener
            Intent intent = new Intent(context, MyWidgetProvider.class);

            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.widget_view, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }
}