package com.simplesolutions2003.happybabycare;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.simplesolutions2003.happybabycare.data.AppContract;

/**
 * Created by SuriyaKumar on 8/20/2016.
 */
public class FeedingFragment extends Fragment {
    public final static boolean KEEP_IN_STACK = false;
    public final static String TAG = FeedingFragment.class.getSimpleName();
    public static final String TITLE_FEEDING = "Feeding";
    public static long FEEDING_ID = -1;

    public static final String FEEDING_TYPE_NURSING = "Nursing";

    String activityTimestamp = null;

    private static final String[] FEEDING_COLUMNS = {
            AppContract.FeedingEntry.TABLE_NAME + "." + AppContract.FeedingEntry._ID,
            AppContract.FeedingEntry.TABLE_NAME + "." + AppContract.FeedingEntry.COLUMN_USER_ID,
            AppContract.FeedingEntry.TABLE_NAME + "." + AppContract.FeedingEntry.COLUMN_BABY_ID,
            AppContract.FeedingEntry.TABLE_NAME + "." + AppContract.FeedingEntry.COLUMN_TIMESTAMP,
            AppContract.FeedingEntry.TABLE_NAME + "." + AppContract.FeedingEntry.COLUMN_DATE,
            AppContract.FeedingEntry.TABLE_NAME + "." + AppContract.FeedingEntry.COLUMN_TIME,
            AppContract.FeedingEntry.TABLE_NAME + "." + AppContract.FeedingEntry.COLUMN_TYPE,
            AppContract.FeedingEntry.TABLE_NAME + "." + AppContract.FeedingEntry.COLUMN_QUANTITY,
            AppContract.FeedingEntry.TABLE_NAME + "." + AppContract.FeedingEntry.COLUMN_UNIT,
            AppContract.FeedingEntry.TABLE_NAME + "." + AppContract.FeedingEntry.COLUMN_NOTES
    };


    static final int COL_FEEDING_ID = 0;
    static final int COL_FEEDING_USER_ID = 1;
    static final int COL_FEEDING_BABY_ID = 2;
    static final int COL_FEEDING_TIMESTAMP = 3;
    static final int COL_FEEDING_DATE = 4;
    static final int COL_FEEDING_TIME = 5;
    static final int COL_FEEDING_TYPE = 6;
    static final int COL_FEEDING_QUANTITY = 7;
    static final int COL_FEEDING_UNIT = 8;
    static final int COL_FEEDING_NOTES = 9;

    EditText activityDate;
    EditText activityTime;
    EditText activityNotes;

    Spinner feedingType;
    Spinner feedingUnit;
    EditText feedingQuantity;

    public FeedingFragment(){}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final String[] spinnerNursingArray = getResources().getStringArray(R.array.spinner_feeding_unit_nursing);
        final String[] spinnerOthersArray = getResources().getStringArray(R.array.spinner_feeding_unit_others);

        View rootView = inflater.inflate(R.layout.feeding, container, false);

        activityDate = (EditText) rootView.findViewById(R.id.activity_date);
        activityTime = (EditText) rootView.findViewById(R.id.activity_time);
        activityNotes = (EditText) rootView.findViewById(R.id.activity_notes);

        feedingType = (Spinner) rootView.findViewById(R.id.feeding_type);
        feedingQuantity = (EditText) rootView.findViewById(R.id.feeding_quantity);
        feedingUnit = (Spinner) rootView.findViewById(R.id.feeding_unit);

        activityDate.setInputType(InputType.TYPE_NULL);
        activityTime.setInputType(InputType.TYPE_NULL);
        activityDate.setText(new Utilities(getActivity()).getCurrentDateDisp());
        activityTime.setText(new Utilities(getActivity()).getCurrentTimeDB());

        SetDateEditText setActivityDate = new SetDateEditText(activityDate, getActivity());
        SetTimeEditText setActivityTime = new SetTimeEditText(activityTime, getActivity());

        feedingType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayAdapter<String> spinnerArrayAdapter;
                if(feedingType.getSelectedItem().toString().equals(FEEDING_TYPE_NURSING)){
                    spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerNursingArray);
                    feedingQuantity.setHint(getString(R.string.text_feeding_duration));
                }else{
                    spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerOthersArray);
                    feedingQuantity.setHint(getString(R.string.text_feeding_qty));
                }
                spinnerArrayAdapter.setDropDownViewResource(R.layout.dropdown_item);
                feedingUnit.setAdapter(spinnerArrayAdapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        feedingQuantity.addTextChangedListener(new TextWatcher() {

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


        if(FEEDING_ID != -1) {
            Uri uri = AppContract.FeedingEntry.buildFeedingUri(FEEDING_ID);
            Cursor activityEntry = getActivity().getContentResolver().query(uri,FEEDING_COLUMNS,null,null,null);
            if(activityEntry != null){
                if(activityEntry.getCount() > 0){
                    Log.v(TAG,"got feeding entry");
                    activityEntry.moveToFirst();
                    activityDate.setText(new Utilities(getActivity()).convDateDb2Disp(activityEntry.getString(COL_FEEDING_DATE)));
                    activityTime.setText(activityEntry.getString(COL_FEEDING_TIME));
                    activityNotes.setText(activityEntry.getString(COL_FEEDING_NOTES));
                    for(int iType = 0; iType < feedingType.getCount(); iType++){
                        if(activityEntry.getString(COL_FEEDING_TYPE).equals(feedingType.getItemAtPosition(iType).toString())){
                            feedingType.setSelection(iType);
                            feedingType.setContentDescription(activityEntry.getString(COL_FEEDING_TYPE));
                            break;
                        }
                    }

                    feedingQuantity.setText(activityEntry.getString(COL_FEEDING_QUANTITY));

                    for(int iUnit = 0; iUnit < feedingUnit.getCount(); iUnit++){
                        if(activityEntry.getString(COL_FEEDING_UNIT).equals(feedingUnit.getItemAtPosition(iUnit).toString())){
                            feedingUnit.setSelection(iUnit);
                            feedingUnit.setContentDescription(activityEntry.getString(COL_FEEDING_UNIT));
                            break;
                        }
                    }

                    activityTimestamp = activityEntry.getString(COL_FEEDING_TIMESTAMP);
                    activityDate.setContentDescription(activityDate.getText().toString());
                    activityTime.setContentDescription(activityTime.getText().toString());
                    activityNotes.setContentDescription(activityNotes.getText().toString());
                    feedingQuantity.setContentDescription(feedingQuantity.getText().toString());
                }
            }else{
                FEEDING_ID = -1;
            }
        }
        return rootView;
    }


    @Override
    public void onResume()
    {
        super.onResume();
        ((MainActivity) getActivity()).updateToolbarTitle(TITLE_FEEDING + " - " + MainActivity.ACTIVE_BABY_NAME);
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
                if(FEEDING_ID != -1) {
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
                feedingQuantity.getText().toString().isEmpty()){
            MainActivity.saveMenuEnabled = false;
        }else{
            MainActivity.saveMenuEnabled = true;
        }

        if (FEEDING_ID == -1) {
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
        Uri uri = AppContract.FeedingEntry.CONTENT_URI;
        String currentTimestamp = new Utilities().getCurrentTimestampDB();
        if(activityTimestamp == null){
            activityTimestamp = currentTimestamp;
        }
        ContentValues newValues = new ContentValues();
        newValues.put(AppContract.FeedingEntry.COLUMN_USER_ID, MainActivity.LOGGED_IN_USER_ID);
        newValues.put(AppContract.FeedingEntry.COLUMN_BABY_ID, MainActivity.ACTIVE_BABY_ID);
        newValues.put(AppContract.FeedingEntry.COLUMN_TIMESTAMP, activityTimestamp);
        newValues.put(AppContract.FeedingEntry.COLUMN_DATE, new Utilities().convDateDisp2Db(activityDate.getText().toString()));
        newValues.put(AppContract.FeedingEntry.COLUMN_TIME, activityTime.getText().toString());
        newValues.put(AppContract.FeedingEntry.COLUMN_TYPE, feedingType.getSelectedItem().toString());
        newValues.put(AppContract.FeedingEntry.COLUMN_QUANTITY, feedingQuantity.getText().toString());
        newValues.put(AppContract.FeedingEntry.COLUMN_UNIT, feedingUnit.getSelectedItem().toString());
        newValues.put(AppContract.FeedingEntry.COLUMN_NOTES, activityNotes.getText().toString());
        newValues.put(AppContract.FeedingEntry.COLUMN_LAST_UPDATED_TS, currentTimestamp);
        Log.v(TAG,"newValues-"+newValues);
        if(FEEDING_ID == -1) {
            FEEDING_ID = AppContract.FeedingEntry.getIdFromUri(getActivity().getContentResolver().insert(uri, newValues));
            if(FEEDING_ID == -1){
                Toast.makeText(getActivity(), getString(R.string.text_entry_cannot_save) + TITLE_FEEDING, Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getActivity(), TITLE_FEEDING + getString(R.string.text_entry_added), Toast.LENGTH_LONG).show();
            }
        }else{
            String sWhere = AppContract.FeedingEntry.COLUMN_USER_ID + " = ? AND " + AppContract.FeedingEntry.COLUMN_BABY_ID + " = ? AND " + AppContract.FeedingEntry._ID + " = ?";
            String[] sWhereArgs = new String[]{MainActivity.LOGGED_IN_USER_ID,Long.toString(MainActivity.ACTIVE_BABY_ID),Long.toString(FEEDING_ID)};
            getActivity().getContentResolver().update(uri, newValues, sWhere,sWhereArgs);
            Toast.makeText(getActivity(), TITLE_FEEDING + getString(R.string.text_entry_saved), Toast.LENGTH_LONG).show();
        }
        goFinish();
    }

    public void actionDelete(){
        Log.v(TAG, "actionDelete");

        Uri uri = AppContract.FeedingEntry.CONTENT_URI;
        String sWhere = AppContract.FeedingEntry.COLUMN_USER_ID + " = ? AND " + AppContract.FeedingEntry.COLUMN_BABY_ID + " = ? AND " + AppContract.FeedingEntry._ID + " = ?";
        String[] sWhereArgs = new String[]{MainActivity.LOGGED_IN_USER_ID,Long.toString(MainActivity.ACTIVE_BABY_ID),Long.toString(FEEDING_ID)};
        getActivity().getContentResolver().delete(uri, sWhere, sWhereArgs);
        Toast.makeText(getActivity(),TITLE_FEEDING + getString(R.string.text_entry_deleted), Toast.LENGTH_LONG).show();
        goFinish();
    }


    public void goFinish(){
        Log.v(TAG, "goFinish");
        FEEDING_ID = -1;
        ((MainActivity) getActivity()).handleFragments(new ActivitiesFragment(),ActivitiesFragment.TAG,ActivitiesFragment.KEEP_IN_STACK);
        //((MainActivity) getActivity()).handleFragments(true);
    }
}
