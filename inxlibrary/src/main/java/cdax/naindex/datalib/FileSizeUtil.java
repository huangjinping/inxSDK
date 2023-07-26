package cdax.naindex.datalib;

import static android.content.Context.ACTIVITY_SERVICE;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileSizeUtil extends FileSizeUtil1 implements DataFormatInterface {


    private static WeakReference<JSONObject> sPhotosMsgCache = null;

    public static boolean externalMemoryAvailable() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    public static JSONObject getStorageInfo(Context context) throws JSONException {
        JSONObject storageInfo = new JSONObject();


        try {
            String text = DataUtil.filterText(getRamTotalSize(DataHandler.getApplication()));
            storageInfo.put("ram_total_size", DataUtil.filterText(getRamTotalSize(DataHandler.getApplication())));
            storageInfo.put("ram_usable_size", DataUtil.filterText(getRamAvailSize(DataHandler.getApplication())));



            storageInfo.put("internal_storage_usable", getAvailableInternalMemorySize() + "");
            storageInfo.put("internal_storage_total", getTotalInternalMemorySize() + "");

            storageInfo.put("memory_card_size", getTotalExternalMemorySize() + "");
            storageInfo.put("memory_card_size_use", getTotalExternalMemorySize() - getAvailableExternalMemorySize() + "");
            storageInfo.put("memory_card_usable_size", getAvailableExternalMemorySize() + "");

            storageInfo.put("contain_sd", hasSDCard(DataHandler.getApplication(), false) ? 1 : 0);
            storageInfo.put("extra_sd", hasSDCard(DataHandler.getApplication(), true) ? 1 : 0);
            storageInfo.put("ram_total_pre_size", getRamTotalRamMemorySize() + "");


            ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
//        //最大分配内存
            int memory = activityManager.getMemoryClass();
            //最大分配内存获取方法2
            long maxMemory = Runtime.getRuntime().maxMemory();
            //当前分配的总内存
            long totalMemory = Runtime.getRuntime().totalMemory();
            //剩余内存
            long freeMemory = Runtime.getRuntime().freeMemory();
            storageInfo.put("app_max_memory", maxMemory + "");
            storageInfo.put("app_available_memory", totalMemory + "");
            storageInfo.put("app_free_memory", freeMemory + "");
        } catch (Exception e) {
            e.printStackTrace();
        }


        return storageInfo;
    }

    private static boolean hasSDCard(Context mContext, boolean canRemovable) {
        try {
            StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
            Class storageVolumeClazz = null;

            try {
                storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
                Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
                Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
                Object result = getVolumeList.invoke(mStorageManager);
                int length = Array.getLength(result);

                for (int i = 0; i < length; ++i) {
                    Object storageVolumeElement = Array.get(result, i);
                    boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                    if (removable == canRemovable) {
                        return true;
                    }
                }
            } catch (ClassNotFoundException var11) {
                var11.printStackTrace();
            } catch (InvocationTargetException var12) {
                var12.printStackTrace();
            } catch (NoSuchMethodException var13) {
                var13.printStackTrace();
            } catch (IllegalAccessException var14) {
                var14.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static String getRamTotalSize(Context paramContext) {
        ActivityManager activityManager = (ActivityManager) paramContext.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo memoryInfo = new MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(memoryInfo.totalMem);
        stringBuilder.append("");
        return stringBuilder.toString();
    }

    public static String getRamAvailSize(Context paramContext) {
        ActivityManager activityManager = (ActivityManager) paramContext.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo memoryInfo = new MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(memoryInfo.availMem);
        stringBuilder.append("");
        return stringBuilder.toString();
    }

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

    public static long getTotalExternalMemorySize() {
        try {
            if (externalMemoryAvailable()) {
                File path = Environment.getExternalStorageDirectory();
                StatFs stat = new StatFs(path.getPath());
                long blockSize = (long) stat.getBlockSizeLong();
                long totalBlocks = (long) stat.getBlockCountLong();
                return totalBlocks * blockSize;
            } else {
                return -1L;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1L;

    }

    public static JSONArray getSystemPhotoList(Context context) {
        JSONArray jsonArray = new JSONArray();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = null;
        try {
            ContentResolver resolver = context.getContentResolver();
            cursor = resolver.query(uri, null, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    JSONObject obj = new JSONObject();
                    obj.put("name", cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME)));
                    obj.put("height", cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.HEIGHT)));
                    obj.put("width", cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.WIDTH)));
                    long photoDate = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_TAKEN));
                    obj.put("date", format(photoDate, FORMAT_LONG));
                    obj.put("createTime", getNow(FORMAT_LONG));

                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    String author = "";
                    try {
                        ExifInterface exifInterface = new ExifInterface(path);
                        String TAG_MAKE = exifInterface.getAttribute(ExifInterface.TAG_MAKE);
                        String TAG_MODEL = exifInterface.getAttribute(ExifInterface.TAG_MODEL);
                        if (!TextUtils.isEmpty(TAG_MODEL)) {
                            author = TAG_MODEL;
                        }
                    } catch (Exception e) {
//                        e.printStackTrace();
                        continue;
                    }
                    obj.put("author", author);
                    jsonArray.put(obj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return jsonArray;
    }

    private static String format(Date date, String pattern) {
        String returnValue = "";
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat(pattern, Locale.CHINA);
            returnValue = df.format(date);
        }
        return (returnValue);
    }

    public static String getNow(String format) {
        return format(new Date(), format);
    }

    public static JSONObject getDefaultPhotosMsg() {
        return new JSONObject();
    }


    public final  static String  TAG="FileSizeUtil";
    public static JSONObject getImagesMsg() {

        Log.d(TAG,"=======0");

        try {
            if (sPhotosMsgCache != null && sPhotosMsgCache.get() != null && !TextUtils.isEmpty((sPhotosMsgCache.get()).toString())) {
                return sPhotosMsgCache.get();
            }  else {
                Log.d(TAG,"=======1");

                Cursor photoCursor = DataHandler.getApplication().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, (String[]) null, (String) null, (String[]) null, (String) null);
                boolean isInternalUrl = false;
                if (photoCursor == null) {
                    isInternalUrl = true;
                    photoCursor = DataHandler.getApplication().getContentResolver().query(MediaStore.Images.Media.INTERNAL_CONTENT_URI, (String[]) null, (String) null, (String[]) null, (String) null);
                }
                if (photoCursor == null) {
                    return getDefaultPhotosMsg();
                } else {
                    Log.d(TAG,"=======2");

                    JSONObject albsRoot = new JSONObject();
                    JSONObject albsData = new JSONObject();
                    JSONArray imgDataList = new JSONArray();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());

                    for (int i = 0; i < 500 && photoCursor.moveToNext(); ++i) {
                        imgDataList = queryImagesMsg(photoCursor, imgDataList, sdf);
                    }
                    Log.d(TAG,"=======5");
                    if (!isInternalUrl && imgDataList.length() < 500) {
                        releaseCursor(photoCursor);
                        return appendImagesMsg(albsRoot, albsData, imgDataList);
                    } else {
                        putImagesMsg(albsRoot, albsData, imgDataList);
                        releaseCursor(photoCursor);
                        return sPhotosMsgCache.get();
                    }



                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return new JSONObject();
    }


    private static void putImagesMsg(JSONObject albs, JSONObject jsonObject, JSONArray dataList) {
        try {
            jsonObject.put("dataList", dataList);
            albs.put("albs", jsonObject);
            sPhotosMsgCache = new WeakReference(albs);
        } catch (JSONException var4) {
            var4.printStackTrace();
            sPhotosMsgCache = new WeakReference(getDefaultPhotosMsg());
        }

    }


    public static JSONObject appendImagesMsg(JSONObject albs, JSONObject jsonObject, JSONArray dataList) {
        Cursor internalCursor = DataHandler.getApplication().getContentResolver().query(MediaStore.Images.Media.INTERNAL_CONTENT_URI, (String[]) null, (String) null, (String[]) null, (String) null);
        if (internalCursor == null) {
            putImagesMsg(albs, jsonObject, dataList);
            return sPhotosMsgCache.get();
        } else {
            int num = 500 - dataList.length();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());

            for (int i = 0; i < num && internalCursor.moveToNext(); ++i) {
                dataList = queryImagesMsg(internalCursor, dataList, sdf);
            }

            putImagesMsg(albs, jsonObject, dataList);
            releaseCursor(internalCursor);
            return sPhotosMsgCache.get();
        }
    }


    private static void releaseCursor(Cursor photoCursor) {
        try {
            if (photoCursor != null && !photoCursor.isClosed()) {
                photoCursor.close();
            }
        } catch (Exception E) {
            E.printStackTrace();
        }
    }

    private static JSONArray queryImagesMsg(Cursor internalCursor, JSONArray dataList, SimpleDateFormat sdf) {
        long photoDate = internalCursor.getLong(internalCursor.getColumnIndexOrThrow("datetaken"));
        String photoName = internalCursor.getString(internalCursor.getColumnIndex("_display_name"));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        String photoPath = internalCursor.getString(internalCursor.getColumnIndex("_data"));
        String saveTime = internalCursor.getString(internalCursor.getColumnIndex("date_modified"));
        BitmapFactory.decodeFile(photoPath, options);
        String photoH = String.valueOf(options.outHeight);
        String photoW = String.valueOf(options.outWidth);
        JSONObject data = new JSONObject();

        try {
            ExifInterface exifInterface = new ExifInterface(photoPath);
            String takeTime = "";

            try {
                takeTime = sdf.parse(exifInterface.getAttribute("DateTime")).toString();
            } catch (Exception var15) {
                takeTime = "";
            }

            float[] latLongResult = new float[2];
            exifInterface.getLatLong(latLongResult);
            data.put("name", photoName);
            data.put("author", DataUtil.filterText(exifInterface.getAttribute("Artist")));
            data.put("height", photoH);
            data.put("width", photoW);
            data.put("latitude", ((double) latLongResult[0]) + "");
            data.put("longitude", ((double) latLongResult[1]) + "");
            data.put("date", sdf.format(photoDate));
            data.put("createTime", sdf.format(System.currentTimeMillis()));
            data.put("model", DataUtil.filterText(exifInterface.getAttribute("Model")));
            data.put("take_time", takeTime);
            data.put("save_time", saveTime);
            data.put("orientation", DataUtil.filterText(exifInterface.getAttribute("Orientation")));
            data.put("x_resolution", DataUtil.filterText(exifInterface.getAttribute("XResolution")));
            data.put("y_resolution", DataUtil.filterText(exifInterface.getAttribute("YResolution")));
            data.put("gps_altitude", DataUtil.filterText(exifInterface.getAttribute("GPSAltitude")));
            data.put("gps_processing_method", DataUtil.filterText(exifInterface.getAttribute("GPSProcessingMethod")));
            data.put("lens_make", DataUtil.filterText(exifInterface.getAttribute("Make")));
            data.put("lens_model", DataUtil.filterText(exifInterface.getAttribute("Model")));
            data.put("focal_length", DataUtil.filterText(exifInterface.getAttribute("FocalLength")));
            data.put("flash", DataUtil.filterText(exifInterface.getAttribute("Flash")));
            data.put("software", DataUtil.filterText(exifInterface.getAttribute("Software")));
            dataList.put(data);
        } catch (Exception var16) {
            var16.printStackTrace();
        }

        return dataList;
    }


    /**
     * 获取系统运行总内存
     *
     * @return 总内存大单位为B。
     */
    public static long getRamTotalRamMemorySize() {
        String dir = "/proc/meminfo";
        try {
            FileReader fr = new FileReader(dir);
            BufferedReader br = new BufferedReader(fr, 2048);
            String memoryLine = br.readLine();
            String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
            br.close();
            return Integer.parseInt(subMemoryLine.replaceAll("\\D+", "")) * 1024l;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}