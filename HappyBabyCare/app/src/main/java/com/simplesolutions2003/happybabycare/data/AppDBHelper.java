package com.simplesolutions2003.happybabycare.data;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.audiofx.BassBoost;
import android.util.Log;

import com.simplesolutions2003.happybabycare.data.AppContract.SettingsEntry;
import com.simplesolutions2003.happybabycare.data.AppContract.UserEntry;
import com.simplesolutions2003.happybabycare.data.AppContract.UserPreferenceEntry;
import com.simplesolutions2003.happybabycare.data.AppContract.SyncLogEntry;
import com.simplesolutions2003.happybabycare.data.AppContract.SubscribeEntry;
import com.simplesolutions2003.happybabycare.data.AppContract.ShareEntry;
import com.simplesolutions2003.happybabycare.data.AppContract.BabyEntry;
import com.simplesolutions2003.happybabycare.data.AppContract.FeedingEntry;
import com.simplesolutions2003.happybabycare.data.AppContract.DiaperEntry;
import com.simplesolutions2003.happybabycare.data.AppContract.SleepingEntry;
import com.simplesolutions2003.happybabycare.data.AppContract.HealthEntry;
import com.simplesolutions2003.happybabycare.data.AppContract.ArticleEntry;
import com.simplesolutions2003.happybabycare.data.AppContract.ArticleDetailEntry;
import com.simplesolutions2003.happybabycare.data.AppContract.MediaEntry;

/**
 * Created by SuriyaKumar on 8/16/2016.
 */
public class AppDBHelper extends SQLiteOpenHelper  {
    private final String LOG_TAG = AppDBHelper.class.getSimpleName();

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "happybabycare.db";

    public AppDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_SETTINGS_TABLE = "CREATE TABLE " + SettingsEntry.TABLE_NAME + " (" +
                SettingsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SettingsEntry.COLUMN_VERSION + " TEXT NOT NULL," +
                SettingsEntry.COLUMN_TYPE + " TEXT NOT NULL, " +
                SettingsEntry.COLUMN_VALUE + " TEXT, " +
                SettingsEntry.COLUMN_ACTIVE + " INTEGER, " +
                SettingsEntry.COLUMN_UPDATED_TS + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +

                " UNIQUE (" + SettingsEntry._ID + ") ON CONFLICT REPLACE," +

                " UNIQUE (" + SettingsEntry.COLUMN_VERSION + ", "
                            + SettingsEntry.COLUMN_TYPE + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_USER_TABLE = "CREATE TABLE " + UserEntry.TABLE_NAME + " (" +
                UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                UserEntry.COLUMN_USER_ID + " TEXT NOT NULL, " +
                UserEntry.COLUMN_PASSWORD + " TEXT, " +
                UserEntry.COLUMN_ACTIVE + " INTEGER, " +
                UserEntry.COLUMN_LAST_SYNC_TS + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +

                " UNIQUE (" + UserEntry._ID + ") ON CONFLICT REPLACE," +

                " UNIQUE (" + UserEntry.COLUMN_USER_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_USER_PREF_TABLE = "CREATE TABLE " + UserPreferenceEntry.TABLE_NAME + " (" +
                UserPreferenceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                UserPreferenceEntry.COLUMN_USER_ID + " TEXT NOT NULL," +
                UserPreferenceEntry.COLUMN_TYPE + " TEXT NOT NULL, " +
                UserPreferenceEntry.COLUMN_VALUE + " TEXT, " +

                " UNIQUE (" + UserPreferenceEntry._ID + ") ON CONFLICT REPLACE," +

                " UNIQUE (" + UserPreferenceEntry.COLUMN_USER_ID + ", "
                            + UserPreferenceEntry.COLUMN_TYPE +  ") ON CONFLICT REPLACE," +

                " FOREIGN KEY (" + UserPreferenceEntry.COLUMN_USER_ID + ") REFERENCES " +
                UserEntry.TABLE_NAME + " (" + UserEntry.COLUMN_USER_ID + "));";

        final String SQL_CREATE_SYNC_LOG_TABLE = "CREATE TABLE " + SyncLogEntry.TABLE_NAME + " (" +
                SyncLogEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SyncLogEntry.COLUMN_USER_ID + " TEXT NOT NULL," +
                SyncLogEntry.COLUMN_TABLE_NAME + " TEXT NOT NULL, " +
                SyncLogEntry.COLUMN_LAST_SYNC_TS + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +

                " UNIQUE (" + SyncLogEntry._ID + ") ON CONFLICT REPLACE," +

                " UNIQUE (" + SyncLogEntry.COLUMN_USER_ID + ", "
                            + SyncLogEntry.COLUMN_TABLE_NAME + ") ON CONFLICT REPLACE," +

                " FOREIGN KEY (" + SyncLogEntry.COLUMN_USER_ID + ") REFERENCES " +
                UserEntry.TABLE_NAME + " (" + UserEntry.COLUMN_USER_ID + "));";

        final String SQL_CREATE_SUBSCRIBE_TABLE = "CREATE TABLE " + SubscribeEntry.TABLE_NAME + " (" +
                SubscribeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SubscribeEntry.COLUMN_USER_ID + " TEXT NOT NULL, " +
                SubscribeEntry.COLUMN_OWNER_USER_ID + " TEXT NOT NULL, " +

                " UNIQUE (" + SubscribeEntry._ID + ") ON CONFLICT REPLACE," +

                " UNIQUE (" + SubscribeEntry.COLUMN_USER_ID + ", "
                + SubscribeEntry.COLUMN_OWNER_USER_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_SHARE_TABLE = "CREATE TABLE " + ShareEntry.TABLE_NAME + " (" +
                ShareEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ShareEntry.COLUMN_BABY_ID + " INTEGER," +
                ShareEntry.COLUMN_USER_ID + " TEXT NOT NULL, " +
                ShareEntry.COLUMN_OWNER_BABY_ID + " INTEGER," +
                ShareEntry.COLUMN_OWNER_USER_ID + " TEXT NOT NULL, " +

                " UNIQUE (" + ShareEntry._ID + ") ON CONFLICT REPLACE," +

                " UNIQUE (" + ShareEntry.COLUMN_BABY_ID + ", "
                + ShareEntry.COLUMN_USER_ID + ") ON CONFLICT REPLACE," +

                " FOREIGN KEY (" + ShareEntry.COLUMN_BABY_ID + ") REFERENCES " +
                UserEntry.TABLE_NAME + " (" + BabyEntry._ID + "));";

        final String SQL_CREATE_BABY_TABLE = "CREATE TABLE " + BabyEntry.TABLE_NAME + " (" +
                BabyEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                BabyEntry.COLUMN_USER_ID + " TEXT NOT NULL, " +
                BabyEntry.COLUMN_OWNER_BABY_ID + " INTEGER, " +
                BabyEntry.COLUMN_OWNER_USER_ID + " TEXT NOT NULL, " +
                BabyEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                BabyEntry.COLUMN_GENDER + " TEXT NOT NULL, " +
                BabyEntry.COLUMN_BIRTH_DATE + " DATE, " +
                BabyEntry.COLUMN_DUE_DATE + " DATE, " +
                BabyEntry.COLUMN_COLOR + " TEXT, " +
                BabyEntry.COLUMN_PHOTO + " TEXT, " +
                BabyEntry.COLUMN_ACTIVE + " INTEGER, " +

                " UNIQUE (" + BabyEntry._ID + ") ON CONFLICT REPLACE," +

                " FOREIGN KEY (" + BabyEntry.COLUMN_USER_ID + ") REFERENCES " +
                UserEntry.TABLE_NAME + " (" + UserEntry.COLUMN_USER_ID + ")," +

                " UNIQUE (" + BabyEntry.COLUMN_USER_ID + ","
                            + BabyEntry.COLUMN_NAME + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_FEEDING_TABLE = "CREATE TABLE " + FeedingEntry.TABLE_NAME + " (" +
                FeedingEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FeedingEntry.COLUMN_USER_ID + " TEXT NOT NULL," +
                FeedingEntry.COLUMN_BABY_ID + " INTEGER NOT NULL, " +
                FeedingEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                FeedingEntry.COLUMN_DATE + " DATE, " +
                FeedingEntry.COLUMN_TIME + " TIME, " +
                FeedingEntry.COLUMN_TYPE + " TEXT, " +
                FeedingEntry.COLUMN_QUANTITY + " TEXT, " +
                FeedingEntry.COLUMN_UNIT + " TEXT, " +
                FeedingEntry.COLUMN_NOTES + " TEXT, " +
                FeedingEntry.COLUMN_LAST_UPDATED_TS + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +

                " UNIQUE (" + FeedingEntry._ID + ") ON CONFLICT REPLACE," +

                " UNIQUE (" + FeedingEntry.COLUMN_USER_ID + "," +
                FeedingEntry.COLUMN_BABY_ID + "," +
                FeedingEntry.COLUMN_TIMESTAMP + ") ON CONFLICT REPLACE," +

                " FOREIGN KEY (" + FeedingEntry.COLUMN_USER_ID + ") REFERENCES " +
                UserEntry.TABLE_NAME + " (" + UserEntry.COLUMN_USER_ID + ")," +

                " FOREIGN KEY (" + FeedingEntry.COLUMN_BABY_ID + ") REFERENCES " +
                BabyEntry.TABLE_NAME + " (" + BabyEntry._ID + "));";

        final String SQL_CREATE_DIAPER_TABLE = "CREATE TABLE " + DiaperEntry.TABLE_NAME + " (" +
                DiaperEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DiaperEntry.COLUMN_USER_ID + " TEXT NOT NULL," +
                DiaperEntry.COLUMN_BABY_ID + " INTEGER NOT NULL, " +
                DiaperEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                DiaperEntry.COLUMN_DATE + " DATE, " +
                DiaperEntry.COLUMN_TIME + " TIME, " +
                DiaperEntry.COLUMN_TYPE + " TEXT, " +
                DiaperEntry.COLUMN_CREAM + " TEXT, " +
                DiaperEntry.COLUMN_NOTES + " TEXT, " +
                DiaperEntry.COLUMN_LAST_UPDATED_TS + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +

                " UNIQUE (" + DiaperEntry._ID + ") ON CONFLICT REPLACE," +

                " UNIQUE (" + DiaperEntry.COLUMN_USER_ID + "," +
                DiaperEntry.COLUMN_BABY_ID + "," +
                DiaperEntry.COLUMN_TIMESTAMP + ") ON CONFLICT REPLACE," +

                " FOREIGN KEY (" + DiaperEntry.COLUMN_USER_ID + ") REFERENCES " +
                UserEntry.TABLE_NAME + " (" + UserEntry.COLUMN_USER_ID + ")," +

                " FOREIGN KEY (" + FeedingEntry.COLUMN_BABY_ID + ") REFERENCES " +
                BabyEntry.TABLE_NAME + " (" + BabyEntry._ID + "));";

        final String SQL_CREATE_SLEEPING_TABLE = "CREATE TABLE " + SleepingEntry.TABLE_NAME + " (" +
                SleepingEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SleepingEntry.COLUMN_USER_ID + " TEXT NOT NULL," +
                SleepingEntry.COLUMN_BABY_ID + " INTEGER NOT NULL, " +
                SleepingEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                SleepingEntry.COLUMN_DATE + " DATE, " +
                SleepingEntry.COLUMN_TIME + " TIME, " +
                SleepingEntry.COLUMN_TYPE + " TEXT DEFAULT 'Sleeping', " +
                SleepingEntry.COLUMN_END_TIME + " TIME, " +
                SleepingEntry.COLUMN_DURATION + " INTEGER, " +
                SleepingEntry.COLUMN_WHERE_SLEEP + " TEXT, " +
                SleepingEntry.COLUMN_NOTES + " TEXT, " +
                SleepingEntry.COLUMN_LAST_UPDATED_TS + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +

                " UNIQUE (" + SleepingEntry._ID + ") ON CONFLICT REPLACE," +

                " UNIQUE (" + SleepingEntry.COLUMN_USER_ID + "," +
                SleepingEntry.COLUMN_BABY_ID + "," +
                SleepingEntry.COLUMN_TIMESTAMP + ") ON CONFLICT REPLACE," +

                " FOREIGN KEY (" + SleepingEntry.COLUMN_USER_ID + ") REFERENCES " +
                UserEntry.TABLE_NAME + " (" + UserEntry.COLUMN_USER_ID + ")," +

                " FOREIGN KEY (" + SleepingEntry.COLUMN_BABY_ID + ") REFERENCES " +
                BabyEntry.TABLE_NAME + " (" + BabyEntry._ID + "));";

        final String SQL_CREATE_HEALTH_TABLE = "CREATE TABLE " + HealthEntry.TABLE_NAME + " (" +
                HealthEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                HealthEntry.COLUMN_USER_ID + " TEXT NOT NULL," +
                HealthEntry.COLUMN_BABY_ID + " INTEGER NOT NULL, " +
                HealthEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                HealthEntry.COLUMN_DATE + " DATE, " +
                HealthEntry.COLUMN_TIME + " TIME, " +
                HealthEntry.COLUMN_TYPE + " TEXT, " +
                HealthEntry.COLUMN_VALUE + " TEXT, " +
                HealthEntry.COLUMN_UNIT + " TEXT, " +
                HealthEntry.COLUMN_NOTES + " TEXT, " +
                HealthEntry.COLUMN_LAST_UPDATED_TS + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +

                " UNIQUE (" + HealthEntry._ID + ") ON CONFLICT REPLACE," +

                " UNIQUE (" + HealthEntry.COLUMN_USER_ID + "," +
                HealthEntry.COLUMN_BABY_ID + "," +
                HealthEntry.COLUMN_TIMESTAMP + ") ON CONFLICT REPLACE," +

                " FOREIGN KEY (" + HealthEntry.COLUMN_USER_ID + ") REFERENCES " +
                UserEntry.TABLE_NAME + " (" + UserEntry.COLUMN_USER_ID + ")," +

                " FOREIGN KEY (" + HealthEntry.COLUMN_BABY_ID + ") REFERENCES " +
                BabyEntry.TABLE_NAME + " (" + BabyEntry._ID + "));";

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

        final String SQL_CREATE_MEDIA_TABLE = "CREATE TABLE " + MediaEntry.TABLE_NAME + " (" +
                MediaEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MediaEntry.COLUMN_TYPE + " TEXT NOT NULL, " +
                MediaEntry.COLUMN_CATEGORY + " TEXT NOT NULL, " +
                MediaEntry.COLUMN_PATH + " TEXT," +

                " UNIQUE (" + SettingsEntry._ID + ") ON CONFLICT REPLACE);";

        Log.v(LOG_TAG, "SQL_CREATE_SETTING_TABLE" + SQL_CREATE_SETTINGS_TABLE);
        Log.v(LOG_TAG, "SQL_CREATE_USER_TABLE" + SQL_CREATE_USER_TABLE);
        Log.v(LOG_TAG, "SQL_CREATE_USER_PREF_TABLE" + SQL_CREATE_USER_PREF_TABLE);
        Log.v(LOG_TAG, "SQL_CREATE_SYNC_LOG_TABLE" + SQL_CREATE_SYNC_LOG_TABLE);
        Log.v(LOG_TAG, "SQL_CREATE_SUBSCRIBE_TABLE" + SQL_CREATE_SUBSCRIBE_TABLE);
        Log.v(LOG_TAG, "SQL_CREATE_SHARE_TABLE" + SQL_CREATE_SHARE_TABLE);
        Log.v(LOG_TAG, "SQL_CREATE_BABY_TABLE" + SQL_CREATE_BABY_TABLE);
        Log.v(LOG_TAG, "SQL_CREATE_FEEDING_TABLE" + SQL_CREATE_FEEDING_TABLE);
        Log.v(LOG_TAG, "SQL_CREATE_DIAPER_TABLE" + SQL_CREATE_DIAPER_TABLE);
        Log.v(LOG_TAG, "SQL_CREATE_SLEEPING_TABLE" + SQL_CREATE_SLEEPING_TABLE);
        Log.v(LOG_TAG, "SQL_CREATE_HEALTH_TABLE" + SQL_CREATE_HEALTH_TABLE);
        Log.v(LOG_TAG, "SQL_CREATE_ARTICLE_TABLE" + SQL_CREATE_ARTICLE_TABLE);
        Log.v(LOG_TAG, "SQL_CREATE_ARTICLE_DETAIL_TABLE" + SQL_CREATE_ARTICLE_DETAIL_TABLE);
        Log.v(LOG_TAG, "SQL_CREATE_MEDIA_TABLE" + SQL_CREATE_MEDIA_TABLE);

        sqLiteDatabase.execSQL(SQL_CREATE_SETTINGS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_USER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_USER_PREF_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SYNC_LOG_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SUBSCRIBE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SHARE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_BABY_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FEEDING_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_DIAPER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SLEEPING_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_HEALTH_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ARTICLE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ARTICLE_DETAIL_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MEDIA_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        onCreate(sqLiteDatabase);
    }

}
