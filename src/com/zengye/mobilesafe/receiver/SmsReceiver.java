package com.zengye.mobilesafe.receiver;

import com.zengye.mobilesafe.R;
import com.zengye.mobilesafe.utils.DeviceAdminUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {
	
//	private static final String TAG = "SMSReceiver";
	private SharedPreferences sp;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		for (Object object : objs) {
			SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
			
			String sender = sms.getOriginatingAddress();
			
			String safePhone = sp.getString("safePhone", "");
			
			String body = sms.getMessageBody();
			if(safePhone.equals(sender)) {
				//
				if("#*location*#".equals(body)) {
					String location = sp.getString("lastLocation", "geting location.....");
					SmsManager.getDefault().sendTextMessage(sender, null, location, null, null);
					abortBroadcast();
				} else if("#*alarm*#".equals(body)) {
					MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.alarm);
					mediaPlayer.setVolume(1.0f, 1.0f);
					mediaPlayer.setLooping(false);
					mediaPlayer.start();
					abortBroadcast();
				} else if("#*wipedata*#".equals(body)) {
					DeviceAdminUtils.wipeData(context);
					abortBroadcast();
				} else if(body.startsWith("#*lockscreen*#")) {
					int last = body.lastIndexOf("#");
					String password = "";
					if(last + 1 < body.length()) {
					   password = body.substring(last +1);
					}
					DeviceAdminUtils.lockscreen(context, password);
					abortBroadcast();
				}
			} 
		}
	}

}
