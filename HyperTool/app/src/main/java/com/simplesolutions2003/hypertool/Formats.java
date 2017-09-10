package com.simplesolutions2003.hypertool;

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

}
