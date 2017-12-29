package com.simplesolutions2003.lookupcoupons.data;

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

    private static final SQLiteQueryBuilder sArticleQueryBuilder;
    private static final SQLiteQueryBuilder sArticleDetailQueryBuilder;
    private static final SQLiteQueryBuilder sArticleDetailWithDetailQueryBuilder;
    private static final SQLiteQueryBuilder sArticleFavoriteQueryBuilder;

    static final int MENU = 1000;
    static final int ARTICLE = 2000;
    static final int ARTICLE_BY_TYPE = 2001;
    static final int ARTICLE_BY_CATEGORY = 2002;
    static final int ARTICLE_BY_TYPE_SEARCH = 2003;
    static final int ARTICLE_BY_ID = 2004;
    static final int ARTICLE_DETAIL = 3000;
    static final int ARTICLE_DETAIL_BY_ARTICLEID = 3001;
    static final int ARTICLE_DETAIL_WITH_DETAIL_BY_ARTICLEID = 3002;
    static final int FAVORITE = 4000;
    static final int ARTICLE_BY_FAVORITE = 4001;

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
        sArticleDetailWithDetailQueryBuilder = new SQLiteQueryBuilder();

        sArticleDetailWithDetailQueryBuilder.setTables(
                AppContract.ArticleEntry.TABLE_NAME +
                        " INNER JOIN " + AppContract.ArticleDetailEntry.TABLE_NAME +
                        " ON " + AppContract.ArticleEntry.TABLE_NAME +
                        "." + AppContract.ArticleEntry._ID +
                        " = " + AppContract.ArticleDetailEntry.TABLE_NAME +
                        "." + AppContract.ArticleDetailEntry.COLUMN_ARTICLE_ID);
    }

    static{
        sArticleFavoriteQueryBuilder = new SQLiteQueryBuilder();

        sArticleFavoriteQueryBuilder.setTables(
                AppContract.ArticleEntry.TABLE_NAME +
                        " INNER JOIN " + AppContract.FavoriteEntry.TABLE_NAME +
                        " ON " + AppContract.ArticleEntry.TABLE_NAME +
                        "." + AppContract.ArticleEntry._ID +
                        " = " + AppContract.FavoriteEntry.TABLE_NAME +
                        "." + AppContract.FavoriteEntry.COLUMN_ARTICLE_ID);
    }

    private static final String sArticleByIdSelection =
            AppContract.ArticleEntry.TABLE_NAME +
                    "." + AppContract.ArticleEntry._ID + " = ? ";

    private static final String sArticleByTypeSelection =
            AppContract.ArticleEntry.TABLE_NAME +
                    "." + AppContract.ArticleEntry.COLUMN_TYPE + " = ? ";

    private static final String sArticleByTypeSearchSelection =
            AppContract.ArticleEntry.TABLE_NAME +
                    "." + AppContract.ArticleEntry.COLUMN_TYPE + " = ? " +
                    " AND " +
            AppContract.ArticleEntry.TABLE_NAME +
                    "." + AppContract.ArticleEntry.COLUMN_TITLE + " LIKE ? ";

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

    private Cursor getArticleById(Uri uri, String[] projection, String sortOrder) {

        Log.v(LOG_TAG, "getArticleById uri - " + uri);
        Long articleId = AppContract.ArticleEntry.getIdFromUri(uri);
        Log.v(LOG_TAG, "getArticleById articleId - " + articleId);

        return sArticleQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sArticleByIdSelection,
                new String[]{Long.toString(articleId)},
                null,
                null,
                sortOrder
        );
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

    private Cursor getArticleByTypeSearch(Uri uri, String[] projection, String sortOrder) {

        Log.v(LOG_TAG, "getArticleByTypeSearch uri - " + uri);
        String articleType = AppContract.ArticleEntry.getArticleTypeFromUri(uri);
        String articleSearch = '%' + AppContract.ArticleEntry.getArticleSearchFromUri(uri) + '%';
        Log.v(LOG_TAG, "getArticleByTypeSearch articleType - " + articleType);
        Log.v(LOG_TAG, "getArticleByTypeSearch articleSearch - " + articleSearch);

        return sArticleQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sArticleByTypeSearchSelection,
                new String[]{articleType,articleSearch},
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

    private Cursor getArticleByFavorite(Uri uri, String[] projection, String sortOrder) {

        Log.v(LOG_TAG, "getArticleByFavorite uri - " + uri);

        return sArticleFavoriteQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = AppContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, AppContract.PATH_MENU, MENU);
        matcher.addURI(authority, AppContract.PATH_ARTICLE, ARTICLE);
        matcher.addURI(authority, AppContract.PATH_ARTICLE + "/TYPE/*/SEARCH/*", ARTICLE_BY_TYPE_SEARCH);
        matcher.addURI(authority, AppContract.PATH_ARTICLE + "/TYPE/*", ARTICLE_BY_TYPE);
        matcher.addURI(authority, AppContract.PATH_ARTICLE + "/CATEGORY/*", ARTICLE_BY_CATEGORY);
        matcher.addURI(authority, AppContract.PATH_ARTICLE_DETAIL, ARTICLE_DETAIL);
        matcher.addURI(authority, AppContract.PATH_ARTICLE_DETAIL + "/ARTICLE/DETAIL/*", ARTICLE_DETAIL_WITH_DETAIL_BY_ARTICLEID);
        matcher.addURI(authority, AppContract.PATH_ARTICLE_DETAIL + "/ARTICLE/*", ARTICLE_DETAIL_BY_ARTICLEID);
        matcher.addURI(authority, AppContract.PATH_ARTICLE + "/*", ARTICLE_BY_ID);
        matcher.addURI(authority, AppContract.PATH_FAVORITE, FAVORITE);
        matcher.addURI(authority, AppContract.PATH_FAVORITE + "/ARTICLE", ARTICLE_BY_FAVORITE);
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
            case MENU:
                return AppContract.MenuEntry.CONTENT_TYPE;
            case ARTICLE:
                return AppContract.ArticleEntry.CONTENT_TYPE;
            case ARTICLE_BY_ID:
                return AppContract.ArticleEntry.CONTENT_ITEM_TYPE;
            case ARTICLE_BY_TYPE:
                return AppContract.ArticleEntry.CONTENT_ITEM_TYPE;
            case ARTICLE_BY_TYPE_SEARCH:
                return AppContract.ArticleEntry.CONTENT_ITEM_TYPE;
            case ARTICLE_BY_CATEGORY:
                return AppContract.ArticleEntry.CONTENT_ITEM_TYPE;
            case ARTICLE_DETAIL:
                return AppContract.ArticleDetailEntry.CONTENT_TYPE;
            case ARTICLE_DETAIL_BY_ARTICLEID:
                return AppContract.ArticleDetailEntry.CONTENT_ITEM_TYPE;
            case ARTICLE_DETAIL_WITH_DETAIL_BY_ARTICLEID:
                return AppContract.ArticleDetailEntry.CONTENT_ITEM_TYPE;
            case FAVORITE:
                return AppContract.FavoriteEntry.CONTENT_TYPE;
            case ARTICLE_BY_FAVORITE:
                return AppContract.ArticleEntry.CONTENT_ITEM_TYPE;
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

                case ARTICLE_BY_ID: {
                    retCursor = getArticleById(uri, projection, sortOrder);
                    break;
                }

                case ARTICLE_BY_TYPE: {
                    retCursor = getArticleByType(uri, projection, sortOrder);
                    break;
                }

                case ARTICLE_BY_TYPE_SEARCH: {
                    retCursor = getArticleByTypeSearch(uri, projection, sortOrder);
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

                case ARTICLE_BY_FAVORITE: {
                    retCursor = getArticleByFavorite(uri, projection, sortOrder);
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
            case MENU:
                return AppContract.MenuEntry.TABLE_NAME;
            case ARTICLE:
                return AppContract.ArticleEntry.TABLE_NAME;
            case ARTICLE_DETAIL:
                return AppContract.ArticleDetailEntry.TABLE_NAME;
            case FAVORITE:
                return AppContract.FavoriteEntry.TABLE_NAME;
            default:
                return (new String("Others"));
        }
    }

    private Uri getUriByTableId(int tableId, long _id){
        switch (tableId){
            case MENU:
                return AppContract.MenuEntry.buildMenuUri(_id);
            case ARTICLE:
                return AppContract.ArticleEntry.buildArticleUri(_id);
            case ARTICLE_DETAIL:
                return AppContract.ArticleDetailEntry.buildArticleDetailUri(_id);
            case FAVORITE:
                return AppContract.FavoriteEntry.buildFavoriteUri(_id);
            default:
                return null;
        }
    }

}
