package com.simplesolutions2003.happybabycare;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.simplesolutions2003.happybabycare.data.AppContract;

/**
 * Created by SuriyaKumar on 8/20/2016.
 */
public class DiaperFragment extends Fragment {
    public final static boolean KEEP_IN_STACK = false;
    public final static String TAG = DiaperFragment.class.getSimpleName();
    public static long DIAPER_ID = -1;
    public static final String TITLE_DIAPER = "Diaper";

    String activityTimestamp = null;

    public final static int DIAPER_TYPE_WET = 1;
    public final static int DIAPER_TYPE_DIRTY = 2;
    public final static int DIAPER_TYPE_MIXED = 3;
    public final static int DIAPER_TYPE_DRY = 4;

    public final static String DIAPER_TYPE_TEXT_WET = "Wet";
    public final static String DIAPER_TYPE_TEXT_DIRTY = "Dirty";
    public final static String DIAPER_TYPE_TEXT_MIXED = "Mixed";
    public final static String DIAPER_TYPE_TEXT_DRY = "Dry";

    public final static String DIAPER_CREAM_YES = "(applied cream)";
    public final static String DIAPER_CREAM_NO = "";

    private static final String[] DIAPER_COLUMNS = {
            AppContract.DiaperEntry.TABLE_NAME + "." + AppContract.DiaperEntry._ID,
            AppContract.DiaperEntry.TABLE_NAME + "." + AppContract.DiaperEntry.COLUMN_USER_ID,
            AppContract.DiaperEntry.TABLE_NAME + "." + AppContract.DiaperEntry.COLUMN_BABY_ID,
            AppContract.DiaperEntry.TABLE_NAME + "." + AppContract.DiaperEntry.COLUMN_TIMESTAMP,
            AppContract.DiaperEntry.TABLE_NAME + "." + AppContract.DiaperEntry.COLUMN_DATE,
            AppContract.DiaperEntry.TABLE_NAME + "." + AppContract.DiaperEntry.COLUMN_TIME,
            AppContract.DiaperEntry.TABLE_NAME + "." + AppContract.DiaperEntry.COLUMN_TYPE,
            AppContract.DiaperEntry.TABLE_NAME + "." + AppContract.DiaperEntry.COLUMN_CREAM,
            AppContract.DiaperEntry.TABLE_NAME + "." + AppContract.DiaperEntry.COLUMN_NOTES
    };


    static final int COL_DIAPER_ID = 0;
    static final int COL_DIAPER_USER_ID = 1;
    static final int COL_DIAPER_BABY_ID = 2;
    static final int COL_DIAPER_TIMESTAMP = 3;
    static final int COL_DIAPER_DATE = 4;
    static final int COL_DIAPER_TIME = 5;
    static final int COL_DIAPER_TYPE = 6;
    static final int COL_DIAPER_CREAM = 7;
    static final int COL_DIAPER_NOTES = 8;

    EditText activityDate;
    EditText activityTime;
    EditText activityNotes;

    ImageButton diaperWet;
    ImageButton diaperDirty;
    ImageButton diaperMixed;
    ImageButton diaperDry;

    CheckBox diaperCream;
    int diaperType = 0;

    public DiaperFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.diaper, container, false);
        activityDate = (EditText) rootView.findViewById(R.id.activity_date);
        activityTime = (EditText) rootView.findViewById(R.id.activity_time);
        activityNotes = (EditText) rootView.findViewById(R.id.activity_notes);

        diaperWet = (ImageButton) rootView.findViewById(R.id.diaper_wet);
        diaperDirty = (ImageButton) rootView.findViewById(R.id.diaper_dirty);
        diaperMixed = (ImageButton) rootView.findViewById(R.id.diaper_mixed);
        diaperDry = (ImageButton) rootView.findViewById(R.id.diaper_dry);

        diaperCream = (CheckBox) rootView.findViewById(R.id.diaper_cream);

        activityDate.setInputType(InputType.TYPE_NULL);
        activityTime.setInputType(InputType.TYPE_NULL);
        activityDate.setText(new Utilities(getActivity()).getCurrentDateDisp());
        activityTime.setText(new Utilities(getActivity()).getCurrentTimeDB());

        SetDateEditText setActivityDate = new SetDateEditText(activityDate, getActivity());
        SetTimeEditText setActivityTime = new SetTimeEditText(activityTime, getActivity());

        setDiaperType(0);

        diaperWet.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v(TAG, "diaperWet clicked");
                setDiaperType(DIAPER_TYPE_WET);
            }
        });

        diaperDirty.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v(TAG, "diaperDirty clicked");
                setDiaperType(DIAPER_TYPE_DIRTY);
            }
        });

        diaperMixed.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v(TAG, "diaperMixed clicked");
                setDiaperType(DIAPER_TYPE_MIXED);
            }
        });

        diaperDry.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v(TAG, "diaperDry clicked");
                setDiaperType(DIAPER_TYPE_DRY);
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

        if(DIAPER_ID != -1) {
            Uri uri = AppContract.DiaperEntry.buildDiaperUri(DIAPER_ID);
            Cursor activityEntry = getActivity().getContentResolver().query(uri,DIAPER_COLUMNS,null,null,null);
            if(activityEntry != null){
                if(activityEntry.getCount() > 0){
                    Log.v(TAG,"got diaper entry");
                    activityEntry.moveToFirst();
                    activityDate.setText(new Utilities(getActivity()).convDateDb2Disp(activityEntry.getString(COL_DIAPER_DATE)));
                    activityTime.setText(activityEntry.getString(COL_DIAPER_TIME));
                    setDiaperType(getDiaperTypeId(activityEntry.getString(COL_DIAPER_TYPE)));
                    activityNotes.setText(activityEntry.getString(COL_DIAPER_NOTES));
                    activityTimestamp = activityEntry.getString(COL_DIAPER_TIMESTAMP);

                    if(activityEntry.getString(COL_DIAPER_CREAM).equals(DIAPER_CREAM_YES)){
                        diaperCream.setChecked(true);
                    }else{
                        diaperCream.setChecked(false);
                    }

                    activityDate.setContentDescription(activityDate.getText().toString());
                    activityTime.setContentDescription(activityTime.getText().toString());
                    activityNotes.setContentDescription(activityNotes.getText().toString());
                }
            }else{
                DIAPER_ID = -1;
            }
        }
        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        ((MainActivity) getActivity()).updateToolbarTitle(TITLE_DIAPER + " - " + MainActivity.ACTIVE_BABY_NAME);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.v(TAG, "onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu,inflater);
        validateInputs();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu){
        Log.v(TAG, "onPrepareOptionsMenu");
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
                if(DIAPER_ID != -1) {
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
                activityTime.getText().toString().isEmpty()){
            MainActivity.saveMenuEnabled = false;
        }else{
            MainActivity.saveMenuEnabled = true;
        }
        if (DIAPER_ID == -1) {
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
        Uri uri = AppContract.DiaperEntry.CONTENT_URI;
        String currentTimestamp = new Utilities().getCurrentTimestampDB();
        if(activityTimestamp == null){
            activityTimestamp = currentTimestamp;
        }

        ContentValues newValues = new ContentValues();
        newValues.put(AppContract.DiaperEntry.COLUMN_USER_ID, MainActivity.LOGGED_IN_USER_ID);
        newValues.put(AppContract.DiaperEntry.COLUMN_BABY_ID, MainActivity.ACTIVE_BABY_ID);
        newValues.put(AppContract.DiaperEntry.COLUMN_TIMESTAMP, activityTimestamp);
        newValues.put(AppContract.DiaperEntry.COLUMN_DATE, new Utilities(getActivity()).convDateDisp2Db(activityDate.getText().toString()));
        newValues.put(AppContract.DiaperEntry.COLUMN_TIME, activityTime.getText().toString());
        newValues.put(AppContract.DiaperEntry.COLUMN_TYPE, getDiaperTypeName(diaperType));
        if(diaperCream.isChecked()){
            newValues.put(AppContract.DiaperEntry.COLUMN_CREAM, DIAPER_CREAM_YES);
        }else{
            newValues.put(AppContract.DiaperEntry.COLUMN_CREAM, DIAPER_CREAM_NO);
        }

        newValues.put(AppContract.DiaperEntry.COLUMN_NOTES, activityNotes.getText().toString());
        newValues.put(AppContract.DiaperEntry.COLUMN_LAST_UPDATED_TS, currentTimestamp);
        Log.v(TAG,"newValues-"+newValues);
        if(DIAPER_ID == -1) {
            DIAPER_ID = AppContract.DiaperEntry.getIdFromUri(getActivity().getContentResolver().insert(uri, newValues));
            if(DIAPER_ID == -1){
                Toast.makeText(getActivity(), getString(R.string.text_entry_cannot_save) + TITLE_DIAPER, Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getActivity(), TITLE_DIAPER + getString(R.string.text_entry_added), Toast.LENGTH_LONG).show();
            }
        }else{
            String sWhere = AppContract.DiaperEntry.COLUMN_USER_ID + " = ? AND " + AppContract.DiaperEntry.COLUMN_BABY_ID + " = ? AND " + AppContract.DiaperEntry._ID + " = ?";
            String[] sWhereArgs = new String[]{MainActivity.LOGGED_IN_USER_ID,Long.toString(MainActivity.ACTIVE_BABY_ID),Long.toString(DiaperFragment.DIAPER_ID)};
            getActivity().getContentResolver().update(uri, newValues, sWhere,sWhereArgs);
            Toast.makeText(getActivity(), TITLE_DIAPER + getString(R.string.text_entry_saved), Toast.LENGTH_LONG).show();
        }
        goFinish();
    }

    public void actionDelete(){
        Log.v(TAG, "actionDelete");

        Uri uri = AppContract.DiaperEntry.CONTENT_URI;
        String sWhere = AppContract.DiaperEntry.COLUMN_USER_ID + " = ? AND " + AppContract.DiaperEntry.COLUMN_BABY_ID + " = ? AND " + AppContract.DiaperEntry._ID + " = ?";
        String[] sWhereArgs = new String[]{MainActivity.LOGGED_IN_USER_ID,Long.toString(MainActivity.ACTIVE_BABY_ID),Long.toString(DiaperFragment.DIAPER_ID)};
        getActivity().getContentResolver().delete(uri, sWhere, sWhereArgs);
        Toast.makeText(getActivity(), TITLE_DIAPER + getString(R.string.text_entry_deleted), Toast.LENGTH_LONG).show();
        goFinish();
    }


    public void goFinish(){
        Log.v(TAG, "goFinish");
        DIAPER_ID = -1;
        ((MainActivity) getActivity()).handleFragments(new ActivitiesFragment(),ActivitiesFragment.TAG,ActivitiesFragment.KEEP_IN_STACK);
        //((MainActivity) getActivity()).handleFragments(true);
    }

    public String getDiaperTypeName(int type){
        switch(type){
            case DIAPER_TYPE_WET:
                return DIAPER_TYPE_TEXT_WET;
            case DIAPER_TYPE_DIRTY:
                return DIAPER_TYPE_TEXT_DIRTY;
            case DIAPER_TYPE_MIXED:
                return DIAPER_TYPE_TEXT_MIXED;
            case DIAPER_TYPE_DRY:
            default:
                return DIAPER_TYPE_TEXT_DRY;
        }
    }

    public int getDiaperTypeId(String type){
        switch(type){
            case DIAPER_TYPE_TEXT_WET:
                return DIAPER_TYPE_WET;
            case DIAPER_TYPE_TEXT_DIRTY:
                return DIAPER_TYPE_DIRTY;
            case DIAPER_TYPE_TEXT_MIXED:
                return DIAPER_TYPE_MIXED;
            case DIAPER_TYPE_TEXT_DRY:
            default:
                return DIAPER_TYPE_DRY;
        }
    }

    public void setDiaperType(int type){
        diaperWet.setImageAlpha(50);
        diaperWet.setContentDescription(getString(R.string.text_diaper_wet) + getString(R.string.cd_not_selected));
        diaperDirty.setImageAlpha(50);
        diaperDirty.setContentDescription(getString(R.string.text_diaper_dirty) + getString(R.string.cd_not_selected));
        diaperMixed.setImageAlpha(50);
        diaperMixed.setContentDescription(getString(R.string.text_diaper_mixed) + getString(R.string.cd_not_selected));
        diaperDry.setImageAlpha(50);
        diaperDry.setContentDescription(getString(R.string.text_diaper_dry) + getString(R.string.cd_not_selected));
        diaperType = type;

        switch (diaperType){
            case DIAPER_TYPE_WET:
                diaperWet.setImageAlpha(255);
                diaperWet.setContentDescription(getString(R.string.text_diaper_wet) + getString(R.string.cd_selected));
                break;
            case DIAPER_TYPE_DIRTY:
                diaperDirty.setImageAlpha(255);
                diaperDirty.setContentDescription(getString(R.string.text_diaper_dirty) + getString(R.string.cd_selected));
                break;
            case DIAPER_TYPE_MIXED:
                diaperMixed.setImageAlpha(255);
                diaperMixed.setContentDescription(getString(R.string.text_diaper_mixed) + getString(R.string.cd_selected));
                break;
            case DIAPER_TYPE_DRY:
            default:
                diaperDry.setImageAlpha(255);
                diaperDry.setContentDescription(getString(R.string.text_diaper_dry) + getString(R.string.cd_selected));
                break;
        }
    }

}
