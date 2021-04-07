package com.vunke.videochat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.vunke.videochat.R;
import com.vunke.videochat.dialog.AddContactsDialog;

/**
 * Created by zhuxi on 2020/9/3.
 */

public class TestActivity extends AppCompatActivity {
    private static final String TAG = "TestActivity";
    private Button test_but;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        test_but= findViewById(R.id.test_but);
        test_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TestSelect();
            }
        });

    }
    AddContactsDialog dialog;
    private void TestSelect() {
//        String phone_number = "88160966";
////        SelectPhoneManage.fixedLineNumber(this,phone_number);
//        SelectPhoneManage.initTimeOut(this);
        Intent intent = new Intent(this,AddContactActivity.class);
        startActivity(intent);
    }


}
