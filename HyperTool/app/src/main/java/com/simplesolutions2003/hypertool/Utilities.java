package com.simplesolutions2003.hypertool;

import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.content.Context.BATTERY_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Suriya on 5/4/2016.
 */
public class Utilities  {

    private final static String LOG_TAG = Utilities.class.getSimpleName();

    public static Context context;
    public static final String LINE_FEED = System.getProperty("line.separator");

    final static double WALKING_FACTOR = 0.57;

    public static String sDeviceBrand;
    public static String sDeviceModel;
    public static String sDeviceOS;
    public static String sCarrierName;
    public static String sCarrierStrength;
    public static int iCarrierLevel;
    public static String sStorageInternalTotal;
    public static String sStorageInternalUsed;
    public static String sStorageInternalFree;
    public static String sStorageInternalPercentage;
    public static String sStorageExternalTotal;
    public static String sStorageExternalUsed;
    public static String sStorageExternalFree;
    public static String sStorageExternalPercentage;
    public static String sCpuType;
    public static String sCpuInfo;
    public static String sCpuSpeed;
    public static String sCpuPercentage;
    public static String sRamTotal;
    public static String sRamUsed;
    public static String sRamFree;
    public static String sRamPercentage;
    public static String sBatteryLevel;
    public static int iBatteryLevel;
    public static boolean bBatteryStatus;
    public static String sBatteryVolt;
    public static String sBatteryTemp;
    public static String sBatteryCap;
    public static String sVolumeLevel;
    public static String sBrightnessLevel;
    public static String sDateDay;
    public static String sDateMonthName;
    public static String sDateYear;
    public static String sDateYear_cc;
    public static String sDateYear_yy;
    public static String sDateDayOfWeek;
    public static String sDateWeekOfYear;
    public static String sDateMonthOfYear;
    public static String sDateDayOfYear;
    public static String sDataUsage;
    public static boolean bDataSwitch;
    public static String sWifiUsage;
    public static boolean bWifiSwitch;
    public static String sTimeHHMMSS;
    public static String sTimeAMPM;
    public static boolean bAirplaneSwitch;
    public static boolean bGpsSwitch;


    public static boolean bBluetoothSwitch;
    public static boolean bTorchSwitch;

    private static Camera cam;
    private static CameraManager cam2;
    private static String cam2Id;

    public static void setContext(Context c){
        context = c;
    }

    public static void getDeviceInfo(){
        Log.v(LOG_TAG,"getDeviceInfo");
        sDeviceBrand = Build.BRAND;
        sDeviceModel = Build.MODEL;
        StringBuilder builder = new StringBuilder();

        Field[] fields = Build.VERSION_CODES.class.getFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            int fieldValue = -1;

            try {
                fieldValue = field.getInt(new Object());
            } catch (IllegalArgumentException e) {
                Log.v(LOG_TAG, e.getMessage());
            } catch (IllegalAccessException e) {
                Log.v(LOG_TAG, e.getMessage());
            } catch (NullPointerException e) {
                Log.v(LOG_TAG, e.getMessage());
            }

            if (fieldValue == Build.VERSION.SDK_INT) {
                builder.append(fieldName).append(" ").append(Build.VERSION.RELEASE).append(LINE_FEED)
                        .append(context.getString(R.string.label_sdk)).append(fieldValue);
            }
        }
        sDeviceOS = builder.toString();
    }

    public static void getCarrierInfo(){
        Log.v(LOG_TAG,"getCarrierInfo");
        int dbm = 0;
        iCarrierLevel = 0;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        sCarrierName = telephonyManager.getNetworkOperatorName();
        if(sCarrierName.isEmpty() || sCarrierName == null){
            sCarrierName = context.getString(R.string.operator_unknown);
        }

        try {
            for (final CellInfo info : telephonyManager.getAllCellInfo()) {
                Log.v(LOG_TAG, "info " + info.toString());
                if (info instanceof CellInfoGsm) {
                    dbm = ((CellInfoGsm) info).getCellSignalStrength().getDbm();
                    iCarrierLevel = ((CellInfoGsm) info).getCellSignalStrength().getLevel();
                } else if (info instanceof CellInfoCdma) {
                    dbm = ((CellInfoCdma) info).getCellSignalStrength().getDbm();
                    iCarrierLevel = ((CellInfoCdma) info).getCellSignalStrength().getLevel();
                } else if (info instanceof CellInfoLte) {
                    dbm = ((CellInfoLte) info).getCellSignalStrength().getDbm();
                    iCarrierLevel = ((CellInfoLte) info).getCellSignalStrength().getLevel();
                } else {
                    Log.v(LOG_TAG, "Unknown type of cell signal! " + info.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Log.v(LOG_TAG, "dbm :" + dbm + " level :" + iCarrierLevel);
        if(dbm <= 0 ) {
            sCarrierStrength = Integer.toString(dbm) + context.getString(R.string.unit_dbm);
        }else{
            sCarrierStrength = "";
            iCarrierLevel = 0;
        }
    }

    public static void getCpuInfo(){
        Log.v(LOG_TAG,"getCpuInfo");
        getCpuType();
        getCpuSpeed();
        getCpuPercentage();
    }

    public static void getCpuType(){
        Log.v(LOG_TAG,"getCpuType");
        Process proc;
        try {
            proc = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            try{
                String aLine = "";
                InputStream in = proc.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strArray[] = new String[2];
                boolean bModelName = false;
                boolean bHardware = false;
                while ((aLine = br.readLine()) != null) {
                    //Log.v(LOG_TAG,"getCPU >> " + aLine);
                    if(aLine.contains("Processor") && !bModelName){
                        strArray = aLine.split(":", 2);
                        sCpuType = strArray[1];
                        bModelName = true;
                    }
                    if(aLine.contains("Hardware") && !bHardware){
                        strArray = aLine.split(":", 2);
                        sCpuInfo = strArray[1];
                        bHardware = true;
                    }

                }
                if (br != null) {
                    br.close();
                }
                in.close();
            } catch(IOException ex){
                Log.v(LOG_TAG, ex.getMessage());
            }
        }catch (IOException e){
            Log.v(LOG_TAG, e.getMessage());
        }

    }

    public static void getCpuSpeed(){
        Log.v(LOG_TAG,"getCpuSpeed");
        Process proc;
        String result = "";
        try {
            proc = Runtime.getRuntime().exec("cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
            try{
                InputStream in = proc.getInputStream();
                byte[] re = new byte[1024];
                while(in.read(re) != -1){
                    result = result + new String(re);
                }
                in.close();
                try {
                    int cpuSpeedHz = Integer.parseInt(result.trim());
                    sCpuSpeed = Formats.onePointTwoDoubleForm(UnitConversions.convertHzToMhz(cpuSpeedHz))+ context.getString(R.string.unit_mhz);
                }catch (NumberFormatException e){
                    Log.v(LOG_TAG, e.getMessage());
                }

            } catch(IOException ex){
                Log.v(LOG_TAG, ex.getMessage());
            }
        }catch (IOException e){
            Log.v(LOG_TAG, e.getMessage());
        }

    }

    public static void getCpuPercentage() {
        Log.v(LOG_TAG,"getCpuPercentage");
        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String load = reader.readLine();
            String[] toks = load.split(" ");

            long idle1 = Long.parseLong(toks[5]);
            long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            try {
                Thread.sleep(360);
            } catch (Exception e) {}

            reader.seek(0);
            load = reader.readLine();
            reader.close();

            toks = load.split(" ");

            long idle2 = Long.parseLong(toks[5]);
            long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            sCpuPercentage = Formats.oneDigitIntForm((float) 100.0 * (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1))) + context.getString(R.string.unit_percentage);

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
    public static void getStorageInternalInfo(){
        Log.v(LOG_TAG,"getStorageInternalInfo");
        //StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long Total = ((long) statFs.getBlockCountLong() * (long) statFs.getBlockSizeLong());
        long Free =  ((long) statFs.getAvailableBlocksLong() * (long) statFs.getBlockSizeLong());
        long Used = Total - Free;
        sStorageInternalTotal = Formats.bytesFormat(Total);
        sStorageInternalUsed = Formats.bytesFormat(Used);
        sStorageInternalFree = Formats.bytesFormat(Free);
        sStorageInternalPercentage = Formats.oneDigitIntForm(100 * Used / Total) + context.getString(R.string.unit_percentage);
    }

    public static void getStorageExternalInfo(){
        Log.v(LOG_TAG,"getStorageExternalInfo");
        File externalSdCard = null;
        String externalSdCardPath = getStorageDirectories();
        if(externalSdCardPath != null) {
            externalSdCard = new File(externalSdCardPath);
        }
        if(externalSdCard != null) {
            StatFs statFs = new StatFs(externalSdCard.getPath());
            long Total = ((long) statFs.getBlockCountLong() * (long) statFs.getBlockSizeLong());
            long Free = ((long) statFs.getAvailableBlocksLong() * (long) statFs.getBlockSizeLong());
            long Used = Total - Free;
            sStorageExternalTotal = Formats.bytesFormat(Total);
            if(Total > 0) {
                sStorageExternalUsed = Formats.bytesFormat(Used);
                sStorageExternalFree = Formats.bytesFormat(Free);
                sStorageExternalPercentage = Formats.oneDigitIntForm(100 * Used / Total) + context.getString(R.string.unit_percentage);
            }else {
                sStorageExternalTotal = context.getString(R.string.not_available);
            }
        }else{
            sStorageExternalTotal = context.getString(R.string.not_available);
        }

    }

    public static void getRamInfo(){
        Log.v(LOG_TAG,"getRamInfo");
        ActivityManager actManager = (ActivityManager) context.getSystemService(MainActivity.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        long Total = memInfo.totalMem;
        long Free = memInfo.availMem;
        long Used = Total - Free;
        sRamTotal = Formats.bytesFormat(Total);
        sRamUsed = Formats.bytesFormat(Used);
        sRamFree = Formats.bytesFormat(Free);
        sRamPercentage = Formats.oneDigitIntForm(100 * Used / Total) + context.getString(R.string.unit_percentage);
        
    }

    public static void getDateInfo(){
        Log.v(LOG_TAG,"getDateInfo");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");

        sDateDay = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
        sDateMonthName = month_date.format(calendar.getTime());
        sDateYear = Integer.toString(calendar.get(Calendar.YEAR));
        sDateYear_cc = Integer.toString(calendar.get(Calendar.YEAR)).substring(0,2);
        sDateYear_yy = Integer.toString(calendar.get(Calendar.YEAR)).substring(2,4);
        sDateWeekOfYear = context.getString(R.string.label_week) + Integer.toString(calendar.get(Calendar.WEEK_OF_YEAR));
        sDateDayOfYear = context.getString(R.string.label_day) + Integer.toString(calendar.get(Calendar.DAY_OF_YEAR));
        sDateMonthOfYear = context.getString(R.string.label_month) + Integer.toString(calendar.get(Calendar.MONTH)+1);
        sDateDayOfWeek = UnitConversions.getWeekDay(calendar.get(Calendar.DAY_OF_WEEK),context);

        
    }

    public static void getVolumeInfo(){
        Log.v(LOG_TAG,"getVolumeInfo");
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int currVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        int currVolumeLevel = (int) ((currVolume * 100.0f) /maxVolume);
        sVolumeLevel = Formats.oneDigitIntForm(currVolumeLevel)+ context.getString(R.string.unit_percentage);
    }

    public static void getBrightnessInfo(){
        Log.v(LOG_TAG,"getBrightnessInfo");
        try {
            float currBrightness = android.provider.Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            int maxBrightness = 255;
            int currBrightnessLevel = (int) ((currBrightness * 100.0f) /maxBrightness);
            sBrightnessLevel = Formats.oneDigitIntForm(currBrightnessLevel)+ context.getString(R.string.unit_percentage);
        }catch (Settings.SettingNotFoundException e){
            Log.v(LOG_TAG, e.getMessage());
        }
    }

    public static void getTimeInfo() {
        //Log.v(LOG_TAG,"getTimeInfo");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat ampmFormat = new SimpleDateFormat("aaa z");

        sTimeHHMMSS = timeFormat.format(calendar.getTime());
        sTimeAMPM = ampmFormat.format(calendar.getTime());

    }

    public static void getBatteryInfo() {
        Log.v(LOG_TAG,"getBatteryInfo");
        Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int currBattery = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int maxBattery = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        iBatteryLevel = (int) ((currBattery * 100.0f) /maxBattery);

        float currBatteryVolt = batteryIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)/1000.0f;
        float currBatteryTemp = batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)/10.0f;

        sBatteryLevel = Formats.oneDigitIntForm(iBatteryLevel) + context.getString(R.string.unit_percentage);
        sBatteryVolt = Float.toString(currBatteryVolt) + context.getString(R.string.unit_volts);
        sBatteryTemp = Float.toString(currBatteryTemp) + context.getString(R.string.unit_celcius);

        int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        bBatteryStatus = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        int plugged = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean pluggedAC = (plugged == BatteryManager.BATTERY_PLUGGED_AC);
        boolean pluggedUSB = (plugged == BatteryManager.BATTERY_PLUGGED_USB);

        bBatteryStatus = pluggedAC || pluggedUSB;

        sBatteryCap = "";

        Object mPowerProfile_ = null;
        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";
        try {
            mPowerProfile_ = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class).newInstance(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            double batteryCapacity = (double) Class
                    .forName(POWER_PROFILE_CLASS)
                    .getMethod("getAveragePower", java.lang.String.class)
                    .invoke(mPowerProfile_, "battery.capacity");
            sBatteryCap = Formats.oneDigitIntForm(batteryCapacity) + context.getString(R.string.unit_milliAmps);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getCompassInfo() {


    }

    public static void getDataInfo() {
        Log.v(LOG_TAG,"getDataInfo");
        bDataSwitch = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            bDataSwitch = (Boolean)method.invoke(cm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*long mobileTx = TrafficStats.getMobileTxBytes();
        long mobileRx = TrafficStats.getMobileRxBytes();
        sDataUsage = Formats.bytesFormat(mobileTx + mobileRx);
        */
    }

    public static void getWifiInfo() {
        Log.v(LOG_TAG,"getWifiInfo");
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        bWifiSwitch = wifiManager.isWifiEnabled();
        /*
        long mobileTx = TrafficStats.getMobileTxBytes();
        long mobileRx = TrafficStats.getMobileRxBytes();
        long wifiTx = TrafficStats.getTotalTxBytes() - mobileTx;
        long wifiRx = TrafficStats.getTotalRxBytes() - mobileRx;
        sWifiUsage = Formats.bytesFormat(wifiTx + wifiRx);
        */
    }

    public static void dataOnOff(){
        Log.v(LOG_TAG,"dataOnOff");
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setComponent(new ComponentName("com.android.settings",
                "com.android.settings.Settings$DataUsageSummaryActivity"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ((Activity) context).startActivityForResult(intent,0);
    }

    public static void gpsOnOff(){
        Log.v(LOG_TAG,"gpsOnOff");
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        ((Activity) context).startActivityForResult(intent,0);
    }

    public static void soundOnOff(){
        Log.v(LOG_TAG,"soundOnOff");
        Intent intent = new Intent(Settings.ACTION_SOUND_SETTINGS);
        ((Activity) context).startActivityForResult(intent,0);
    }


    public static void displayOnOff(){
        Log.v(LOG_TAG,"displayOnOff");
        Intent intent = new Intent(Settings.ACTION_DISPLAY_SETTINGS);
        ((Activity) context).startActivityForResult(intent,0);
    }


    public static void airplaneOnOff(){
        Log.v(LOG_TAG,"airplaneOnOff");
        Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
        ((Activity) context).startActivityForResult(intent,0);
    }

    public static void wifiOn(){
        Log.v(LOG_TAG,"wifiOn");
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
    }

    public static void wifiOff(){
        Log.v(LOG_TAG,"wifiOff");
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);
    }

    public static void bluetoothOn(){
        Log.v(LOG_TAG,"bluetoothOn");
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.enable();
    }

    public static void bluetoothOff(){
        Log.v(LOG_TAG,"bluetoothOff");
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.disable();
    }

    public static void getAirplaneInfo() {
        Log.v(LOG_TAG,"getAirplaneInfo");
        try{
            bAirplaneSwitch = android.provider.Settings.Global.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON) != 0;
        }catch (Settings.SettingNotFoundException e){
            Log.v(LOG_TAG, e.getMessage());
        }
    }

    public static void getGpsInfo() {
        Log.v(LOG_TAG,"getGpsInfo");
        final LocationManager locationManager = (LocationManager) context.getSystemService( Context.LOCATION_SERVICE );

        if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            bGpsSwitch = false;
        }else{
            bGpsSwitch = true;
        }

    }

    public static void getBluetoothInfo() {
        Log.v(LOG_TAG,"getBluetoothInfo");
        try{
            bBluetoothSwitch = android.provider.Settings.Global.getInt(context.getContentResolver(), Settings.Global.BLUETOOTH_ON) != 0;
        }catch (Settings.SettingNotFoundException e){
            Log.v(LOG_TAG, e.getMessage());
        }

    }

    public static void torchOn() {
        Log.v(LOG_TAG,"torchOn");
        try {
            if (context.getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_CAMERA_FLASH)) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    cam2 = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
                    try {
                        cam2.setTorchMode(cam2.getCameraIdList()[0], true);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }else {
                    cam = Camera.open();
                    Camera.Parameters p = cam.getParameters();
                    Log.v(LOG_TAG, "getParameters " + p.toString());
                    List<String> supportedFlashModes = p.getSupportedFlashModes();
                    Log.v(LOG_TAG, "getSupportedFlashModes " + supportedFlashModes.toString());
                    if (supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    } else if (supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_ON)) {
                        p.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                    }
                    cam.autoFocus(null);
                    cam.setParameters(p);
                    cam.setPreviewTexture(new SurfaceTexture(0));
                    cam.startPreview();
                }
                bTorchSwitch = true;
            }
        } catch (Exception e) {
            Log.v(LOG_TAG,e.getMessage());
        }
    }

    public static void torchOff() {
        Log.v(LOG_TAG,"torchOff");
        try {
            if (context.getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_CAMERA_FLASH)) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    cam2 = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
                    try {
                        cam2.setTorchMode(cam2.getCameraIdList()[0], false);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }else {
                    cam.stopPreview();
                    cam.release();
                    cam = null;
                }
                bTorchSwitch = false;
            }
        } catch (Exception e) {
            Log.v(LOG_TAG,e.getMessage());
        }
    }


    public static String getStorageDirectories() {
        Log.v(LOG_TAG,"getStorageDirectories");
        String [] storageDirectories = null;
        String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            List<String> results = new ArrayList<String>();
            try {
                File[] externalDirs = context.getExternalFilesDirs(null);
                for (File file : externalDirs) {
                    String path = file.getPath().split("/Android")[0];
                    if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Environment.isExternalStorageRemovable(file))
                            || rawSecondaryStoragesStr != null && rawSecondaryStoragesStr.contains(path)) {
                        results.add(path);
                    }
                }
                storageDirectories = results.toArray(new String[0]);
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }else{
            final Set<String> rv = new HashSet<String>();

            if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
                final String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
                Collections.addAll(rv, rawSecondaryStorages);
            }
            storageDirectories = rv.toArray(new String[rv.size()]);
        }
        if(storageDirectories != null){
            if(storageDirectories.length > 0){
                Log.v(LOG_TAG, "storageDirectories - " + storageDirectories[0]);
                return storageDirectories[0];
            }
        }
        return null;


    }

    public static String CalorieBurnedCalculator(float stepsCount, float height, float weight) {
        // weight kg and height cm

        double CaloriesBurnedPerMile;
        double strip;
        double stepCountMile; // step/mile
        double conversationFactor;
        double CaloriesBurned;

        CaloriesBurnedPerMile = WALKING_FACTOR * (weight * 2.2);
        strip = height * 0.415;
        stepCountMile = 160934.4 / strip;
        conversationFactor = CaloriesBurnedPerMile / stepCountMile;
        CaloriesBurned = stepsCount * conversationFactor; //cal

        return Formats.oneDigitIntForm(CaloriesBurned);
    }

    public static Float DistanceCalculator(float stepsCount, float height) {
        //heigh in cm

        float strip;
        float distance;

        strip = height * 0.415f;
        distance =  (stepsCount * strip) / 100000.0f; //km

        return distance;

    }

    public static String DistanceKmCalculator(float stepsCount, float height) {
        return Formats.onePointTwoDoubleForm(DistanceCalculator(stepsCount,height));
    }

    public static String DistanceMiCalculator(float stepsCount, float height) {
        return Formats.onePointTwoDoubleForm(UnitConversions.convertKToM(DistanceCalculator(stepsCount,height)));
    }

}

