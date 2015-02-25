package com.zengye.mobilesafe.activity;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.zengye.mobilesafe.R;
import com.zengye.mobilesafe.ui.SettingItemView;

public class Setup2Activity extends BaseSetUpActivity {

	private SettingItemView bindSim;
	private TelephonyManager tm;
	private static final String SIM_SN = "simSn";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		bindSim = (SettingItemView) findViewById(R.id.bind_sim_siv);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		String saveSimSn = sp.getString(SIM_SN, null);
		bindSim.setChecked(!TextUtils.isEmpty(saveSimSn));
		
		bindSim.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				if(bindSim.isChecked()) {
					bindSim.setChecked(false);
					editor.putString(SIM_SN, null);
					
				} else {
					bindSim.setChecked(true);
					String simSn = tm.getSimSerialNumber();
					editor.putString(SIM_SN, simSn);
				}
				editor.commit();
				
			}
		});
	}

	public void showNext() {
		// TODO Auto-generated method stub
		String simSn = sp.getString(SIM_SN, null);
		if(TextUtils.isEmpty(simSn)) {
			Toast.makeText(this, "手机防盗功能必须绑定sim", Toast.LENGTH_LONG).show();
			return;
		}
		Intent intent = new Intent(this, Setup3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	public void showPre() {

		// TODO Auto-generated method stub
		Intent intent = new Intent(this, Setup1Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_pre_in,R.anim.tran_pre_out);

	}
}
