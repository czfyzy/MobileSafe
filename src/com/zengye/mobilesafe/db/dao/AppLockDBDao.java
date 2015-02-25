package com.zengye.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import com.zengye.mobilesafe.db.AppLockDBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AppLockDBDao {

	private AppLockDBHelper helper;
	private Intent intent;
	private Context context;
	public AppLockDBDao(Context context) {
		this.context = context;
		helper = new AppLockDBHelper(context);
		intent = new Intent("com.zengye.mobilesafe.applock.datachanged");
	}
	
	public void add(String pkgName) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values =new ContentValues();
		values.put("pkgName", pkgName);
		db.insert(AppLockDBHelper.TABLE_NAME, null, values);
		db.close();
		context.sendBroadcast(intent);
	}
	
	public List<String> findAll(){
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(AppLockDBHelper.TABLE_NAME, new String[]{"pkgName"}, null, null, null, null, null);
		List<String> pkgNameList = new ArrayList<String>();
		while (cursor.moveToNext()) {
			pkgNameList.add(cursor.getString(0));
		}
		cursor.close();
		db.close();
		return pkgNameList;
		
	}
	public boolean find(String pkgName) {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(AppLockDBHelper.TABLE_NAME, new String[]{"pkgName"}, "pkgName=?", new String[]{pkgName}, null, null, null);
		if(cursor.moveToNext()) {
			cursor.close();
			db.close();
			return true;
		}
		cursor.close();
		db.close();
		return false;
	}
	public void delete(String pkgName) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete(AppLockDBHelper.TABLE_NAME, "pkgName=?", new String[]{pkgName});
		context.sendBroadcast(intent);
	}
}
