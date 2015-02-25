package com.zengye.mobilesafe.service;

import com.zengye.mobilesafe.R;
import com.zengye.mobilesafe.db.dao.PhoneAddressDaoUtils;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

public class PhoneAddressService extends Service {

	private TelephonyManager tm;
	private MyPhoneStateListener mPhoneStateListener;
	private OutCallReceiver receiver;
	private WindowManager wm;
	private View view;
	private SharedPreferences sp;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		mPhoneStateListener = new MyPhoneStateListener();
		tm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		receiver = new OutCallReceiver();
		IntentFilter filter = new IntentFilter(
				"android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(receiver, filter);

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		tm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
		mPhoneStateListener = null;
		if (receiver != null) {
			unregisterReceiver(receiver);
		}
		super.onDestroy();

	}

	public class MyPhoneStateListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				String address = PhoneAddressDaoUtils
						.queryAddress(incomingNumber);
				mToast(address);
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				if (view != null) {
					wm.removeView(view);
				}
				break;

			default:
				break;
			}
		}
	}

	public class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String phoneNo = getResultData();
			String address = PhoneAddressDaoUtils.queryAddress(phoneNo);
			mToast(address);
		}

	}

	private WindowManager.LayoutParams params;

	private long[] mHits = new long[3];

	private void mToast(String address) {
		view = View.inflate(this, R.layout.toast_phone_address, null);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				mHits[mHits.length - 1] = SystemClock.uptimeMillis();
				if (mHits[0] > (mHits[mHits.length - 1] - 500)) {
					params.x = (wm.getDefaultDisplay().getWidth() - view
							.getWidth()) / 2;
					wm.updateViewLayout(view, params);
					Editor editor = sp.edit();
					editor.putInt("lastX", params.x);
					editor.putInt("lastY", params.y);
					editor.commit();
				}
			}
		});
		view.setOnTouchListener(new OnTouchListener() {
			int startX;
			int startY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();

					break;
				case MotionEvent.ACTION_MOVE:
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();
					int dx = newX - startX;
					int dy = newY - startY;

					params.x += dx;
					params.y += dy;
					// 边界问题
					if (params.x < 0) {
						params.x = 0;
					}
					if (params.y < 0) {
						params.y = 0;
					}

					int dWidth = wm.getDefaultDisplay().getWidth()
							- view.getWidth();
					int dHeight = wm.getDefaultDisplay().getHeight()
							- view.getHeight();
					if (params.x > dWidth) {
						params.x = dWidth;
					}
					if (params.y > dHeight) {
						params.y = dHeight;
					}
					if (view != null) {
						wm.updateViewLayout(view, params);
					}
					startX = newX;
					startY = newY;
					break;
				case MotionEvent.ACTION_UP:
					Editor editor = sp.edit();
					editor.putInt("lastX", params.x);
					editor.putInt("lastY", params.y);
					editor.commit();
					break;

				default:
					break;
				}
				return false;
			}
		});
		// "半透明","活力橙","卫士蓝","金属灰","苹果绿"
		int[] ids = { R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };

		view.setBackgroundResource(ids[sp.getInt("phoneAddressColor", 0)]);

		TextView tvAddress = (TextView) view
				.findViewById(R.id.tv_toast_address);
		tvAddress.setText(address);
		params = new WindowManager.LayoutParams();

		params.gravity = Gravity.TOP + Gravity.LEFT;
		params.x = sp.getInt("lastX", 0);
		params.y = sp.getInt("lastY", 0);

		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		wm.addView(view, params);
	}
}
