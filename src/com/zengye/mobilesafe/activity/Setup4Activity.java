package com.zengye.mobilesafe.activity;

import com.zengye.mobilesafe.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

public class Setup4Activity extends BaseSetUpActivity {

	private SharedPreferences sp;
	private CheckBox cbProtecting;
	private static final String IS_PROTECTING = "isProtecting";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		cbProtecting = (CheckBox) findViewById(R.id.cb_protecting);
		boolean saveProtecting = sp.getBoolean(IS_PROTECTING, false);
		if(saveProtecting) {
			cbProtecting.setText("您已经开启了手机防盗");
		} else {
			cbProtecting.setText("您没有开启手机防盗");

		}
		cbProtecting.setChecked(saveProtecting);
		 
	}

	public void showPre() {

		// TODO Auto-generated method stub
		Intent intent = new Intent(this, Setup3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}

	public void showNext() {
		// TODO Auto-generated method stub
		Editor editor = sp.edit();
		editor.putBoolean("configed", true);
		editor.commit();
		Intent intent = new Intent(this, LostFindActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}
	public void protecting(View view) {
		boolean checked = cbProtecting.isChecked();
		if(checked) {
			cbProtecting.setText("您已经开启了手机防盗");
		} else {
			cbProtecting.setText("您没有开启手机防盗");
		}
		Editor editor = sp.edit();
		editor.putBoolean(IS_PROTECTING, checked);
		editor.commit();
	}
}
