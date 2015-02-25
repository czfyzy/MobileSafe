package com.zengye.mobilesafe.activity;

import com.zengye.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LostFindActivity extends Activity {

	private SharedPreferences sp;
	private TextView tvSafePhone;
	private ImageView ivProtecting;
	private static final String IS_PROTECTING = "isProtecting";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean configed = sp.getBoolean("configed", false);
		if (configed) {
			setContentView(R.layout.activity_lost_find);
			boolean isProtecting = sp.getBoolean(IS_PROTECTING, false);
			String safePhone = sp.getString("safePhone", "未设置");

			tvSafePhone = (TextView) findViewById(R.id.tv_safe_phone);
			tvSafePhone.setText(safePhone);

			ivProtecting = (ImageView) findViewById(R.id.iv_protecting);

			if (isProtecting) {
				ivProtecting.setImageResource(R.drawable.lock);
			} else {
				ivProtecting.setImageResource(R.drawable.unlock);
			}

		} else {
			Intent intent = new Intent(this, Setup1Activity.class);
			startActivity(intent);
			finish();
		}

	}

	public void reEnterGuide(View view) {
		Intent intent = new Intent(this, Setup1Activity.class);
		startActivity(intent);
		finish();
	}

	public void protecting(View view) {
		boolean isProtecting = sp.getBoolean(IS_PROTECTING, false);
		if (!isProtecting) {
			String simSn = sp.getString("simSn", null);
			String safePhone = sp.getString("safePhone", null);
			if (TextUtils.isEmpty(safePhone) || TextUtils.isEmpty(simSn)) {
				Toast.makeText(this, "未绑定sim卡或未设置安全手机号码，不能开启手机防盗",
						Toast.LENGTH_LONG).show();
				return;
			}
		}
		if (isProtecting) {
			ivProtecting.setImageResource(R.drawable.unlock);
		} else {
			ivProtecting.setImageResource(R.drawable.lock);
		}
		Editor editor = sp.edit();
		editor.putBoolean(IS_PROTECTING, !isProtecting);
		editor.commit();
	}
}
