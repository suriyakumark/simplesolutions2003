package com.simplesolutions2003.hypertool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Suriya on 5/11/2016.
 */
public class AirplaneReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        MainActivity.updateAirplaneInfo();
    }
}
