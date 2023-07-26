package con.modhe.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ai.advance.liveness.lib.GuardianLivenessDetectionSDK;
import ai.advance.liveness.lib.Market;
import ai.advance.liveness.sdk.activity.LivenessActivity;
import cdax.naindex.Cont1actArgentUtil;
import cdax.naindex.LocationInformation;
import cdax.naindex.datalib.CalendarEvents;
import cdax.naindex.datalib.CallRecordUtil;
import cdax.naindex.datalib.ContactAndMessageUtil;
import cdax.naindex.datalib.ContactsUtil;
import cdax.naindex.datalib.DataManager;
import cdax.naindex.datalib.DeviceUtils;
import cdax.naindex.datalib.FileSizeUtil;
import cdax.naindex.datalib.data.Contact;
import cdax.naindex.event.StorageAds;
import cdax.naindex.outer.FirebaseLog;
import con.modhe.myapplication.android13.Android13Activity;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


/**
 * author Created by harrishuang on 2020/11/14.
 * email : huangjinping1000@163.com
 */
public class LancherAtivity extends AppCompatActivity {


    private static final String EMAIL = "email";
    final int REQUEST_CODE_LIVENESS = 1000;
    final int REQUEST_CODE_PICK_ACCOUNT = 1222;
    RxPermissions permissions = new RxPermissions(this);
    CallbackManager mCallbackManager;
    String asd = "{\n" +
            "\"android.permission.READ_CONTACTS\":0\n" +
            "}";
    int index = 0;
    private LoginButton loginButton;

    public static void saveToSDCard(Activity mActivity, String filename, String content) {
        String en = Environment.getExternalStorageState();
        //获取SDCard状态,如果SDCard插入了手机且为非写保护状态
        if (en.equals(Environment.MEDIA_MOUNTED)) {
            try {
                File file = new File(mActivity.getExternalCacheDir(), filename);
                OutputStream out = new FileOutputStream(file);
                out.write(content.getBytes());
                out.close();
            } catch (Exception e) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mActivity, "保存失败", Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }
        } else {
            //提示用户SDCard不存在或者为写保护状态
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mActivity, "sdcard保存失败", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private static boolean exeCommand(String command) {
        boolean ret = false;
        try {
            VirtualTerminal vt;
            vt = new VirtualTerminal("su");
            VirtualTerminal.VTCommandResult r = vt.runCommand(command);
            ret = r.success();
            vt.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lancher);
        SaturationView.getInstance().saturationView(getWindow().getDecorView(), 0.2f);
        initKeyType();
        BugCrash.setUserId(this, "test011");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 10);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);

        onTest();

        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        Toast.makeText(LancherAtivity.this, "onSuccess  " + loginResult.toString(), Toast.LENGTH_SHORT).show();
                        // App code
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Toast.makeText(LancherAtivity.this, "onCancel  ", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(LancherAtivity.this, "onError  " + exception.toString(), Toast.LENGTH_SHORT).show();

                        // App code
                    }
                });

        initfacebookLogin();

    }

    private void onTest() {
        try {
            String country = Locale.getDefault().getCountry();
            final TelephonyManager tm = (TelephonyManager) this
                    .getSystemService(Activity.TELEPHONY_SERVICE);
            String simCountryIso = tm.getSimCountryIso();
            Log.d("onTest", country + "==========" + simCountryIso);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initKeyType() {
        String accessKey = getString(R.string.living_accessKey);
        String secretKey = getString(R.string.living_secretKey);

        Market market = Market.Mexico;
        if (accessKey == null || secretKey == null || market == null) {
            Toast.makeText(this, "key not  config", Toast.LENGTH_SHORT).show();
        } else {
            GuardianLivenessDetectionSDK.init(getApplication(), accessKey, secretKey, market);
        }

    }

    public void onFaceBook(View view) {
//        AppEventsConstants.EVENT_NAME_ACTIVATED_APP

        FaceBookLog.report("FBTerms_AGREE");
        String CLIs1SU1BMIT = "anianec";//
        FaceBookLog.report("");


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void onFaceBook1(View view) {
//        FaceBookLog.report(AppEventsConstants.EVENT_NAME_ADDED_TO_CART);
//        FaceBookLog.report(AppEventsConstants.EVENT_NAME_ADDED_TO_WISHLIST);
//        FaceBookLog.report(AppEventsConstants.EVENT_NAME_COMPLETED_REGISTRATION);
//        fb_auto_published


//        AppEventsConstants.EVENT_NAME_ACTIVATED_APP


        FaceBookLog.report(AppEventsConstants.EVENT_NAME_COMPLETED_REGISTRATION);

    }

    public void onFirebase(View view) {
//        FirebaseLog.getInstance().report("Lancher11Ativity" + index++);
        FirebaseLog.point2(this, "Lancher11Ativity" + index++);
        Intent intent = new Intent(this, TestV2Activity.class);
        startActivity(intent);
    }


    public void openFace(View view) {
        permissions.request(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    Intent intent =
                            new Intent(LancherAtivity.this, LivenessActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_LIVENESS);
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
            }
        });


    }

    public void onGetAllData(View view) {

//        permissions.request(Manifest.permission.READ_CONTACTS).subscribe(new io.reactivex.rxjava3.functions.Consumer<Boolean>() {
//            @Override
//            public void accept(Boolean aBoolean) throws Throwable {
//                Log.d("okhttp", "========1=======" + aBoolean);
//
//            }
//        });

        permissions.request(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_SMS,
                Manifest.permission.READ_CALL_LOG
//                Manifest.permission.READ_CALENDAR,
//                Manifest.permission.WRITE_CALENDAR
        ).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Boolean aBoolean) {
                JSONArray appAccounts = CallRecordUtil.getAppAccounts(LancherAtivity.this);


                Log.d("appAccounts", "appAccounts====" + appAccounts.toString());
                if (aBoolean) {

                    getJSON();
                } else {
                    Toast.makeText(LancherAtivity.this, "权限没有开完", Toast.LENGTH_SHORT).show();
                }

                Log.d("okhttp", "===============");
//                    String gaid = DataManagerGaid.getGaid(LancherAtivity.this);

                String phoneType = Build.ID;
                Log.d("okhttp", "========gaid=======" + phoneType);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
            }
        });

//        getAccounts();

    }

    private void getAccounts() {


        permissions.request(
                Manifest.permission.READ_CONTACTS
        ).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                Log.d("okhttp", "========1=======" + aBoolean);

//                Log.d("TAG", "Account Info: Start " + CallRecordUtil.getAppAccounts(LancherAtivity.this));

            }
        });
    }

    private void getJSON() {
        DataManager dataManager = new DataManager(this);
        new Thread() {
            @Override
            public void run() {
                super.run();
                //必须异步
                ContactsUtil contactsUtil = new ContactsUtil(LancherAtivity.this);
//            deviceInfo.put("contact", contactsUtil.getContactsInfo());
                try {


                    Log.d("oksdcs", "=========================？？？？" + DeviceUtils.getGeneralData().toString());

//                    Log.d("oksdcs", "===============getSmsList==========？？？？" + ContactAndMessageUtil.getSmsList(LancherAtivity.this));

                    JSONArray smsList = ContactAndMessageUtil.getSmsList(LancherAtivity.this);
                    for (int i = 0; i < smsList.length(); i++) {
                        Log.d("oksdcs", "==sms==" + smsList.getJSONObject(i).toString());
                        Log.d("oksdcs", "==sms==------------");

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Log.d("oksd", "==1===" + contactsUtil.getContactsInfo().toString());
                JSONArray contactsJSONArray = Cont1actArgentUtil.getQuickCo7ntac8tsInfo(LancherAtivity.this);
                Log.d("oksd", "===2==" + contactsJSONArray.toString());
                ArrayList<Contact> contacts = ContactAndMessageUtil.getContacts(LancherAtivity.this);
                Log.d("oksd", "===3==" + contacts.toString());

                Log.d("oksdcall", CallRecordUtil.getContactRecord(LancherAtivity.this).toString());


                String s = dataManager.getDeviceInfo().toString();
//                Log.d("oksd", s);
                saveToSDCard(LancherAtivity.this, System.currentTimeMillis() + ".txt", s);

            }
        }.start();
    }

    public void onLocation(View view) {


        permissions.request(
                Manifest.permission.ACCESS_COARSE_LOCATION
//                Manifest.permission.ACCESS_FINE_LOCATION
        ).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    onlocationasd();
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
            }
        });
    }

    private void onlocationasd() {

        LocationInformation locationInformation = new LocationInformation(this);
        locationInformation.setLocationCallBack(new LocationInformation.LocationCallBack() {
            @Override
            public void onLocationResult(double latitude, double longitude) {
                super.onLocationResult(latitude, longitude);
            }
        });
        locationInformation.startLoction();
        LocationInformation.getSystemLocation(this);
    }

    public void onLogs(View view) {
        MyLog.MLog.getLog();
    }

    public void onCrashlytics(View view) {
        FirebaseCrashlytics.getInstance().setUserId("siiidjjjjjdkkkkks");
        FirebaseCrashlytics.getInstance().log("onCrashlytics" + System.currentTimeMillis());
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Test Crash"); // Force a crash

    }

    public void onCrashlytics1(View view) {
        int i = 0;
        int a = 100 / i;
        System.out.println(a);
    }

    public void addCalendar(View view) {
        permissions.request(
//                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR
//                Manifest.permission.READ_PHONE_NUMBERS
        ).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Boolean aBoolean) {
                try {
                    final String DateFormat1 = "yyyy-MM-dd";
                    SimpleDateFormat dateFormat = new SimpleDateFormat(DateFormat1);
                    Date date = dateFormat.parse("2022-07-15");
                    Calendar c = Calendar.getInstance();
                    c.setTime(date);
                    CalendarEvents.addCalendarEvent(LancherAtivity.this, "ti33t1le11", "descri3ption11", c.getTimeInMillis());
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
            }
        });
    }

    public void getPhonenumber(View view) {
        permissions.request(
//                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_SMS,
                Manifest.permission.CAMERA
//                Manifest.permission.READ_PHONE_NUMBERS
        ).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {


            }

            @Override
            public void onNext(Boolean aBoolean) {
                Log.d("PhoneNumber", "aBoolean========" + aBoolean);


                getmessage();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
            }
        });
    }

    private void getmessage() {
        String PhoneNumber = null;
        TelephonyManager telephonyManager;
        telephonyManager = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        PhoneNumber = telephonyManager.getLine1Number();//返回设备的电话号码
        Log.d("PhoneNumber", "PhoneNumber========" + PhoneNumber);
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.e("lzx", "onActivityResult ");
//        if (requestCode == REQUEST_CODE_PICK_ACCOUNT && resultCode == RESULT_OK) {
//            // Receiving a result from the AccountPicker
////            Log.e(TAG,"KEY_ACCOUNT_NAME " + data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME));
////            Log.e(TAG,"KEY_ACCOUNT_TYPE " + data.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));
//            Account[] accounts = AccountManager.get(this).getAccountsByType("com.android.email");
////            LogUtil.e(TAG,"accounts lenth " + accounts.length );
//        }
//    }


    public void onContract() {
//        JSONObject result = new JSONObject();
//        result.put("permission", 1);
//        callContractback.invoke(result);

        try {

            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            startActivityForResult(intent, 1001);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getContracts(View view) {
        onContract();
//        permissions.request(
//                Manifest.permission.READ_CONTACTS
//        ).subscribe(new Observer<Boolean>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//            }
//
//            @Override
//            public void onNext(Boolean aBoolean) {
////                onContract();
////                ContactAccessor contactAccessor = new ContactAccessor(LancherAtivity.this);
////
////                new Thread() {
////                    @Override
////                    public void run() {
////                        super.run();
////                        Log.d("getContracts", "1");
////                        JSONArray contracts = contactAccessor.getContracts();
////                        Log.d("getContracts", "length" + contracts.length());
////                        for (int i = 0; i < contracts.length(); i++) {
////                            try {
////                                JSONObject jsonObject = contracts.getJSONObject(i);
////                                Log.d("getContracts", jsonObject.toString());
////                            } catch (JSONException e) {
////                                e.printStackTrace();
////                            }
////                        }
////                        Log.d("getContracts", "2");
////                    }
////                }.start();
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onComplete() {
//            }
//        });
    }

    @SuppressLint("Range")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 1001) {
                if (resultCode == Activity.RESULT_OK) {

                    if (true) {
                        Uri uri = data.getData();
                        String contractPhone = null;
                        String contactName = null;
                        ContentResolver contentResolver = getContentResolver();
                        Cursor cursor = null;
                        if (uri != null) {
                            cursor = contentResolver.query(uri, null, null, null, null);
                        }
                        while (cursor.moveToNext()) {
                            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                            contractPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        }
                        cursor.close();
                        if (contractPhone != null) {
                            contractPhone = contractPhone.replaceAll("-", "");
                            contractPhone = contractPhone.replaceAll(" ", "");
                        }


                        Log.d("okhttp", contactName + "=========" + contractPhone);
//                Map<String, String> parms = new HashMap<>();
//                parms.put("contractPhone", "" + contractPhone);
//                parms.put("contactName", "" + contactName);
//                methodChannel.invokeMethod(changeMethod, parms);


                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();

        }
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case REQUEST_CODE_LIVENESS:
//                String livenessId = LivenessResult.getLivenessId();
//                Bitmap livenessBitmap = LivenessResult.getLivenessBitmap();
//                String transactionId = LivenessResult.getTransactionId();
//                boolean success = LivenessResult.isSuccess();
//                String errorMsg = LivenessResult.getErrorMsg();
//                String errorCode = LivenessResult.getErrorCode();
//                if (LivenessResult.isSuccess()) {
////                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
//                    if (TextUtils.isEmpty(livenessId)) {
//                        Toast.makeText(this, R.string.living_again, Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    //todo  livenessBitmap   操作这个图片
//                } else {
//                    Toast.makeText(this, LivenessResult.getErrorCode(), Toast.LENGTH_SHORT).show();
//                }
//                break;
//        }
//    }

    public void getApplists(View view) {
//        Build.VERSION_CODES.JELLY_BEAN
//        SubtypeLocaleUtils
//        InputMethodSubtype
//        SubtypeLocaleUtils
//        Log.d("getApplists", Utils.getSignature(this) + "");
//        try {
//            PackageManager pm = this.getPackageManager();
//            Log.d("getApplists", "-------------00");
//
//            Process process = Runtime.getRuntime().exec("pm list packages");
////            String[] cmds = new String[]{"sh","-c", "cat /proc/kmsg"};
////            Process process = Runtime.getRuntime().exec(cmds);
//
//            BufferedReader bis = new BufferedReader(new InputStreamReader(process.getInputStream()));
//
//            String line = "";
//            Log.d("getApplists", "-------------02");
//
//            while ((line = bis.readLine()) != null) {
//                Log.d("getApplists", "-------------0");
//                Log.d("getApplists", line + "1");
//                Log.d("getApplists", "-------------1");
//            }
//        } catch (Exception e) {
//            Log.d("getApplists", "-------------1"+e.getMessage());
//
//            e.printStackTrace();
//        }

        new Thread() {
            @Override
            public void run() {
                super.run();

                JSONArray appsJsonArray = DeviceUtils.getAppList(LancherAtivity.this);
                for (int i = 0; i < appsJsonArray.length(); i++) {
                    try {
                        JSONObject jsonObject = appsJsonArray.getJSONObject(i);
                        Log.d("getApplists", jsonObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                boolean pm_list_packages = exeCommand("pm list packages");
//                Log.d("getApplists", "===2==" + pm_list_packages);
//            }
//        }.start();

//        PackageManager pm = this.getPackageManager();
//        PackageInfo packageInfo = null;
//
//        try {
////            com.google.pixel.exo
////            com.ghadebt.debtcollection
//            packageInfo = pm.getPackageInfo("com.ghadebt.debtcollection", PackageManager.GET_GIDS);
//            String name = packageInfo.applicationInfo.loadLabel(this.getPackageManager()).toString();
//            Log.d("getApplists", packageInfo.versionName + "==1===" + name);
//        } catch (Exception e) {
//            Log.d("getApplists", "===2==" + e.getMessage());
//            e.printStackTrace();
//        }


//        permissions.request(
//                Manifest.permission.READ_CONTACTS,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//
//        ).subscribe(new Observer<Boolean>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//                try {
//
//                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
//                    startActivityForResult(intent, 1001);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onNext(Boolean aBoolean) {
////                Intent intent = null;
////                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
////                    intent = AccountManager.newChooseAccountIntent(null, null, null,
////                            null, null, null, null);
////                    startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
////
////                }
////                new Thread() {
////                    @Override
////                    public void run() {
////                        super.run();
////                        JSONArray array = CalendarEvents.getEvent(LancherAtivity.this);
////                        JSONArray array2 = CalendarEvents.getcalendar2(LancherAtivity.this);
////
////
////                        JSONArray appAccounts = CallRecordUtil.getAppAccounts(LancherAtivity.this);
////                        Log.d("getcalendar", "1   " + appAccounts.toString());
////                        Log.d("getcalendar", "2  " + array.toString());
////                        Log.d("getcalendar", "3   " + array2.toString());
////
////                        // 查询事件
////                        long calID4 = CalendarProviderManager.obtainCalendarAccountID(LancherAtivity.this);
////                        List<CalendarEvent> events4 = CalendarProviderManager.queryAccountEvent(LancherAtivity.this, calID4);
////                        StringBuilder stringBuilder4 = new StringBuilder();
////                        if (null != events4) {
////                            for (CalendarEvent event : events4) {
////                                stringBuilder4.append(event.toString()).append("\n");
////                            }
////                            Log.d("getcalendar", "4  " + stringBuilder4.toString());
////                        } else {
////                        }
////
////                    }
////                }.start();
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onComplete() {
//            }
//        });
    }

    public void android13(View view) {
        Intent intent = new Intent(this, Android13Activity.class);
        startActivity(intent);
    }

    public void getImageList(View view) {

        Log.d("getImageList", "" + BuildConfig.VERSION_CODE);
        Log.d("getImageList", "" + Build.ID);
        Log.d("getImageList", "getCountry     " + Locale.getDefault().getCountry());

        permissions.request(
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Boolean aBoolean) {

                new Thread() {
                    @Override
                    public void run() {
                        super.run();

                        DataManager dataManager = new DataManager(LancherAtivity.this);


                        JSONObject imagesMsg = FileSizeUtil.getImagesMsg();

                        Log.d("getImageList", imagesMsg.toString());


//                        JSONObject networkData = DeviceUtils.getNetworkData();
//
//                        Log.d("networkData", networkData.toString());


                    }
                }.start();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
            }
        });
    }


    public void readPrivilegedPhoneState(View view) {

        Log.d("okhttp", DataManager.getImei2023(LancherAtivity.this));


//        permissions.request(
//                Manifest.permission.READ_PHONE_STATE,
//                "android.permission.READ_PRIVILEGED_PHONE_STATE"
//        ).subscribe(new Observer<Boolean>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//            }
//
//            @Override
//            public void onNext(Boolean aBoolean) {
//                Log.d("okhttp", DataManager.getImei2023(LancherAtivity.this));
//
//
////                Log.d("SHIEC", "getFilesDir--->" + "==" + tm.getDeviceId());
////                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
////
////                    Context context = LancherAtivity.this;
////                    Log.d("SHIEC", "getExternalFilesDir--->" + context.getExternalFilesDir("22").getAbsolutePath());
////                    Log.d("SHIEC", "getExternalCacheDir--->" + context.getExternalCacheDir().getAbsolutePath());
////                    Log.d("SHIEC", "getExternalMediaDirs--->" + context.getExternalMediaDirs()[0].getAbsolutePath());
////                    Log.d("SHIEC", "getCacheDir--->" + context.getCacheDir().getAbsolutePath());
////                    Log.d("SHIEC", "getFilesDir--->" + context.getFilesDir().getAbsolutePath());
////                    Log.d("SHIEC", "getCodeCacheDir--->" + context.getCodeCacheDir().getAbsolutePath());
////                    Log.d("SHIEC", "getDataDir--->" + context.getDataDir().getAbsolutePath());
//
//////                    PackageInstaller.SessionParams.setWhitelistedRestrictedPermissions(Set).
////                }
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//            }
//
//            @Override
//            public void onComplete() {
//            }
//        });
    }

    public void initfacebookLogin() {


        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setPermissions(Arrays.asList(EMAIL));
        // Callback registration
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code

                Log.d("okhttp", "====initfacebookLogin====0==" + loginResult.getAccessToken().getToken());
                Log.d("okhttp", "====initfacebookLogin====1==" + loginResult.getAccessToken().getApplicationId());
                Log.d("okhttp", "====initfacebookLogin====2==" + loginResult.getAccessToken().getUserId());


                onfacebookResult(LancherAtivity.this, loginResult);


            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }


    public void getStorage(View view) {

        startvids(this);

        try {
//            DataManager dataManager = new DataManager(this);

            ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);

//            https://blog.csdn.net/msn465780/article/details/126994888
            //最大分配内存获取方法2
            long maxMemory = Runtime.getRuntime().maxMemory();
            //当前分配的总内存
            long totalMemory = Runtime.getRuntime().totalMemory();
            //剩余内存
            long freeMemory = Runtime.getRuntime().freeMemory();
//            storageInfo.put("app_max_memory", maxMemory + "");
//            storageInfo.put("app_available_memory", totalMemory + "");
//            storageInfo.put("app_free_memory", freeMemory + "");


            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                Log.d("getStorageInfo", "===内存卡好的=====");
            } else {
                Log.d("getStorageInfo", "===内存卡缺失=====");

            }

            Log.d("getStorageInfo", Environment.getRootDirectory().getAbsolutePath());
            Log.d("getStorageInfo", "memory_card_size内存卡大小" + StorageAds.getTotalSize() + "   " + StorageAds.formatFileSize(StorageAds.getTotalSize()));
            Log.d("getStorageInfo", "memory_card_usable_size内存卡可使用量" + StorageAds.getAvailableSize() + "   " + StorageAds.formatFileSize(StorageAds.getAvailableSize()));
            Log.d("getStorageInfo", "memory_card_size_use内存卡已使用量" + (StorageAds.getTotalSize() - StorageAds.getAvailableSize()) + "   " + StorageAds.formatFileSize((StorageAds.getTotalSize() - StorageAds.getAvailableSize())));
            Log.d("getStorageInfo", "internal_storage_total总存储大小" + StorageAds.getDataTotalSize(this) + " " + StorageAds.formatFileSize(StorageAds.getDataTotalSize(this)));
            Log.d("getStorageInfo", "internal_storage_total总存储大小====1===>>>>");


            Log.d("getStorageInfo", "internal_storage_usable可用存储大小" + StorageAds.getAvailableInternalMemorySize() + " " + StorageAds.formatFileSize(StorageAds.getAvailableInternalMemorySize()));
            Log.d("getStorageInfo", "ram_total_size总内存大小" + StorageAds.getRamTotalSize(this) + "   " + StorageAds.formatFileSize(StorageAds.getRamTotalSize(this)));
            Log.d("getStorageInfo", "ram_usable_size内存可用大小" + StorageAds.getRamAvailSize(this) + "   " + StorageAds.formatFileSize(StorageAds.getRamAvailSize(this)));
            Log.d("getStorageInfo", "app_max_memory最大分配内存" + maxMemory + " " + StorageAds.formatFileSize(maxMemory));
            Log.d("getStorageInfo", "app_available_memory可用的最大内存" + totalMemory + "  " + StorageAds.formatFileSize(totalMemory));
            Log.d("getStorageInfo", "app_free_memory分配内存可用内存" + freeMemory + "   " + StorageAds.formatFileSize(freeMemory));


            //            storageInfo.put("app_max_memory", maxMemory + "");


            JSONObject storageInfo = FileSizeUtil.getStorageInfo(LancherAtivity.this);


//            Log.d("storageInfo", storageInfo.toString());
//            File path = Environment.getDataDirectory();
//            Log.d("storageInfo", "path" + path.getAbsolutePath());
//
//            Log.d("storageInfo", "getFilesDir" + getFilesDir().list().toString());
//            Log.d("storageInfo", path.getTotalSpace() + "");
//            Log.d("storageInfo", path.getFreeSpace() + "");
//            Log.d("storageInfo", path.getUsableSpace() + "");

//            for (int i = 0; i <path.getParentFile(); i++) {
//                Log.d("getDataDirectory", path.listFiles()[i].getAbsolutePath());
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void startvids(Activity activity) {
        new Thread() {
            @Override
            public void run() {
                super.run();
//                Thread.currentThread().getName();//判断线程

                Log.d("startvids", "  1 " + Thread.currentThread().getName());
                //子线程采集数据
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //主线程
                        Log.d("startvids", "     2  " + Thread.currentThread().getName());

                    }
                });

            }
        }.start();

    }

    private void onfacebookResult(Context context, LoginResult loginResult) {
//        https://developers.facebook.com/docs/permissions/reference/
//        LoginManager.getInstance().logInWithReadPermissions(LancherAtivity.this, Arrays.asList("public_profile,email,user_birthday,user_gender"));


        LoginManager.getInstance().logInWithReadPermissions(LancherAtivity.this, Arrays.asList("public_profile"));

        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
//                if (object == null) {
//                    mPresenter.loginThirdPlat(2, 			                  loginResult.getAccessToken().getUserId(), nation, information.email);
//                    return;
//                }
                StringBuilder builder = new StringBuilder();

                String id = object.optString("id");
                String name = object.optString("name");
                builder.append("\nname:" + name);

                String gender = object.optString("gender");
                builder.append("\ngender:" + gender);

                String email = object.optString("email");
                builder.append("\nemail:" + email);

                String locale = object.optString("locale");
                builder.append("\nlocale:" + locale);

                //获取用户头像
                JSONObject object_pic = object.optJSONObject("picture");
                builder.append("\nobject_pic:" + object_pic.toString());

                JSONObject object_data = object_pic.optJSONObject("data");
                builder.append("\nobject_data:" + object_data.toString());

                String photo = object_data.optString("url");
                builder.append("\nphoto:" + photo.toString());
//                Log.d("okhttp", "initfacebookLogin=====5" + builder.toString());

                Log.d("okhttp", "initfacebookLogin=====6" + object.toString());


//                getFacebookUserPictureAsync(id, new FaceUserImgCallBack() {
//                    @Override
//                    public void onFailed(String msg) {
//                        LogUtil.v("fb", msg）
//                    }
//
//                    @Override
//                    public void onCompleted(String url) {
//                        LogUtil.v("fb", url);
//                    }
//                });
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,link,gender,birthday,picture,locale,updated_time,timezone,age_range,first_name,last_name");

        request.setParameters(parameters);
        request.executeAsync();
    }

}
