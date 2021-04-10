package cdax.naindex.mylibrary;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import io.branch.referral.util.BranchEvent;


/**
 * 使用方法
 * 1.携带参数的打点
 * Bundle values = new Bundle();
 * values.putString("测试数据的key","测试数据的value");
 * values.putInt("测试整型数据的key",666);
 * FirebaseLog.getInstance().report("打点key名",values);
 * 2.不携带参数的打点
 * FirebaseLog.getInstance().report("不携带参数的打点key名");
 */


/**
 * author Created by harrishuang on 2020/11/11.
 * email : huangjinping1000@163.com
 */
public class FirebaseLog {

    private static volatile FirebaseLog instance;
    private static FirebaseAnalytics mFirebaseAnalytics;

    public static FirebaseLog getInstance() {
        if (instance == null) {
            instance = new FirebaseLog();
        }
        return instance;
    }

    @SuppressLint("MissingPermission")
    private FirebaseLog() {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(AppManager.getContext());
    }

    /**
     * 埋点
     *
     * @param key
     * @param value
     */
    public void report(String key, Bundle value) {
        if (value == null) {
            value = new Bundle();
        }
        key = key.trim();
        mFirebaseAnalytics.logEvent(key, value);
        BranchEvent be = new BranchEvent(key);
        be.logEvent(AppManager.getContext());
    }

    /**
     * 埋点
     *
     * @param key
     */
    public void report(String key) {
        report(key, null);
    }


}
