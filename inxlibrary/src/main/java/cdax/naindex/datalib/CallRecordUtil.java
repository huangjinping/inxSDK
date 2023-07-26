package cdax.naindex.datalib;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;


public class CallRecordUtil {

    public static JSONArray getContactRecord(Context context) {
        JSONArray arr = new JSONArray();
        Cursor cursor = null;
        try {
            Uri uri = CallLog.Calls.CONTENT_URI;
            String[] projection = {CallLog.Calls.DATE, // 日期
                    CallLog.Calls.NUMBER, // 号码
                    CallLog.Calls.TYPE, // 类型
                    CallLog.Calls.CACHED_NAME, // 名字
                    CallLog.Calls._ID, // id
                    CallLog.Calls.DURATION,
                    CallLog.Calls.NEW
            };
            cursor = context.getContentResolver()
                    .query(uri, projection, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);

//            cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, "LIMIT 1", null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                    String cachedName = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));// 缓存的名称与电话号码，如果它的存在
                    JSONObject obj = new JSONObject();
                    obj.put("id", cursor.getInt(cursor.getColumnIndex(CallLog.Calls._ID)));
                    if (TextUtils.isEmpty(cachedName)) {
                        obj.put("name", number);
                    } else {
                        obj.put("name", cachedName);
                    }
                    obj.put("number", number);
                    obj.put("date", cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE)));
                    obj.put("type", cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE)));
                    obj.put("duration", cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DURATION)));
                    obj.put("turnon", cursor.getInt(cursor.getColumnIndex(CallLog.Calls.NEW)));
                    arr.put(obj);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return arr;
    }


    public static JSONArray getAppAccounts(Context context) {
        JSONArray jsonArray = new JSONArray();
        try {
            Account[] accounts = AccountManager.get(context).getAccounts();

            if (accounts != null && accounts.length > 0) {
                for (Account account : accounts) {
                    String name = account.name;
                    String type = account.type;

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("name", name);
                    jsonObject.put("type", type);
                    jsonArray.put(jsonObject);
                }
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }
}
