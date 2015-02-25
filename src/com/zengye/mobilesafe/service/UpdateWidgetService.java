package com.zengye.mobilesafe.service;

import java.util.Timer;
import java.util.TimerTask;

import com.zengye.mobilesafe.R;
import com.zengye.mobilesafe.utils.SystemInfoUtils;
import com.zengye.mobilesafe.widget.ProcessWidgetProvider;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

public class UpdateWidgetService extends Service {

	protected static final String TAG = "UpdateWidgetService";
	private AppWidgetManager awm;
	private Timer timer;
	private TimerTask timerTask;
	private ScreenOffReceiver offReceiver;
	private ScreenOnReceiver onReceiver;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		awm = AppWidgetManager.getInstance(this);
		offReceiver = new ScreenOffReceiver();
		onReceiver = new ScreenOnReceiver();
		registerReceiver(offReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
		registerReceiver(onReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
		startUpdate();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(offReceiver);
		unregisterReceiver(onReceiver);
		offReceiver = null;
		onReceiver = null;
		stopUpdate();
		super.onDestroy();
	}

	private void startUpdate() {
		// TODO Auto-generated method stub
		timer = new Timer();
		timerTask = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				ComponentName provider = new ComponentName(
						UpdateWidgetService.this, ProcessWidgetProvider.class);
				RemoteViews views = new RemoteViews(getPackageName(),
						R.layout.process_widget);
				views.setTextViewText(
						R.id.process_count,
						"正在运行的进程："
								+ SystemInfoUtils
										.getRunningProcessCount(UpdateWidgetService.this)
								+ "个");
				views.setTextViewText(
						R.id.process_memory,
						"可用内存："
								+ Formatter
										.formatFileSize(
												UpdateWidgetService.this,
												SystemInfoUtils
														.getAvailableMemory(UpdateWidgetService.this)));
				PendingIntent pendingIntent = PendingIntent.getBroadcast(UpdateWidgetService.this, 0, new Intent("com.znegye.mobilesafe.widget.onkeyclear"), PendingIntent.FLAG_UPDATE_CURRENT);
				views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
				awm.updateAppWidget(provider, views);
				Log.i(TAG, "更新widget");
			}
		};
		timer.schedule(timerTask, 0, 6000);
	}

	private void stopUpdate() {
		// TODO Auto-generated method stub
		if(timer != null && timerTask != null) {
			
			timerTask.cancel();
			timer.cancel();
			timer = null;
			timerTask = null;
		}
	}

	class ScreenOnReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Log.i(TAG, "屏幕解锁");
			startUpdate();
		}
		
	}
	class ScreenOffReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			
			// TODO Auto-generated method stub
			Log.i(TAG, "屏幕锁屏");
			stopUpdate();
		}
		
	}
	
}
