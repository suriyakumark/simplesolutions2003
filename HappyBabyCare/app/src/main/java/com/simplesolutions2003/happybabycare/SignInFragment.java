package com.simplesolutions2003.happybabycare;

import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.simplesolutions2003.happybabycare.data.AppContract;

/**
 * Created by SuriyaKumar on 8/20/2016.
 */
public class SignInFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    public final static boolean KEEP_IN_STACK = false;
    public final static String TAG = SignInFragment.class.getSimpleName();
    public final static int RC_SIGN_IN = 100;

    public final static int ACTIVE = 1;
    public final static int INACTIVE = 0;

    public static boolean ACTION_SIGN_OUT = false;
    public static boolean ACTION_SIGN_SILENT = false;
    SignInButton signInButton;
    boolean mSignInProgress = false;
    ProgressBar progressBar;

    private static final String[] USER_COLUMNS = {
            AppContract.UserEntry.TABLE_NAME + "." + AppContract.UserEntry._ID,
            AppContract.UserEntry.TABLE_NAME + "." + AppContract.UserEntry.COLUMN_USER_ID,
            AppContract.UserEntry.TABLE_NAME + "." + AppContract.UserEntry.COLUMN_GROUP_ID
    };

    private static final int COL_ID = 0;
    private static final int COL_USER_ID = 1;
    private static final int COL_GROUP_ID = 2;

    public SignInFragment(){}
    GoogleApiClient mGoogleApiClient;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.sign_in, container, false);

        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);
        signInButton = (SignInButton) rootView.findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.v(TAG,v.toString());
                switch (v.getId()) {
                    case R.id.sign_in_button:
                        signIn();
                        break;
                    // ...
                }
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope("https://www.googleapis.com/auth/userinfo.email"))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume(){
        super.onResume();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

    }

    private void signIn() {
        Log.v(TAG,"signIn method");
        showProgressBar(true);
        mSignInProgress = true;
        signInButton.setEnabled(false);
        ACTION_SIGN_SILENT = false;
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void signOut(){
        showProgressBar(true);
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Toast.makeText(getActivity(), getString(R.string.text_sign_out_success), Toast.LENGTH_LONG).show();
                        MainActivity.USER_LOGGED_IN = false;
                        MainActivity.LOGGED_IN_USER_ID = null;
                        signInButton.setEnabled(true);
                        ActivityCompat.invalidateOptionsMenu(getActivity());
                        ACTION_SIGN_OUT = false;
                        updateActiveUser(-1);
                        showProgressBar(false);
                    }
                });
    }

    public void offlineSignIn(){
        Log.v(TAG,"offlineSignIn method");
        mSignInProgress = true;
        signInButton.setEnabled(false);

        Cursor userCursor = new Utilities(getActivity()).getSavedActiveUser();

        if(userCursor!=null) {
            if (userCursor.getCount() == 1) {
                userCursor.moveToFirst();
                MainActivity.LOGGED_IN_USER_ID_ID = userCursor.getLong(COL_ID);
                MainActivity.LOGGED_IN_USER_ID = userCursor.getString(COL_USER_ID);
                MainActivity.GROUP_ID = userCursor.getString(COL_GROUP_ID);
                MainActivity.USER_LOGGED_IN = true;
                Log.v(TAG, "offlineSignIn - active user found");
                goLoginSuccess();
            }
        }

        mSignInProgress = false;
        if(!MainActivity.USER_LOGGED_IN) {
            signInButton.setEnabled(true);
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            if(acct.getEmail()!=null) {
                MainActivity.LOGGED_IN_USER_ID = acct.getEmail();
                Log.d(TAG, "GoogleSignInAccount:" + MainActivity.LOGGED_IN_USER_ID);
                checkAndCreateNewUser();
                goLoginSuccess();
            }else{
                Toast.makeText(getActivity(), getString(R.string.text_sign_in_failed), Toast.LENGTH_LONG).show();
            }

        } else {

            if(!new Utilities(getActivity()).isInternetOn() & !ACTION_SIGN_OUT) {
                offlineSignIn();
            }
            if(!MainActivity.USER_LOGGED_IN) {
                signInButton.setEnabled(true);
                MainActivity.USER_LOGGED_IN = false;
                ActivityCompat.invalidateOptionsMenu(getActivity());
                progressBar.setVisibility(View.GONE);
                if (!ACTION_SIGN_SILENT) {
                    Toast.makeText(getActivity(), getString(R.string.text_sign_in_failed), Toast.LENGTH_LONG).show();
                }
            }
        }
        mSignInProgress = false;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        showProgressBar(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
        showProgressBar(false);
    }

    @Override
    public void onConnected(Bundle bundle) {
        if(ACTION_SIGN_OUT){
            signOut();
        }else {
            OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
            ACTION_SIGN_SILENT = true;
            if (opr.isDone()) {
                Log.d(TAG, "Got cached sign-in");
                GoogleSignInResult result = opr.get();
                handleSignInResult(result);
            } else {
                opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                    @Override
                    public void onResult(GoogleSignInResult googleSignInResult) {
                        handleSignInResult(googleSignInResult);
                    }
                });
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!mSignInProgress) {
            if (connectionResult.hasResolution()) {
                // The user has already clicked 'sign-in' so we attempt to resolve all
                // errors until the user is signed in, or they cancel.
                try {
                    connectionResult.startResolutionForResult(getActivity(), RC_SIGN_IN);
                    mSignInProgress = true;
                } catch (IntentSender.SendIntentException e) {
                    // The intent was canceled before it was sent.  Return to the default
                    // state and attempt to connect to get an updated ConnectionResult.
                    mSignInProgress = false;
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    public void checkAndCreateNewUser(){
        Uri query_user_uri = AppContract.UserEntry.buildUserByUserIdUri(MainActivity.LOGGED_IN_USER_ID);
        Cursor userCursor = getActivity().getContentResolver().query(query_user_uri,USER_COLUMNS,null,null,null);
        if(userCursor!=null) {
            if (userCursor.getCount() == 0) {
                //new user, so insert email to database
                createNewUser();
            } else {
                userCursor.moveToFirst();
                MainActivity.LOGGED_IN_USER_ID_ID = userCursor.getLong(COL_ID);
                MainActivity.GROUP_ID = userCursor.getString(COL_GROUP_ID);
                Log.v(TAG, "checkAndCreateNewUser - user exists");
                updateActiveUser(MainActivity.LOGGED_IN_USER_ID_ID);
                //start initial sync
            }
        }else{
            createNewUser();
        }
    }

    public void createNewUser(){
        Log.v(TAG, "checkAndCreateNewUser - new user");
        Uri insert_user_uri = AppContract.UserEntry.CONTENT_URI;
        ContentValues newValues = new ContentValues();
        newValues.put(AppContract.UserEntry.COLUMN_USER_ID, MainActivity.LOGGED_IN_USER_ID);
        newValues.put(AppContract.UserEntry.COLUMN_ACTIVE, Integer.toString(ACTIVE));
        Uri resultInsert = getActivity().getContentResolver().insert(insert_user_uri, newValues);
        if(resultInsert != null) {
            MainActivity.LOGGED_IN_USER_ID_ID = AppContract.UserEntry.getIdFromUri(resultInsert);
            updateActiveUser(MainActivity.LOGGED_IN_USER_ID_ID);
        }
    }

    public void updateActiveUser(long _id){
        Log.v(TAG, "updateActiveUser - " + _id);
        Uri update_user_uri = AppContract.UserEntry.CONTENT_URI;

        ContentValues newValues1 = new ContentValues();
        newValues1.put(AppContract.UserEntry.COLUMN_ACTIVE, Integer.toString(INACTIVE));
        getActivity().getContentResolver().update(update_user_uri, newValues1, null, null);

        if(_id != -1) {
            ContentValues newValues2 = new ContentValues();
            newValues2.put(AppContract.UserEntry.COLUMN_ACTIVE, Integer.toString(ACTIVE));
            String sSelection = AppContract.UserEntry.TABLE_NAME + "." + AppContract.UserEntry._ID + " = ? ";
            String[] sSelectionArgs = new String[]{Long.toString(_id)};
            getActivity().getContentResolver().update(update_user_uri, newValues2, sSelection, sSelectionArgs);
        }

    }

    public void goLoginSuccess(){
        MainActivity.USER_LOGGED_IN = true;
        ActivityCompat.invalidateOptionsMenu(getActivity());
        showProgressBar(false);
        signInButton.setEnabled(false);
        if(MainActivity.GROUP_ID == null){
            ((MainActivity) getActivity()).handleFragments(new GroupFragment(), GroupFragment.TAG, GroupFragment.KEEP_IN_STACK);
        }else {
            ((MainActivity) getActivity()).handleFragments(new BabyFragment(), BabyFragment.TAG, BabyFragment.KEEP_IN_STACK);
        }
    }

    public void showProgressBar(boolean visible){
        if(visible){
            progressBar.setVisibility(View.VISIBLE);
        }else {
            progressBar.setVisibility(View.GONE);
        }
    }

}
