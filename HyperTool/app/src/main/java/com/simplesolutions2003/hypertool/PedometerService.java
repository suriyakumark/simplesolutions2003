package com.simplesolutions2003.hypertool;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Suriya on 5/4/2016.
 */
public class PedometerService extends Service implements SensorEventListener{

    public final static String LOG_TAG = PedometerService.class.getSimpleName();
    public static final String ACTION_DATA_UPDATED_PEDOMETER = "com.simplesolutions2003.hypertool.ACTION_DATA_UPDATED_PEDOMETER";
    private SensorManager sensorManager;

    public PedometerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        Sensor countSensor = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            if (countSensor != null) {
                sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_NORMAL );
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch(event.sensor.getType()){
            case Sensor.TYPE_STEP_COUNTER:

                PedometerInfo.setContext(this);
                PedometerInfo.setStepCount(event.values[0]);

                //Log.v(LOG_TAG,"Send Broadcast for step changes");
                Intent i = new Intent(ACTION_DATA_UPDATED_PEDOMETER);
                this.sendBroadcast(i);
                break;
            default:
                break;
        }
    }
}


