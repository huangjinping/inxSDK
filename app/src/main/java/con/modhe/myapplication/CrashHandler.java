package con.modhe.myapplication;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static String TAG = "CrashHandler";

    private static CrashHandler instance = new CrashHandler();
    private Thread.UncaughtExceptionHandler mExceptionHandler;
    private Context mContext;

    private CrashHandler() {
    }

    //单例
    public static CrashHandler getInstance() {
        return instance;
    }

    //初始化，建议再application中调用
    public void init(Context context) {
        mExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
    }

    // 获取异常信息
    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable ex) {
        try {
            printWriteExceptionLog(ex);
            uploadToNet();
            if (mExceptionHandler != null) {
                mExceptionHandler.uncaughtException(thread, ex);
            } else {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadToNet() {

    }

    //写入异常信息
    private void printWriteExceptionLog(Throwable ex) {

        String path = mContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getPath() + "/crashLog/";
        File src = new File(path);
        if (!src.exists()) {
            src.mkdirs();
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");

        String time = simpleDateFormat.format(System.currentTimeMillis());
//        String time = MyDateUtil.formatTime(System.currentTimeMillis(), CommonConstants.YYYY_MM_DD_HH_MM_SS_EN);
        try {
            Log.d(TAG, time);
            File file = new File(path + "crash_log" + time + ".txt");
            PrintWriter pw = new PrintWriter(new FileWriter(file));
            Log.d(TAG, file.getAbsolutePath());
            pw.println(time);
            pw.println();
            writePhoneInfo(pw);
            ex.printStackTrace(pw);
            pw.close();
        } catch (Exception e) {
//            KLog.e(ex.toString());
            Log.d(TAG, ex.toString());
        }
    }

    //写入手机信息
    private void writePhoneInfo(PrintWriter pw) {
        pw.println("手机品牌:" + PhoneUtil.getPhoneBrand());
        pw.println("手机型号:" + PhoneUtil.getPhoneName());
        pw.println("机系统版本号:" + PhoneUtil.getPhoneVersion());
        pw.println("系统版本:" + PhoneUtil.getPhoneSDK());
        pw.println("系统CPU架构:" + PhoneUtil.getCpus());
    }
}
