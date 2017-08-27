package com.simplesolutions2003.hypertool;

import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;

/**
 * Created by Suriya on 5/11/2016.
 */
public class SettingsContentObserver extends ContentObserver {

    public SettingsContentObserver(Handler handler) {
        super(handler);
    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        MainActivity.updateVolumeInfo();
        MainActivity.updateBluetoothInfo();
        MainActivity.updateBrightnessInfo();
    }
}
