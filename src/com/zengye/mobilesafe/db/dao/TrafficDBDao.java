package com.zengye.mobilesafe.db.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.tsz.afinal.exception.DbException;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.zengye.mobilesafe.db.AppLockDBHelper;
import com.zengye.mobilesafe.db.TrafficDBHelper;
import com.zengye.mobilesafe.domain.AllTraffic;
import com.zengye.mobilesafe.domain.AppTraffic;
import com.zengye.mobilesafe.domain.TrafficInfo;

public class TrafficDBDao {

	private TrafficDBHelper helper;
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
//	private Intent intent;
//	private Context context;
	public TrafficDBDao(Context context) {
//		this.context = context;
		helper = new TrafficDBHelper(context);
//		intent = new Intent("com.zengye.mobilesafe.applock.datachanged");
	}
	
	public void add(String pkgName) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values =new ContentValues();
		values.put("pkgName", pkgName);
		db.insert(AppLockDBHelper.TABLE_NAME, null, values);
		db.close();
	}
	public boolean add(List<AppTraffic> appTraffics) {
			 
			SQLiteDatabase db = null;
			try {
				db = helper.getWritableDatabase();
				String sql = "insert into " + TrafficDBHelper.TRAFFIC_NAME + "("
						+ TrafficDBHelper.PACKAGE_NAME +","
						+ TrafficDBHelper.UID +","
						+ TrafficDBHelper.TX +","
						+ TrafficDBHelper.RX 
						+ ") " + "values(?,?,?,?)";
				SQLiteStatement stat = db.compileStatement(sql);
				db.beginTransaction();
				for (AppTraffic appTraffic : appTraffics) {
					stat.bindString(1, appTraffic.packageName);
					stat.bindLong(2, appTraffic.uid);
					stat.bindLong(3, appTraffic.tx);
					stat.bindLong(4, appTraffic.rx);
					long result = stat.executeInsert();
					if (result < 0) {
						return false;
					}
				}
				db.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			} finally {
				try {
					if (null != db) {
						db.endTransaction();
						db.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return true;
	}
	public List<AppTraffic> findAll(){
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(TrafficDBHelper.TRAFFIC_NAME, new String[]{"packageName","uid","tx","rx"}, null, null, null, null, null);
		List<AppTraffic> appTraffics = new ArrayList<AppTraffic>();
		while (cursor.moveToNext()) {
			AppTraffic traffic = new AppTraffic();
			traffic.packageName = cursor.getString(0);
			traffic.uid = (int) cursor.getLong(1);
			traffic.tx = cursor.getLong(2);
			traffic.rx = cursor.getLong(3);
			appTraffics.add(traffic);
		}
		cursor.close();
		db.close();
		return appTraffics;
		
	}
	public AppTraffic find(String packageName) {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(TrafficDBHelper.TRAFFIC_NAME, new String[]{"packageName","uid","tx","rx"}, "packageName=?", new String[]{packageName}, null, null, null);
		AppTraffic traffic = null;
		if(cursor.moveToNext()) {
			traffic = new AppTraffic();
			traffic.packageName = cursor.getString(0);
			traffic.uid = (int) cursor.getLong(1);
			traffic.tx = cursor.getLong(2);
			traffic.rx = cursor.getLong(3);
		}
		cursor.close();
		db.close();
		return traffic;
	}
	public void delete(String packageName) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete(TrafficDBHelper.TRAFFIC_NAME, "packageName=?", new String[]{packageName});
	}
	
	public Map<Integer, AppTraffic> findAllMap(){
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(TrafficDBHelper.TRAFFIC_NAME, new String[]{"packageName","uid","tx","rx"}, null, null, null, null, null);
		Map<Integer,AppTraffic> appTraffics = new HashMap<Integer,AppTraffic>();
		while (cursor.moveToNext()) {
			AppTraffic traffic = new AppTraffic();
			traffic.packageName = cursor.getString(0);
			traffic.uid = (int) cursor.getLong(1);
			traffic.tx = cursor.getLong(2);
			traffic.rx = cursor.getLong(3);
			appTraffics.put(traffic.uid,traffic);
		}
		cursor.close();
		db.close();
		return appTraffics;
		
	}
	
	public AppTraffic findByUid(int uid) {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(TrafficDBHelper.TRAFFIC_NAME, new String[]{"packageName","uid","tx","rx"}, "uid=?", new String[]{uid+""}, null, null, null);
		AppTraffic traffic = null;
		if(cursor.moveToNext()) {
			traffic = new AppTraffic();
			traffic.packageName = cursor.getString(0);
			traffic.uid = (int) cursor.getLong(1);
			traffic.tx = cursor.getLong(2);
			traffic.rx = cursor.getLong(3);
		}
		cursor.close();
		db.close();
		return traffic;
	}
	
	public boolean add(Map<Integer,AppTraffic> appTrafficMap) {
		 
		SQLiteDatabase db = null;
		try {
			db = helper.getWritableDatabase();
			String sql = "insert into " + TrafficDBHelper.TRAFFIC_NAME + "("
					+ TrafficDBHelper.PACKAGE_NAME +","
					+ TrafficDBHelper.UID +","
					+ TrafficDBHelper.TX +","
					+ TrafficDBHelper.RX 
					+ ") " + "values(?,?,?,?)";
			SQLiteStatement stat = db.compileStatement(sql);
			db.beginTransaction();
			for (Entry<Integer, AppTraffic> entry : appTrafficMap.entrySet()) {
				AppTraffic appTraffic = entry.getValue();
				stat.bindString(1, appTraffic.packageName);
				stat.bindLong(2, appTraffic.uid);
				stat.bindLong(3, appTraffic.tx);
				stat.bindLong(4, appTraffic.rx);
				long result = stat.executeInsert();
				if (result < 0) {
					return false;
				}
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (null != db) {
					db.endTransaction();
					db.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	public void deleteAll() {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete(TrafficDBHelper.TRAFFIC_NAME, null, null);
		db.close();
	}
	
	public int getLastUpdateTime(int type) {
		int date = 0;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select date from " + TrafficDBHelper.TRAFFIC_DATE + " where type = ?", new String[]{type + ""});
		while(cursor.moveToNext()) {
		    date = (int) cursor.getLong(0);
		}
		cursor.close();
		db.close();
		return date;
	}
	
	public void updateTime(int type) {
		SQLiteDatabase db = helper.getWritableDatabase();
		String date = sdf.format(new Date());
		db.execSQL("update " + TrafficDBHelper.TRAFFIC_DATE + " set date = " + date + " where type = ?",new String[]{type+""});
		db.close();
	}
	
	public void addAllTr(AllTraffic traffic) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("type", traffic.type);
		values.put(TrafficDBHelper.TX, traffic.tx);
		values.put(TrafficDBHelper.RX, traffic.rx);
		db.insert(TrafficDBHelper.ALL_TRAFFIC, null, values);
		db.close();
	}
	
	public void deleteAllTr() {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete(TrafficDBHelper.ALL_TRAFFIC, null, null);
		db.close();
	}
	
	public AllTraffic findAllTr(int type) {
		SQLiteDatabase db = helper.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("select type,rx,tx from " + TrafficDBHelper.ALL_TRAFFIC + " where type = ?", new String[]{type+""});
		
		if(cursor.moveToNext()) {
			AllTraffic traffic = new AllTraffic();
			
			traffic.type = cursor.getInt(0);
			traffic.rx = cursor.getLong(1);
			traffic.tx = cursor.getLong(2);
			cursor.close();
			db.close();
			return traffic;
		}
		cursor.close();
		db.close();
		
		return null;
	}
}
