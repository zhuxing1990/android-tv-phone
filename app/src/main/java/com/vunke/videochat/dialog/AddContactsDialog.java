package com.vunke.videochat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.vunke.videochat.R;
import com.vunke.videochat.login.UserInfoUtil;
import com.vunke.videochat.tools.UiUtil;

import java.io.Serializable;

/**
 * Created by zhuxi on 2020/3/1.
 */

public class AddContactsDialog implements Serializable {
    private static final String TAG = "AddContactsDialog";
    private Context context;
    private Dialog dialog;
    private RelativeLayout dialog_add_window;
    private View mDecor;
    private EditText add_usernameedit,add_phoneedit;
    private Button add_commit_but,add_cancel_but;
    private ImageView add_qrcode_image;
    private boolean isShow = false;
    public AddContactsDialog(Context context){
        this.context = context;

    }
    public AddContactsDialog builder(){
        mDecor = LayoutInflater.from(context).inflate(R.layout.dialog_addcontacts,null);
        dialog_add_window = mDecor.findViewById(R.id.dialog_add_window);
        add_usernameedit = mDecor.findViewById(R.id.add_usernameedit);
        add_phoneedit = mDecor.findViewById(R.id.add_phoneedit);
        add_commit_but = mDecor.findViewById(R.id.add_save_but);
        add_cancel_but = mDecor.findViewById(R.id.add_cancel_but);
        add_qrcode_image = mDecor.findViewById(R.id.add_qrcode_image);
        initQrCode();
        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(mDecor);
        add_phoneedit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN){
                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
                        if (!TextUtils.isEmpty(add_usernameedit.getText())&&!TextUtils.isEmpty(add_phoneedit.getText())){
                            add_commit_but.requestFocus();
                            return true;
                        }
                    }
                }
                return false;
            }
        });

        //        dialog_add_window.setLayoutParams(new FrameLayout.LayoutParams((int) (display
//                .getWidth() * 0.85), LinearLayout.LayoutParams.WRAP_CONTENT));
        return this;
    }

    private void initQrCode() {
        UserInfoUtil userInfoUtil = UserInfoUtil.getInstance(context);
        String userId = userInfoUtil.getUserId();
        Bitmap qrcodeImage = UiUtil.getQrcodeImage(userId);
        add_qrcode_image.setImageBitmap(qrcodeImage);
    }

    public AddContactsDialog setNameEditTextHint(CharSequence hint){
        if (!TextUtils.isEmpty(hint)){
            add_usernameedit.setHint(hint);
        }
        return this;
    }
    public Editable getNameEdit(){
        return  add_usernameedit.getText();
    }
    public AddContactsDialog setNameEdit(CharSequence charSequence){
        if (!TextUtils.isEmpty(charSequence)) {
            add_usernameedit.setText(charSequence);
            add_usernameedit.setSelection(charSequence.length());

        }
        return  this;
    }
    public AddContactsDialog setNameEditTextInputType(int type){
        add_usernameedit.setInputType(type);
        return this;
    }
    public AddContactsDialog AddNameTextWatcher(TextWatcher textWatcher){
        add_usernameedit.addTextChangedListener(textWatcher);
        return this;
    }

    public AddContactsDialog setPhoneEditTextHint(CharSequence hint){
        if (!TextUtils.isEmpty(hint)){
            add_phoneedit.setHint(hint);
        }
        return this;
    }
    public Editable getPhoenEdit(){
        return  add_phoneedit.getText();
    }
    public AddContactsDialog setPhoenEdit(CharSequence charSequence){
        if (!TextUtils.isEmpty(charSequence)){
            add_phoneedit.setText(charSequence);
            add_phoneedit.setSelection(charSequence.length());
        }
        return  this;
    }
    public AddContactsDialog setPhoneEditTextInputType(int type){
        add_phoneedit.setInputType(type);
        return this;
    }
    public AddContactsDialog AddPhoneTextWatcher(TextWatcher textWatcher){
        add_phoneedit.addTextChangedListener(textWatcher);
        return this;
    }
    public AddContactsDialog setPositiveButton(CharSequence text, final View.OnClickListener listener) {
        if (TextUtils.isEmpty(text)){
            add_commit_but.setText(R.string.save);
        }else{
            add_commit_but.setText(text);
        }
        add_commit_but.setOnClickListener(listener);
        return this;
    }
    public AddContactsDialog setNeutralButton(CharSequence text, final View.OnClickListener listener){
        if (TextUtils.isEmpty(text)){
            add_cancel_but.setText(R.string.cancel);
        }else{
            add_cancel_but.setText(text);
        }
        add_cancel_but.setOnClickListener(listener);
        return this;
    }
    private AddContactsDialog setNegativeButton(CharSequence text, final View.OnClickListener listener){
        if (TextUtils.isEmpty(text)){

        }else{

        }
        return this;
    }
    public boolean isShowing(){
        if (dialog==null){
            return false;
        }
        return dialog.isShowing();
    }
    public AddContactsDialog setCanceledOnTouchOutside(boolean b){
        dialog.setCanceledOnTouchOutside(b);
        return this;
    }
    public AddContactsDialog setCancelable(boolean b){
        dialog.setCancelable(b);
        return this;
    }
    public void show(){
        isShow = true;
        if (mDecor!=null&& mDecor.getVisibility()==View.GONE){
            mDecor.setVisibility(View.VISIBLE);
        }
        dialog.show();
    }
    public void cancel(){
        if (isShow){
            isShow = false;
            if (dialog!=null&&dialog.isShowing()){
                isShow = false;
                dialog.cancel();
            }
        }

    }
    public void hide(){
        if (mDecor != null) {
            mDecor.setVisibility(View.GONE);
        }
    }
}
