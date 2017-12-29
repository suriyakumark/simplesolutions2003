package com.simplesolutions2003.thirukkuralplus;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.simplesolutions2003.thirukkuralplus.data.AppContract.KuralsEntry;

/**
 * Created by SuriyaKumar on 8/20/2016.
 */
public class PrefaceFragment extends Fragment{
    public final static boolean KEEP_IN_STACK = true;
    public final static String TAG = PrefaceFragment.class.getSimpleName();

    public PrefaceFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.preface, container, false);

        TextView appMenuHeading = (TextView) rootView.findViewById(R.id.app_menu_heading);
        TextView prefaceText = (TextView) rootView.findViewById(R.id.preface_text);
        TextView prefaceTextEng = (TextView) rootView.findViewById(R.id.preface_text_eng);

        if(new Utilities().isEnglishEnabled(this.getContext())) {
            appMenuHeading.setText(getString(R.string.nav_preface_eng));
        }else{
            appMenuHeading.setText(getString(R.string.nav_preface));
        }
        prefaceText.setText(getString(R.string.content_preface));
        prefaceTextEng.setText(getString(R.string.content_preface_eng));

        if(!new Utilities().isEnglishEnabled(this.getContext())) {
            prefaceTextEng.setVisibility(View.GONE);
        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.v(TAG, "onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        Log.v(TAG, "onPrepareOptionsMenu");
        super.onPrepareOptionsMenu(menu);
    }

    public void onResume()
    {
        super.onResume();
        MainActivity.CURRENT_FRAGMENT = TAG;
        hideKeyboard();
    }

    public void hideKeyboard(){
        Utilities.hideKeyboardFrom(this.getContext(),this.getView().getRootView());
    }
}
