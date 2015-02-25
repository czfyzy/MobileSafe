package com.zengye.mobilesafe.utils;

import com.zengye.mobilesafe.receiver.MsDeviceAdminReceiver;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class DeviceAdminUtils {

	private static DevicePolicyManager dpm;

	public static void wipeData(Context context) {
		if (dpm == null) {
			dpm = (DevicePolicyManager) context
					.getSystemService(Context.DEVICE_POLICY_SERVICE);
		}

		ComponentName who = new ComponentName(context,
				MsDeviceAdminReceiver.class);
		if (dpm.isAdminActive(who)) {
			// 清除Sdcard上的数据
			dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
			// 恢复出厂设置
			dpm.wipeData(0);
		} else {
			Toast.makeText(context, "还没有打开管理员权限", 1).show();
			return;
		}

	}

	public static void lockscreen(Context context,String password) {
		if (dpm == null) {
			dpm = (DevicePolicyManager) context
					.getSystemService(Context.DEVICE_POLICY_SERVICE);
		}
		ComponentName who = new ComponentName(context,
				MsDeviceAdminReceiver.class);
		if (dpm.isAdminActive(who)) {
			dpm.lockNow();// 锁屏
			dpm.resetPassword(password, 0);// 设置屏蔽密码

			// 清除Sdcard上的数据
			// dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
			// 恢复出厂设置
			// dpm.wipeData(0);
		} else {
			Toast.makeText(context, "还没有打开管理员权限", 1).show();
			return;
		}

	}

	/**
	 * 用代码去开启管理员
	 * 
	 * @param view
	 */
	public static void openAdmin(Context context) {
		// 创建一个Intent
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		// 我要激活谁
		ComponentName mDeviceAdminSample = new ComponentName(context,
				MsDeviceAdminReceiver.class);

		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
				mDeviceAdminSample);
		// 劝说用户开启管理员权限
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
				"哥们开启我可以一键锁屏，你的按钮就不会经常失灵");
		context.startActivity(intent);
	}
}
