package com.zengye.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import com.zengye.mobilesafe.db.BlackNumberDBHelper;
import com.zengye.mobilesafe.domain.BlackNumberInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BlackNumberDBDao {
	private BlackNumberDBHelper helper;
	public BlackNumberDBDao(Context context) {
		// TODO Auto-generated constructor stub
		helper = new BlackNumberDBHelper(context);
	}
	
	public List<BlackNumberInfo> findAll() {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select number,mode from blacknumber order by _id desc",null);
		List<BlackNumberInfo> infos = new ArrayList<BlackNumberInfo>();
		while(cursor.moveToNext()) {
			BlackNumberInfo info = new BlackNumberInfo();
			info.setNubmer(cursor.getString(0));
			info.setMode(cursor.getString(1));
			infos.add(info);
		}
		cursor.close();
		db.close();
		return infos;
	}
	public List<BlackNumberInfo> findPart(int offset, int max) {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select number,mode from blacknumber order by _id desc limit ? offset ?",new String[] {String.valueOf(max),String.valueOf(offset)});
		List<BlackNumberInfo> infos = new ArrayList<BlackNumberInfo>();
		while(cursor.moveToNext()) {
			BlackNumberInfo info = new BlackNumberInfo();
			info.setNubmer(cursor.getString(0));
			info.setMode(cursor.getString(1));
			infos.add(info);
		}
		cursor.close();
		db.close();
		return infos;
	}
	
	public boolean find(String number) {
		boolean result = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("select number,mode from blacknumber where number = ?", new String[]{number});
		if (cursor.moveToNext()) {
			result = true;
		}
		cursor.close();
		db.close();
		return result;
	}
	
	public void add(String number,String mode) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("number", number);
		values.put("mode", mode);
		db.insert(BlackNumberDBHelper.TABLE_NAME, null, values);
		db.close();
	}
	public void update(String number, String mode) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", mode);
		values.put("number", number);
		db.update(BlackNumberDBHelper.TABLE_NAME, values, "number = ?", new String[]{number});
		db.close();
	}
	public void delete(String number) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete(BlackNumberDBHelper.TABLE_NAME, "number = ?", new String[]{number});
		db.close();
	}
	
	/**
	 * 查询黑名单号码的拦截模式
	 * @param number
	 * @return 返回号码的拦截模式，不是黑名单号码返回null
	 */
	public String findMode(String number){
		String result = null;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select mode from blacknumber where number=?", new String[]{number});
		if(cursor.moveToNext()){
			result = cursor.getString(0);
		}
		cursor.close();
		db.close();
		return result;
	}
}
