package com.simplesolutions2003.happybabycare;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.io.IOException;

/**
 * Created by SuriyaKumar on 8/20/2016.
 */
public class SoundsFragment extends Fragment{
    public final static boolean KEEP_IN_STACK = true;
    public final static String TAG = SoundsFragment.class.getSimpleName();

    private ImageButton rattle01ImageButton;
    private ImageButton rattle02ImageButton;
    private ImageButton whiteNoise01ImageButton;
    private ImageButton bellsImageButton;
    private ImageButton birds01ImageButton;
    private ImageButton birds02ImageButton;
    private ImageButton blenderImageButton;
    private ImageButton clockImageButton;
    private ImageButton fanfareImageButton;
    private ImageButton forestImageButton;
    private ImageButton frogImageButton;
    private ImageButton shhhImageButton;

    private int activeSound = 0;
    private int soundToPlay;
    MediaPlayer mp = new MediaPlayer();

    public SoundsFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.sounds, container, false);
        rattle01ImageButton = (ImageButton) rootView.findViewById(R.id.sound_rattle_01);
        rattle02ImageButton = (ImageButton) rootView.findViewById(R.id.sound_rattle_02);
        whiteNoise01ImageButton = (ImageButton) rootView.findViewById(R.id.sound_white_noise_01);
        bellsImageButton = (ImageButton) rootView.findViewById(R.id.sound_bells);
        birds01ImageButton = (ImageButton) rootView.findViewById(R.id.sound_birds_01);
        birds02ImageButton = (ImageButton) rootView.findViewById(R.id.sound_birds_02);
        blenderImageButton = (ImageButton) rootView.findViewById(R.id.sound_blender);
        clockImageButton = (ImageButton) rootView.findViewById(R.id.sound_clock);
        fanfareImageButton = (ImageButton) rootView.findViewById(R.id.sound_fanfare);
        forestImageButton = (ImageButton) rootView.findViewById(R.id.sound_forest);
        frogImageButton = (ImageButton) rootView.findViewById(R.id.sound_frog);
        shhhImageButton = (ImageButton) rootView.findViewById(R.id.sound_shhh);

        rattle01ImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(activeSound == 1){
                    activeSound = 0;
                }else{
                    activeSound = 1;
                }
                updateUI();

            }
        });

        rattle02ImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(activeSound == 2){
                    activeSound = 0;
                }else{
                    activeSound = 2;
                }
                updateUI();

            }
        });

        whiteNoise01ImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(activeSound == 3){
                    activeSound = 0;
                }else{
                    activeSound = 3;
                }
                updateUI();

            }
        });

        bellsImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(activeSound == 4){
                    activeSound = 0;
                }else{
                    activeSound = 4;
                }
                updateUI();

            }
        });
        birds01ImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(activeSound == 5){
                    activeSound = 0;
                }else{
                    activeSound = 5;
                }
                updateUI();

            }
        });
        birds02ImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(activeSound == 6){
                    activeSound = 0;
                }else{
                    activeSound = 6;
                }
                updateUI();

            }
        });


        blenderImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(activeSound == 7){
                    activeSound = 0;
                }else{
                    activeSound = 7;
                }
                updateUI();

            }
        });
        clockImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(activeSound == 8){
                    activeSound = 0;
                }else{
                    activeSound = 8;
                }
                updateUI();

            }
        });
        fanfareImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(activeSound == 9){
                    activeSound = 0;
                }else{
                    activeSound = 9;
                }
                updateUI();

            }
        });

        forestImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(activeSound == 10){
                    activeSound = 0;
                }else{
                    activeSound = 10;
                }
                updateUI();

            }
        });
        frogImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(activeSound == 11){
                    activeSound = 0;
                }else{
                    activeSound = 11;
                }
                updateUI();

            }
        });
        shhhImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(activeSound == 12){
                    activeSound = 0;
                }else{
                    activeSound = 12;
                }
                updateUI();

            }
        });


        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.v(TAG, "onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu,inflater);
        ((MainActivity) getActivity()).disableActionEditMenus();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        Log.v(TAG, "onPrepareOptionsMenu");
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        ((MainActivity) getActivity()).updateToolbarTitle("Sounds");
    }

    public void updateUI(){
        rattle01ImageButton.setAlpha(100);
        rattle02ImageButton.setAlpha(100);
        whiteNoise01ImageButton.setAlpha(100);
        bellsImageButton.setAlpha(100);
        birds01ImageButton.setAlpha(100);
        birds02ImageButton.setAlpha(100);
        blenderImageButton.setAlpha(100);
        clockImageButton.setAlpha(100);
        fanfareImageButton.setAlpha(100);
        forestImageButton.setAlpha(100);
        frogImageButton.setAlpha(100);
        shhhImageButton.setAlpha(100);

        switch(activeSound){
            case 1:
                rattle01ImageButton.setAlpha(255);
                handleMediaPlayback(R.raw.rattle_01);
                break;
            case 2:
                rattle02ImageButton.setAlpha(255);
                handleMediaPlayback(R.raw.rattle_02);
                break;
            case 3:
                whiteNoise01ImageButton.setAlpha(255);
                handleMediaPlayback(R.raw.white_noise_01);
                break;
            case 4:
                bellsImageButton.setAlpha(255);
                handleMediaPlayback(R.raw.bells);
                break;
            case 5:
                birds01ImageButton.setAlpha(255);
                handleMediaPlayback(R.raw.birds_01);
                break;
            case 6:
                birds02ImageButton.setAlpha(255);
                handleMediaPlayback(R.raw.birds_02);
                break;
            case 7:
                blenderImageButton.setAlpha(255);
                handleMediaPlayback(R.raw.blender);
                break;
            case 8:
                clockImageButton.setAlpha(255);
                handleMediaPlayback(R.raw.clock);
                break;
            case 9:
                fanfareImageButton.setAlpha(255);
                handleMediaPlayback(R.raw.fanfare);
                break;
            case 10:
                forestImageButton.setAlpha(255);
                handleMediaPlayback(R.raw.forest);
                break;
            case 11:
                frogImageButton.setAlpha(255);
                handleMediaPlayback(R.raw.frog);
                break;
            case 12:
                shhhImageButton.setAlpha(255);
                handleMediaPlayback(R.raw.shhh);
                break;
            default:
                handleMediaPlayback(0);
                break;
        }
    }

    public void handleMediaPlayback(int soundToPlay) {
        try {
            if (mp.isPlaying()) {
                mp.stop();
            }
        } catch (IllegalStateException e) {
            Log.v(TAG,"handleMediaPlayback - IllegalStateException");
        }

        mp.release();
        if (soundToPlay != 0) {
            mp = MediaPlayer.create(getActivity(), soundToPlay);
            mp.setLooping(true);
            mp.start();
        }

    }
}
