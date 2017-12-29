package com.simplesolutions2003.thirukkuralplus;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by simpl on 27-Dec-17.
 */

public class Utilities {

    public final static String LOG_TAG = Utilities.class.getSimpleName();

    Context context;
    public final static int LIST_OK = 0;
    public final static int LIST_LOADING = 1;
    public final static int LIST_EMPTY = 2;

    public Utilities(){}

    Utilities(Context context){
        this.context = context;
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

    public boolean isItTimeToRemindRate(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Long app_launch_count = pref.getLong(context.getString(R.string.pref_app_launch_count), 0L);
        boolean rate_never = pref.getBoolean(context.getString(R.string.pref_app_rate_never), false);

        if(rate_never){
            return false;
        }

        if((app_launch_count % 5) == 0){
            return true;
        }
        return false;
    }

    public boolean isItTimeToRemindReward(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Long app_launch_count = pref.getLong(context.getString(R.string.pref_app_launch_count), 0L);

        if((app_launch_count % 6) == 0){
            return true;
        }
        return false;
    }

    public void addReward(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        int reward_count = pref.getInt(context.getString(R.string.pref_app_reward_count), 0);
        reward_count++;
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(context.getString(R.string.pref_app_reward_count), reward_count);
        editor.commit();
    }

    public void addVisit(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Long app_launch_count = pref.getLong(context.getString(R.string.pref_app_launch_count), 0L);
        app_launch_count++;
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(context.getString(R.string.pref_app_launch_count), app_launch_count);
        editor.commit();
    }

    public boolean isEnglishEnabled(Context context){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String prefEnglish = pref.getString(context.getString(R.string.pref_english), "On");
        if(prefEnglish.equals("Off")){
            return false;
        }else{
            return true;
        }

    }

    public int getScore(Context context){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        int prefScore = pref.getInt(context.getString(R.string.pref_score), 0);
        return prefScore;
    }

    public void addScore(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        int prefScore = pref.getInt(context.getString(R.string.pref_score), 0);
        prefScore = prefScore + 10;
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(context.getString(R.string.pref_score), prefScore);
        editor.commit();
    }

    public void minusScore(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        int prefScore = pref.getInt(context.getString(R.string.pref_score), 0);
        prefScore = prefScore - 5;
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(context.getString(R.string.pref_score), prefScore);
        editor.commit();
    }

    public int getWidgetColor(Context context){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String prefColor = pref.getString(context.getString(R.string.pref_color), "White");
        if(prefColor.equals("Blue")){
            return ContextCompat.getColor(context,R.color.colorPrimary);
        }else if(prefColor.equals("Red")){
            return Color.RED;
        }else if(prefColor.equals("Black")){
            return Color.BLACK;
        }else if(prefColor.equals("White")){
            return Color.WHITE;
        }else{
            return Color.WHITE;
        }

    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void shareMe(String shareKural){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        if(isEnglishEnabled(context)) {
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareKural +
                    context.getString(R.string.msg_share_app_eng) + " " +
                        "https://play.google.com/store/apps/details?id=" + context.getPackageName());
        } else {
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareKural +
                    context.getString(R.string.msg_share_app) + " " +
                    "https://play.google.com/store/apps/details?id=" + context.getPackageName());
        }
        sendIntent.setType("text/html");
        context.startActivity(sendIntent);
    }

    public void rateMe(){
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + context.getPackageName())));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }

    public void remindRate(){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.rate_dialog);

        TextView tv1 = (TextView) dialog.findViewById(R.id.rate_message);
        Button b1 = (Button) dialog.findViewById(R.id.rate_yes);
        Button b2 = (Button) dialog.findViewById(R.id.rate_later);
        Button b3 = (Button) dialog.findViewById(R.id.rate_never);

        if(isEnglishEnabled(context)) {
            dialog.setTitle(context.getString(R.string.dialog_rate_title_eng));
            tv1.setText(context.getString(R.string.dialog_rate_message_eng));
            b1.setText(context.getString(R.string.dialog_rate_yes_eng));
            b2.setText(context.getString(R.string.dialog_rate_later_eng));
            b3.setText(context.getString(R.string.dialog_rate_never_eng));
        }else{
            dialog.setTitle(context.getString(R.string.dialog_rate_title));
            tv1.setText(context.getString(R.string.dialog_rate_message));
            b1.setText(context.getString(R.string.dialog_rate_yes));
            b2.setText(context.getString(R.string.dialog_rate_later));
            b3.setText(context.getString(R.string.dialog_rate_never));
        }
        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rateMe();
                dialog.dismiss();
            }
        });


        b2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        b3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean(context.getString(R.string.pref_app_rate_never), true);
                editor.commit();
                dialog.dismiss();
            }
        });

        dialog.show();
    }


}
