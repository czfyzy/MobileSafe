package com.zengye.mobilesafe.widget;

import com.zengye.mobilesafe.service.UpdateWidgetService;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class ProcessWidgetProvider extends AppWidgetProvider {

	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
	}
	
	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		super.onEnabled(context);
	    Intent intent = new Intent(context, UpdateWidgetService.class);
		context.startService(intent);
	}
	
	@Override
	public void onDisabled(Context context) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(context, UpdateWidgetService.class);
		context.stopService(intent);
		super.onDisabled(context);
	}
}
