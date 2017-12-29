package com.simplesolutions2003.thirukkuralplus.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.simplesolutions2003.thirukkuralplus.data.AppContract.SectionsEntry;
import com.simplesolutions2003.thirukkuralplus.data.AppContract.GroupsEntry;
import com.simplesolutions2003.thirukkuralplus.data.AppContract.ChaptersEntry;
import com.simplesolutions2003.thirukkuralplus.data.AppContract.KuralsEntry;

import static com.simplesolutions2003.thirukkuralplus.data.AppContract.CONTENT_AUTHORITY;
import static com.simplesolutions2003.thirukkuralplus.data.AppContract.PATH_CHAPTERS;
import static com.simplesolutions2003.thirukkuralplus.data.AppContract.PATH_GROUPS;
import static com.simplesolutions2003.thirukkuralplus.data.AppContract.PATH_KURALS;
import static com.simplesolutions2003.thirukkuralplus.data.AppContract.PATH_SECTIONS;

/**
 * Created by SuriyaKumar on 8/16/2016.
 */
public class AppProvider extends ContentProvider {
    private final String LOG_TAG = AppProvider.class.getSimpleName();

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private AppDBHelper mOpenHelper;

    private static final SQLiteQueryBuilder sChaptersQueryBuilder;
    private static final SQLiteQueryBuilder sKuralsQueryBuilder;

    static final int SECTIONS = 100;
    static final int GROUPS = 200;
    static final int CHAPTERS = 300;
    static final int CHAPTERS_BY_SECTION = 301;
    static final int CHAPTERS_BY_GROUP = 302;
    static final int KURALS = 400;
    static final int KURALS_BY_CHAPTER = 401;
    static final int KURALS_BY_SEARCH = 402;
    static final int KURALS_BY_FAVORITE = 403;
    static final int KURALS_BY_ID = 404;

    static{
        sChaptersQueryBuilder = new SQLiteQueryBuilder();

        sChaptersQueryBuilder.setTables(
                ChaptersEntry.TABLE_NAME +
                        " INNER JOIN " + SectionsEntry.TABLE_NAME +
                        " ON " + SectionsEntry.TABLE_NAME +
                        "." + SectionsEntry._ID +
                        " = " + ChaptersEntry.TABLE_NAME +
                        "." + ChaptersEntry.COLUMN_SECTION_ID +
                        " INNER JOIN " + GroupsEntry.TABLE_NAME +
                        " ON " + GroupsEntry.TABLE_NAME +
                        "." + GroupsEntry._ID +
                        " = " + ChaptersEntry.TABLE_NAME +
                        "." + ChaptersEntry.COLUMN_GROUP_ID);
    }

    static{
        sKuralsQueryBuilder = new SQLiteQueryBuilder();

        sKuralsQueryBuilder.setTables(
                KuralsEntry.TABLE_NAME +
                        " INNER JOIN " + ChaptersEntry.TABLE_NAME +
                        " ON " + ChaptersEntry.TABLE_NAME +
                        "." + ChaptersEntry._ID +
                        " = " + KuralsEntry.TABLE_NAME +
                        "." + KuralsEntry.COLUMN_CHAPTER_ID +
                        " INNER JOIN " + SectionsEntry.TABLE_NAME +
                        " ON " + SectionsEntry.TABLE_NAME +
                        "." + SectionsEntry._ID +
                        " = " + ChaptersEntry.TABLE_NAME +
                        "." + ChaptersEntry.COLUMN_SECTION_ID +
                        " INNER JOIN " + GroupsEntry.TABLE_NAME +
                        " ON " + GroupsEntry.TABLE_NAME +
                        "." + GroupsEntry._ID +
                        " = " + ChaptersEntry.TABLE_NAME +
                        "." + ChaptersEntry.COLUMN_GROUP_ID);
    }

    private static final String sChapterBySectionSelection =
            ChaptersEntry.TABLE_NAME +
                    "." + ChaptersEntry.COLUMN_SECTION_ID + " = ? ";

    private static final String sChapterByGroupSelection =
            ChaptersEntry.TABLE_NAME +
                    "." + ChaptersEntry.COLUMN_GROUP_ID + " = ? ";

    private static final String sKuralByChapterSelection =
            KuralsEntry.TABLE_NAME +
                    "." + KuralsEntry.COLUMN_CHAPTER_ID + " = ? ";

    private static final String sKuralByIdSelection =
            KuralsEntry.TABLE_NAME +
                    "." + KuralsEntry._ID + " = ? ";
    
    private static final String sKuralBySearchSelection =
            KuralsEntry.TABLE_NAME +
                    "." + KuralsEntry.COLUMN_NAME + " LIKE ? " +
                    " OR " +
                    KuralsEntry.TABLE_NAME +
                    "." + KuralsEntry.COLUMN_NAME_ENG + " LIKE ? " +
                    " OR " +
                    KuralsEntry.TABLE_NAME +
                    "." + KuralsEntry.COLUMN_EXP_MUVA + " LIKE ? " +
                    " OR " +
                    KuralsEntry.TABLE_NAME +
                    "." + KuralsEntry.COLUMN_EXP_SOLO + " LIKE ? " +
                    " OR " +
                    KuralsEntry.TABLE_NAME +
                    "." + KuralsEntry.COLUMN_EXP_MUKA + " LIKE ? " +
                    " OR " +
                    KuralsEntry.TABLE_NAME +
                    "." + KuralsEntry.COLUMN_COUPLET + " LIKE ? " +
                    " OR " +
                    KuralsEntry.TABLE_NAME +
                    "." + KuralsEntry.COLUMN_TRANS + " LIKE ? " +
                    " OR " +
                    SectionsEntry.TABLE_NAME +
                    "." + SectionsEntry.COLUMN_NAME + " LIKE ? " +
                    " OR " +
                    SectionsEntry.TABLE_NAME +
                    "." + SectionsEntry.COLUMN_NAME_ENG + " LIKE ? " +
                    " OR " +
                    GroupsEntry.TABLE_NAME +
                    "." + GroupsEntry.COLUMN_NAME + " LIKE ? " +
                    " OR " +
                    GroupsEntry.TABLE_NAME +
                    "." + GroupsEntry.COLUMN_NAME_ENG + " LIKE ? " +
                    " OR " +
                    ChaptersEntry.TABLE_NAME +
                    "." + ChaptersEntry.COLUMN_NAME + " LIKE ? " +
                    " OR " +
                    ChaptersEntry.TABLE_NAME +
                    "." + ChaptersEntry.COLUMN_NAME_ENG + " LIKE ? ";

    private static final String sKuralsByFavoriteSelection =
            KuralsEntry.TABLE_NAME +
                    "." + KuralsEntry.COLUMN_FAVORITE + " = 1 ";

    private Cursor getChaptersBySection(Uri uri, String[] projection, String sortOrder) {

        Log.v(LOG_TAG, "getChaptersBySection uri - " + uri);
        Long sectionId = ChaptersEntry.getSectionFromUri(uri);
        Log.v(LOG_TAG, "getChaptersBySection sectionId - " + sectionId);

        return sChaptersQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sChapterBySectionSelection,
                new String[]{Long.toString(sectionId)},
                null,
                null,
                sortOrder
        );
    }


    private Cursor getChaptersByGroup(Uri uri, String[] projection, String sortOrder) {

        Log.v(LOG_TAG, "getChaptersByGroup uri - " + uri);
        Long groupId = ChaptersEntry.getGroupFromUri(uri);
        Log.v(LOG_TAG, "getChaptersByGroup groupId - " + groupId);

        return sChaptersQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sChapterByGroupSelection,
                new String[]{Long.toString(groupId)},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getKuralsById(Uri uri, String[] projection, String sortOrder) {

        Log.v(LOG_TAG, "getKuralsById uri - " + uri);
        long kuralId = KuralsEntry.getIdFromUri(uri);
        Log.v(LOG_TAG, "getKuralsById kuralId - " + kuralId);

        return sKuralsQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sKuralByIdSelection,
                new String[]{Long.toString(kuralId)},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getKuralsByChapter(Uri uri, String[] projection, String sortOrder) {

        Log.v(LOG_TAG, "getKuralsByChapter uri - " + uri);
        long chapterId = KuralsEntry.getKuralChapterFromUri(uri);
        Log.v(LOG_TAG, "getKuralsByChapter chapterId - " + chapterId);

        return sKuralsQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sKuralByChapterSelection,
                new String[]{Long.toString(chapterId)},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getKuralsBySearch(Uri uri, String[] projection, String sortOrder) {

        Log.v(LOG_TAG, "getKuralsBySearch uri - " + uri);
        String kuralSearch = '%' + KuralsEntry.getKuralSearchFromUri(uri) + '%';
        Log.v(LOG_TAG, "getKuralsBySearch kuralSearch - " + kuralSearch);

        return sKuralsQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sKuralBySearchSelection,
                new String[]{kuralSearch,kuralSearch,kuralSearch,kuralSearch,
                        kuralSearch,kuralSearch,kuralSearch,kuralSearch,
                        kuralSearch,kuralSearch,kuralSearch,kuralSearch,kuralSearch},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getKuralsByFavorite(Uri uri, String[] projection, String sortOrder) {

        Log.v(LOG_TAG, "getKuralsByFavorite uri - " + uri);

        return sKuralsQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sKuralsByFavoriteSelection,
                null,
                null,
                null,
                sortOrder
        );
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CONTENT_AUTHORITY;

        matcher.addURI(authority, PATH_SECTIONS, SECTIONS);
        matcher.addURI(authority, PATH_GROUPS, GROUPS);
        matcher.addURI(authority, PATH_CHAPTERS, CHAPTERS);
        matcher.addURI(authority, PATH_CHAPTERS + "/SECTION/*", CHAPTERS_BY_SECTION);
        matcher.addURI(authority, PATH_CHAPTERS + "/GROUP/*", CHAPTERS_BY_GROUP);
        matcher.addURI(authority, PATH_KURALS, KURALS);
        matcher.addURI(authority, PATH_KURALS + "/CHAPTER/*", KURALS_BY_CHAPTER);
        matcher.addURI(authority, PATH_KURALS + "/SEARCH/*", KURALS_BY_SEARCH);
        matcher.addURI(authority, PATH_KURALS + "/FAVORITES", KURALS_BY_FAVORITE);
        matcher.addURI(authority, PATH_KURALS + "/*", KURALS_BY_ID);
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
            case SECTIONS:
                return SectionsEntry.CONTENT_TYPE;
            case GROUPS:
                return GroupsEntry.CONTENT_TYPE;
            case CHAPTERS:
                return ChaptersEntry.CONTENT_TYPE;
            case CHAPTERS_BY_SECTION:
                return ChaptersEntry.CONTENT_ITEM_TYPE;
            case CHAPTERS_BY_GROUP:
                return ChaptersEntry.CONTENT_ITEM_TYPE;
            case KURALS:
                return KuralsEntry.CONTENT_TYPE;
            case KURALS_BY_ID:
                return KuralsEntry.CONTENT_ITEM_TYPE;
            case KURALS_BY_CHAPTER:
                return KuralsEntry.CONTENT_ITEM_TYPE;
            case KURALS_BY_SEARCH:
                return KuralsEntry.CONTENT_ITEM_TYPE;
            case KURALS_BY_FAVORITE:
                return KuralsEntry.CONTENT_ITEM_TYPE;
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

                case CHAPTERS_BY_SECTION: {
                    retCursor = getChaptersBySection(uri, projection, sortOrder);
                    break;
                }

                case CHAPTERS_BY_GROUP: {
                    retCursor = getChaptersByGroup(uri, projection, sortOrder);
                    break;
                }

                case KURALS_BY_ID: {
                    retCursor = getKuralsById(uri, projection, sortOrder);
                    break;
                }
                
                case KURALS_BY_CHAPTER: {
                    retCursor = getKuralsByChapter(uri, projection, sortOrder);
                    break;
                }

                case KURALS_BY_SEARCH: {
                    retCursor = getKuralsBySearch(uri, projection, sortOrder);
                    break;
                }

                case KURALS_BY_FAVORITE: {
                    retCursor = getKuralsByFavorite(uri, projection, sortOrder);
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
            case SECTIONS:
                return SectionsEntry.TABLE_NAME;
            case GROUPS:
                return GroupsEntry.TABLE_NAME;
            case CHAPTERS:
                return ChaptersEntry.TABLE_NAME;
            case KURALS:
                return KuralsEntry.TABLE_NAME;
            default:
                return (new String("Others"));
        }
    }

    private Uri getUriByTableId(int tableId, long _id){
        switch (tableId){
            case SECTIONS:
                return SectionsEntry.buildSectionsUri(_id);
            case GROUPS:
                return GroupsEntry.buildGroupsUri(_id);
            case CHAPTERS:
                return ChaptersEntry.buildChaptersUri(_id);
            case KURALS:
                return KuralsEntry.buildKuralsUri(_id);
            default:
                return null;
        }
    }

}
