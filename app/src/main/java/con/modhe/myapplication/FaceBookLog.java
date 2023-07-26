package con.modhe.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.appevents.AppEventsLogger;

import cdax.naindex.datalib.DeviceUtils;
import cdax.naindex.outer.AppManager;


public class FaceBookLog {


    public static void report(String key) {

        Log.d("FaceBookLog", "" + key);
        if (TextUtils.isEmpty(key)) {
            return;
        }

        key = key.trim();
        AppEventsLogger logger = AppEventsLogger.newLogger(AppManager.getContext());
        logger.logEvent(key);


    }


}
