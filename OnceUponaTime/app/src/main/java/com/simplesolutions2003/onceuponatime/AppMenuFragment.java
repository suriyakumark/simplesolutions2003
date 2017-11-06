package com.simplesolutions2003.onceuponatime;

import android.database.ContentObserver;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.simplesolutions2003.onceuponatime.data.AppContract;

/**
 * Created by SuriyaKumar on 8/20/2016.
 */
public class AppMenuFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    public final static boolean KEEP_IN_STACK = true;
    public final static String TAG = AppMenuFragment.class.getSimpleName();

    private final static int MENU_LOADER = 0;
    public static int dPosition;
    private AppMenuAdapter appMenuAdapter;
    RecyclerView appMenuRecyclerView;
    TextView tvEmptyLoading;
    private ContentObserver mObserver;


    public static final String[] MENU_COLUMNS = {
            AppContract.MenuEntry.TABLE_NAME + "." + AppContract.MenuEntry._ID,
            AppContract.MenuEntry.TABLE_NAME + "." + AppContract.MenuEntry.COLUMN_MENU,
            AppContract.MenuEntry.TABLE_NAME + "." + AppContract.MenuEntry.COLUMN_DESC,
            AppContract.MenuEntry.TABLE_NAME + "." + AppContract.MenuEntry.COLUMN_COVER_PIC
    };


    static final int COL_MENU_ID = 0;
    static final int COL_MENU_MENU = 1;
    static final int COL_MENU_DESC = 2;
    static final int COL_MENU_COVER_PIC = 3;

    public interface Callback {
    }

    public AppMenuFragment(){}

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
        tvEmptyLoading = (TextView) rootView.findViewById(R.id.text_empty_loading);
        appMenuRecyclerView = (RecyclerView) rootView.findViewById(R.id.app_menu_recycler_view);
        getLoaderManager().initLoader(0, null, this);
        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.v(TAG, "onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu,inflater);
        MainActivity.bSearchEnabled = false;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        Log.v(TAG, "onPrepareOptionsMenu");
        super.onPrepareOptionsMenu(menu);
    }

    public void onResume()
    {
        super.onResume();
        hideKeyboard();
        getLoaderManager().initLoader(MENU_LOADER, null, this);
    }

    //check which loader is initiated and get appropriate cursor using content provider
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.v(TAG, "onCreateLoader - " + i + " loader");
        new Utilities(getActivity()).updateEmptyLoadingGone(Utilities.LIST_LOADING,tvEmptyLoading,"");
        Uri buildMenu= AppContract.MenuEntry.CONTENT_URI;

        return new CursorLoader(getActivity(),
                buildMenu,
                MENU_COLUMNS,
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
                appMenuAdapter = new AppMenuAdapter(getActivity(),cursor,0);
                appMenuAdapter.setHasStableIds(true);
                appMenuRecyclerView.setAdapter(appMenuAdapter);
                int columnCount = 1;
                StaggeredGridLayoutManager sglm =
                        new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
                appMenuRecyclerView.setLayoutManager(sglm);
                //if(dPosition > 0 ){
                //    appMenuRecyclerView.smoothScrollToPosition(dPosition);
                //}
                new Utilities(getActivity()).updateEmptyLoadingGone(Utilities.LIST_OK,tvEmptyLoading,"");
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
