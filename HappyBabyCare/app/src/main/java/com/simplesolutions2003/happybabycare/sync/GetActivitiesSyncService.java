package com.simplesolutions2003.happybabycare.sync;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by SuriyaKumar on 9/6/2016.
 */
public class GetActivitiesSyncService extends Service  {
    private final static String TAG = GetActivitiesSyncService.class.getSimpleName();
    private static final Object sSyncAdapterLock = new Object();
    private static GetActivitiesSyncAdapter sGetActivitiesSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        synchronized (sSyncAdapterLock) {
            if (sGetActivitiesSyncAdapter == null) {
                sGetActivitiesSyncAdapter = new GetActivitiesSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sGetActivitiesSyncAdapter.getSyncAdapterBinder();
    }
}
