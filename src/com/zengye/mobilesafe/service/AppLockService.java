package com.zengye.mobilesafe.service;

import java.util.ArrayList;
import java.util.List;

import com.zengye.mobilesafe.activity.AppLockActivity;
import com.zengye.mobilesafe.db.dao.AppLockDBDao;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class AppLockService extends Service {

	private boolean flag;
	private ActivityManager am;
	private List<String> pkgList;
	private List<String> tempPkgNames = new ArrayList<String>();
	private TempReceiver tempReceiver;
	private ScreenOnReceiver onReceiver;
	private ScreenOffReceiver offReceiver;
	private AppLockDBDao dao;
	private DataChangedReceiver dataChangedReceiver;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		flag = true;
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		tempReceiver = new TempReceiver();
		onReceiver = new ScreenOnReceiver();
		offReceiver = new ScreenOffReceiver();
		dataChangedReceiver = new DataChangedReceiver();
		dao = new AppLockDBDao(this);
		pkgList = dao.findAll();
		registerReceiver(tempReceiver, new IntentFilter("com.zengye.mobile.applock.temp"));
		registerReceiver(onReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
		registerReceiver(offReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
		registerReceiver(dataChangedReceiver, new IntentFilter("com.zengye.mobilesafe.applock.datachanged"));
		start();
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub'
		unregisterReceiver(tempReceiver);
		unregisterReceiver(onReceiver);
		unregisterReceiver(offReceiver);
		unregisterReceiver(dataChangedReceiver);
		tempReceiver = null;
		onReceiver = null;
		offReceiver = null;
		dataChangedReceiver = null;
		flag = false;
		super.onDestroy();
	}
	public void start() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(flag) {
					Log.i("AppLock", "lock");
					String pkgName = am.getRunningTasks(1).get(0).topActivity.getPackageName();
					if(pkgList.contains(pkgName) && !tempPkgNames.contains(pkgName)) {
						Intent intent = new Intent(AppLockService.this, AppLockActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra("pkgName", pkgName);
						startActivity(intent);
					}
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				Log.i("AppLock", "unlock");
			}
		}).start();
	}
	
	class TempReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String tempPkgName = intent.getStringExtra("pkgName");
			tempPkgNames.add(tempPkgName);
		}
		
	}
	
	class ScreenOnReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			start();
			flag = true;
			
		}
		
	}
	class ScreenOffReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			
			tempPkgNames.clear();
			flag = false;
		}
		
	}
	
	class DataChangedReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			pkgList = dao.findAll();
		}
		
	}
}
