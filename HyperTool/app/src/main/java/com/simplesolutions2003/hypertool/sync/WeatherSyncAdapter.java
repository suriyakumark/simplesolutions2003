package com.simplesolutions2003.hypertool.sync;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.simplesolutions2003.hypertool.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by SuriyaKumar on 9/6/2016.
 */
public class WeatherSyncAdapter extends AbstractThreadedSyncAdapter {
    public final static String LOG_TAG = WeatherSyncAdapter.class.getSimpleName();
    public static final String ACTION_DATA_UPDATED_WEATHER = "com.simplesolutions2003.hypertool.ACTION_DATA_UPDATED_WEATHER";

    // 60 seconds (1 minute) * 5 = 5 min
    public static int SYNC_INTERVAL = 5;

    private Context context;

    public WeatherSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.context = context;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String weatherJsonStr = null;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String query_name = prefs.getString(context.getString(R.string.pref_key_zip), "");
        String query_country = prefs.getString(context.getString(R.string.pref_key_country), "");
        Log.v(LOG_TAG,"query_name - " + query_name);
        Log.v(LOG_TAG,"query_country - " + query_country);

        try {
            final String FORECAST_BASE_URL =
                    "http://api.openweathermap.org/data/2.5/weather?";
            final String QUERY_PARAM = "q";
            final String MODE_PARAM = "mode";
            final String APPID_PARAM = "APPID";
            final String UNIT_PARAM = "units";
            String app_id = context.getString(R.string.appId);

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, query_name + "," + query_country)
                    .appendQueryParameter(MODE_PARAM,"json")
                    .appendQueryParameter(APPID_PARAM,app_id)
                    .appendQueryParameter(UNIT_PARAM,"metric")
                    .build();

            Log.v(LOG_TAG, "builtUri - " + builtUri.toString());

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                Log.v(LOG_TAG, "Empty response");
                return;
            }
            weatherJsonStr = buffer.toString();
            getWeatherDataFromJson(weatherJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        Log.v(LOG_TAG,"Send Broadcast for weather sync complete");
        Intent i = new Intent(ACTION_DATA_UPDATED_WEATHER);
        context.sendBroadcast(i);

        return;
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private void getWeatherDataFromJson(String weatherJsonStr)
            throws JSONException {
        Log.v(LOG_TAG, "getWeatherDataFromJson");
        // into an Object hierarchy for us.
        // These are the names of the JSON objects that need to be extracted.

        // Weather information
        final String OWM_WEATHER = "weather";
        final String OWM_WEATHER_MAIN = "main";
        final String OWM_WEATHER_ICON = "icon";

        final String OWM_MAIN = "main";
        final String OWM_WEATHER_TEMP = "temp";
        final String OWM_WEATHER_TEMP_MIN = "temp_min";
        final String OWM_WEATHER_TEMP_MAX = "temp_max";

        final String OWM_WIND = "wind";
        final String OWM_WEATHER_WIND_SPEED = "speed";
        final String OWM_WEATHER_WIND_DIR = "deg";

        final String OWM_SUN = "sys";
        final String OWM_WEATHER_SUNRISE = "sunrise";
        final String OWM_WEATHER_SUNSET = "sunset";

        final String OWM_CITY = "name";

        final String OWM_MESSAGE_CODE = "cod";

        try {
            JSONObject weatherJson = new JSONObject(weatherJsonStr);

            // do we have an error?
            if ( weatherJson.has(OWM_MESSAGE_CODE) ) {
                int errorCode = weatherJson.getInt(OWM_MESSAGE_CODE);

                switch (errorCode) {
                    case 200:
                        break;
                    default:
                        Log.v(LOG_TAG, "getWeatherDataFromJson - unknown error");
                        return;
                }
            }

            JSONArray weatherArray = weatherJson.getJSONArray(OWM_WEATHER);
            JSONObject weatherObj = weatherArray.getJSONObject(0);
            JSONObject tempObj = weatherJson.getJSONObject(OWM_MAIN);
            JSONObject windObj = weatherJson.getJSONObject(OWM_WIND);
            JSONObject sunObj = weatherJson.getJSONObject(OWM_SUN);

            String weatherCity = weatherJson.getString(OWM_CITY);
            String weatherMain = weatherObj.getString(OWM_WEATHER_MAIN);
            String weatherIcon = weatherObj.getString(OWM_WEATHER_ICON);
            String weatherTemp = tempObj.getString(OWM_WEATHER_TEMP);
            String weatherTempMin = tempObj.getString(OWM_WEATHER_TEMP_MIN);
            String weatherTempMax = tempObj.getString(OWM_WEATHER_TEMP_MAX);
            String weatherWindSpeed = windObj.getString(OWM_WEATHER_WIND_SPEED);
            String weatherWindDir = windObj.getString(OWM_WEATHER_WIND_DIR);
            String weatherSunrise = sunObj.getString(OWM_WEATHER_SUNRISE);
            String weatherSunset = sunObj.getString(OWM_WEATHER_SUNSET);

            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            editor.putString(context.getString(R.string.pref_key_weatherCity), weatherCity);
            editor.putString(context.getString(R.string.pref_key_weatherForecast), weatherMain);
            editor.putString(context.getString(R.string.pref_key_weatherIcon), weatherIcon);
            editor.putString(context.getString(R.string.pref_key_weatherTemp), weatherTemp);
            editor.putString(context.getString(R.string.pref_key_weatherTempMin), weatherTempMin);
            editor.putString(context.getString(R.string.pref_key_weatherTempMax), weatherTempMax);
            editor.putString(context.getString(R.string.pref_key_weatherWindSpeed), weatherWindSpeed);
            editor.putString(context.getString(R.string.pref_key_weatherWindDir), weatherWindDir);
            editor.putString(context.getString(R.string.pref_key_weatherSunrise), weatherSunrise);
            editor.putString(context.getString(R.string.pref_key_weatherSunset), weatherSunset);
            editor.commit();

            Log.v(LOG_TAG,"getWeatherDataFromJson - weatherCity" + weatherCity);
            Log.d(LOG_TAG, "Sync Complete. ");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);

            e.printStackTrace();
        }
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context) {
        Log.v(LOG_TAG,"configurePeriodicSync");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String sSyncInterval = prefs.getString(context.getString(R.string.pref_key_sync_interval), Integer.toString(SYNC_INTERVAL));
        Log.v(LOG_TAG,"sSyncInterval - " + sSyncInterval);
        int syncInterval = 60 * Integer.parseInt(sSyncInterval);
        int flexTime = syncInterval/3;

        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        ContentResolver.cancelSync(account,authority);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Log.v(LOG_TAG,"syncImmediately");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        Log.v(LOG_TAG, "getSyncAccount");
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));


        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }


    private static void onAccountCreated(Account newAccount, Context context) {
        Log.v(LOG_TAG, "onAccountCreated");
        /*
         * Since we've created an account
         */
        WeatherSyncAdapter.configurePeriodicSync(context);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }


}
