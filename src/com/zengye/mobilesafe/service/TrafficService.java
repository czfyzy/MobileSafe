package com.zengye.mobilesafe.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zengye.mobilesafe.db.TrafficDBHelper;
import com.zengye.mobilesafe.db.dao.TrafficDBDao;
import com.zengye.mobilesafe.domain.AllTraffic;
import com.zengye.mobilesafe.domain.AppTraffic;
import com.zengye.mobilesafe.utils.CommonUtils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class TrafficService extends Service {

	private static final String DEV_PATH = "/proc/self/net/dev";
	private PackageManager pm;
	private TrafficDBDao dao;
	public static final int APP_TRAFFIC = 0;
	public static final int DATA_COMPLETE = 1;
	public static final int SAVE_ALL = 2;
	public static final int GET_ALL = 3;

	public static final String WIFI = "wlan";
	public static final String MOBILE_1 = "rmnet";
	public static final String MOBILE_2 = "ccmni";
	private static final String TAG = "TrafficService";

	private long wifiRx = 0;
	private long wifiTx = 0;
	private long mobileRx = 0;
	private long mobileTx = 0;

	private long lastWifiRx = 0;
	private long lastWifiTx = 0;
	private long lastMobileRx = 0;
	private long lastMobileTx = 0;

	private SharedPreferences sp;

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
	private long last;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case APP_TRAFFIC:
				update();
				handler.sendEmptyMessageDelayed(APP_TRAFFIC, 120000);
				break;
			case SAVE_ALL:
				saveAll();
				break;
			case DATA_COMPLETE:
				break;
			case GET_ALL:
				updateAll();
				handler.sendEmptyMessageDelayed(GET_ALL, 120000);
				break;

			}

		};
	};

	private int count = 60 * 10 / 3;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		pm = getPackageManager();
		dao = new TrafficDBDao(this);
		handler.sendEmptyMessage(APP_TRAFFIC);
		handler.sendEmptyMessage(GET_ALL);
		sp = this.getSharedPreferences("config", Context.MODE_PRIVATE);
		lastWifiRx = sp.getLong("lastWifiRx", 0);
		lastWifiTx = sp.getLong("lastWifiTx", 0);
		lastMobileRx = sp.getLong("lastMobileRx", 0);
		lastMobileTx = sp.getLong("lastMobileTx", 0);
	}

	@Override
	public void onDestroy() {
		handler.removeMessages(APP_TRAFFIC);
		handler.removeMessages(DATA_COMPLETE);
		handler.removeMessages(SAVE_ALL);
		handler.removeMessages(GET_ALL);

		Editor edit = sp.edit();
		edit.putLong("lastWifiRx", lastWifiRx);
		edit.putLong("lastWifiTx", lastWifiTx);
		edit.putLong("lastMobileRx", lastMobileRx);
		edit.putLong("lastMobileTx", lastMobileTx);
		edit.commit();

		super.onDestroy();
	}

	public void update() {
		List<ApplicationInfo> infos = pm.getInstalledApplications(0);
		boolean isNew = false;
		Map<Integer, AppTraffic> trafficMap;
		int date = dao.getLastUpdateTime(TrafficDBHelper.TYPE_APP);
		String now = sdf.format(new Date());
		if (now.equalsIgnoreCase(date + "")) {
			trafficMap = dao.findAllMap();
		} else {
			trafficMap = new HashMap<Integer, AppTraffic>();
			isNew = true;
			dao.updateTime(TrafficDBHelper.TYPE_APP);
		}

		for (ApplicationInfo info : infos) {
			int uid = info.uid;

			String rxPath = "/proc/uid_stat/" + uid + "/tcp_rcv";
			String txPath = "/proc/uid_stat/" + uid + "/tcp_snd";
			String rxStr = null;
			String txStr = null;
			try {
				rxStr = CommonUtils.readFile(rxPath);
				txStr = CommonUtils.readFile(txPath);
			} catch (Exception e1) {
				// TODO Auto-generated catch block

			}
			long rx = 0;
			if (rxStr != null) {
				rx = Long.parseLong(rxStr);

			}
			long tx = 0;
			if (txStr != null) {
				tx = Long.parseLong(txStr);

			}

			if (rx != 0 || tx != 0) {

				if (isNew && trafficMap.containsKey(uid)) {
					AppTraffic appTraffic = trafficMap.get(uid);
					appTraffic.rx = appTraffic.rx + rx;
					appTraffic.tx = appTraffic.tx + tx;
				} else {
					AppTraffic appTraffic = new AppTraffic();
					appTraffic.uid = uid;
					appTraffic.packageName = info.packageName;
					appTraffic.rx = rx;
					appTraffic.tx = tx;
					trafficMap.put(uid, appTraffic);
				}
			}
		}
		if (!trafficMap.isEmpty()) {
			dao.deleteAll();
			dao.add(trafficMap);
		}

	}

	private void updateAll() {
		// TODO Auto-generated method stub
		List<String> res = null;
		try {
			res = CommonUtils.readFileArr(DEV_PATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		if (res == null || res.size() == 0) {
			return;
		}
		res.remove(0);
		res.remove(0);
		long rx = 0;
		long tx = 0;

		mobileRx = 0;
		mobileTx = 0;
		wifiRx = 0;
		wifiTx = 0;
		for (String string : res) {
			String[] org = string.split(":");
			String str = org[1].trim();
			String row = org[0].trim();
			String[] tempData = str.trim().split(" ");
			List<String> dataList = new ArrayList<String>();
			for (String temp : tempData) {
				if (!temp.trim().equals("")) {
					dataList.add(temp);
				}
			}
			long perRx = Long.parseLong(dataList.get(0));
			long perTx = Long.parseLong(dataList.get(8));
			rx += perRx;
			tx += perTx;

			if (row.contains(MOBILE_1) || row.contains(MOBILE_2)) {

				mobileRx += perRx;
				mobileTx += perTx;
			} else if (row.contains(WIFI)) {
				wifiRx += perRx;
				wifiTx += perTx;
			}
		}
		Log.e(TAG, "mobileRx = " + mobileRx);
		Log.e(TAG, "mobileTx = " + mobileTx);
		Log.e(TAG, "wifiRx = " + wifiRx);
		Log.e(TAG, "wifiTx = " + wifiTx);
		count--;
		// if (count <= 0) {
		// count = 60 * 10 / 3;
		handler.sendEmptyMessage(SAVE_ALL);
		// }

	}

	private void saveAll() {
		int date = dao.getLastUpdateTime(TrafficDBHelper.TYPE_ALL);
		String now = sdf.format(new Date());
		AllTraffic wTr = null;
		AllTraffic mTr = null;
		if (now.equalsIgnoreCase(date + "")) {
			wTr = dao.findAllTr(TrafficDBHelper.TYPE_WIFI);
			mTr = dao.findAllTr(TrafficDBHelper.TYPE_MOBILE);
		} else {
			wTr = new AllTraffic();
			wTr.type = TrafficDBHelper.TYPE_WIFI;
			mTr = new AllTraffic();
			mTr.type = TrafficDBHelper.TYPE_MOBILE;
			dao.updateTime(TrafficDBHelper.TYPE_ALL);
		}

		if (lastMobileRx > 0) {
			long d = mobileRx - lastMobileRx;
			mTr.rx += d;
		} else {
			mTr.rx += mobileRx;
		}

		if (lastMobileTx > 0) {
			long d = mobileTx - lastMobileTx;
			mTr.tx += d;
		} else {
			mTr.tx += mobileTx;
		}

		if (lastWifiRx > 0) {
			long d = wifiRx - lastWifiRx;
			wTr.rx += d;
		} else {
			wTr.rx += wifiRx;
		}
		if (lastWifiTx > 0) {
			long d = wifiTx - lastWifiTx;
			wTr.tx += d;
		} else {
			wTr.tx += lastWifiTx;
		}

		dao.deleteAllTr();
		dao.addAllTr(mTr);
		dao.addAllTr(wTr);

		lastMobileRx = mobileRx;
		lastMobileTx = mobileTx;
		lastWifiRx = wifiRx;
		lastWifiTx = wifiTx;

	}
}
