package com.vunke.videochat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

public class LinphoneStatusReceiver extends BroadcastReceiver {
    private static final String TAG = "LinphoneStatusReceiver";
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getStringExtra("action");
            if (!TextUtils.isEmpty(action)) {
                Log.i(TAG, "onReceive: get action:" + action);
                switch (action) {
                    case "reg_state":
                        Log.i(TAG, "onReceive: reg_state:"+intent.getStringExtra("data"));
                        break;
                    case "show_code":
                        Log.i(TAG, "onReceive  show_code: " + intent.getStringExtra("data"));
                        break;
                    case "show_version":
                        Log.i(TAG, "onReceive: show_version:"+intent.getStringExtra("data"));
                        break;
                    case "show_status":
                        Log.i(TAG, "onReceive: show_status:"+intent.getStringExtra("data"));
                        break;
                    default:
                        break;
                }
            }
        }
    }