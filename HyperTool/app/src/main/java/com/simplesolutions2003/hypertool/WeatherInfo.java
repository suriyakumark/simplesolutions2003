package com.simplesolutions2003.hypertool;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by simpl on 9/10/2017.
 */

public class WeatherInfo {

    private final static String LOG_TAG = WeatherInfo.class.getSimpleName();

    public static Context context;

    public static String sWeatherTempNowC;
    public static String sWeatherTempNowF;
    public static String sWeatherTempHiC;
    public static String sWeatherTempHiF;
    public static String sWeatherTempLoC;
    public static String sWeatherTempLoF;
    public static String sWeatherWindKmph;
    public static String sWeatherWindMph;
    public static String sWeatherForecast;
    public static String sWeatherCity;
    public static String sWeatherWindDir;
    public static String sWeatherIcon;
    public static String sSunRiseTime;
    public static String sSunSetTime;

    public static void setContext(Context c){
        context = c;
    }

    public static void getWeatherInfo() {
        Log.v(LOG_TAG,"getWeatherInfo");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        sWeatherCity = prefs.getString(context.getString(R.string.pref_key_weatherCity), "");
        sWeatherForecast = prefs.getString(context.getString(R.string.pref_key_weatherForecast), "");
        sWeatherIcon = prefs.getString(context.getString(R.string.pref_key_weatherIcon), "");
        sWeatherTempNowC = prefs.getString(context.getString(R.string.pref_key_weatherTemp), "");
        sWeatherTempLoC = prefs.getString(context.getString(R.string.pref_key_weatherTempMin), "");
        sWeatherTempHiC = prefs.getString(context.getString(R.string.pref_key_weatherTempMax), "");
        sWeatherWindKmph = prefs.getString(context.getString(R.string.pref_key_weatherWindSpeed), "");
        sWeatherWindDir = prefs.getString(context.getString(R.string.pref_key_weatherWindDir), "");
        sSunRiseTime = prefs.getString(context.getString(R.string.pref_key_weatherSunrise), "");
        sSunSetTime = prefs.getString(context.getString(R.string.pref_key_weatherSunset), "");

        Log.v(LOG_TAG,"getWeatherInfo - sWeatherCity" + sWeatherCity);

        if(!sWeatherTempNowC.isEmpty()) {
            sWeatherTempNowC = Formats.oneDigitDoubleForm(Float.parseFloat(sWeatherTempNowC));
            sWeatherTempNowF = Formats.oneDigitDoubleForm(UnitConversions.convertCelciusToFahrenheit(Float.parseFloat(sWeatherTempNowC)));
        }

        if(!sWeatherTempLoC.isEmpty()) {
            sWeatherTempLoC = Formats.oneDigitDoubleForm(Float.parseFloat(sWeatherTempLoC));
            sWeatherTempLoF = Formats.oneDigitDoubleForm(UnitConversions.convertCelciusToFahrenheit(Float.parseFloat(sWeatherTempLoC)));
        }

        if(!sWeatherTempHiC.isEmpty()) {
            sWeatherTempHiC = Formats.oneDigitDoubleForm(Float.parseFloat(sWeatherTempHiC));
            sWeatherTempHiF = Formats.oneDigitDoubleForm(UnitConversions.convertCelciusToFahrenheit(Float.parseFloat(sWeatherTempHiC)));
        }

        if(!sWeatherWindKmph.isEmpty()) {
            sWeatherWindMph = Formats.onePointTwoDoubleForm(UnitConversions.convertKToM(Float.parseFloat(sWeatherWindKmph)));
        }

        if(!sWeatherWindDir.isEmpty()) {
            sWeatherWindDir = Formats.directionFormat(context, Float.parseFloat(sWeatherWindDir));
        }

        if(!sSunRiseTime.isEmpty()) {
            sSunRiseTime = UnitConversions.convertUnixtimeToDateFormat(Long.parseLong(sSunRiseTime));
        }

        if(!sSunSetTime.isEmpty()) {
            sSunSetTime = UnitConversions.convertUnixtimeToDateFormat(Long.parseLong(sSunSetTime));
        }

    }
}
