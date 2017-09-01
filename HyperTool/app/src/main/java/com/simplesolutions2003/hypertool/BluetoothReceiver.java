package com.simplesolutions2003.hypertool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Suriya on 5/7/2016.
 */
public class BluetoothReceiver extends BroadcastReceiver {
    public final static String LOG_TAG = BluetoothReceiver.class.getSimpleName();
    public static final String ACTION_DATA_UPDATED_BLUETOOTH = "com.simplesolutions2003.hypertool.ACTION_DATA_UPDATED_BLUETOOTH";
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.v(LOG_TAG,"Send Broadcast for bluetooth changes");
        Intent i = new Intent(ACTION_DATA_UPDATED_BLUETOOTH);
        context.sendBroadcast(i);

    }

}
