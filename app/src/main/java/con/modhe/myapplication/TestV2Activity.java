package con.modhe.myapplication;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import cdax.naindex.outer.FirebaseLog;

public class TestV2Activity extends AppCompatActivity {
    int index = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testv2);

//        FirebaseLog.getInstance().report("TestV2Activity" + index++);

        FirebaseLog.point2(this, "TestV2Activity" + index++);

    }
}
