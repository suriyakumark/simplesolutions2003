package com.simplesolutions2003.onceuponatime.data;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.audiofx.BassBoost;
import android.util.Log;

import com.simplesolutions2003.onceuponatime.data.AppContract.MenuEntry;
import com.simplesolutions2003.onceuponatime.data.AppContract.ArticleEntry;
import com.simplesolutions2003.onceuponatime.data.AppContract.ArticleDetailEntry;
import com.simplesolutions2003.onceuponatime.data.AppContract.FavoriteEntry;

/**
 * Created by SuriyaKumar on 8/16/2016.
 */
public class AppDBHelper extends SQLiteOpenHelper  {
    private final String LOG_TAG = AppDBHelper.class.getSimpleName();

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "onceuponatime.db";

    public AppDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.v(LOG_TAG,"AppDBHelper");

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.v(LOG_TAG,"onCreate");

        final String SQL_CREATE_MENU_TABLE = "CREATE TABLE " + MenuEntry.TABLE_NAME + " (" +
                MenuEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MenuEntry.COLUMN_MENU + " TEXT NOT NULL, " +
                MenuEntry.COLUMN_DESC + " TEXT NOT NULL, " +
                MenuEntry.COLUMN_COVER_PIC + " TEXT, " +
                " UNIQUE (" + MenuEntry._ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_ARTICLE_TABLE = "CREATE TABLE " + ArticleEntry.TABLE_NAME + " (" +
                ArticleEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ArticleEntry.COLUMN_TYPE + " TEXT NOT NULL, " +
                ArticleEntry.COLUMN_CATEGORY + " TEXT NOT NULL, " +
                ArticleEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                ArticleEntry.COLUMN_COVER_PIC + " TEXT, " +
                ArticleEntry.COLUMN_AUTHOR + " TEXT, " +
                ArticleEntry.COLUMN_NEW + " TEXT, " +
                ArticleEntry.COLUMN_LAST_UPDATED_TS + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                ArticleEntry.COLUMN_BOOKMARK + " INTEGER, " +

                " UNIQUE (" + ArticleEntry._ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_ARTICLE_DETAIL_TABLE = "CREATE TABLE " + ArticleDetailEntry.TABLE_NAME + " (" +
                ArticleEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ArticleDetailEntry.COLUMN_ARTICLE_ID + " INTEGER," +
                ArticleDetailEntry.COLUMN_SEQUENCE + " INTEGER, " +
                ArticleDetailEntry.COLUMN_TYPE + " TEXT, " +
                ArticleDetailEntry.COLUMN_CONTENT + " TEXT, " +

                " UNIQUE (" + ArticleDetailEntry._ID + ") ON CONFLICT REPLACE," +

                " UNIQUE (" + ArticleDetailEntry.COLUMN_ARTICLE_ID + "," +
                ArticleDetailEntry.COLUMN_SEQUENCE + ") ON CONFLICT REPLACE," +

                " FOREIGN KEY (" + ArticleDetailEntry.COLUMN_ARTICLE_ID + ") REFERENCES " +
                ArticleEntry.TABLE_NAME + " (" + ArticleEntry._ID + "));";

        final String SQL_CREATE_FAVORITE_TABLE = "CREATE TABLE " + FavoriteEntry.TABLE_NAME + " (" +
                FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavoriteEntry.COLUMN_ARTICLE_ID + " INTEGER, " +

                " UNIQUE (" + FavoriteEntry._ID + ") ON CONFLICT REPLACE, " +

                " UNIQUE (" + FavoriteEntry.COLUMN_ARTICLE_ID + ") ON CONFLICT REPLACE," +

                " FOREIGN KEY (" + FavoriteEntry.COLUMN_ARTICLE_ID + ") REFERENCES " +
                        ArticleEntry.TABLE_NAME + " (" + ArticleEntry._ID + "));";

        Log.v(LOG_TAG, "SQL_CREATE_MENU_TABLE" + SQL_CREATE_MENU_TABLE);
        Log.v(LOG_TAG, "SQL_CREATE_ARTICLE_TABLE" + SQL_CREATE_ARTICLE_TABLE);
        Log.v(LOG_TAG, "SQL_CREATE_ARTICLE_DETAIL_TABLE" + SQL_CREATE_ARTICLE_DETAIL_TABLE);
        Log.v(LOG_TAG, "SQL_CREATE_FAVORITE_TABLE" + SQL_CREATE_FAVORITE_TABLE);

        sqLiteDatabase.execSQL(SQL_CREATE_MENU_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ARTICLE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ARTICLE_DETAIL_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_TABLE);

        ContentValues menuCV = new ContentValues();
        menuCV.put(MenuEntry._ID, 1);
        menuCV.put(MenuEntry.COLUMN_MENU, "Short Stories");
        menuCV.put(MenuEntry.COLUMN_DESC, "Every short story has a moral for the children to learn. ");
        menuCV.put(MenuEntry.COLUMN_COVER_PIC, "menu_01");
        sqLiteDatabase.insert(MenuEntry.TABLE_NAME, null, menuCV);

        menuCV.put(MenuEntry._ID, 2);
        menuCV.put(MenuEntry.COLUMN_MENU, "Stories");
        menuCV.put(MenuEntry.COLUMN_DESC, "Interesting and adventurous stories for children. ");
        menuCV.put(MenuEntry.COLUMN_COVER_PIC, "menu_02");
        sqLiteDatabase.insert(MenuEntry.TABLE_NAME, null, menuCV);

        menuCV.put(MenuEntry._ID, 3);
        menuCV.put(MenuEntry.COLUMN_MENU, "Rhymes");
        menuCV.put(MenuEntry.COLUMN_DESC, "Nursery Rhymes help children learn with fun and joy! ");
        menuCV.put(MenuEntry.COLUMN_COVER_PIC, "menu_03");
        sqLiteDatabase.insert(MenuEntry.TABLE_NAME, null, menuCV);

        menuCV.put(MenuEntry._ID, 4);
        menuCV.put(MenuEntry.COLUMN_MENU, "Poems");
        menuCV.put(MenuEntry.COLUMN_DESC, "Collection of the most popular poems written by famous poets.");
        menuCV.put(MenuEntry.COLUMN_COVER_PIC, "menu_04");
        sqLiteDatabase.insert(MenuEntry.TABLE_NAME, null, menuCV);

        menuCV.put(MenuEntry._ID, 5);
        menuCV.put(MenuEntry.COLUMN_MENU, "Favorites");
        menuCV.put(MenuEntry.COLUMN_DESC, "Quickly access your favorite stories, rhymes, poems.");
        menuCV.put(MenuEntry.COLUMN_COVER_PIC, "menu_05");
        sqLiteDatabase.insert(MenuEntry.TABLE_NAME, null, menuCV);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.v(LOG_TAG,"onUpgrade");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MenuEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ArticleEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ArticleDetailEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
