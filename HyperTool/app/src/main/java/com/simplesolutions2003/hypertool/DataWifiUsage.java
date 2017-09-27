package com.simplesolutions2003.hypertool;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.TrafficStats;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by simpl on 9/26/2017.
 */

public class DataWifiUsage {
    private static final String LOG_TAG = DataWifiUsage.class.getSimpleName();
    private static Context context;
    public static void setContext(Context c){
        context = c;
    }

    public static void resetData(){
        Log.v(LOG_TAG,"resetData");
        long mobileTx = TrafficStats.getMobileTxBytes();
        long mobileRx = TrafficStats.getMobileRxBytes();
        long currDataUsage = mobileTx + mobileRx;
        //Log.v(LOG_TAG,"currDataUsage " + currDataUsage);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putLong(context.getString(R.string.pref_key_data), 0);
        editor.putLong(context.getString(R.string.pref_key_prev_data), currDataUsage);
        editor.commit();
    }

    public static void resetWifi(){
        Log.v(LOG_TAG,"resetWifi");
        long mobileTx = TrafficStats.getMobileTxBytes();
        long mobileRx = TrafficStats.getMobileRxBytes();
        long wifiTx = TrafficStats.getTotalTxBytes() - mobileTx;
        long wifiRx = TrafficStats.getTotalRxBytes() - mobileRx;
        long currWifiUsage = wifiTx + wifiRx;
        //Log.v(LOG_TAG,"currWifiUsage " + currWifiUsage);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putLong(context.getString(R.string.pref_key_wifi), 0);
        editor.putLong(context.getString(R.string.pref_key_prev_wifi), currWifiUsage);
        editor.commit();
    }

    public static boolean getDataUsage(){
        Log.v(LOG_TAG,"getDataUsage");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        long cummDataUsage = prefs.getLong(context.getString(R.string.pref_key_data), 0);
        long prevDataUsage = prefs.getLong(context.getString(R.string.pref_key_prev_data), 0);
        //Log.v(LOG_TAG,"prevDataUsage " + prevDataUsage);
        if(prevDataUsage == 0 && cummDataUsage == 0){
            resetData();
        }

        cummDataUsage = prefs.getLong(context.getString(R.string.pref_key_data), 0);
        prevDataUsage = prefs.getLong(context.getString(R.string.pref_key_prev_data), 0);

        long mobileTx = TrafficStats.getMobileTxBytes();
        long mobileRx = TrafficStats.getMobileRxBytes();
        long currDataUsage = mobileTx + mobileRx;
        //Log.v(LOG_TAG,"currDataUsage " + currDataUsage);
        long newDataUsage = cummDataUsage;

        //Log.v(LOG_TAG,"cummDataUsage " + cummDataUsage);
        if(prevDataUsage < currDataUsage) {
            newDataUsage += currDataUsage - prevDataUsage;
        }else if(prevDataUsage > currDataUsage){
            newDataUsage += currDataUsage;
        }
        //Log.v(LOG_TAG,"newDataUsage " + newDataUsage);
        if(newDataUsage > cummDataUsage){
            //Log.v(LOG_TAG,"data usage updated");
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            editor.putLong(context.getString(R.string.pref_key_data), newDataUsage);
            editor.putLong(context.getString(R.string.pref_key_prev_data), currDataUsage);
            editor.commit();
            return true;
        }
        return false;
    }

    public static boolean getWifiUsage(){
        Log.v(LOG_TAG,"getWifiUsage");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        long cummWifiUsage = prefs.getLong(context.getString(R.string.pref_key_wifi), 0);
        long prevWifiUsage = prefs.getLong(context.getString(R.string.pref_key_prev_wifi), 0);
        //Log.v(LOG_TAG,"prevWifiUsage " + prevWifiUsage);
        if(prevWifiUsage == 0 && cummWifiUsage == 0){
            resetWifi();
        }
        cummWifiUsage = prefs.getLong(context.getString(R.string.pref_key_wifi), 0);
        prevWifiUsage = prefs.getLong(context.getString(R.string.pref_key_prev_wifi), 0);

        long mobileTx = TrafficStats.getMobileTxBytes();
        long mobileRx = TrafficStats.getMobileRxBytes();
        long wifiTx = TrafficStats.getTotalTxBytes() - mobileTx;
        long wifiRx = TrafficStats.getTotalRxBytes() - mobileRx;
        long currWifiUsage = wifiTx + wifiRx;
        //Log.v(LOG_TAG,"currWifiUsage " + currWifiUsage);
        long newWifiUsage = cummWifiUsage;

        //Log.v(LOG_TAG,"cummWifiUsage " + cummWifiUsage);
        if(prevWifiUsage < currWifiUsage){
            newWifiUsage += currWifiUsage - prevWifiUsage;
        }else if(prevWifiUsage > currWifiUsage){
            newWifiUsage += currWifiUsage;
        }
        //Log.v(LOG_TAG,"newWifiUsage " + newWifiUsage);
        if(newWifiUsage > cummWifiUsage){
            //Log.v(LOG_TAG,"wifi usage updated");
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            editor.putLong(context.getString(R.string.pref_key_wifi), newWifiUsage);
            editor.putLong(context.getString(R.string.pref_key_prev_wifi), currWifiUsage);
            editor.commit();
            return true;
        }
        return false;
    }
}
