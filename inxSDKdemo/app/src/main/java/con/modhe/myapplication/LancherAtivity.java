package con.modhe.myapplication;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.appevents.AppEventsConstants;
import com.tbruyelle.rxpermissions2.RxPermissions;

import ai.advance.liveness.lib.GuardianLivenessDetectionSDK;
import ai.advance.liveness.lib.LivenessResult;
import ai.advance.liveness.lib.Market;
import ai.advance.liveness.sdk.activity.LivenessActivity;
import cdax.naindex.LocationInformation;
import cdax.naindex.mylibrary.FaceBookLog;
import cdax.naindex.mylibrary.FirebaseLog;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


/**
 * author Created by harrishuang on 2020/11/14.
 * email : huangjinping1000@163.com
 */
public class LancherAtivity extends AppCompatActivity {

    final int REQUEST_CODE_LIVENESS = 1000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lancher);
        initKeyType();
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
        FaceBookLog.getInstance().report(AppEventsConstants.EVENT_NAME_ACHIEVED_LEVEL);
    }

    public void onFirebase(View view) {
        FirebaseLog.getInstance().report("LancherAtivity");
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

    RxPermissions permissions = new RxPermissions(this);

    public void onGetAllData(View view) {

        permissions.request(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_SMS,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_WIFI_STATE
        ).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
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


    private void getJSON() {
//        DataManager dataManager = new DataManager(this);
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                //必须异步
//                Log.d("oksd", dataManager.getDeviceInfo().toString());
//            }
//        }.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_LIVENESS:
                String livenessId = LivenessResult.getLivenessId();
                Bitmap livenessBitmap = LivenessResult.getLivenessBitmap();
                String transactionId = LivenessResult.getTransactionId();
                boolean success = LivenessResult.isSuccess();
                String errorMsg = LivenessResult.getErrorMsg();
                String errorCode = LivenessResult.getErrorCode();
                if (LivenessResult.isSuccess()) {
//                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                    if (TextUtils.isEmpty(livenessId)) {
                        Toast.makeText(this, R.string.living_again, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //todo  livenessBitmap   操作这个图片
                } else {
                    Toast.makeText(this, LivenessResult.getErrorCode(), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void onLocation(View view) {


        permissions.request(
                Manifest.permission.ACCESS_COARSE_LOCATION
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
                Toast.makeText(LancherAtivity.this, latitude + "=========" + longitude, Toast.LENGTH_SHORT).show();
            }
        });
        locationInformation.startLoction();
    }
}
