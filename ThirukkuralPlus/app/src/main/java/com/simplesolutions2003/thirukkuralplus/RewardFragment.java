package com.simplesolutions2003.thirukkuralplus;

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
import android.widget.Toast;

/**
 * Created by SuriyaKumar on 8/20/2016.
 */
public class RewardFragment extends Fragment{
    public final static boolean KEEP_IN_STACK = true;
    public final static String TAG = RewardFragment.class.getSimpleName();

    public RewardFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.reward, container, false);

        TextView appMenuHeading = (TextView) rootView.findViewById(R.id.app_menu_heading);
        TextView rewardText = (TextView) rootView.findViewById(R.id.reward_text);
        TextView rewardTextEng = (TextView) rootView.findViewById(R.id.reward_text_eng);

        if(new Utilities().isEnglishEnabled(this.getContext())) {
            appMenuHeading.setText(getString(R.string.nav_reward_eng));
        }else{
            appMenuHeading.setText(getString(R.string.nav_reward));
        }
        rewardText.setText(getString(R.string.content_reward));
        rewardTextEng.setText(getString(R.string.content_reward_eng));

        if(!new Utilities().isEnglishEnabled(this.getContext())) {
            rewardTextEng.setVisibility(View.GONE);
        }

        ImageButton btn_reward = (ImageButton) rootView.findViewById(R.id.btn_reward);
        btn_reward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.showAd();
            }
        });

        if(!new Utilities(this.getContext()).isInternetOn()){
            Toast.makeText(this.getContext(), this.getContext().getString(R.string.msg_internet), Toast.LENGTH_LONG).show();
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
