package com.simplesolutions2003.happybabycare;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.simplesolutions2003.happybabycare.data.AppContract;

/**
 * Created by SuriyaKumar on 8/23/2016.
 */
public class BabyFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    public final static boolean KEEP_IN_STACK = true;
    public final static String TAG = BabyFragment.class.getSimpleName();
    public final static String TITLE_BABY = "Baby Profiles";

    private final static int BABY_LOADER = 0;
    private int dPosition;
    private BabyAdapter babyListAdapter;
    ListView babyListView;
    TextView tvEmptyLoading;

    private static final String[] BABY_COLUMNS = {
            AppContract.BabyEntry.TABLE_NAME + "." + AppContract.BabyEntry._ID,
            AppContract.BabyEntry.TABLE_NAME + "." + AppContract.BabyEntry.COLUMN_USER_ID,
            AppContract.BabyEntry.TABLE_NAME + "." + AppContract.BabyEntry.COLUMN_NAME,
            AppContract.BabyEntry.TABLE_NAME + "." + AppContract.BabyEntry.COLUMN_BIRTH_DATE,
            AppContract.BabyEntry.TABLE_NAME + "." + AppContract.BabyEntry.COLUMN_DUE_DATE,
            AppContract.BabyEntry.TABLE_NAME + "." + AppContract.BabyEntry.COLUMN_GENDER,
            AppContract.BabyEntry.TABLE_NAME + "." + AppContract.BabyEntry.COLUMN_PHOTO,
            AppContract.BabyEntry.TABLE_NAME + "." + AppContract.BabyEntry.COLUMN_ACTIVE
    };


    static final int COL_BABY_ID = 0;
    static final int COL_BABY_USER_ID = 1;
    static final int COL_BABY_NAME = 2;
    static final int COL_BABY_BIRTH_DATE = 3;
    static final int COL_BABY_DUE_DATE = 4;
    static final int COL_BABY_GENDER = 5;
    static final int COL_BABY_PHOTO = 6;
    static final int COL_BABY_ACTIVE = 7;

    public interface Callback {
    }

    public BabyFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        dPosition = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.baby, container, false);
        babyListView = (ListView) rootView.findViewById(R.id.baby_listview);

        babyListAdapter = new BabyAdapter(getActivity(),null,0);
        babyListView.setAdapter(babyListAdapter);
        tvEmptyLoading = (TextView) rootView.findViewById(R.id.text_empty_loading);
        babyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                dPosition = babyListView.getScrollY();
                Log.v(TAG,"setOnItemClickListener - dPosition " + dPosition);

                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                MainActivity.ACTIVE_BABY_ID = cursor.getLong(COL_BABY_ID);
                MainActivity.ACTIVE_BABY_NAME = cursor.getString(COL_BABY_NAME);
                ((MainActivity) getActivity()).handleFragments(new ActivitiesFragment(), ActivitiesFragment.TAG, ActivitiesFragment.KEEP_IN_STACK);

            }
        });

        return rootView;
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
            case R.id.action_add:
                MainActivity.ACTIVE_BABY_ID = -1;
                ((MainActivity) getActivity()).handleFragments(new BabyProfileFragment(),BabyProfileFragment.TAG,BabyProfileFragment.KEEP_IN_STACK);
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void validateInputs() {
        MainActivity.addMenuEnabled = true;
        //MainActivity.saveMenuEnabled = false;
        //MainActivity.deleteMenuEnabled = false;
    }

    public void onResume()
    {
        super.onResume();
        ((MainActivity) getActivity()).updateToolbarTitle(TITLE_BABY);
        getLoaderManager().initLoader(BABY_LOADER, null, this);
    }

    //check which loader is initiated and get appropriate cursor using content provider
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.v(TAG, "onCreateLoader - " + i + " loader");
        new Utilities(getActivity()).updateEmptyLoadingGone(Utilities.LIST_LOADING,tvEmptyLoading,"");
        Uri buildBaby = AppContract.BabyEntry.buildBabyByUserIdUri(MainActivity.LOGGED_IN_USER_ID);

        return new CursorLoader(getActivity(),
                buildBaby,
                BABY_COLUMNS,
                null,
                null,
                null);

    }

    //check which loader has completed and use the data accordingly
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.v(TAG, "onLoadFinished - " + loader.getId() + " loader - " + cursor.getCount() + " rows retrieved");

        if(cursor != null){
            if(cursor.getCount() > 0){
                babyListAdapter.swapCursor(cursor);
                new Utilities(getActivity()).updateEmptyLoadingGone(Utilities.LIST_OK,tvEmptyLoading,"");
            }else{
                new Utilities(getActivity()).updateEmptyLoadingGone(Utilities.LIST_EMPTY,tvEmptyLoading,getString(R.string.text_baby_list_empty));
            }
        }else{
            new Utilities(getActivity()).updateEmptyLoadingGone(Utilities.LIST_EMPTY,tvEmptyLoading,getString(R.string.text_baby_list_empty));
        }

        //scroll to top, after listview are loaded it focuses on listview
        Log.v(TAG, "onLoadFinished - dPosition " + dPosition);
        if(dPosition > 0) {
            babyListView.scrollTo(0, dPosition);
        }else{
            babyListView.scrollTo(0, 0);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        babyListAdapter.swapCursor(null);
    }

}
