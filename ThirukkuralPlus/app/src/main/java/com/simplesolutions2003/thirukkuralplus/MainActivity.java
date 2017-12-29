package com.simplesolutions2003.thirukkuralplus;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.audiofx.BassBoost;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.simplesolutions2003.thirukkuralplus.data.AppDBHelper;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RewardedVideoAdListener {

    public final static String LOG_TAG = MainActivity.class.getSimpleName();

    final static String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots";
    FragmentManager fragmentManager = getSupportFragmentManager();
    NavigationView navigationView;
    FragmentTransaction fragmentTransaction;

    public static String CURRENT_FRAGMENT = "";
    public static String share_kural = "";

    public static RewardedVideoAd mAd;

    private static boolean exitAllow = false;
    private static MenuItem mSearchAction;
    private static boolean isSearchOpened = false;
    private static EditText etSearch;
    private static String search_text = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AppDBHelper dBHelper;
        dBHelper = new AppDBHelper(this);
        SQLiteDatabase sqLiteDatabase = dBHelper.getWritableDatabase();

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CURRENT_FRAGMENT.equals(KuralsDetailFragment.TAG)) {
                    new Utilities(MainActivity.this).shareMe(share_kural);
                }else if(CURRENT_FRAGMENT.equals(QuizFragment.TAG)) {
                    shareMe();
                }else{
                    new Utilities(MainActivity.this).shareMe("");
                }
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        updateNavigation();

        MobileAds.initialize(this, getString(R.string.ad_app_id));

        mAd = MobileAds.getRewardedVideoAdInstance(this);
        mAd.setRewardedVideoAdListener(this);
        loadRewardedVideo();

        //increment visit count
        new Utilities().addVisit(this);

        if(new Utilities().isItTimeToRemindRate(this)){
            new Utilities(this).remindRate();
        }
        if(new Utilities().isItTimeToRemindReward(this)){
            remindReward();
        }

        // do not add this fragment to back stack, as this is the starting transaction
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, new PrefaceFragment(), PrefaceFragment.TAG);
        fragmentTransaction.commit();
    }

    private void loadRewardedVideo() {
        mAd.loadAd(getString(R.string.rewarded_ad_unit_id),
                new AdRequest.Builder()
                        .addTestDevice(getString(R.string.ad_device_id))
                        .build());
    }

    public void remindReward(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.reward_dialog);

        TextView tv1 = (TextView) dialog.findViewById(R.id.reward_message);
        Button b1 = (Button) dialog.findViewById(R.id.reward_yes);
        Button b2 = (Button) dialog.findViewById(R.id.reward_later);

        if(new Utilities().isEnglishEnabled(this)) {
            dialog.setTitle(getString(R.string.dialog_reward_title_eng));
            tv1.setText(getString(R.string.dialog_reward_message_eng));
            b1.setText(getString(R.string.dialog_reward_yes_eng));
            b2.setText(getString(R.string.dialog_reward_later_eng));
        }else{
            dialog.setTitle(getString(R.string.dialog_reward_title));
            tv1.setText(getString(R.string.dialog_reward_message));
            b1.setText(getString(R.string.dialog_reward_yes));
            b2.setText(getString(R.string.dialog_reward_later));
        }
        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, new RewardFragment(), RewardFragment.TAG);
                fragmentTransaction.commit();
                dialog.dismiss();
            }
        });


        b2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(fragmentManager.getBackStackEntryCount() > 0) {
                exitAllow = false;
                super.onBackPressed();
            }else if(!exitAllow) {
                exitAllow = true;
                Toast.makeText(this, getString(R.string.msg_exit), Toast.LENGTH_SHORT).show();
            }else{
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateNavigation();
        search_text = "";
        exitAllow = false;
    }

    public void updateNavigation(){
        Log.v(LOG_TAG,"updateNavigation - " + navigationView);

        if(navigationView != null) {
            Menu menu = navigationView.getMenu();
            if(new Utilities().isEnglishEnabled(this)) {
                menu.findItem(R.id.nav_preface).setTitle(getString(R.string.nav_preface_eng));
                menu.findItem(R.id.nav_sections).setTitle(getString(R.string.nav_sections_eng));
                menu.findItem(R.id.nav_groups).setTitle(getString(R.string.nav_groups_eng));
                menu.findItem(R.id.nav_chapters).setTitle(getString(R.string.nav_chapters_eng));
                menu.findItem(R.id.nav_favorites).setTitle(getString(R.string.nav_favorites_eng));
                menu.findItem(R.id.nav_quiz).setTitle(getString(R.string.nav_quiz_eng));
                menu.findItem(R.id.nav_settings).setTitle(getString(R.string.nav_settings_eng));
                menu.findItem(R.id.nav_rate).setTitle(getString(R.string.nav_rate_eng));
                menu.findItem(R.id.nav_reward).setTitle(getString(R.string.nav_reward_eng));
            }else{
                menu.findItem(R.id.nav_preface).setTitle(getString(R.string.nav_preface));
                menu.findItem(R.id.nav_sections).setTitle(getString(R.string.nav_sections));
                menu.findItem(R.id.nav_groups).setTitle(getString(R.string.nav_groups));
                menu.findItem(R.id.nav_chapters).setTitle(getString(R.string.nav_chapters));
                menu.findItem(R.id.nav_favorites).setTitle(getString(R.string.nav_favorites));
                menu.findItem(R.id.nav_quiz).setTitle(getString(R.string.nav_quiz));
                menu.findItem(R.id.nav_settings).setTitle(getString(R.string.nav_settings));
                menu.findItem(R.id.nav_rate).setTitle(getString(R.string.nav_rate));
                menu.findItem(R.id.nav_reward).setTitle(getString(R.string.nav_reward));

            }
        }


    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(R.id.action_search);
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            if(isSearchOpened) {
                handleMenuSearchInitialize();
            }else {
                handleMenuSearchOpen();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    protected void handleMenuSearchInitialize() {
        Log.v(LOG_TAG,"handleMenuSearchInitialize");
        search_text = "";
        handleMenuSearchClose();
    }

    protected void handleMenuSearchClose(){

        Log.v(LOG_TAG,"handleMenuSearchClose");
        ActionBar action = getSupportActionBar(); //get the actionbar
        action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
        action.setDisplayShowTitleEnabled(true); //show the title in the action bar

        //hides the keyboard
        hideKeyboard();

        //add the search icon in the action bar
        mSearchAction.setIcon(getResources().getDrawable(R.drawable.icon_search));

        isSearchOpened = false;

    }

    private void doSearch() {
        handleMenuSearchClose();
        exitAllow = false;
        KuralsFragment.CHAPTER_ID = -1;
        KuralsFragment.SEARCH_TEXT = search_text;

        // do not add this fragment to back stack, as this is the starting transaction
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, new KuralsFragment(), KuralsFragment.TAG);
        fragmentTransaction.commit();
    }

    public void hideKeyboard(){
        Utilities.hideKeyboard(this);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        exitAllow = false;
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_preface:
                // do not add this fragment to back stack, as this is the starting transaction
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, new PrefaceFragment(), PrefaceFragment.TAG);
                fragmentTransaction.commit();
                break;
            case R.id.nav_sections:
                // do not add this fragment to back stack, as this is the starting transaction
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, new SectionsFragment(), SectionsFragment.TAG);
                fragmentTransaction.commit();
                break;
            case R.id.nav_groups:
                // do not add this fragment to back stack, as this is the starting transaction
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, new GroupsFragment(), GroupsFragment.TAG);
                fragmentTransaction.commit();
                break;
            case R.id.nav_chapters:
                ChaptersFragment.SECTION_ID = -1;
                ChaptersFragment.GROUP_ID = -1;
                ChaptersFragment.dPosition = 0;
                ChaptersFragment.MODE = ChaptersFragment.MODE_READ;

                // do not add this fragment to back stack, as this is the starting transaction
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, new ChaptersFragment(), ChaptersFragment.TAG);
                fragmentTransaction.commit();
                break;
            case R.id.nav_favorites:
                KuralsFragment.dPosition = 0;
                KuralsFragment.CHAPTER_ID = -1;
                KuralsFragment.SEARCH_TEXT = "";
                // do not add this fragment to back stack, as this is the starting transaction
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, new KuralsFragment(), KuralsFragment.TAG);
                fragmentTransaction.commit();
                break;
            case R.id.nav_quiz:
                selectQuizType();
                break;
            case R.id.nav_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_rate:
                new Utilities(this).rateMe();
                break;
            case R.id.nav_reward:
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, new RewardFragment(), RewardFragment.TAG);
                fragmentTransaction.commit();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static void showAd(){
        try {
            if (mAd.isLoaded()) {
                mAd.show();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        Log.i(LOG_TAG, "Rewarded: onRewardedVideoAdLoaded");
    }

    @Override
    public void onRewardedVideoAdOpened() {
        Log.i(LOG_TAG, "Rewarded: onRewardedVideoAdOpened");
    }

    @Override
    public void onRewardedVideoStarted() {
        Log.i(LOG_TAG, "Rewarded: onRewardedVideoStarted");
    }

    @Override
    public void onRewardedVideoAdClosed() {
        Log.i(LOG_TAG, "Rewarded: onRewardedVideoAdClosed");
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        Log.i(LOG_TAG, "Rewarded:  onRewarded! currency: " + rewardItem.getType() + "  amount: " +
                rewardItem.getAmount());
        if(new Utilities().isEnglishEnabled(this)) {
            Toast.makeText(this, getString(R.string.msg_reward_thank_eng), Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this,getString(R.string.msg_reward_thank),Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Log.i(LOG_TAG, "Rewarded: onRewardedVideoAdLeftApplication ");
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        Log.i(LOG_TAG, "Rewarded: onRewardedVideoAdFailedToLoad: " + i);

    }

    public void shareMe(){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyy-hhmmss.SSS");
        String file = "File-" + simpleDateFormat.format(new Date()) + ".png";

        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);

        Uri uri = Uri.fromFile(store(getScreenShot(rootView),file));
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");

        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        try {
            startActivity(Intent.createChooser(intent, getString(R.string.app_name) + " - " + getString(R.string.nav_quiz)));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No App Available", Toast.LENGTH_SHORT).show();
        }

    }

    public static Bitmap getScreenShot(View view) {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public static File store(Bitmap bm, String fileName){
        File dir = new File(dirPath);
        if(!dir.exists())
            dir.mkdirs();
        File file = new File(dirPath, fileName);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public void selectQuizType(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.quiz_dialog);
        TextView tv1 = (TextView) dialog.findViewById(R.id.quiz_message);
        Button b1 = (Button) dialog.findViewById(R.id.quiz_random);
        Button b2 = (Button) dialog.findViewById(R.id.quiz_chapter);

        if(new Utilities().isEnglishEnabled(this)) {
            tv1.setText(getString(R.string.dialog_quiz_title_eng));
            b1.setText(getString(R.string.dialog_quiz_random_eng));
            b2.setText(getString(R.string.dialog_quiz_chapter_eng));
        }else{
            tv1.setText(getString(R.string.dialog_quiz_title));
            b1.setText(getString(R.string.dialog_quiz_random));
            b2.setText(getString(R.string.dialog_quiz_chapter));
        }
        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                QuizFragment.CHAPTER_ID = -1;
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, new QuizFragment(), QuizFragment.TAG);
                fragmentTransaction.commit();
                dialog.dismiss();
            }
        });


        b2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ChaptersFragment.MODE = ChaptersFragment.MODE_QUIZ;
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, new ChaptersFragment(), ChaptersFragment.TAG);
                fragmentTransaction.commit();
                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }
}
