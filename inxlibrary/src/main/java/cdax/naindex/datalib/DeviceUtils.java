package cdax.naindex.datalib;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Proxy;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.ContactsContract.Groups;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.LinkedBlockingQueue;

import cdax.naindex.GoogleLocation;
import cdax.naindex.LocationInformation;
import cdax.naindex.outer.AppManager;


public class DeviceUtils extends DeviceUtils1 {

    private static Context context = AppManager.getContext();

    DeviceUtils() {
    }


    public static JSONObject getLocation(Context context) throws JSONException {
        JSONObject locationData = new JSONObject();

        try {
            GoogleLocation location = LocationInformation.getLocalLocation(context);
            String latitude = "";
            String longitude = "";
            if (location != null) {
                latitude = location.getGoogleLatitude();
                longitude = location.getGoogleLongitude();
            }
            String latlng = latitude + "," + longitude;
            locationData.put("gps_address_city", latlng);
            locationData.put("gps_address_province", latlng);
            locationData.put("gps_address_street", latlng);
            JSONObject gpsJSONject = new JSONObject();
            gpsJSONject.put("latitude", latitude);
            gpsJSONject.put("longitude", longitude);
            locationData.put("gps", gpsJSONject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return locationData;
    }

    public static JSONObject getHardWareInfo() throws JSONException {


        JSONObject hardWareData = new JSONObject();
        try {
            hardWareData.put("device_name", DataUtil.filterText(getDriverBrand()));
            hardWareData.put("brand", DataUtil.filterText(getDriverBrand()));
            hardWareData.put("board", DataUtil.filterText(getBoard()));
            hardWareData.put("sdk_version", getDriverSDKVersion());
            hardWareData.put("model", DataUtil.filterText(getDriverModel()));
            hardWareData.put("release", DataUtil.filterText(getDriverOsVersion()));
            hardWareData.put("serial_number", DataUtil.filterText(getSerialNumber()));
            hardWareData.put("physical_size", DataUtil.filterText(getScreenPhysicalSize(context)));
            hardWareData.put("production_date", getDriverTime());
            hardWareData.put("device_height", getDisplayMetrics(context).heightPixels);
            hardWareData.put("device_width", getDisplayMetrics(context).widthPixels);
            hardWareData.put("cpu_num", getCpuNum() + "");
            hardWareData.put("imei1", DataUtil.filterText(getIMEI1(context)));
            hardWareData.put("imei2", DataUtil.filterText(getIMEI2(context)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return hardWareData;
    }

    public static int getCpuNum() {
        try {
            return Runtime.getRuntime().availableProcessors();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static long getDriverTime() {
        try {
            long l = Build.TIME;
            return l;
        } catch (Exception var3) {
            return -1l;
        }
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }


    public static JSONObject getGeneralData() throws JSONException {
        JSONObject generalData = new JSONObject();
        try {
            generalData.put("gaid", DataUtil.filterText(getGAID()));
            generalData.put("and_id", DataUtil.filterText(getAndroidId(context)));
            generalData.put("phone_type", DataUtil.filterText(String.valueOf(getPhoneType())));
            generalData.put("mac", DataUtil.filterText(getMacAddress()));
            generalData.put("locale_iso_3_language", DataUtil.filterText(getISO3Language(context)));
            generalData.put("locale_display_language", DataUtil.filterText(getLocaleDisplayLanguage()));
            generalData.put("locale_iso_3_country", DataUtil.filterText(getISO3Country(context)));
            generalData.put("imei", DataUtil.filterText(getDeviceImeIValue(context)));
            generalData.put("phone_number", DataUtil.filterText(getCurrentPhoneNum()));
            generalData.put("network_operator_name", DataUtil.filterText(getNetWorkOperatorName()));
            generalData.put("network_type", DataUtil.filterText(getNetworkState()));
            generalData.put("time_zone_id", DataUtil.filterText(getCurrentTimeZone()));
            generalData.put("language", DataUtil.filterText(getLanguage()));
            generalData.put("is_using_proxy_port", isWifiProxy());
            generalData.put("is_using_vpn", isUsingVPN());
            generalData.put("is_usb_debug", isUsbDebug());
            generalData.put("sensor_list", getSensorList());
            generalData.put("elapsedRealtime", getElapsedRealtime());
            generalData.put("currentSystemTime", System.currentTimeMillis());
            generalData.put("uptimeMillis", getUpdateMills());
        } catch (Exception e) {
            e.printStackTrace();

        }
        return generalData;
    }

    public static JSONObject getOtherData() throws JSONException {
        JSONObject otherData = new JSONObject();
        try {
            otherData.put("root_jailbreak", isRoot() ? "1" : "0");
            otherData.put("last_boot_time", bootTime() + "");
            otherData.put("keyboard", "");
            otherData.put("simulator", isEmulator() ? "1" : "0");
            otherData.put("dbm", DataUtil.filterText(getMobileDbm()));
            otherData.put("total_boot_time", getTotalBootTime());
            otherData.put("total_boot_time_wake", getTotalBootTimeWake());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return otherData;
    }


    public static JSONObject getNetworkData() {
        JSONObject network = new JSONObject();
        JSONObject currentNetwork = new JSONObject();
        JSONArray configNetwork = new JSONArray();

        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null && wifiManager.isWifiEnabled()) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                currentNetwork.put("bssid", wifiInfo.getBSSID());
                currentNetwork.put("ssid", wifiInfo.getSSID());
                currentNetwork.put("mac", wifiInfo.getMacAddress());
                currentNetwork.put("name", getWifiName());
                network.put("current_wifi", currentNetwork);
                network.put("IP", getWifiIP());
                List<ScanResult> configs = wifiManager.getScanResults();
                Iterator var6 = configs.iterator();

                while (var6.hasNext()) {
                    ScanResult scanResult = (ScanResult) var6.next();
                    JSONObject config = new JSONObject();
                    config.put("bssid", scanResult.BSSID);
                    config.put("ssid", scanResult.SSID);
                    config.put("mac", scanResult.BSSID);
                    config.put("name", scanResult.SSID);
                    configNetwork.put(config);
                }

                network.put("configured_wifi", configNetwork);
                network.put("wifi_count", getScanResults(context));

            }
        } catch (Exception var9) {
        }

        return network;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static JSONObject getBatteryData() throws JSONException {
        JSONObject jSONObject = new JSONObject();
        BatteryManager manager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
        if (manager != null) {
            int dianliang = manager.getIntProperty(4);
            jSONObject.put("battery_pct", "" + dianliang);
        }

        Intent intent = context.registerReceiver((BroadcastReceiver) null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        int k = intent.getIntExtra("plugged", -1);
        switch (k) {
            case 1:
                jSONObject.put("is_usb_charge", "0");
                jSONObject.put("is_ac_charge", "1");
                jSONObject.put("is_charging", "1");
                return jSONObject;
            case 2:
                jSONObject.put("is_usb_charge", "1");
                jSONObject.put("is_ac_charge", "0");
                jSONObject.put("is_charging", "1");
                return jSONObject;
            default:
                jSONObject.put("is_usb_charge", "0");
                jSONObject.put("is_ac_charge", "0");
                jSONObject.put("is_charging", "0");
                return jSONObject;
        }
    }

    public static String getGAID() {
        if (!TextUtils.isEmpty(DataHandler.getGaidValue())) {
            return DataHandler.getGaidValue();
        } else {
            try {
                String googleAdId = getGoogleAdId(context);

                Log.d("getGAID", "" + googleAdId);
                DataHandler.setGaidValue(googleAdId);
                return googleAdId;
            } catch (Exception var1) {
                var1.printStackTrace();
            }
            return "";
        }
    }


    private static String getTotalBootTime() {
        String total_boot_time = "";
        try {
            total_boot_time = SystemClock.elapsedRealtime() + "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total_boot_time;
    }

    private static String getTotalBootTimeWake() {
        String total_boot_time = "";
        try {
            total_boot_time = SystemClock.uptimeMillis() + "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total_boot_time;
    }

    public static String getISO3Language(Context paramContext) {
        return paramContext.getResources().getConfiguration().locale.getISO3Language();
    }

    private static String getISO3Country(Context paramContext) {
        return paramContext.getResources().getConfiguration().locale.getISO3Country();
    }

    public static String getLocaleDisplayLanguage() {
        return Locale.getDefault().getDisplayLanguage();
    }

    @SuppressLint("MissingPermission")
    private static String getDeviceImeIValue(Context paramContext) {
        if (!TextUtils.isEmpty(DataHandler.getImeIValue())) {
            return DataHandler.getImeIValue();
        } else {
            if (DataUtil.haveSelfPermission(paramContext, "android.permission.READ_PHONE_STATE")) {
                try {

                    if (VERSION.SDK_INT >= 26) {
                        return ((TelephonyManager) paramContext.getSystemService(Context.TELEPHONY_SERVICE)).getImei();
                    }

                    return ((TelephonyManager) paramContext.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                } catch (Exception var2) {
                }
            }

            return "";
        }
    }

    @SuppressLint({"MissingPermission"})
    private static String getCurrentPhoneNum() {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                String tel = tm.getLine1Number();
                return tel;
            }
        } catch (Exception var2) {
        }

        return "";
    }

    private static String getScreenPhysicalSize(Context paramContext) {
        try {
            Display display = ((WindowManager) paramContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            display.getMetrics(displayMetrics);
            return Double.toString(Math.sqrt(Math.pow((double) ((float) displayMetrics.heightPixels / displayMetrics.ydpi), 2.0D) + Math.pow((double) ((float) displayMetrics.widthPixels / displayMetrics.xdpi), 2.0D)));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public static int getAudioExternalNumber() {
        int result = 0;
        if (!DataUtil.haveSelfPermission(DataHandler.getApplication(), "android.permission.READ_EXTERNAL_STORAGE")) {
            return 0;
        } else {
            Cursor cursor;
            for (cursor = DataHandler.getApplication().getContentResolver().query(Media.EXTERNAL_CONTENT_URI, new String[]{"date_added", "date_modified", "duration", "mime_type", "is_music", "year", "is_notification", "is_ringtone", "is_alarm"}, (String) null, (String[]) null, (String) null); cursor != null && cursor.moveToNext(); ++result) {
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            return result;
        }
    }

    public static int getAudioInternalNumber() {
        int result = 0;

        try {
            Cursor cursor;
            for (cursor = DataHandler.getApplication().getContentResolver().query(Media.INTERNAL_CONTENT_URI, new String[]{"date_added", "date_modified", "duration", "mime_type", "is_music", "year", "is_notification", "is_ringtone", "is_alarm"}, (String) null, (String[]) null, "title_key"); cursor != null && cursor.moveToNext(); ++result) {
            }

            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static int getImagesExternalNumber() {
        int result = 0;
        if (!DataUtil.haveSelfPermission(DataHandler.getApplication(), "android.permission.READ_EXTERNAL_STORAGE")) {
            return 0;
        } else {
            Cursor cursor;
            for (cursor = DataHandler.getApplication().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{"datetaken", "date_added", "date_modified", "height", "width", "latitude", "longitude", "mime_type", "title", "_size"}, (String) null, (String[]) null, (String) null); cursor != null && cursor.moveToNext(); ++result) {
            }

            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

            return result;
        }
    }

    public static int getImagesInternalNumber() {
        int result = 0;

        try {
            Cursor cursor;
            for (cursor = DataHandler.getApplication().getContentResolver().query(MediaStore.Images.Media.INTERNAL_CONTENT_URI, new String[]{"datetaken", "date_added", "date_modified", "height", "width", "latitude", "longitude", "mime_type", "title", "_size"}, (String) null, (String[]) null, (String) null); cursor != null && cursor.moveToNext(); ++result) {
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static int getVideoExternalNumber() {
        int result = 0;
        if (!DataUtil.haveSelfPermission(DataHandler.getApplication(), "android.permission.READ_EXTERNAL_STORAGE")) {
            return 0;
        } else {
            String[] arrayOfString = new String[]{"date_added"};
            Cursor cursor;
            for (cursor = DataHandler.getApplication().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, arrayOfString, (String) null, (String[]) null, (String) null); cursor != null && cursor.moveToNext(); ++result) {
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            return result;
        }
    }

    public static int getVideoInternalNumber() {
        int result = 0;
        try {
            String[] arrayOfString = new String[]{"date_added"};
            Cursor cursor;
            for (cursor = DataHandler.getApplication().getContentResolver().query(MediaStore.Video.Media.INTERNAL_CONTENT_URI, arrayOfString, (String) null, (String[]) null, (String) null); cursor != null && cursor.moveToNext(); ++result) {
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public static int getVersionCode(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public static String getPackageName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static int getDownloadFileNumber() {
        int result = 0;
        try {
            File[] files = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).listFiles();
            if (files != null) {
                result = files.length;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public static int getContactsGroupNumber() {
        int result = 0;
        try {
            if (!DataUtil.haveSelfPermission(DataHandler.getApplication(), "android.permission.READ_CONTACTS")) {
                return 0;
            } else {
                Uri uri = Groups.CONTENT_URI;
                ContentResolver contentResolver = DataHandler.getApplication().getContentResolver();

                Cursor cursor;
                for (cursor = contentResolver.query(uri, (String[]) null, (String) null, (String[]) null, (String) null); cursor != null && cursor.moveToNext(); ++result) {
                }

                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return result;

    }

    public static int getPhoneType() {
        try {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return manager.getPhoneType();
        } catch (Exception var1) {
            var1.printStackTrace();
            return 0;
        }
    }

    public static int getPhoneType(Context context) {
        try {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return manager.getPhoneType();
        } catch (Exception var1) {
            var1.printStackTrace();
            return 0;
        }
    }


    public static String getPhoneTypeName() {
        try {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            int phoneType = manager.getPhoneType();
            switch (phoneType) {
                case TelephonyManager.PHONE_TYPE_NONE:
                    return "None";
                case TelephonyManager.PHONE_TYPE_GSM:
                    return "GSM";
                case TelephonyManager.PHONE_TYPE_CDMA:
                    return "CDMA";
                case TelephonyManager.PHONE_TYPE_SIP:
                    return "SIP";
                default:
                    return "Unknown";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Unknown";

    }


    public static String getPhoneTypeName(int phoneType) {
        switch (phoneType) {
            case TelephonyManager.PHONE_TYPE_NONE:
                return "None";
            case TelephonyManager.PHONE_TYPE_GSM:
                return "GSM";
            case TelephonyManager.PHONE_TYPE_CDMA:
                return "CDMA";
            case TelephonyManager.PHONE_TYPE_SIP:
                return "SIP";
            default:
                return "Unknown";
        }
    }

    private static TelephonyManager getTelephonyManager(Context context) {
        return (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    public static String getPhoneType2(Context context) {
        int phoneType = getTelephonyManager(context).getPhoneType();//locale_iso_3_language
        switch (phoneType) {
            case TelephonyManager.PHONE_TYPE_NONE:
                return "NONE";
            case TelephonyManager.PHONE_TYPE_GSM:
                return "GSM";
            case TelephonyManager.PHONE_TYPE_CDMA:
                return "CDMA";
            case TelephonyManager.PHONE_TYPE_SIP:
                return "SIP";
        }
        return "";
    }


    public static String getLanguage() {
        String language = "";
        try {
            Locale locale = context.getResources().getConfiguration().locale;
            language = locale.getLanguage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return language;
    }

    public static String getNetWorkOperatorName() {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return manager.getNetworkOperatorName();
    }

    private static boolean isOnline() {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public static JSONArray getAppList(Context context) {
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        JSONArray jsonArray = new JSONArray();
        if (packages != null && packages.size() > 0) {
            try {
                for (int i = 0; i < packages.size(); ++i) {
                    PackageInfo packageInfo = (PackageInfo) packages.get(i);
                    String name = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("app_name", name);
                    jsonObject.put("package", packageInfo.packageName);
                    jsonObject.put("version_name", packageInfo.versionName);
                    jsonObject.put("version_code", packageInfo.versionCode);
                    jsonObject.put("in_time", packageInfo.firstInstallTime);
                    jsonObject.put("up_time", packageInfo.lastUpdateTime);
                    jsonObject.put("flags", packageInfo.applicationInfo.flags);
                    jsonObject.put("app_type", (packageInfo.applicationInfo.flags & 1) == 0 ? 0 : 1);
                    jsonArray.put(jsonObject);
                }
            } catch (Exception var7) {
            }
        }

        return jsonArray;
    }

    public static JSONArray getAppList2(Context context) {
        JSONArray jsonArray = new JSONArray();


        try {
            PackageManager pm = context.getPackageManager();
            Process process = Runtime.getRuntime().exec("pm list packages");
            BufferedReader bis = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";


            while ((line = bis.readLine()) != null) {
                Log.d("getApplists", "-------------0");
                Log.d("getApplists", line.replace("package:", ""));
                Log.d("getApplists", "-------------1");
                PackageInfo packageInfo = pm.getPackageInfo(line.replace("package:", ""), PackageManager.GET_GIDS);
                String name = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("app_name", name);
                jsonObject.put("package", packageInfo.packageName);
                jsonObject.put("version_name", packageInfo.versionName);
                jsonObject.put("version_code", packageInfo.versionCode);
                jsonObject.put("in_time", packageInfo.firstInstallTime);
                jsonObject.put("up_time", packageInfo.lastUpdateTime);
                jsonObject.put("flags", packageInfo.applicationInfo.flags);
                jsonObject.put("app_type", (packageInfo.applicationInfo.flags & 1) == 0 ? 0 : 1);
                jsonArray.put(jsonObject);
            }

            bis.close();
        } catch (Exception var9) {
        }

        return jsonArray;
    }

    private static String getMobileDbm() {
        int dbm = -1;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        try {
            List<CellInfo> cellInfoList = tm.getAllCellInfo();
            if (null != cellInfoList) {
                Iterator var3 = cellInfoList.iterator();

                while (var3.hasNext()) {
                    CellInfo cellInfo = (CellInfo) var3.next();
                    if (cellInfo instanceof CellInfoGsm) {
                        CellSignalStrengthGsm cellSignalStrengthGsm = ((CellInfoGsm) cellInfo).getCellSignalStrength();
                        dbm = cellSignalStrengthGsm.getDbm();
                    } else if (cellInfo instanceof CellInfoCdma) {
                        CellSignalStrengthCdma cellSignalStrengthCdma = ((CellInfoCdma) cellInfo).getCellSignalStrength();
                        dbm = cellSignalStrengthCdma.getDbm();
                    } else if (cellInfo instanceof CellInfoWcdma) {
                        CellSignalStrengthWcdma cellSignalStrengthWcdma = ((CellInfoWcdma) cellInfo).getCellSignalStrength();
                        dbm = cellSignalStrengthWcdma.getDbm();
                    } else if (cellInfo instanceof CellInfoLte) {
                        CellSignalStrengthLte cellSignalStrengthLte = ((CellInfoLte) cellInfo).getCellSignalStrength();
                        dbm = cellSignalStrengthLte.getDbm();
                    }
                }
            }
        } catch (Exception var6) {
        }

        return String.valueOf(dbm);
    }

    private static String getWifiIP() {
        String ip = null;

        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (wifiManager.isWifiEnabled()) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int i = wifiInfo.getIpAddress();
                ip = (i & 255) + "." + (i >> 8 & 255) + "." + (i >> 16 & 255) + "." + (i >> 24 & 255);
            }
        } catch (Exception var4) {
        }

        return ip;
    }

    private static String getWifiName() {
        if (isOnline() && getNetworkState().equals("WIFI")) {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String ssid = wifiInfo.getSSID();
            if (!TextUtils.isEmpty(ssid) && ssid.contains("\"")) {
                ssid = ssid.replaceAll("\"", "");
            }

            return ssid;
        } else {
            return "";
        }
    }

    private static String getMacAddress() {
        String mac = getMacAddress1();
        if (TextUtils.isEmpty(mac)) {
            mac = getMacFromHardware();
        }

        return mac;
    }

    private static String getMacAddress1() {
        try {
            WifiManager localWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo localWifiInfo = localWifiManager.getConnectionInfo();
            String macAddress = localWifiInfo.getMacAddress();
            if (TextUtils.isEmpty(macAddress) || "02:00:00:00:00:00".equals(macAddress)) {
                macAddress = getMacAddress2();
            }

            return macAddress;
        } catch (Exception var3) {
            return null;
        }
    }

    private static String getMacAddress2() {
        if (isOnline() && getNetworkState().equals("WIFI")) {
            String macSerial = null;
            String str = "";

            try {
                Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
                InputStreamReader ir = new InputStreamReader(pp.getInputStream());
                LineNumberReader input = new LineNumberReader(ir);

                while (null != str) {
                    str = input.readLine();
                    if (str != null) {
                        macSerial = str.trim();
                        break;
                    }
                }
            } catch (Exception var5) {
            }

            return macSerial;
        } else {
            return "";
        }
    }

    private static String getMacFromHardware() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            Iterator var1 = all.iterator();

            while (var1.hasNext()) {
                NetworkInterface nif = (NetworkInterface) var1.next();
                if (nif.getName().equalsIgnoreCase("wlan0")) {
                    byte[] macBytes = nif.getHardwareAddress();
                    if (macBytes == null) {
                        return null;
                    }

                    StringBuilder mac = new StringBuilder();
                    byte[] var5 = macBytes;
                    int var6 = macBytes.length;

                    for (int var7 = 0; var7 < var6; ++var7) {
                        byte b = var5[var7];
                        mac.append(String.format("%02X:", b));
                    }

                    if (mac.length() > 0) {
                        mac.deleteCharAt(mac.length() - 1);
                    }

                    return mac.toString();
                }
            }
        } catch (Exception var9) {
        }

        return null;
    }


    public static String getModelName() {
        return Build.MODEL;
    }

    public static String getManufacturerName() {
        return Build.MANUFACTURER;
    }


    public static String getBoard() {
        return Build.BOARD;
    }


    private static boolean isRoot() {
        boolean bool = false;

        try {
            if (!(new File("/system/bin/su")).exists() && !(new File("/system/xbin/su")).exists()) {
                bool = false;
            } else {
                bool = true;
            }
        } catch (Exception var2) {
        }

        return bool;
    }

    private static long bootTime() {
        return System.currentTimeMillis() - SystemClock.elapsedRealtimeNanos() / 1000000L;
    }

    private static boolean isEmulator() {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String imei = tm.getDeviceId();
            if (imei != null && imei.equals("000000000000000")) {
                return true;
            } else {
                return Build.MODEL.equals("sdk") || Build.MODEL.equals("google_sdk");
            }
        } catch (Exception var2) {
            return false;
        }
    }

    @SuppressLint({"HardwareIds"})
    private static String getAndroidId(Context context) {
        try {
            return Secure.getString(context.getApplicationContext().getContentResolver(), "android_id");
        } catch (Exception var2) {
            var2.printStackTrace();
            return null;
        }
    }

    private static String getNetworkState() {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == connManager) {
            return "none";
        } else {
            NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
            if (activeNetInfo != null && activeNetInfo.isAvailable()) {
                NetworkInfo wifiInfo = connManager.getNetworkInfo(1);
                if (null != wifiInfo) {
                    State state = wifiInfo.getState();
                    if (null != state && (state == State.CONNECTED || state == State.CONNECTING)) {
                        return "WIFI";
                    }
                }

                NetworkInfo networkInfo = connManager.getNetworkInfo(0);
                if (null != networkInfo) {
                    State state = networkInfo.getState();
                    String strSubTypeName = networkInfo.getSubtypeName();
                    if (null != state && (state == State.CONNECTED || state == State.CONNECTING)) {
                        switch (activeNetInfo.getSubtype()) {
                            case 1:
                            case 2:
                            case 4:
                            case 7:
                            case 11:
                                return "2G";
                            case 3:
                            case 5:
                            case 6:
                            case 8:
                            case 9:
                            case 10:
                            case 12:
                            case 14:
                            case 15:
                                return "3G";
                            case 13:
                                return "4G";
                            default:
                                if (!strSubTypeName.equalsIgnoreCase("TD-SCDMA") && !strSubTypeName.equalsIgnoreCase("WCDMA") && !strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                                    return "other";
                                }

                                return "3G";
                        }
                    }
                }

                return "none";
            } else {
                return "none";
            }
        }
    }

    private static String getCurrentTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        String strTz = tz.getDisplayName(false, 0);
        return strTz;
    }

    private static String getDriverBrand() {
        try {
            return Build.BRAND;
        } catch (Exception var1) {
            return "";
        }
    }

    private static int getDriverSDKVersion() {
        try {
            return VERSION.SDK_INT;
        } catch (Exception var1) {
            return 1;
        }
    }

    private static String getDriverModel() {
        try {
            return Build.MODEL;
        } catch (Exception var1) {
            return "";
        }
    }

    private static String getDriverOsVersion() {
        try {
            return VERSION.RELEASE;
        } catch (Exception var1) {
            return "";
        }
    }

    private static String getSerialNumber1() {
        try {
            Class<?> clazz = Class.forName("android.os.SystemProperties");

            return (String) clazz.getMethod("get", String.class).invoke(clazz, "ro.serialno");
        } catch (Exception var1) {
            return "";
        }
    }

    private static String getSerialNumber() {
        String serial = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialnocustom");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (serial == null) {
            serial = getSerialNumber1();
        }
        return serial;
    }


    /**
     * <p>手机操作系统版本</p>
     *
     * @return
     * @author
     * @date 2013-1-4
     */
    public static String getSoftSDKVersion() {
        return VERSION.RELEASE;//Firmware/OS 版本号
    }


    public static String getScreenDisplay(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        return width + "*" + height;
    }


    public static String getIMEI1(Context context) {
        String imei1 = "";
        try {
            imei1 = getImeiOrMeid(context, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(imei1)) {
            imei1 = Settings.System.getString(
                    context.getContentResolver(), Secure.ANDROID_ID);
        }
        if (!TextUtils.isEmpty(imei1)) {
            return imei1;
        }

        return "";
    }


    public static String getIMEI2(Context context) {

        try {
            //imei2必须与 imei1不一样
            String imeiDefault = getIMEI1(context);
            if (TextUtils.isEmpty(imeiDefault)) {
                //默认的 imei 竟然为空，说明权限还没拿到，或者是平板
                //这种情况下，返回 imei2也应该是空串
                return "";
            }

            //注意，拿第一个 IMEI 是传0，第2个 IMEI 是传1，别搞错了
            String imei1 = getImeiOrMeid(context, 0);
            String imei2 = getImeiOrMeid(context, 1);
            //sim 卡换卡位时，imei1与 imei2有可能互换，而 imeidefault 有可能不变
            if (!TextUtils.equals(imei2, imeiDefault)) {
                //返回与 imeiDefault 不一样的
                return imei2;
            }
            if (!TextUtils.equals(imei1, imeiDefault)) {
                return imei1;
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

        return "";
    }


    public static String getImeiOrMeid(Context context, int slotId) {
        String imei = "";

        //Android 6.0 以后需要获取动态权限  检查权限
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return imei;
        }

        try {
            TelephonyManager manager = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            if (manager != null) {
                if (VERSION.SDK_INT >= Build.VERSION_CODES.O) {// android 8 即以后建议用getImei 方法获取 不会获取到MEID
                    Method method = manager.getClass().getMethod("getImei", int.class);
                    imei = (String) method.invoke(manager, slotId);
                } else if (VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //5.0的系统如果想获取MEID/IMEI1/IMEI2  ----framework层提供了两个属性值“ril.cdma.meid"和“ril.gsm.imei"获取
                    imei = getSystemPropertyByReflect("ril.gsm.imei");
                    //如果获取不到 就调用 getDeviceId 方法获取

                } else {//5.0以下获取imei/meid只能通过 getDeviceId  方法去取


                }
            }
        } catch (Exception e) {
        }

        if (TextUtils.isEmpty(imei)) {
            imei = getDeviceId(context, slotId);
        }
        return imei;
    }


    private static String getSystemPropertyByReflect(String key) {
        try {
            @SuppressLint("PrivateApi")
            Class<?> clz = Class.forName("android.os.SystemProperties");
            Method getMethod = clz.getMethod("get", String.class, String.class);
            return (String) getMethod.invoke(clz, key, "");
        } catch (Exception e) {/**/}
        return "";
    }


    public static String getDeviceId(Context context, int slotId) {
        String imei = "";
        imei = getDeviceIdFromSystemApi(context, slotId);
        if (TextUtils.isEmpty(imei)) {
            imei = getDeviceIdByReflect(context, slotId);
        }
        return imei;
    }

    public static String getDeviceIdFromSystemApi(Context context, int slotId) {
        String imei = "";
        try {
            TelephonyManager telephonyManager =
                    (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                imei = telephonyManager.getDeviceId(slotId);
            }
        } catch (Throwable e) {
        }
        return imei;
    }


    /**
     * 反射获取 deviceId
     *
     * @param context
     * @param slotId  slotId为卡槽Id，它的值为 0、1；
     * @return
     */
    public static String getDeviceIdByReflect(Context context, int slotId) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            Method method = tm.getClass().getMethod("getDeviceId", int.class);
            return method.invoke(tm, slotId).toString();
        } catch (Throwable e) {
        }
        return "";
    }


    /**
     * 这个方法是耗时的，不能在主线程调用
     */
    public static String getGoogleAdId(Context context) throws Exception {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            return "Cannot call in the main thread, You must call in the other thread";
        }
        PackageManager pm = context.getPackageManager();
        pm.getPackageInfo("com.android.vending", 0);
        AdvertisingConnection connection = new AdvertisingConnection();
        Intent intent = new Intent(
                "com.google.android.gms.ads.identifier.service.START");
        intent.setPackage("com.google.android.gms");
        if (context.bindService(intent, connection, Context.BIND_AUTO_CREATE)) {
            try {
                AdvertisingInterface adInterface = new AdvertisingInterface(
                        connection.getBinder());
                return adInterface.getId();
            } finally {
                context.unbindService(connection);
            }
        }
        return "";
    }

    private static boolean isWifiProxy() {
        boolean IS_ICS_OR_LATER = VERSION.SDK_INT >= 14;
        String proxyAddress = "";
        int proxyPort = -1;
        try {
            if (IS_ICS_OR_LATER) {
                proxyAddress = System.getProperty("http.proxyHost");
                String portStr = System.getProperty("http.proxyPort");
                proxyPort = Integer.parseInt(portStr != null ? portStr : "-1");
            } else {
                proxyAddress = Proxy.getHost(context);
                proxyPort = Proxy.getPort(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return !TextUtils.isEmpty(proxyAddress) && proxyPort != -1;
    }

    public static boolean isUsingVPN() {
        try {
            if (VERSION.SDK_INT > 14) {
                String defaultHost = Proxy.getDefaultHost();
                return !TextUtils.isEmpty(defaultHost);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    private static boolean isUsbDebug() {
        try {
            return Secure.getInt(context.getContentResolver(), "adb_enabled", 0) > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static JSONArray getSensorList() {
        JSONArray jsonArray = new JSONArray();
        try {
            SensorManager sensorManager = (SensorManager) context.getSystemService("sensor");
            List<Sensor> sensors = sensorManager.getSensorList(-1);
            Iterator var3 = sensors.iterator();
            while (var3.hasNext()) {
                Sensor sensor = (Sensor) var3.next();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", String.valueOf(sensor.getType()));
                jsonObject.put("name", String.valueOf(sensor.getName()));
                jsonObject.put("version", String.valueOf(sensor.getVersion()));
                jsonObject.put("vendor", String.valueOf(sensor.getVendor()));
                jsonObject.put("maxRange", String.valueOf(sensor.getMaximumRange()));
                jsonObject.put("minDelay", String.valueOf(sensor.getMinDelay()));
                jsonObject.put("power", String.valueOf(sensor.getPower()));
                jsonObject.put("resolution", String.valueOf(sensor.getResolution()));
                jsonArray.put(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    public static long getElapsedRealtime() {
        try {
            return SystemClock.elapsedRealtime();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1l;
    }

    public static long getUpdateMills() {
        try {
            return SystemClock.uptimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1l;
    }

    public static int getScanResults(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            List<ScanResult> scanResults = wifiManager.getScanResults();
            return scanResults.size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static final class AdvertisingConnection implements ServiceConnection {
        private final LinkedBlockingQueue<IBinder> queue = new LinkedBlockingQueue<>(1);
        boolean retrieved = false;

        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                this.queue.put(service);
            } catch (InterruptedException localInterruptedException) {
            }
        }

        public void onServiceDisconnected(ComponentName name) {
        }

        public IBinder getBinder() throws InterruptedException {
            if (this.retrieved)
                throw new IllegalStateException();
            this.retrieved = true;
            return this.queue.take();
        }
    }

    private static final class AdvertisingInterface implements IInterface {
        private IBinder binder;

        public AdvertisingInterface(IBinder pBinder) {
            binder = pBinder;
        }

        public IBinder asBinder() {
            return binder;
        }

        public String getId() throws RemoteException {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            String id;
            try {
                data.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                binder.transact(1, data, reply, 0);
                reply.readException();
                id = reply.readString();
            } finally {
                reply.recycle();
                data.recycle();
            }
            return id;
        }
    }

}
