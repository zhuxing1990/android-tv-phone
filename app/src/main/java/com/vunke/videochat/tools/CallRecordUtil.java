package com.vunke.videochat.tools;

import android.content.Context;
import android.content.Intent;

import com.vunke.videochat.dao.CallRecordDao;
import com.vunke.videochat.db.CallRecord;
import com.vunke.videochat.receiver.CallRecordReceiver;

/**
 * Created by zhuxi on 2020/4/17.
 */

public class CallRecordUtil {
    public static void updateCallRecord(Context context, CallRecord callRecord){
        try{
            CallRecordDao.Companion.getInstance(context).insertData(callRecord);
            Intent intent = new Intent();
            intent.setAction(CallRecordReceiver.CALL_RECORD_ACTION);
            context.sendBroadcast(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
