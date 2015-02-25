package com.zengye.mobilesafe.receiver;

import com.zengye.mobilesafe.service.TrafficService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class BootCompletedReceiver extends BroadcastReceiver {

	private SharedPreferences sp;
	private TelephonyManager tm;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String saveSimSn = sp.getString("simSn", "");

		String currentSimSn = tm.getSimSerialNumber();

		if (!saveSimSn.equals(currentSimSn)) {
			if(sp.getBoolean("isProtecting", false)) {
				SmsManager.getDefault().sendTextMessage(
						sp.getString("safenumber", ""), null, "sim changing....",
						null, null);
			}
		}
		
		Editor edit = sp.edit();
		edit.putLong("lastWifiRx", 0);
		edit.putLong("lastWifiTx", 0);
		edit.putLong("lastMobileRx", 0);
		edit.putLong("lastMobileTx", 0);
		edit.commit();
		Intent intent2 = new Intent(context, TrafficService.class);
		context.startService(intent2);
	}

}
