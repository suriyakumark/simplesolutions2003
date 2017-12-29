package com.simplesolutions2003.thirukkuralplus.data;
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

    public static final String CONTENT_AUTHORITY = "com.simplesolutions2003.thirukkuralplus";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_SECTIONS = "sections";
    public static final String PATH_GROUPS = "groups";
    public static final String PATH_CHAPTERS = "chapters";
    public static final String PATH_KURALS = "kurals";

    public static final class SectionsEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SECTIONS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SECTIONS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SECTIONS;

        public static final String TABLE_NAME = "sections";
        public static final String _ID = "_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_NAME_ENG = "name_eng";
        public static final String COLUMN_ICON = "icon";

        public static Uri buildSectionsUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

    }

    public static final class GroupsEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_GROUPS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GROUPS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GROUPS;

        public static final String TABLE_NAME = "groups";
        public static final String _ID = "_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_NAME_ENG = "name_eng";
        public static final String COLUMN_ICON = "icon";

        public static Uri buildGroupsUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

    }

    public static final class ChaptersEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CHAPTERS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CHAPTERS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CHAPTERS;

        public static final String TABLE_NAME = "chapters";
        public static final String _ID = "_id";
        public static final String COLUMN_SECTION_ID = "section_id";
        public static final String COLUMN_GROUP_ID = "group_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_NAME_ENG = "name_eng";
        public static final String COLUMN_ICON = "icon";

        public static Uri buildChaptersUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

        public static Uri buildChapterBySectionUri(long sectionId) {
            return CONTENT_URI.buildUpon().appendPath("SECTION").appendPath(Long.toString(sectionId)).build();
        }

        public static Uri buildChapterByGroupUri(long groupId) {
            return CONTENT_URI.buildUpon().appendPath("GROUP").appendPath(Long.toString(groupId)).build();
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static long getSectionFromUri(Uri uri) {
            if(uri.getPathSegments().get(1).equals("SECTION")) {
                return Long.parseLong(uri.getPathSegments().get(2));
            }
            return -1;
        }

        public static long getGroupFromUri(Uri uri) {
            if(uri.getPathSegments().get(1).equals("GROUP")) {
                return Long.parseLong(uri.getPathSegments().get(2));
            }
            return -1;
        }

    }

    public static final class KuralsEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_KURALS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_KURALS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_KURALS;

        public static final String TABLE_NAME = "kurals";
        public static final String _ID = "_id";
        public static final String COLUMN_CHAPTER_ID = "chapter_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_NAME_ENG = "name_eng";
        public static final String COLUMN_EXP_MUVA = "exp_muva";
        public static final String COLUMN_EXP_SOLO = "exp_solo";
        public static final String COLUMN_EXP_MUKA = "exp_muka";
        public static final String COLUMN_COUPLET = "couplet";
        public static final String COLUMN_TRANS = "transliteration";
        public static final String COLUMN_FAVORITE = "favorite";
        public static final String COLUMN_READ = "read";

        public static Uri buildKuralsUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

        public static Uri buildKuralsByChapterUri(long kuralChapter) {
            return CONTENT_URI.buildUpon().appendPath("CHAPTER").appendPath(Long.toString(kuralChapter)).build();
        }

        public static Uri buildKuralsBySearchUri(String kuralSearch) {
            if(kuralSearch.isEmpty() || kuralSearch.equals("")){
                kuralSearch = "%";
            }
            return CONTENT_URI.buildUpon().appendPath("SEARCH").appendPath(kuralSearch).build();
        }

        public static Uri buildKuralsByFavoritesUri() {
            return CONTENT_URI.buildUpon().appendPath("FAVORITES").build();
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static long getKuralChapterFromUri(Uri uri) {
            if(uri.getPathSegments().get(1).equals("CHAPTER")) {
                return Long.parseLong(uri.getPathSegments().get(2));
            }
            return -1;
        }

        public static String getKuralSearchFromUri(Uri uri) {
            if(uri.getPathSegments().get(1).equals("SEARCH")) {
                return uri.getPathSegments().get(2);
            }
            return null;
        }


    }

}
