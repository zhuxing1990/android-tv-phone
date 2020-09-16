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
import com.vunke.videochat.R
import com.vunke.videochat.base.BaseConfig
import com.vunke.videochat.config.CallInfo
import com.vunke.videochat.dao.ContactsDao
import com.vunke.videochat.db.CallRecord
import com.vunke.videochat.db.CallRecordTable
import com.vunke.videochat.dialog.NotCameraDialog
import com.vunke.videochat.manage.BackgroundManage
import com.vunke.videochat.service.LinphoneMiniManager
import com.vunke.videochat.tools.AudioUtil
import com.vunke.videochat.tools.CallRecordUtil
import com.vunke.videochat.tools.FocusUtil
import kotlinx.android.synthetic.main.activity_call_in.*

/**
 * Created by zhuxi on 2019/11/20.
 */
class  CallInActivity :AppCompatActivity(), View.OnClickListener{
    var TAG = "CallInActivity"
    var instance:LinphoneMiniManager?=null
    var message:String="";
    var callRecord:CallRecord = CallRecord()
    private var mReceiver: MainActivityReceiver? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_in)
        initLinstener()
        initData()
        instance = LinphoneMiniManager.getInstance()
        registerBroad()
        call_in_answer.requestFocus()
    }
    var dialog:NotCameraDialog?=null ;
    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume: ")
        val rootView = findViewById<View>(android.R.id.content)
        BackgroundManage.setBackground(this, rootView)

    }
    private fun initLinstener() {
        call_in_hang_up.setOnClickListener(this)
        call_in_answer.setOnClickListener(this)
        call_in_hang_up.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View, hasFocus: Boolean) {
                FocusUtil.setFocus(hasFocus, v, applicationContext)
            }
        })
        call_in_answer.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View, hasFocus: Boolean) {
                FocusUtil.setFocus(hasFocus, v, applicationContext)
            }
        })
    }

    private fun registerBroad() {
        mReceiver = MainActivityReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(BaseConfig.RECEIVE_UPB_CHANGE)
        intentFilter.addAction(BaseConfig.RECEIVE_MAIN_ACTIVITY)
        registerReceiver(mReceiver, intentFilter)
    }
    fun initData(){
        var intent = intent;
        if (intent.hasExtra("message")){
            message = intent.getStringExtra("message")
            Log.i(TAG,"message:$message")
            try {
                if (!TextUtils.isEmpty(message)){

                    if (message.contains("<tel")){
                        val data = message.split("<tel:")
//                        for (i in data.indices) {
//                            println(data[i])
//                        }
                        val number = data[1].substring(0, data[1].indexOf(";"))
                        audio_in_phone.setText(number)
                        callRecord.call_phone = number
                        callRecord.call_name = number
                        val contactsList = ContactsDao.getInstance(this).queryPhone(number)
                        if (contactsList!=null&&contactsList.size!=0){
                            callRecord.call_name = contactsList.get(0).user_name
                            audio_in_phone.setText(callRecord.call_name)
                        }
                    }
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
        if(intent.hasExtra(CallRecordTable.CALL_STATUS)){
            var call_status = intent.getStringExtra(CallRecordTable.CALL_STATUS)
            if (!TextUtils.isEmpty(call_status)){
                callRecord.call_status = call_status
            }else{
                callRecord.call_status = CallInfo.CALL_IN
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id){
            R.id.call_in_hang_up ->{
//                Toast.makeText(this,"挂断",Toast.LENGTH_SHORT).show()
                callRecord.call_status = CallInfo.CALL_MISSED
                if (instance!=null){
                    instance!!.hangUp()
                }
                finish()
            }
            R.id.call_in_answer ->{
//                Toast.makeText(this,"接听",Toast.LENGTH_SHORT).show()
//                callRecord.call_status = CallInfo.CALL_ANSWER
                try {
                    val numberOfCameras = Camera.getNumberOfCameras()
                    Log.i(TAG, "get camera number:" + numberOfCameras)
                    val hasMicroPhone = AudioUtil.hasMicroPhone(this)
                    if (numberOfCameras == 0&&hasMicroPhone == false) {
//                        NotCameraDialog(this).Builder().show()
                        Log.i(TAG,"not camera and not microphone")
                        dialog = NotCameraDialog(this)
                        dialog!!.Builder(this).show()
                    }else if (numberOfCameras==0 && hasMicroPhone == true){
                        instance!!.lilin_jie()
                        Log.i("call_in_answer","获取摄像头失败，自动转语音")
                        var intent = Intent(this@CallInActivity, AudioActivity::class.java)
                        if (!TextUtils.isEmpty(message)){
                            intent.putExtra("message",message)
                        }
                        intent.putExtra(CallRecordTable.CALL_STATUS,CallInfo.CALL_IN)
                        startActivity(intent)
                        finish()
                    }else{
                        instance!!.lilin_jie()
                        if (instance!!.lilin_getVideoEnabled()) {//启动视频
                            Log.i("call_in_answer","接视频")
                            var intent = Intent(this@CallInActivity, VideoActivity::class.java)
                            if (!TextUtils.isEmpty(message)){
                                intent.putExtra("message",message)
                            }
                            intent.putExtra(CallRecordTable.CALL_STATUS,CallInfo.CALL_IN)
                            startActivity(intent)
                            finish()
                        }else{
                            Log.i("call_in_answer","接语音")
                            var intent = Intent(this@CallInActivity, AudioActivity::class.java)
                            if (!TextUtils.isEmpty(message)){
                                intent.putExtra("message",message)
                            }
                            intent.putExtra(CallRecordTable.CALL_STATUS,CallInfo.CALL_IN)
                            startActivity(intent)
                            finish()
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, e.message)
                }catch (e:NullPointerException){
                    Log.e(TAG, e.message)
                }catch (e:Exception){
                    Log.e(TAG, e.message)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG,"onDestroy")
        callRecord.call_time = System.currentTimeMillis().toString()
        if(callRecord.call_status != CallInfo.CALL_IN){
            CallRecordUtil.updateCallRecord(this,callRecord);
        }
        if (null!=dialog&&dialog!!.isShow()){
            dialog!!.cancel()
        }
//        CallRecordDao.getInstance(this).insertData(callRecord)
        unregisterReceiver(mReceiver)
    }
    inner class MainActivityReceiver : BroadcastReceiver() {
        private val TAG = "LoginReceiver"
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.getStringExtra("action")
            if (!TextUtils.isEmpty(action)) {
                android.util.Log.i(TAG, "onReceive: get action:" + action)
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
                            callRecord.call_status = CallInfo.CALL_MISSED
                            finish()
                        }else if(data.contains("Call released")){
                            callRecord.call_status = CallInfo.CALL_MISSED
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
                                    dialog = NotCameraDialog(context)
                                    dialog!!.Builder(context).show()
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
                }
                    else -> {

                    }
                }
            }
        }
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            return false;
        }
        return super.onKeyDown(keyCode, event)
    }
}