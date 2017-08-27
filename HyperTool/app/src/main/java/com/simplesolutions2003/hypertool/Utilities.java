package com.simplesolutions2003.hypertool;

import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
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
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.content.Context.BATTERY_SERVICE;

/**
 * Created by Suriya on 5/4/2016.
 */
public class Utilities  {

    private final static String LOG_TAG = Utilities.class.getSimpleName();
    private final static String ENTER_CHAR ="\000A";
    public static Context context;
    public static String sDeviceBrand;
    public static String sDeviceModel;
    public static String sDeviceOS;
    public static String sCarrierName;
    public static String sCarrierStrength;
    public static String sStorageInternalTotal;
    public static String sStorageInternalUsed;
    public static String sStorageInternalFree;
    public static String sStorageExternalTotal;
    public static String sStorageExternalUsed;
    public static String sStorageExternalFree;
    public static String sCpuType;
    public static String sCpuInfo;
    public static String sCpuSpeed;
    public static String sRamTotal;
    public static String sRamUsed;
    public static String sRamFree;
    public static String sBatteryLevel;
    public static boolean bBatteryStatus;
    public static String sBatteryVolt;
    public static String sBatteryTemp;
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
    public static String sSunRiseTime;
    public static String sSunSetTime;
    public static String sCompassDirection;
    public static String sDataUsage;
    public static boolean bDataSwitch;
    public static String sWifiUsage;
    public static boolean bWifiSwitch;
    public static String sTimeHHMMSS;
    public static String sTimeAMPM;
    public static String sMoonPhase;
    public static String sMoonRiseTime;
    public static String sMoonSetTime;
    public static String sPedometerCount;
    public static boolean bPedometerSwitch;
    public static boolean bAirplaneSwitch;
    public static boolean bGpsSwitch;
    public static String sWeatherTempNowC;
    public static String sWeatherTempNowF;
    public static String sWeatherTempHiC;
    public static String sWeatherTempHiF;
    public static String sWeatherTempLoC;
    public static String sWeatherTempLoF;
    public static String sWeatherWindKmph;
    public static String sWeatherWindMph;
    public static String sWeatherForecast;
    public static boolean bBluetoothSwitch;
    public static boolean bTorchSwitch;

    private static Camera cam;

    public static void setContext(Context c){
        context = c;
    }

    public static void getDeviceInfo(){
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
                builder.append(fieldName).append(" ").append(Build.VERSION.RELEASE).append(" sdk ").append(fieldValue);
            }
        }
        sDeviceOS = builder.toString();
    }


    public static void getCarrierInfo(){
        int dbm = 0;
        int level = 0;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        sCarrierName = telephonyManager.getNetworkOperatorName();
        try {
            for (final CellInfo info : telephonyManager.getAllCellInfo()) {
                if (info instanceof CellInfoGsm) {
                    dbm = ((CellInfoGsm) info).getCellSignalStrength().getDbm();
                    level = ((CellInfoGsm) info).getCellSignalStrength().getLevel();
                } else if (info instanceof CellInfoCdma) {
                    dbm = ((CellInfoCdma) info).getCellSignalStrength().getDbm();
                    level = ((CellInfoCdma) info).getCellSignalStrength().getLevel();
                } else if (info instanceof CellInfoLte) {
                    dbm = ((CellInfoLte) info).getCellSignalStrength().getDbm();
                    level = ((CellInfoLte) info).getCellSignalStrength().getLevel();
                } else {
                    Log.v(LOG_TAG, "Unknown type of cell signal! " + info.toString());
                }
            }
        } catch (Exception e) {
            Log.v(LOG_TAG, "unknown error :"+e.getMessage());
        }
        sCarrierStrength = Integer.toString(dbm) + " dBm : " + Integer.toString(level);
    }

    public static void getCpuInfo(){
        getCpuType();
        getCpuSpeed();
    }

    public static void getCpuType(){
        Process proc;
        try {
            proc = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            try{
                String aLine = new String("");
                InputStream in = proc.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strArray[] = new String[2];
                boolean bModdelName = false;
                boolean bHardware = false;
                while ((aLine = br.readLine()) != null) {

                    if(aLine.contains("model name") && !bModdelName){
                        strArray = aLine.split(":", 2);
                        sCpuType = strArray[1];
                        bModdelName = true;
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
        Process proc;
        String result = new String("");
        DecimalFormat df = new DecimalFormat("0.00");
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
                    float cpuSpeedMhz = (float) cpuSpeedHz/1000000.0f;
                    sCpuSpeed = df.format(cpuSpeedMhz)+" Mhz";
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

    public static void getStorageInternalInfo(){
        StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
        long Total = ((long) statFs.getBlockCountLong() * (long) statFs.getBlockSizeLong());
        long Free =  ((long) statFs.getAvailableBlocksLong() * (long) statFs.getBlockSizeLong());
        long Used = Total - Free;
        sStorageInternalTotal = bytesFormat(Total);
        sStorageInternalUsed = bytesFormat(Used);
        sStorageInternalFree = bytesFormat(Free);
        
    }

    public static void getStorageExternalInfo(){
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long Total = ((long) statFs.getBlockCountLong() * (long) statFs.getBlockSizeLong());
        long Free =  ((long) statFs.getAvailableBlocksLong() * (long) statFs.getBlockSizeLong());
        long Used = Total - Free;
        sStorageExternalTotal = bytesFormat(Total);
        sStorageExternalUsed = bytesFormat(Used);
        sStorageExternalFree = bytesFormat(Free);
    }

    public static void getRamInfo(){
        ActivityManager actManager = (ActivityManager) context.getSystemService(MainActivity.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        long Total = memInfo.totalMem;
        long Free = memInfo.availMem;
        long Used = Total - Free;
        sRamTotal = bytesFormat(Total);
        sRamUsed = bytesFormat(Used);
        sRamFree = bytesFormat(Free);
        
    }

    public static void getDateInfo(){

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");

        sDateDay = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
        sDateMonthName = month_date.format(calendar.getTime());
        sDateYear = Integer.toString(calendar.get(Calendar.YEAR));
        sDateYear_cc = Integer.toString(calendar.get(Calendar.YEAR)).substring(0,2);
        sDateYear_yy = Integer.toString(calendar.get(Calendar.YEAR)).substring(2,4);
        sDateWeekOfYear = "W:"+Integer.toString(calendar.get(Calendar.WEEK_OF_YEAR));
        sDateDayOfYear = "D:"+Integer.toString(calendar.get(Calendar.DAY_OF_YEAR));
        sDateMonthOfYear = "M:"+Integer.toString(calendar.get(Calendar.MONTH)+1);

        int day = calendar.get(Calendar.DAY_OF_WEEK);

        switch (day) {
            case Calendar.SUNDAY:
                sDateDayOfWeek = "Sun";
                break;
            case Calendar.MONDAY:
                sDateDayOfWeek = "Mon";
                break;
            case Calendar.TUESDAY:
                sDateDayOfWeek = "Tue";
                break;
            case Calendar.WEDNESDAY:
                sDateDayOfWeek = "Wed";
                break;
            case Calendar.THURSDAY:
                sDateDayOfWeek = "Thu";
                break;
            case Calendar.FRIDAY:
                sDateDayOfWeek = "Fri";
                break;
            case Calendar.SATURDAY:
                sDateDayOfWeek = "Sat";
                break;
        }
        
    }

    public static void getVolumeInfo(){
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int currVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        int currVolumeLevel = (int) ((currVolume * 100.0f) /maxVolume);
        sVolumeLevel = oneDigitIntForm(currVolumeLevel)+"%";
    }

    public static void getBrightnessInfo(){
        try {
            float currBrightness = android.provider.Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            int maxBrightness = 255;
            int currBrightnessLevel = (int) ((currBrightness * 100.0f) /maxBrightness);
            sBrightnessLevel = oneDigitIntForm(currBrightnessLevel)+"%";
        }catch (Settings.SettingNotFoundException e){
            Log.v(LOG_TAG, e.getMessage());
        }
    }

    public static void getTimeInfo() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat ampmFormat = new SimpleDateFormat("aaa z");

        sTimeHHMMSS = timeFormat.format(calendar.getTime());
        sTimeAMPM = ampmFormat.format(calendar.getTime());

    }

    public static void getBatteryInfo() {

        Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int currBattery = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int maxBattery = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int currBatteryLevel = (int) ((currBattery * 100.0f) /maxBattery);

        float currBatteryVolt = batteryIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)/1000.0f;
        float currBatteryTemp = batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)/10.0f;

        sBatteryLevel = oneDigitIntForm(currBatteryLevel) + "%";
        sBatteryVolt = Float.toString(currBatteryVolt) +"v";
        sBatteryTemp = Float.toString(currBatteryTemp) +"c";

        int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        bBatteryStatus = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        int plugged = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean pluggedAC = (plugged == BatteryManager.BATTERY_PLUGGED_AC);
        boolean pluggedUSB = (plugged == BatteryManager.BATTERY_PLUGGED_USB);

        bBatteryStatus = pluggedAC || pluggedUSB;
    }

    public static void getCompassInfo() {


    }

    public static void getDataInfo() {
        bDataSwitch = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            bDataSwitch = (Boolean)method.invoke(cm);
        } catch (Exception e) {
            Log.v(LOG_TAG, e.getMessage());
        }
        long mobileTx = TrafficStats.getMobileTxBytes();
        long mobileRx = TrafficStats.getMobileRxBytes();
        sDataUsage = bytesFormat(mobileTx + mobileRx);
    }

    public static void getWifiInfo() {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        bWifiSwitch = wifiManager.isWifiEnabled();
        long mobileTx = TrafficStats.getMobileTxBytes();
        long mobileRx = TrafficStats.getMobileRxBytes();
        long wifiTx = TrafficStats.getTotalTxBytes() - mobileTx;
        long wifiRx = TrafficStats.getTotalRxBytes() - mobileRx;
        sWifiUsage = bytesFormat(wifiTx + wifiRx);
    }

    public static void dataOnOff(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setComponent(new ComponentName("com.android.settings",
                "com.android.settings.Settings$DataUsageSummaryActivity"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ((Activity) context).startActivityForResult(intent,0);
    }

    public static void gpsOnOff(){
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        ((Activity) context).startActivityForResult(intent,0);
    }


    public static void soundOnOff(){
        Intent intent = new Intent(Settings.ACTION_SOUND_SETTINGS);
        ((Activity) context).startActivityForResult(intent,0);
    }


    public static void displayOnOff(){
        Intent intent = new Intent(Settings.ACTION_DISPLAY_SETTINGS);
        ((Activity) context).startActivityForResult(intent,0);
    }


    public static void airplaneOnOff(){
        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        ((Activity) context).startActivityForResult(intent,0);
    }

    public static void wifiOn(){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
    }

    public static void wifiOff(){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);
    }

    public static void bluetoothOn(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.enable();
    }

    public static void bluetoothOff(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.disable();
    }

    public static void getPedometerInfo() {


    }

    public static void getAirplaneInfo() {
        try{
            bAirplaneSwitch = android.provider.Settings.Global.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON) != 0;
        }catch (Settings.SettingNotFoundException e){
            Log.v(LOG_TAG, e.getMessage());
        }
    }

    public static void getGpsInfo() {
        final LocationManager locationManager = (LocationManager) context.getSystemService( Context.LOCATION_SERVICE );

        if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            bGpsSwitch = false;
        }else{
            bGpsSwitch = true;
        }

    }

    public static void getBluetoothInfo() {
        try{
            bBluetoothSwitch = android.provider.Settings.Global.getInt(context.getContentResolver(), Settings.Global.BLUETOOTH_ON) != 0;
        }catch (Settings.SettingNotFoundException e){
            Log.v(LOG_TAG, e.getMessage());
        }

    }

    public static void getTorchInfo() {
        //camera is exclusive, so we cannot open and check if its ON
    }

    public static void torchOn() {

        try {
            if (context.getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_CAMERA_FLASH)) {
                cam = Camera.open();
                Camera.Parameters p = cam.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                cam.setParameters(p);
                cam.startPreview();
                bTorchSwitch = true;
            }
        } catch (Exception e) {
            Log.v(LOG_TAG,e.getMessage());
        }
    }

    public static void torchOff() {
        try {
            if (context.getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_CAMERA_FLASH)) {
                cam.stopPreview();
                cam.release();
                cam = null;
                bTorchSwitch = false;
            }
        } catch (Exception e) {
            Log.v(LOG_TAG,e.getMessage());
        }
    }

    public static void getSunInfo() {


    }

    public static void getMoonInfo() {


    }

    public static void getWeatherInfo() {


    }


    public static String onePointTwoDoubleForm(double d){
        return new DecimalFormat("#.##").format(d);
    }

    public static String oneDigitDoubleForm(double d){
        return new DecimalFormat("#").format(d);
    }

    public static String oneDigitIntForm(int i){
        return new DecimalFormat("#").format(i);
    }

    public static String twoDigitIntForm(int i){
        return new DecimalFormat("##").format(i);
    }

    public static String bytesFormat(long size){
        long Kb = 1 * 1024;
        long Mb = Kb * 1024;
        long Gb = Mb * 1024;

        if(size < Kb){
            return onePointTwoDoubleForm(size) + " B";
        }else if(size >= Kb/10 && size < Mb/10){
            return onePointTwoDoubleForm((double)size/Kb) + " KB";
        }else if(size >= Mb/10 && size < Gb/10){
            return onePointTwoDoubleForm((double)size/Mb) + " MB";
        }else if(size >= Gb/10){
            return onePointTwoDoubleForm((double)size/Gb) + " GB";
        }

        return null;
    }

 
}
