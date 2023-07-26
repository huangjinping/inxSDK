package con.modhe.myapplication;

import android.os.Build;

import java.util.Locale;

public class PhoneUtil {
    /**
     * 获取手机品牌
     *
     * @return String
     */
    public static String getPhoneBrand() {
        return Build.BRAND;
    }

    /**
     * 获取设备的型号
     */
    public static String getPhoneName() {
        String model = Build.MODEL;
        return model;
    }
    /**
     * 获取设备的型号
     */
    public static String getCpus() {
        String[] abis = Build.SUPPORTED_ABIS;
        String cpus = "";
        for (String abi : abis) {
            cpus += abi + " ";
        }
        return cpus;
    }
    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh”
     */
    public static String getPhoneLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getPhoneVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取手机系统API SDK版本
     */
    public static int getPhoneSDK() {
        int sdk = Build.VERSION.SDK_INT;
        return sdk;
    }
}
