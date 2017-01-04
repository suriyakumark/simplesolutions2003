package com.simplesolutions2003.happybabycare;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.TextView;
/**
 * Created by SuriyaKumar on 8/20/2016.
 */
public class SleepingFragment extends Fragment {
    public final static boolean KEEP_IN_STACK = false;
    public final static String TAG = SleepingFragment.class.getSimpleName();
    public static final String TITLE_SLEEPING = "Sleeping";
    public static long SLEEPING_ID = -1;

    String activityTimestamp = null;

    private static final String[] SLEEPING_COLUMNS = {
            AppContract.SleepingEntry.TABLE_NAME + "." + AppContract.SleepingEntry._ID,
            AppContract.SleepingEntry.TABLE_NAME + "." + AppContract.SleepingEntry.COLUMN_USER_ID,
            AppContract.SleepingEntry.TABLE_NAME + "." + AppContract.SleepingEntry.COLUMN_BABY_ID,
            AppContract.SleepingEntry.TABLE_NAME + "." + AppContract.SleepingEntry.COLUMN_TIMESTAMP,
            AppContract.SleepingEntry.TABLE_NAME + "." + AppContract.SleepingEntry.COLUMN_DATE,
            AppContract.SleepingEntry.TABLE_NAME + "." + AppContract.SleepingEntry.COLUMN_TIME,
            AppContract.SleepingEntry.TABLE_NAME + "." + AppContract.SleepingEntry.COLUMN_END_TIME,
            AppContract.SleepingEntry.TABLE_NAME + "." + AppContract.SleepingEntry.COLUMN_DURATION,
            AppContract.SleepingEntry.TABLE_NAME + "." + AppContract.SleepingEntry.COLUMN_WHERE_SLEEP,
            AppContract.SleepingEntry.TABLE_NAME + "." + AppContract.SleepingEntry.COLUMN_NOTES
    };


    static final int COL_SLEEPING_ID = 0;
    static final int COL_SLEEPING_USER_ID = 1;
    static final int COL_SLEEPING_BABY_ID = 2;
    static final int COL_SLEEPING_TIMESTAMP = 3;
    static final int COL_SLEEPING_DATE = 4;
    static final int COL_SLEEPING_TIME = 5;
    static final int COL_SLEEPING_END_TIME = 6;
    static final int COL_SLEEPING_DURATION = 7;
    static final int COL_SLEEPING_WHERE_SLEEP = 8;
    static final int COL_SLEEPING_NOTES = 9;

    EditText activityDate;
    EditText activityTime;
    EditText activityNotes;

    EditText sleepingEndTime;
    TextView sleepingDuration;
    Spinner sleepingWhere;

    public SleepingFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.sleeping, container, false);
        activityDate = (EditText) rootView.findViewById(R.id.activity_date);
        activityTime = (EditText) rootView.findViewById(R.id.activity_time);
        activityNotes = (EditText) rootView.findViewById(R.id.activity_notes);

        sleepingEndTime = (EditText) rootView.findViewById(R.id.sleep_end_time);
        sleepingDuration = (TextView) rootView.findViewById(R.id.sleep_duration);
        sleepingWhere = (Spinner) rootView.findViewById(R.id.sleep_place);

        activityDate.setInputType(InputType.TYPE_NULL);
        activityTime.setInputType(InputType.TYPE_NULL);
        sleepingEndTime.setInputType(InputType.TYPE_NULL);
        activityDate.setText(new Utilities(getActivity()).getCurrentDateDisp());
        activityTime.setText(new Utilities(getActivity()).getCurrentTimeDB());
        sleepingEndTime.setText(new Utilities(getActivity()).getCurrentTimeDB());

        SetDateEditText setActivityDate = new SetDateEditText(activityDate, getActivity());
        SetTimeEditText setActivityTime = new SetTimeEditText(activityTime, getActivity());
        SetTimeEditText setSleepingEndTime = new SetTimeEditText(sleepingEndTime, getActivity());

        sleepingEndTime.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                sleepingDuration.setText(new Utilities().getTimeDifferenceDisp(activityTime.getText().toString(),sleepingEndTime.getText().toString()));
                new Utilities(getActivity()).resetFocus(sleepingEndTime);
                updateMenuVisibility();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

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
                sleepingDuration.setText(new Utilities().getTimeDifferenceDisp(activityTime.getText().toString(),sleepingEndTime.getText().toString()));
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

        if(SLEEPING_ID != -1) {
            Uri uri = AppContract.SleepingEntry.buildSleepingUri(SLEEPING_ID);
            Cursor activityEntry = getActivity().getContentResolver().query(uri,SLEEPING_COLUMNS,null,null,null);
            if(activityEntry != null){
                if(activityEntry.getCount() > 0){
                    Log.v(TAG,"got sleep entry");
                    activityEntry.moveToFirst();
                    activityDate.setText(new Utilities(getActivity()).convDateDb2Disp(activityEntry.getString(COL_SLEEPING_DATE)));
                    activityTime.setText(activityEntry.getString(COL_SLEEPING_TIME));
                    activityNotes.setText(activityEntry.getString(COL_SLEEPING_NOTES));
                    sleepingEndTime.setText(activityEntry.getString(COL_SLEEPING_END_TIME));
                    //sleepingDuration.setText(activityEntry.getString(COL_SLEEPING_DURATION));
                    sleepingDuration.setText(new Utilities().getTimeDifferenceDisp(activityTime.getText().toString(),sleepingEndTime.getText().toString()));
                    for(int iType = 0; iType < sleepingWhere.getCount(); iType++){
                        if(activityEntry.getString(COL_SLEEPING_WHERE_SLEEP).equals(sleepingWhere.getItemAtPosition(iType).toString())){
                            sleepingWhere.setSelection(iType);
                            sleepingWhere.setContentDescription(activityEntry.getString(COL_SLEEPING_WHERE_SLEEP));
                            break;
                        }
                    }
                    activityTimestamp = activityEntry.getString(COL_SLEEPING_TIMESTAMP);

                    activityDate.setContentDescription(activityDate.getText().toString());
                    activityTime.setContentDescription(activityTime.getText().toString());
                    activityNotes.setContentDescription(activityNotes.getText().toString());
                    sleepingEndTime.setContentDescription(sleepingEndTime.getText().toString());
                    sleepingDuration.setContentDescription(sleepingDuration.getText().toString());
                }
            }else{
                SLEEPING_ID = -1;
            }
        }
        return rootView;
    }


    @Override
    public void onResume()
    {
        super.onResume();
        ((MainActivity) getActivity()).updateToolbarTitle(TITLE_SLEEPING + " - " + MainActivity.ACTIVE_BABY_NAME);
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
                if(SLEEPING_ID != -1) {
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
                sleepingEndTime.getText().toString().isEmpty() |
                sleepingDuration.getText().toString().isEmpty()){

            MainActivity.saveMenuEnabled = false;
        }else{
            MainActivity.saveMenuEnabled = true;
        }
        if (SLEEPING_ID == -1) {
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
        Uri uri = AppContract.SleepingEntry.CONTENT_URI;
        String currentTimestamp = new Utilities().getCurrentTimestampDB();
        if(activityTimestamp == null){
            activityTimestamp = currentTimestamp;
        }

        ContentValues newValues = new ContentValues();
        newValues.put(AppContract.SleepingEntry.COLUMN_USER_ID, MainActivity.LOGGED_IN_USER_ID);
        newValues.put(AppContract.SleepingEntry.COLUMN_BABY_ID, MainActivity.ACTIVE_BABY_ID);
        newValues.put(AppContract.SleepingEntry.COLUMN_TIMESTAMP, activityTimestamp);
        newValues.put(AppContract.SleepingEntry.COLUMN_DATE, new Utilities(getActivity()).convDateDisp2Db(activityDate.getText().toString()));
        newValues.put(AppContract.SleepingEntry.COLUMN_TIME, activityTime.getText().toString());
        newValues.put(AppContract.SleepingEntry.COLUMN_END_TIME, sleepingEndTime.getText().toString());
        newValues.put(AppContract.SleepingEntry.COLUMN_DURATION, new Utilities().getTimeDifferenceMins(activityTime.getText().toString(),sleepingEndTime.getText().toString()));
        newValues.put(AppContract.SleepingEntry.COLUMN_WHERE_SLEEP, sleepingWhere.getSelectedItem().toString());
        newValues.put(AppContract.SleepingEntry.COLUMN_NOTES, activityNotes.getText().toString());
        newValues.put(AppContract.SleepingEntry.COLUMN_LAST_UPDATED_TS, currentTimestamp);
        Log.v(TAG,"newValues-"+newValues);

        if(SLEEPING_ID == -1) {
            SLEEPING_ID = AppContract.SleepingEntry.getIdFromUri(getActivity().getContentResolver().insert(uri, newValues));
            if(SLEEPING_ID == -1){
                Toast.makeText(getActivity(), getString(R.string.text_entry_cannot_save) + TITLE_SLEEPING, Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getActivity(), TITLE_SLEEPING + getString(R.string.text_entry_deleted), Toast.LENGTH_LONG).show();
            }
        }else{
            String sWhere = AppContract.SleepingEntry.COLUMN_USER_ID + " = ? AND " + AppContract.SleepingEntry.COLUMN_BABY_ID + " = ? AND " + AppContract.SleepingEntry._ID + " = ?";
            String[] sWhereArgs = new String[]{MainActivity.LOGGED_IN_USER_ID,Long.toString(MainActivity.ACTIVE_BABY_ID),Long.toString(SLEEPING_ID)};
            getActivity().getContentResolver().update(uri, newValues, sWhere,sWhereArgs);
            Toast.makeText(getActivity(), TITLE_SLEEPING + getString(R.string.text_entry_deleted), Toast.LENGTH_LONG).show();
        }
        goFinish();
    }

    public void actionDelete(){
        Log.v(TAG, "actionDelete");

        Uri uri = AppContract.SleepingEntry.CONTENT_URI;
        String sWhere = AppContract.SleepingEntry.COLUMN_USER_ID + " = ? AND " + AppContract.SleepingEntry.COLUMN_BABY_ID + " = ? AND " + AppContract.SleepingEntry._ID + " = ?";
        String[] sWhereArgs = new String[]{MainActivity.LOGGED_IN_USER_ID,Long.toString(MainActivity.ACTIVE_BABY_ID),Long.toString(SLEEPING_ID)};
        getActivity().getContentResolver().delete(uri, sWhere, sWhereArgs);
        Toast.makeText(getActivity(), TITLE_SLEEPING + getString(R.string.text_entry_deleted), Toast.LENGTH_LONG).show();
        goFinish();
    }


    public void goFinish(){
        Log.v(TAG, "goFinish");
        SLEEPING_ID = -1;
        ((MainActivity) getActivity()).handleFragments(new ActivitiesFragment(),ActivitiesFragment.TAG,ActivitiesFragment.KEEP_IN_STACK);
        //((MainActivity) getActivity()).handleFragments(true);
    }
}
