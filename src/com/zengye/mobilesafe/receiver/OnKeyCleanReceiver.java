package com.zengye.mobilesafe.receiver;

import java.util.List;

import com.zengye.mobilesafe.R;
import com.zengye.mobilesafe.domain.TaskInfo;
import com.zengye.mobilesafe.engine.TaskInfoProvider;
import com.zengye.mobilesafe.utils.SystemInfoUtils;
import com.zengye.mobilesafe.widget.ProcessWidgetProvider;

import android.app.ActivityManager;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.format.Formatter;
import android.widget.RemoteViews;
import android.widget.Toast;

public class OnKeyCleanReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    int beforeCount = SystemInfoUtils.getRunningProcessCount(context);
	    long beforeMemSize = SystemInfoUtils.getAvailableMemory(context);
	    
	    int count = 0;
	    long memSize = 0;
	    List<TaskInfo> taskInfos = TaskInfoProvider.getTaskInfos(context);
	    
	    for (TaskInfo info : taskInfos) {
	    	
	    	if(context.getPackageName().equals(info.getPackageName())) {
	    		continue;
	    	}
 	    	am.killBackgroundProcesses(info.getPackageName());
	    	count ++;
	    	memSize =+ info.getMemSize();
		}
	    String format = Formatter.formatFileSize(context, memSize);
	    Toast.makeText(context, "清理了" + count +"个进程，节约了"+ format+"的内存资源", Toast.LENGTH_SHORT).show();
	    AppWidgetManager awm = AppWidgetManager.getInstance(context);
	    
	    int currentCount = beforeCount - count;
	    long currentMemSize = beforeMemSize + memSize;
	    ComponentName provider = new ComponentName(
				context, ProcessWidgetProvider.class);
		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.process_widget);
		views.setTextViewText(
				R.id.process_count,
				"正在运行的进程："
						+  currentCount
						+ "个");
		views.setTextViewText(
				R.id.process_memory,
				"可用内存："
						+ Formatter
								.formatFileSize(
										context,
										currentMemSize));
		awm.updateAppWidget(provider, views);
	}

}
