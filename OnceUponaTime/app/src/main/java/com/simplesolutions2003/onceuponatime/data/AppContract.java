package com.simplesolutions2003.onceuponatime.data;
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

    public static final String CONTENT_AUTHORITY = "com.simplesolutions2003.onceuponatime";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MENU = "menu";
    public static final String PATH_ARTICLE = "article";
    public static final String PATH_ARTICLE_DETAIL = "article_detail";
    public static final String PATH_FAVORITE = "favorite";

    public static final class MenuEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MENU).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MENU;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MENU;

        public static final String TABLE_NAME = "menu";
        public static final String _ID = "_id";
        public static final String COLUMN_MENU = "name";
        public static final String COLUMN_DESC = "desc";
        public static final String COLUMN_COVER_PIC = "cover_pic";

        public static Uri buildMenuUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
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
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_NEW = "new";
        public static final String COLUMN_LAST_UPDATED_TS = "last_updated_timestamp";

        public static Uri buildArticleUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

        public static Uri buildArticleByTypeUri(String articleType) {
            return CONTENT_URI.buildUpon().appendPath("TYPE").appendPath(articleType).build();
        }

        public static Uri buildArticleByTypeSearchUri(String articleType, String articleSearch) {
            if(articleSearch.isEmpty() || articleSearch.equals("")){
                articleSearch = "%";
            }
            return CONTENT_URI.buildUpon().appendPath("TYPE").appendPath(articleType).appendPath("SEARCH").appendPath(articleSearch).build();
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

        public static String getArticleSearchFromUri(Uri uri) {
            if(uri.getPathSegments().get(3).equals("SEARCH")) {
                return uri.getPathSegments().get(4);
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

    public static final class FavoriteEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;

        public static final String TABLE_NAME = "favorite";
        public static final String _ID = "_id";
        public static final String COLUMN_ARTICLE_ID = "article_id";

        public static Uri buildFavoriteUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

        public static Uri buildArticleByFavoriteUri() {
            return CONTENT_URI.buildUpon().appendPath("ARTICLE").build();
        }


        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

    }

}
