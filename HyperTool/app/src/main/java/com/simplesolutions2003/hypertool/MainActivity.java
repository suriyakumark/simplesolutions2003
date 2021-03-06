package com.simplesolutions2003.hypertool;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Toast;
import java.util.Map;

import com.simplesolutions2003.hypertool.sync.WeatherSyncAdapter;

public class MainActivity extends AppCompatActivity implements SensorEventListener, GestureDetector.OnGestureListener {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DEGREE  = "\u00b0";
    private final int SENSOR_DELAY = 2000000;
    private final int LOCATION_DELAY_DISTANCE = 5000;
    private final int LOCATION_DELAY_TIME = 0;

    private static Context context;
    private SensorManager sensorManager;
    private GestureDetector myGesture;


    public static TextView tvDeviceBrand;
    public static TextView tvDeviceModel;
    public static TextView tvDeviceOS;

    public static TextView tvCarrierName;
    public static TextView tvCarrierStrength;
    public static ImageView ivCarrierLevel;

    public static TextView tvStorageInternalTotal;
    public static TextView tvStorageInternalUsed;
    public static TextView tvStorageInternalFree;
    public static TextView tvStorageInternalPercentage;

    public static TextView tvStorageExternalTotal;
    public static TextView tvStorageExternalUsed;
    public static TextView tvStorageExternalFree;
    public static TextView tvStorageExternalPercentage;

    public static TextView tvCpuType;
    public static TextView tvCpuInfo;
    public static TextView tvCpuSpeed;
    public static TextView tvCpuPercentage;

    public static TextView tvRamTotal;
    public static TextView tvRamUsed;
    public static TextView tvRamFree;
    public static TextView tvRamPercentage;

    public static ImageView ivBatterySwitch;
    public static TextView tvBatteryLevel;
    public static ImageView ivBatteryStatus;
    public static TextView tvBatteryVolt;
    public static TextView tvBatteryTemp;
    public static TextView tvBatteryCap;

    public static TextView tvVolumeLevel;
    public static ImageButton ibSoundSwitch;

    public static TextView tvBrightnessLevel;
    public static ImageButton ibDisplaySwitch;

    public static TextView tvDateDay;
    public static TextView tvDateMonthName;
    public static TextView tvDateYear_cc;
    public static TextView tvDateYear_yy;
    public static TextView tvDateDayOfWeek;
    public static TextView tvDateWeekOfYear;
    public static TextView tvDateMonthOfYear;
    public static TextView tvDateDayOfYear;

    public static TextView tvSunRiseTime;
    public static TextView tvSunSetTime;
    public static TextView tvCity;

    public static TextView tvCompassDirection;
    public static TextView tvCompassAltDirection;
    public static String sCompassQuadrant = "";
    public static String sCompassAzimuth = "";
    public static ImageView ivCompass;
    public static boolean bCompass;

    public static TextView tvDataUsage;
    public static ImageButton ibDataSwitch;
    public static boolean bDataSwitch;

    public static TextView tvWifiUsage;
    public static ImageButton ibWifiSwitch;
    public static boolean bWifiSwitch;

    public static TextView tvTimeHHMMSS;
    public static TextView tvTimeAMPM;

    public static TextView tvStopwatch;
    public static ImageButton ibStopwatch;
    public static int iStopwatch = 0;
    public static long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;
    public static int Seconds, Minutes, MilliSeconds ;

    public static ImageView ivPedometer;
    public static TextView tvPedometerCount;
    public static TextView tvPedometerCalorie;
    public static TextView tvPedometerDistance;
    public static boolean bPedometerSwitch;

    public static boolean bAirplaneSwitch;
    public static ImageButton ibAirplaneSwitch;
    public static boolean bGpsSwitch;
    public static ImageButton ibGpsSwitch;
    public static String sLongitude;
    public static String sLatitude;
    public static TextView tvLongitude;
    public static TextView tvLatitude;
    public static TextView tvCityLoc;

    public static TextView tvWeatherTempNowC;
    public static TextView tvWeatherTempNowF;
    public static TextView tvWeatherTempHiC;
    public static TextView tvWeatherTempHiF;
    public static TextView tvWeatherTempLoC;
    public static TextView tvWeatherTempLoF;
    public static TextView tvWeatherWindKmph;
    public static TextView tvWeatherWindMph;
    public static TextView tvWeatherWindDir;
    public static TextView tvWeatherForecast;
    public static ImageView ivWeatherForecast;

    public static boolean bBluetoothSwitch;
    public static ImageButton ibBluetoothSwitch;
    public static boolean bTorchSwitch;
    public static ImageButton ibTorchSwitch;

    public static final float fSwitchOff = 0.3f;
    public static final float fSwitchOn = 1.0f;

    static float[] mGravity;
    static float[] mGeomagnetic;
    static float[] mOrientation;

    private PedometerService mPedometerService;
    private DateChangeNotifyService mDateChangeNotifyService;
    private Handler mHandler = new Handler();
    private static Handler mStopwatchHandler = new Handler();

    private SettingsContentObserver mSettingsContentObserver;
    private TelephonyManager tManager;
    private static float mCurrentDegree = 0f;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private static final float ALPHA = 0.1f;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(LOG_TAG, "Broadcast Receiver - " + intent.getAction().toString());
            switch (intent.getAction()){
                case WeatherSyncAdapter.ACTION_DATA_UPDATED_WEATHER:
                    updateWeatherInfo();
                    break;
                case BatteryReceiver.ACTION_DATA_UPDATED_BATTERY:
                    updateBatteryInfo();
                    break;
                case AirplaneReceiver.ACTION_DATA_UPDATED_AIRPLANE:
                    updateAirplaneInfo();
                    break;
                case BluetoothReceiver.ACTION_DATA_UPDATED_BLUETOOTH:
                    updateBluetoothInfo();
                    break;
                case DateChangeNotifyService.ACTION_DATA_UPDATED_DATE:
                    updateDateInfo();
                    break;
                case SettingsContentObserver.ACTION_DATA_UPDATED_SETTINGS:
                    updateVolumeInfo();
                    updateBluetoothInfo();
                    updateBrightnessInfo();
                    break;
                case SignalReceiver.ACTION_DATA_UPDATED_SIGNAL:
                    updateCarrierInfo();
                    break;
                case WifiReceiver.ACTION_DATA_UPDATED_WIFI:
                    updateWifiInfo();
                    break;
                case DateChangeNotifyService.ACTION_DATA_UPDATED_DATA_USAGE:
                    updateDataUsage();
                    break;
                case DateChangeNotifyService.ACTION_DATA_UPDATED_WIFI_USAGE:
                    updateWifiUsage();
                    break;
                case PedometerService.ACTION_DATA_UPDATED_PEDOMETER:
                    updatePedometerInfo();
                    break;
                case MyLocationListener.ACTION_DATA_UPDATED_LOCATION:
                    updateLocation();
                    break;
                case MyLocationListener.ACTION_DATA_UPDATED_GPS:
                    updateGpsInfo();
                    setupLocation();
                    getLocation();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        checkPermission();
        myGesture = new GestureDetector(getBaseContext(),
                (GestureDetector.OnGestureListener) this);


        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Map<String, ?> allEntries = prefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.v(LOG_TAG, "SharedPref - " + entry.getKey() + ": " + entry.getValue().toString());
        }

        tvDeviceBrand = (TextView) findViewById(R.id.device_brand);
        tvDeviceModel = (TextView) findViewById(R.id.device_model);
        tvDeviceOS = (TextView) findViewById(R.id.device_os);
        tvCarrierName = (TextView) findViewById(R.id.carrier_name);
        tvCarrierStrength = (TextView) findViewById(R.id.carrier_strength);
        ivCarrierLevel = (ImageView) findViewById(R.id.carrier_img);
        tvStorageInternalTotal = (TextView) findViewById(R.id.internal_memory_total);
        tvStorageInternalUsed = (TextView) findViewById(R.id.internal_memory_used);
        tvStorageInternalFree = (TextView) findViewById(R.id.internal_memory_free);
        tvStorageInternalPercentage = (TextView) findViewById(R.id.internal_memory_percentage);
        tvStorageExternalTotal = (TextView) findViewById(R.id.external_memory_total);
        tvStorageExternalUsed = (TextView) findViewById(R.id.external_memory_used);
        tvStorageExternalFree = (TextView) findViewById(R.id.external_memory_free);
        tvStorageExternalPercentage = (TextView) findViewById(R.id.external_memory_percentage);
        tvCpuType = (TextView) findViewById(R.id.cpu_type);
        tvCpuInfo = (TextView) findViewById(R.id.cpu_info);
        tvCpuSpeed = (TextView) findViewById(R.id.cpu_speed);
        tvCpuPercentage = (TextView) findViewById(R.id.cpu_percentage);
        tvRamTotal = (TextView) findViewById(R.id.ram_total);
        tvRamUsed = (TextView) findViewById(R.id.ram_used);
        tvRamFree = (TextView) findViewById(R.id.ram_free);
        tvRamPercentage = (TextView) findViewById(R.id.ram_percentage);
        ivBatterySwitch = (ImageView) findViewById(R.id.battery_img);
        tvBatteryLevel = (TextView) findViewById(R.id.battery_level);
        ivBatteryStatus = (ImageView) findViewById(R.id.battery_status);
        tvBatteryVolt = (TextView) findViewById(R.id.battery_volt);
        tvBatteryTemp = (TextView) findViewById(R.id.battery_temp);
        tvBatteryCap = (TextView) findViewById(R.id.battery_cap);
        tvVolumeLevel = (TextView) findViewById(R.id.volume_level);
        ibSoundSwitch = (ImageButton) findViewById(R.id.volume_img);
        tvBrightnessLevel = (TextView) findViewById(R.id.brightness_level);
        ibDisplaySwitch = (ImageButton) findViewById(R.id.brightness_img);
        tvDateDay = (TextView) findViewById(R.id.date_day);
        tvDateMonthName = (TextView) findViewById(R.id.date_month);
        tvDateYear_cc = (TextView) findViewById(R.id.date_year_cc);
        tvDateYear_yy = (TextView) findViewById(R.id.date_year_yy);
        tvDateDayOfWeek = (TextView) findViewById(R.id.date_day_of_week);
        tvDateWeekOfYear = (TextView) findViewById(R.id.date_week_of_year);
        tvDateMonthOfYear = (TextView) findViewById(R.id.date_month_of_year);
        tvDateDayOfYear = (TextView) findViewById(R.id.date_day_of_year);
        tvSunRiseTime = (TextView) findViewById(R.id.sun_rise);
        tvSunSetTime = (TextView) findViewById(R.id.sun_set);
        tvCity = (TextView) findViewById(R.id.city);
        tvCompassDirection = (TextView) findViewById(R.id.compass_direction);
        tvCompassAltDirection = (TextView) findViewById(R.id.compass_alt_direction);
        ivCompass = (ImageView) findViewById(R.id.compass_img);
        tvDataUsage = (TextView) findViewById(R.id.data_usage);
        ibDataSwitch = (ImageButton) findViewById(R.id.data_img);
        tvWifiUsage = (TextView) findViewById(R.id.wifi_usage);
        ibWifiSwitch = (ImageButton) findViewById(R.id.wifi_img);
        tvTimeHHMMSS = (TextView) findViewById(R.id.time_dtl);
        tvTimeAMPM = (TextView) findViewById(R.id.time_am_pm);
        tvStopwatch = (TextView) findViewById(R.id.stopwatch);
        ibStopwatch = (ImageButton) findViewById(R.id.stopwatch_img);
        ivPedometer = (ImageView) findViewById(R.id.pedometer_img);
        tvPedometerCount = (TextView) findViewById(R.id.pedometer_steps);
        tvPedometerCalorie = (TextView) findViewById(R.id.pedometer_calorie);
        tvPedometerDistance = (TextView) findViewById(R.id.pedometer_distance);
        ibAirplaneSwitch = (ImageButton) findViewById(R.id.airplane_img);
        ibGpsSwitch = (ImageButton) findViewById(R.id.gps_img);
        tvLongitude = (TextView) findViewById(R.id.longitude);
        tvLatitude = (TextView) findViewById(R.id.latitude);
        tvWeatherTempNowC = (TextView) findViewById(R.id.temperature_c);
        tvWeatherTempNowF = (TextView) findViewById(R.id.temperature_f);
        tvWeatherTempHiC = (TextView) findViewById(R.id.forecast_hi_c);
        tvWeatherTempHiF = (TextView) findViewById(R.id.forecast_hi_f);
        tvWeatherTempLoC = (TextView) findViewById(R.id.forecast_lo_c);
        tvWeatherTempLoF = (TextView) findViewById(R.id.forecast_lo_f);
        tvWeatherWindKmph = (TextView) findViewById(R.id.wind_speed_km);
        tvWeatherWindMph = (TextView) findViewById(R.id.wind_speed_m);
        tvWeatherWindDir = (TextView) findViewById(R.id.wind_dir);
        tvWeatherForecast = (TextView) findViewById(R.id.weather_dtl);
        ivWeatherForecast = (ImageView) findViewById(R.id.weather_img);
        ibBluetoothSwitch = (ImageButton) findViewById(R.id.bluetooth_img);
        ibTorchSwitch = (ImageButton) findViewById(R.id.torch_img);

        Utilities.setContext(this);
        WeatherInfo.setContext(this);

        Log.v(LOG_TAG, "initializeSyncAdapter");
        WeatherSyncAdapter.initializeSyncAdapter(this);

        Toast.makeText(this, getString(R.string.msg_pref_press), Toast.LENGTH_SHORT).show();
    }


    @TargetApi(23)
    private void checkPermission() {
        Log.v(LOG_TAG,"checkPermission");
        if ( Build.VERSION.SDK_INT >= 23){
            int PERMISSION_ALL = 1;
            String[] PERMISSIONS = {Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_SETTINGS,
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_CALENDAR,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.READ_SYNC_SETTINGS,
                    Manifest.permission.WRITE_SYNC_SETTINGS,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE};

            if(!hasPermissions(this, PERMISSIONS)){
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            }

            if(!Settings.System.canWrite(this)){
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                startActivity(intent);
            }
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void onResume(){
        Log.v(LOG_TAG,"onResume");
        super.onResume();

        //register listener when app resumes
        IntentFilter filter = new IntentFilter();
        filter.addAction(WeatherSyncAdapter.ACTION_DATA_UPDATED_WEATHER);
        filter.addAction(BatteryReceiver.ACTION_DATA_UPDATED_BATTERY);
        filter.addAction(AirplaneReceiver.ACTION_DATA_UPDATED_AIRPLANE);
        filter.addAction(BluetoothReceiver.ACTION_DATA_UPDATED_BLUETOOTH);
        filter.addAction(DateChangeNotifyService.ACTION_DATA_UPDATED_DATE);
        filter.addAction(SettingsContentObserver.ACTION_DATA_UPDATED_SETTINGS);
        filter.addAction(SignalReceiver.ACTION_DATA_UPDATED_SIGNAL);
        filter.addAction(WifiReceiver.ACTION_DATA_UPDATED_WIFI);
        filter.addAction(PedometerService.ACTION_DATA_UPDATED_PEDOMETER);
        filter.addAction(MyLocationListener.ACTION_DATA_UPDATED_LOCATION);
        filter.addAction(MyLocationListener.ACTION_DATA_UPDATED_GPS);
        filter.addAction(DateChangeNotifyService.ACTION_DATA_UPDATED_DATA_USAGE);
        filter.addAction(DateChangeNotifyService.ACTION_DATA_UPDATED_WIFI_USAGE);
        registerReceiver(broadcastReceiver, filter);

        setupAll();
        updateAll();

    }

    @Override
    protected void onPause() {
        Log.v(LOG_TAG,"onPause");
        super.onPause();

        // unregister listener when app is not active
        sensorManager.unregisterListener(this);
        mHandler.removeCallbacks(updateTimeInfo);
        getApplicationContext().getContentResolver().unregisterContentObserver(mSettingsContentObserver);
        unregisterReceiver(broadcastReceiver);
        tManager.listen(new SignalReceiver(this), PhoneStateListener.LISTEN_NONE);
        locationManager.removeUpdates(locationListener);
    }

    public void setupAll(){
        Log.v(LOG_TAG,"setupAll");
        setupDateChangeNotifyService();
        setupSensor();
        setupPedometerInfo();
        setupCompassInfo();
        setupVolumeBluetoothInfo();
        setupCarrierInfo();
        setupGps();
        setupLocation();
        setupAirplane();
        setupSound();
        setupBrightness();
        setupData();
        setupWifi();
        setupBluetooth();
        setupTorch();
        setupStopwatch();
    }

    public void updateAll(){
        Log.v(LOG_TAG,"updateAll");
        updateDeviceInfo();
        updateCarrierInfo();
        updateCpuInfo();
        updateStorageInternalInfo();
        updateStorageExternalInfo();
        updateRamInfo();
        updateDateInfo();
        updateVolumeInfo();
        updateBrightnessInfo();
        mHandler.postDelayed(updateTimeInfo,500);
        updateBatteryInfo();
        //updateCompassInfo();
        //updatePedometerInfo();
        updateDataInfo();
        updateWifiInfo();
        updateAirplaneInfo();
        updateGpsInfo();
        updateLocation();
        updateBluetoothInfo();
        updateTorchInfo();
        updateWeatherInfo();
    }

    public void setupDateChangeNotifyService(){
        Log.v(LOG_TAG,"setupDateChangeNotifyService");
        mDateChangeNotifyService = new DateChangeNotifyService(context);
        if (!isMyServiceRunning(mDateChangeNotifyService.getClass())) {
            startService(new Intent(context, mDateChangeNotifyService.getClass()));
        }
    }

    public void setupSensor(){
        Log.v(LOG_TAG,"setupSensor");
        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
    }

    public void setupCompassInfo() {
        Log.v(LOG_TAG,"setupCompassInfo");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Sensor compassSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if(compassSensor != null && accelerometerSensor != null) {
                sensorManager.registerListener(this, compassSensor, SENSOR_DELAY, SENSOR_DELAY);
                sensorManager.registerListener(this, accelerometerSensor, SENSOR_DELAY, SENSOR_DELAY);
                bCompass = true;
            }else{
                bCompass = false;
            }

        }else{
            Sensor orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
            sensorManager.registerListener(this, orientationSensor, SENSOR_DELAY);
        }
    }

    public void setupPedometerInfo() {
        Log.v(LOG_TAG,"setupPedometerInfo");
        bPedometerSwitch = false;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
                updatePedometerInfo();
                bPedometerSwitch = true;
                mPedometerService = new PedometerService();
                if (!isMyServiceRunning(mPedometerService.getClass())) {
                    startService(new Intent(context, mPedometerService.getClass()));
                }
            }
        }

        if(bPedometerSwitch){
            ivPedometer.setAlpha(fSwitchOn);
        }else{
            ivPedometer.setAlpha(fSwitchOff);
        }
    }

    public void setupVolumeBluetoothInfo(){
        Log.v(LOG_TAG,"setupVolumeBluetoothInfo");
        mSettingsContentObserver = new SettingsContentObserver(new Handler(),getApplicationContext());
        getApplicationContext().getContentResolver().registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, mSettingsContentObserver );
        getApplicationContext().getContentResolver().registerContentObserver(android.provider.Settings.Global.CONTENT_URI, true, mSettingsContentObserver );

    }

    public void setupAirplane(){
        Log.v(LOG_TAG,"setupAirplane");
        ibAirplaneSwitch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Utilities.airplaneOnOff();
            }
        });
    }

    public void setupStopwatch(){
        Log.v(LOG_TAG,"setupStopwatch");
        resetStopwatch();
        ibStopwatch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                switch (iStopwatch) {
                    case 0:
                        startStopwatch();
                        break;
                    case 1:
                        stopStopwatch();
                        break;
                    case 2:
                        resetStopwatch();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    public void setupSound(){
        Log.v(LOG_TAG,"setupSound");
        ibSoundSwitch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final AlertDialog volumeDialog = new AlertDialog.Builder(context)
                        .setTitle(getString(R.string.dialog_volume))
                        .setView(R.layout.volume)
                        .create();
                volumeDialog.show();

                SeekBar musicSeekBar = (SeekBar) volumeDialog.findViewById(R.id.volume_music);
                SeekBar ringSeekBar = (SeekBar) volumeDialog.findViewById(R.id.volume_ring);
                SeekBar callSeekBar = (SeekBar) volumeDialog.findViewById(R.id.volume_call);
                SeekBar systemSeekBar = (SeekBar) volumeDialog.findViewById(R.id.volume_system);
                SeekBar alarmSeekBar = (SeekBar) volumeDialog.findViewById(R.id.volume_alarm);
                //ImageButton vibrateButton = (ImageButton) volumeDialog.findViewById(R.id.volume_vibrate);

                final AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                musicSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
                ringSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_RING));
                callSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL));
                systemSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM));
                alarmSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM));

                musicSeekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                ringSeekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_RING));
                callSeekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL));
                systemSeekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM));
                alarmSeekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_ALARM));

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
                            case R.id.volume_ring:
                                audioManager.setStreamVolume(AudioManager.STREAM_RING, progress, 0);
                                break;
                            case R.id.volume_call:
                                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, progress, 0);
                                break;
                            case R.id.volume_system:
                                audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, progress, 0);
                                break;
                            case R.id.volume_alarm:
                                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, progress, 0);
                                break;
                            default:
                                break;
                        }
                    }
                };
                musicSeekBar.setOnSeekBarChangeListener(volumeSeekBarListener);
                ringSeekBar.setOnSeekBarChangeListener(volumeSeekBarListener);
                callSeekBar.setOnSeekBarChangeListener(volumeSeekBarListener);
                systemSeekBar.setOnSeekBarChangeListener(volumeSeekBarListener);
                alarmSeekBar.setOnSeekBarChangeListener(volumeSeekBarListener);

            }
        });
    }

    public void setupBrightness(){
        Log.v(LOG_TAG,"setupBrightness");
        ibDisplaySwitch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                final AlertDialog brightnessDialog = new AlertDialog.Builder (context)
                        .setTitle(getString(R.string.dialog_brightness))
                        .setView(R.layout.brightness)
                        .create();
                brightnessDialog.show();

                SeekBar brightnessSeekBar = (SeekBar) brightnessDialog.findViewById(R.id.brightness_seekbar);
                brightnessSeekBar.setMax(255);
                try {
                    brightnessSeekBar.setProgress(Settings.System.getInt(getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS));
                    //Log.v(LOG_TAG,"brightnessSeekBar - " + brightnessSeekBar.getProgress());
                }catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
                SeekBar.OnSeekBarChangeListener brightnessSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        brightnessDialog.dismiss();
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        //add code here
                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, progress);
                    }
                };
                brightnessSeekBar.setOnSeekBarChangeListener(brightnessSeekBarListener);

            }
        });
    }

    public void setupData(){
        Log.v(LOG_TAG,"setupData");
        ibDataSwitch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Utilities.dataOnOff();
                updateDataInfo();
            }
        });
        DataWifiUsage.setContext(context);
        DataWifiUsage.getDataUsage();
        updateDataUsage();
    }

    public void setupGps(){
        Log.v(LOG_TAG,"setupGps");
        ibGpsSwitch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Utilities.gpsOnOff();
                updateGpsInfo();
            }
        });
    }

    public void setupLocation(){
        Log.v(LOG_TAG,"setupLocation");
        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        locationListener = new MyLocationListener(context);
        if(locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER )) {
            try {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, LOCATION_DELAY_TIME, LOCATION_DELAY_DISTANCE, locationListener);

            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    public void getLocation(){
        Log.v(LOG_TAG,"getLocation");
        try {
            Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(loc != null) {
                String longitude = Double.toString(loc.getLongitude());
                String latitude = Double.toString(loc.getLatitude());

                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                editor.putString(getString(R.string.pref_key_longitude), longitude);
                editor.putString(getString(R.string.pref_key_latitude), latitude);
                editor.commit();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }


    public void setupWifi(){
        Log.v(LOG_TAG,"setupWifi");
        ibWifiSwitch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(bWifiSwitch) {
                    Utilities.wifiOff();
                }else{
                    Utilities.wifiOn();
                }
                updateWifiInfo();
            }
        });
        DataWifiUsage.setContext(context);
        DataWifiUsage.getWifiUsage();
        updateWifiUsage();
    }

    public void setupBluetooth(){
        Log.v(LOG_TAG,"setupBluetooth");
        ibBluetoothSwitch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(bBluetoothSwitch) {
                    Utilities.bluetoothOff();
                }else{
                    Utilities.bluetoothOn();
                }
                updateBluetoothInfo();
            }
        });
    }

    public void setupTorch(){
        Log.v(LOG_TAG,"setupTorch");
        ibTorchSwitch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(bTorchSwitch) {
                    Utilities.torchOff();
                }else{
                    Utilities.torchOn();
                }
                updateTorchInfo();
            }
        });
    }

    public void setupCarrierInfo(){
        Log.v(LOG_TAG,"setupCarrierInfo");
        tManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        tManager.listen(new SignalReceiver(this), PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }


    public static void updateDeviceInfo(){
        Log.v(LOG_TAG,"updateDeviceInfo");
        Utilities.getDeviceInfo();
        tvDeviceBrand.setText(Utilities.sDeviceBrand);
        tvDeviceModel.setText(Utilities.sDeviceModel);
        tvDeviceOS.setText(Utilities.sDeviceOS);
    }

    public static void updateCarrierInfo(){
        Log.v(LOG_TAG,"updateCarrierInfo");
        Utilities.getCarrierInfo();
        tvCarrierName.setText(Utilities.sCarrierName);
        tvCarrierStrength.setText(Utilities.sCarrierStrength);

        switch(Utilities.iCarrierLevel) {
            case 0:
                ivCarrierLevel.setImageResource(R.drawable.signal_0);
                break;
            case 1:
                ivCarrierLevel.setImageResource(R.drawable.signal_1);
                break;
            case 2:
                ivCarrierLevel.setImageResource(R.drawable.signal_3);
                break;
            case 3:
                ivCarrierLevel.setImageResource(R.drawable.signal_4);
                break;
            case 4:
                ivCarrierLevel.setImageResource(R.drawable.signal_5);
                break;
            default:
                ivCarrierLevel.setImageResource(R.drawable.signal_5);
                break;
        }

    }

    public static void updateCpuInfo(){
        Log.v(LOG_TAG,"updateCpuInfo");
        Utilities.getCpuInfo();
        tvCpuType.setText(Utilities.sCpuType);
        tvCpuInfo.setText(Utilities.sCpuInfo);
        tvCpuSpeed.setText(Utilities.sCpuSpeed);
        tvCpuPercentage.setText(Utilities.sCpuPercentage);
    }

    public static void updateStorageInternalInfo(){
        Log.v(LOG_TAG,"updateStorageInternalInfo");
        Utilities.getStorageInternalInfo();
        tvStorageInternalTotal.setText(Utilities.sStorageInternalTotal);
        tvStorageInternalUsed.setText(Utilities.sStorageInternalUsed);
        tvStorageInternalFree.setText(Utilities.sStorageInternalFree);
        tvStorageInternalPercentage.setText(Utilities.sStorageInternalPercentage);
    }

    public static void updateStorageExternalInfo(){
        Log.v(LOG_TAG,"updateStorageExternalInfo");
        Utilities.getStorageExternalInfo();
        tvStorageExternalTotal.setText(Utilities.sStorageExternalTotal);
        tvStorageExternalUsed.setText(Utilities.sStorageExternalUsed);
        tvStorageExternalFree.setText(Utilities.sStorageExternalFree);
        tvStorageExternalPercentage.setText(Utilities.sStorageExternalPercentage);
    }

    public static void updateRamInfo(){
        Log.v(LOG_TAG,"updateRamInfo");
        Utilities.getRamInfo();
        tvRamTotal.setText(Utilities.sRamTotal);
        tvRamUsed.setText(Utilities.sRamUsed);
        tvRamFree.setText(Utilities.sRamFree);
        tvRamPercentage.setText(Utilities.sRamPercentage);
    }

    public static void updateDateInfo(){
        Log.v(LOG_TAG,"updateDateInfo");
        Utilities.getDateInfo();
        tvDateDay.setText(Utilities.sDateDay);
        tvDateMonthName.setText(Utilities.sDateMonthName);
        tvDateDayOfWeek.setText(Utilities.sDateDayOfWeek);
        tvDateYear_cc.setText(Utilities.sDateYear_cc);
        tvDateYear_yy.setText(Utilities.sDateYear_yy);
        tvDateWeekOfYear.setText(Utilities.sDateWeekOfYear);
        tvDateMonthOfYear.setText(Utilities.sDateMonthOfYear);
        tvDateDayOfYear.setText(Utilities.sDateDayOfYear);
    }

    public static void updateVolumeInfo(){
        Log.v(LOG_TAG,"updateVolumeInfo");
        Utilities.getVolumeInfo();
        tvVolumeLevel.setText(Utilities.sVolumeLevel);
    }

    public static void updateBrightnessInfo(){
        Log.v(LOG_TAG,"updateBrightnessInfo");
        Utilities.getBrightnessInfo();
        tvBrightnessLevel.setText(Utilities.sBrightnessLevel);
    }

    public Runnable updateTimeInfo = new Runnable(){
        public void run() {
            Utilities.getTimeInfo();
            tvTimeHHMMSS.setText(Utilities.sTimeHHMMSS);
            tvTimeAMPM.setText(Utilities.sTimeAMPM);
            mHandler.postDelayed(updateTimeInfo,500);
        }
    };

    public static void resetStopwatch(){
        Log.v(LOG_TAG,"resetStopwatch");
        mStopwatchHandler.removeCallbacks(updateStopwatch);
        iStopwatch = 0;
        MillisecondTime = 0L ;
        StartTime = 0L ;
        TimeBuff = 0L ;
        UpdateTime = 0L ;
        Seconds = 0 ;
        Minutes = 0 ;
        MilliSeconds = 0 ;
        tvStopwatch.setText(context.getString(R.string.stopwatch_default));
        ibStopwatch.setImageResource(R.drawable.stopwatch_off);
    }
    public static void startStopwatch(){
        Log.v(LOG_TAG,"startStopwatch");
        iStopwatch = 1;
        ibStopwatch.setImageResource(R.drawable.stopwatch_on);
        StartTime = SystemClock.uptimeMillis();
        mStopwatchHandler.postDelayed(updateStopwatch, 0);
    }

    public static void stopStopwatch(){
        Log.v(LOG_TAG,"stopStopwatch");
        iStopwatch = 2;
        TimeBuff += MillisecondTime;
        mStopwatchHandler.removeCallbacks(updateStopwatch);
        ibStopwatch.setImageResource(R.drawable.stopwatch_reset);
    }

    public static Runnable updateStopwatch = new Runnable() {
        public void run() {
            MillisecondTime = SystemClock.uptimeMillis() - StartTime;
            UpdateTime = TimeBuff + MillisecondTime;
            Seconds = (int) (UpdateTime / 1000);
            Minutes = Seconds / 60;
            Seconds = Seconds % 60;
            MilliSeconds = (int) (UpdateTime % 1000);
            tvStopwatch.setText("" + String.format("%02d", Minutes) + ":"
                    + String.format("%02d", Seconds) + ":"
                    + String.format("%03d", MilliSeconds));

            mStopwatchHandler.postDelayed(updateStopwatch, 0);
        }

    };

    public static void updateBatteryInfo(){
        Log.v(LOG_TAG,"updateBatteryInfo");
        Utilities.getBatteryInfo();
        tvBatteryLevel.setText(Utilities.sBatteryLevel);
        switch((int) Utilities.iBatteryLevel/10) {
            case 0:
                ivBatterySwitch.setImageResource(R.drawable.battery_0_10);
                break;
            case 1:
                ivBatterySwitch.setImageResource(R.drawable.battery_10_20);
                break;
            case 2:
                ivBatterySwitch.setImageResource(R.drawable.battery_20_30);
                break;
            case 3:
                ivBatterySwitch.setImageResource(R.drawable.battery_30_40);
                break;
            case 4:
                ivBatterySwitch.setImageResource(R.drawable.battery_40_50);
                break;
            case 5:
                ivBatterySwitch.setImageResource(R.drawable.battery_50_60);
                break;
            case 6:
                ivBatterySwitch.setImageResource(R.drawable.battery_60_70);
                break;
            case 7:
                ivBatterySwitch.setImageResource(R.drawable.battery_70_80);
                break;
            case 8:
                ivBatterySwitch.setImageResource(R.drawable.battery_80_90);
                break;
            case 9:
            case 10:
                ivBatterySwitch.setImageResource(R.drawable.battery_90_100);
                break;
            default:
                ivBatterySwitch.setImageResource(R.drawable.battery_90_100);
                break;
        }


        if(Utilities.bBatteryStatus){
            ivBatteryStatus.setAlpha(fSwitchOn);
        }else{
            ivBatteryStatus.setAlpha(fSwitchOff);
        }
        tvBatteryTemp.setText(Utilities.sBatteryTemp);
        tvBatteryVolt.setText(Utilities.sBatteryVolt);
        tvBatteryCap.setText(Utilities.sBatteryCap);
    }

    public static void updateCompassInfo(){
        //Log.v(LOG_TAG,"updateCompassInfo");
        //Utilities.getCompassInfo();
        //tvCompassDirection.setText(Utilities.sCompassDirection);
        float azimuthInDegrees = 0.0f;
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                float azimuthInRadians = orientation[0]; // orientation contains: azimut, pitch and roll
                azimuthInDegrees = (float)Math.toDegrees(azimuthInRadians);
            }
        }

        if(mOrientation != null){
            azimuthInDegrees = mOrientation[0];
        }

        if((mGravity != null && mGeomagnetic != null) || (mOrientation != null)){
            if (azimuthInDegrees < 0.0f) {
                azimuthInDegrees += 360.0f;
            }
            if(azimuthInDegrees == 0){
                sCompassQuadrant = String.format("%.1f", azimuthInDegrees) + context.getString(R.string.unit_north);
            }else if(azimuthInDegrees > 0 && azimuthInDegrees < 90 ){
                sCompassQuadrant = String.format("%.1f", azimuthInDegrees) + context.getString(R.string.unit_northeast);
            }else if(azimuthInDegrees == 90 ){
                sCompassQuadrant = String.format("%.1f", azimuthInDegrees) + context.getString(R.string.unit_east);
            }else if(azimuthInDegrees > 90 && azimuthInDegrees < 180 ){
                sCompassQuadrant = String.format("%.1f", 180.0f - azimuthInDegrees) + context.getString(R.string.unit_southeast);
            }else if(azimuthInDegrees == 180 ){
                sCompassQuadrant = String.format("%.1f", azimuthInDegrees) + context.getString(R.string.unit_south);
            }else if(azimuthInDegrees > 180 && azimuthInDegrees < 270 ){
                sCompassQuadrant = String.format("%.1f", azimuthInDegrees - 180.0f) + context.getString(R.string.unit_southwest);
            }else if(azimuthInDegrees == 270 ){
                sCompassQuadrant = String.format("%.1f", azimuthInDegrees) + context.getString(R.string.unit_west);
            }else if(azimuthInDegrees > 270 && azimuthInDegrees < 360 ){
                sCompassQuadrant = String.format("%.1f", 360.0f - azimuthInDegrees) + context.getString(R.string.unit_northwest);
            }

            if(azimuthInDegrees >= 0 && azimuthInDegrees <= 22.5f ){
                sCompassAzimuth = String.format("%.1f", azimuthInDegrees) + context.getString(R.string.unit_north);
            }else if(azimuthInDegrees >= 22.5f && azimuthInDegrees <= 67.5f ){
                sCompassAzimuth = String.format("%.1f", azimuthInDegrees) + context.getString(R.string.unit_northeast);
            }else if(azimuthInDegrees >= 67.5f && azimuthInDegrees <= 112.5f ){
                sCompassAzimuth = String.format("%.1f", azimuthInDegrees) + context.getString(R.string.unit_east);
            }else if(azimuthInDegrees >= 112.5f && azimuthInDegrees <= 157.5f ){
                sCompassAzimuth = String.format("%.1f", azimuthInDegrees) + context.getString(R.string.unit_southeast);
            }else if(azimuthInDegrees >= 157.5f && azimuthInDegrees <= 202.5f ){
                sCompassAzimuth = String.format("%.1f", azimuthInDegrees) + context.getString(R.string.unit_south);
            }else if(azimuthInDegrees >= 202.5f && azimuthInDegrees <= 247.5f ){
                sCompassAzimuth = String.format("%.1f", azimuthInDegrees) + context.getString(R.string.unit_southwest);
            }else if(azimuthInDegrees >= 247.5f && azimuthInDegrees <= 292.5f ){
                sCompassAzimuth = String.format("%.1f", azimuthInDegrees) + context.getString(R.string.unit_west);
            }else if(azimuthInDegrees >= 292.5 && azimuthInDegrees <= 337.5f ){
                sCompassAzimuth = String.format("%.1f", azimuthInDegrees) + context.getString(R.string.unit_northwest);
            }else if(azimuthInDegrees >= 337.5f && azimuthInDegrees <= 360.0f ){
                sCompassAzimuth = String.format("%.1f", azimuthInDegrees) + context.getString(R.string.unit_north);
            }

            tvCompassDirection.setText(context.getString(R.string.label_quadrant) + sCompassQuadrant);
            tvCompassAltDirection.setText(context.getString(R.string.label_azimuth) + sCompassAzimuth);
            RotateAnimation ra = new RotateAnimation(
                    mCurrentDegree,
                    -azimuthInDegrees,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);

            ra.setInterpolator(new DecelerateInterpolator());
            ra.setDuration(1000);
            ra.setFillAfter(true);
            ivCompass.startAnimation(ra);
            mCurrentDegree = -azimuthInDegrees;
        }

        if(bCompass){
            ivCompass.setAlpha(fSwitchOn);
        }else{
            ivCompass.setAlpha(fSwitchOff);
            tvCompassDirection.setText(context.getString(R.string.not_available));
        }
    }

    public void updatePedometerInfo(){
        //Log.v(LOG_TAG,"updatePedometerInfo");
        PedometerInfo.setContext(this);
        PedometerInfo.getStepCount();
        tvPedometerCount.setText(PedometerInfo.sPedometerSteps);
        tvPedometerCalorie.setText(PedometerInfo.sPedometerCalories);
        tvPedometerDistance.setText(PedometerInfo.sPedometerDistance);
    }

    public static void updateDataInfo(){
        Log.v(LOG_TAG,"updateDataInfo");
        Utilities.getDataInfo();
        bDataSwitch = Utilities.bDataSwitch;
        if(bDataSwitch){
            ibDataSwitch.setAlpha(fSwitchOn);
        }else{
            ibDataSwitch.setAlpha(fSwitchOff);
        }
    }

    public static void updateDataUsage(){
        Log.v(LOG_TAG,"updateDataUsage");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        tvDataUsage.setText(Formats.bytesFormat(prefs.getLong(context.getString(R.string.pref_key_data), 0)));
    }

    public static void updateWifiInfo(){
        Log.v(LOG_TAG,"updateWifiInfo");
        Utilities.getWifiInfo();
        bWifiSwitch = Utilities.bWifiSwitch;
        if(bWifiSwitch){
            ibWifiSwitch.setAlpha(fSwitchOn);
        }else{
            ibWifiSwitch.setAlpha(fSwitchOff);
        }
    }

    public static void updateWifiUsage(){
        Log.v(LOG_TAG,"updateWifiUsage");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        tvWifiUsage.setText(Formats.bytesFormat(prefs.getLong(context.getString(R.string.pref_key_wifi), 0)));
    }

    public static void updateAirplaneInfo(){
        Log.v(LOG_TAG,"updateAirplaneInfo");
        Utilities.getAirplaneInfo();
        bAirplaneSwitch = Utilities.bAirplaneSwitch;
        if(bAirplaneSwitch){
            ibAirplaneSwitch.setAlpha(fSwitchOn);
        }else{
            ibAirplaneSwitch.setAlpha(fSwitchOff);
        }
    }

    public static void updateGpsInfo(){
        Log.v(LOG_TAG,"updateGpsInfo");
        Utilities.getGpsInfo();
        bGpsSwitch = Utilities.bGpsSwitch;
        if(bGpsSwitch){
            ibGpsSwitch.setAlpha(fSwitchOn);
        }else{
            ibGpsSwitch.setAlpha(fSwitchOff);
        }
    }

    public static void updateLocation(){
        Log.v(LOG_TAG,"updateLocation");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        sLongitude = prefs.getString(context.getString(R.string.pref_key_longitude), "");
        sLatitude = prefs.getString(context.getString(R.string.pref_key_latitude), "");

        if(!sLongitude.isEmpty() && !sLongitude.isEmpty()) {
            tvLongitude.setText(context.getString(R.string.label_longitude) + sLongitude);
            tvLatitude.setText(context.getString(R.string.label_latitude) + sLatitude);

        /*    Double longitude = Double.parseDouble(sLongitude);
            Double latitude = Double.parseDouble(sLatitude);

            Geocoder gcd = new Geocoder(context, Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(longitude,
                        longitude, 1);
                if (addresses.size() > 0 && addresses.get(0).getLocality() != null) {
                    tvCityLoc.setText("City: " + addresses.get(0).getLocality());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        */
        }else{
            tvLongitude.setText(context.getString(R.string.label_longitude) + " " + context.getString(R.string.not_available));
            tvLatitude.setText(context.getString(R.string.label_latitude) + " " + context.getString(R.string.not_available));
        }

    }

    public static void updateBluetoothInfo(){
        Log.v(LOG_TAG,"updateBluetoothInfo");
        Utilities.getBluetoothInfo();
        bBluetoothSwitch = Utilities.bBluetoothSwitch;
        if(bBluetoothSwitch){
            ibBluetoothSwitch.setAlpha(fSwitchOn);
        }else{
            ibBluetoothSwitch.setAlpha(fSwitchOff);
        }
    }

    public static void updateTorchInfo(){
        Log.v(LOG_TAG,"updateTorchInfo");
        bTorchSwitch = Utilities.bTorchSwitch;
        if(bTorchSwitch){
            ibTorchSwitch.setAlpha(fSwitchOn);
        }else{
            ibTorchSwitch.setAlpha(fSwitchOff);
        }
    }

    public static void updateWeatherInfo(){
        Log.v(LOG_TAG,"updateWeatherInfo");
        WeatherInfo.getWeatherInfo();
        if(!WeatherInfo.sWeatherTempNowC.isEmpty()) {
            tvWeatherTempNowC.setText(WeatherInfo.sWeatherTempNowC + context.getString(R.string.unit_celcius));
            tvWeatherTempNowF.setText(WeatherInfo.sWeatherTempNowF + context.getString(R.string.unit_fahrenheit));
        }
        if(!WeatherInfo.sWeatherTempHiC.isEmpty()) {
            tvWeatherTempHiC.setText(WeatherInfo.sWeatherTempHiC + context.getString(R.string.unit_celcius));
            tvWeatherTempHiF.setText(WeatherInfo.sWeatherTempHiF + context.getString(R.string.unit_fahrenheit));
        }
        if(!WeatherInfo.sWeatherTempLoC.isEmpty()) {
            tvWeatherTempLoC.setText(WeatherInfo.sWeatherTempLoC + context.getString(R.string.unit_celcius));
            tvWeatherTempLoF.setText(WeatherInfo.sWeatherTempLoF + context.getString(R.string.unit_fahrenheit));
        }
        if(!WeatherInfo.sWeatherWindKmph.isEmpty()) {
            tvWeatherWindKmph.setText(WeatherInfo.sWeatherWindKmph + context.getString(R.string.unit_kmph));
            tvWeatherWindMph.setText(WeatherInfo.sWeatherWindMph + context.getString(R.string.unit_mph));
        }
        tvWeatherForecast.setText(WeatherInfo.sWeatherForecast);
        tvWeatherWindDir.setText(WeatherInfo.sWeatherWindDir);
        tvSunRiseTime.setText(WeatherInfo.sSunRiseTime);
        tvSunSetTime.setText(WeatherInfo.sSunSetTime);
        tvCity.setText(WeatherInfo.sWeatherCity);

        switch(WeatherInfo.sWeatherIcon) {
            case "01d":
                ivWeatherForecast.setImageResource(R.drawable.w01d);
                break;
            case "02d":
                ivWeatherForecast.setImageResource(R.drawable.w02d);
                break;
            case "03d":
                ivWeatherForecast.setImageResource(R.drawable.w03d);
                break;
            case "04d":
                ivWeatherForecast.setImageResource(R.drawable.w04d);
                break;
            case "09d":
                ivWeatherForecast.setImageResource(R.drawable.w09d);
                break;
            case "10d":
                ivWeatherForecast.setImageResource(R.drawable.w10d);
                break;
            case "11d":
                ivWeatherForecast.setImageResource(R.drawable.w11d);
                break;
            case "13d":
                ivWeatherForecast.setImageResource(R.drawable.w13d);
                break;
            case "50d":
                ivWeatherForecast.setImageResource(R.drawable.w50d);
                break;
            case "01n":
                ivWeatherForecast.setImageResource(R.drawable.w01n);
                break;
            case "02n":
                ivWeatherForecast.setImageResource(R.drawable.w02n);
                break;
            case "03n":
                ivWeatherForecast.setImageResource(R.drawable.w03n);
                break;
            case "04n":
                ivWeatherForecast.setImageResource(R.drawable.w04n);
                break;
            case "09n":
                ivWeatherForecast.setImageResource(R.drawable.w09n);
                break;
            case "10n":
                ivWeatherForecast.setImageResource(R.drawable.w10n);
                break;
            case "11n":
                ivWeatherForecast.setImageResource(R.drawable.w11n);
                break;
            case "13n":
                ivWeatherForecast.setImageResource(R.drawable.w13n);
                break;
            case "50n":
                ivWeatherForecast.setImageResource(R.drawable.w50n);
                break;
            default:
                ivWeatherForecast.setImageResource(android.R.color.transparent);
                break;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return myGesture.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.v(LOG_TAG, "onLongPress");
        Intent i = new Intent(this, MyPreferencesActivity.class);
        startActivity(i);
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        return false;

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch(event.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                //mGravity = event.values;
                mGravity = lowPass( event.values, mGravity );
                updateCompassInfo();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                //mGeomagnetic = event.values;
                mGeomagnetic = lowPass( event.values, mGeomagnetic );
                updateCompassInfo();
                break;
            case Sensor.TYPE_ORIENTATION:
                //mOrientation = event.values;
                mOrientation = lowPass( event.values, mOrientation );
                updateCompassInfo();
                break;
            default:
                break;
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private float[] lowPass( float[] input, float[] output ) {
        if ( output == null ) return input;

        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }


}




