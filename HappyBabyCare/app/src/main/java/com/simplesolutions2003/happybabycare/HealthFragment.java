package com.simplesolutions2003.happybabycare;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.Spinner;
import android.widget.Toast;

import com.simplesolutions2003.happybabycare.data.AppContract;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by SuriyaKumar on 8/20/2016.
 */
public class HealthFragment extends Fragment {
    public final static boolean KEEP_IN_STACK = false;
    public final static String TAG = HealthFragment.class.getSimpleName();
    public static final String TITLE_HEALTH = "Health";
    public static long HEALTH_ID = -1;

    public static final String HEALTH_TYPE_TEMP = "Temperature";
    public static final String HEALTH_TYPE_HEIGHT = "Height";
    public static final String HEALTH_TYPE_WEIGHT = "Weight";
    public static final String HEALTH_TYPE_HEAD = "Head Circumference";
    boolean healthTypeWithUnit = false;
    boolean runSetupUnit = true;

    String activityTimestamp = null;

    private static final String[] HEALTH_COLUMNS = {
            AppContract.HealthEntry.TABLE_NAME + "." + AppContract.HealthEntry._ID,
            AppContract.HealthEntry.TABLE_NAME + "." + AppContract.HealthEntry.COLUMN_USER_ID,
            AppContract.HealthEntry.TABLE_NAME + "." + AppContract.HealthEntry.COLUMN_BABY_ID,
            AppContract.HealthEntry.TABLE_NAME + "." + AppContract.HealthEntry.COLUMN_TIMESTAMP,
            AppContract.HealthEntry.TABLE_NAME + "." + AppContract.HealthEntry.COLUMN_DATE,
            AppContract.HealthEntry.TABLE_NAME + "." + AppContract.HealthEntry.COLUMN_TIME,
            AppContract.HealthEntry.TABLE_NAME + "." + AppContract.HealthEntry.COLUMN_TYPE,
            AppContract.HealthEntry.TABLE_NAME + "." + AppContract.HealthEntry.COLUMN_VALUE,
            AppContract.HealthEntry.TABLE_NAME + "." + AppContract.HealthEntry.COLUMN_UNIT,
            AppContract.HealthEntry.TABLE_NAME + "." + AppContract.HealthEntry.COLUMN_NOTES
    };


    static final int COL_HEALTH_ID = 0;
    static final int COL_HEALTH_USER_ID = 1;
    static final int COL_HEALTH_BABY_ID = 2;
    static final int COL_HEALTH_TIMESTAMP = 3;
    static final int COL_HEALTH_DATE = 4;
    static final int COL_HEALTH_TIME = 5;
    static final int COL_HEALTH_TYPE = 6;
    static final int COL_HEALTH_VALUE = 7;
    static final int COL_HEALTH_UNIT = 8;
    static final int COL_HEALTH_NOTES = 9;

    EditText activityDate;
    EditText activityTime;
    EditText activityNotes;

    Spinner healthType;
    Spinner healthUnit;
    EditText healthValue;
    EditText healthSummary;

    public HealthFragment(){}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.health, container, false);
        activityDate = (EditText) rootView.findViewById(R.id.activity_date);
        activityTime = (EditText) rootView.findViewById(R.id.activity_time);
        activityNotes = (EditText) rootView.findViewById(R.id.activity_notes);

        healthType = (Spinner) rootView.findViewById(R.id.health_title);
        healthUnit = (Spinner) rootView.findViewById(R.id.health_unit);
        healthValue = (EditText) rootView.findViewById(R.id.health_value);
        healthSummary = (EditText) rootView.findViewById(R.id.health_summary);

        activityDate.setInputType(InputType.TYPE_NULL);
        activityTime.setInputType(InputType.TYPE_NULL);
        activityDate.setText(new Utilities(getActivity()).getCurrentDateDisp());
        activityTime.setText(new Utilities(getActivity()).getCurrentTimeDB());

        SetDateEditText setActivityDate = new SetDateEditText(activityDate, getActivity());
        SetTimeEditText setActivityTime = new SetTimeEditText(activityTime, getActivity());

        healthType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.v(TAG,"on item selected - " + i);
                if(runSetupUnit){
                    setupUnit();
                }
                runSetupUnit = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        healthValue.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateMenuVisibility();
            }
        });

        healthSummary.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateMenuVisibility();
            }
        });

        activityDate.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                new Utilities(getActivity()).resetFocus(activityDate);
                updateMenuVisibility();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });


        activityTime.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                new Utilities(getActivity()).resetFocus(activityTime);
                updateMenuVisibility();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        if(HEALTH_ID != -1) {
            Uri uri = AppContract.HealthEntry.buildHealthUri(HEALTH_ID);
            Cursor activityEntry = getActivity().getContentResolver().query(uri,HEALTH_COLUMNS,null,null,null);
            if(activityEntry != null){
                if(activityEntry.getCount() > 0){
                    Log.v(TAG,"got health entry");
                    activityEntry.moveToFirst();
                    activityDate.setText(new Utilities(getActivity()).convDateDb2Disp(activityEntry.getString(COL_HEALTH_DATE)));
                    activityTime.setText(activityEntry.getString(COL_HEALTH_TIME));
                    activityNotes.setText(activityEntry.getString(COL_HEALTH_NOTES));
                    for(int iType = 0; iType < healthType.getCount(); iType++){
                        if(activityEntry.getString(COL_HEALTH_TYPE).equals(healthType.getItemAtPosition(iType).toString())){
                            runSetupUnit = false;
                            healthType.setSelection(iType);
                            healthType.setContentDescription(activityEntry.getString(COL_HEALTH_TYPE));
                            setupUnit();
                            break;
                        }
                    }

                    if(!activityEntry.getString(COL_HEALTH_UNIT).toString().isEmpty()) {
                        for (int iType = 0; iType < healthUnit.getCount(); iType++) {
                            if (activityEntry.getString(COL_HEALTH_UNIT).equals(healthUnit.getItemAtPosition(iType).toString())) {
                                Log.v(TAG,"unit found:" + activityEntry.getString(COL_HEALTH_UNIT) + ":" + healthUnit.getItemAtPosition(iType).toString() );

                                healthUnit.setSelection(iType);
                                healthUnit.setContentDescription(activityEntry.getString(COL_HEALTH_UNIT));
                                break;
                            }
                        }

                        healthValue.setText(activityEntry.getString(COL_HEALTH_VALUE));
                        healthSummary.setText("");
                    }else {
                        healthValue.setText("");
                        healthSummary.setText(activityEntry.getString(COL_HEALTH_VALUE));
                    }
                    activityTimestamp = activityEntry.getString(COL_HEALTH_TIMESTAMP);

                    activityDate.setContentDescription(activityDate.getText().toString());
                    activityTime.setContentDescription(activityTime.getText().toString());
                    activityNotes.setContentDescription(activityNotes.getText().toString());
                    healthValue.setContentDescription(healthValue.getText().toString());
                    healthSummary.setContentDescription(healthSummary.getText().toString());
                }
            }else{
                HEALTH_ID = -1;
            }
        }
        Log.v(TAG,"health unit - " + healthUnit.getSelectedItem().toString());
        return rootView;
    }

    public void setupUnit(){
        Log.v(TAG, "called setup unit");
        final String[] spinnerTempArray = getResources().getStringArray(R.array.spinner_health_unit_temp);
        final String[] spinnerHeightArray = getResources().getStringArray(R.array.spinner_health_unit_height);
        final String[] spinnerWeightArray = getResources().getStringArray(R.array.spinner_health_unit_weight);
        final String[] spinnerHeadArray = getResources().getStringArray(R.array.spinner_health_unit_head);

        healthTypeWithUnit = true;
        ArrayAdapter<String> spinnerArrayAdapter = null;
        if (healthType.getSelectedItem().toString().equals(HEALTH_TYPE_TEMP)) {
            spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerTempArray);
            healthValue.setHint(getString(R.string.text_health_value));
        } else if (healthType.getSelectedItem().toString().equals(HEALTH_TYPE_HEIGHT)) {
            spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerHeightArray);
            healthValue.setHint(getString(R.string.text_health_value));
        } else if (healthType.getSelectedItem().toString().equals(HEALTH_TYPE_WEIGHT)) {
            spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerWeightArray);
            healthValue.setHint(getString(R.string.text_health_value));
        } else if (healthType.getSelectedItem().toString().equals(HEALTH_TYPE_HEAD)) {
            spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerHeadArray);
            healthValue.setHint(getString(R.string.text_health_value));
        } else {
            healthTypeWithUnit = false;
        }
        if (!healthTypeWithUnit) {
            healthUnit.setVisibility(View.GONE);
            healthValue.setVisibility(View.GONE);
            healthSummary.setVisibility(View.VISIBLE);
        } else {
            healthUnit.setVisibility(View.VISIBLE);
            healthValue.setVisibility(View.VISIBLE);
            healthSummary.setVisibility(View.GONE);
            spinnerArrayAdapter.setDropDownViewResource(R.layout.dropdown_item);
            healthUnit.setAdapter(spinnerArrayAdapter);
        }
    }


    @Override
    public void onResume()
    {
        super.onResume();
        ((MainActivity) getActivity()).updateToolbarTitle(TITLE_HEALTH + " - " + MainActivity.ACTIVE_BABY_NAME);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        validateInputs();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu){
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_save:
                actionSave();
                return true;
            case R.id.action_delete:
                if(HEALTH_ID != -1) {
                    actionDelete();
                }
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void validateInputs() {
        if(activityDate.getText().toString().isEmpty() |
                activityTime.getText().toString().isEmpty() |
                healthType.getSelectedItem().toString().isEmpty() |
                (healthValue.getText().toString().isEmpty() &
                        healthSummary.getText().toString().isEmpty())){
            MainActivity.saveMenuEnabled = false;
        }else{
            MainActivity.saveMenuEnabled = true;
        }

        if (HEALTH_ID == -1) {
            MainActivity.deleteMenuEnabled = false;
        } else {
            MainActivity.deleteMenuEnabled = true;
        }
    }

    public void updateMenuVisibility(){
        validateInputs();
        ActivityCompat.invalidateOptionsMenu(getActivity());
    }


    public void actionSave(){
        Log.v(TAG, "actionSave");
        Uri uri = AppContract.HealthEntry.CONTENT_URI;
        String currentTimestamp = new Utilities().getCurrentTimestampDB();
        if(activityTimestamp == null){
            activityTimestamp = currentTimestamp;
        }

        ContentValues newValues = new ContentValues();
        newValues.put(AppContract.HealthEntry.COLUMN_USER_ID, MainActivity.LOGGED_IN_USER_ID);
        newValues.put(AppContract.HealthEntry.COLUMN_BABY_ID, MainActivity.ACTIVE_BABY_ID);
        newValues.put(AppContract.HealthEntry.COLUMN_TIMESTAMP, activityTimestamp);
        newValues.put(AppContract.HealthEntry.COLUMN_DATE, new Utilities(getActivity()).convDateDisp2Db(activityDate.getText().toString()));
        newValues.put(AppContract.HealthEntry.COLUMN_TIME, activityTime.getText().toString());
        newValues.put(AppContract.HealthEntry.COLUMN_TYPE, healthType.getSelectedItem().toString());

        if(healthTypeWithUnit) {
            newValues.put(AppContract.HealthEntry.COLUMN_VALUE, healthValue.getText().toString());
            newValues.put(AppContract.HealthEntry.COLUMN_UNIT, healthUnit.getSelectedItem().toString());
        }else {
            newValues.put(AppContract.HealthEntry.COLUMN_VALUE, healthSummary.getText().toString());
            newValues.put(AppContract.HealthEntry.COLUMN_UNIT, "");
        }
        newValues.put(AppContract.HealthEntry.COLUMN_NOTES, activityNotes.getText().toString());
        newValues.put(AppContract.HealthEntry.COLUMN_LAST_UPDATED_TS, currentTimestamp);
        Log.v(TAG,"newValues-"+newValues);
        if(HEALTH_ID == -1) {
            HEALTH_ID = AppContract.HealthEntry.getIdFromUri(getActivity().getContentResolver().insert(uri, newValues));
            if(HEALTH_ID == -1){
                Toast.makeText(getActivity(), getString(R.string.text_entry_cannot_save) + TITLE_HEALTH, Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getActivity(), TITLE_HEALTH + getString(R.string.text_entry_added), Toast.LENGTH_LONG).show();
            }
        }else{
            String sWhere = AppContract.HealthEntry.COLUMN_USER_ID + " = ? AND " + AppContract.HealthEntry.COLUMN_BABY_ID + " = ? AND " + AppContract.HealthEntry._ID + " = ?";
            String[] sWhereArgs = new String[]{MainActivity.LOGGED_IN_USER_ID,Long.toString(MainActivity.ACTIVE_BABY_ID),Long.toString(HEALTH_ID)};
            getActivity().getContentResolver().update(uri, newValues, sWhere,sWhereArgs);
            Toast.makeText(getActivity(), TITLE_HEALTH + getString(R.string.text_entry_saved), Toast.LENGTH_LONG).show();
        }
        goFinish();
    }

    public void actionDelete(){
        Log.v(TAG, "actionDelete");

        Uri uri = AppContract.HealthEntry.CONTENT_URI;
        String sWhere = AppContract.HealthEntry.COLUMN_USER_ID + " = ? AND " + AppContract.HealthEntry.COLUMN_BABY_ID + " = ? AND " + AppContract.HealthEntry._ID + " = ?";
        String[] sWhereArgs = new String[]{MainActivity.LOGGED_IN_USER_ID,Long.toString(MainActivity.ACTIVE_BABY_ID),Long.toString(HEALTH_ID)};
        getActivity().getContentResolver().delete(uri, sWhere, sWhereArgs);
        Toast.makeText(getActivity(), TITLE_HEALTH + getString(R.string.text_entry_deleted), Toast.LENGTH_LONG).show();
        goFinish();
    }


    public void goFinish(){
        Log.v(TAG, "goFinish");
        HEALTH_ID = -1;
        ((MainActivity) getActivity()).handleFragments(new ActivitiesFragment(),ActivitiesFragment.TAG,ActivitiesFragment.KEEP_IN_STACK);
        //((MainActivity) getActivity()).handleFragments(true);
    }
}
