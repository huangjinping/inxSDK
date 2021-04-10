package con.modhe.myapplication;

import android.app.Application;
import android.content.Context;

import cdax.naindex.mylibrary.AppManager;

/**
 * author Created by harrishuang on 2020/11/14.
 * email : huangjinping1000@163.com
 */
public class App extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        AppManager.init(this, "key_live_laYiqXqvAwfw1vm18RNDIcbpuEf54kIV");
    }

    public static Context getContext() {
        return context;
    }
}
