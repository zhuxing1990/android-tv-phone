package com.vunke.videochat.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.vunke.videochat.R;
import com.vunke.videochat.dao.ContactsDao;
import com.vunke.videochat.db.Contacts;
import com.vunke.videochat.db.ContactsTable;
import com.vunke.videochat.login.UserInfoUtil;
import com.vunke.videochat.manage.ContactsManage;
import com.vunke.videochat.receiver.ContactsReceiver;
import com.vunke.videochat.tools.UiUtil;
import com.vunke.videochat.tools.Utils;

/**
 * Created by zhuxi on 2020/12/2.
 */

public class AddContactActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "AddContactActivity";
    private ImageView add_qrcode_image;
    private EditText add_usernameedit,add_phoneedit;
    private Button add_commit_but,add_cancel_but;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addcontact);
        initView();
        initQrcode();
        initData(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initData(intent);
    }
    private long id=-1;
    private void initData(Intent intent) {
        if (intent!=null){
            if (intent.hasExtra(ContactsTable.INSTANCE.getUSER_NAME())){
                String userName = intent.getStringExtra(ContactsTable.INSTANCE.getUSER_NAME());
                add_usernameedit.setText(userName);
                add_usernameedit.setSelection(userName.length());
                Log.i(TAG, "initData: get old userName:"+userName);
            }
            if (intent.hasExtra(ContactsTable.INSTANCE.getPHONE())){
                String phone = intent.getStringExtra(ContactsTable.INSTANCE.getPHONE());
                add_phoneedit.setText(phone);
                add_phoneedit.setSelection(phone.length());
                Log.i(TAG, "initData: get old phone:"+phone);
            }
            if (intent.hasExtra(ContactsTable.INSTANCE.get_ID())){
                id = intent.getLongExtra(ContactsTable.INSTANCE.get_ID(),System.currentTimeMillis());
                Log.i(TAG, "initData: get old id:"+id);
            }
        }
    }

    private void initView() {
        add_qrcode_image = findViewById(R.id.add_qrcode_image);
        add_usernameedit = findViewById(R.id.add_usernameedit);
        add_phoneedit = findViewById(R.id.add_phoneedit);
        add_commit_but = findViewById(R.id.add_commit_but);
        add_cancel_but = findViewById(R.id.add_cancel_but);
        add_commit_but.setOnClickListener(this);
        add_cancel_but.setOnClickListener(this);
    }

    private void initQrcode() {
        UserInfoUtil userInfoUtil = UserInfoUtil.getInstance(this);
        String userId = userInfoUtil.getUserId();
        Bitmap qrcodeImage = UiUtil.getQrcodeImage(userId);
        add_qrcode_image.setImageBitmap(qrcodeImage);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_commit_but:
                saveContact();
                break;
            case R.id.add_cancel_but:
                finish();
                break;
        }
    }

    private void saveContact() {
      String name =  add_usernameedit.getText().toString();
      String phone =  add_phoneedit.getText().toString();
        if (TextUtils.isEmpty(name)){
            Toast.makeText(this,"请输入姓名",Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(phone)){
            Toast.makeText(this,"请输入号码",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Utils.isNumeric(phone)){
            Toast.makeText(this,"号码请输入数字",Toast.LENGTH_SHORT).show();
            return;
        }
        Contacts contacts = new Contacts();
        contacts.setUser_name(name);
        contacts.setPhone(phone);
        if (id!=-1){
            contacts.set_id(id);
        }else{
            contacts.set_id(System.currentTimeMillis());
        }
        ContactsDao.Companion.getInstance(this).saveContacts(getApplicationContext(),contacts);
        finish();
        ContactsManage.UploadContacts(this,contacts);
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode ==KeyEvent.KEYCODE_BACK){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
        SendBroad();
    }

    private void SendBroad() {
        Log.i(TAG, "SendBroad: ");
        try {
            Intent intent = new Intent();
            intent.setAction(ContactsReceiver.CONTACTS_ADD_ACTION);
            sendBroadcast(intent);
//            Intent intent2 = new Intent();
//            intent2.setAction(CallRecordReceiver.CALL_RECORD_ACTION);
//            sendBroadcast(intent2);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
