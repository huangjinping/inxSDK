package cdax.naindex.outer;

import android.app.Application;
import android.content.Context;



import cdax.naindex.datalib.DataManagerGaid;
import cdax.naindex.datalib.DeviceUtils;
import io.branch.referral.Branch;


/**
 * author Created by harrishuang on 4/9/21.
 * email : huangjinping1000@163.com
 */
public class AppManager {


    private static Context context;

    public static void init(Application context, String branchKey) {
        AppManager.context = context;


        Branch.enableLogging();
        Branch.getAutoInstance(context, branchKey);
        initGuid(context);
    }


    private static void initGuid(final Context context) {
        try {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    String gaid = DeviceUtils.getGAID();
                    DataManagerGaid.saveLocalGaid(context, gaid);
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Context getContext() {
        return context;
    }
}
