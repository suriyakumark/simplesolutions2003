package com.simplesolutions2003.onceuponatime;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.TextView;

import com.simplesolutions2003.onceuponatime.data.AppContract;

/**
 * Created by SuriyaKumar on 8/20/2016.
 */
public class ArticlesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    public final static boolean KEEP_IN_STACK = true;
    public final static String TAG = ArticlesFragment.class.getSimpleName();

    private final static int ARTICLES_LOADER = 0;
    public static int dPosition;
    private ArticlesAdapter articlesAdapter;
    public static String ARTICLE_TYPE = "";
    RecyclerView articlesRecyclerView;
    TextView tvEmptyLoading;
    private ContentObserver mObserver;


    public static final String[] ARTICLE_COLUMNS = {
            AppContract.ArticleEntry.TABLE_NAME + "." + AppContract.ArticleEntry._ID,
            AppContract.ArticleEntry.TABLE_NAME + "." + AppContract.ArticleEntry.COLUMN_TYPE,
            AppContract.ArticleEntry.TABLE_NAME + "." + AppContract.ArticleEntry.COLUMN_CATEGORY,
            AppContract.ArticleEntry.TABLE_NAME + "." + AppContract.ArticleEntry.COLUMN_TITLE,
            AppContract.ArticleEntry.TABLE_NAME + "." + AppContract.ArticleEntry.COLUMN_COVER_PIC,
            AppContract.ArticleEntry.TABLE_NAME + "." + AppContract.ArticleEntry.COLUMN_AUTHOR,
            AppContract.ArticleEntry.TABLE_NAME + "." + AppContract.ArticleEntry.COLUMN_NEW,
            AppContract.ArticleEntry.TABLE_NAME + "." + AppContract.ArticleEntry.COLUMN_LAST_UPDATED_TS
    };


    static final int COL_ARTICLE_ID = 0;
    static final int COL_ARTICLE_TYPE = 1;
    static final int COL_ARTICLE_CATEGORY = 2;
    static final int COL_ARTICLE_TITLE = 3;
    static final int COL_ARTICLE_COVER_PIC = 4;
    static final int COL_ARTICLE_AUTHOR = 5;
    static final int COL_ARTICLE_NEW = 6;
    public static final int COL_ARTICLE_LAST_UPD_TS = 7;


    public interface Callback {
    }

    public ArticlesFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        dPosition = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.articles, container, false);
        tvEmptyLoading = (TextView) rootView.findViewById(R.id.text_empty_loading);
        articlesRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        getLoaderManager().initLoader(0, null, this);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.v(TAG, "onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu,inflater);
        if(ARTICLE_TYPE.equals("favorites")){
            MainActivity.bSearchEnabled = false;
        }else {
            MainActivity.bSearchEnabled = true;
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        Log.v(TAG, "onPrepareOptionsMenu");
        super.onPrepareOptionsMenu(menu);
    }

    public void onResume() {
        Log.v(TAG, "onResume");
        super.onResume();
        hideKeyboard();
        getLoaderManager().restartLoader(ARTICLES_LOADER, null, this);
    }

    //check which loader is initiated and get appropriate cursor using content provider
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.v(TAG, "onCreateLoader - " + i + " loader");
        new Utilities(getActivity()).updateEmptyLoadingGone(Utilities.LIST_LOADING,tvEmptyLoading,"");

        Uri buildArticle = AppContract.ArticleEntry.buildArticleByTypeSearchUri(ARTICLE_TYPE, MainActivity.search_text);

        if(ARTICLE_TYPE.equals("favorites")) {
            buildArticle = AppContract.FavoriteEntry.buildArticleByFavoriteUri();
        }

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
            if(cursor.getCount() > 0){
                articlesAdapter = new ArticlesAdapter(getActivity(),cursor,0);
                articlesAdapter.setHasStableIds(true);
                articlesRecyclerView.setAdapter(articlesAdapter);
                int columnCount = 1;
                if(ARTICLE_TYPE.equals("short stories")){
                    columnCount = 2;
                }else if(ARTICLE_TYPE.equals("stories")){
                    columnCount = 2;
                }

                StaggeredGridLayoutManager sglm =
                        new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
                articlesRecyclerView.setLayoutManager(sglm);
                if(dPosition > 0 ){
                    articlesRecyclerView.smoothScrollToPosition(dPosition);
                }
                new Utilities(getActivity()).updateEmptyLoadingGone(Utilities.LIST_OK,tvEmptyLoading,"");
            }else{
                handleEmptyMessage();
            }
        }else{
            handleEmptyMessage();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        articlesRecyclerView.setAdapter(null);
    }

    public void handleEmptyMessage(){
        articlesRecyclerView.setAdapter(null);
        if(ARTICLE_TYPE.equals("favorites")){
            new Utilities(getActivity()).updateEmptyLoadingGone(Utilities.LIST_EMPTY, tvEmptyLoading, getString(R.string.text_articles_search_empty));
        }else if(new Utilities(getActivity()).isInternetOn()) {
            if(MainActivity.search_text.isEmpty() || MainActivity.search_text == "") {
                new Utilities(getActivity()).updateEmptyLoadingGone(Utilities.LIST_EMPTY, tvEmptyLoading, getString(R.string.text_articles_sync_progress));
            }else {
                new Utilities(getActivity()).updateEmptyLoadingGone(Utilities.LIST_EMPTY, tvEmptyLoading, getString(R.string.text_articles_search_empty));
            }
        }else{
            new Utilities(getActivity()).updateEmptyLoadingGone(Utilities.LIST_EMPTY, tvEmptyLoading, getString(R.string.text_articles_no_data));
        }
    }

    public void hideKeyboard(){
        Utilities.hideKeyboardFrom(this.getContext(),this.getView().getRootView());
    }

}
