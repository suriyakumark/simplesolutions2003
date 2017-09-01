package com.simplesolutions2003.hypertool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Suriya on 5/11/2016.
 */
public class AirplaneReceiver extends BroadcastReceiver {
    public final static String LOG_TAG = AirplaneReceiver.class.getSimpleName();
    public static final String ACTION_DATA_UPDATED_AIRPLANE = "com.simplesolutions2003.hypertool.ACTION_DATA_UPDATED_AIRPLANE";
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.v(LOG_TAG,"Send Broadcast for airplane mode changes");
        Intent i = new Intent(ACTION_DATA_UPDATED_AIRPLANE);
        context.sendBroadcast(i);

    }
}
