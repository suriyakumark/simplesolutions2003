package com.simplesolutions2003.hypertool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import static android.app.Service.START_STICKY;

/**
 * Created by Suriya on 5/11/2016.
 */
public class DateReceiver  extends BroadcastReceiver {
    public final static String LOG_TAG = DateReceiver.class.getSimpleName();
    public static final String ACTION_DATA_UPDATED_DATE = "com.simplesolutions2003.hypertool.ACTION_DATA_UPDATED_DATE";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.v(LOG_TAG,"Send Broadcast for date changes");
        Intent i = new Intent(ACTION_DATA_UPDATED_DATE);
        context.sendBroadcast(i);

        Float step_count = 0F;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        step_count = prefs.getFloat("step_count", 0F);

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putFloat("initial_step_count", step_count);
        editor.commit();
    }


}
