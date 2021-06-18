package com.vunke.videochat.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Camera
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import com.vunke.videochat.R
import com.vunke.videochat.base.BaseConfig
import com.vunke.videochat.callback.TalkCallBack
import com.vunke.videochat.config.CallInfo
import com.vunke.videochat.dao.ContactsDao
import com.vunke.videochat.db.CallRecord
import com.vunke.videochat.db.CallRecordTable
import com.vunke.videochat.dialog.NotCameraDialog
import com.vunke.videochat.login.UserInfoUtil
import com.vunke.videochat.manage.BackgroundManage
import com.vunke.videochat.manage.TalkManage
import com.vunke.videochat.model.TalkBean
import com.vunke.videochat.service.LinphoneMiniManager
import com.vunke.videochat.tools.CallRecordUtil
import com.vunke.videochat.tools.FocusUtil
import com.vunke.videochat.tools.LinphoneMiniUtils
import com.vunke.videochat.tools.TimeUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_audio.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Created by zhuxi on 2019/11/20.
 */
class AudioActivity:AppCompatActivity(), View.OnClickListener{
    private val TAG = "AudioActivity"
    var instance:LinphoneMiniManager?=null
    var message:String="";
    private var mReceiver: MainActivityReceiver? = null
    var  firstCallTime :Long?=0;
    var dialog:NotCameraDialog?=null
    var callRecord: CallRecord = CallRecord()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio)
        firstCallTime = System.currentTimeMillis()
        instance = LinphoneMiniManager.getInstance()
        initData()
        registerBroad()
        initLinstener()
        startCallTimeTask()
        val numberOfCameras = Camera.getNumberOfCameras()
        Log.i(TAG, "get camera number:" + numberOfCameras)
        if (numberOfCameras == 0) {
//            NotCameraDialog(this).Builder().show()
            dialog = NotCameraDialog(this).Builder(this)
            dialog!!.show()
        }
        LinphoneMiniUtils.initEchoCancellation()
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume: ")
        val rootView = findViewById<View>(android.R.id.content)
        BackgroundManage.setBackground(this, rootView)
    }
    var timeOb:DisposableObserver<Long>?=null
    private fun startCallTimeTask() {
        timeOb = object :DisposableObserver<Long>(){
            override fun onComplete() {
                dispose()
            }

            override fun onNext(t: Long) {
                var callTime = System.currentTimeMillis() - firstCallTime!!;
                var getTime = getDateTimes(callTime);
                audio_calltime.setText(getTime)
            }

            override fun onError(e: Throwable) {
                dispose()
            }

        }
        Observable.interval(0,1,TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(timeOb!!)

    }

    /**
     * long 转时间
     * @param time
     * @back
     */
    fun getDateTimes(time: Long): String {
        val sdf = SimpleDateFormat("HH:mm:ss")
        sdf.setTimeZone(TimeZone.getTimeZone("GMT00:00"));
        return sdf.format(Date(time))
    }
    private fun stopCallTimeTask() {
        if (timeOb!=null){
            timeOb!!.dispose()
            timeOb=null
        }
    }
    private fun registerBroad() {
        mReceiver = MainActivityReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(BaseConfig.RECEIVE_UPB_CHANGE)
        intentFilter.addAction(BaseConfig.RECEIVE_MAIN_ACTIVITY)
        registerReceiver(mReceiver, intentFilter)
    }
    private fun initData() {
        var intent = intent;
        if (intent.hasExtra("message")){
            message = intent.getStringExtra("message")
            Log.i(TAG,"message:$message")
            try {
                if (!TextUtils.isEmpty(message)){
                    if (message.contains("<tel:")){
                        val data = message.split("<tel:")
                        for (i in data.indices) {
                            println(data[i])
                        }
                        val number = data[1].substring(0, data[1].indexOf(";"))
                        audio_phone.setText(number)
                        callRecord.call_phone = number
                        callRecord.call_name = number
                        var contactsList = ContactsDao.getInstance(this).queryPhone(number)
                        if (contactsList!=null&&contactsList.size!=0){
                            callRecord.call_name = contactsList.get(0).user_name
                            audio_phone.setText(callRecord.call_name)
                        }
                    }else{
                        val remoteAddress = instance!!.getmLinphoneCore().remoteAddress
                        Log.i(TAG, "initData: remoteAddress:$remoteAddress")
                        val userName = remoteAddress.userName
                        audio_phone.setText(userName)
                        val getDisplayName = remoteAddress.displayName
                        callRecord.call_phone = userName
                        callRecord.call_name = getDisplayName
                    }
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }else if (intent.hasExtra("number")){
            var number = intent.getStringExtra("number")
            audio_phone.setText(number)
            callRecord.call_phone = number
            callRecord.call_name = number
            var contactsList = ContactsDao.getInstance(this).queryPhone(number)
            if (contactsList!=null&&contactsList.size!=0){
                callRecord.call_name = contactsList.get(0).user_name
                audio_phone.setText(callRecord.call_name)
            }
        }
        if(intent.hasExtra(CallRecordTable.CALL_STATUS)){
            var call_status = intent.getStringExtra(CallRecordTable.CALL_STATUS)
            if (!TextUtils.isEmpty(call_status)){
                callRecord.call_status = call_status
            }else{
                callRecord.call_status = CallInfo.CALL_OUT
            }
        }
    }

    private fun initLinstener(){
        audio_hang_up.setOnClickListener(this)
        audio_mute.setOnClickListener(this)
        audio_hang_up.setOnFocusChangeListener(object :View.OnFocusChangeListener{
            override fun onFocusChange(v: View, hasFocus: Boolean) {
                FocusUtil.setFocus(hasFocus,v,applicationContext)
            }
        })
        audio_mute.setOnFocusChangeListener(object :View.OnFocusChangeListener{
            override fun onFocusChange(v: View, hasFocus: Boolean) {
                FocusUtil.setFocus(hasFocus,v,applicationContext)
            }
        })
        audio_hang_up.requestFocus()
    }

    private var isMute = false
    override fun onClick(v: View) {
        when(v.id){
            R.id.audio_hang_up->{
//                Toast.makeText(this,"挂断", Toast.LENGTH_SHORT).show()
                if (!callRecord.call_status.equals(CallInfo.CALL_IN)) {
                    callRecord.call_status = CallInfo.CALL_OUT
                }
                instance!!.hangUp()
                finish()
            }
            R.id.audio_mute->{
                isMute =!isMute
                var lc = instance!!.getmLinphoneCore()
                lc.muteMic(isMute)
                if (isMute){
                    Toast.makeText(this,"已静音",Toast.LENGTH_SHORT).show()
                    audio_mute.setBackgroundResource(R.mipmap.mute2)
                }else{
                    Toast.makeText(this,"已恢复",Toast.LENGTH_SHORT).show()
                    audio_mute.setBackgroundResource(R.mipmap.mute)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopCallTimeTask()
        unregisterReceiver(mReceiver)
        callRecord.call_time = System.currentTimeMillis().toString()
//        CallRecordDao.getInstance(this).insertData(callRecord)
        CallRecordUtil.updateCallRecord(this,callRecord);
        if ( null!= dialog  && dialog!!.isShow()){
            dialog!!.cancel()
        }
        var userInfoUtil = UserInfoUtil.getInstance(this)
        val userId= userInfoUtil.getUserId()
        var talkbean = TalkBean()
        talkbean.userId=userId
        talkbean.talkDuration= (System.currentTimeMillis()-firstCallTime!!)/1000
        talkbean.talkTime = TimeUtil.getDateTime(TimeUtil.dateFormatYMDHMS,firstCallTime!!)
        TalkManage.addConversationLog(talkbean,object:TalkCallBack{
            override fun onSuccess() {

            }

            override fun OnFailed() {

            }
        })
    }

    inner class MainActivityReceiver : BroadcastReceiver() {
        private val TAG = "LoginReceiver"
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.getStringExtra("action")
            if (!TextUtils.isEmpty(action)) {
                Log.i(TAG, "onReceive: get action:" + action)
                when (action) {
                    "reg_state" ->{
                        Log.i(TAG, "onReceive: reg_state:" + intent.getStringExtra("data"))

                    }
                    "show_code" -> {
                        Log.i(TAG, "onReceive  show_code: " + intent.getStringExtra("data"))
                    }
                    "show_version" ->{
                        Log.i(TAG, "onReceive: show_version:" + intent.getStringExtra("data"))
                    }
                    "show_status" -> {
                        Log.i(TAG, "onReceive: show_status:" + intent.getStringExtra("data"))
                        var data = intent.getStringExtra("data")
                        if (data.contains("Call terminated")){
//                            callRecord.call_status = CallInfo.CALL_RINGING
                            finish()
                        }else if (data.contains("You have missed 1 call.")){
                            if (callRecord.call_status!=CallInfo.CALL_IN){
                                callRecord.call_status = CallInfo.CALL_MISSED
                            }
                            finish()
                        }else if (data.contains("Request timeout.")){
                            if (callRecord.call_status!=CallInfo.CALL_IN) {
                                callRecord.call_status = CallInfo.CALL_MISSED
                            }
                            finish()
                        }else if(data.contains("Call released")){
//                            callRecord.call_status = CallInfo.CALL_ANSWER
                            finish()
                        }
                    }"usb_change" -> {
                    val status = intent.getStringExtra("status")
                    val numberOfCameras = Camera.getNumberOfCameras()
                    if(!TextUtils.isEmpty(status)){
                        when(status){
                            Intent.ACTION_MEDIA_CHECKING ->{
                                Log.e(TAG, "ACTION_MEDIA_CHECKING")
                            }
                            Intent.ACTION_MEDIA_MOUNTED ->{
                                Log.e(TAG, "ACTION_MEDIA_MOUNTED")
                                if (numberOfCameras > 0) {

                                }
                            }
                            Intent.ACTION_MEDIA_UNMOUNTED ->{
                                Log.e(TAG, "ACTION_MEDIA_UNMOUNTED")
                                Log.i(TAG, "get camera number:" + numberOfCameras)

                            }
                            UsbManager.ACTION_USB_DEVICE_DETACHED ->{
                                Log.e(TAG, "ACTION_USB_DEVICE_DETACHED")
                                if (numberOfCameras == 0) {
//                                        NotCameraDialog(context).Builder().show()
                                    dialog = NotCameraDialog(context).Builder(context)
                                    dialog!!.show()
                                }
                            }
                            UsbManager.ACTION_USB_DEVICE_ATTACHED ->{
                                Log.e(TAG, "ACTION_USB_DEVICE_ATTACHED")
                                val device_add = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                                if (device_add != null) {
                                    if (numberOfCameras > 0) {

                                    }
                                }
                            }
                            Intent.ACTION_MEDIA_EJECT ->{
                                Log.e(TAG, "ACTION_MEDIA_EJECT")
                            }else ->{

                        }
                        }
                    }
                }else -> {

                }
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            return false
        }
        return super.onKeyDown(keyCode, event)
    }
}