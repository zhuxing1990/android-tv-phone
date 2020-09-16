package com.vunke.videochat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.vunke.videochat.callback.ContactsCallBack;

/**
 * Created by zhuxi on 2020/8/27.
 */

public class ContactsReceiver extends BroadcastReceiver {
    private static final String TAG = "ContactsReceiver";
    private ContactsCallBack contactsCallBack;
    public static final String CONTACTS_ADD_ACTION = "contacts_add_action";

    public  ContactsReceiver(ContactsCallBack contactsCallBack) {
        this.contactsCallBack = contactsCallBack;
    }
    public void setContactsCallBack(ContactsCallBack contactsCallBack) {
        this.contactsCallBack = contactsCallBack;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (!TextUtils.isEmpty(action)){
            if (action.equals(CONTACTS_ADD_ACTION)){
                if (contactsCallBack!=null){
                    contactsCallBack.onUpdate();
                }
            }
        }
    }
}
