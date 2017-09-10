package com.simplesolutions2003.hypertool;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by simpl on 9/10/2017.
 */

public class UnitConversions {
    // Converts to celcius
    public static float convertFahrenheitToCelcius(float fahrenheit) {
        return ((fahrenheit - 32) * 5 / 9);
    }

    // Converts to fahrenheit
    public static float convertCelciusToFahrenheit(float celsius) {
        return ((celsius * 9) / 5) + 32;
    }

    // Converts to mph
    public static float convertKphToMph(float Kph) {
        return (Kph / 1.609f);
    }

    // Converts to date
    public static String convertUnixtimeToDateFormat(long unixTime) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(unixTime*1000L); // *1000 is to convert seconds to milliseconds
        return timeFormat.format(date);
    }

    public static float convertHzToMhz(float inHz){
        float outMhz = (float) inHz/1000000.0f;
        return outMhz;
    }

    public static String getWeekDay(int day, Context context){

        switch (day) {
            case Calendar.SUNDAY:
                return context.getString(R.string.weekday_sunday);
            case Calendar.MONDAY:
                return context.getString(R.string.weekday_monday);
            case Calendar.TUESDAY:
                return context.getString(R.string.weekday_tuesday);
            case Calendar.WEDNESDAY:
                return context.getString(R.string.weekday_wednesday);
            case Calendar.THURSDAY:
                return context.getString(R.string.weekday_thursday);
            case Calendar.FRIDAY:
                return context.getString(R.string.weekday_friday);
            case Calendar.SATURDAY:
                return context.getString(R.string.weekday_saturday);
            default:
                return null;
        }
    }
}
