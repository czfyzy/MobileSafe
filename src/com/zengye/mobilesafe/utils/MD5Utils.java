package com.zengye.mobilesafe.utils;

import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {

	public static String encrypt(String password) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] result = digest.digest(password.getBytes());
			StringBuffer sb = new StringBuffer();
			for (byte b : result) {
				int num = b & 0xff; //加盐
				String hexStr = Integer.toHexString(num);
				if(hexStr.length() == 1) {
					sb.append("0");
				}
				sb.append(hexStr);
			}
		   return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
	public static String getFileMD5(String path) {
		try {
			MessageDigest digest = MessageDigest.getInstance("md5");
			FileInputStream fis = new FileInputStream(path);
			byte[] buffer = new byte[1024];
			int len = 0;
			while((len = fis.read(buffer)) != -1 ) {
				digest.update(buffer, 0, len);
			}
			
			byte[] result = digest.digest();
			StringBuffer sb = new StringBuffer();
			for (byte b : result) {
				int number = b & 0xff;
				String hexStr = Integer.toHexString(number);
				if(hexStr.length() == 1) {
					sb.append("0");
				}
				sb.append(hexStr);
			}
			return sb.toString();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		
		
	}
}
