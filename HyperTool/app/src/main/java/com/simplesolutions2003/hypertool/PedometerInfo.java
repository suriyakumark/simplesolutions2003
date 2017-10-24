package com.simplesolutions2003.hypertool;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by simpl on 10/24/2017.
 */

public class PedometerInfo {
    private final static String LOG_TAG = PedometerInfo.class.getSimpleName();

    public static Context context;
    public static String sPedometerSteps;
    public static String sPedometerCalories;
    public static String sPedometerDistance;


    public static void setContext(Context c){
        context = c;
    }

    public static void getStepCount(){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Float step_count = prefs.getFloat(context.getString(R.string.pref_key_step_count), 0F);

        float height = Float.parseFloat(prefs.getString(context.getString(R.string.pref_key_pedometer_height), "150.0"));
        float weight = Float.parseFloat(prefs.getString(context.getString(R.string.pref_key_pedometer_weight), "65.0"));
        String units = prefs.getString(context.getString(R.string.pref_key_pedometer_units), "Metric");

        sPedometerSteps = Formats.oneDigitIntForm(step_count);
        if(units.equals("Imperial")) {
            height = height * 2.54F;
            weight = weight * 0.453592F;
        }

        sPedometerCalories = Utilities.CalorieBurnedCalculator(step_count, height, weight) + " cal";

        if(units.equals("Imperial")){
            sPedometerDistance = Utilities.DistanceMiCalculator(step_count,height)+ " Mi";
        }else {
            sPedometerDistance = Utilities.DistanceKmCalculator(step_count, height) + " Km";
        }

    }

    public static void setStepCount(Float curr_step_count){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Float step_count = prefs.getFloat(context.getString(R.string.pref_key_step_count), 0F);
        Float prev_step_count = prefs.getFloat(context.getString(R.string.pref_key_prev_step_count), 0F);
        Log.v(LOG_TAG,"setStepCount step_count - " + step_count);
        Log.v(LOG_TAG,"setStepCount prev_step_count - " + prev_step_count);

        if(step_count == 0 && prev_step_count == 0){
            initializeStepCount(curr_step_count);
            prev_step_count = prefs.getFloat(context.getString(R.string.pref_key_prev_step_count), 0F);
        }

        if(curr_step_count >= prev_step_count) {
            step_count = step_count + curr_step_count - prev_step_count;
        }else{
            step_count = step_count + curr_step_count;
        }
        Log.v(LOG_TAG,"setStepCount step_count new - " + step_count);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putFloat(context.getString(R.string.pref_key_step_count), step_count);
        editor.putFloat(context.getString(R.string.pref_key_prev_step_count), curr_step_count);
        editor.commit();

    }

    public static void resetStepCount(){
        Log.v(LOG_TAG,"resetStepCount");
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putFloat(context.getString(R.string.pref_key_step_count), 0F);
        editor.commit();
    }

    public static void initializeStepCount(Float curr_step_count){
        Log.v(LOG_TAG,"initializeStepCount");
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putFloat(context.getString(R.string.pref_key_prev_step_count), curr_step_count);
        editor.commit();
    }
}
