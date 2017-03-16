package com.simplesolutions2003.happybabycare.sync;
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

import com.simplesolutions2003.happybabycare.R;
import com.simplesolutions2003.happybabycare.data.AppContract;

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

/**
 * Created by SuriyaKumar on 9/6/2016.
 */
public class GetActivitiesSyncAdapter extends AbstractThreadedSyncAdapter {
    public final static String TAG = GetActivitiesSyncAdapter.class.getSimpleName();
    public static final String ACTION_DATA_UPDATED =
            "com.simplesolutions2003.happybabycare.ACTION_DATA_UPDATED";
    // 60 seconds (1 minute) * 180 = 3 hours
    //public static final int SYNC_INTERVAL = 60 * 180;
    //for testing let us use 2 minutes
    public static final int SYNC_INTERVAL = 60 * 2;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;

    private static final String RECORD_ACTION_SKIP = "skip";
    private static final String RECORD_ACTION_INSERT = "insert";
    private static final String RECORD_ACTION_UPDATE = "update";
    private static final String RECORD_ACTION_DELETE = "delete";

    private static String USER_ID;
    private static long BABY_ID;

    private Context context;

    public GetActivitiesSyncAdapter(Context context, boolean autoInitialize) {
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
        String activitiesJsonStr = null;

        try {
            final String FORECAST_BASE_URL =
                    "http://simplesolutions2003.com/happybabycare/api/getActivities";

            String userId = "suriya.hidden@gmail.com";
            USER_ID = userId;

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon().appendPath("user").appendPath(userId)
                    .build();

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
            activitiesJsonStr = buffer.toString();
            getActivitiesDataFromJson(activitiesJsonStr);
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
        return;
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private void getActivitiesDataFromJson(String activitiesJsonStr)
            throws JSONException {
        Log.v(TAG, "getActivitiesDataFromJson");
        // into an Object hierarchy for us.
        // These are the names of the JSON objects that need to be extracted.

        // Baby information
        final String OWM_BABIES = "babiesActivities";

        final String OWM_BABY_BABY_ID = "babyBabyId";
        final String OWM_BABY_USER_ID = "babyUserId";
        final String OWM_BABY_OWNER_BABY_ID = "babyOwnerBabyId";
        final String OWM_BABY_OWNER_USER_ID = "babyOwnerUserId";
        final String OWM_BABY_NAME = "babyName";
        final String OWM_BABY_GENDER = "babyGender";
        final String OWM_BABY_BIRTH_DATE = "babyBirthDate";
        final String OWM_BABY_DUE_DATE = "babyDueDate";
        final String OWM_BABY_COLOR = "babyColor";
        final String OWM_BABY_PHOTO = "babyPhoto";
        final String OWM_BABY_ACTIVE = "babyActive";

        // Baby Activity information
        final String OWM_ACTIVITIES = "activities";

        final String OWM_ACTIVITY_ID = "activityId";
        final String OWM_ACTIVITY_USER_ID = "activityUserId";
        final String OWM_ACTIVITY_BABY_ID = "activityBabyId";

        final String OWM_ACTIVITY_TYPE = "activityType";
        final String OWM_ACTIVITY_TIMESTAMP = "activityTimestamp";
        final String OWM_ACTIVITY_ACTION = "activityAction";
        final String OWM_ACTIVITY_DATE = "activityDate";
        final String OWM_ACTIVITY_TIME = "activityTime";
        final String OWM_LAST_UPDATED_TIMESTAMP = "lastUpdatedTimestamp";
        final String OWM_ACTIVITY_NOTES = "activityNotes";
        final String OWM_ACTIVITY_FIELD_TYPE = "activityFieldType";

        final String OWM_ACTIVITY_FIELD_QUANTITY = "activityFieldQuantity";
        final String OWM_ACTIVITY_FIELD_UNIT = "activityFieldUnit";
        final String OWM_ACTIVITY_FIELD_CREAM = "activityFieldCream";
        final String OWM_ACTIVITY_FIELD_END_TIME = "activityFieldEndTime";
        final String OWM_ACTIVITY_FIELD_DURATION = "activityFieldDuration";
        final String OWM_ACTIVITY_FIELD_WHERE_SLEEP = "activityFieldWhereSleep";
        final String OWM_ACTIVITY_FIELD_VALUE = "activityFieldValue";

        final String OWM_MESSAGE_CODE = "success";

        int insertedBabies = 0;
        int insertedActivities = 0;

        try {
            JSONObject activitiesJson = new JSONObject(activitiesJsonStr);

            // do we have an error?
            if ( activitiesJson.has(OWM_MESSAGE_CODE) ) {
                int errorCode = activitiesJson.getInt(OWM_MESSAGE_CODE);

                switch (errorCode) {
                    case 1:
                        break;
                    case 0:
                        Log.v(TAG, "getActivitiesDataFromJson - error");
                        return;
                    default:
                        Log.v(TAG, "getActivitiesDataFromJson - unknown error");
                        return;
                }
            }

            JSONArray babyArray = activitiesJson.getJSONArray(OWM_BABIES);

            for(int i = 0; i < babyArray.length(); i++) {
                // These are the values that will be collected.
                String babyBabyId;
                String babyUserId;
                String babyOwnerBabyId;
                String babyOwnerUserId;
                String babyName;
                String babyGender;
                String babyBirthDate;
                String babyDueDate;
                String babyColor;
                String babyPhoto;
                String babyActive;

                JSONObject baby = babyArray.getJSONObject(i);

                babyBabyId = baby.getString(OWM_BABY_BABY_ID);
                babyUserId = baby.getString(OWM_BABY_USER_ID);
                babyOwnerBabyId = baby.getString(OWM_BABY_OWNER_BABY_ID);
                babyOwnerUserId = baby.getString(OWM_BABY_OWNER_USER_ID);
                babyName = baby.getString(OWM_BABY_NAME);
                babyGender = baby.getString(OWM_BABY_GENDER);
                babyBirthDate = baby.getString(OWM_BABY_BIRTH_DATE);
                babyDueDate = baby.getString(OWM_BABY_DUE_DATE);
                babyColor = baby.getString(OWM_BABY_COLOR);
                babyPhoto = baby.getString(OWM_BABY_PHOTO);
                babyActive = baby.getString(OWM_BABY_ACTIVE);

                ContentValues babyValues = new ContentValues();
                babyValues.put(AppContract.BabyEntry.COLUMN_USER_ID, USER_ID);
                babyValues.put(AppContract.BabyEntry.COLUMN_OWNER_BABY_ID, babyOwnerBabyId);
                babyValues.put(AppContract.BabyEntry.COLUMN_OWNER_USER_ID, babyOwnerUserId);
                babyValues.put(AppContract.BabyEntry.COLUMN_NAME, babyName);
                babyValues.put(AppContract.BabyEntry.COLUMN_GENDER, babyGender);
                babyValues.put(AppContract.BabyEntry.COLUMN_BIRTH_DATE, babyBirthDate);
                babyValues.put(AppContract.BabyEntry.COLUMN_DUE_DATE, babyDueDate);
                babyValues.put(AppContract.BabyEntry.COLUMN_COLOR, babyColor);
                babyValues.put(AppContract.BabyEntry.COLUMN_PHOTO, babyPhoto);
                babyValues.put(AppContract.BabyEntry.COLUMN_ACTIVE, babyActive);

                BABY_ID = AppContract.BabyEntry.getIdFromUri(getContext().getContentResolver().insert(AppContract.BabyEntry.CONTENT_URI, babyValues));
                insertedBabies++;

                JSONArray activitiesArray =
                        baby.getJSONArray(OWM_ACTIVITIES);
                for(int j = 0; j < activitiesArray.length(); j++){

                    String activityId;
                    String activityUserId;
                    String activityBabyId;

                    String activityType;
                    String activityTimestamp;
                    String activityAction;
                    String activityDate;
                    String activityTime;
                    String lastUpdatedTimestamp;
                    String activityNotes;
                    String activityFieldType;

                    String activityFieldQuantity;
                    String activityFieldCream;
                    String activityFieldUnit;
                    String activityFieldEndTime;
                    String activityFieldDuration;
                    String activityFieldWhereSleep;
                    String activityFieldValue;


                    JSONObject activity = activitiesArray.getJSONObject(j);

                    activityAction = activity.getString(OWM_ACTIVITY_ACTION);

                    activityId = activity.getString(OWM_ACTIVITY_ID);
                    activityUserId = activity.getString(OWM_ACTIVITY_USER_ID);
                    activityBabyId = activity.getString(OWM_ACTIVITY_BABY_ID);

                    activityType = activity.getString(OWM_ACTIVITY_TYPE);
                    activityTimestamp = activity.getString(OWM_ACTIVITY_TIMESTAMP);
                    activityDate = activity.getString(OWM_ACTIVITY_DATE);
                    activityTime = activity.getString(OWM_ACTIVITY_TIME);
                    lastUpdatedTimestamp = activity.getString(OWM_LAST_UPDATED_TIMESTAMP);
                    activityNotes = activity.getString(OWM_ACTIVITY_NOTES);
                    activityFieldType = activity.getString(OWM_ACTIVITY_FIELD_TYPE);

                    activityFieldQuantity = activity.getString(OWM_ACTIVITY_FIELD_QUANTITY);
                    activityFieldCream = activity.getString(OWM_ACTIVITY_FIELD_CREAM);
                    activityFieldUnit = activity.getString(OWM_ACTIVITY_FIELD_UNIT);
                    activityFieldEndTime = activity.getString(OWM_ACTIVITY_FIELD_END_TIME);
                    activityFieldDuration = activity.getString(OWM_ACTIVITY_FIELD_DURATION);
                    activityFieldWhereSleep = activity.getString(OWM_ACTIVITY_FIELD_WHERE_SLEEP);
                    activityFieldValue = activity.getString(OWM_ACTIVITY_FIELD_VALUE);

                    String activityRecordAction = RECORD_ACTION_SKIP;
                    Uri uri = null;
                    String[] sColumnsActivity = null;
                    String sSelection = null;
                    String[] sSelectionArgs = null;
                    Cursor activityRecord = null;
                    Integer COL_ACTIVITY_ID = 0;
                    Integer COL_ACTIVITY_LAST_UPD_TS = 1;

                    long checkActivityId = -1;
                    String activityLastUpdatedTS = null;

                    switch (activityType) {
                        case "feeding":
                            uri = AppContract.FeedingEntry.buildFeedingByUserIdBabyIdUri(USER_ID,BABY_ID);
                            sColumnsActivity = new String[]{AppContract.FeedingEntry._ID,
                                    AppContract.FeedingEntry.COLUMN_LAST_UPDATED_TS};
                            sSelection = AppContract.FeedingEntry.COLUMN_TIMESTAMP + " = ? ";
                            sSelectionArgs = new String[]{activityTimestamp};
                            activityRecord = getContext().getContentResolver().query(uri,sColumnsActivity,sSelection,sSelectionArgs,null);
                            break;
                        case "diaper":
                            uri = AppContract.DiaperEntry.buildDiaperByUserIdBabyIdUri(USER_ID,BABY_ID);
                            sColumnsActivity = new String[]{AppContract.FeedingEntry._ID,
                                    AppContract.DiaperEntry.COLUMN_LAST_UPDATED_TS};
                            sSelection = AppContract.DiaperEntry.COLUMN_TIMESTAMP + " = ? ";
                            sSelectionArgs = new String[]{activityTimestamp};
                            activityRecord = getContext().getContentResolver().query(uri,sColumnsActivity,sSelection,sSelectionArgs,null);
                            break;
                        case "sleeping":
                            uri = AppContract.SleepingEntry.buildSleepingByUserIdBabyIdUri(USER_ID,BABY_ID);
                            sColumnsActivity = new String[]{AppContract.FeedingEntry._ID,
                                    AppContract.SleepingEntry.COLUMN_LAST_UPDATED_TS};
                            sSelection = AppContract.SleepingEntry.COLUMN_TIMESTAMP + " = ? ";
                            sSelectionArgs = new String[]{activityTimestamp};
                            activityRecord = getContext().getContentResolver().query(uri,sColumnsActivity,sSelection,sSelectionArgs,null);
                            break;
                        case "health":
                            uri = AppContract.HealthEntry.buildHealthByUserBabyUri(USER_ID,BABY_ID);
                            sColumnsActivity = new String[]{AppContract.FeedingEntry._ID,
                                    AppContract.HealthEntry.COLUMN_LAST_UPDATED_TS};
                            sSelection = AppContract.HealthEntry.COLUMN_TIMESTAMP + " = ? ";
                            sSelectionArgs = new String[]{activityTimestamp};
                            activityRecord = getContext().getContentResolver().query(uri,sColumnsActivity,sSelection,sSelectionArgs,null);
                            break;
                        default:
                            break;
                    }

                    if(activityRecord != null){
                        if(activityRecord.getCount() > 0 ){
                            checkActivityId = activityRecord.getLong(COL_ACTIVITY_ID);
                            activityLastUpdatedTS = activityRecord.getString(COL_ACTIVITY_LAST_UPD_TS);
                            int compare = activityLastUpdatedTS.compareTo(lastUpdatedTimestamp);
                            if(compare < 0){
                                activityRecordAction = RECORD_ACTION_UPDATE;
                            }else if(compare == 0){
                                activityRecordAction = RECORD_ACTION_SKIP;
                            }else if(compare > 0){
                                activityRecordAction = RECORD_ACTION_SKIP;
                            }
                        }else{
                            activityRecordAction = RECORD_ACTION_INSERT;
                        }
                    }

                    switch (activityRecordAction){
                        case RECORD_ACTION_INSERT:
                            switch (activityType) {
                                case "feeding":
                                    ContentValues feedingValues = new ContentValues();
                                    feedingValues.put(AppContract.FeedingEntry.COLUMN_USER_ID, USER_ID);
                                    feedingValues.put(AppContract.FeedingEntry.COLUMN_BABY_ID, BABY_ID);
                                    feedingValues.put(AppContract.FeedingEntry.COLUMN_TIMESTAMP, activityTimestamp);
                                    feedingValues.put(AppContract.FeedingEntry.COLUMN_DATE, activityDate);
                                    feedingValues.put(AppContract.FeedingEntry.COLUMN_TIME, activityTime);
                                    feedingValues.put(AppContract.FeedingEntry.COLUMN_LAST_UPDATED_TS, lastUpdatedTimestamp);
                                    feedingValues.put(AppContract.FeedingEntry.COLUMN_NOTES, activityNotes);
                                    feedingValues.put(AppContract.FeedingEntry.COLUMN_TYPE, activityFieldType);

                                    feedingValues.put(AppContract.FeedingEntry.COLUMN_QUANTITY, activityFieldQuantity);
                                    feedingValues.put(AppContract.FeedingEntry.COLUMN_UNIT, activityFieldUnit);

                                    getContext().getContentResolver().insert(AppContract.FeedingEntry.CONTENT_URI, feedingValues);
                                    insertedActivities++;
                                    break;
                                case "diaper":
                                    ContentValues diaperValues = new ContentValues();
                                    diaperValues.put(AppContract.DiaperEntry.COLUMN_USER_ID, USER_ID);
                                    diaperValues.put(AppContract.DiaperEntry.COLUMN_BABY_ID, BABY_ID);
                                    diaperValues.put(AppContract.DiaperEntry.COLUMN_DATE, activityDate);
                                    diaperValues.put(AppContract.DiaperEntry.COLUMN_TIME, activityTime);
                                    diaperValues.put(AppContract.DiaperEntry.COLUMN_LAST_UPDATED_TS, lastUpdatedTimestamp);
                                    diaperValues.put(AppContract.DiaperEntry.COLUMN_NOTES, activityNotes);
                                    diaperValues.put(AppContract.DiaperEntry.COLUMN_TYPE, activityFieldType);

                                    diaperValues.put(AppContract.DiaperEntry.COLUMN_CREAM, activityFieldCream);

                                    getContext().getContentResolver().insert(AppContract.DiaperEntry.CONTENT_URI, diaperValues);
                                    insertedActivities++;
                                    break;
                                case "sleeping":
                                    ContentValues sleepingValues = new ContentValues();
                                    sleepingValues.put(AppContract.SleepingEntry.COLUMN_USER_ID, USER_ID);
                                    sleepingValues.put(AppContract.SleepingEntry.COLUMN_BABY_ID, BABY_ID);
                                    sleepingValues.put(AppContract.SleepingEntry.COLUMN_DATE, activityDate);
                                    sleepingValues.put(AppContract.SleepingEntry.COLUMN_TIME, activityTime);
                                    sleepingValues.put(AppContract.SleepingEntry.COLUMN_LAST_UPDATED_TS, lastUpdatedTimestamp);
                                    sleepingValues.put(AppContract.SleepingEntry.COLUMN_NOTES, activityNotes);
                                    sleepingValues.put(AppContract.SleepingEntry.COLUMN_TYPE, activityFieldType);

                                    sleepingValues.put(AppContract.SleepingEntry.COLUMN_END_TIME, activityFieldEndTime);
                                    sleepingValues.put(AppContract.SleepingEntry.COLUMN_DURATION, activityFieldDuration);
                                    sleepingValues.put(AppContract.SleepingEntry.COLUMN_WHERE_SLEEP, activityFieldWhereSleep);

                                    getContext().getContentResolver().insert(AppContract.SleepingEntry.CONTENT_URI, sleepingValues);
                                    insertedActivities++;
                                    break;
                                case "health":
                                    ContentValues healthValues = new ContentValues();
                                    healthValues.put(AppContract.HealthEntry.COLUMN_USER_ID, USER_ID);
                                    healthValues.put(AppContract.HealthEntry.COLUMN_BABY_ID, BABY_ID);
                                    healthValues.put(AppContract.HealthEntry.COLUMN_DATE, activityDate);
                                    healthValues.put(AppContract.HealthEntry.COLUMN_TIME, activityTime);
                                    healthValues.put(AppContract.HealthEntry.COLUMN_LAST_UPDATED_TS, lastUpdatedTimestamp);
                                    healthValues.put(AppContract.HealthEntry.COLUMN_NOTES, activityNotes);
                                    healthValues.put(AppContract.HealthEntry.COLUMN_TYPE, activityFieldType);

                                    healthValues.put(AppContract.HealthEntry.COLUMN_VALUE, activityFieldValue);

                                    getContext().getContentResolver().insert(AppContract.HealthEntry.CONTENT_URI, healthValues);
                                    insertedActivities++;
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case RECORD_ACTION_UPDATE:
                            switch (activityType) {
                                case "feeding":
                                    ContentValues feedingValues = new ContentValues();
                                    feedingValues.put(AppContract.FeedingEntry.COLUMN_USER_ID, USER_ID);
                                    feedingValues.put(AppContract.FeedingEntry.COLUMN_BABY_ID, BABY_ID);
                                    feedingValues.put(AppContract.FeedingEntry.COLUMN_TIMESTAMP, activityTimestamp);
                                    feedingValues.put(AppContract.FeedingEntry.COLUMN_DATE, activityDate);
                                    feedingValues.put(AppContract.FeedingEntry.COLUMN_TIME, activityTime);
                                    feedingValues.put(AppContract.FeedingEntry.COLUMN_LAST_UPDATED_TS, lastUpdatedTimestamp);
                                    feedingValues.put(AppContract.FeedingEntry.COLUMN_NOTES, activityNotes);
                                    feedingValues.put(AppContract.FeedingEntry.COLUMN_TYPE, activityFieldType);

                                    feedingValues.put(AppContract.FeedingEntry.COLUMN_QUANTITY, activityFieldQuantity);
                                    feedingValues.put(AppContract.FeedingEntry.COLUMN_UNIT, activityFieldUnit);

                                    getContext().getContentResolver().insert(AppContract.FeedingEntry.CONTENT_URI, feedingValues);
                                    insertedActivities++;
                                    break;
                                case "diaper":
                                    ContentValues diaperValues = new ContentValues();
                                    diaperValues.put(AppContract.DiaperEntry.COLUMN_USER_ID, USER_ID);
                                    diaperValues.put(AppContract.DiaperEntry.COLUMN_BABY_ID, BABY_ID);
                                    diaperValues.put(AppContract.DiaperEntry.COLUMN_DATE, activityDate);
                                    diaperValues.put(AppContract.DiaperEntry.COLUMN_TIME, activityTime);
                                    diaperValues.put(AppContract.DiaperEntry.COLUMN_LAST_UPDATED_TS, lastUpdatedTimestamp);
                                    diaperValues.put(AppContract.DiaperEntry.COLUMN_NOTES, activityNotes);
                                    diaperValues.put(AppContract.DiaperEntry.COLUMN_TYPE, activityFieldType);

                                    diaperValues.put(AppContract.DiaperEntry.COLUMN_CREAM, activityFieldCream);

                                    getContext().getContentResolver().insert(AppContract.DiaperEntry.CONTENT_URI, diaperValues);
                                    insertedActivities++;
                                    break;
                                case "sleeping":
                                    ContentValues sleepingValues = new ContentValues();
                                    sleepingValues.put(AppContract.SleepingEntry.COLUMN_USER_ID, USER_ID);
                                    sleepingValues.put(AppContract.SleepingEntry.COLUMN_BABY_ID, BABY_ID);
                                    sleepingValues.put(AppContract.SleepingEntry.COLUMN_DATE, activityDate);
                                    sleepingValues.put(AppContract.SleepingEntry.COLUMN_TIME, activityTime);
                                    sleepingValues.put(AppContract.SleepingEntry.COLUMN_LAST_UPDATED_TS, lastUpdatedTimestamp);
                                    sleepingValues.put(AppContract.SleepingEntry.COLUMN_NOTES, activityNotes);
                                    sleepingValues.put(AppContract.SleepingEntry.COLUMN_TYPE, activityFieldType);

                                    sleepingValues.put(AppContract.SleepingEntry.COLUMN_END_TIME, activityFieldEndTime);
                                    sleepingValues.put(AppContract.SleepingEntry.COLUMN_DURATION, activityFieldDuration);
                                    sleepingValues.put(AppContract.SleepingEntry.COLUMN_WHERE_SLEEP, activityFieldWhereSleep);

                                    getContext().getContentResolver().insert(AppContract.SleepingEntry.CONTENT_URI, sleepingValues);
                                    insertedActivities++;
                                    break;
                                case "health":
                                    ContentValues healthValues = new ContentValues();
                                    healthValues.put(AppContract.HealthEntry.COLUMN_USER_ID, USER_ID);
                                    healthValues.put(AppContract.HealthEntry.COLUMN_BABY_ID, BABY_ID);
                                    healthValues.put(AppContract.HealthEntry.COLUMN_DATE, activityDate);
                                    healthValues.put(AppContract.HealthEntry.COLUMN_TIME, activityTime);
                                    healthValues.put(AppContract.HealthEntry.COLUMN_LAST_UPDATED_TS, lastUpdatedTimestamp);
                                    healthValues.put(AppContract.HealthEntry.COLUMN_NOTES, activityNotes);
                                    healthValues.put(AppContract.HealthEntry.COLUMN_TYPE, activityFieldType);

                                    healthValues.put(AppContract.HealthEntry.COLUMN_VALUE, activityFieldValue);

                                    getContext().getContentResolver().insert(AppContract.HealthEntry.CONTENT_URI, healthValues);
                                    insertedActivities++;
                                    break;
                                default:
                                    break;
                            }

                            break;
                        case RECORD_ACTION_DELETE:
                            break;
                        case RECORD_ACTION_SKIP:
                            break;
                    }

                }

            }

            Log.d(TAG, "Sync Complete. " + "activities inserted - " + insertedActivities + "; babies inserted - " + insertedBabies);

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority_activities);
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
                context.getString(R.string.content_authority_activities), bundle);
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
        GetActivitiesSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority_activities), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

}
