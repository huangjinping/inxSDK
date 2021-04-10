package cdax.naindex.mylibrary;

import android.app.Application;
import android.content.Context;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import io.branch.referral.Branch;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * author Created by harrishuang on 4/9/21.
 * email : huangjinping1000@163.com
 */
public class AppManager {


    private static Context context;

    public static void init(Application context,String branchKey) {
        AppManager.context=context;
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(context);
        Branch.enableLogging();
        Branch.getAutoInstance(context, branchKey);
    }

    public static Context getContext() {
        return context;
    }
}
