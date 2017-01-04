package com.simplesolutions2003.happybabycare;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.simplesolutions2003.happybabycare.data.AppContract;

/**
 * Created by SuriyaKumar on 8/20/2016.
 */
public class ActivitiesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    public final static boolean KEEP_IN_STACK = true;
    public final static String TAG = ActivitiesFragment.class.getSimpleName();

    private final static int ACTIVITIES_LOADER = 0;
    private static String ACTIVITIES_DATE = "";
    private int dPosition;
    private ActivitiesAdapter activitiesListAdapter;
    ListView activitiesListView;
    EditText activityFilterDate;
    Spinner activityFilterType;
    TextView tvEmptyLoading;
    LinearLayout activitiesSummary;

    public static final String ACTIVITY_TYPE_FEEDING = "Feeding";
    public static final String ACTIVITY_TYPE_DIAPER = "Diaper";
    public static final String ACTIVITY_TYPE_SLEEPING = "Sleeping";
    public static final String ACTIVITY_TYPE_HEALTH = "Health";

    private static final String[] ACTIVITY_COLUMNS = {
            AppContract.ActivitiesEntry.COLUMN_ACTIVITY_TYPE,
            AppContract.ActivitiesEntry.COLUMN_ACTIVITY_ID,
            AppContract.ActivitiesEntry.COLUMN_USER_ID,
            AppContract.ActivitiesEntry.COLUMN_BABY_ID,
            AppContract.ActivitiesEntry.COLUMN_TIMESTAMP,
            AppContract.ActivitiesEntry.COLUMN_DATE,
            AppContract.ActivitiesEntry.COLUMN_TIME,
            AppContract.ActivitiesEntry.COLUMN_SUMMARY,
            AppContract.ActivitiesEntry.COLUMN_DETAIL
    };


    static final int COL_ACTIVITY_TYPE = 0;
    static final int COL_ACTIVITY_ID = 1;
    static final int COL_ACTIVITY_USER_ID = 2;
    static final int COL_ACTIVITY_BABY_ID = 3;
    static final int COL_ACTIVITY_TIMESTAMP = 4;
    static final int COL_ACTIVITY_DATE = 5;
    static final int COL_ACTIVITY_TIME = 6;
    static final int COL_ACTIVITY_SUMMARY = 7;
    static final int COL_ACTIVITY_DETAIL = 8;

    private static final String[] SUMMARY_COLUMNS = {
            AppContract.ActivitiesEntry.COLUMN_ACTIVITY_ID,
            AppContract.ActivitiesEntry.COLUMN_SUMMARY,
            AppContract.ActivitiesEntry.COLUMN_DETAIL
    };

    static final int COL_SUMMARY_ID = 0;
    static final int COL_SUMMARY_TYPE = 1;
    static final int COL_SUMMARY_DETAIL = 2;

    private static final String ACTIVITY_SORT =
            AppContract.ActivitiesEntry.COLUMN_TIME + " ASC, " +
                    AppContract.ActivitiesEntry.COLUMN_ACTIVITY_TYPE + " ASC ";

    public interface Callback {
    }

    public ActivitiesFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        dPosition = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activities, container, false);

        activitiesListView = (ListView) rootView.findViewById(R.id.activities_listview);
        activityFilterDate = (EditText) rootView.findViewById(R.id.activities_filter_date);
        activityFilterType = (Spinner) rootView.findViewById(R.id.activity_filter);
        tvEmptyLoading = (TextView) rootView.findViewById(R.id.text_empty_loading);
        activitiesSummary = (LinearLayout) rootView.findViewById(R.id.activities_summary);

        activitiesListAdapter = new ActivitiesAdapter(getActivity(),null,0);
        activitiesListView.setAdapter(activitiesListAdapter);

        activityFilterDate.setInputType(InputType.TYPE_NULL);
        if(ACTIVITIES_DATE.isEmpty()){
            ACTIVITIES_DATE = new Utilities(getActivity()).getCurrentDateDisp();
        }
        activityFilterDate.setText(ACTIVITIES_DATE);

        activityFilterType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                refreshData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        activityFilterDate.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                new Utilities(getActivity()).resetFocus(activityFilterDate);
                ACTIVITIES_DATE = activityFilterDate.getText().toString();
                refreshData();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        SetDateEditText setActivityFilterDate = new SetDateEditText(activityFilterDate, getActivity());

        activitiesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                //need to handle scroll to previous position when returning from youtube/share
                dPosition = activitiesListView.getScrollY();
                Log.v(TAG,"setOnItemClickListener - dPosition " + dPosition);

                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                switch(cursor.getString(COL_ACTIVITY_TYPE)){
                    case ACTIVITY_TYPE_FEEDING:
                        FeedingFragment.FEEDING_ID = cursor.getLong(COL_ACTIVITY_ID);
                        ((MainActivity) getActivity()).handleFragments(new FeedingFragment(),FeedingFragment.TAG,FeedingFragment.KEEP_IN_STACK);
                        break;
                    case ACTIVITY_TYPE_DIAPER:
                        DiaperFragment.DIAPER_ID = cursor.getLong(COL_ACTIVITY_ID);
                        ((MainActivity) getActivity()).handleFragments(new DiaperFragment(),DiaperFragment.TAG,DiaperFragment.KEEP_IN_STACK);
                        break;
                    case ACTIVITY_TYPE_SLEEPING:
                        SleepingFragment.SLEEPING_ID = cursor.getLong(COL_ACTIVITY_ID);
                        ((MainActivity) getActivity()).handleFragments(new SleepingFragment(),SleepingFragment.TAG,SleepingFragment.KEEP_IN_STACK);
                        break;
                    case ACTIVITY_TYPE_HEALTH:
                        HealthFragment.HEALTH_ID = cursor.getLong(COL_ACTIVITY_ID);
                        ((MainActivity) getActivity()).handleFragments(new HealthFragment(),HealthFragment.TAG,HealthFragment.KEEP_IN_STACK);
                        break;
                    default:
                        break;
                }

            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.v(TAG, "onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu,inflater);
        ((MainActivity) getActivity()).disableActionEditMenus();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        Log.v(TAG, "onPrepareOptionsMenu");
        super.onPrepareOptionsMenu(menu);
    }

    public void onResume()
    {
        super.onResume();
        ((MainActivity) getActivity()).updateToolbarTitle(MainActivity.ACTIVE_BABY_NAME);
        getLoaderManager().initLoader(ACTIVITIES_LOADER, null, this);
    }

    //check which loader is initiated and get appropriate cursor using content provider
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.v(TAG, "onCreateLoader - " + i + " loader");
        new Utilities(getActivity()).updateEmptyLoadingGone(Utilities.LIST_LOADING,tvEmptyLoading,"");

        Uri buildActivitiesUri = AppContract.ActivitiesEntry.buildActivitiesByUserIdBabyIdUri(MainActivity.LOGGED_IN_USER_ID,MainActivity.ACTIVE_BABY_ID,
                new Utilities().convDateDisp2Db(activityFilterDate.getText().toString()),activityFilterType.getSelectedItem().toString());

        return new CursorLoader(getActivity(),
                buildActivitiesUri,
                ACTIVITY_COLUMNS,
                null,
                null,
                ACTIVITY_SORT);
    }

    //check which loader has completed and use the data accordingly
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.v(TAG, "onLoadFinished - " + loader.getId() + " loader - " + cursor.getCount() + " rows retrieved");
        activitiesListAdapter.swapCursor(null);
        activitiesSummary.removeAllViews();
        if(cursor != null){
            if (cursor.getCount() > 0) {
                new Utilities(getActivity()).updateEmptyLoadingGone(Utilities.LIST_OK,tvEmptyLoading,"");

                Uri query_summary_uri = AppContract.ActivitiesEntry.buildActivitiesSummaryByUserIdBabyIdUri(MainActivity.LOGGED_IN_USER_ID,MainActivity.ACTIVE_BABY_ID,new Utilities().convDateDisp2Db(activityFilterDate.getText().toString()),activityFilterType.getSelectedItem().toString());
                Cursor summaryCursor = getActivity().getContentResolver().query(query_summary_uri,SUMMARY_COLUMNS,null,null,null);
                if(summaryCursor != null){
                    if(summaryCursor.getCount() > 0){
                        Log.v(TAG, "summaryCursor " + summaryCursor.getCount());
                        while(summaryCursor.moveToNext()) {
                            if(summaryCursor.getString(COL_SUMMARY_TYPE) != null & summaryCursor.getString(COL_SUMMARY_DETAIL) != null) {
                                Log.v(TAG, "summaryCursor " + summaryCursor.getString(COL_SUMMARY_TYPE) + " " + summaryCursor.getString(COL_SUMMARY_DETAIL));
                                TextView summaryInfo = new TextView(getActivity());
                                summaryInfo.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                summaryInfo.setText(summaryCursor.getString(COL_SUMMARY_TYPE) + " " + summaryCursor.getString(COL_SUMMARY_DETAIL));
                                activitiesSummary.addView(summaryInfo);
                            }
                        }
                    }
                    summaryCursor.close();
                }

                activitiesListAdapter.swapCursor(cursor);
            }else{
                new Utilities(getActivity()).updateEmptyLoadingGone(Utilities.LIST_EMPTY,tvEmptyLoading,getString(R.string.text_activity_list_empty));
            }
        }else{
            new Utilities(getActivity()).updateEmptyLoadingGone(Utilities.LIST_EMPTY,tvEmptyLoading,getString(R.string.text_activity_list_empty));
        }

        //scroll to top, after listview are loaded it focuses on listview
        Log.v(TAG, "onLoadFinished - dPosition " + dPosition);
        if(dPosition > 0) {
            activitiesListView.scrollTo(0, dPosition);
        }else{
            activitiesListView.scrollTo(0, 0);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        activitiesListAdapter.swapCursor(null);
    }

    private void refreshData(){
        getLoaderManager().restartLoader(ACTIVITIES_LOADER, null, this);
        //activitiesListAdapter.notifyDataSetChanged();
    }

}
