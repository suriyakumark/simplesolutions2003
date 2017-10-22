package com.simplesolutions2003.onceuponatime.data;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.audiofx.BassBoost;
import android.util.Log;

import com.simplesolutions2003.onceuponatime.data.AppContract.ArticleEntry;
import com.simplesolutions2003.onceuponatime.data.AppContract.ArticleDetailEntry;

/**
 * Created by SuriyaKumar on 8/16/2016.
 */
public class AppDBHelper extends SQLiteOpenHelper  {
    private final String LOG_TAG = AppDBHelper.class.getSimpleName();

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "onceuponatime.db";

    public AppDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_ARTICLE_TABLE = "CREATE TABLE " + ArticleEntry.TABLE_NAME + " (" +
                ArticleEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ArticleEntry.COLUMN_TYPE + " TEXT NOT NULL, " +
                ArticleEntry.COLUMN_CATEGORY + " TEXT NOT NULL, " +
                ArticleEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                ArticleEntry.COLUMN_COVER_PIC + " TEXT, " +
                ArticleEntry.COLUMN_LAST_UPDATED_TS + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +

                " UNIQUE (" + ArticleEntry._ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_ARTICLE_DETAIL_TABLE = "CREATE TABLE " + ArticleDetailEntry.TABLE_NAME + " (" +
                ArticleEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ArticleDetailEntry.COLUMN_ARTICLE_ID + " INTEGER," +
                ArticleDetailEntry.COLUMN_SEQUENCE + " TEXT NOT NULL, " +
                ArticleDetailEntry.COLUMN_TYPE + " TEXT, " +
                ArticleDetailEntry.COLUMN_CONTENT + " TEXT, " +

                " UNIQUE (" + ArticleDetailEntry._ID + ") ON CONFLICT REPLACE," +

                " UNIQUE (" + ArticleDetailEntry.COLUMN_ARTICLE_ID + "," +
                ArticleDetailEntry.COLUMN_SEQUENCE + ") ON CONFLICT REPLACE," +

                " FOREIGN KEY (" + ArticleDetailEntry.COLUMN_ARTICLE_ID + ") REFERENCES " +
                ArticleEntry.TABLE_NAME + " (" + ArticleEntry._ID + "));";

        Log.v(LOG_TAG, "SQL_CREATE_ARTICLE_TABLE" + SQL_CREATE_ARTICLE_TABLE);
        Log.v(LOG_TAG, "SQL_CREATE_ARTICLE_DETAIL_TABLE" + SQL_CREATE_ARTICLE_DETAIL_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ARTICLE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ARTICLE_DETAIL_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        onCreate(sqLiteDatabase);
    }

}
