package com.simplesolutions2003.onceuponatime;

import android.content.ContentValues;
import android.content.Context;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
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
    public static ImageButton articleFavorite;
    public static boolean bFavorite = false;

    private static final String[] ARTICLE_COLUMNS = {
            AppContract.ArticleEntry.TABLE_NAME + "." + AppContract.ArticleEntry._ID,
            AppContract.ArticleEntry.TABLE_NAME + "." + AppContract.ArticleEntry.COLUMN_TYPE,
            AppContract.ArticleEntry.TABLE_NAME + "." + AppContract.ArticleEntry.COLUMN_CATEGORY,
            AppContract.ArticleEntry.TABLE_NAME + "." + AppContract.ArticleEntry.COLUMN_TITLE,
            AppContract.ArticleDetailEntry.TABLE_NAME + "." + AppContract.ArticleDetailEntry.COLUMN_SEQUENCE,
            AppContract.ArticleDetailEntry.TABLE_NAME + "." + AppContract.ArticleDetailEntry.COLUMN_TYPE,
            AppContract.ArticleDetailEntry.TABLE_NAME + "." + AppContract.ArticleDetailEntry.COLUMN_CONTENT
    };

    private static final String[] FAVORITE_COLUMNS = {
            AppContract.FavoriteEntry.TABLE_NAME + "." + AppContract.FavoriteEntry._ID,
            AppContract.FavoriteEntry.TABLE_NAME + "." + AppContract.FavoriteEntry.COLUMN_ARTICLE_ID
    };

    static final int COL_ARTICLE_ID = 0;
    static final int COL_ARTICLE_TYPE = 1;
    static final int COL_ARTICLE_CATEGORY = 2;
    static final int COL_ARTICLE_TITLE = 3;
    static final int COL_ARTICLE_DETAIL_SEQUENCE = 4;
    static final int COL_ARTICLE_DETAIL_TYPE = 5;
    static final int COL_ARTICLE_DETAIL_CONTENT = 6;

    static final int COL_FAVORITE_ID = 0;
    static final int COL_FAVORITE_ARTICLE_ID = 1;

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
        articleFavorite = (ImageButton) rootView.findViewById(R.id.favorite);

        updateFavorite();

        articleFavorite.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!bFavorite) {
                    ContentValues favoriteValues = new ContentValues();
                    favoriteValues.put(AppContract.FavoriteEntry.COLUMN_ARTICLE_ID, Long.toString(ARTICLE_ID));
                    ((MainActivity) getActivity()).getContentResolver().insert(AppContract.FavoriteEntry.CONTENT_URI, favoriteValues);
                }else{
                    ContentValues favoriteValues = new ContentValues();
                    favoriteValues.put(AppContract.FavoriteEntry.COLUMN_ARTICLE_ID, Long.toString(ARTICLE_ID));
                    ((MainActivity) getActivity()).getContentResolver().delete(AppContract.FavoriteEntry.CONTENT_URI,
                            AppContract.FavoriteEntry.TABLE_NAME +
                                    "." + AppContract.FavoriteEntry.COLUMN_ARTICLE_ID + " = ? ",
                            new String[]{Long.toString(ARTICLE_ID)});
                }
                updateFavorite();
            }
        });

        articleDetailListAdapter = new ArticleDetailAdapter(getActivity(),null,0);
        articleDetailListView.setAdapter(articleDetailListAdapter);
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

    @Override
    public void onResume()
    {
        super.onResume();
        new Utilities().checkUserAdStatus(this.getContext());
        ActionBar action = ((AppCompatActivity) getActivity()).getSupportActionBar(); //get the actionbar
        action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
        action.setDisplayShowTitleEnabled(true); //show the title in the action bar
        hideKeyboard();

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
                articleDetailTitle.setText(cursor.getString(ArticleDetailFragment.COL_ARTICLE_TITLE));
                articleDetailTitle.setContentDescription(ArticleDetailFragment.articleDetailTitle.getText().toString());
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

    public void hideKeyboard(){
        Utilities.hideKeyboardFrom(this.getContext(),this.getView().getRootView());
    }


    public void updateFavorite(){
        Cursor favoriteCursor = ((MainActivity) getActivity()).getContentResolver().query(AppContract.FavoriteEntry.CONTENT_URI,
                FAVORITE_COLUMNS,
                AppContract.FavoriteEntry.TABLE_NAME +
                        "." + AppContract.FavoriteEntry.COLUMN_ARTICLE_ID + " = ? ",
                new String[]{Long.toString(ARTICLE_ID)},null);

        bFavorite = false;

        if(favoriteCursor != null){
            if(favoriteCursor.getCount() > 0){
                bFavorite = true;

            }
        }


        if(bFavorite){
            articleFavorite.setImageResource(R.drawable.fav_on);
        }else{
            articleFavorite.setImageResource(R.drawable.fav_off);
        }
    }

}
