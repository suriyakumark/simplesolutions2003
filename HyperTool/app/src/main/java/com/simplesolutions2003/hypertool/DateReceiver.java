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

import java.text.SimpleDateFormat;
import java.util.Date;

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

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String previous_date = prefs.getString("previous_date", "");
        Float step_count = prefs.getFloat("step_count", 0F);

        String current_date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString("current_date", current_date);

        if(previous_date.isEmpty()){
            editor.putString("previous_date", current_date);
        }else if(!current_date.equals(previous_date)){
            editor.putString("previous_date", current_date);
            Intent i = new Intent(ACTION_DATA_UPDATED_DATE);
            context.sendBroadcast(i);
            editor.putFloat("initial_step_count", step_count);
        }

        editor.commit();
    }


}
