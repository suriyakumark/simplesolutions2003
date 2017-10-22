package com.simplesolutions2003.onceuponatime;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.simplesolutions2003.onceuponatime.sync.ArticleSyncAdapter;
import com.simplesolutions2003.onceuponatime.sync.SyncAdapter;

public class MainActivity extends AppCompatActivity {

    private final static String LOG_TAG = MainActivity.class.getSimpleName();
    Toolbar toolbar;
    FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(getString(R.string.ad_device_id))
                .build();
        mAdView.loadAd(adRequest);

        Log.v(LOG_TAG, "initializeSyncAdapter");
        SyncAdapter.initializeSyncAdapter(this);

    }

    @Override
    public void onResume(){
        super.onResume();
        getSupportActionBar().setTitle(getString(R.string.app_name));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations( R.anim.slide_in_left, 0, 0, R.anim.slide_out_left);
        fragmentTransaction.replace(R.id.frame_container, new ArticlesFragment(), ArticlesFragment.TAG);
        //fragmentTransaction.addToBackStack(ArticlesFragment.TAG);
        fragmentTransaction.commit();

    }

}

