package com.zengye.mobilesafe.activity;

import com.zengye.mobilesafe.R;
import com.zengye.mobilesafe.service.AppLockService;
import com.zengye.mobilesafe.service.BlackNumberService;
import com.zengye.mobilesafe.service.PhoneAddressService;
import com.zengye.mobilesafe.ui.SettingClickItemView;
import com.zengye.mobilesafe.ui.SettingItemView;
import com.zengye.mobilesafe.utils.MD5Utils;
import com.zengye.mobilesafe.utils.ServiceUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingActivity extends Activity {

	private SettingItemView autoUpdate;
	private SharedPreferences sp;
	private static final String KEY = "autoUpdate";
	private SettingItemView sivPhoneAddress;
	private Intent intentPAS;
	private SettingClickItemView scivColor;
	private SettingItemView sivBlackNumber;
	private Intent intentBN;
	private SettingItemView sivApplock;
	private Intent intentAL;
	private AlertDialog dialog;
	private EditText etPwd;
	private EditText etConfirm;
	private Button btConfirm;
	private Button btCancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean flag = sp.getBoolean(KEY, false);
		autoUpdate = (SettingItemView) findViewById(R.id.is_auto_update);
		autoUpdate.setChecked(flag);

		sivPhoneAddress = (SettingItemView) findViewById(R.id.siv_phone_address);
		sivPhoneAddress.setChecked(ServiceUtils.isServiceRunning(this,
				PhoneAddressService.class.getName()));

		intentPAS = new Intent(this, PhoneAddressService.class);
		sivPhoneAddress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (sivPhoneAddress.isChecked()) {
					sivPhoneAddress.setChecked(false);
					stopService(intentPAS);
				} else {
					sivPhoneAddress.setChecked(true);
					startService(intentPAS);
				}
			}
		});

		autoUpdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Editor editor = sp.edit();
				if (autoUpdate.isChecked()) {
					autoUpdate.setChecked(false);
					editor.putBoolean(KEY, false);
				} else {
					autoUpdate.setChecked(true);
					editor.putBoolean(KEY, true);
				}
				editor.commit();
			}
		});
		int color = sp.getInt("phoneAddressColor", 0);
		final String[] items = { "半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿" };

		scivColor = (SettingClickItemView) findViewById(R.id.sciv_phone_address_color);
		scivColor.setDesc(items[color]);

		scivColor.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int which = sp.getInt("phoneAddressColor", 0);
				// 弹出一个对话框
				AlertDialog.Builder builder = new Builder(SettingActivity.this);
				builder.setTitle("归属地提示框风格");
				builder.setSingleChoiceItems(items, which,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								// 保存选择参数
								Editor editor = sp.edit();
								editor.putInt("phoneAddressColor", which);
								editor.commit();
								scivColor.setDesc(items[which]);

								// 取消对话框
								dialog.dismiss();
							}
						});
				builder.setNegativeButton("取消", null);
				builder.show();
			}
		});
		sivBlackNumber = (SettingItemView) findViewById(R.id.is_open_black_block);
		sivBlackNumber.setChecked(ServiceUtils.isServiceRunning(this,
				BlackNumberService.class.getName()));
		intentBN = new Intent(this, BlackNumberService.class);
		sivBlackNumber.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (sivBlackNumber.isChecked()) {
					stopService(intentBN);
					sivBlackNumber.setChecked(false);
				} else {
					startService(intentBN);
					sivBlackNumber.setChecked(true);
				}
			}
		});

		sivApplock = (SettingItemView) findViewById(R.id.siv_open_app_lock);
		intentAL = new Intent(this, AppLockService.class);
		sivApplock.setChecked(ServiceUtils.isServiceRunning(SettingActivity.this,
						AppLockService.class.getName()));
		sivApplock.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (ServiceUtils.isServiceRunning(SettingActivity.this,
						AppLockService.class.getName())) {
					stopService(intentAL);
					sivApplock.setChecked(false);
				} else {
					String password = sp.getString("appLockPassword", null);
					if (TextUtils.isEmpty(password)) {
						showPasswordDialog();
					} else {
						startService(intentAL);
						sivApplock.setChecked(true);
					}
				}
			}
		});
	}

	protected void showPasswordDialog() {
		// TODO Auto-generated method stub
		/**
		 * 设置密码对话框
		 */

		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new Builder(this);

		View view = View.inflate(this, R.layout.dialog_setup_pwd, null);

		etPwd = (EditText) view.findViewById(R.id.et_set_pwd);
		etConfirm = (EditText) view.findViewById(R.id.et_set_pwd_confirm);
		btConfirm = (Button) view.findViewById(R.id.confirm);
		btCancel = (Button) view.findViewById(R.id.cancel);

		dialog = builder.create();
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();

		btCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		btConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String password = etPwd.getText().toString().trim();
				String pwdConfirm = etConfirm.getText().toString().trim();
				if (TextUtils.isEmpty(password)
						|| TextUtils.isEmpty(pwdConfirm)) {
					Toast.makeText(SettingActivity.this, "密码不能为空",
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (!password.equals(pwdConfirm)) {
					Toast.makeText(SettingActivity.this, "两次输入密码不一致",
							Toast.LENGTH_SHORT).show();
					return;
				}
				Editor editor = sp.edit();
				editor.putString("appLockPassword", MD5Utils.encrypt(password));
				editor.commit();
				dialog.dismiss();
				startService(intentAL);
				sivApplock.setChecked(true); 
				
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		sivPhoneAddress.setChecked(ServiceUtils.isServiceRunning(this,
				PhoneAddressService.class.getName()));
		sivBlackNumber.setChecked(ServiceUtils.isServiceRunning(this,
				BlackNumberService.class.getName()));
	}
}
