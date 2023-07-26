package cdax.naindex.datalib;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * author Created by harrishuang on 5/8/21.
 * email : huangjinping1000@163.com
 */
public class CalendarEvents {
    public static final String[] EVENT_PROJECTION = new String[]{
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
            CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
    };
    private static String CALENDER_EVENT_URL = "content://com.android.calendar/events";
    private static TelephonyManager mTm;
    private static String CALANDER_URL = "content://com.android.calendar/calendars";
    private static String CALANDER_EVENT_URL = "content://com.android.calendar/events";
    private static String CALANDER_REMIDER_URL = "content://com.android.calendar/reminders";
    private static String CALENDARS_NAME = "test";
    private static String CALENDARS_ACCOUNT_NAME = "test@gmail.com";
    private static String CALENDARS_ACCOUNT_TYPE = "com.android.exchange";
    private static String CALENDARS_DISPLAY_NAME = "测试账户";

    public static JSONArray getcalendar2(Context context) {
        JSONArray array = new JSONArray();
        ContentResolver cr = context.getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) AND ("
                + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?))";
        String[] selectionArgs = new String[]{"953195247@qq.com", "com.google",
                "953195247@qq.com"};
//        CalendarContract.Calendars

//        Log.d("columnNames", "====="+);

// Submit the query and get a Cursor object back.
        Gson gson = new Gson();
        Cursor cur = cr.query(uri, null, null, null, null);
        while (cur.moveToNext()) {
            JSONObject json = new JSONObject();

            String[] columnNames = cur.getColumnNames();

            try {
                String _ID = cur.getString(cur.getColumnIndex(CalendarContract.Calendars._ID));
                String accountName = cur.getString(cur.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME));
                String displayName = cur.getString(cur.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME));
                String ownerAccount = cur.getString(cur.getColumnIndex(CalendarContract.Calendars.OWNER_ACCOUNT));
                
                json.put("_ID", _ID);
                json.put("accountName", accountName);
                json.put("displayName", displayName);
                json.put("ownerAccount", ownerAccount);
                array.put(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("columnNames", gson.toJson(columnNames));

        }
        Log.d("columnNames", array.toString());


        return array;
    }


    public static JSONArray getEvent(Context context) {
        JSONArray array = new JSONArray();
        ContentResolver cr = context.getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;

//        final String[] EVENT_PROJECTION = new String[]{
//                CalendarContract.Events._ID,                           // 0
//                CalendarContract.Events.TITLE,                  // 1
//                CalendarContract.Events.DESCRIPTION,         // 2
//                CalendarContract.Events.EVENT_LOCATION                  // 3
//        };

// Submit the query and get a Cursor object back.
        Gson gson = new Gson();
        Cursor cur = cr.query(uri, null, null, null, null);
        while (cur.moveToNext()) {
            JSONObject json = new JSONObject();

            String[] columnNames = cur.getColumnNames();

            try {
                String _id = cur.getString(cur.getColumnIndex(CalendarContract.Events._ID));
                String CALENDAR_ID = cur.getString(cur.getColumnIndex(CalendarContract.Events.CALENDAR_ID));


                String title = cur.getString(cur.getColumnIndex(CalendarContract.Events.TITLE));
                String description = cur.getString(cur.getColumnIndex(CalendarContract.Events.DESCRIPTION));
                String guestsCanSeeGuests = cur.getString(cur.getColumnIndex(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS));


                json.put("_id", _id);
                json.put("CALENDAR_ID", CALENDAR_ID);
                json.put("title", title);
                json.put("description", description);
                json.put("guestsCanSeeGuests", guestsCanSeeGuests);

                array.put(json);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("columnNames", gson.toJson(columnNames));

        }
        Log.d("columnNames", array.toString());


        return array;
    }


    public static JSONArray getCalendar(Context context) {
        String startTime = "";
        String endTime = "";
        String eventTitle = "";
        String description = "";
        String location = "";
        JSONArray arr = new JSONArray();
        try {
            Uri iUri = CalendarContract.Events.CONTENT_URI;
            if (iUri == null) {
                iUri = Uri.parse("content://com.android.calendar/events");
            }
            Cursor eventCursor = context.getContentResolver().query(iUri, null,
                    null, null, null);
            while (eventCursor.moveToNext()) {
                JSONObject json = new JSONObject();
                eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title"));
                description = eventCursor.getString(eventCursor.getColumnIndex("description"));
                try {
                    if (eventCursor.getString(eventCursor.getColumnIndex("eventLocation")) != null) {
                        location = eventCursor.getString(eventCursor.getColumnIndex("eventLocation"));
                    }
                    if (eventCursor.getString(eventCursor.getColumnIndex("dtstart")) != null) {
                        startTime = timeStampDate(Long.parseLong(eventCursor.getString(eventCursor.getColumnIndex("dtstart"))));
                    }
                    if (eventCursor.getString(eventCursor.getColumnIndex("dtend")) != null) {
                        endTime = timeStampDate(Long.parseLong(eventCursor.getString(eventCursor.getColumnIndex("dtend"))));
                    }
                    json.put("eventTitle", eventTitle);
                    json.put("description", description);
                    json.put("location", location);
                    json.put("startTime", startTime);
                    json.put("endTime", endTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                arr.put(json);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return arr;
    }

    private static String timeStampDate(long time) {
        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(time));
    }

    public static JSONArray getcalendar(Context context) {
        String startTime = "";
        String endTime = "";
        String eventTitle = "";
        String description = "";
        String location = "";


        JSONArray arr = new JSONArray();
        try {
            Cursor eventCursor = context.getContentResolver().query(Uri.parse(CALENDER_EVENT_URL), null,
                    null, null, null);
            while (eventCursor.moveToNext()) {
                JSONObject json = new JSONObject();
                eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title"));
                description = eventCursor.getString(eventCursor.getColumnIndex("description"));
                location = eventCursor.getString(eventCursor.getColumnIndex("eventLocation"));
                startTime = timeStamp2Date(Long.parseLong(eventCursor.getString(eventCursor.getColumnIndex("dtstart"))));
                endTime = timeStamp2Date(Long.parseLong(eventCursor.getString(eventCursor.getColumnIndex("dtend"))));
                try {
                    json.put("eventTitle", eventTitle);
                    json.put("description", description);
                    json.put("location", location);
                    json.put("startTime", startTime);
                    json.put("endTime", endTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                arr.put(json);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return arr;
    }

    /**
     * 时间戳转换为字符串
     *
     * @param time:时间戳
     * @return
     */
    private static String timeStamp2Date(long time) {
        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(time));
    }

    private static int checkCalendarAccount(Context context) {
        Cursor userCursor = context.getContentResolver().query(Uri.parse(CALANDER_URL), null, null, null, null);
        try {
            if (userCursor == null)//查询返回空值
                return -1;
            int count = userCursor.getCount();
            if (count > 0) {//存在现有账户，取第一个账户的id返回
                userCursor.moveToFirst();
                return userCursor.getInt(userCursor.getColumnIndex(CalendarContract.Calendars._ID));
            } else {
                return -1;
            }
        } finally {
            if (userCursor != null) {
                userCursor.close();
            }
        }
    }

    private static long addCalendarAccount(Context context) {
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(CalendarContract.Calendars.NAME, CALENDARS_NAME);

        value.put(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE);
        value.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDARS_DISPLAY_NAME);
        value.put(CalendarContract.Calendars.VISIBLE, 1);
        value.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.BLUE);
        value.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        value.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        value.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(CalendarContract.Calendars.OWNER_ACCOUNT, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0);

        Uri calendarUri = Uri.parse(CALANDER_URL);
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE)
                .build();

        Uri result = context.getContentResolver().insert(calendarUri, value);
        long id = result == null ? -1 : ContentUris.parseId(result);
        return id;
    }


    private static int checkAndAddCalendarAccount(Context context) {
        int oldId = checkCalendarAccount(context);
        if (oldId >= 0) {
            return oldId;
        } else {
            long addId = addCalendarAccount(context);
            if (addId >= 0) {
                return checkCalendarAccount(context);
            } else {
                return -1;
            }
        }
    }

    public static void addCalendarEvent(Context context, String title, String description, long beginTime) {


        Log.d("okhttp", description + "===" + beginTime);


        // 获取日历账户的id
        int calId = checkAndAddCalendarAccount(context);
        if (calId < 0) {
            // 获取账户id失败直接返回，添加日历事件失败
            return;
        }

        ContentValues event = new ContentValues();
        event.put("title", title);
        event.put("description", description);
        // 插入账户的id
        event.put("calendar_id", calId);

        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(beginTime);//设置开始时间
        long start = mCalendar.getTime().getTime();
        mCalendar.setTimeInMillis(start + 60);//设置终止时间
        long end = mCalendar.getTime().getTime();

        event.put(CalendarContract.Events.DTSTART, start);
        event.put(CalendarContract.Events.DTEND, end);
        event.put(CalendarContract.Events.HAS_ALARM, 1);//设置有闹钟提醒
        event.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Shanghai");  //这个是时区，必须有，
        //添加事件
        Uri newEvent = context.getContentResolver().insert(Uri.parse(CALANDER_EVENT_URL), event);
        if (newEvent == null) {
//            ToastUtils.e(context,"添加日历事件失败直接返回").show();
            // 添加日历事件失败直接返回
            return;
        }
        Log.d("okhttp", description);

        //事件提醒的设定
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Reminders.EVENT_ID, ContentUris.parseId(newEvent));
        // 提前10分钟有提醒
        values.put(CalendarContract.Reminders.MINUTES, 10);
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        Uri uri = context.getContentResolver().insert(Uri.parse(CALANDER_REMIDER_URL), values);
        if (uri == null) {
            //
//            ToastUtils.e(context,"添加闹钟提醒失败直接返回").show();

            return;
        }
        Log.d("okhttp", description);

    }

    public static void deleteCalendarEvent(Context context, String title) {
        Cursor eventCursor = context.getContentResolver().query(Uri.parse(CALANDER_EVENT_URL), null, null, null, null);
        try {
            if (eventCursor == null)//查询返回空值
                return;
            if (eventCursor.getCount() > 0) {
                //遍历所有事件，找到title跟需要查询的title一样的项
                for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
                    String eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title"));
                    if (!TextUtils.isEmpty(title) && title.equals(eventTitle)) {
                        int id = eventCursor.getInt(eventCursor.getColumnIndex(CalendarContract.Calendars._ID));//取得id
                        Uri deleteUri = ContentUris.withAppendedId(Uri.parse(CALANDER_EVENT_URL), id);
                        int rows = context.getContentResolver().delete(deleteUri, null, null);
                        if (rows == -1) {
                            //事件删除失败
                            return;
                        }
                    }
                }
            }
        } finally {
            if (eventCursor != null) {
                eventCursor.close();
            }
        }
    }

}
