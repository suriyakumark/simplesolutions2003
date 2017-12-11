package com.simplesolutions2003.onceuponatime;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.simplesolutions2003.onceuponatime.data.AppDBHelper;
import com.simplesolutions2003.onceuponatime.sync.ArticleSyncAdapter;
import com.simplesolutions2003.onceuponatime.sync.SyncAdapter;
import com.simplesolutions2003.onceuponatime.utils.IabHelper;
import com.simplesolutions2003.onceuponatime.utils.IabResult;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final static String LOG_TAG = MainActivity.class.getSimpleName();
    public final static String MY_PREF = "onceuponatime_pref";

    public final static String ARTICLE_TYPE_SHORT_STORIES = "short stories";
    public final static String ARTICLE_TYPE_STORIES = "stories";
    public final static String ARTICLE_TYPE_RHYMES = "rhymes";
    public final static String ARTICLE_TYPE_POEMS = "poems";
    public final static String ARTICLE_TYPE_FAVORITES = "favorites";

    public final static long APP_UNLOCK_INTERSTITIAL_VISITS = 50;
    public static final int BILLING_RESPONSE_RESULT_OK = 0;
    public static final int REQUEST_CODE = 1001;

    public static boolean PREMIUM_USER = false;
    public static boolean AD_ENABLED = true;

    FragmentManager fragmentManager = getSupportFragmentManager();

    Toolbar toolbar;
    private static MenuItem mSearchAction;
    private static MenuItem mUpgradeAction;

    private static boolean isSearchOpened = false;
    public static boolean bSearchEnabled = true;
    private static EditText etSearch;
    public static String search_text = "";

    public static InterstitialAd mInterstitialAd;  // The ad
    public static AdView mBannerAd;
    IInAppBillingService mService;

    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
            PREMIUM_USER = isPaidUser();
            handleBannerAds();
            if(mUpgradeAction != null) {
                mUpgradeAction.setVisible(false);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AppDBHelper dBHelper;
        dBHelper = new AppDBHelper(this);
        dBHelper.getWritableDatabase();


        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

        mBannerAd = (AdView) findViewById(R.id.adView);
        AdRequest adRequestBanner = new AdRequest.Builder()
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(getString(R.string.ad_device_id))
                .addTestDevice("2D08CBA4EBFA436854E4CE92F1D95900")
                .build();
        mBannerAd.loadAd(adRequestBanner);

        AdRequest adRequestInterstitial = new AdRequest.Builder()
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("2D08CBA4EBFA436854E4CE92F1D95900")
                .addTestDevice(getString(R.string.ad_device_id))
                .build();

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        mInterstitialAd.loadAd(adRequestInterstitial);

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice(getString(R.string.ad_device_id)).build());
            }
        });

        Log.v(LOG_TAG, "initializeSyncAdapter");
        SyncAdapter.initializeSyncAdapter(this);

        /*Picasso picasso = new Picasso.Builder(this)
                .downloader(new OkHttpDownloader(this,Integer.MAX_VALUE))
                .build();
        picasso.setIndicatorsEnabled(true);
        picasso.setLoggingEnabled(true);
        Picasso.setSingletonInstance(picasso);*/

        // do not add this fragment to back stack, as this is the starting transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations( R.anim.slide_in_left, 0, 0, R.anim.slide_out_left);
        fragmentTransaction.replace(R.id.frame_container, new AppMenuFragment(), AppMenuFragment.TAG);
        fragmentTransaction.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(R.id.action_search);
        mUpgradeAction = menu.findItem(R.id.action_premium);
        if(bSearchEnabled) {
            new Utilities().enableMenu(mSearchAction, true);
        }else{
            mSearchAction.setVisible(false);
        }
        if(!PREMIUM_USER) {
            new Utilities().enableMenuOption(mUpgradeAction, true);
        }else{
            mUpgradeAction.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_rate:
                rateMe();
                return true;

            case R.id.action_share:
                shareMe();
                return true;

            case R.id.action_search:
                if(isSearchOpened) {
                    handleMenuSearchInitialize();
                }else {
                    handleMenuSearchOpen();
                }
                return true;

            case R.id.action_premium:
                if(isPaidUser()){
                    Toast.makeText(this,getString(R.string.msg_already_premium),Toast.LENGTH_SHORT).show();
                }else {
                    upgradePremium();
                }
                return true;

            case R.id.action_submit:
                submitStory();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();
        new Utilities().checkUserAdStatus(this,false);
        getSupportActionBar().setTitle(getString(R.string.app_name));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        search_text = "";
        handleBannerAds();
        if(new Utilities().isItTimeToRemindRate(this,true)){
            remindRate();
        }
    }

    @Override
    public void onResumeFragments (){
        super.onResumeFragments();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mServiceConn != null) {
            unbindService(mServiceConn);
        }
    }

    protected void handleMenuSearchInitialize() {
        Log.v(LOG_TAG,"handleMenuSearchInitialize");
        search_text = "";
        doSearch();
    }

    protected void handleMenuSearchClose(){

        Log.v(LOG_TAG,"handleMenuSearchClose");
        ActionBar action = getSupportActionBar(); //get the actionbar
        action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
        action.setDisplayShowTitleEnabled(true); //show the title in the action bar

        //hides the keyboard
        hideKeyboard();

        //add the search icon in the action bar
        mSearchAction.setIcon(getResources().getDrawable(R.drawable.search));

        isSearchOpened = false;

    }

    protected void handleMenuSearchOpen(){
        if(!isSearchOpened){ //test if the search is open
            Log.v(LOG_TAG,"handleMenuSearchOpen");
            ActionBar action = getSupportActionBar(); //get the actionbar
            action.setDisplayShowCustomEnabled(true); //enable it to display a
            // custom view in the action bar.
            action.setCustomView(R.layout.search_bar);//add the custom view
            action.setDisplayShowTitleEnabled(false); //hide the title

            etSearch = (EditText)action.getCustomView().findViewById(R.id.search_text); //the text editor
            etSearch.setText(search_text);
            //this is a listener to do a search when the user clicks on search button
            etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        search_text = etSearch.getText().toString();
                        doSearch();
                        return true;
                    }
                    return false;
                }
            });


            etSearch.requestFocus();

            //open the keyboard focused in the edtSearch
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT);

            //add the close icon
            mSearchAction.setIcon(getResources().getDrawable(R.drawable.search_close));
            isSearchOpened = true;
        }
    }

    @Override
    public void onBackPressed() {
        hideKeyboard();
        if(isSearchOpened) {
            handleMenuSearchInitialize();
            return;
        }
        super.onBackPressed();
    }

    private void doSearch() {
        handleMenuSearchClose();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations( R.anim.slide_in_left, 0, 0, R.anim.slide_out_left);
        fragmentTransaction.replace(R.id.frame_container, new ArticlesFragment(), ArticlesFragment.TAG);
        if(!search_text.isEmpty() || !search_text.equals("")) {
            fragmentTransaction.addToBackStack(ArticlesFragment.TAG);
        }
        fragmentTransaction.commit();
    }

    public void hideKeyboard(){
        Utilities.hideKeyboard(this);
    }


    public static void displayAd(){
        if(AD_ENABLED && !PREMIUM_USER) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }
    }

    public void remindRate(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.rate_dialog);
        dialog.setTitle(getString(R.string.dialog_rate_title));

        Button b1 = (Button) dialog.findViewById(R.id.rate_yes);
        Button b2 = (Button) dialog.findViewById(R.id.rate_later);
        Button b3 = (Button) dialog.findViewById(R.id.rate_never);

        b1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                rateMe();
                dialog.dismiss();
            }
        });


        b2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        b3.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SharedPreferences pref = getSharedPreferences(MY_PREF, 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean(getString(R.string.pref_app_rate_never), true);
                editor.commit();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void rateMe(){
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    public void shareMe(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                this.getString(R.string.msg_share_app) +
                        "https://play.google.com/store/apps/details?id=" + getPackageName());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public void submitStory(){
        String url = getString(R.string.action_submit_url);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public boolean isPaidUser(){

        try {
            Bundle ownedItems = mService.getPurchases(3, getPackageName(), "inapp", null);

            int response = ownedItems.getInt("RESPONSE_CODE");
            if (response == BILLING_RESPONSE_RESULT_OK) {
                ArrayList<String> ownedSkus =
                        ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                ArrayList<String>  purchaseDataList =
                        ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                ArrayList<String>  signatureList =
                        ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
                String continuationToken =
                        ownedItems.getString("INAPP_CONTINUATION_TOKEN");

                for (int i = 0; i < purchaseDataList.size(); ++i) {
                    String purchaseData = purchaseDataList.get(i);
                    String signature = signatureList.get(i);
                    String sku = ownedSkus.get(i);
                    if (sku.equals(getString(R.string.billing_premium))){
                        //TEST//
                        //return false;
                        return true;
                    }
                    // do something with this purchase information
                    // e.g. display the updated list of products owned by user
                }

                // if continuationToken != null, call getPurchases again
                // and pass in the token to retrieve more items
            }
        }catch (android.os.RemoteException e){
            e.printStackTrace();
        }
        return false;
    }

    public void upgradePremium(){

        ArrayList<String> skuList = new ArrayList<String>();
        skuList.add("premium_upgrade");
        Bundle querySkus = new Bundle();
        querySkus.putStringArrayList("ITEM_ID_LIST", skuList);

        try {
            Bundle skuDetails = mService.getSkuDetails(3,
                    getPackageName(), "inapp", querySkus);

            int response = skuDetails.getInt("RESPONSE_CODE");
            if (response == BILLING_RESPONSE_RESULT_OK) {
                ArrayList<String> responseList
                        = skuDetails.getStringArrayList("DETAILS_LIST");

                for (String thisResponse : responseList) {
                    JSONObject object = new JSONObject(thisResponse);
                    String sku = object.getString("productId");
                    String price = object.getString("price");
                    if (sku.equals(getString(R.string.billing_premium))){
                        //mPremiumUpgradePrice = price;
                        String deviceId = Settings.Secure.getString(getContentResolver(),
                                Settings.Secure.ANDROID_ID);
                        Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(),
                                sku, "inapp", deviceId);

                        PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");

                        startIntentSenderForResult(pendingIntent.getIntentSender(),
                                REQUEST_CODE, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
                                Integer.valueOf(0));
                    }
                }
            }


        }catch (android.os.RemoteException e){
            e.printStackTrace();
        }catch (org.json.JSONException e){
            e.printStackTrace();
        }catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    public void handleBannerAds(){
        if(PREMIUM_USER && mBannerAd != null){
            Log.v(LOG_TAG,"hiding banner");
            mBannerAd.setVisibility(View.GONE);
            mBannerAd.destroy();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            if (resultCode == RESULT_OK) {
                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    String sku = jo.getString("productId");
                    String developerPayload = jo.getString("developerPayload");
                    String deviceId = Settings.Secure.getString(getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                    if(sku.equals(getString(R.string.billing_premium)) &&
                            developerPayload.equals(deviceId)) {
                        PREMIUM_USER = isPaidUser();
                        new Utilities().checkUserAdStatus(this,false);
                        handleBannerAds();
                        Toast.makeText(this,getString(R.string.msg_premium_upgraded),Toast.LENGTH_LONG).show();
                        Log.v(LOG_TAG, "Purchased " + sku );
                    }
                }
                catch (JSONException e) {
                    Log.v(LOG_TAG,"Failed to parse purchase data.");
                    e.printStackTrace();
                }
            }
        }
    }
}

