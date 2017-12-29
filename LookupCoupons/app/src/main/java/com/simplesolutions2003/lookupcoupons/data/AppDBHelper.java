package com.simplesolutions2003.lookupcoupons.data;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.simplesolutions2003.lookupcoupons.data.AppContract.CouponsEntry;

/**
 * Created by SuriyaKumar on 8/16/2016.
 */
public class AppDBHelper extends SQLiteOpenHelper  {
    private final String LOG_TAG = AppDBHelper.class.getSimpleName();

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "lookupcoupons.db";

    public AppDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.v(LOG_TAG,"AppDBHelper");

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.v(LOG_TAG,"onCreate");

        final String SQL_CREATE_COUPONS_TABLE = "CREATE TABLE " + CouponsEntry.TABLE_NAME + " (" +
                CouponsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                CouponsEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                CouponsEntry.COLUMN_CATEGORY + " TEXT NOT NULL, " +
                CouponsEntry.COLUMN_RETAILER + " TEXT NOT NULL, " +
                CouponsEntry.COLUMN_TYPE + " TEXT NOT NULL, " +
                CouponsEntry.COLUMN_VALUE + " TEXT NOT NULL, " +
                CouponsEntry.COLUMN_URL + " TEXT NOT NULL, " +
                CouponsEntry.COLUMN_DESC + " TEXT NOT NULL, " +
                CouponsEntry.COLUMN_IMAGE + " TEXT NOT NULL, " +
                CouponsEntry.COLUMN_LOGO + " TEXT NOT NULL, " +
                CouponsEntry.COLUMN_BEG_DATE + " TEXT NOT NULL, " +
                CouponsEntry.COLUMN_END_DATE + " TEXT NOT NULL, " +
                " UNIQUE (" + CouponsEntry._ID + ") ON CONFLICT REPLACE);";

        Log.v(LOG_TAG, "SQL_CREATE_MENU_TABLE" + SQL_CREATE_COUPONS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_COUPONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.v(LOG_TAG,"onUpgrade");
        onCreate(sqLiteDatabase);
    }

}
