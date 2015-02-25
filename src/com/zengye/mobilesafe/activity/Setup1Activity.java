package com.zengye.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.zengye.mobilesafe.R;

public class Setup1Activity extends BaseSetUpActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
		
	}
	
	public void next(View view) {
		// TODO Auto-generated method stub
		showNext();
	}

	public void showNext() {
		Intent intent = new Intent(this, Setup2Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	@Override
	public void showPre() {
		// TODO Auto-generated method stub
		
	}
}
