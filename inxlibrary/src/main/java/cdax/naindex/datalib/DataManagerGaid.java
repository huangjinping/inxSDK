package cdax.naindex.datalib;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import static android.content.Context.MODE_PRIVATE;

/**
 * author Created by harrishuang on 4/14/21.
 * email : huangjinping1000@163.com
 */
public class DataManagerGaid {
    public static String getGaid(Context context) {
        String gaid = "";
        SharedPreferences local_location = context.getSharedPreferences("gaid", MODE_PRIVATE);
        gaid = local_location.getString("gaid", "");
        return gaid;
    }

    public static void clearGaid(Context context) {
        SharedPreferences setting_info = context.getSharedPreferences("gaid", MODE_PRIVATE);
        SharedPreferences.Editor edit = setting_info.edit();
        edit.putString("gaid", "");
        edit.commit();
    }

    public static void saveLocalGaid(Context context, String gaid) {
        if (TextUtils.isEmpty(gaid)) {
            return;
        }
        SharedPreferences setting_info = context.getSharedPreferences("gaid", MODE_PRIVATE);
        SharedPreferences.Editor edit = setting_info.edit();
        edit.putString("gaid", gaid);
        edit.commit();
    }
}
