package com.simplesolutions2003.hypertool;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;

/**
 * Created by Suriya on 5/11/2016.
 */
public class SettingsContentObserver extends ContentObserver {
    public final static String LOG_TAG = SettingsContentObserver.class.getSimpleName();
    public static final String ACTION_DATA_UPDATED_SETTINGS = "com.simplesolutions2003.hypertool.ACTION_DATA_UPDATED_SETTINGS";

    private Context context;

    public SettingsContentObserver(Handler handler, Context context) {
        super(handler);
        this.context = context;
    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Log.v(LOG_TAG,"Send Broadcast for settings changes");
        Intent i = new Intent(ACTION_DATA_UPDATED_SETTINGS);
        context.sendBroadcast(i);

    }

}
