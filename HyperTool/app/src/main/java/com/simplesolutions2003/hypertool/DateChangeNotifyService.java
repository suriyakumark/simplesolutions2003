package com.simplesolutions2003.hypertool;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Service.START_STICKY;

/**
 * Created by Suriya on 5/11/2016.
 */
public class DateChangeNotifyService  extends Service {
    public final static String LOG_TAG = DateChangeNotifyService.class.getSimpleName();
    public static final String ACTION_DATA_UPDATED_DATE = "com.simplesolutions2003.hypertool.ACTION_DATA_UPDATED_DATE";

    private Context context;

    public DateChangeNotifyService() {
    }

    public DateChangeNotifyService(Context context) {
        this.context = context;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        registerReceiver(receiver, new IntentFilter(Intent.ACTION_TIME_TICK));

        return START_STICKY;
    }


    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(LOG_TAG,"onReceive - " + intent.getAction());
            String action = intent.getAction();
            if(action.equals(Intent.ACTION_TIME_TICK)){

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                String previous_date = prefs.getString(context.getString(R.string.pref_key_previous_date), "");
                Float step_count = prefs.getFloat(context.getString(R.string.pref_key_step_count), 0F);
                Float initial_step_count = prefs.getFloat(context.getString(R.string.pref_key_initial_step_count), 0F);

                String current_date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                editor.putString(context.getString(R.string.pref_key_current_date), current_date);

                if(previous_date.isEmpty()){
                    editor.putString(context.getString(R.string.pref_key_previous_date), current_date);
                }else if(!current_date.equals(previous_date)){
                    editor.putString(context.getString(R.string.pref_key_previous_date), current_date);
                    editor.putFloat(context.getString(R.string.pref_key_initial_step_count), step_count);
                    Log.v(LOG_TAG,"initial_step_count prev - " + initial_step_count);
                    Log.v(LOG_TAG,"initial_step_count curr - " + step_count);

                    Log.v(LOG_TAG,"Send Broadcast for date changes");
                    Intent i = new Intent(ACTION_DATA_UPDATED_DATE);
                    context.sendBroadcast(i);
                }

                editor.commit();
            }
        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
