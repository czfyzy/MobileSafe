package com.zengye.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;

import com.zengye.mobilesafe.R;
import com.zengye.mobilesafe.domain.TaskInfo;

public class TaskInfoProvider {

	public static List<TaskInfo> getTaskInfos(Context context){
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager pm = context.getPackageManager();
		List<RunningAppProcessInfo>  infos = am.getRunningAppProcesses();
		List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
		for (RunningAppProcessInfo info : infos) {
			TaskInfo taskInfo = new TaskInfo();
			String packageName = info.processName;
			MemoryInfo[] memoryInfos = am.getProcessMemoryInfo(new int[]{info.pid});
			long memSize = memoryInfos[0].getTotalPrivateDirty() * 1024;
			taskInfo.setMemSize(memSize);
			taskInfo.setPackageName(packageName);
			taskInfo.setPid(info.pid);
			try {
				ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);
				Drawable icon = appInfo.loadIcon(pm);
				String name = (String) appInfo.loadLabel(pm);
				boolean isUserTask = ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) ==0);
				
				taskInfo.setIcon(icon);
				taskInfo.setName(name);
				taskInfo.setUserTask(isUserTask);
			
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				taskInfo.setIcon(context.getResources().getDrawable(R.drawable.defual_icon));
				taskInfo.setName(packageName);
				taskInfo.setUserTask(false);
			}
			taskInfos.add(taskInfo);
			
		}
		return taskInfos;
	}
}
