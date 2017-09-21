package com.simplesolutions2003.hypertool;

import android.content.Context;

import java.text.DecimalFormat;

/**
 * Created by simpl on 9/10/2017.
 */

public class Formats {

    public static String onePointTwoDoubleForm(double d){
        return new DecimalFormat("#.##").format(d);
    }

    public static String oneDigitDoubleForm(double d){
        return new DecimalFormat("#.#").format(d);
    }

    public static String oneDigitIntForm(double d){
        return new DecimalFormat("#").format(d);
    }

    public static String oneDigitIntForm(int i){
        return new DecimalFormat("#").format(i);
    }

    public static String twoDigitIntForm(int i){
        return new DecimalFormat("##").format(i);
    }

    public static String bytesFormat(long size){
        long Kb = 1 * 1024;
        long Mb = Kb * 1024;
        long Gb = Mb * 1024;

        if(size < Kb){
            return onePointTwoDoubleForm(size) + " B";
        }else if(size >= Kb/10 && size < Mb/10){
            return onePointTwoDoubleForm((double)size/Kb) + " KB";
        }else if(size >= Mb/10 && size < Gb/10){
            return onePointTwoDoubleForm((double)size/Mb) + " MB";
        }else if(size >= Gb/10){
            return onePointTwoDoubleForm((double)size/Gb) + " GB";
        }

        return null;
    }

    public static String directionFormat(Context context, Float azimuthInDegrees){
        if (azimuthInDegrees < 0.0f) {
            azimuthInDegrees += 360.0f;
        }
        if(azimuthInDegrees == 0){
            return String.format("%.1f", azimuthInDegrees) + context.getString(R.string.unit_north);
        }else if(azimuthInDegrees > 0 && azimuthInDegrees < 90 ){
            return String.format("%.1f", azimuthInDegrees) + context.getString(R.string.unit_northeast);
        }else if(azimuthInDegrees == 90 ){
            return String.format("%.1f", azimuthInDegrees) + context.getString(R.string.unit_east);
        }else if(azimuthInDegrees > 90 && azimuthInDegrees < 180 ){
            return String.format("%.1f", 180.0f - azimuthInDegrees) + context.getString(R.string.unit_southeast);
        }else if(azimuthInDegrees == 180 ){
            return String.format("%.1f", azimuthInDegrees) + context.getString(R.string.unit_south);
        }else if(azimuthInDegrees > 180 && azimuthInDegrees < 270 ){
            return String.format("%.1f", azimuthInDegrees - 180.0f) + context.getString(R.string.unit_southwest);
        }else if(azimuthInDegrees == 270 ){
            return String.format("%.1f", azimuthInDegrees) + context.getString(R.string.unit_west);
        }else if(azimuthInDegrees > 270 && azimuthInDegrees < 360 ){
            return String.format("%.1f", 360.0f - azimuthInDegrees) + context.getString(R.string.unit_northwest);
        }else{
            return null;
        }

    }
}
