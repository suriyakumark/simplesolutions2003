package com.simplesolutions2003.onceuponatime.sync;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.format.Time;
import android.util.Log;

import com.simplesolutions2003.onceuponatime.ArticlesFragment;
import com.simplesolutions2003.onceuponatime.MainActivity;
import com.simplesolutions2003.onceuponatime.R;
import com.simplesolutions2003.onceuponatime.Utilities;
import com.simplesolutions2003.onceuponatime.data.AppContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.simplesolutions2003.onceuponatime.ArticlesFragment.ARTICLE_TYPE;

/**
 * Created by SuriyaKumar on 9/6/2016.
 */
public class ArticleSyncAdapter extends AbstractThreadedSyncAdapter {
    public final static String TAG = ArticleSyncAdapter.class.getSimpleName();
    public static final String ACTION_DATA_UPDATED =
            "com.simplesolutions2003.onceuponatime.ACTION_DATA_UPDATED";

    // 60 seconds (1 minute) * 180 = 3 hours
    //public static final int SYNC_INTERVAL = 60 * 180;
    //for testing let us use 15 minutes
    public static final int SYNC_INTERVAL = 60 * 120;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;

    private Context context;

    public ArticleSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.context = context;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(TAG, "Starting sync");

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String articleJsonStr = null;

        try {

            final String FORECAST_BASE_URL =
                    "http://suriyakumar.com/api/articles/v1/getArticles";

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon().build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                Log.v(TAG, "Empty response");
                return;
            }
            articleJsonStr = buffer.toString();
            getArticleDataFromJson(articleJsonStr);
        } catch (IOException e) {
            Log.e(TAG, "Error ", e);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }
        Uri buildArticle = AppContract.ArticleEntry.buildArticleByTypeUri(ARTICLE_TYPE);
        getContext().getContentResolver().notifyChange(buildArticle, null, false);

        return;
    }

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
    private void getArticleDataFromJson(String articleJsonStr)
            throws JSONException {
        Log.v(TAG, "getArticleDataFromJson");
        // into an Object hierarchy for us.
        // These are the names of the JSON objects that need to be extracted.

        // Article information
        final String OWM_ARTICLE = "article";
        final String OWM_ARTICLE_DTL = "articleDetail";

        final String OWM_ARTICLE_ID = "articleId";
        final String OWM_ARTICLE_TYPE = "articleType";
        final String OWM_ARTICLE_CATEGORY = "articleCategory";
        final String OWM_ARTICLE_TITLE = "articleTitle";
        final String OWM_ARTICLE_COVER_PIC = "articleCoverPic";
        final String OWM_ARTICLE_LAST_UPD_TS = "last_updated_timestamp";

        final String OWM_ARTICLE_DTL_ID = "articleDetailId";
        final String OWM_ARTICLE_DTL_SEQUENCE = "articleDetailSequence";
        final String OWM_ARTICLE_DTL_TYPE = "articleDetailType";
        final String OWM_ARTICLE_DTL_CONTENT = "articleDetailContent";

        final String OWM_MESSAGE_CODE = "success";

        int insertedArticles = 0;
        int insertedArticlesDetail = 0;

        try {
            JSONObject articleJson = new JSONObject(articleJsonStr);

            // do we have an error?
            if ( articleJson.has(OWM_MESSAGE_CODE) ) {
                int errorCode = articleJson.getInt(OWM_MESSAGE_CODE);

                switch (errorCode) {
                    case 1:
                        break;
                    case 0:
                        Log.v(TAG, "getArticleDataFromJson - error");
                        return;
                    default:
                        Log.v(TAG, "getArticleDataFromJson - unknown error");
                        return;
                }
            }

            JSONArray articleArray = articleJson.getJSONArray(OWM_ARTICLE);

            for(int i = 0; i < articleArray.length(); i++) {
                // These are the values that will be collected.
                long articleId;
                String articleType;
                String articleCategory;
                String articleTitle;
                String articleCoverPic;
                String articleLastUpdatedTs;

                // Get the JSON object representing the day
                JSONObject article = articleArray.getJSONObject(i);

                articleId = article.getLong(OWM_ARTICLE_ID);
                articleType = article.getString(OWM_ARTICLE_TYPE);
                articleCategory = article.getString(OWM_ARTICLE_CATEGORY);
                articleTitle = article.getString(OWM_ARTICLE_TITLE);
                articleCoverPic = article.getString(OWM_ARTICLE_COVER_PIC);
                articleLastUpdatedTs = article.getString(OWM_ARTICLE_LAST_UPD_TS);

                Uri articleUri = AppContract.ArticleEntry.buildArticleUri(articleId);
                Cursor articleCursor = getContext().getContentResolver().query(articleUri, ArticlesFragment.ARTICLE_COLUMNS,null,null,null);
                String articleLastUpdatedTsPrev = "";
                if(articleCursor != null) {
                    if (articleCursor.getCount() > 0) {
                        articleCursor.moveToNext();
                        articleLastUpdatedTsPrev = articleCursor.getString(ArticlesFragment.COL_ARTICLE_LAST_UPD_TS);
                        Log.v(TAG, "articleLastUpdatedTsPrev " + articleLastUpdatedTsPrev);
                    }
                }
                Log.v(TAG, "articleLastUpdatedTs " + articleLastUpdatedTs);

                if(articleLastUpdatedTs.compareTo(articleLastUpdatedTsPrev) > 0) {
                    ContentValues articleValues = new ContentValues();
                    articleValues.put(AppContract.ArticleEntry._ID, articleId);
                    articleValues.put(AppContract.ArticleEntry.COLUMN_TYPE, articleType);
                    articleValues.put(AppContract.ArticleEntry.COLUMN_CATEGORY, articleCategory);
                    articleValues.put(AppContract.ArticleEntry.COLUMN_TITLE, articleTitle);
                    articleValues.put(AppContract.ArticleEntry.COLUMN_COVER_PIC, articleCoverPic);
                    articleValues.put(AppContract.ArticleEntry.COLUMN_LAST_UPDATED_TS, articleLastUpdatedTs);
                    getContext().getContentResolver().insert(AppContract.ArticleEntry.CONTENT_URI, articleValues);
                    insertedArticles++;

                    JSONArray articleDetailArray =
                            article.getJSONArray(OWM_ARTICLE_DTL);
                    for (int j = 0; j < articleDetailArray.length(); j++) {

                        long articleDetailId;
                        long articleDetailSequenceId;
                        String articleDetailType;
                        String articleDetailContent;

                        JSONObject articleDetail = articleDetailArray.getJSONObject(j);

                        articleDetailId = articleDetail.getLong(OWM_ARTICLE_DTL_ID);
                        articleDetailSequenceId = articleDetail.getLong(OWM_ARTICLE_DTL_SEQUENCE);
                        articleDetailType = articleDetail.getString(OWM_ARTICLE_DTL_TYPE);
                        articleDetailContent = articleDetail.getString(OWM_ARTICLE_DTL_CONTENT);

                        ContentValues articleDetailValues = new ContentValues();

                        articleDetailValues.put(AppContract.ArticleDetailEntry._ID, articleDetailId);
                        articleDetailValues.put(AppContract.ArticleDetailEntry.COLUMN_ARTICLE_ID, articleId);
                        articleDetailValues.put(AppContract.ArticleDetailEntry.COLUMN_SEQUENCE, articleDetailSequenceId);
                        articleDetailValues.put(AppContract.ArticleDetailEntry.COLUMN_TYPE, articleDetailType);
                        articleDetailValues.put(AppContract.ArticleDetailEntry.COLUMN_CONTENT, articleDetailContent);

                        getContext().getContentResolver().insert(AppContract.ArticleDetailEntry.CONTENT_URI, articleDetailValues);
                        insertedArticlesDetail++;
                    }

                }

            }

            Log.d(TAG, "Sync Complete. " + "articles inserted - " + insertedArticles + "; detail inserted - " + insertedArticlesDetail);

            if(insertedArticles > 0){
                sendNotification();
            }

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public void sendNotification() {
        NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(NOTIFICATION_SERVICE);

        Intent intent = new Intent(context.getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.logo_white)
                        .setContentIntent(pendingIntent)
                        .setContentTitle(context.getString(R.string.notify_title))
                        .setContentText(context.getString(R.string.notify_msg))
                        .setAutoCancel(true);

        mNotificationManager.notify(1, mBuilder.build());

    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        Log.v(TAG, "getSyncAccount");
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));


        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }


    private static void onAccountCreated(Account newAccount, Context context) {
        Log.v(TAG, "onAccountCreated");
        /*
         * Since we've created an account
         */
        ArticleSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }


}
