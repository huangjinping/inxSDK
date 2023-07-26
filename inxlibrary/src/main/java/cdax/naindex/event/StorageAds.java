package cdax.naindex.event;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;
import java.text.DecimalFormat;

//https://blog.csdn.net/shijx190221/article/details/88682947 目录区分
public class StorageAds {


    public static long getDataTotalSize(Context context) {
        String absolutePath = context.getCacheDir().getAbsolutePath();
        Log.d("getStorageInfo", "----------" + absolutePath);
        StatFs sf = new StatFs(absolutePath);
        long blockSize = sf.getBlockSizeLong();
        long totalBlocks = sf.getBlockCountLong();
        return blockSize * totalBlocks;
    }


    // 获取SD卡全部存储空间
    public static long getTotalSize() {
        File file = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs(file.getPath());
//        //获得sdcard上 block的总数
        long blockCount = statFs.getBlockCountLong();
        //获得sdcard上每个block 的大小
        long blockSize = statFs.getBlockSizeLong();
        //计算标准大小使用：1024，当然使用1000也可以
        return blockCount * blockSize;

//        long blockSize = statFs.getBlockSize();
//        long totalBlocks = statFs.getBlockCount();

//        return blockSize * totalBlocks;
    }

    // 获取SD卡可用存储空间
    public static long getAvailableSize() {
        File file = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs(file.getPath());
        //获得sdcard上 block的总数
        long feeBlockCount = statFs.getAvailableBlocksLong();
        //获得sdcard上每个block 的大小
        long blockSize = statFs.getBlockSizeLong();
        //计算标准大小使用：1024，当然使用1000也可以
//        return feeBlockCount * blockSize;
        return statFs.getAvailableBytes();

    }


    //自带存储大小
    public static long getTotalInternalMemorySize() {
        try {

            File path = Environment.getDataDirectory();

            StatFs stat = new StatFs(path.getPath());
            long blockSize = (long) stat.getBlockSizeLong();
            long totalBlocks = (long) stat.getBlockCountLong();
            return totalBlocks * blockSize;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1L;
    }

    //自带存储可用大小
    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = (long) stat.getBlockSizeLong();
        long availableBlocks = (long) stat.getAvailableBlocksLong();
//        return availableBlocks * blockSize;
        return stat.getAvailableBytes();
    }

    //总内存大小
    public static long getRamTotalSize(Context paramContext) {
        ActivityManager activityManager = (ActivityManager) paramContext.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo.totalMem;
    }

    //内存可用大小
    public static long getRamAvailSize(Context paramContext) {
        ActivityManager activityManager = (ActivityManager) paramContext.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);

        return memoryInfo.availMem;
    }


    public static String formatFileSize(long length) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        DecimalFormat df = new DecimalFormat("#.00");
        if (length >= gb) {
            return df.format((double) length / gb) + "GB";
        }
        if (length >= mb) {
            return df.format((double) length / mb) + "MB";
        }
        if (length >= kb) {
            return String.format("%.2f", (double) length / kb) + "KB";
        }
        return length + "B";
    }


}
