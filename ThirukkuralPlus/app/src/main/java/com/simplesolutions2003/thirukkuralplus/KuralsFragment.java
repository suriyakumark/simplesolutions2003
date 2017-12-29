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
import com.simplesolutions2003.thirukkuralplus.data.AppContract.KuralsEntry;
import com.simplesolutions2003.thirukkuralplus.data.AppContract.ChaptersEntry;
import com.simplesolutions2003.thirukkuralplus.data.AppContract.GroupsEntry;
import com.simplesolutions2003.thirukkuralplus.data.AppContract.SectionsEntry;

/**
 * Created by SuriyaKumar on 8/20/2016.
 */
public class KuralsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    public final static boolean KEEP_IN_STACK = true;
    public final static String TAG = KuralsFragment.class.getSimpleName();

    private final static int KURALS_LOADER = 0;
    public static long CHAPTER_ID = -1;
    public static String SEARCH_TEXT = "";
    public static int dPosition;

    private MenuAdapter menuAdapter;
    RecyclerView appMenuRecyclerView;
    private ContentObserver mObserver;


    public static final String[] KURALS_COLUMNS = {
            KuralsEntry.TABLE_NAME + "." + KuralsEntry._ID,
            KuralsEntry.TABLE_NAME + "." + KuralsEntry.COLUMN_CHAPTER_ID,
            KuralsEntry.TABLE_NAME + "." + KuralsEntry.COLUMN_NAME,
            KuralsEntry.TABLE_NAME + "." + KuralsEntry.COLUMN_NAME_ENG,
            KuralsEntry.TABLE_NAME + "." + KuralsEntry.COLUMN_EXP_MUVA,
            KuralsEntry.TABLE_NAME + "." + KuralsEntry.COLUMN_EXP_SOLO,
            KuralsEntry.TABLE_NAME + "." + KuralsEntry.COLUMN_EXP_MUKA,
            KuralsEntry.TABLE_NAME + "." + KuralsEntry.COLUMN_COUPLET,
            KuralsEntry.TABLE_NAME + "." + KuralsEntry.COLUMN_TRANS,
            KuralsEntry.TABLE_NAME + "." + KuralsEntry.COLUMN_FAVORITE,
            KuralsEntry.TABLE_NAME + "." + KuralsEntry.COLUMN_READ,
            ChaptersEntry.TABLE_NAME + "." + ChaptersEntry.COLUMN_NAME,
            ChaptersEntry.TABLE_NAME + "." + ChaptersEntry.COLUMN_NAME_ENG,
            SectionsEntry.TABLE_NAME + "." + SectionsEntry._ID,
            SectionsEntry.TABLE_NAME + "." + SectionsEntry.COLUMN_NAME,
            SectionsEntry.TABLE_NAME + "." + SectionsEntry.COLUMN_NAME_ENG,
            GroupsEntry.TABLE_NAME + "." + GroupsEntry._ID,
            GroupsEntry.TABLE_NAME + "." + GroupsEntry.COLUMN_NAME,
            GroupsEntry.TABLE_NAME + "." + GroupsEntry.COLUMN_NAME_ENG
    };


    static final int COL_KURALS_ID = 0;
    static final int COL_KURALS_CHAPTER_ID = 1;
    static final int COL_KURALS_NAME = 2;
    static final int COL_KURALS_NAME_ENG = 3;
    static final int COL_KURALS_EXP_MUVA = 4;
    static final int COL_KURALS_EXP_SOLO = 5;
    static final int COL_KURALS_EXP_MUKA = 6;
    static final int COL_KURALS_COUPLET = 7;
    static final int COL_KURALS_TRANS = 8;
    static final int COL_KURALS_FAV = 9;
    static final int COL_KURALS_READ = 10;
    static final int COL_CHAPTER_NAME = 11;
    static final int COL_CHAPTER_NAME_ENG = 12;
    static final int COL_SECTION_ID = 13;
    static final int COL_SECTION_NAME = 14;
    static final int COL_SECTION_NAME_ENG = 15;
    static final int COL_GROUP_ID = 16;
    static final int COL_GROUP_NAME = 17;
    static final int COL_GROUP_NAME_ENG = 18;

    public KuralsFragment(){}

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
            appMenuHeading.setText(getString(R.string.nav_kurals_eng));
        }else{
            appMenuHeading.setText(getString(R.string.nav_kurals));
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
        getLoaderManager().initLoader(KURALS_LOADER, null, this);
    }

    //check which loader is initiated and get appropriate cursor using content provider
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.v(TAG, "onCreateLoader - " + i + " loader");
        Uri buildMenu;
        if(CHAPTER_ID > -1){
            buildMenu = KuralsEntry.buildKuralsByChapterUri(CHAPTER_ID);
        }else if(!SEARCH_TEXT.isEmpty()){
            buildMenu = KuralsEntry.buildKuralsBySearchUri(SEARCH_TEXT);
        }else {
            buildMenu = KuralsEntry.buildKuralsByFavoritesUri();
        }

        return new CursorLoader(getActivity(),
                buildMenu,
                KURALS_COLUMNS,
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
                menuAdapter.setAdapterType(MenuAdapter.ADAPTER_TYPE_KURALS);
                appMenuRecyclerView.setAdapter(menuAdapter);
                int columnCount = 1;
                StaggeredGridLayoutManager sglm =
                        new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
                appMenuRecyclerView.setLayoutManager(sglm);
                if(dPosition > 0 ){
                    appMenuRecyclerView.smoothScrollToPosition(dPosition);
                }
            }else{
                appMenuRecyclerView.setAdapter(null);
            }
        }else{
            appMenuRecyclerView.setAdapter(null);
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
