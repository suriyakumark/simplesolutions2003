package com.simplesolutions2003.thirukkuralplus;

import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.simplesolutions2003.thirukkuralplus.data.AppContract.GroupsEntry;

/**
 * Created by SuriyaKumar on 8/20/2016.
 */
public class GroupsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    public final static boolean KEEP_IN_STACK = true;
    public final static String TAG = GroupsFragment.class.getSimpleName();

    private final static int GROUPS_LOADER = 0;
    public static int dPosition;
    private MenuAdapter menuAdapter;
    RecyclerView appMenuRecyclerView;
    private ContentObserver mObserver;


    public static final String[] GROUPS_COLUMNS = {
            GroupsEntry.TABLE_NAME + "." + GroupsEntry._ID,
            GroupsEntry.TABLE_NAME + "." + GroupsEntry.COLUMN_NAME,
            GroupsEntry.TABLE_NAME + "." + GroupsEntry.COLUMN_NAME_ENG,
            GroupsEntry.TABLE_NAME + "." + GroupsEntry.COLUMN_ICON
    };


    static final int COL_GROUPS_ID = 0;
    static final int COL_GROUPS_NAME = 1;
    static final int COL_GROUPS_NAME_ENG = 2;
    static final int COL_GROUPS_ICON = 3;

    public GroupsFragment(){}

    public interface Callback {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        dPosition = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.app_menu, container, false);
        TextView appMenuHeading = (TextView) rootView.findViewById(R.id.app_menu_heading);
        if(new Utilities().isEnglishEnabled(this.getContext())) {
            appMenuHeading.setText(getString(R.string.nav_groups_eng));
        }else{
            appMenuHeading.setText(getString(R.string.nav_groups));
        }
        appMenuRecyclerView = (RecyclerView) rootView.findViewById(R.id.app_menu_recycler_view);
        getLoaderManager().initLoader(0, null, this);
        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.v(TAG, "onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        Log.v(TAG, "onPrepareOptionsMenu");
        super.onPrepareOptionsMenu(menu);
    }

    public void onResume()
    {
        super.onResume();
        MainActivity.CURRENT_FRAGMENT = TAG;
        hideKeyboard();
        getLoaderManager().initLoader(GROUPS_LOADER, null, this);
    }

    //check which loader is initiated and get appropriate cursor using content provider
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.v(TAG, "onCreateLoader - " + i + " loader");
        Uri buildMenu = GroupsEntry.CONTENT_URI;

        return new CursorLoader(getActivity(),
                buildMenu,
                GROUPS_COLUMNS,
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
                menuAdapter = new MenuAdapter(getActivity(),cursor,0);
                menuAdapter.setHasStableIds(true);
                menuAdapter.setAdapterType(MenuAdapter.ADAPTER_TYPE_GROUPS);
                appMenuRecyclerView.setAdapter(menuAdapter);
                int columnCount = 1;
                StaggeredGridLayoutManager sglm =
                        new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
                appMenuRecyclerView.setLayoutManager(sglm);
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        appMenuRecyclerView.setAdapter(null);
    }


    public void hideKeyboard(){
        Utilities.hideKeyboardFrom(this.getContext(),this.getView().getRootView());
    }
}
