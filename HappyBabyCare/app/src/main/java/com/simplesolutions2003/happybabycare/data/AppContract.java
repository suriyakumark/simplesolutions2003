package com.simplesolutions2003.happybabycare.data;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;
/**
 * Created by SuriyaKumar on 8/16/2016.
 */
public class AppContract {
    private static final String LOG_TAG = AppContract.class.getSimpleName();

    public static final String CONTENT_AUTHORITY = "com.simplesolutions2003.happybabycare";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_SETTINGS = "settings";
    public static final String PATH_USER = "user";
    public static final String PATH_USER_PREF = "user_preference";
    public static final String PATH_SYNC_LOG = "sync_log";
    public static final String PATH_SUBSCRIBE = "subscribe";
    public static final String PATH_SHARE = "share";
    public static final String PATH_BABY = "baby";
    public static final String PATH_FEEDING = "feeding";
    public static final String PATH_DIAPER = "diaper";
    public static final String PATH_SLEEPING = "sleeping";
    public static final String PATH_HEALTH = "health";
    public static final String PATH_ACTIVITIES = "activities";
    public static final String PATH_ACTIVITIES_SUMMARY = "activities_summary";
    public static final String PATH_ARTICLE = "article";
    public static final String PATH_ARTICLE_DETAIL = "article_detail";
    public static final String PATH_MEDIA = "media";

    public static final class SettingsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SETTINGS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SETTINGS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SETTINGS;

        public static final String TABLE_NAME = "settings";
        public static final String _ID = "_id";
        public static final String COLUMN_VERSION = "version";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_VALUE = "value";
        public static final String COLUMN_ACTIVE = "active";
        public static final String COLUMN_UPDATED_TS = "updated_timestamp";

        public static Uri buildSettingsUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

        public static Uri buildSettingsByVersionUri(String version) {
            return CONTENT_URI.buildUpon().appendPath("VERSION").appendPath(version).build();
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }

        public static String getVersionFromUri(Uri uri) {
            if(uri.getPathSegments().get(2).equals("VERSION")) {
                return uri.getPathSegments().get(3);
            }
            return null;
        }

    }

    public static final class UserEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER;

        public static final String TABLE_NAME = "user";

        public static final String _ID = "_id";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_ACTIVE = "active";
        public static final String COLUMN_LAST_SYNC_TS = "last_sync_timestamp";

        public static Uri buildUserUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

        public static Uri buildUserByUserIdUri(String userId) {
            return CONTENT_URI.buildUpon().appendPath("USER").appendPath(userId).build();
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static String getUserIdFromUri(Uri uri) {
            if(uri.getPathSegments().get(1).equals("USER")) {
                return uri.getPathSegments().get(2);
            }
            return null;
        }
    }

    public static final class UserPreferenceEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER_PREF).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER_PREF;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER_PREF;

        public static final String TABLE_NAME = "user_preference";
        public static final String _ID = "_id";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_VALUE = "value";

        public static Uri buildUserPreferenceUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

        public static Uri buildUserByUserIdUri(String userId) {
            return CONTENT_URI.buildUpon().appendPath("USER").appendPath(userId).build();
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static String getUserIdFromUri(Uri uri) {
            if(uri.getPathSegments().get(1).equals("USER")) {
                return uri.getPathSegments().get(2);
            }
            return null;
        }

    }

    public static final class SyncLogEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SYNC_LOG).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SYNC_LOG;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SYNC_LOG;

        public static final String TABLE_NAME = "sync_log";
        public static final String _ID = "_id";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_TABLE_NAME = "table_name";
        public static final String COLUMN_LAST_SYNC_TS = "last_sync_timestamp";

        public static Uri buildSyncLogUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

        public static Uri buildSyncLogByUserIdUri(String userId) {
            return CONTENT_URI.buildUpon().appendPath("USER").appendPath(userId).build();
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static String getUserIdFromUri(Uri uri) {
            if(uri.getPathSegments().get(1).equals("USER")) {
                return uri.getPathSegments().get(2);
            }
            return null;
        }
    }

    public static final class SubscribeEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SUBSCRIBE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUBSCRIBE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUBSCRIBE;

        public static final String TABLE_NAME = "subscribe";
        public static final String _ID = "_id";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_OWNER_USER_ID = "owner_user_id";

        public static Uri buildSubscribeUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

        public static Uri buildSubscribeByUserIdUri(String userId) {
            return CONTENT_URI.buildUpon().appendPath("USER").appendPath(userId).build();
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static String getUserIdFromUri(Uri uri) {
            if(uri.getPathSegments().get(1).equals("USER")) {
                return uri.getPathSegments().get(2);
            }
            return null;
        }

    }


    public static final class ShareEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SHARE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SHARE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SHARE;

        public static final String TABLE_NAME = "share_manage";
        public static final String _ID = "_id";
        public static final String COLUMN_BABY_ID = "baby_id";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_OWNER_BABY_ID = "owner_baby_id";
        public static final String COLUMN_OWNER_USER_ID = "owner_user_id";

        public static Uri buildShareUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

        public static Uri buildShareByUserIdUri(String userId) {
            return CONTENT_URI.buildUpon().appendPath("USER").appendPath(userId).build();
        }

        public static Uri buildShareByUserIdBabyIdUri(String userId, Long babyId) {
            return CONTENT_URI.buildUpon().appendPath("USER").appendPath(userId).appendPath("BABY").appendPath(Long.toString(babyId)).build();
        }

        public static Uri buildShareByOwnerUserIdOwnerBabyIdUri(String userId, Long babyId) {
            return CONTENT_URI.buildUpon().appendPath("OWNER_USER").appendPath(userId).appendPath("OWNER_BABY").appendPath(Long.toString(babyId)).build();
        }

        public static Uri buildShareByOwnerUserIdUri(String userId) {
            return CONTENT_URI.buildUpon().appendPath("OWNER_USER").appendPath(userId).build();
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static String getUserIdFromUri(Uri uri) {
            if(uri.getPathSegments().get(1).equals("USER")) {
                return uri.getPathSegments().get(2);
            }
            return null;
        }

        public static long getBabyIdFromUri(Uri uri) {
            if(uri.getPathSegments().get(3).equals("BABY")) {
                return Long.parseLong(uri.getPathSegments().get(4));
            }
            return -1;
        }

        public static String getOwnerUserIdFromUri(Uri uri) {
            if(uri.getPathSegments().get(1).equals("OWNER_USER")) {
                return uri.getPathSegments().get(2);
            }
            return null;
        }

        public static long getOwnerBabyIdFromUri(Uri uri) {
            if(uri.getPathSegments().get(3).equals("OWNER_BABY")) {
                return Long.parseLong(uri.getPathSegments().get(4));
            }
            return -1;
        }

    }

    public static final class BabyEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BABY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BABY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BABY;

        public static final String TABLE_NAME = "baby";
        public static final String _ID = "_id";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_OWNER_BABY_ID = "owner_baby_id";
        public static final String COLUMN_OWNER_USER_ID = "owner_user_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_GENDER = "gender";
        public static final String COLUMN_BIRTH_DATE = "birth_date";
        public static final String COLUMN_DUE_DATE = "due_date";
        public static final String COLUMN_COLOR = "color";
        public static final String COLUMN_PHOTO = "photo";
        public static final String COLUMN_ACTIVE = "active";

        public static Uri buildBabyUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

        public static Uri buildBabyByUserIdUri(String userId) {
            return CONTENT_URI.buildUpon().appendPath("USER").appendPath(userId).build();
        }

        public static Uri buildBabyByUserIdBabyIdUri(String userId, Long babyId) {
            return CONTENT_URI.buildUpon().appendPath("USER").appendPath(userId).appendPath("BABY").appendPath(Long.toString(babyId)).build();
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static String getUserIdFromUri(Uri uri) {
            if(uri.getPathSegments().get(1).equals("USER")) {
                return uri.getPathSegments().get(2);
            }
            return null;
        }

        public static long getBabyIdFromUri(Uri uri) {
            if(uri.getPathSegments().get(3).equals("BABY")) {
                return Long.parseLong(uri.getPathSegments().get(4));
            }
            return -1;
        }

    }

    public static final class FeedingEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FEEDING).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FEEDING;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FEEDING;

        public static final String TABLE_NAME = "feeding";
        public static final String _ID = "_id";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_BABY_ID = "baby_id";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_UNIT = "unit";
        public static final String COLUMN_NOTES = "notes";
        public static final String COLUMN_LAST_UPDATED_TS = "last_updated_timestamp";

        public static Uri buildFeedingUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

        public static Uri buildFeedingByUserIdBabyIdUri(String user_id, long baby_id) {
            return CONTENT_URI.buildUpon().appendPath("USER").appendPath(user_id).appendPath("BABY").appendPath(Long.toString(baby_id)).build();
        }


        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static String getUserIdFromUri(Uri uri) {
            if(uri.getPathSegments().get(1).equals("USER")) {
                return uri.getPathSegments().get(2);
            }
            return null;
        }

        public static long getBabyIdFromUri(Uri uri) {
            if(uri.getPathSegments().get(3).equals("BABY")) {
                return Long.parseLong(uri.getPathSegments().get(4));
            }
            return -1;
        }

    }

    public static final class DiaperEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_DIAPER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DIAPER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DIAPER;

        public static final String TABLE_NAME = "diaper";
        public static final String _ID = "_id";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_BABY_ID = "baby_id";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_CREAM = "cream";
        public static final String COLUMN_NOTES = "notes";
        public static final String COLUMN_LAST_UPDATED_TS = "last_updated_timestamp";

        public static Uri buildDiaperUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

        public static Uri buildDiaperByUserIdBabyIdUri(String user_id, long baby_id) {
            return CONTENT_URI.buildUpon().appendPath("USER").appendPath(user_id).appendPath("BABY").appendPath(Long.toString(baby_id)).build();
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static String getUserIdFromUri(Uri uri) {
            if(uri.getPathSegments().get(1).equals("USER")) {
                return uri.getPathSegments().get(2);
            }
            return null;
        }

        public static long getBabyIdFromUri(Uri uri) {
            if(uri.getPathSegments().get(3).equals("BABY")) {
                return Long.parseLong(uri.getPathSegments().get(4));
            }
            return -1;
        }
    }

    public static final class SleepingEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SLEEPING).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SLEEPING;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SLEEPING;

        public static final String TABLE_NAME = "sleeping";
        public static final String _ID = "_id";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_BABY_ID = "baby_id";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_END_TIME = "end_time";
        public static final String COLUMN_DURATION = "duration";
        public static final String COLUMN_WHERE_SLEEP = "where_sleep";
        public static final String COLUMN_NOTES = "notes";
        public static final String COLUMN_LAST_UPDATED_TS = "last_updated_timestamp";

        public static Uri buildSleepingUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

        public static Uri buildSleepingByUserIdBabyIdUri(String user_id, long baby_id) {
            return CONTENT_URI.buildUpon().appendPath("USER").appendPath(user_id).appendPath("BABY").appendPath(Long.toString(baby_id)).build();
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static String getUserIdFromUri(Uri uri) {
            if(uri.getPathSegments().get(1).equals("USER")) {
                return uri.getPathSegments().get(2);
            }
            return null;
        }

        public static long getBabyIdFromUri(Uri uri) {
            if(uri.getPathSegments().get(3).equals("BABY")) {
                return Long.parseLong(uri.getPathSegments().get(4));
            }
            return -1;
        }
    }

    public static final class HealthEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_HEALTH).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HEALTH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HEALTH;

        public static final String TABLE_NAME = "health";
        public static final String _ID = "_id";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_BABY_ID = "baby_id";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_VALUE = "value";
        public static final String COLUMN_UNIT = "unit";
        public static final String COLUMN_NOTES = "notes";
        public static final String COLUMN_LAST_UPDATED_TS = "last_updated_timestamp";

        public static Uri buildHealthUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

        public static Uri buildHealthByUserBabyUri(String user_id, long baby_id) {
            return CONTENT_URI.buildUpon().appendPath("USER").appendPath(user_id).appendPath("BABY").appendPath(Long.toString(baby_id)).build();
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static String getUserIdFromUri(Uri uri) {
            if(uri.getPathSegments().get(1).equals("USER")) {
                return uri.getPathSegments().get(2);
            }
            return null;
        }

        public static long getBabyIdFromUri(Uri uri) {
            if(uri.getPathSegments().get(3).equals("BABY")) {
                return Long.parseLong(uri.getPathSegments().get(4));
            }
            return -1;
        }
    }


    public static final class ActivitiesEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ACTIVITIES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ACTIVITIES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ACTIVITIES;

        public static final String COLUMN_ACTIVITY_ID = "_id";
        public static final String COLUMN_ACTIVITY_TYPE = "activity_type";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_BABY_ID = "baby_id";
        public static final String COLUMN_TIMESTAMP = "activity_timestamp";
        public static final String COLUMN_DATE = "activity_date";
        public static final String COLUMN_TIME = "activity_time";
        public static final String COLUMN_SUMMARY = "activity_summary";
        public static final String COLUMN_DETAIL = "activity_detail";
        public static final String COLUMN_LAST_UPDATED_TS = "activity_last_updated_timestamp";

        public static Uri buildActivitiesByUserIdBabyIdUri(String user_id, long baby_id,String activity_date,String activity_type) {
            return CONTENT_URI.buildUpon().appendPath("USER").appendPath(user_id).appendPath("BABY").appendPath(Long.toString(baby_id)).appendPath("DATE").appendPath(activity_date).appendPath("TYPE").appendPath(activity_type).build();
        }

        public static Uri buildActivitiesSummaryByUserIdBabyIdUri(String user_id, long baby_id,String activity_date,String activity_type) {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_ACTIVITIES_SUMMARY).appendPath("USER").appendPath(user_id).appendPath("BABY").appendPath(Long.toString(baby_id)).appendPath("DATE").appendPath(activity_date).appendPath("TYPE").appendPath(activity_type).build();
        }

        public static String getUserIdFromUri(Uri uri) {
            if(uri.getPathSegments().get(1).equals("USER")) {
                return uri.getPathSegments().get(2);
            }
            return null;
        }

        public static long getBabyIdFromUri(Uri uri) {
            if(uri.getPathSegments().get(3).equals("BABY")) {
                return Long.parseLong(uri.getPathSegments().get(4));
            }
            return -1;
        }

        public static String getActivityDateFromUri(Uri uri) {
            if(uri.getPathSegments().get(5).equals("DATE")) {
                return uri.getPathSegments().get(6);
            }else if(uri.getPathSegments().get(3).equals("DATE")) {
                return uri.getPathSegments().get(4);
            }
            return null;
        }

        public static String getActivityTypeFromUri(Uri uri) {
            if(uri.getPathSegments().get(7).equals("TYPE")) {
                return uri.getPathSegments().get(8);
            }else if(uri.getPathSegments().get(5).equals("TYPE")) {
                return uri.getPathSegments().get(6);
            }
            return null;
        }

    }

    public static final class ArticleEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ARTICLE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTICLE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTICLE;

        public static final String TABLE_NAME = "article";
        public static final String _ID = "_id";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_COVER_PIC = "cover_pic";
        public static final String COLUMN_LAST_UPDATED_TS = "last_updated_timestamp";

        public static Uri buildArticleUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

        public static Uri buildArticleByTypeUri(String articleType) {
            return CONTENT_URI.buildUpon().appendPath("TYPE").appendPath(articleType).build();
        }

        public static Uri buildArticleByCategoryUri(String articleCategory) {
            return CONTENT_URI.buildUpon().appendPath("CATEGORY").appendPath(articleCategory).build();
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static String getArticleTypeFromUri(Uri uri) {
            if(uri.getPathSegments().get(1).equals("TYPE")) {
                return uri.getPathSegments().get(2);
            }
            return null;
        }

        public static String getArticleCategoryFromUri(Uri uri) {
            if(uri.getPathSegments().get(1).equals("CATEGORY")) {
                return uri.getPathSegments().get(2);
            }
            return null;
        }
    }

    public static final class ArticleDetailEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ARTICLE_DETAIL).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTICLE_DETAIL;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTICLE_DETAIL;

        public static final String TABLE_NAME = "article_detail";
        public static final String _ID = "_id";
        public static final String COLUMN_ARTICLE_ID = "article_id";
        public static final String COLUMN_SEQUENCE = "sequence";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_CONTENT = "content";

        public static Uri buildArticleDetailUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

        public static Uri buildArticleDetailByArticleIdUri(long article_id) {
            return CONTENT_URI.buildUpon().appendPath("ARTICLE").appendPath(Long.toString(article_id)).build();
        }

        public static Uri buildArticleWithDetailByArticleIdUri(long article_id) {
            return CONTENT_URI.buildUpon().appendPath("ARTICLE").appendPath("DETAIL").appendPath(Long.toString(article_id)).build();
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static long getArticleIdFromUri(Uri uri) {
            if(uri.getPathSegments().get(1).equals("ARTICLE")) {
                if(uri.getPathSegments().get(2).equals("DETAIL")) {
                    return Long.parseLong(uri.getPathSegments().get(3));
                }else{
                    return Long.parseLong(uri.getPathSegments().get(2));
                }
            }else{
                return -1;
            }
        }

    }

    public static final class MediaEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MEDIA).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MEDIA;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MEDIA;

        public static final String TABLE_NAME = "media";

        public static final String _ID = "_id";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_PATH = "path";

        public static Uri buildMediaUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

        public static Uri buildMediaByTypeUri(String mediaType) {
            return CONTENT_URI.buildUpon().appendPath("TYPE").appendPath(mediaType).build();
        }

        public static Uri buildMediaByCategoryUri(String mediaCategory) {
            return CONTENT_URI.buildUpon().appendPath("CATEGORY").appendPath(mediaCategory).build();
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static String getMediaTypeFromUri(Uri uri) {
            if(uri.getPathSegments().get(1).equals("TYPE")) {
                return uri.getPathSegments().get(2);
            }
            return null;
        }

        public static String getMediaCategoryFromUri(Uri uri) {
            if(uri.getPathSegments().get(1).equals("CATEGORY")) {
                return uri.getPathSegments().get(2);
            }
            return null;
        }
    }

}
