package com.simplesolutions2003.onceuponatime;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.simplesolutions2003.onceuponatime.sync.ArticleSyncAdapter;
import com.simplesolutions2003.onceuponatime.sync.SyncAdapter;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private final static String LOG_TAG = MainActivity.class.getSimpleName();

    public final static String ARTICLE_TYPE_SHORT_STORIES = "short stories";
    public final static String ARTICLE_TYPE_STORIES = "stories";
    public final static String ARTICLE_TYPE_RHYMES = "rhymes";
    public final static String ARTICLE_TYPE_POEMS = "poems";
    public final static String ARTICLE_TYPE_FAVORITES = "favorites";

    public final static long APP_UNLOCK_INTERSTITIAL_VISITS = 50;
    public static boolean AD_ENABLED = true;

    FragmentManager fragmentManager = getSupportFragmentManager();

    Toolbar toolbar;
    private static MenuItem mSearchAction;

    private static boolean isSearchOpened = false;
    public static boolean bSearchEnabled = true;
    private static EditText etSearch;
    public static String search_text = "";

    public static InterstitialAd mInterstitialAd;  // The ad

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AdView mBannerAd = (AdView) findViewById(R.id.adView);
        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequestBanner = new AdRequest.Builder()
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(getString(R.string.ad_device_id))
                .build();
        mBannerAd.loadAd(adRequestBanner);


        AdRequest adRequestInterstitial = new AdRequest.Builder()
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
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
        if(bSearchEnabled) {
            new Utilities().enableMenu(mSearchAction, true);
        }else{
            mSearchAction.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_rate:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                }
                return true;
            case R.id.action_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        this.getString(R.string.msg_share_app) +
                                "https://play.google.com/store/apps/details?id=" + getPackageName());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                return true;

            case R.id.action_search:
                if(isSearchOpened) {
                    handleMenuSearchInitialize();
                }else {
                    handleMenuSearchOpen();
                }
                return true;

            case R.id.action_submit:
                String url = getString(R.string.action_submit_url);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();
        getSupportActionBar().setTitle(getString(R.string.app_name));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        search_text = "";
    }

    @Override
    public void onResumeFragments (){
        super.onResumeFragments();
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
        if(AD_ENABLED) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }
    }
}

