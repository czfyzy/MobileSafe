package com.zengye.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.zengye.mobilesafe.domain.AppInfo;

public class AppInfoProvider {

	public static List<AppInfo> getAppInfos(Context context) {
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		for (PackageInfo info : packageInfos) {
			AppInfo appInfo = new AppInfo();
			appInfo.setPackageName(info.packageName);
			appInfo.setIcon(info.applicationInfo.loadIcon(pm));
			appInfo.setName((String) info.applicationInfo.loadLabel(pm));
			appInfo.setUid(info.applicationInfo.uid);
			int flag = info.applicationInfo.flags;
			
			appInfo.setUserApp((flag & ApplicationInfo.FLAG_SYSTEM) == 0);
			appInfo.setInRom((flag & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0);
			appInfos.add(appInfo);
		}
		return appInfos;
	}
}
