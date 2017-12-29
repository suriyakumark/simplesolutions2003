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

import com.simplesolutions2003.thirukkuralplus.data.AppContract;
import com.simplesolutions2003.thirukkuralplus.data.AppContract.ChaptersEntry;
import com.simplesolutions2003.thirukkuralplus.data.AppContract.SectionsEntry;
import com.simplesolutions2003.thirukkuralplus.data.AppContract.GroupsEntry;

/**
 * Created by SuriyaKumar on 8/20/2016.
 */
public class ChaptersFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    public final static boolean KEEP_IN_STACK = true;
    public final static String TAG = ChaptersFragment.class.getSimpleName();

    private final static int CHAPTERS_LOADER = 0;

    public static String MODE_READ =  "read";
    public static String MODE_QUIZ =  "quiz";
    public static String MODE =  MODE_READ;

    public static long GROUP_ID = -1;
    public static long SECTION_ID = -1;
    public static int dPosition;

    private MenuAdapter menuAdapter;
    RecyclerView appMenuRecyclerView;
    private ContentObserver mObserver;


    public static final String[] CHAPTERS_COLUMNS = {
            ChaptersEntry.TABLE_NAME + "." + ChaptersEntry._ID,
            ChaptersEntry.TABLE_NAME + "." + ChaptersEntry.COLUMN_SECTION_ID,
            ChaptersEntry.TABLE_NAME + "." + ChaptersEntry.COLUMN_GROUP_ID,
            ChaptersEntry.TABLE_NAME + "." + ChaptersEntry.COLUMN_NAME,
            ChaptersEntry.TABLE_NAME + "." + ChaptersEntry.COLUMN_NAME_ENG,
            ChaptersEntry.TABLE_NAME + "." + ChaptersEntry.COLUMN_ICON
    };

    public static final String[] CHAPTERS_COLUMNS_MORE = {
            ChaptersEntry.TABLE_NAME + "." + ChaptersEntry._ID,
            ChaptersEntry.TABLE_NAME + "." + ChaptersEntry.COLUMN_SECTION_ID,
            ChaptersEntry.TABLE_NAME + "." + ChaptersEntry.COLUMN_GROUP_ID,
            ChaptersEntry.TABLE_NAME + "." + ChaptersEntry.COLUMN_NAME,
            ChaptersEntry.TABLE_NAME + "." + ChaptersEntry.COLUMN_NAME_ENG,
            ChaptersEntry.TABLE_NAME + "." + ChaptersEntry.COLUMN_ICON,
            SectionsEntry.TABLE_NAME + "." + SectionsEntry.COLUMN_NAME,
            SectionsEntry.TABLE_NAME + "." + SectionsEntry.COLUMN_NAME_ENG,
            GroupsEntry.TABLE_NAME + "." + GroupsEntry.COLUMN_NAME,
            GroupsEntry.TABLE_NAME + "." + GroupsEntry.COLUMN_NAME_ENG
    };

    static final int COL_CHAPTERS_ID = 0;
    static final int COL_CHAPTERS_SECTION_ID = 1;
    static final int COL_CHAPTERS_GROUP_ID = 2;
    static final int COL_CHAPTERS_NAME = 3;
    static final int COL_CHAPTERS_NAME_ENG = 4;
    static final int COL_CHAPTERS_ICON = 5;
    static final int COL_SECTION_NAME = 6;
    static final int COL_SECTION_NAME_ENG = 7;
    static final int COL_GROUP_NAME = 8;
    static final int COL_GROUP_NAME_ENG = 9;

    public ChaptersFragment(){}

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
            appMenuHeading.setText(getString(R.string.nav_chapters_eng));
        }else{
            appMenuHeading.setText(getString(R.string.nav_chapters));
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
        getLoaderManager().initLoader(CHAPTERS_LOADER, null, this);
    }

    //check which loader is initiated and get appropriate cursor using content provider
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.v(TAG, "onCreateLoader - " + i + " loader");
        Uri buildMenu;
        if(SECTION_ID > -1){
            buildMenu = ChaptersEntry.buildChapterBySectionUri(SECTION_ID);
            return new CursorLoader(getActivity(),
                    buildMenu,
                    CHAPTERS_COLUMNS_MORE,
                    null,
                    null,
                    null);

        }else if(GROUP_ID > -1){
            buildMenu = ChaptersEntry.buildChapterByGroupUri(GROUP_ID);
            return new CursorLoader(getActivity(),
                    buildMenu,
                    CHAPTERS_COLUMNS_MORE,
                    null,
                    null,
                    null);
        }else {
            buildMenu = ChaptersEntry.CONTENT_URI;
            return new CursorLoader(getActivity(),
                    buildMenu,
                    CHAPTERS_COLUMNS,
                    null,
                    null,
                    null);
        }

    }

    //check which loader has completed and use the data accordingly
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.v(TAG, "onLoadFinished - " + loader.getId() + " loader - " + cursor.getCount() + " rows retrieved");

        if(cursor != null){
            if(cursor.getCount() > 0){
                menuAdapter = new MenuAdapter(getActivity(),cursor,0);
                menuAdapter.setHasStableIds(true);
                menuAdapter.setAdapterType(MenuAdapter.ADAPTER_TYPE_CHAPTERS);
                appMenuRecyclerView.setAdapter(menuAdapter);
                int columnCount = 1;
                StaggeredGridLayoutManager sglm =
                        new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
                appMenuRecyclerView.setLayoutManager(sglm);
                if(dPosition > 0 ){
                    appMenuRecyclerView.smoothScrollToPosition(dPosition);
                }
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
