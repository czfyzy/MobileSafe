package com.zengye.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import com.zengye.mobilesafe.domain.AppInfo;

public class AutoStartProvider {

	public static List<AppInfo> getAllAutoStartAppInfo(PackageManager pm) {
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		Intent intent = new Intent(Intent.ACTION_BOOT_COMPLETED);
		List<ResolveInfo> resolveInfoList = pm.queryBroadcastReceivers(
				intent, PackageManager.GET_DISABLED_COMPONENTS);
		for (ResolveInfo resolveInfo : resolveInfoList) {
			ComponentName mComponentName = new ComponentName(
					resolveInfo.activityInfo.packageName,
					resolveInfo.activityInfo.name);
			Log.d("MainActivity",
					"COMPONENT_ENABLED_STATE:"
							+ pm.getComponentEnabledSetting(mComponentName)
							+ "\tpackageName:"
							+ resolveInfo.activityInfo.packageName);
			int flags = resolveInfo.activityInfo.applicationInfo.flags;
			AppInfo appInfo = new AppInfo();
			
			appInfo.setIcon(resolveInfo.activityInfo.loadIcon(pm));
			appInfo.setUserApp((flags & ApplicationInfo.FLAG_SYSTEM) == 0);
			appInfo.setInRom((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0);
			appInfo.setName(resolveInfo.activityInfo.loadLabel(pm).toString());
			appInfo.setPackageName(resolveInfo.activityInfo.packageName);
			appInfo.setReceiverClass(resolveInfo.activityInfo.name);
			appInfo.setUid(resolveInfo.activityInfo.applicationInfo.uid);
			
			int state = pm.getComponentEnabledSetting(mComponentName);
			
			if(state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED || state == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT) {
				appInfo.setAutoStart(true);
			} else {
				appInfo.setAutoStart(false);
			}
			appInfos.add(appInfo);
			
////			Log.d(TAG,resolveInfo.activityInfo.name);
//			if(resolveInfo.activityInfo.packageName.toLowerCase().contains("zengye")) {
//				if(pm.getComponentEnabledSetting(mComponentName)==PackageManager.COMPONENT_ENABLED_STATE_ENABLED ||pm.getComponentEnabledSetting(mComponentName)==PackageManager.COMPONENT_ENABLED_STATE_DEFAULT ) {
//					boolean  flag = CommonUtils.execCommandWithSu("pm disable " + resolveInfo.activityInfo.packageName + "/" + resolveInfo.activityInfo.name);
//					
//				}
//			}
			
		}
		
		return appInfos;
	}
}
