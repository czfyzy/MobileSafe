package com.zengye.mobilesafe.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TrafficDBHelper extends SQLiteOpenHelper {

	public static final String DATA_BASE_NAME = "traffic.db";
	public static final String TRAFFIC_NAME = "TRAFFIC";
	public static final String ALL_TRAFFIC = "ALL_TRAFFIC";
	public static final String TRAFFIC_DATE = "traffic_date";
	public static final int DATA_BASE_VERSION = 2;
	public static final String PACKAGE_NAME = "packageName";
	public static final String UID = "uid";
	public static final String RX = "rx";
	public static final String TX = "tx";
	public static final int TYPE_WIFI = 0;
	public static final int TYPE_MOBILE = 1;
	
	public static final int TYPE_APP = 0;
	public static final int TYPE_ALL = 1;
	
	public TrafficDBHelper(Context context) {
		super(context, DATA_BASE_NAME, null, DATA_BASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table "+TRAFFIC_NAME+" (_id integer primary key autoincrement,packageName varchar(50), tx long(30),rx long(30),uid Integer(10))");
		
		db.execSQL("create table "+ALL_TRAFFIC+" (_id integer primary key autoincrement,tx long(30),rx long(30),type varchar(1))");
		db.execSQL("create table "+TRAFFIC_DATE+" (_id integer primary key autoincrement,type varchar(1), date long(10))");
		
		ContentValues values = new ContentValues();
		values.put("date", 0);
		values.put("type", 0);
		db.insert(TRAFFIC_DATE, null, values);
		values.clear();
		values.put("date", 0);
		values.put("type", 1);
		db.insert(TRAFFIC_DATE, null, values);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("drop table " + TRAFFIC_DATE);
		
		db.execSQL("create table "+ALL_TRAFFIC+" (_id integer primary key autoincrement,tx long(30),rx long(30),type varchar(1))");
		db.execSQL("create table "+TRAFFIC_DATE+" (_id integer primary key autoincrement,type varchar(1), date long(10))");
		ContentValues values = new ContentValues();
		values.put("date", 0);
		values.put("type", 0);
		db.insert(TRAFFIC_DATE, null, values);
		
		values.clear();
		values.put("date", 0);
		values.put("type", 1);
		db.insert(TRAFFIC_DATE, null, values);
	}

}
