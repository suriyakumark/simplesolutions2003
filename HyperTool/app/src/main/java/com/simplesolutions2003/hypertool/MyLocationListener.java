package com.simplesolutions2003.hypertool;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by simpl on 9/4/2017.
 */

public class MyLocationListener implements LocationListener {
    public final static String LOG_TAG = MyLocationListener.class.getSimpleName();
    public static final String ACTION_DATA_UPDATED_LOCATION = "com.simplesolutions2003.hypertool.ACTION_DATA_UPDATED_LOCATION";
    public static final String ACTION_DATA_UPDATED_GPS = "com.simplesolutions2003.hypertool.ACTION_DATA_UPDATED_GPS";

    private Context context;

    public MyLocationListener(Context context){
        this.context = context;
    }

    @Override
    public void onLocationChanged(Location loc) {

        String longitude = Double.toString(loc.getLongitude());
        String latitude = Double.toString(loc.getLatitude());

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(context.getString(R.string.pref_key_longitude), longitude);
        editor.putString(context.getString(R.string.pref_key_latitude), latitude);
        editor.commit();

        Log.v(LOG_TAG,"Send Broadcast for location changes");
        Intent i = new Intent(ACTION_DATA_UPDATED_LOCATION);
        context.sendBroadcast(i);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.v(LOG_TAG,"Send Broadcast for gps changes");
        Intent i = new Intent(ACTION_DATA_UPDATED_GPS);
        context.sendBroadcast(i);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.v(LOG_TAG,"Send Broadcast for gps changes");
        Intent i = new Intent(ACTION_DATA_UPDATED_GPS);
        context.sendBroadcast(i);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
