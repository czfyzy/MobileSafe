package com.zengye.mobilesafe.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

public class SmsUtils {

	public interface SmsUtilsCallBack {
		public void doBefore(int max);

		public void doing(int process);
	}

	public static void bakupSms(Context context, SmsUtilsCallBack callBack)
			throws Exception {

		FileOutputStream fos;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File file = new File(Environment.getExternalStorageDirectory(),
					"zyMobileSafe");
			if (!file.exists()) {
				file.mkdirs();
			}
			File xmlFile = new File(file, "/bakupSms.xml");
			fos = new FileOutputStream(xmlFile);

		} else {
			return;
		}
		ContentResolver resolver = context.getContentResolver();
		XmlSerializer serializer = Xml.newSerializer();
		serializer.setOutput(fos, "utf-8");
		serializer.startDocument("utf-8", true);
		serializer.startTag(null, "smss");

		Uri uri = Uri.parse("content://sms/");
		Cursor cursor = resolver.query(uri, new String[] { "body", "address",
				"type", "date" }, null, null, null);
		int max = cursor.getCount();
		callBack.doBefore(max);
		serializer.attribute(null, "max", String.valueOf(max));
		int process = 0;
		while (cursor.moveToNext()) {
			String body = cursor.getString(0);
			String address = cursor.getString(1);
			String type = cursor.getString(2);
			String date = cursor.getString(3);
			serializer.startTag(null, "sms");
			serializer.startTag(null, "body");
			serializer.text(body);
			serializer.endTag(null, "body");

			serializer.startTag(null, "address");
			serializer.text(address);
			serializer.endTag(null, "address");

			serializer.startTag(null, "type");
			serializer.text(type);
			serializer.endTag(null, "type");

			serializer.startTag(null, "date");
			serializer.text(date);
			serializer.endTag(null, "date");
			serializer.endTag(null, "sms");
			process++;
			callBack.doing(process);
		}

		serializer.endTag(null, "smss");
		serializer.endDocument();
		fos.close();
	}

	public static void restoreSms(Context context, SmsUtilsCallBack callBack)
			throws Exception {
		File bakupFile = new File(Environment.getExternalStorageDirectory(),
				"zyMobileSafe/bakupSms.xml");
		if (bakupFile.exists() && bakupFile.length() > 0) {
			FileInputStream fis = new FileInputStream(bakupFile);
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(fis, "utf-8");
			int eventType = parser.getEventType();
			String body = null;
			String date = null;
			String type = null;
			String address = null;
			ContentResolver resolver = context.getContentResolver();
			Uri uri = Uri.parse("content://sms/");
			int process = 0;
			while (eventType != XmlPullParser.END_DOCUMENT) {

				switch (eventType) {
				case XmlPullParser.START_TAG:
					String name = parser.getName();

					if (name.equals("body")) {
						eventType = parser.next();
						body = parser.getText();
					} else if (name.equals("address")) {
						eventType = parser.next();
						address = parser.getText();
					} else if (name.equals("type")) {
						eventType = parser.next();
						type = parser.getText();
					} else if (name.equals("date")) {
						eventType = parser.next();
						date = parser.getText();
					} else if (name.equals("smss")) {
						int max = Integer.parseInt(parser.getAttributeValue(
								null, "max"));
						callBack.doBefore(max);
					}
					break;
				case XmlPullParser.END_TAG:

					if (parser.getName().equals("sms")) {
						ContentValues values = new ContentValues();
						values.put("body", body);
						values.put("address", address);
						values.put("type", type);
						values.put("date", date);
						resolver.insert(uri, values);
						process++;
						callBack.doing(process);
					}
					break;
				}

				eventType = parser.next();
			}
		} else {
			throw new FileNotFoundException("bakup file is not found");
		}

	}
}
