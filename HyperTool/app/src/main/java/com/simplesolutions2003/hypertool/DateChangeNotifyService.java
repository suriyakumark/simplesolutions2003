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
import android.net.TrafficStats;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import static android.app.Service.START_STICKY;

/**
 * Created by Suriya on 5/11/2016.
 */
public class DateChangeNotifyService  extends Service {
    public final static String LOG_TAG = DateChangeNotifyService.class.getSimpleName();
    public static final String ACTION_DATA_UPDATED_DATE = "com.simplesolutions2003.hypertool.ACTION_DATA_UPDATED_DATE";
    public static final String ACTION_DATA_UPDATED_DATA_USAGE = "com.simplesolutions2003.hypertool.ACTION_DATA_UPDATED_DATA_USAGE";
    public static final String ACTION_DATA_UPDATED_WIFI_USAGE = "com.simplesolutions2003.hypertool.ACTION_DATA_UPDATED_WIFI_USAGE";

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

                SharedPreferences prefs;
                SharedPreferences.Editor editor;

                prefs = PreferenceManager.getDefaultSharedPreferences(context);

                /*Map<String, ?> allEntries = prefs.getAll();
                for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                    Log.v(LOG_TAG, "SharedPref - " + entry.getKey() + ": " + entry.getValue().toString());
                }*/

                String previous_date = prefs.getString(context.getString(R.string.pref_key_previous_date), "");
                int data_cycle = Integer.parseInt(prefs.getString(context.getString(R.string.pref_key_data_cycle), "1"));
                Float step_count = prefs.getFloat(context.getString(R.string.pref_key_step_count), 0F);
                Float initial_step_count = prefs.getFloat(context.getString(R.string.pref_key_initial_step_count), 0F);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String current_date = sdf.format(new Date());

                editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                editor.putString(context.getString(R.string.pref_key_current_date), current_date);

                boolean newDay = false;
                boolean firstTime = false;
                if(previous_date.isEmpty() || previous_date.equals("")){
                    firstTime = true;
                    editor.putString(context.getString(R.string.pref_key_previous_date), current_date);
                }

                if(!current_date.equals(previous_date) && !firstTime) {
                    editor.putString(context.getString(R.string.pref_key_previous_date), current_date);
                    newDay = true;
                }
                editor.commit();

                Calendar calendar = Calendar.getInstance();
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                int minute = calendar.get(Calendar.MINUTE);
                int frequency = 5;

                if(newDay) {

                    Calendar c = Calendar.getInstance();
                    c.setTime(new Date());
                    c.add(Calendar.DATE, 1);  // number of days to add
                    String next_date = sdf.format(c.getTime());
                    int dayOfMonthPlus1 = c.get(Calendar.DAY_OF_MONTH);

                    if (data_cycle == dayOfMonth || (dayOfMonthPlus1 < dayOfMonth && data_cycle > 27)) {
                        Log.v(LOG_TAG,"Data Cycle Reset");
                        //reset data
                        editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                        editor.putLong(context.getString(R.string.pref_key_data), 0);
                        editor.putLong(context.getString(R.string.pref_key_wifi), 0);
                        editor.commit();
                    }

                    //save previous day step count
                    Log.v(LOG_TAG,"Step Count Reset");
                    editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                    editor.putFloat(context.getString(R.string.pref_key_initial_step_count), step_count);
                    editor.commit();

                    Log.v(LOG_TAG,"Send Broadcast for date changes");
                    Intent i = new Intent(ACTION_DATA_UPDATED_DATE);
                    context.sendBroadcast(i);
                }

                if(minute % frequency == 0){
                    prefs = PreferenceManager.getDefaultSharedPreferences(context);

                    long cummDataUsage = prefs.getLong(context.getString(R.string.pref_key_data), 0);
                    long cummWifiUsage = prefs.getLong(context.getString(R.string.pref_key_wifi), 0);
                    long prevDataUsage = prefs.getLong(context.getString(R.string.pref_key_prev_data), 0);
                    long prevWifiUsage = prefs.getLong(context.getString(R.string.pref_key_prev_wifi), 0);

                    long mobileTx = TrafficStats.getMobileTxBytes();
                    long mobileRx = TrafficStats.getMobileRxBytes();
                    long wifiTx = TrafficStats.getTotalTxBytes() - mobileTx;
                    long wifiRx = TrafficStats.getTotalRxBytes() - mobileRx;
                    long currWifiUsage = wifiTx + wifiRx;
                    long currDataUsage = mobileTx + mobileRx;

                    long newDataUsage = cummDataUsage;
                    long newWifiUsage = cummWifiUsage;

                    if(prevDataUsage < currDataUsage || prevDataUsage == 0) {
                        newDataUsage += currDataUsage - prevDataUsage;
                    }else if(prevDataUsage > cummDataUsage){
                        newDataUsage += currDataUsage;
                    }
                    if(newDataUsage > cummDataUsage){
                        editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                        editor.putLong(context.getString(R.string.pref_key_data), newDataUsage);
                        editor.putLong(context.getString(R.string.pref_key_prev_data), currDataUsage);
                        editor.commit();

                        Log.v(LOG_TAG,"Send Broadcast for data changes");
                        Intent i = new Intent(ACTION_DATA_UPDATED_DATA_USAGE);
                        context.sendBroadcast(i);
                    }

                    if(prevWifiUsage < cummWifiUsage || prevWifiUsage == 0){
                        newWifiUsage += currWifiUsage - prevWifiUsage;
                    }else if(prevWifiUsage > cummWifiUsage){
                        newWifiUsage += currWifiUsage;
                    }
                    if(newWifiUsage > cummWifiUsage){
                        editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                        editor.putLong(context.getString(R.string.pref_key_wifi), newWifiUsage);
                        editor.putLong(context.getString(R.string.pref_key_prev_wifi), currWifiUsage);
                        editor.commit();

                        Log.v(LOG_TAG,"Send Broadcast for wifi changes");
                        Intent i = new Intent(ACTION_DATA_UPDATED_WIFI_USAGE);
                        context.sendBroadcast(i);

                    }

                }
            }
        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
