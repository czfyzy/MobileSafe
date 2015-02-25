package com.zengye.mobilesafe.activity;

import com.zengye.mobilesafe.R;
import com.zengye.mobilesafe.utils.SmsUtils;
import com.zengye.mobilesafe.utils.SmsUtils.SmsUtilsCallBack;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class AtoolsAvtivity extends Activity {

	private ProgressDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
		dialog = new ProgressDialog(this);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	}

	public void phoneQuery(View view) {
		Intent intent = new Intent(this, PhoneAddressActivity.class);
		startActivity(intent);
	}

	public void bakupSms(View view) {
		dialog.setMessage("正在备份短信");
		
		dialog.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					SmsUtils.bakupSms(AtoolsAvtivity.this,new SmsUtilsCallBack() {
						
						@Override
						public void doing(int process) {
							// TODO Auto-generated method stub
							dialog.setProgress(process);
						}
						
						@Override
						public void doBefore(int max) {
							// TODO Auto-generated method stub
							dialog.setMax(max);
						}
					});
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							dialog.dismiss();
							Toast.makeText(AtoolsAvtivity.this, "备份成功",
									Toast.LENGTH_SHORT).show();
						}
					});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							Toast.makeText(AtoolsAvtivity.this, "备份失败",
									Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		}).start();
	}

	public void restoreSms(View view) {
		
		dialog.setMessage("正在恢复短信");
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					SmsUtils.restoreSms(AtoolsAvtivity.this,new SmsUtilsCallBack() {
						
						@Override
						public void doing(int process) {
							// TODO Auto-generated method stub
							dialog.setProgress(process);
						}
						
						@Override
						public void doBefore(int max) {
							// TODO Auto-generated method stub
							dialog.setMax(max);
						}
					});
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							dialog.dismiss();
							Toast.makeText(AtoolsAvtivity.this, "恢复成功",
									Toast.LENGTH_SHORT).show();
						}
					});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub\
							dialog.dismiss();
							Toast.makeText(AtoolsAvtivity.this, "恢复失败",
									Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		}).start();
	}
}
