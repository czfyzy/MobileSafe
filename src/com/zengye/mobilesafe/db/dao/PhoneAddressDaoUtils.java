package com.zengye.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PhoneAddressDaoUtils {
	private static String path = "data/data/com.zengye.mobilesafe/files/address.db";

	public static String queryAddress(String phoneNo) {
		String address = null;

		SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		if (phoneNo.matches("^1[34568]\\d{9}$")) {

			Cursor cursor = database
					.rawQuery(
							"select location from data2 where id = (select outkey from data1 where id = ?)",
							new String[] { phoneNo.substring(0, 7) });
			while (cursor.moveToNext()) {
				address = cursor.getString(0);
			}
			cursor.close();
		} else {
			switch (phoneNo.length()) {
			case 3:
				address = "紧急电话";
				break;
			case 4:
				address = "模拟器";
				break;
			case 5:
				address = "客服电话";
				break;
			case 7:
				address = "本地电话";
				break;
			case 8:
				address = "本地电话";
				break;

			default:
				if (phoneNo.length() > 10 && phoneNo.startsWith("0")) {
					Cursor cursor = database.rawQuery(
							"select location from data2 where area = ?",
							new String[] { phoneNo.substring(1, 3) });
					while (cursor.moveToNext()) {
						String tempAdd = cursor.getString(0);
						address = tempAdd.substring(0, tempAdd.length() - 2);
					}
					cursor.close();
					if(address == null) {
						cursor = database.rawQuery(
								"select location from data2 where area = ?",
								new String[] { phoneNo.substring(1, 4) });
						while (cursor.moveToNext()) {
							String location = cursor.getString(0);
							address = location.substring(0, location.length() - 2);

						}
					}
				} 
				break;
			}
			 
		}
		if(address == null) {
			address = phoneNo;
		}
		return address;
	}
}
