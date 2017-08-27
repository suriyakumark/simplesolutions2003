package com.simplesolutions2003.happybabycare.sync;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by SuriyaKumar on 9/6/2016.
 */
public class PutActivitiesSyncService extends Service  {
    private final static String TAG = PutActivitiesSyncService.class.getSimpleName();
    private static final Object sSyncAdapterLock = new Object();
    private static PutActivitiesSyncAdapter sPutActivitiesSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        synchronized (sSyncAdapterLock) {
            if (sPutActivitiesSyncAdapter == null) {
                sPutActivitiesSyncAdapter = new PutActivitiesSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sPutActivitiesSyncAdapter.getSyncAdapterBinder();
    }
}
