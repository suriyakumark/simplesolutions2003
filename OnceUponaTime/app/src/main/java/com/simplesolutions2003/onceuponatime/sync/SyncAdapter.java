package com.simplesolutions2003.onceuponatime.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.util.Log;

import com.simplesolutions2003.onceuponatime.R;

/**
 * Created by simpl on 3/15/2017.
 */

public class SyncAdapter {
    public final static String TAG = SyncAdapter.class.getSimpleName();

    private Context context;

    public SyncAdapter(Context context, boolean autoInitialize) {
        this.context = context;
    }


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

        ArticleSyncAdapter.configurePeriodicSync(context, ArticleSyncAdapter.SYNC_INTERVAL, ArticleSyncAdapter.SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        ArticleSyncAdapter.syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
