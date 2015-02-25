package com.zengye.mobilesafe.service;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.zengye.mobilesafe.db.dao.BlackNumberDBDao;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

public class BlackNumberService extends Service {

	public static final String TAG = "BlackNumberService";
	private BlackNumberDBDao dao;
	private BlackNumberSmsReceiver smsReceiver;
	private TelephonyManager tm;
	private BlackNumberListener blackNumberListener;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub

		super.onCreate();
		dao = new BlackNumberDBDao(this);
		smsReceiver = new BlackNumberSmsReceiver();
		IntentFilter smsFilter = new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED");
		smsFilter.setPriority(1000);
		registerReceiver(smsReceiver, smsFilter);

		tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		blackNumberListener = new BlackNumberListener();
		tm.listen(blackNumberListener, PhoneStateListener.LISTEN_CALL_STATE);
		Log.i(TAG, "开启黑名单拦截服务");
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(smsReceiver);
		tm.listen(blackNumberListener, PhoneStateListener.LISTEN_NONE);
		super.onDestroy();
	}

	class BlackNumberSmsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Object[] objs = (Object[]) intent.getExtras().get("pdus");
			for (Object object : objs) {
				SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
				String sender = sms.getOriginatingAddress();
				String mode = dao.findMode(sender);
				if ("2".equals(mode) || "3".equals(mode)) {
					abortBroadcast();
				}

			}
		}
	}

	class BlackNumberListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				String result = dao.findMode(incomingNumber);
				if ("1".equals(result) || "3".equals(result)) {
					getContentResolver().registerContentObserver(
							Uri.parse("content://call_log/calls"), true,
							new CallLogObserver(incomingNumber, new Handler()));
					endCall();
				}
				break;
			}
			super.onCallStateChanged(state, incomingNumber);

		}
	}

	public void endCall() {
		// IBinder iBinder = ServiceManager.getService(TELEPHONY_SERVICE);
		try {
			// 加载servicemanager的字节码
			Class clazz = BlackNumberService.class.getClassLoader().loadClass(
					"android.os.ServiceManager");
			Method method = clazz.getDeclaredMethod("getService", String.class);
			IBinder ibinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
			ITelephony.Stub.asInterface(ibinder).endCall();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void deleteCallLog(String incomingNumber) {
		Uri uri = Uri.parse("content://call_log/calls");
		ContentResolver resolver = getContentResolver();
		resolver.delete(uri, "number = ?", new String[] { incomingNumber });

	}

	class CallLogObserver extends ContentObserver {

		private String incomingNumber;

		public CallLogObserver(String incomingNumber, Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
			this.incomingNumber = incomingNumber;
		}

		@Override
		public void onChange(boolean selfChange) {
			// TODO Auto-generated method stub
			getContentResolver().unregisterContentObserver(this);
			deleteCallLog(incomingNumber);
			super.onChange(selfChange);
		}
	}
}
