package com.simplesolutions2003.hypertool.sync;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by SuriyaKumar on 9/6/2016.
 */
public class WeatherSyncService extends Service  {
    private final static String TAG = WeatherSyncService.class.getSimpleName();
    private static final Object sSyncAdapterLock = new Object();
    private static WeatherSyncAdapter sWeatherSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        synchronized (sSyncAdapterLock) {
            if (sWeatherSyncAdapter == null) {
                sWeatherSyncAdapter = new WeatherSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sWeatherSyncAdapter.getSyncAdapterBinder();
    }
}
