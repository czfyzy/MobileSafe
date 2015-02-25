package com.zengye.mobilesafe.activity;

import android.app.Activity;
//import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zengye.mobilesafe.R;
import com.zengye.mobilesafe.utils.MD5Utils;

public class AppLockActivity extends Activity {

	private PackageManager pm;
	private ImageView ivAppLockIcon;
	private EditText etAppLockPwd;
	private TextView tvAppLockName;
	private String name;
	private String pkgName;
	private SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_lock);
		ivAppLockIcon = (ImageView) findViewById(R.id.iv_app_lock_icon);
		etAppLockPwd = (EditText) findViewById(R.id.et_app_lock_pwd);
		tvAppLockName = (TextView) findViewById(R.id.tv_app_lock_name);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
		pm = getPackageManager();
		Intent intent = getIntent();
		
	    pkgName = intent.getStringExtra("pkgName");
		
		try {
			PackageInfo info = pm.getPackageInfo(pkgName, 0);
			ivAppLockIcon.setImageDrawable(info.applicationInfo.loadIcon(pm));
			name = (String) info.applicationInfo.loadLabel(pm);
			tvAppLockName.setText(name);
			
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onStop() {
		finish();
		super.onStop();
	}
	public void confirm(View view) {
		String input = etAppLockPwd.getText().toString();
		if(TextUtils.isEmpty(input)) {
			Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
			return;
		}
		String password = MD5Utils.encrypt(input);
		String savePwd = sp.getString("appLockPassword", null);
		if(password.equals(savePwd)) {
			Intent intent = new Intent();
			intent.setAction("com.zengye.mobile.applock.temp");
			intent.putExtra("pkgName", pkgName);
			sendBroadcast(intent);
			finish();
		} else {
			etAppLockPwd.setText("");
			Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
		}
		
	}
	public void cancel(View view) {
		backHome();
	}
	
//	private void close() {
//		// TODO Auto-generated method stub
//		
//		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//		am.killBackgroundProcesses(pkgName);
//		finish();
//	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		
		backHome();
	}

	private void backHome() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.addCategory(intent.CATEGORY_DEFAULT);
		intent.addCategory(Intent.CATEGORY_MONKEY);
		startActivity(intent);
	}
}
