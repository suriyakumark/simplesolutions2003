package com.simplesolutions2003.happybabycare.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by SuriyaKumar on 8/16/2016.
 */
public class AppProvider extends ContentProvider {
    private final String LOG_TAG = AppProvider.class.getSimpleName();

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private AppDBHelper mOpenHelper;

    private static final SQLiteQueryBuilder sSettingsQueryBuilder;
    private static final SQLiteQueryBuilder sUserQueryBuilder;
    private static final SQLiteQueryBuilder sUserPrefQueryBuilder;
    private static final SQLiteQueryBuilder sSyncLogQueryBuilder;
    private static final SQLiteQueryBuilder sSubscribeQueryBuilder;
    private static final SQLiteQueryBuilder sShareQueryBuilder;
    private static final SQLiteQueryBuilder sBabyQueryBuilder;
    private static final SQLiteQueryBuilder sFeedingQueryBuilder;
    private static final SQLiteQueryBuilder sDiaperQueryBuilder;
    private static final SQLiteQueryBuilder sSleepingQueryBuilder;
    private static final SQLiteQueryBuilder sHealthQueryBuilder;
    private static final SQLiteQueryBuilder sArticleQueryBuilder;
    private static final SQLiteQueryBuilder sArticleDetailQueryBuilder;
    private static final SQLiteQueryBuilder sMediaQueryBuilder;

    private static final SQLiteQueryBuilder sActivitiesQueryBuilder;
    private static final SQLiteQueryBuilder sActivitiesSummaryQueryBuilder;
    private static final SQLiteQueryBuilder sArticleDetailWithDetailQueryBuilder;

    static final int SETTINGS = 100;
    static final int SETTINGS_BY_VERSION = 101;
    static final int USER = 200;
    static final int USER_BY_USERID = 201;
    static final int USER_PREF = 300;
    static final int USER_PREF_BY_USERID = 301;
    static final int SYNC_LOG = 400;
    static final int SYNC_LOG_BY_USERID = 401;
    static final int SUBSCRIBE = 4000;
    static final int SUBSCRIBE_BY_USERID = 4001;
    static final int SHARE = 4100;
    static final int SHARE_BY_USERID = 4101;
    static final int SHARE_BY_USERID_BABYID = 4102;
    static final int SHARE_BY_OWNERUSERID = 4103;
    static final int SHARE_BY_OWNERUSERID_OWNERBABYID = 4104;
    static final int BABY = 500;
    static final int BABY_BY_USERID = 501;
    static final int BABY_BY_USERID_BABYID = 502;
    static final int FEEDING = 600;
    static final int FEEDING_BY_ID = 601;
    static final int FEEDING_BY_USERID_BABYID = 602;
    static final int DIAPER = 700;
    static final int DIAPER_BY_ID = 701;
    static final int DIAPER_BY_USERID_BABYID = 702;
    static final int SLEEPING = 800;
    static final int SLEEPING_BY_ID = 801;
    static final int SLEEPING_BY_USERID_BABYID = 802;
    static final int HEALTH = 900;
    static final int HEALTH_BY_ID = 901;
    static final int HEALTH_BY_USERID_BABYID = 902;
    static final int ACTIVITIES_BY_USERID_BABYID = 904;
    static final int ACTIVITIES_SUMMARY_BY_USERID_BABYID = 905;
    static final int ARTICLE = 1000;
    static final int ARTICLE_BY_TYPE = 1001;
    static final int ARTICLE_BY_CATEGORY = 1002;
    static final int ARTICLE_DETAIL = 1100;
    static final int ARTICLE_DETAIL_BY_ARTICLEID = 1101;
    static final int ARTICLE_DETAIL_WITH_DETAIL_BY_ARTICLEID = 1102;
    static final int MEDIA = 1200;
    static final int MEDIA_BY_TYPE = 1201;
    static final int MEDIA_BY_CATEGORY = 1202;

    static{
        sSettingsQueryBuilder = new SQLiteQueryBuilder();

        sSettingsQueryBuilder.setTables(
                AppContract.SettingsEntry.TABLE_NAME);
    }

    static{
        sUserQueryBuilder = new SQLiteQueryBuilder();

        sUserQueryBuilder.setTables(
                AppContract.UserEntry.TABLE_NAME);
    }

    static{
        sUserPrefQueryBuilder = new SQLiteQueryBuilder();

        sUserPrefQueryBuilder.setTables(
                AppContract.UserPreferenceEntry.TABLE_NAME);
    }

    static{
        sSyncLogQueryBuilder = new SQLiteQueryBuilder();

        sSyncLogQueryBuilder.setTables(
                AppContract.SyncLogEntry.TABLE_NAME);
    }

    static{
        sSubscribeQueryBuilder = new SQLiteQueryBuilder();

        sSubscribeQueryBuilder.setTables(
                AppContract.SubscribeEntry.TABLE_NAME);
    }

    static{
        sShareQueryBuilder = new SQLiteQueryBuilder();

        sShareQueryBuilder.setTables(
                AppContract.ShareEntry.TABLE_NAME);
    }
    
    static{
        sBabyQueryBuilder = new SQLiteQueryBuilder();

        sBabyQueryBuilder.setTables(
                AppContract.BabyEntry.TABLE_NAME);
    }

    static{
        sFeedingQueryBuilder = new SQLiteQueryBuilder();

        sFeedingQueryBuilder.setTables(
                AppContract.FeedingEntry.TABLE_NAME);
    }

    static{
        sDiaperQueryBuilder = new SQLiteQueryBuilder();

        sDiaperQueryBuilder.setTables(
                AppContract.DiaperEntry.TABLE_NAME);
    }

    static{
        sSleepingQueryBuilder = new SQLiteQueryBuilder();

        sSleepingQueryBuilder.setTables(
                AppContract.SleepingEntry.TABLE_NAME);
    }

    static{
        sHealthQueryBuilder = new SQLiteQueryBuilder();

        sHealthQueryBuilder.setTables(
                AppContract.HealthEntry.TABLE_NAME);
    }

    static{
        sArticleQueryBuilder = new SQLiteQueryBuilder();

        sArticleQueryBuilder.setTables(
                AppContract.ArticleEntry.TABLE_NAME);
    }

    static{
        sArticleDetailQueryBuilder = new SQLiteQueryBuilder();

        sArticleDetailQueryBuilder.setTables(
                AppContract.ArticleDetailEntry.TABLE_NAME);
    }

    static{
        sMediaQueryBuilder = new SQLiteQueryBuilder();

        sMediaQueryBuilder.setTables(
                AppContract.MediaEntry.TABLE_NAME);
    }

    static{
        sActivitiesQueryBuilder = new SQLiteQueryBuilder();
    }

    static{
        sActivitiesSummaryQueryBuilder = new SQLiteQueryBuilder();
    }

    static{
        sArticleDetailWithDetailQueryBuilder = new SQLiteQueryBuilder();

        sArticleDetailWithDetailQueryBuilder.setTables(
                AppContract.ArticleEntry.TABLE_NAME +
                        " INNER JOIN " + AppContract.ArticleDetailEntry.TABLE_NAME +
                        " ON " + AppContract.ArticleEntry.TABLE_NAME +
                        "." + AppContract.ArticleEntry._ID +
                        " = " + AppContract.ArticleDetailEntry.TABLE_NAME +
                        "." + AppContract.ArticleDetailEntry.COLUMN_ARTICLE_ID);
    }

    private static final String sSettingsSelection =
            AppContract.SettingsEntry.TABLE_NAME +
                    "." + AppContract.SettingsEntry._ID + " = ? ";

    private static final String sSettingsByVersionSelection =
            AppContract.SettingsEntry.TABLE_NAME +
                    "." + AppContract.SettingsEntry.COLUMN_VERSION + " = ? ";

    private static final String sUserSelection =
            AppContract.UserEntry.TABLE_NAME +
                    "." + AppContract.UserEntry._ID + " = ? ";

    private static final String sUserByUserIdSelection =
            AppContract.UserEntry.TABLE_NAME +
                    "." + AppContract.UserEntry.COLUMN_USER_ID + " = ? ";

    private static final String sUserPrefSelection =
            AppContract.UserPreferenceEntry.TABLE_NAME +
                    "." + AppContract.UserPreferenceEntry._ID + " = ? ";

    private static final String sUserPrefByUserIdSelection =
            AppContract.UserPreferenceEntry.TABLE_NAME +
                    "." + AppContract.UserPreferenceEntry.COLUMN_USER_ID + " = ? ";

    private static final String sSyncLogSelection =
            AppContract.SyncLogEntry.TABLE_NAME +
                    "." + AppContract.SyncLogEntry._ID + " = ? ";

    private static final String sSyncLogByUserIdSelection =
            AppContract.SyncLogEntry.TABLE_NAME +
                    "." + AppContract.SyncLogEntry.COLUMN_USER_ID + " = ? ";

    private static final String sSubscribeSelection =
            AppContract.SubscribeEntry.TABLE_NAME +
                    "." + AppContract.SubscribeEntry._ID + " = ? ";

    private static final String sSubscribeByUserIdSelection =
            AppContract.SubscribeEntry.TABLE_NAME +
                    "." + AppContract.SubscribeEntry.COLUMN_USER_ID + " = ? ";

    private static final String sShareSelection =
            AppContract.ShareEntry.TABLE_NAME +
                    "." + AppContract.ShareEntry._ID + " = ? ";

    private static final String sShareByUserIdBabyIdSelection =
            AppContract.ShareEntry.TABLE_NAME +
                    "." + AppContract.ShareEntry.COLUMN_USER_ID + " = ? AND " +
            AppContract.ShareEntry.TABLE_NAME +
                    "." + AppContract.ShareEntry.COLUMN_BABY_ID + " = ? ";

    private static final String sShareByUserIdSelection =
            AppContract.ShareEntry.TABLE_NAME +
                    "." + AppContract.ShareEntry.COLUMN_USER_ID + " = ? ";

    private static final String sShareByOwnerUserIdOwnerBabyIdSelection =
            AppContract.ShareEntry.TABLE_NAME +
                    "." + AppContract.ShareEntry.COLUMN_OWNER_USER_ID + " = ? AND " +
                    AppContract.ShareEntry.TABLE_NAME +
                    "." + AppContract.ShareEntry.COLUMN_OWNER_BABY_ID + " = ? ";

    private static final String sShareByOwnerUserIdSelection =
            AppContract.ShareEntry.TABLE_NAME +
                    "." + AppContract.ShareEntry.COLUMN_OWNER_USER_ID + " = ? ";

    private static final String sBabySelection =
            AppContract.BabyEntry.TABLE_NAME +
                    "." + AppContract.BabyEntry._ID + " = ? ";

    private static final String sBabyByUserIdSelection =
            AppContract.BabyEntry.TABLE_NAME +
                    "." + AppContract.BabyEntry.COLUMN_USER_ID + " = ? ";

    private static final String sBabyByUserIdBabyIdSelection =
            AppContract.BabyEntry.TABLE_NAME +
                    "." + AppContract.BabyEntry.COLUMN_USER_ID + " = ? AND " +
            AppContract.BabyEntry.TABLE_NAME +
            "." + AppContract.BabyEntry._ID + " = ? ";

    private static final String sFeedingSelection =
            AppContract.FeedingEntry.TABLE_NAME +
                    "." + AppContract.FeedingEntry._ID + " = ? ";

    private static final String sFeedingByUserIdSelection =
            AppContract.FeedingEntry.TABLE_NAME +
                    "." + AppContract.FeedingEntry.COLUMN_USER_ID + " = ? ";

    private static final String sFeedingByUserIdBabyIdSelection =
            AppContract.FeedingEntry.TABLE_NAME +
                    "." + AppContract.FeedingEntry.COLUMN_USER_ID + " = ? " +
            " AND " + AppContract.FeedingEntry.TABLE_NAME +
                    "." + AppContract.FeedingEntry.COLUMN_BABY_ID + " = ? ";

    private static final String sActivitiesFeedingByUserIdBabyIdSelection =
            sFeedingByUserIdBabyIdSelection +
                    " AND " + AppContract.FeedingEntry.TABLE_NAME +
                    "." + AppContract.FeedingEntry.COLUMN_DATE + " = ? " +
                    " AND ('Feeding' = ? OR 'All' = ? ) ";

    private static final String sDiaperSelection =
            AppContract.DiaperEntry.TABLE_NAME +
                    "." + AppContract.DiaperEntry._ID + " = ? ";

    private static final String sDiaperByUserIdSelection =
            AppContract.DiaperEntry.TABLE_NAME +
                    "." + AppContract.DiaperEntry.COLUMN_USER_ID + " = ? ";

    private static final String sDiaperByUserIdBabyIdSelection =
            AppContract.DiaperEntry.TABLE_NAME +
                    "." + AppContract.DiaperEntry.COLUMN_USER_ID + " = ? " +
                    " AND " + AppContract.DiaperEntry.TABLE_NAME +
                    "." + AppContract.DiaperEntry.COLUMN_BABY_ID + " = ? ";

    private static final String sActivitiesDiaperByUserIdBabyIdSelection =
            sDiaperByUserIdBabyIdSelection +
                    " AND " + AppContract.DiaperEntry.TABLE_NAME +
                    "." + AppContract.DiaperEntry.COLUMN_DATE + " = ? " +
                    " AND ('Diaper' = ? OR 'All' = ? ) ";

    private static final String sSleepingSelection =
            AppContract.SleepingEntry.TABLE_NAME +
                    "." + AppContract.SleepingEntry._ID + " = ? ";

    private static final String sSleepingByUserIdSelection =
            AppContract.SleepingEntry.TABLE_NAME +
                    "." + AppContract.SleepingEntry.COLUMN_USER_ID + " = ? ";

    private static final String sSleepingByUserIdBabyIdSelection =
            AppContract.SleepingEntry.TABLE_NAME +
                    "." + AppContract.SleepingEntry.COLUMN_USER_ID + " = ? " +
                    " AND " + AppContract.SleepingEntry.TABLE_NAME +
                    "." + AppContract.SleepingEntry.COLUMN_BABY_ID + " = ? ";

    private static final String sActivitiesSleepingByUserIdBabyIdSelection =
            sSleepingByUserIdBabyIdSelection +
                    " AND " + AppContract.SleepingEntry.TABLE_NAME +
                    "." + AppContract.SleepingEntry.COLUMN_DATE + " = ? " +
                    " AND ('Sleeping' = ? OR 'All' = ? ) ";

    private static final String sHealthSelection =
            AppContract.HealthEntry.TABLE_NAME +
                    "." + AppContract.HealthEntry._ID + " = ? ";

    private static final String sHealthByUserIdBabyIdSelection =
            AppContract.HealthEntry.TABLE_NAME +
                    "." + AppContract.HealthEntry.COLUMN_USER_ID + " = ? " +
                    " AND " + AppContract.HealthEntry.TABLE_NAME +
                    "." + AppContract.HealthEntry.COLUMN_BABY_ID + " = ? ";

    private static final String sActivitiesHealthByUserIdBabyIdSelection =
            sHealthByUserIdBabyIdSelection +
                    " AND " + AppContract.HealthEntry.TABLE_NAME +
                    "." + AppContract.HealthEntry.COLUMN_DATE + " = ? " +
                    " AND ('Health' = ? OR 'All' = ? ) ";

    private static final String sArticleSelection =
            AppContract.ArticleEntry.TABLE_NAME +
                    "." + AppContract.ArticleEntry._ID + " = ? ";

    private static final String sArticleByTypeSelection =
            AppContract.ArticleEntry.TABLE_NAME +
                    "." + AppContract.ArticleEntry.COLUMN_TYPE + " = ? ";

    private static final String sArticleByCategorySelection =
            AppContract.ArticleEntry.TABLE_NAME +
                    "." + AppContract.ArticleEntry.COLUMN_CATEGORY + " = ? ";

    private static final String sArticleDetailSelection =
            AppContract.ArticleDetailEntry.TABLE_NAME +
                    "." + AppContract.ArticleDetailEntry._ID + " = ? ";

    private static final String sArticleDetailByArticleIdSelection =
            AppContract.ArticleDetailEntry.TABLE_NAME +
                    "." + AppContract.ArticleDetailEntry.COLUMN_ARTICLE_ID + " = ? ";

    private static final String sArticleDetailWithDetailByArticleIdSelection =
            AppContract.ArticleDetailEntry.TABLE_NAME +
                    "." + AppContract.ArticleDetailEntry.COLUMN_ARTICLE_ID + " = ? ";

    private static final String sMediaSelection =
            AppContract.MediaEntry.TABLE_NAME +
                    "." + AppContract.MediaEntry._ID + " = ? ";

    private static final String sMediaByTypeSelection =
            AppContract.MediaEntry.TABLE_NAME +
                    "." + AppContract.MediaEntry.COLUMN_TYPE + " = ? ";

    private static final String sMediaByCategorySelection =
            AppContract.MediaEntry.TABLE_NAME +
                    "." + AppContract.MediaEntry.COLUMN_CATEGORY + " = ? ";

    private Cursor getSettingsByVersion(Uri uri, String[] projection, String sortOrder) {

        Log.v(LOG_TAG, "getSettingsByVersion uri - " + uri);
        String version = AppContract.SettingsEntry.getVersionFromUri(uri);
        Log.v(LOG_TAG, "getArticleByType version - " + version);

        return sSettingsQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sSettingsByVersionSelection,
                new String[]{version},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getUserByUserId(Uri uri, String[] projection, String sortOrder) {

        Log.v(LOG_TAG, "getUserByUserId uri - " + uri);
        String userId = AppContract.UserEntry.getUserIdFromUri(uri);
        Log.v(LOG_TAG, "getUserByUserId userId - " + userId);

        return sUserQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sUserByUserIdSelection,
                new String[]{userId},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getUserPrefByUserId(Uri uri, String[] projection, String sortOrder) {

        Log.v(LOG_TAG, "getUserPrefByUserId uri - " + uri);
        String userId = AppContract.UserPreferenceEntry.getUserIdFromUri(uri);
        Log.v(LOG_TAG, "getUserPrefByUserId userId - " + userId);

        return sUserPrefQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sUserPrefByUserIdSelection,
                new String[]{userId},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getSyncLogByUserId(Uri uri, String[] projection, String sortOrder) {

        Log.v(LOG_TAG, "getSyncLogByUserId uri - " + uri);
        String userId = AppContract.SyncLogEntry.getUserIdFromUri(uri);
        Log.v(LOG_TAG, "getSyncLogByUserId userId - " + userId);

        return sSyncLogQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sSyncLogByUserIdSelection,
                new String[]{userId},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getSubscribeByUserId(Uri uri, String[] projection, String sortOrder) {

        Log.v(LOG_TAG, "getSubscribeByUserId uri - " + uri);
        String userId = AppContract.SubscribeEntry.getUserIdFromUri(uri);
        Log.v(LOG_TAG, "getSubscribeByUserId userId - " + userId);

        return sSubscribeQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sSubscribeByUserIdSelection,
                new String[]{userId},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getShareByUserId(Uri uri, String[] projection, String sortOrder) {

        Log.v(LOG_TAG, "getShareByUserId uri - " + uri);
        String userId = AppContract.ShareEntry.getUserIdFromUri(uri);
        Log.v(LOG_TAG, "getShareByUserId userId - " + userId);

        return sShareQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sShareByUserIdSelection,
                new String[]{userId},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getShareByUserIdBabyId(Uri uri, String[] projection, String sortOrder) {

        Log.v(LOG_TAG, "getShareByUserIdBabyId uri - " + uri);
        String userId = AppContract.ShareEntry.getUserIdFromUri(uri);
        String babyId = Long.toString(AppContract.ShareEntry.getBabyIdFromUri(uri));
        Log.v(LOG_TAG, "getShareByUserIdBabyId userId - " + userId);
        Log.v(LOG_TAG, "getShareByUserIdBabyId babyId - " + babyId);

        return sShareQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sShareByUserIdBabyIdSelection,
                new String[]{userId,babyId},
                null,
                null,
                sortOrder
        );
    }


    private Cursor getShareByOwnerUserId(Uri uri, String[] projection, String sortOrder) {

        Log.v(LOG_TAG, "getShareByOwnerUserId uri - " + uri);
        String ownerUserId = AppContract.ShareEntry.getOwnerUserIdFromUri(uri);
        Log.v(LOG_TAG, "getShareByOwnerUserId ownerUserId - " + ownerUserId);

        return sShareQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sShareByOwnerUserIdSelection,
                new String[]{ownerUserId},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getShareByOwnerUserIdOwnerBabyId(Uri uri, String[] projection, String sortOrder) {

        Log.v(LOG_TAG, "getShareByOwnerUserIdOwnerBabyId uri - " + uri);
        String ownerUserId = AppContract.ShareEntry.getOwnerUserIdFromUri(uri);
        String ownerBabyId = Long.toString(AppContract.ShareEntry.getOwnerBabyIdFromUri(uri));
        Log.v(LOG_TAG, "getShareByOwnerUserIdOwnerBabyId ownerUserId - " + ownerUserId);
        Log.v(LOG_TAG, "getShareByOwnerUserIdOwnerBabyId babyId - " + ownerBabyId);

        return sShareQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sShareByOwnerUserIdOwnerBabyIdSelection,
                new String[]{ownerUserId,ownerBabyId},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getBabyByUserId(Uri uri, String[] projection, String sortOrder) {

        Log.v(LOG_TAG, "getBabyByUserId uri - " + uri);
        String userId = AppContract.BabyEntry.getUserIdFromUri(uri);
        Log.v(LOG_TAG, "getBabyByUserId userId - " + userId);

        return sBabyQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sBabyByUserIdSelection,
                new String[]{userId},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getBabyByUserIdBabyId(Uri uri, String[] projection, String sortOrder) {

        Log.v(LOG_TAG, "getBabyByUserId uri - " + uri);
        String userId = AppContract.BabyEntry.getUserIdFromUri(uri);
        String babyId = Long.toString(AppContract.BabyEntry.getBabyIdFromUri(uri));
        Log.v(LOG_TAG, "getBabyByUserId userId - " + userId + " BabyId - " + babyId);

        return sBabyQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sBabyByUserIdBabyIdSelection,
                new String[]{userId,babyId},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getFeedingById(Uri uri, String[] projection, String sortOrder) {

        Log.v(LOG_TAG, "getFeedingById uri - " + uri);
        String _Id = Long.toString(AppContract.FeedingEntry.getIdFromUri(uri));
        Log.v(LOG_TAG, "getFeedingById _Id - " + _Id);

        return sFeedingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sFeedingSelection,
                new String[]{_Id},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getFeedingByUserIdBabyId(Uri uri, String[] projection, String sortOrder) {

        Log.v(LOG_TAG, "getFeedingByUserIdBabyId uri - " + uri);
        String userId = AppContract.FeedingEntry.getUserIdFromUri(uri);
        String babyId = Long.toString(AppContract.FeedingEntry.getBabyIdFromUri(uri));
        Log.v(LOG_TAG, "getFeedingByUserIdBabyId userId - " + userId + " BabyId - " + babyId);

        return sFeedingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sFeedingByUserIdBabyIdSelection,
                new String[]{userId,babyId},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getDiaperById(Uri uri, String[] projection, String sortOrder) {

        Log.v(LOG_TAG, "getDiaperById uri - " + uri);
        String _Id = Long.toString(AppContract.DiaperEntry.getIdFromUri(uri));
        Log.v(LOG_TAG, "getDiaperById _Id - " + _Id);

        return sDiaperQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sDiaperSelection,
                new String[]{_Id},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getDiaperByUserIdBabyId(Uri uri, String[] projection, String sortOrder) {

        Log.v(LOG_TAG, "getDiaperByUserIdBabyId uri - " + uri);
        String userId = AppContract.DiaperEntry.getUserIdFromUri(uri);
        String babyId = Long.toString(AppContract.DiaperEntry.getBabyIdFromUri(uri));
        Log.v(LOG_TAG, "getDiaperByUserIdBabyId userId - " + userId + " BabyId - " + babyId);

        return sDiaperQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sDiaperByUserIdBabyIdSelection,
                new String[]{userId,babyId},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getSleepingById(Uri uri, String[] projection, String sortOrder) {

        Log.v(LOG_TAG, "getSleepingById uri - " + uri);
        String _Id = Long.toString(AppContract.SleepingEntry.getIdFromUri(uri));
        Log.v(LOG_TAG, "getSleepingById _Id - " + _Id);

        return sSleepingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sSleepingSelection,
                new String[]{_Id},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getSleepingByUserIdBabyId(Uri uri, String[] projection, String sortOrder) {

        Log.v(LOG_TAG, "getSleepingByUserIdBabyId uri - " + uri);
        String userId = AppContract.SleepingEntry.getUserIdFromUri(uri);
        String babyId = Long.toString(AppContract.SleepingEntry.getBabyIdFromUri(uri));
        Log.v(LOG_TAG, "getSleepingByUserIdBabyId userId - " + userId + " BabyId - " + babyId);

        return sSleepingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sSleepingByUserIdBabyIdSelection,
                new String[]{userId,babyId},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getHealthById(Uri uri, String[] projection, String sortOrder) {

        Log.v(LOG_TAG, "getHealthById uri - " + uri);
        String _Id = Long.toString(AppContract.HealthEntry.getIdFromUri(uri));
        Log.v(LOG_TAG, "getHealthById _Id - " + _Id);

        return sHealthQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sHealthSelection,
                new String[]{_Id},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getHealthByUserIdBabyId(Uri uri, String[] projection, String sortOrder) {

        Log.v(LOG_TAG, "getHealthByUserIdBabyId uri - " + uri);
        String userId = AppContract.HealthEntry.getUserIdFromUri(uri);
        String babyId = Long.toString(AppContract.HealthEntry.getBabyIdFromUri(uri));
        Log.v(LOG_TAG, "getHealthByUserIdBabyId userId - " + userId + " BabyId - " + babyId);

        return sHealthQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sHealthByUserIdBabyIdSelection,
                new String[]{userId,babyId},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getActivitiesByUserIdBabyId(Uri uri, String[] projection, String sortOrder) {

        Log.v(LOG_TAG, "getActivitiesByBabyId uri - " + uri);
        String userId = AppContract.ActivitiesEntry.getUserIdFromUri(uri);
        String babyId = Long.toString(AppContract.ActivitiesEntry.getBabyIdFromUri(uri));
        String activityDate = AppContract.ActivitiesEntry.getActivityDateFromUri(uri);
        String activityType = AppContract.ActivitiesEntry.getActivityTypeFromUri(uri);
        Log.v(LOG_TAG, "getActivitiesByBabyId userId - " + userId);
        Log.v(LOG_TAG, "getActivitiesByBabyId babyId - " + babyId);
        Log.v(LOG_TAG, "getActivitiesByBabyId activityDate - " + activityDate);
        Log.v(LOG_TAG, "getActivitiesByBabyId activityType - " + activityType);

        String[] selectionArgs = new String[]{userId,babyId,activityDate,activityType,activityType,
                userId,babyId,activityDate,activityType,activityType,
                userId,babyId,activityDate,activityType,activityType,
                userId,babyId,activityDate,activityType,activityType};

        String activitiesFeedingQuery;
        String activitiesDiaperQuery;
        String activitiesSleepingQuery;
        String activitiesHealthQuery;

        activitiesFeedingQuery = "SELECT " +
                "'Feeding'" + " AS " + AppContract.ActivitiesEntry.COLUMN_ACTIVITY_TYPE + ", " +
                AppContract.FeedingEntry._ID + " AS " + AppContract.ActivitiesEntry.COLUMN_ACTIVITY_ID + ", " +
                AppContract.FeedingEntry.COLUMN_USER_ID + " AS " + AppContract.ActivitiesEntry.COLUMN_USER_ID + ", " +
                AppContract.FeedingEntry.COLUMN_BABY_ID + " AS " + AppContract.ActivitiesEntry.COLUMN_BABY_ID + ", " +
                AppContract.FeedingEntry.COLUMN_TIMESTAMP + " AS " + AppContract.ActivitiesEntry.COLUMN_TIMESTAMP + ", " +
                AppContract.FeedingEntry.COLUMN_DATE + " AS " + AppContract.ActivitiesEntry.COLUMN_DATE + ", " +
                AppContract.FeedingEntry.COLUMN_TIME + " AS " + AppContract.ActivitiesEntry.COLUMN_TIME + ", " +
                AppContract.FeedingEntry.COLUMN_TYPE + " || ': ' || " +
                AppContract.FeedingEntry.COLUMN_QUANTITY + " || ' ' || " +
                AppContract.FeedingEntry.COLUMN_UNIT +
                                    " AS " + AppContract.ActivitiesEntry.COLUMN_SUMMARY + ", " +
                AppContract.FeedingEntry.COLUMN_NOTES + " AS " + AppContract.ActivitiesEntry.COLUMN_DETAIL + ", " +
                AppContract.FeedingEntry.COLUMN_LAST_UPDATED_TS + " AS " + AppContract.ActivitiesEntry.COLUMN_LAST_UPDATED_TS + " " +
                " FROM " + AppContract.FeedingEntry.TABLE_NAME +
                " WHERE " +
                sActivitiesFeedingByUserIdBabyIdSelection;

        activitiesDiaperQuery = "SELECT " +
                "'Diaper'" + " AS " + AppContract.ActivitiesEntry.COLUMN_ACTIVITY_TYPE + ", " +
                AppContract.DiaperEntry._ID + " AS " + AppContract.ActivitiesEntry.COLUMN_ACTIVITY_ID + ", " +
                AppContract.DiaperEntry.COLUMN_USER_ID + " AS " + AppContract.ActivitiesEntry.COLUMN_USER_ID + ", " +
                AppContract.DiaperEntry.COLUMN_BABY_ID + " AS " + AppContract.ActivitiesEntry.COLUMN_BABY_ID + ", " +
                AppContract.DiaperEntry.COLUMN_TIMESTAMP + " AS " + AppContract.ActivitiesEntry.COLUMN_TIMESTAMP + ", " +
                AppContract.DiaperEntry.COLUMN_DATE + " AS " + AppContract.ActivitiesEntry.COLUMN_DATE + ", " +
                AppContract.DiaperEntry.COLUMN_TIME + " AS " + AppContract.ActivitiesEntry.COLUMN_TIME + ", " +
                AppContract.DiaperEntry.COLUMN_TYPE + " || ' ' || " +
                AppContract.DiaperEntry.COLUMN_CREAM +
                                    " AS " + AppContract.ActivitiesEntry.COLUMN_SUMMARY + ", " +
                AppContract.DiaperEntry.COLUMN_NOTES + " AS " + AppContract.ActivitiesEntry.COLUMN_DETAIL + ", " +
                AppContract.DiaperEntry.COLUMN_LAST_UPDATED_TS + " AS " + AppContract.ActivitiesEntry.COLUMN_LAST_UPDATED_TS + " " +
                " FROM " + AppContract.DiaperEntry.TABLE_NAME +
                " WHERE " +
                sActivitiesDiaperByUserIdBabyIdSelection;


        activitiesSleepingQuery = "SELECT " +
                "'Sleeping'" + " AS " + AppContract.ActivitiesEntry.COLUMN_ACTIVITY_TYPE + ", " +
                AppContract.SleepingEntry._ID + " AS " + AppContract.ActivitiesEntry.COLUMN_ACTIVITY_ID + ", " +
                AppContract.SleepingEntry.COLUMN_USER_ID + " AS " + AppContract.ActivitiesEntry.COLUMN_USER_ID + ", " +
                AppContract.SleepingEntry.COLUMN_BABY_ID + " AS " + AppContract.ActivitiesEntry.COLUMN_BABY_ID + ", " +
                AppContract.SleepingEntry.COLUMN_TIMESTAMP + " AS " + AppContract.ActivitiesEntry.COLUMN_TIMESTAMP + ", " +
                AppContract.SleepingEntry.COLUMN_DATE + " AS " + AppContract.ActivitiesEntry.COLUMN_DATE + ", " +
                AppContract.SleepingEntry.COLUMN_TIME + " AS " + AppContract.ActivitiesEntry.COLUMN_TIME + ", " +
                "CASE WHEN " + AppContract.SleepingEntry.COLUMN_DURATION + " > 59 THEN " +
                "CAST(" + AppContract.SleepingEntry.COLUMN_DURATION + "/60 AS INTEGER)" + " || 'hrs ' ELSE '' END || " +
                "CAST(" + AppContract.SleepingEntry.COLUMN_DURATION + " % 60 AS INTEGER)" + " || 'mins ' || " +
                AppContract.SleepingEntry.COLUMN_WHERE_SLEEP +
                                    " AS " + AppContract.ActivitiesEntry.COLUMN_SUMMARY + ", " +
                AppContract.SleepingEntry.COLUMN_NOTES + " AS " + AppContract.ActivitiesEntry.COLUMN_DETAIL + ", " +
                AppContract.SleepingEntry.COLUMN_LAST_UPDATED_TS + " AS " + AppContract.ActivitiesEntry.COLUMN_LAST_UPDATED_TS + " " +
                " FROM " + AppContract.SleepingEntry.TABLE_NAME +
                " WHERE " +
                sActivitiesSleepingByUserIdBabyIdSelection;

        activitiesHealthQuery = "SELECT " +
                "'Health'" + " AS " + AppContract.ActivitiesEntry.COLUMN_ACTIVITY_TYPE + ", " +
                AppContract.HealthEntry._ID + " AS " + AppContract.ActivitiesEntry.COLUMN_ACTIVITY_ID + ", " +
                AppContract.HealthEntry.COLUMN_USER_ID + " AS " + AppContract.ActivitiesEntry.COLUMN_USER_ID + ", " +
                AppContract.HealthEntry.COLUMN_BABY_ID + " AS " + AppContract.ActivitiesEntry.COLUMN_BABY_ID + ", " +
                AppContract.HealthEntry.COLUMN_TIMESTAMP + " AS " + AppContract.ActivitiesEntry.COLUMN_TIMESTAMP + ", " +
                AppContract.HealthEntry.COLUMN_DATE + " AS " + AppContract.ActivitiesEntry.COLUMN_DATE + ", " +
                AppContract.HealthEntry.COLUMN_TIME + " AS " + AppContract.ActivitiesEntry.COLUMN_TIME + ", " +
                AppContract.HealthEntry.COLUMN_TYPE + " || ' ' || " +
                AppContract.HealthEntry.COLUMN_VALUE + " || '' || " +
                AppContract.HealthEntry.COLUMN_UNIT  +
                                " AS " + AppContract.ActivitiesEntry.COLUMN_SUMMARY + ", " +
                AppContract.HealthEntry.COLUMN_NOTES + " AS " + AppContract.ActivitiesEntry.COLUMN_DETAIL + ", " +
                AppContract.HealthEntry.COLUMN_LAST_UPDATED_TS + " AS " + AppContract.ActivitiesEntry.COLUMN_LAST_UPDATED_TS + " " +
                " FROM " + AppContract.HealthEntry.TABLE_NAME +
                " WHERE " +
                sActivitiesHealthByUserIdBabyIdSelection;


        String[] subQueries = new String[] {activitiesFeedingQuery,activitiesDiaperQuery,activitiesSleepingQuery,activitiesHealthQuery};
        return mOpenHelper.getReadableDatabase().rawQuery(sActivitiesQueryBuilder.buildUnionQuery(subQueries, sortOrder, null),selectionArgs);
    }


    private Cursor getActivitiesSummaryByUserIdBabyId(Uri uri, String[] projection, String sortOrder) {

        Log.v(LOG_TAG, "getActivitiesSummaryByUserIdBabyId uri - " + uri);
        String userId = AppContract.ActivitiesEntry.getUserIdFromUri(uri);
        String babyId = Long.toString(AppContract.ActivitiesEntry.getBabyIdFromUri(uri));
        String activityDate = AppContract.ActivitiesEntry.getActivityDateFromUri(uri);
        String activityType = AppContract.ActivitiesEntry.getActivityTypeFromUri(uri);

        Log.v(LOG_TAG, "getActivitiesSummaryByUserIdBabyId userId - " + userId);
        Log.v(LOG_TAG, "getActivitiesSummaryByUserIdBabyId babyId - " + babyId);
        Log.v(LOG_TAG, "getActivitiesSummaryByUserIdBabyId activityDate - " + activityDate);
        Log.v(LOG_TAG, "getActivitiesSummaryByUserIdBabyId activityType - " + activityType);

        String[] selectionArgs = new String[]{userId,babyId,activityDate,activityType,activityType,
                userId,babyId,activityDate,activityType,activityType,
                userId,babyId,activityDate,activityType,activityType};

        String activitiesFeedingQuery;
        String activitiesDiaperQuery;
        String activitiesSleepingQuery;

        activitiesFeedingQuery = "SELECT " +
                "'Feeding'" + " AS " + AppContract.ActivitiesEntry.COLUMN_ACTIVITY_ID + ", " +
                AppContract.FeedingEntry.COLUMN_TYPE + " AS " + AppContract.ActivitiesEntry.COLUMN_SUMMARY + ", " +
                "SUM(" + AppContract.FeedingEntry.COLUMN_QUANTITY + ") || ' ' || " +
                AppContract.FeedingEntry.COLUMN_UNIT + " AS " + AppContract.ActivitiesEntry.COLUMN_DETAIL + " " +
                " FROM " + AppContract.FeedingEntry.TABLE_NAME +
                " WHERE " +
                sActivitiesFeedingByUserIdBabyIdSelection +
                " GROUP BY " +
                AppContract.FeedingEntry.COLUMN_TYPE + ", " +
                AppContract.FeedingEntry.COLUMN_UNIT;

        activitiesDiaperQuery = "SELECT " +
                "'Diaper'" + " AS " + AppContract.ActivitiesEntry.COLUMN_ACTIVITY_ID + ", " +
                AppContract.DiaperEntry.COLUMN_TYPE + " AS " + AppContract.ActivitiesEntry.COLUMN_SUMMARY + ", " +
                "COUNT(*)" + " AS " + AppContract.ActivitiesEntry.COLUMN_DETAIL + " " +
                " FROM " + AppContract.DiaperEntry.TABLE_NAME +
                " WHERE " +
                sActivitiesDiaperByUserIdBabyIdSelection +
                " GROUP BY " +
                AppContract.DiaperEntry.COLUMN_TYPE;

        activitiesSleepingQuery = "SELECT " +
                "'Sleeping'" + " AS " + AppContract.ActivitiesEntry.COLUMN_ACTIVITY_ID + ", " +
                "'Sleeping'" + " AS " + AppContract.ActivitiesEntry.COLUMN_SUMMARY + ", " +
                "CASE WHEN SUM(" + AppContract.SleepingEntry.COLUMN_DURATION + ") > 59 THEN " +
                "CAST(SUM(" + AppContract.SleepingEntry.COLUMN_DURATION + ")/60 AS INTEGER)" + " || 'hrs ' ELSE '' END || " +
                "CAST(SUM(" + AppContract.SleepingEntry.COLUMN_DURATION + ") % 60 AS INTEGER)" + " || 'mins'" +
                " AS " + AppContract.ActivitiesEntry.COLUMN_DETAIL + " " +
                " FROM " + AppContract.SleepingEntry.TABLE_NAME +
                " WHERE " +
                sActivitiesSleepingByUserIdBabyIdSelection;

        String[] subQueries = new String[] {activitiesFeedingQuery,activitiesDiaperQuery,activitiesSleepingQuery};
        return mOpenHelper.getReadableDatabase().rawQuery(sActivitiesSummaryQueryBuilder.buildUnionQuery(subQueries, sortOrder, null),selectionArgs);
    }

    private Cursor getArticleByType(Uri uri, String[] projection, String sortOrder) {

        Log.v(LOG_TAG, "getArticleByType uri - " + uri);
        String articleType = AppContract.ArticleEntry.getArticleTypeFromUri(uri);
        Log.v(LOG_TAG, "getArticleByType articleType - " + articleType);

        return sArticleQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sArticleByTypeSelection,
                new String[]{articleType},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getArticleByCategory(Uri uri, String[] projection, String sortOrder) {

        Log.v(LOG_TAG, "getArticleByCategory uri - " + uri);
        String articleCategory = AppContract.ArticleEntry.getArticleCategoryFromUri(uri);
        Log.v(LOG_TAG, "getArticleByCategory articleCategory - " + articleCategory);

        return sArticleQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sArticleByCategorySelection,
                new String[]{articleCategory},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getArticleDetailByArticleId(Uri uri, String[] projection, String sortOrder) {

        Log.v(LOG_TAG, "getArticleDetailByArticleId uri - " + uri);
        String articleId = Long.toString(AppContract.ArticleDetailEntry.getArticleIdFromUri(uri));
        Log.v(LOG_TAG, "getArticleDetailByArticleId articleId - " + articleId);

        return sArticleDetailQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sArticleDetailByArticleIdSelection,
                new String[]{articleId},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getArticleDetailWithDetailByArticleId(Uri uri, String[] projection, String sortOrder) {

        Log.v(LOG_TAG, "getArticleDetailWithDetailByArticleId uri - " + uri);
        String articleId = Long.toString(AppContract.ArticleDetailEntry.getArticleIdFromUri(uri));
        Log.v(LOG_TAG, "getArticleDetailWithDetailByArticleId articleId - " + articleId);

        return sArticleDetailWithDetailQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sArticleDetailWithDetailByArticleIdSelection,
                new String[]{articleId},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getMediaByType(Uri uri, String[] projection, String sortOrder) {

        Log.v(LOG_TAG, "getMediaByType uri - " + uri);
        String mediaType = AppContract.MediaEntry.getMediaTypeFromUri(uri);
        Log.v(LOG_TAG, "getMediaByType articleType - " + mediaType);

        return sArticleQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMediaByTypeSelection,
                new String[]{mediaType},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getMediaByCategory(Uri uri, String[] projection, String sortOrder) {

        Log.v(LOG_TAG, "getMediaByCategory uri - " + uri);
        String mediaCategory = AppContract.MediaEntry.getMediaCategoryFromUri(uri);
        Log.v(LOG_TAG, "getMediaByCategory mediaCategory - " + mediaCategory);

        return sMediaQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMediaByCategorySelection,
                new String[]{mediaCategory},
                null,
                null,
                sortOrder
        );
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = AppContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, AppContract.PATH_SETTINGS, SETTINGS);
        matcher.addURI(authority, AppContract.PATH_SETTINGS + "/VERSION/", SETTINGS_BY_VERSION);
        matcher.addURI(authority, AppContract.PATH_USER, USER);
        matcher.addURI(authority, AppContract.PATH_USER + "/USER/*", USER_BY_USERID);
        matcher.addURI(authority, AppContract.PATH_USER_PREF, USER_PREF);
        matcher.addURI(authority, AppContract.PATH_USER_PREF + "/USER/*", USER_PREF_BY_USERID);
        matcher.addURI(authority, AppContract.PATH_SYNC_LOG, SYNC_LOG);
        matcher.addURI(authority, AppContract.PATH_SYNC_LOG + "/USER/*", SYNC_LOG_BY_USERID);
        matcher.addURI(authority, AppContract.PATH_SUBSCRIBE, SUBSCRIBE);
        matcher.addURI(authority, AppContract.PATH_SUBSCRIBE + "/USER/*", SUBSCRIBE_BY_USERID);
        matcher.addURI(authority, AppContract.PATH_SHARE, SHARE);
        matcher.addURI(authority, AppContract.PATH_SHARE + "/USER/*", SHARE_BY_USERID);
        matcher.addURI(authority, AppContract.PATH_SHARE + "/USER/*/BABY/*", SHARE_BY_USERID_BABYID);
        matcher.addURI(authority, AppContract.PATH_SHARE + "/OWNER_USER/*", SHARE_BY_OWNERUSERID);
        matcher.addURI(authority, AppContract.PATH_SHARE + "/OWNER_USER/*/OWNER_BABY/*", SHARE_BY_OWNERUSERID_OWNERBABYID);
        matcher.addURI(authority, AppContract.PATH_BABY, BABY);
        matcher.addURI(authority, AppContract.PATH_BABY + "/USER/*", BABY_BY_USERID);
        matcher.addURI(authority, AppContract.PATH_BABY + "/USER/*/BABY/#", BABY_BY_USERID_BABYID);
        matcher.addURI(authority, AppContract.PATH_FEEDING, FEEDING);
        matcher.addURI(authority, AppContract.PATH_FEEDING + "/*", FEEDING_BY_ID);
        matcher.addURI(authority, AppContract.PATH_FEEDING + "/USER/*/BABY/#", FEEDING_BY_USERID_BABYID);
        matcher.addURI(authority, AppContract.PATH_DIAPER, DIAPER);
        matcher.addURI(authority, AppContract.PATH_DIAPER + "/*", DIAPER_BY_ID);
        matcher.addURI(authority, AppContract.PATH_DIAPER + "/USER/*/BABY/#", DIAPER_BY_USERID_BABYID);
        matcher.addURI(authority, AppContract.PATH_SLEEPING, SLEEPING);
        matcher.addURI(authority, AppContract.PATH_SLEEPING + "/*", SLEEPING_BY_ID);
        matcher.addURI(authority, AppContract.PATH_SLEEPING + "/USER/*/BABY/#", SLEEPING_BY_USERID_BABYID);
        matcher.addURI(authority, AppContract.PATH_HEALTH, HEALTH);
        matcher.addURI(authority, AppContract.PATH_HEALTH + "/*", HEALTH_BY_ID);
        matcher.addURI(authority, AppContract.PATH_HEALTH + "/USER/*/BABY/#", HEALTH_BY_USERID_BABYID);
        matcher.addURI(authority, AppContract.PATH_ACTIVITIES + "/USER/*/BABY/*/DATE/*/TYPE/*", ACTIVITIES_BY_USERID_BABYID);
        matcher.addURI(authority, AppContract.PATH_ACTIVITIES_SUMMARY + "/USER/*/BABY/*/DATE/*/TYPE/*", ACTIVITIES_SUMMARY_BY_USERID_BABYID);
        matcher.addURI(authority, AppContract.PATH_ARTICLE, ARTICLE);
        matcher.addURI(authority, AppContract.PATH_ARTICLE + "/TYPE/*", ARTICLE_BY_TYPE);
        matcher.addURI(authority, AppContract.PATH_ARTICLE + "/CATEGORY/*", ARTICLE_BY_CATEGORY);
        matcher.addURI(authority, AppContract.PATH_ARTICLE_DETAIL, ARTICLE_DETAIL);
        matcher.addURI(authority, AppContract.PATH_ARTICLE_DETAIL + "/ARTICLE/#", ARTICLE_DETAIL_BY_ARTICLEID);
        matcher.addURI(authority, AppContract.PATH_ARTICLE_DETAIL + "/ARTICLE/DETAIL/#", ARTICLE_DETAIL_WITH_DETAIL_BY_ARTICLEID);
        matcher.addURI(authority, AppContract.PATH_MEDIA, MEDIA);
        matcher.addURI(authority, AppContract.PATH_MEDIA + "/TYPE/*", MEDIA_BY_TYPE);
        matcher.addURI(authority, AppContract.PATH_MEDIA + "/CATEGORY/*", MEDIA_BY_CATEGORY);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new AppDBHelper(getContext());
        return true;
    }

    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        Log.v(LOG_TAG,"UriMatcher - " + uri);
        switch (match) {
            case SETTINGS:
                return AppContract.SettingsEntry.CONTENT_TYPE;
            case SETTINGS_BY_VERSION:
                return AppContract.SettingsEntry.CONTENT_ITEM_TYPE;
            case USER:
                return AppContract.UserEntry.CONTENT_TYPE;
            case USER_BY_USERID:
                return AppContract.UserEntry.CONTENT_ITEM_TYPE;
            case USER_PREF:
                return AppContract.UserPreferenceEntry.CONTENT_TYPE;
            case USER_PREF_BY_USERID:
                return AppContract.UserPreferenceEntry.CONTENT_ITEM_TYPE;
            case SYNC_LOG:
                return AppContract.SyncLogEntry.CONTENT_TYPE;
            case SYNC_LOG_BY_USERID:
                return AppContract.SyncLogEntry.CONTENT_ITEM_TYPE;
            case SUBSCRIBE:
                return AppContract.SubscribeEntry.CONTENT_TYPE;
            case SUBSCRIBE_BY_USERID:
                return AppContract.SubscribeEntry.CONTENT_ITEM_TYPE;
            case SHARE:
                return AppContract.ShareEntry.CONTENT_TYPE;
            case SHARE_BY_USERID:
                return AppContract.ShareEntry.CONTENT_ITEM_TYPE;
            case SHARE_BY_USERID_BABYID:
                return AppContract.ShareEntry.CONTENT_ITEM_TYPE;
            case SHARE_BY_OWNERUSERID:
                return AppContract.ShareEntry.CONTENT_ITEM_TYPE;
            case SHARE_BY_OWNERUSERID_OWNERBABYID:
                return AppContract.ShareEntry.CONTENT_ITEM_TYPE;
            case BABY:
                return AppContract.BabyEntry.CONTENT_TYPE;
            case BABY_BY_USERID:
                return AppContract.BabyEntry.CONTENT_ITEM_TYPE;
            case BABY_BY_USERID_BABYID:
                return AppContract.BabyEntry.CONTENT_ITEM_TYPE;
            case FEEDING:
                return AppContract.FeedingEntry.CONTENT_TYPE;
            case FEEDING_BY_USERID_BABYID:
                return AppContract.FeedingEntry.CONTENT_ITEM_TYPE;
            case DIAPER:
                return AppContract.DiaperEntry.CONTENT_TYPE;
            case DIAPER_BY_USERID_BABYID:
                return AppContract.DiaperEntry.CONTENT_ITEM_TYPE;
            case SLEEPING:
                return AppContract.SleepingEntry.CONTENT_TYPE;
            case SLEEPING_BY_USERID_BABYID:
                return AppContract.SleepingEntry.CONTENT_ITEM_TYPE;
            case HEALTH:
                return AppContract.HealthEntry.CONTENT_TYPE;
            case HEALTH_BY_USERID_BABYID:
                return AppContract.HealthEntry.CONTENT_ITEM_TYPE;
            case ACTIVITIES_BY_USERID_BABYID:
                return AppContract.ActivitiesEntry.CONTENT_ITEM_TYPE;
            case ACTIVITIES_SUMMARY_BY_USERID_BABYID:
                return AppContract.ActivitiesEntry.CONTENT_ITEM_TYPE;
            case ARTICLE:
                return AppContract.ArticleEntry.CONTENT_TYPE;
            case ARTICLE_BY_TYPE:
                return AppContract.ArticleEntry.CONTENT_ITEM_TYPE;
            case ARTICLE_BY_CATEGORY:
                return AppContract.ArticleEntry.CONTENT_ITEM_TYPE;
            case ARTICLE_DETAIL:
                return AppContract.ArticleDetailEntry.CONTENT_TYPE;
            case ARTICLE_DETAIL_BY_ARTICLEID:
                return AppContract.ArticleDetailEntry.CONTENT_ITEM_TYPE;
            case ARTICLE_DETAIL_WITH_DETAIL_BY_ARTICLEID:
                return AppContract.ArticleDetailEntry.CONTENT_ITEM_TYPE;
            case MEDIA:
                return AppContract.MediaEntry.CONTENT_TYPE;
            case MEDIA_BY_TYPE:
                return AppContract.MediaEntry.CONTENT_ITEM_TYPE;
            case MEDIA_BY_CATEGORY:
                return AppContract.MediaEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        Cursor retCursor;
        String tableName = getTableById(sUriMatcher.match(uri));
        if(!tableName.equals("Others")){
            retCursor = mOpenHelper.getReadableDatabase().query(
                    getTableById(sUriMatcher.match(uri)),
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );
        }else {
            switch (sUriMatcher.match(uri)) {

                case SETTINGS_BY_VERSION: {
                    retCursor = getSettingsByVersion(uri, projection, sortOrder);
                    break;
                }

                case USER_BY_USERID: {
                    retCursor = getUserByUserId(uri, projection, sortOrder);
                    break;
                }

                case USER_PREF_BY_USERID: {
                    retCursor = getUserPrefByUserId(uri, projection, sortOrder);
                    break;
                }

                case SYNC_LOG_BY_USERID: {
                    retCursor = getSyncLogByUserId(uri, projection, sortOrder);
                    break;
                }

                case SUBSCRIBE_BY_USERID: {
                    retCursor = getSubscribeByUserId(uri, projection, sortOrder);
                    break;
                }

                case SHARE_BY_USERID: {
                    retCursor = getShareByUserId(uri, projection, sortOrder);
                    break;
                }

                case SHARE_BY_USERID_BABYID: {
                    retCursor = getShareByUserIdBabyId(uri, projection, sortOrder);
                    break;
                }

                case SHARE_BY_OWNERUSERID: {
                    retCursor = getShareByOwnerUserId(uri, projection, sortOrder);
                    break;
                }

                case SHARE_BY_OWNERUSERID_OWNERBABYID: {
                    retCursor = getShareByOwnerUserIdOwnerBabyId(uri, projection, sortOrder);
                    break;
                }

                case BABY_BY_USERID: {
                    retCursor = getBabyByUserId(uri, projection, sortOrder);
                    break;
                }

                case BABY_BY_USERID_BABYID: {
                    retCursor = getBabyByUserIdBabyId(uri, projection, sortOrder);
                    break;
                }

                case FEEDING_BY_ID: {
                    retCursor = getFeedingById(uri, projection, sortOrder);
                    break;
                }

                case FEEDING_BY_USERID_BABYID: {
                    retCursor = getFeedingByUserIdBabyId(uri, projection, sortOrder);
                    break;
                }

                case DIAPER_BY_ID: {
                    retCursor = getDiaperById(uri, projection, sortOrder);
                    break;
                }

                case DIAPER_BY_USERID_BABYID: {
                    retCursor = getDiaperByUserIdBabyId(uri, projection, sortOrder);
                    break;
                }

                case SLEEPING_BY_ID: {
                    retCursor = getSleepingById(uri, projection, sortOrder);
                    break;
                }

                case SLEEPING_BY_USERID_BABYID: {
                    retCursor = getSleepingByUserIdBabyId(uri, projection, sortOrder);
                    break;
                }

                case HEALTH_BY_ID: {
                    retCursor = getHealthById(uri, projection, sortOrder);
                    break;
                }

                case HEALTH_BY_USERID_BABYID: {
                    retCursor = getHealthByUserIdBabyId(uri, projection, sortOrder);
                    break;
                }

                case ACTIVITIES_BY_USERID_BABYID: {
                    retCursor = getActivitiesByUserIdBabyId(uri, projection, sortOrder);
                    break;
                }

                case ACTIVITIES_SUMMARY_BY_USERID_BABYID: {
                    retCursor = getActivitiesSummaryByUserIdBabyId(uri, projection, sortOrder);
                    break;
                }

                case ARTICLE_BY_TYPE: {
                    retCursor = getArticleByType(uri, projection, sortOrder);
                    break;
                }

                case ARTICLE_BY_CATEGORY: {
                    retCursor = getArticleByCategory(uri, projection, sortOrder);
                    break;
                }

                case ARTICLE_DETAIL_BY_ARTICLEID: {
                    retCursor = getArticleDetailByArticleId(uri, projection, sortOrder);
                    break;
                }

                case ARTICLE_DETAIL_WITH_DETAIL_BY_ARTICLEID: {
                    retCursor = getArticleDetailWithDetailByArticleId(uri, projection, sortOrder);
                    break;
                }

                case MEDIA_BY_TYPE: {
                    retCursor = getMediaByType(uri, projection, sortOrder);
                    break;
                }

                case MEDIA_BY_CATEGORY: {
                    retCursor = getMediaByCategory(uri, projection, sortOrder);
                    break;
                }

                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        String tableName = getTableById(sUriMatcher.match(uri));
        if(!tableName.equals("Others")){
                long _id = db.insertWithOnConflict(tableName, null, values,SQLiteDatabase.CONFLICT_REPLACE);
                if ( _id > 0 )
                    return getUriByTableId(sUriMatcher.match(uri), _id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
        }else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";

        String tableName = getTableById(sUriMatcher.match(uri));
        if(!tableName.equals("Others")){
            rowsDeleted = db.delete(tableName, selection, selectionArgs);
        }else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsUpdated;

        String tableName = getTableById(sUriMatcher.match(uri));
        if(!tableName.equals("Others")){
            rowsUpdated = db.update(tableName, values, selection, selectionArgs);
        }else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int returnCount = 0;

        String tableName = getTableById(sUriMatcher.match(uri));
        if(!tableName.equals("Others")) {
            db.beginTransaction();
            returnCount = 0;
            try {
                for (ContentValues value : values) {
                    long _id = db.insert(tableName, null, value);
                    if (_id != -1) {
                        returnCount++;
                    }
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return returnCount;
        }else{
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }

    private String getTableById(int tableId){
        switch (tableId){
            case SETTINGS:
                return AppContract.SettingsEntry.TABLE_NAME;
            case USER:
                return AppContract.UserEntry.TABLE_NAME;
            case USER_PREF:
                return AppContract.UserPreferenceEntry.TABLE_NAME;
            case SYNC_LOG:
                return AppContract.SyncLogEntry.TABLE_NAME;
            case SUBSCRIBE:
                return AppContract.SubscribeEntry.TABLE_NAME;
            case SHARE:
                return AppContract.ShareEntry.TABLE_NAME;
            case BABY:
                return AppContract.BabyEntry.TABLE_NAME;
            case FEEDING:
                return AppContract.FeedingEntry.TABLE_NAME;
            case DIAPER:
                return AppContract.DiaperEntry.TABLE_NAME;
            case SLEEPING:
                return AppContract.SleepingEntry.TABLE_NAME;
            case HEALTH:
                return AppContract.HealthEntry.TABLE_NAME;
            case ARTICLE:
                return AppContract.ArticleEntry.TABLE_NAME;
            case ARTICLE_DETAIL:
                return AppContract.ArticleDetailEntry.TABLE_NAME;
            case MEDIA:
                return AppContract.MediaEntry.TABLE_NAME;
            default:
                return (new String("Others"));
        }
    }

    private Uri getUriByTableId(int tableId, long _id){
        switch (tableId){
            case USER:
                return AppContract.UserEntry.buildUserUri(_id);
            case USER_PREF:
                return AppContract.UserPreferenceEntry.buildUserPreferenceUri(_id);
            case SYNC_LOG:
                return AppContract.SyncLogEntry.buildSyncLogUri(_id);
            case SUBSCRIBE:
                return AppContract.SubscribeEntry.buildSubscribeUri(_id);
            case SHARE:
                return AppContract.ShareEntry.buildShareUri(_id);
            case BABY:
                return AppContract.BabyEntry.buildBabyUri(_id);
            case FEEDING:
                return AppContract.FeedingEntry.buildFeedingUri(_id);
            case DIAPER:
                return AppContract.DiaperEntry.buildDiaperUri(_id);
            case SLEEPING:
                return AppContract.SleepingEntry.buildSleepingUri(_id);
            case HEALTH:
                return AppContract.HealthEntry.buildHealthUri(_id);
            case ARTICLE:
                return AppContract.ArticleEntry.buildArticleUri(_id);
            case ARTICLE_DETAIL:
                return AppContract.ArticleDetailEntry.buildArticleDetailUri(_id);
            case MEDIA:
                return AppContract.MediaEntry.buildMediaUri(_id);
            default:
                return null;
        }
    }

}
