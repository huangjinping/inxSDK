package cdax.naindex.outer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

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


    private static FirebaseLog instance;
    private static FirebaseAnalytics mFirebaseAnalytics;

    @SuppressLint("MissingPermission")
    private FirebaseLog() {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(AppManager.getContext());
    }


    public static void point2(Context context, String log) {
        FirebaseAnalytics.getInstance(context).logEvent(log, new Bundle());
    }

    public static FirebaseLog getInstance() {
        if (instance == null) {
            instance = new FirebaseLog();
        }
        return instance;
    }

    public void report(String key) {

        Log.d("report", "=====" + key);
        if (TextUtils.isEmpty(key)) {
            return;
        }
        key = key.trim();
        mFirebaseAnalytics.logEvent(key, new Bundle());
        try {
            BranchEvent be = new BranchEvent(key);
            be.logEvent(AppManager.getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
