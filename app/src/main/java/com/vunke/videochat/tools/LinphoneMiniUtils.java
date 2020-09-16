package com.vunke.videochat.tools;

/*
LinphoneMiniUtils.java
Copyright (C) 2017  Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.vunke.videochat.service.LinphoneMiniManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class LinphoneMiniUtils {
	public static void copyIfNotExist(Context context, int ressourceId, String target) throws IOException {
		File lFileToCopy = new File(target);
		if (!lFileToCopy.exists()) {
			copyFromPackage(context, ressourceId, lFileToCopy.getName());
		}
	}

	public static void copyFromPackage(Context context, int ressourceId, String target) throws IOException {
		FileOutputStream lOutputStream = context.openFileOutput (target, 0);
		InputStream lInputStream = context.getResources().openRawResource(ressourceId);
		int readByte;
		byte[] buff = new byte[8048];
		while (( readByte = lInputStream.read(buff)) != -1) {
			lOutputStream.write(buff,0, readByte);
		}
		lOutputStream.flush();
		lOutputStream.close();
		lInputStream.close();
	}

	public static void initEchoCancellation() {
		Log.i("提示", "initEchoCancellation: ");
		if (LinphoneMiniManager.getInstance().getLC().needsEchoCalibration()){
			//回声消除
			boolean isEchoCancellation =  true;
			LinphoneMiniManager.getInstance().getLC().enableEchoCancellation(isEchoCancellation);
			Log.i("提示", "ecCalibrationStatus: true");
		}
	}
	public static void initLinphoneService(final Context context) {
		if (PermissionsUtil.hasPermission(context, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA})) {
			//有
			Intent intentOne = new Intent(context, LinphoneMiniManager.class);
			context.startService(intentOne);
		} else {
			PermissionsUtil.requestPermission(context, new PermissionListener() {

				public void permissionGranted(@NonNull String[] permissions) {
					//用户授予了
					Intent intentOne = new Intent(context, LinphoneMiniManager.class);
					context.startService(intentOne);
				}

				public void permissionDenied(@NonNull String[] permissions) {
					//用户拒绝了访问摄像头的申请
					Toast.makeText(context, "您没有授权将无法启用网络电话!", Toast.LENGTH_LONG).show();
				}
			}, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA});
		}
	}
}
