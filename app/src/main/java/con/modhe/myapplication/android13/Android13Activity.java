package con.modhe.myapplication.android13;

import static android.Manifest.permission.MANAGE_EXTERNAL_STORAGE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

//import androidx.activity.result.ActivityResult;
//import androidx.activity.result.ActivityResultCallback;
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.ActivityResultRegistry;
//import androidx.activity.result.contract.ActivityResultContract;
//import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;

import java.util.Arrays;

import cdax.naindex.datalib.DataUtil;
import con.modhe.myapplication.R;

//https://jishuin.proginn.com/p/763bfbd76bb4
public class Android13Activity extends AppCompatActivity {


    public final String[] permissionList = {
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.NEARBY_WIFI_DEVICES,
            Manifest.permission.NEARBY_WIFI_DEVICES
    };

    public final int permissionListCode = 10;

    final String TAG = "dou";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_13);

    }

    ActivityResultLauncher<Intent> intentActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        Log.d(TAG, result.getResultCode() + "===========" + Activity.RESULT_OK);
    });

    public void onNewResult(View view) {
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        intentActivityResultLauncher.launch(intent);
    }

    public void onPermission13(View view) {
//requestPermissions();
        ActivityCompat.requestPermissions(this, permissionList, permissionListCode);
    }

    @RequiresApi(api = 33)
    public void onKillPermission13(View view) {
        revokeSelfPermissionsOnKill(Arrays.asList(permissionList));
        Log.d(TAG, "=======onKillPermission13====1");

//        context.revokeSelfPermissionOnKill();
//        revokeOwnPermissionsOnKill();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "=======onRequestPermissionsResult====000000000000000");

        if (requestCode == permissionListCode) {
            if (DataUtil.checkPermissionRationale(this, permissionList)) {
                Log.d(TAG, "=======onRequestPermissionsResult====1");
            } else {
                Log.d(TAG, "=======onRequestPermissionsResult====0");
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
