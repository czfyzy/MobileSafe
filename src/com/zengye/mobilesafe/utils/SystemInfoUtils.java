package com.zengye.mobilesafe.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

public class SystemInfoUtils {

	public static int getRunningProcessCount(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
		return infos.size();
	}

	public static long getAvailableMemory(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo memoryInfo = new MemoryInfo();
		am.getMemoryInfo(memoryInfo);
		return memoryInfo.availMem;
	}

	public static long getTotalMemory(Context context) {
		// ActivityManager am = (ActivityManager)
		// context.getSystemService(Context.ACTIVITY_SERVICE);
		// MemoryInfo memoryInfo = new MemoryInfo();
		// am.getMemoryInfo(memoryInfo);
		// return memoryInfo.totalMem;

		try {
			FileInputStream fis = new FileInputStream("/proc/meminfo");
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
			String line = reader.readLine();
		    Pattern pattern = Pattern.compile("\\d+");
		    Matcher matcher = pattern.matcher(line);
		    String str = "";
		    if (matcher.find()) {
			   str = matcher.group();
			}
			return Long.parseLong(str) * 1024;
		    
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
}
