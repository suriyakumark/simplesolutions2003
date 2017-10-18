package com.simplesolutions2003.sleepifybaby;

import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public final static String TAG = MainActivity.class.getSimpleName();

    private ImageButton rattle01ImageButton;
    private ImageButton rattle02ImageButton;
    private ImageButton whiteNoise01ImageButton;
    private ImageButton bellsImageButton;
    private ImageButton birds01ImageButton;
    private ImageButton birds02ImageButton;
    private ImageButton blenderImageButton;
    private ImageButton clockImageButton;
    private ImageButton guitarImageButton;
    private ImageButton forestImageButton;
    private ImageButton frogImageButton;
    private ImageButton shhhImageButton;

    private static int activeSound = 0;
    private static MediaPlayer mp = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_launcher);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        rattle01ImageButton = (ImageButton) findViewById(R.id.sound_rattle_01);
        rattle02ImageButton = (ImageButton) findViewById(R.id.sound_rattle_02);
        whiteNoise01ImageButton = (ImageButton) findViewById(R.id.sound_white_noise_01);
        bellsImageButton = (ImageButton) findViewById(R.id.sound_bells);
        birds01ImageButton = (ImageButton) findViewById(R.id.sound_birds_01);
        birds02ImageButton = (ImageButton) findViewById(R.id.sound_birds_02);
        blenderImageButton = (ImageButton) findViewById(R.id.sound_blender);
        clockImageButton = (ImageButton) findViewById(R.id.sound_clock);
        guitarImageButton = (ImageButton) findViewById(R.id.sound_guitar);
        forestImageButton = (ImageButton) findViewById(R.id.sound_forest);
        frogImageButton = (ImageButton) findViewById(R.id.sound_frog);
        shhhImageButton = (ImageButton) findViewById(R.id.sound_shhh);

        rattle01ImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(activeSound == 1){
                    activeSound = 0;
                }else{
                    activeSound = 1;
                }
                updateUI();
                selectSound();

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
                selectSound();

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
                selectSound();

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
                selectSound();

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
                selectSound();

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
                selectSound();

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
                selectSound();

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
                selectSound();

            }
        });
        guitarImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(activeSound == 9){
                    activeSound = 0;
                }else{
                    activeSound = 9;
                }
                updateUI();
                selectSound();

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
                selectSound();

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
                selectSound();

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
                selectSound();

            }
        });

        if(activeSound == 0) {
            handleVolume();
            Toast.makeText(this, getString(R.string.msg), Toast.LENGTH_SHORT).show();
        }
    }

    public void onResume(){
        super.onResume();
        updateUI();
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
        guitarImageButton.setAlpha(100);
        forestImageButton.setAlpha(100);
        frogImageButton.setAlpha(100);
        shhhImageButton.setAlpha(100);

        switch(activeSound){
            case 1:
                rattle01ImageButton.setAlpha(255);
                break;
            case 2:
                rattle02ImageButton.setAlpha(255);
                break;
            case 3:
                whiteNoise01ImageButton.setAlpha(255);
                break;
            case 4:
                bellsImageButton.setAlpha(255);
                break;
            case 5:
                birds01ImageButton.setAlpha(255);
                break;
            case 6:
                birds02ImageButton.setAlpha(255);
                break;
            case 7:
                blenderImageButton.setAlpha(255);
                break;
            case 8:
                clockImageButton.setAlpha(255);
                break;
            case 9:
                guitarImageButton.setAlpha(255);
                break;
            case 10:
                forestImageButton.setAlpha(255);
                break;
            case 11:
                frogImageButton.setAlpha(255);
                break;
            case 12:
                shhhImageButton.setAlpha(255);
                break;
            default:
                break;
        }
    }


    public void selectSound(){

        switch(activeSound){
            case 1:
                handleMediaPlayback(R.raw.rattle_01);
                break;
            case 2:
                handleMediaPlayback(R.raw.rattle_02);
                break;
            case 3:
                handleMediaPlayback(R.raw.white_noise_01);
                break;
            case 4:
                handleMediaPlayback(R.raw.bells);
                break;
            case 5:
                handleMediaPlayback(R.raw.birds_01);
                break;
            case 6:
                handleMediaPlayback(R.raw.birds_02);
                break;
            case 7:
                handleMediaPlayback(R.raw.blender);
                break;
            case 8:
                handleMediaPlayback(R.raw.clock);
                break;
            case 9:
                handleMediaPlayback(R.raw.guitar);
                break;
            case 10:
                handleMediaPlayback(R.raw.forest);
                break;
            case 11:
                handleMediaPlayback(R.raw.frog);
                break;
            case 12:
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
            mp = MediaPlayer.create(this, soundToPlay);
            mp.setLooping(true);
            mp.start();
        }

    }

    public void handleVolume(){

        final AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int currVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currVolumeLevel = (int) ((currVolume * 100.0f) /maxVolume);

        if(currVolumeLevel > 50.0f) {
            Dialog volumeDialog = new Dialog(this);
            volumeDialog.setContentView(R.layout.volume);
            volumeDialog.setTitle(getString(R.string.dialog_volume));
            volumeDialog.show();

            SeekBar musicSeekBar = (SeekBar) volumeDialog.findViewById(R.id.volume_music);

            musicSeekBar.setMax(maxVolume);
            musicSeekBar.setProgress(currVolume);

            SeekBar.OnSeekBarChangeListener volumeSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    //add code here
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    switch (seekBar.getId()) {
                        case R.id.volume_music:
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                            break;
                        default:
                            break;
                    }
                }
            };
            musicSeekBar.setOnSeekBarChangeListener(volumeSeekBarListener);
        }
    }
}
