package cdax.naindex;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

public class MediaLoader {

    protected static final String COLUMN_BUCKET_DISPLAY_NAME = "bucket_display_name";
    protected static final String ORDER_BY = MediaStore.MediaColumns.DATE_MODIFIED + " DESC";
    protected static final String COLUMN_DURATION = "duration";
    protected static final String COLUMN_BUCKET_ID = "bucket_id";
    protected static final String COLUMN_ORIENTATION = "orientation";
    /**
     * A list of which columns to return. Passing null will return all columns, which is inefficient.
     */
    protected static final String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT,
            COLUMN_DURATION,
            MediaStore.MediaColumns.SIZE,
            COLUMN_BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.DISPLAY_NAME,
            COLUMN_BUCKET_ID,
            MediaStore.MediaColumns.DATE_ADDED,
            COLUMN_ORIENTATION};
    protected static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");

    protected static final String COLUMN_COUNT = "count";
    /**
     * A list of which columns to return. Passing null will return all columns, which is inefficient.
     */
    protected static final String[] ALL_PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT,
            COLUMN_DURATION,
            MediaStore.MediaColumns.SIZE,
            COLUMN_BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.DISPLAY_NAME,
            COLUMN_BUCKET_ID,
            MediaStore.MediaColumns.DATE_ADDED,
            COLUMN_ORIENTATION,
            "COUNT(*) AS " + COLUMN_COUNT};
    static String TAG = "MediaLoader";

    public static boolean isQ() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }

    /**
     * 查询方式
     */
    public static boolean isWithAllQuery() {
        if (isQ()) {
            return true;
        } else {
            return true;
        }
    }

    public static void loadAllImage(Context context) {

        Log.d(TAG, "1======" + QUERY_URI.getPath());

        Log.d(TAG, "2======" + MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath());
        Log.d(TAG, "3======" + MediaStore.Images.Media.INTERNAL_CONTENT_URI.getPath());


        Cursor data = context.getContentResolver().query(MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                null, null
                , null, null);

        
        int count = data.getCount();
        Log.d(TAG, "count" + count);

//                getSelection(), getSelectionArgs(), getSortOrder());
    }

    protected static String[] getSelectionArgs() {
        return new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)};
    }

    protected static String getSortOrder() {
        return ORDER_BY;
    }


}
