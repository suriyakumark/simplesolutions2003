package com.simplesolutions2003.hypertool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Suriya on 5/7/2016.
 */
public class WifiReceiver extends BroadcastReceiver {
    public final static String LOG_TAG = WifiReceiver.class.getSimpleName();
    public static final String ACTION_DATA_UPDATED_WIFI = "com.simplesolutions2003.hypertool.ACTION_DATA_UPDATED_WIFI";
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.v(LOG_TAG,"Send Broadcast for battery changes");
        Intent i = new Intent(ACTION_DATA_UPDATED_WIFI);
        context.sendBroadcast(i);

    }

}
