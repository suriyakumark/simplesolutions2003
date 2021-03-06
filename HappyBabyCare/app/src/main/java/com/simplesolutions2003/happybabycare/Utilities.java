package com.simplesolutions2003.happybabycare;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.simplesolutions2003.happybabycare.data.AppContract;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by SuriyaKumar on 9/3/2016.
 */
public class Utilities {

    public final static String TAG = Utilities.class.getSimpleName();

    Context context;
    SimpleDateFormat disp_datetime_fmt = new SimpleDateFormat("MMM dd, hh:mm a");
    SimpleDateFormat disp_date_fmt = new SimpleDateFormat("MMM dd, yyyy");
    SimpleDateFormat disp_time_fmt = new SimpleDateFormat("hh:mm a");
    SimpleDateFormat db_date_fmt = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat db_time_fmt = new SimpleDateFormat("HH:mm");
    SimpleDateFormat db_timestamp_fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public final static int ACTIVE = 1;
    public final static int INACTIVE = 0;

    public final static int LIST_OK = 0;
    public final static int LIST_LOADING = 1;
    public final static int LIST_EMPTY = 2;

    private static final String[] USER_COLUMNS = {
            AppContract.UserEntry.TABLE_NAME + "." + AppContract.UserEntry._ID,
            AppContract.UserEntry.TABLE_NAME + "." + AppContract.UserEntry.COLUMN_USER_ID
    };

    private static final int COL_ID = 0;
    private static final int COL_USER_ID = 1;

    public Utilities(){}

    Utilities(Context context){
        this.context = context;
    }

    public String getCurrentDateDB(){
        return db_date_fmt.format(new Date());
    }

    public String getCurrentDateDisp(){
        return disp_date_fmt.format(new Date());
    }

    public String getCurrentDateTimeDisp(){
        return disp_datetime_fmt.format(new Date());
    }

    public String getCurrentTimestampDB(){
        return db_timestamp_fmt.format(new Date());
    }

    public String convDateDb2Disp(String dateIn ){
        try {
            return disp_date_fmt.format((Date) db_date_fmt.parse(dateIn));
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String convDateDisp2Db(String dateIn ){
        try {
            return db_date_fmt.format((Date) disp_date_fmt.parse(dateIn));
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getCurrentTimeDB(){
        return db_time_fmt.format(new Date().getTime());
    }

    public String getTimeDifferenceDisp(String dateStart, String dateEnd){
        int precision = 0;
        try {
            Date dateTo = (Date) db_time_fmt.parse(dateEnd);
            Date dateFrom = (Date) db_time_fmt.parse(dateStart);
            if(dateFrom.after(dateTo)){
                dateTo = new Date(dateTo.getTime() + (24*60*60*1000));
            }
            long dateDiffSec = Math.abs(dateTo.getTime() - dateFrom.getTime()) / (1000);
            long dateDiffMin = (dateDiffSec/60) % 60;
            long dateDiffHr = (dateDiffSec/(60 * 60)) % 60;
            long dateDiffDays = (dateDiffSec/(60 * 60 * 24));
            String timeDiff = "";
            if(dateDiffDays > 1){
                timeDiff += dateDiffDays + "days ";
                precision++;
            }
            if(dateDiffDays == 1){
                timeDiff += dateDiffDays + "day ";
                precision++;
            }
            if(dateDiffHr > 1 & precision < 3){
                timeDiff += dateDiffHr + "hrs ";
                precision++;
            }
            if(dateDiffHr == 1 & precision < 3){
                timeDiff += dateDiffHr + "hr ";
                precision++;
            }
            if(dateDiffMin > 1 & precision < 3){
                timeDiff += dateDiffMin + "mins ";
                precision++;
            }
            if(dateDiffMin == 1 & precision < 3){
                timeDiff += dateDiffMin + "min ";
                precision++;
            }
            if(timeDiff.isEmpty()){
                timeDiff = "just now";
            }
            return timeDiff;
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getTimeDifferenceMins(String dateStart, String dateEnd){
        int precision = 0;
        try {
            Date dateTo = (Date) db_time_fmt.parse(dateEnd);
            Date dateFrom = (Date) db_time_fmt.parse(dateStart);
            if(dateFrom.after(dateTo)){
                dateTo = new Date(dateTo.getTime() + (24*60*60*1000));
            }
            long dateDiffSec = Math.abs(dateTo.getTime() - dateFrom.getTime()) / (1000);
            long dateDiffMin = dateDiffSec / 60;
            return Long.toString(dateDiffMin);
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String convTimeDb2Disp(String dateIn ){
        try {
            return disp_time_fmt.format((Date) db_time_fmt.parse(dateIn));
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String convTimeDisp2Db(String dateIn ){
        try {
            return db_time_fmt.format((Date) disp_time_fmt.parse(dateIn));
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void resetFocus(View view){
        view.setFocusableInTouchMode(false);
        view.setFocusable(false);
        view.setFocusableInTouchMode(true);
        view.setFocusable(true);
    }

    public boolean isInternetOn(){

        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo i = conMgr.getActiveNetworkInfo();
        if (i == null)
            return false;
        if (!i.isConnected())
            return false;
        if (!i.isAvailable())
            return false;
        return true;
    }

    public Cursor getSavedActiveUser(){
        Uri query_user_uri = AppContract.UserEntry.CONTENT_URI;
        String sSelection = AppContract.UserEntry.TABLE_NAME + "." + AppContract.UserEntry.COLUMN_ACTIVE + " = ? ";
        String[] sSelectionArgs = new String[]{Long.toString(ACTIVE)};
        return context.getContentResolver().query(query_user_uri,USER_COLUMNS,sSelection,sSelectionArgs,null);

    }

    public void enableMenu(MenuItem item, boolean enable){
        item.setEnabled(enable);
        item.setVisible(enable);
        if(enable) {
            item.getIcon().setAlpha(255);
        }else{
            item.getIcon().setAlpha(100);
        }
    }


    public void updateEmptyLoadingGone(int EmptyLoadingGone, TextView tvEmptyLoading, String emptyMessage){
        Log.v(TAG,"EmptyLoadingGone");
        switch(EmptyLoadingGone){
            case LIST_LOADING:
                tvEmptyLoading.setText(context.getString(R.string.text_list_loading));
                tvEmptyLoading.setContentDescription(context.getString(R.string.text_list_loading));
                tvEmptyLoading.setVisibility(View.VISIBLE);
                break;
            case LIST_EMPTY:
                tvEmptyLoading.setText(emptyMessage);
                tvEmptyLoading.setContentDescription(emptyMessage);
                tvEmptyLoading.setVisibility(View.VISIBLE);
                break;
            case LIST_OK:
                tvEmptyLoading.setVisibility(View.GONE);
                break;
            default:
                break;
        }

    }

    public void loadImageView(final ImageView imageView, final String imageUrl){
        Picasso.with(context)
                .load(imageUrl)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .noFade()
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        // Try again online if cache failed
                        Picasso.with(context)
                                .load(imageUrl)
                                .noFade()
                                .into(imageView);
                    }
                });
    }

}
