package com.simplesolutions2003.hypertool;

/**
 * Created by simpl on 8/31/2017.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.util.Log;

import com.simplesolutions2003.hypertool.sync.WeatherSyncAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.prefs.Preferences;

public class MyPreferencesActivity extends PreferenceActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String LOG_TAG = MyPreferencesActivity.class.getSimpleName();
    private static boolean needSync = false;
    private static boolean configureSync = false;
    public static final String LINE_FEED = System.getProperty("line.separator");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG,"OnCreate");
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        ListPreference listPreference = (ListPreference) findPreference(MyPreferencesActivity.this.getString(R.string.pref_key_country));
        if (listPreference != null) {
            ArrayList<String> entries = new ArrayList<>();
            ArrayList<String> entryValues = new ArrayList<>();

            InputStream is = getResources().openRawResource(R.raw.country_list);
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
                is.close();
            }catch (UnsupportedEncodingException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }

            String jsonString = writer.toString();

            try {
                JSONObject json = new JSONObject(jsonString);
                JSONArray jsonArray = json.getJSONArray("countries");
                for(int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject countryObject = (JSONObject) jsonArray.get(i);
                    entryValues.add(countryObject.getString("Code"));
                    entries.add(countryObject.getString("Name"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            listPreference.setEntries(entries.toArray(new String[entries.size()]));
            listPreference.setEntryValues(entryValues.toArray(new String[entryValues.size()]));

        }

        initSummary(getPreferenceScreen());

        final Preference prefZip = (Preference) findPreference("zip");
        prefZip.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                EditTextPreference editPref = (EditTextPreference)preference;
                editPref.getEditText().setSelection( editPref.getText().length() );
                return true;

            }
        });

        Preference prefResetData = (Preference) findPreference("reset_data_wifi_usage");
        prefResetData.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                DataWifiUsage.setContext(MyPreferencesActivity.this);
                DataWifiUsage.resetData();
                DataWifiUsage.resetWifi();
                return false;
            }
        });

        Preference prefResetPedometer = (Preference) findPreference("reset_pedometer");
        prefResetPedometer.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyPreferencesActivity.this);
                Float step_count = prefs.getFloat(MyPreferencesActivity.this.getString(R.string.pref_key_step_count), 0F);

                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MyPreferencesActivity.this).edit();
                editor.putFloat(MyPreferencesActivity.this.getString(R.string.pref_key_initial_step_count), step_count);
                editor.commit();
                return false;
            }
        });

        Preference prefShareLoc = (Preference) findPreference("share_loc");
        prefShareLoc.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyPreferencesActivity.this);
                String sLongitude = prefs.getString(MyPreferencesActivity.this.getString(R.string.pref_key_longitude), "");
                String sLatitude = prefs.getString(MyPreferencesActivity.this.getString(R.string.pref_key_latitude), "");
                if(!sLongitude.isEmpty() && !sLongitude.isEmpty()) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT,
                            MyPreferencesActivity.this.getString(R.string.label_coordinates) + " : " + LINE_FEED
                                    + MyPreferencesActivity.this.getString(R.string.label_longitude) + sLongitude + LINE_FEED
                                    + MyPreferencesActivity.this.getString(R.string.label_latitude) + sLatitude);
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                }
                return false;
            }
        });

        Preference prefShareApp = (Preference) findPreference("share_app");
        prefShareApp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        MyPreferencesActivity.this.getString(R.string.msg_share_app) +
                                "https://play.google.com/store/apps/details?id=" + getPackageName());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                return false;
            }
        });

        Preference prefRate = (Preference) findPreference("rate_us");
        prefRate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                }
                return false;
            }
        });

    }

    protected void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        needSync = false;
        configureSync = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
        if(configureSync) {
            Log.v(LOG_TAG,"configureSync");
            WeatherSyncAdapter.configurePeriodicSync(this);
        }
        if(needSync) {
            Log.v(LOG_TAG,"needSync");
            WeatherSyncAdapter.syncImmediately(this);
        }
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        updatePrefSummary(findPreference(key));
        if (key.equals(MyPreferencesActivity.this.getString(R.string.pref_key_country)) ||
                key.equals(MyPreferencesActivity.this.getString(R.string.pref_key_zip)) ||
                key.equals(MyPreferencesActivity.this.getString(R.string.pref_key_sync_interval))){
            needSync = true;
        }
        if (key.equals(MyPreferencesActivity.this.getString(R.string.pref_key_sync_interval))){
            configureSync = true;
        }
    }

    private void initSummary(Preference p) {
        if (p instanceof PreferenceGroup) {
            PreferenceGroup pGrp = (PreferenceGroup) p;
            for (int i = 0; i < pGrp.getPreferenceCount(); i++) {
                initSummary(pGrp.getPreference(i));
            }
        } else {
            updatePrefSummary(p);
        }
    }

    private void updatePrefSummary(Preference p) {
        if (p instanceof ListPreference) {
            ListPreference listPref = (ListPreference) p;
            p.setSummary(listPref.getEntry());
        }
        if (p instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) p;
            if (p.getTitle().toString().toLowerCase().contains("password"))
            {
                p.setSummary("******");
            } else {
                p.setSummary(editTextPref.getText());
            }
        }
        if (p instanceof MultiSelectListPreference) {
            EditTextPreference editTextPref = (EditTextPreference) p;
            p.setSummary(editTextPref.getText());
        }
    }

}