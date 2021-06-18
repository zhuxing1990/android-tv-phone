package com.vunke.videochat.ui;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.hardware.usb.UsbManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vunke.videochat.R;
import com.vunke.videochat.base.BaseConfig;
import com.vunke.videochat.callback.CallOverCallBack;
import com.vunke.videochat.callback.TalkCallBack;
import com.vunke.videochat.config.CallInfo;
import com.vunke.videochat.dao.ContactsDao;
import com.vunke.videochat.db.CallRecord;
import com.vunke.videochat.db.CallRecordTable;
import com.vunke.videochat.db.Contacts;
import com.vunke.videochat.dialog.CallTimeDialog;
import com.vunke.videochat.dialog.NotCameraDialog;
import com.vunke.videochat.login.UserInfoUtil;
import com.vunke.videochat.manage.TalkManage;
import com.vunke.videochat.model.TalkBean;
import com.vunke.videochat.service.LinphoneMiniManager;
import com.vunke.videochat.tools.AudioUtil;
import com.vunke.videochat.tools.CallRecordUtil;
import com.vunke.videochat.tools.CameraUtil;
import com.vunke.videochat.tools.LinphoneMiniUtils;
import com.vunke.videochat.tools.TimeUtil;

import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.VideoSize;
import org.linphone.mediastream.video.AndroidVideoWindowImpl;
import org.linphone.mediastream.video.capture.hwconf.AndroidCameraConfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class VideoActivity extends AppCompatActivity implements View.OnClickListener{
	private static final String TAG = "VideoActivity";
	private VideoActivityReceiver mReceiver;

	private SurfaceView mRenderingView,mPreviewView;
	private AndroidVideoWindowImpl mAndroidVideoWindow;
	private ImageView video_hang_up,video_mute,video_qiev;
	//	private Button video_speaker;
	private LinphoneMiniManager instance;
	private TextView video_mute_text;
	private RelativeLayout call_video_r3;
	private CallRecord callRecord;
	private NotCameraDialog dialog;
	private ImageView video_switch;
	private RelativeLayout callvideo_rl1,callvideo_rl2;
	private TextView video_callTime;
	private long firstCallTime=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video);
		instance = LinphoneMiniManager.getInstance();
		initData();
		initCamera();
		init();
		firstCallTime = System.currentTimeMillis();
		startCallTimeTask();
		initCallTime();
//		resizePreview();
		initAlphaAnimation1();
		initAlphaAnimation2();
		initTimerOut();
		registerBroadCast();
		switchTime = System.currentTimeMillis();
		initWindow();
		LinphoneMiniUtils.initEchoCancellation();
	}

	DisposableObserver<Long> timeOb=null;
	private void startCallTimeTask() {
		timeOb = new DisposableObserver<Long>(){
			@Override
			public void onNext(Long aLong) {
				long callTime = System.currentTimeMillis() - firstCallTime;
				String getTime = getDateTimes(callTime);
				video_callTime.setText(getTime);
			}

			@Override
			public void onError(Throwable e) {
				dispose();
			}

			@Override
			public void onComplete() {
				dispose();

			}

		};
		Observable.interval(0,1,TimeUnit.SECONDS)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(timeOb);

	}
	private void stopCallTimeTask() {
		if (timeOb!=null){
			if (!timeOb.isDisposed()){
				timeOb.dispose();
				timeOb=null;
			}
		}
	}
	private String  getDateTimes(Long time){
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT00:00"));
		return sdf.format(new Date(time));
	}
	private void initCamera() {
		int numberOfCameras = Camera.getNumberOfCameras();
		Log.i(TAG,"get camera number:"+numberOfCameras);
		boolean hasMicroPhone = AudioUtil.hasMicroPhone(this);
		if (numberOfCameras==0 && hasMicroPhone == false){
			Log.i(TAG, "initCamera: not camera and not microphone");
			dialog = new NotCameraDialog(this);
			dialog.Builder(this).show();
		}else if (numberOfCameras==0 &&hasMicroPhone == true){
			Log.i(TAG, "initCamera: has microphone");
			qiev();
		}else{
			CameraUtil.initCamera(instance);
		}
	}
	private String message;
	private void initData() {
		callRecord = new CallRecord();
		Intent intent = getIntent();
		callRecord.call_phone="";
		callRecord.call_name ="";
		if (intent.hasExtra("message")){
			message = intent.getStringExtra("message");
			Log.i(TAG,"message:"+message);
			try {
				if (!TextUtils.isEmpty(message)){
					if (message.contains("<tel:")){
						String[] data = message.split("<tel:");
//						for (i in data.indices) {
//							println(data[i])
//						}
						String number = data[1].substring(0, data[1].indexOf(";"));
						callRecord.call_phone = number;
						callRecord.call_name = number;
						List<Contacts> contactsList = ContactsDao.Companion.getInstance(this).queryPhone(number);
						if (contactsList!=null&&contactsList.size()!=0){
							callRecord.call_name = contactsList.get(0).getUser_name();
						}
					}else{
						LinphoneAddress remoteAddress = instance.getmLinphoneCore().getRemoteAddress();
						Log.i(TAG, "initData: remoteAddress:"+remoteAddress);
						String userName = remoteAddress.getUserName();
						String getDisplayName = remoteAddress.getDisplayName();
						callRecord.call_phone = userName;
						callRecord.call_name = getDisplayName;
					}
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}else if (intent.hasExtra("number")){
			String  number = intent.getStringExtra("number");
			callRecord.call_phone = number;
			callRecord.call_name = number;
			List<Contacts> contactsList = ContactsDao.Companion.getInstance(this).queryPhone(number);
			if (contactsList!=null&&contactsList.size()!=0){
				callRecord.call_name = contactsList.get(0).getUser_name();
			}
		}
		if(intent.hasExtra(CallRecordTable.INSTANCE.getCALL_STATUS())){
			String call_status = intent.getStringExtra(CallRecordTable.INSTANCE.getCALL_STATUS());;
			if (!TextUtils.isEmpty(call_status)){
				callRecord.call_status = call_status;
			}else{
				callRecord.call_status = CallInfo.INSTANCE.getCALL_OUT();
			}
		}
	}
	private void init() {
		mRenderingView = findViewById(R.id.id_video_rendering);
		mPreviewView = findViewById(R.id.id_video_preview);
		mPreviewView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		video_hang_up = findViewById(R.id.video_hang_up);
		video_mute= findViewById(R.id.video_mute);
		video_callTime= findViewById(R.id.video_callTime);
//		video_speaker = findViewById(R.id.video_speaker);
		video_qiev =findViewById(R.id.video_qiev);
		video_hang_up.setOnClickListener(this);
		video_mute.setOnClickListener(this);
//		video_speaker.setOnClickListener(this);
		video_qiev.setOnClickListener(this);

		callvideo_rl1 = findViewById(R.id.callvideo_rl1);
		callvideo_rl2 = findViewById(R.id.callvideo_rl2);

		video_hang_up.requestFocus();
		video_hang_up.bringToFront();
		video_mute_text = findViewById(R.id.video_mute_text);
		call_video_r3 = findViewById(R.id.call_video_r3);

		video_switch = findViewById(R.id.video_switch);
		video_switch.setOnClickListener(this);
	}


	private CallTimeDialog callTimeDialog;
	private DisposableObserver<Long> callTimeObserver;
	private void initCallTime() {
		callTimeDialog = new CallTimeDialog(this);
		callTimeDialog.setConfirmOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				HangUp();
				callTimeDialog.cancel();
			}
		});
		callTimeDialog.setCallOverCallBack(new CallOverCallBack(){

			@Override
			public void onOver() {
				HangUp();
			}
		});
		clearCallTimeOut();
		callTimeObserver = new DisposableObserver<Long>() {
			@SuppressLint("NewApi")
			@Override
			public void onNext(Long aLong) {
				if (!isDestroyed()) {
					callTimeDialog.show();
					onComplete();
				}
			}

			@Override
			public void onError(Throwable e) {
				dispose();
			}

			@Override
			public void onComplete() {
				dispose();
			}
		};
		Observable.interval(1, TimeUnit.HOURS)
//		Observable.interval(30, TimeUnit.SECONDS)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(callTimeObserver);
	}

	private void initWindow() {
		fixZOrder(mRenderingView, mPreviewView);
		mAndroidVideoWindow = new AndroidVideoWindowImpl(mRenderingView, mPreviewView, new AndroidVideoWindowImpl.VideoWindowListener() {
			public void onVideoRenderingSurfaceReady(AndroidVideoWindowImpl vw, SurfaceView surface) {
				Log.i(TAG, "onVideoRenderingSurfaceReady: ");
				synchronized(vw.getSurface()){
					mRenderingView = surface;
					LinphoneMiniManager.getLC().setVideoWindow(vw);
				}
			}

			public void onVideoRenderingSurfaceDestroyed(AndroidVideoWindowImpl vw) {
				synchronized(vw.getSurface()){
					LinphoneCore linphoneCore = LinphoneMiniManager.getLC();
					if (linphoneCore != null) {
						linphoneCore.setVideoWindow(null);
					}
				}
			}

			public void onVideoPreviewSurfaceReady(AndroidVideoWindowImpl vw, SurfaceView surface) {
				Log.i(TAG, "onVideoPreviewSurfaceReady: ");
				switchTime = System.currentTimeMillis();
				synchronized(surface){
					mPreviewView = surface;
					LinphoneMiniManager.getLC().setPreviewWindow(mPreviewView);
				}
			}

			public void onVideoPreviewSurfaceDestroyed(AndroidVideoWindowImpl vw) {
				synchronized (vw.getSurface()){
					LinphoneMiniManager.getLC().setPreviewWindow(null);
				}
			}
		});
	}

	private void registerBroadCast() {
		//广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BaseConfig.RECEIVE_VIDEO_ACTIVITY);
		intentFilter.addAction(BaseConfig.RECEIVE_MAIN_ACTIVITY);
		intentFilter.addAction(BaseConfig.RECEIVE_UPB_CHANGE);
		intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		mReceiver = new VideoActivityReceiver();
		registerReceiver(mReceiver, intentFilter);
		IntentFilter usbFilter = new IntentFilter();
		usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
		usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
		registerReceiver(UsbReceiver,usbFilter);
	}
	private BroadcastReceiver UsbReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (!TextUtils.isEmpty(action)){
				switch (action){
					case UsbManager.ACTION_USB_DEVICE_DETACHED://接收到U盘设设备拔出广播
						Log.e(TAG, "get action: ACTION_USB_DEVICE_DETACHED");
					Toast.makeText(context,"检测到USB设备被拔出,请确认摄像头是否正常连接",Toast.LENGTH_LONG).show();
						if (getCameras()==0){
//									new NotCameraDialog(context).Builder().show();
							dialog = new NotCameraDialog(context);
							dialog.Builder(context).show();
						}else{
							CameraUtil.initCamera(instance);
						}
						break;
					case UsbManager.ACTION_USB_DEVICE_ATTACHED://接收到U盘设备插入广播
						Log.e(TAG, "ACTION_USB_DEVICE_ATTACHED");
						Toast.makeText(context,"检测到USB设备正在接入……",Toast.LENGTH_LONG).show();
						CameraUtil.initCamera(instance);
						switchCamera(1);
						break;
						default:
						break;
				}
			}
		}
	};


	private void resizePreview() {
		LinphoneCore lc = LinphoneMiniManager.getLC();
		if (lc.getCallsNb() > 0) {
			LinphoneCall call = lc.getCurrentCall();
			if (call == null) {
				call = lc.getCalls()[0];
			}
			if (call == null) return;

			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			int screenHeight = metrics.heightPixels;
			int maxHeight = screenHeight / 4; // Let's take at most 1/4 of the screen for the camera preview

			VideoSize videoSize = call.getCurrentParams().getSentVideoSize(); // It already takes care of rotation
			int width = videoSize.width;
			int height = videoSize.height;

			Log.d(TAG,"Video height is " + height + ", width is " + width);
			width = width * maxHeight / height;
			height = maxHeight;
			Log.d(TAG,"Video preview size set to " + width + "x" + height);
			mPreviewView.getHolder().setFixedSize(width, height);

		}
	}
	private boolean isMute = false;
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.video_hang_up:
				HangUp();
				break;
			case R.id.video_mute:
				isMute =!isMute;
				LinphoneCore lc = instance.getmLinphoneCore();
				lc.muteMic(isMute);
				if (isMute){
					Toast.makeText(this,"已静音",Toast.LENGTH_SHORT).show();
					video_mute.setBackgroundResource(R.mipmap.mute2);
				}else{
					Toast.makeText(this,"已恢复",Toast.LENGTH_SHORT).show();
					video_mute.setBackgroundResource(R.mipmap.mute);
				}
				break;
//			case R.id.video_speaker:
//				isSpeaker = !isSpeaker;
//				instance.lilin_qie(isSpeaker);
//				if (isSpeaker){
//
//				}else{
//
//				}
//				break;
			case R.id.video_qiev:
//				switchCamera(0);
				qiev();
				break;
			case R.id.video_switch:
				try {
					if (System.currentTimeMillis() - switchTime>10000){
						switchTime = System.currentTimeMillis();
						isSwitch =!isSwitch;
						CameraUtil.changeScreen(isSwitch,callvideo_rl2,callvideo_rl1,mPreviewView,mRenderingView);
					}else{
						Toast.makeText(this,"请稍后尝试",Toast.LENGTH_SHORT).show();
					}
				}catch (Exception e){
					e.printStackTrace();
				}
				break;
			default:
				break;
		}
	}

	private void qiev() {
		try {
            instance.lilin_jie(false);
            Intent intent = new Intent(this, AudioActivity.class);
            if (!TextUtils.isEmpty(message)){
                intent.putExtra("message",message);
            }
            intent.putExtra(CallRecordTable.INSTANCE.getCALL_STATUS(), CallInfo.INSTANCE.getCALL_IN());
            startActivity(intent);
//            instance.updateCall();
            finish();
        }catch (LinphoneCoreException e){
            e.printStackTrace();
        }
	}

	private long switchTime = 0;
	private boolean isSwitch = false;

	private void HangUp() {
		if (!callRecord.call_status.equals(CallInfo.INSTANCE.getCALL_IN())){
			callRecord.call_status = CallInfo.INSTANCE.getCALL_OUT();
		}
		instance.hangUp();
		callTimeDialog.cancel();
		finish();
	}

	private boolean isSpeaker = true;


	@Override
	protected void onResume() {
		super.onResume();
		if (mRenderingView != null&&mRenderingView.getVisibility() == View.VISIBLE) {
			((GLSurfaceView) mRenderingView).onResume();
		}
		if (mAndroidVideoWindow != null) {
			synchronized (mAndroidVideoWindow) {
				LinphoneMiniManager.getLC().setVideoWindow(mAndroidVideoWindow);
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mAndroidVideoWindow != null) {
			synchronized (mAndroidVideoWindow) {
				LinphoneMiniManager.getLC().setVideoWindow(null);
			}
		}
		if (mRenderingView != null&&mRenderingView.getVisibility() == View.VISIBLE) {
			((GLSurfaceView) mRenderingView).onPause();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mReceiver != null) {
			unregisterReceiver(mReceiver);
		}
		if (UsbReceiver!=null){
			unregisterReceiver(UsbReceiver);
		}
		callRecord.call_time = System.currentTimeMillis()+"".trim();
		CallRecordUtil.updateCallRecord(this,callRecord);
		mPreviewView = null;
		mRenderingView = null;
		if (mAndroidVideoWindow != null) {
			mAndroidVideoWindow.release();
			mAndroidVideoWindow = null;
		}
		if (null!=dialog&& dialog.isShow()){
			dialog.cancel();
		}
		if (null!=callTimeDialog&&callTimeDialog.isShow()){
			callTimeDialog.cancel();
		}
		clearCallTimeOut();
		stopCallTimeTask();
		UserInfoUtil userInfoUtil = UserInfoUtil.getInstance(this);
		String userId= userInfoUtil.getUserId();
		TalkBean talkbean = new TalkBean();
		talkbean.setUserId(userId);
		talkbean.setTalkDuration((System.currentTimeMillis()-firstCallTime)/1000);
		talkbean.setTalkTime(TimeUtil.getDateTime(TimeUtil.dateFormatYMDHMS,firstCallTime)) ;
		TalkManage.addConversationLog(talkbean,new TalkCallBack (){

			@Override
			public void onSuccess() {

			}

			@Override
			public void OnFailed() {

			}
		});
	}

	private void clearCallTimeOut() {
		if (callTimeObserver!=null&&!callTimeObserver.isDisposed()){
			callTimeObserver.dispose();
		}
	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		Log.i(TAG, "dispatchKeyEvent: event:" + event.getAction());
		if (KeyEvent.ACTION_DOWN == event.getAction()) {
			stopTimerOut();
			initTimerOut();
			isOnTouch= true;
			initTouch(isOnTouch);
		}
		return super.dispatchKeyEvent(event);
	}



	public class VideoActivityReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getStringExtra("action");
			switch (action) {
				case "end":
					finish();
					break;
				case "show_status":
					Log.i(TAG, "onReceive: show_status");
					String data = intent.getStringExtra("data");
					if (data.contains("Call terminated")){
//						callRecord.call_status = CallInfo.INSTANCE.getCALL_RINGING();
						finish();
					}else if (data.contains("You have missed 1 call.")){
						callRecord.call_status = CallInfo.INSTANCE.getCALL_MISSED();
						finish();
					}else if (data.contains("Request timeout.")){
						callRecord.call_status = CallInfo.INSTANCE.getCALL_MISSED();
						finish();
					}else if(data.contains("Call released")){
//						callRecord.call_status = CallInfo.INSTANCE.getCALL_ANSWER();
						finish();
					}else if (data.contains("Call is updated by remote")){
						instance.lilin_qie_updatecall();
						Intent audioIntent = new Intent(context, AudioActivity.class);
						if (!TextUtils.isEmpty(message)){
							audioIntent.putExtra("message",message);
						}
						audioIntent.putExtra(CallRecordTable.INSTANCE.getCALL_STATUS(), CallInfo.INSTANCE.getCALL_IN());
						startActivity(audioIntent);
						finish();
					}
					break;
				case "receive_usb_change":
					String status = intent.getStringExtra("status");

					if(!TextUtils.isEmpty(status)){
						switch (status){
							case Intent.ACTION_MEDIA_CHECKING:
								Log.e(TAG, "get action: ACTION_MEDIA_CHECKING");
								break;
							case Intent.ACTION_MEDIA_MOUNTED:
								Log.e(TAG, "get action: ACTION_MEDIA_MOUNTED");
								if (getCameras()>0){
									switchCamera(1);
								}
								break;
							case Intent.ACTION_MEDIA_UNMOUNTED: //U盘卸载
								Log.e(TAG, "get action:ACTION_MEDIA_UNMOUNTED");
								if (getCameras()==0){
//									new NotCameraDialog(context).Builder().show();
									dialog = new NotCameraDialog(context);
									dialog.Builder(context).show();
								}
								break;
							case UsbManager.ACTION_USB_DEVICE_DETACHED://接收到U盘设设备拔出广播
								Log.e(TAG, "get action: ACTION_USB_DEVICE_DETACHED");
								if (getCameras()==0){
//									new NotCameraDialog(context).Builder().show();
									dialog = new NotCameraDialog(context);
									dialog.Builder(context).show();
								}else{
									CameraUtil.initCamera(instance);
								}
								break;
							case UsbManager.ACTION_USB_DEVICE_ATTACHED://接收到U盘设备插入广播
								Log.e(TAG, "ACTION_USB_DEVICE_ATTACHED");
								CameraUtil.initCamera(instance);
								switchCamera(1);
								break;
							case Intent.ACTION_MEDIA_EJECT:
								Log.e(TAG, "ACTION_MEDIA_EJECT");
								break;
						}
					}
					break;
				default:
					break;
			}
		}
	}


	private int getCameras() {
		AndroidCameraConfiguration.AndroidCamera[] androidCameras = AndroidCameraConfiguration.retrieveCameras();
		Log.i(TAG, "getCameras: "+ androidCameras.length);
		return androidCameras.length;
	}
	DisposableObserver<Long> CameraObserver;
	public void switchCamera(long nmb) {
		Log.i(TAG, "switchCamera: ");
		if (CameraObserver!=null&&!CameraObserver.isDisposed()){
			CameraObserver.dispose();
		}
		CameraObserver = new DisposableObserver<Long>() {
			@Override
			public void onNext(Long aLong) {
				Log.i(TAG, "switchCamera onNext: ");
				try {
					int camId = 0;
					AndroidCameraConfiguration.AndroidCamera[] cameras = AndroidCameraConfiguration.retrieveCameras();
					if (cameras!=null&&cameras.length!=0){
						for (AndroidCameraConfiguration.AndroidCamera androidCamera : cameras) {
							Log.i(TAG, "onNext: androidCamera:"+androidCamera.id);
							camId = androidCamera.id;
							Log.i(TAG, "onNext: set video device :"+camId);
							instance.getLC().setVideoDevice(camId);
						}
						int videoDeviceId = instance.getLC().getVideoDevice();
						Log.i(TAG, "switchCamera get videoDeviceId" + videoDeviceId);
						videoDeviceId = (videoDeviceId + 1) % AndroidCameraConfiguration.retrieveCameras().length;
//			Toast.makeText(getApplicationContext(), "默认Toast样"+mPreviewView+"式"+videoDeviceId,   Toast.LENGTH_SHORT).show();
						//前置摄像头-1，后置摄像头-0
						Log.i(TAG, "switchCamera: videoDeviceId:" + videoDeviceId);
						instance.getLC().setVideoDevice(videoDeviceId);
						instance.updateCall();
						// previous call will cause graph reconstruction -> regive previewwindow

						if (mPreviewView != null) {
							Log.i(TAG, "switchCamera: set mPreviewView");
							instance.getLC().setPreviewWindow(mPreviewView);
						}
					}else{
						Log.i(TAG,"onNext get android Camera failed,not camera");
						dialog = new NotCameraDialog(VideoActivity.this);
						dialog.Builder(VideoActivity.this).show();
					}


				} catch (ArithmeticException ae) {
					Log.e(TAG, "Cannot swtich camera : no camera");

				}catch (NullPointerException e){
					Log.e(TAG,"switchCamea onError",e);
				}
				onComplete();
			}

			@Override
			public void onError(Throwable e) {
				Log.i(TAG, "switchCamera onError: ");
				dispose();
				switchCamera(2);
			}

			@Override
			public void onComplete() {
				Log.i(TAG, "switchCamera onComplete: ");
				dispose();
			}
		};
		Observable.interval(nmb,TimeUnit.SECONDS)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(CameraObserver);
	}

	private   void fixZOrder(SurfaceView rendering, SurfaceView preview) {
		rendering.setZOrderOnTop(false);
		preview.setZOrderOnTop(true);
		preview.setZOrderMediaOverlay(true); // Needed to be able to display control layout over
	}


	private AlphaAnimation alphaAnimation0To1;
	private AlphaAnimation alphaAnimation1To0;
	private void initAlphaAnimation1() {
		alphaAnimation0To1 = new AlphaAnimation(0f, 1f);
		alphaAnimation0To1.setDuration(1000);
		alphaAnimation0To1.setFillAfter(true);

	}

	private void initAlphaAnimation2() {
		alphaAnimation1To0 = new AlphaAnimation(1f, 0f);
		alphaAnimation1To0.setDuration(1000);
		alphaAnimation1To0.setFillAfter(true);
	}

	boolean isOnTouch = false;
	//	Observable<Long> longObservable;
	DisposableObserver<Long> disposableObserver;
	private void initTimerOut(){
		if (disposableObserver!=null&&!disposableObserver.isDisposed()){
			disposableObserver.dispose();
		}
		disposableObserver = new DisposableObserver<Long>() {
			@SuppressLint("NewApi")
			@Override
			public void onNext(Long aLong) {
				if (!isDestroyed()) {
					call_video_r3.clearAnimation();
					call_video_r3.setAnimation(alphaAnimation1To0);
					isOnTouch = false;
					initTouch(isOnTouch);
				}
				onComplete();
			}

			@Override
			public void onError(Throwable e) {
				dispose();
				call_video_r3.clearAnimation();
				initTouch(true);
			}

			@Override
			public void onComplete() {
				dispose();
			}
		};
		Observable.interval(8, TimeUnit.SECONDS)
				.subscribeOn(Schedulers.io()).
				observeOn(AndroidSchedulers.mainThread())
				.subscribe(disposableObserver);

	}

	private void initTouch(boolean b) {
		video_hang_up.setEnabled(b);
		video_mute.setEnabled(b);
		video_qiev.setEnabled(b);
	}

	/**
	 * 取消控件隐藏功能
	 */
	private void stopTimerOut() {
		Log.i(TAG, "stopTimerOut: ");
//		call_video_r3.clearAnimation();
		Animation animation = call_video_r3.getAnimation();
		if (alphaAnimation1To0.equals(animation)) {
			call_video_r3.clearAnimation();
			call_video_r3.startAnimation(alphaAnimation0To1);
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK){
			return  false;
		}
		return super.onKeyDown(keyCode, event);
	}
}
