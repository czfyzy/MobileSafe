package com.zengye.mobilesafe.activity;

import com.zengye.mobilesafe.R;
import com.zengye.mobilesafe.service.AutoClearTaskService;
import com.zengye.mobilesafe.ui.SettingItemView;
import com.zengye.mobilesafe.utils.ServiceUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class TaskSettingActivity extends Activity {

	private SettingItemView sivSystemTask;
	private SettingItemView sivAutoClear;
	private SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_setting);
		sivSystemTask = (SettingItemView) findViewById(R.id.siv_system_task);
		sivAutoClear = (SettingItemView) findViewById(R.id.siv_auto_clear);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		sivSystemTask.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				sivSystemTask.setChecked(!sivSystemTask.isChecked());
				
				Editor editor = sp.edit();
				editor.putBoolean("systemTaskVisible", sivSystemTask.isChecked());
				editor.commit();
			}
		});
		sivAutoClear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(TaskSettingActivity.this, AutoClearTaskService.class);
				if(sivAutoClear.isChecked()) {
					
					stopService(intent);
					sivAutoClear.setChecked(false);
				} else {
					startService(intent);
					sivAutoClear.setChecked(true);
				}
			}
		});
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		boolean systemTask = sp.getBoolean("systemTaskVisible",false);
		sivSystemTask.setChecked(systemTask);
		
		sivAutoClear.setChecked(ServiceUtils.isServiceRunning(this, AutoClearTaskService.class.getName()));
	}
}
