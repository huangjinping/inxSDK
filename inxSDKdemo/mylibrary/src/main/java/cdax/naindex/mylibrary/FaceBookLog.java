package cdax.naindex.mylibrary;

import android.content.Context;
import android.os.Bundle;

import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;

/**
 * author Created by harrishuang on 2020/11/14.
 * email : huangjinping1000@163.com
 */
 public class FaceBookLog {

    private static volatile FaceBookLog instance;

    public static FaceBookLog getInstance() {
        if (instance == null) {
            instance = new FaceBookLog();
        }
        return instance;
    }


    void logs(Context context) {
        AppEventsLogger logger = AppEventsLogger.newLogger(context);
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "USD");
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "product");
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT, "[{\"id\": \"1234\", \"quantity\": 2}, {\"id\": \"5678\", \"quantity\": 1}]");

        logger.logEvent(AppEventsConstants.EVENT_NAME_ACHIEVED_LEVEL,
                54.23,
                params);
        params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "USD");
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "product");
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, "HDFU-8452");

        logger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_CART,
                54.23,
                params);
    }

    /**
     * 埋点
     * @param key
     * @param value
     */
    public void report(String key, Bundle value) {
        if (value == null) {
            value = new Bundle();
        }
        key=key.trim();
        AppEventsLogger logger = AppEventsLogger.newLogger(AppManager.getContext());
        logger.logEvent(key,
                54.23,
                value);


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
