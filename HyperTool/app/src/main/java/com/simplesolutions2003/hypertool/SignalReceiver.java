package com.simplesolutions2003.hypertool;

import android.content.Context;
import android.content.Intent;
import android.telephony.CellInfo;
import android.telephony.PhoneStateListener;
import android.util.Log;

import java.util.List;

/**
 * Created by Suriya on 5/11/2016.
 */
public class SignalReceiver extends PhoneStateListener {
    Context context;
    public final static String LOG_TAG = SignalReceiver.class.getSimpleName();
    public static final String ACTION_DATA_UPDATED_SIGNAL = "com.simplesolutions2003.hypertool.ACTION_DATA_UPDATED_SIGNAL";


    public SignalReceiver(Context context) {
        context = context;
    }

    @Override
    public void onCellInfoChanged(List<CellInfo> cellInfo) {
        super.onCellInfoChanged(cellInfo);
        Log.v(LOG_TAG,"Send Broadcast for signal changes");
        Intent i = new Intent(ACTION_DATA_UPDATED_SIGNAL);
        context.sendBroadcast(i);
    }

}
