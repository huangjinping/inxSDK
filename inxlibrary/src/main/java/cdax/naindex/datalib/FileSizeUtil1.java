package cdax.naindex.datalib;

import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import java.io.File;

/**
 * author Created by harrishuang on 4/14/21.
 * email : huangjinping1000@163.com
 */
class FileSizeUtil1 {
    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = (long) stat.getBlockSizeLong();
        long availableBlocks = (long) stat.getAvailableBlocksLong();
        return availableBlocks * blockSize;
    }

    public static long getAvailableExternalMemorySize() {
        try {
            if (FileSizeUtil.externalMemoryAvailable()) {
                File path = Environment.getExternalStorageDirectory();
                StatFs stat = new StatFs(path.getPath());
                long blockSize = (long) stat.getBlockSizeLong();
                long availableBlocks = (long) stat.getAvailableBlocksLong();
                return availableBlocks * blockSize;
            } else {
                return -1L;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1L;

    }

    public static String format(final long lMillis, final String strInFmt) {
        if (TextUtils.isEmpty(strInFmt)) {
            throw new NullPointerException("message");
        }
        return (String) android.text.format.DateFormat.format(strInFmt, lMillis);
    }
}
