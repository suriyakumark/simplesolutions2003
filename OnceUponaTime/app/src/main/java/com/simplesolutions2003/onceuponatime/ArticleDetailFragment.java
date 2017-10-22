package com.simplesolutions2003.onceuponatime;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.simplesolutions2003.onceuponatime.data.AppContract;

/**
 * Created by SuriyaKumar on 8/20/2016.
 */
public class ArticleDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    public final static boolean KEEP_IN_STACK = false;
    public final static String TAG = ArticleDetailFragment.class.getSimpleName();

    private final static int ARTICLE_DETAIL_LOADER = 0;
    private int dPosition;
    private ArticleDetailAdapter articleDetailListAdapter;
    public static long ARTICLE_ID = -1;
    public static String ARTICLE_TITLE = "";
    ListView articleDetailListView;
    public static TextView articleDetailTitle;

    private static final String[] ARTICLE_COLUMNS = {
            AppContract.ArticleEntry.TABLE_NAME + "." + AppContract.ArticleEntry._ID,
            AppContract.ArticleEntry.TABLE_NAME + "." + AppContract.ArticleEntry.COLUMN_TYPE,
            AppContract.ArticleEntry.TABLE_NAME + "." + AppContract.ArticleEntry.COLUMN_CATEGORY,
            AppContract.ArticleEntry.TABLE_NAME + "." + AppContract.ArticleEntry.COLUMN_TITLE,
            AppContract.ArticleDetailEntry.TABLE_NAME + "." + AppContract.ArticleDetailEntry.COLUMN_SEQUENCE,
            AppContract.ArticleDetailEntry.TABLE_NAME + "." + AppContract.ArticleDetailEntry.COLUMN_TYPE,
            AppContract.ArticleDetailEntry.TABLE_NAME + "." + AppContract.ArticleDetailEntry.COLUMN_CONTENT
    };


    static final int COL_ARTICLE_ID = 0;
    static final int COL_ARTICLE_TYPE = 1;
    static final int COL_ARTICLE_CATEGORY = 2;
    static final int COL_ARTICLE_TITLE = 3;
    static final int COL_ARTICLE_DETAIL_SEQUENCE = 4;
    static final int COL_ARTICLE_DETAIL_TYPE = 5;
    static final int COL_ARTICLE_DETAIL_CONTENT = 6;

    public interface Callback {
    }

    public ArticleDetailFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        dPosition = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.article_detail, container, false);
        articleDetailListView = (ListView) rootView.findViewById(R.id.article_detail_listview);
        articleDetailTitle = (TextView) rootView.findViewById(R.id.article_detail_title);
        articleDetailListAdapter = new ArticleDetailAdapter(getActivity(),null,0);
        articleDetailListView.setAdapter(articleDetailListAdapter);
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
        getLoaderManager().initLoader(ARTICLE_DETAIL_LOADER, null, this);
    }

    //check which loader is initiated and get appropriate cursor using content provider
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.v(TAG, "onCreateLoader - " + i + " loader");

        Uri buildArticle = AppContract.ArticleDetailEntry.buildArticleWithDetailByArticleIdUri(ARTICLE_ID);

        return new CursorLoader(getActivity(),
                buildArticle,
                ARTICLE_COLUMNS,
                null,
                null,
                null);

    }

    //check which loader has completed and use the data accordingly
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.v(TAG, "onLoadFinished - " + loader.getId() + " loader - " + cursor.getCount() + " rows retrieved");
        if(cursor != null){
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                articleDetailListAdapter.swapCursor(cursor);
            }
        }

        //scroll to top, after listview are loaded it focuses on listview
        Log.v(TAG, "onLoadFinished - dPosition " + dPosition);
        if(dPosition > 0) {
            articleDetailListView.scrollTo(0, dPosition);
        }else{
            articleDetailListView.scrollTo(0, 0);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        articleDetailListAdapter.swapCursor(null);
    }

}
