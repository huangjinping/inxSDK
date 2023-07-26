package con.modhe.myapplication;

import android.app.Application;
import android.content.Context;

import com.facebook.appevents.AppEventsLogger;

import cdax.naindex.outer.AppManager;

/**
 * author Created by harrishuang on 2020/11/14.
 * email : huangjinping1000@163.com
 */
public class App extends Application {
    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
//        BugCrash.initCustomCrash(this);

        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);

//        BugCrash.initStatus(this);
        AppManager.init(this, "key_live_laYiqXqvAwfw1vm18RNDIcbpuEf54kIV");

//        FacebookSdk.sdkInitialize(this);
//        FacebookSdk.setAutoLogAppEventsEnabled(true);
//        FacebookSdk.setAutoInitEnabled(true);
//        FacebookSdk.fullyInitialize();
        AppEventsLogger.activateApp(this);

//        FacebookSdk.setIsDebugEnabled(true);
//        FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS);


    }
}
