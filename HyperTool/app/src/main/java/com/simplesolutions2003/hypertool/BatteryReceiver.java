package com.simplesolutions2003.hypertool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Suriya on 5/7/2016.
 */
public class BatteryReceiver extends BroadcastReceiver {
    public final static String LOG_TAG = BatteryReceiver.class.getSimpleName();
    public static final String ACTION_DATA_UPDATED_BATTERY = "com.simplesolutions2003.hypertool.ACTION_DATA_UPDATED_BATTERY";
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.v(LOG_TAG,"Send Broadcast for battery changes");
        Intent i = new Intent(ACTION_DATA_UPDATED_BATTERY);
        context.sendBroadcast(i);

    }

}
