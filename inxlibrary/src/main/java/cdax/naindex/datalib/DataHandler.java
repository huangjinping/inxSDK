package cdax.naindex.datalib;

import android.content.Context;

public class DataHandler {
    private static Context mContext;
    private static String ImeIValue = "";
    private static String gaidValue = "";

    public DataHandler() {

    }


    public static void setGaidValue(String gaidValue) {
        DataHandler.gaidValue = gaidValue;
    }

    public static void init(Context context) {
        mContext = context;
    }

    public static Context getApplication() {
        return mContext;
    }

    static String getImeIValue() {
        return ImeIValue;
    }

    static String getGaidValue() {
        return gaidValue;
    }




}