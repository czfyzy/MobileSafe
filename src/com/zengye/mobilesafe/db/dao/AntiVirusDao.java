package com.zengye.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AntiVirusDao {

	public static final String PATH = "data/data/com.zengye.mobilesafe/files/antivirus.db";
	public static boolean find(String md5) {
		boolean result = false;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select md5 from datable where md5 = ?", new String[]{md5});
		if(cursor.moveToNext()) {
			result =  true;
		}
		cursor.close();
		db.close();
		return result;
	}
}
