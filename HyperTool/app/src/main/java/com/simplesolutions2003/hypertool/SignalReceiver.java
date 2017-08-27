package com.simplesolutions2003.hypertool;

import android.content.Context;
import android.telephony.CellInfo;
import android.telephony.PhoneStateListener;

import java.util.List;

/**
 * Created by Suriya on 5/11/2016.
 */
public class SignalReceiver extends PhoneStateListener {
    Context mContext;
    public static String LOG_TAG = "CustomPhoneStateListener";

    public SignalReceiver(Context context) {
        mContext = context;
    }

    @Override
    public void onCellInfoChanged(List<CellInfo> cellInfo) {
        super.onCellInfoChanged(cellInfo);
        MainActivity.updateCarrierInfo();
    }
}
