package com.simplesolutions2003.lookupcoupons.data;
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

    public static final String CONTENT_AUTHORITY = "com.simplesolutions2003.lookupcoupons";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_COUPONS = "coupons";

    public static final class CouponsEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_COUPONS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COUPONS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COUPONS;

        public static final String TABLE_NAME = "coupons";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_RETAILER = "retailer";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_VALUE = "value";
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_DESC = "desc";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_LOGO = "logo";
        public static final String COLUMN_BEG_DATE = "begin_date";
        public static final String COLUMN_END_DATE = "end_date";

        public static Uri buildCouponsUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

        public static Uri buildCouponsByTypeUri(String couponType) {
            return CONTENT_URI.buildUpon().appendPath("TYPE").appendPath(couponType).build();
        }

        public static Uri buildCouponsByTypeSearchUri(String couponType, String couponSearch) {
            if(couponSearch.isEmpty() || couponSearch.equals("")){
                couponSearch = "%";
            }
            return CONTENT_URI.buildUpon().appendPath("TYPE").appendPath(couponType).appendPath("SEARCH").appendPath(couponSearch).build();
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static String getCouponTypeFromUri(Uri uri) {
            if(uri.getPathSegments().get(1).equals("TYPE")) {
                return uri.getPathSegments().get(2);
            }
            return null;
        }

        public static String getCouponSearchFromUri(Uri uri) {
            if(uri.getPathSegments().get(3).equals("SEARCH")) {
                return uri.getPathSegments().get(4);
            }
            return null;
        }

    }

}
