package com.zengye.mobilesafe.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;


public class CommonUtils {

	public static boolean execCommandWithSu(String... commands) {
		Process process = null;
		DataOutputStream dataOutputStream = null;

		try {
			process = Runtime.getRuntime().exec("su");
			dataOutputStream = new DataOutputStream(process.getOutputStream());
			int length = commands.length;
			for (int i = 0; i < length; i++) {
//				Log.e(TAG, "commands[" + i + "]:" + commands[i]);
				dataOutputStream.writeBytes(commands[i] + "\n");
			}
			dataOutputStream.writeBytes("exit\n");
			dataOutputStream.flush();
			process.waitFor();
		} catch (Exception e) {
//			Log.e(TAG, "error", e);
			return false;
		} finally {
			try {
				if (dataOutputStream != null) {
					dataOutputStream.close();
				}
				process.destroy();
			} catch (Exception e) {
			}
		}
//		Log.v(TAG, "execCommandWithSu finish");
		return true;
	}
	
	public static String readFile(String path) throws Exception {
		FileReader fr = new FileReader(path);
		
		BufferedReader bfr = new BufferedReader(fr);
		
		String line;
		
		StringBuilder sb = new StringBuilder();
		
		while((line = bfr.readLine()) != null) {
			sb.append(line);
		}
		
		return sb.toString().trim();
	}
	public static List<String> readFileArr(String path) throws Exception {
		FileReader fr = new FileReader(path);
		
		BufferedReader bfr = new BufferedReader(fr);

		List<String> result = new ArrayList<String>();
		
		String line;
		
		while((line = bfr.readLine()) != null) {
			result.add(line);
		}
		
		return result;
	}
}
