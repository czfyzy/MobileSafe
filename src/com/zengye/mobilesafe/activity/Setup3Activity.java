package com.zengye.mobilesafe.activity;

import com.zengye.mobilesafe.R;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Setup3Activity extends BaseSetUpActivity {

	private static final int REQUEST_CODE = 1;
	private EditText etSafePhone;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		etSafePhone = (EditText) findViewById(R.id.et_safe_phone);
		etSafePhone.setText(sp.getString("safePhone", ""));
	}

	public void showNext() {
		// TODO Auto-generated method stub
		String phone =  etSafePhone.getText().toString().trim().replace(" ", "");
		if(TextUtils.isEmpty(phone)) {
			Toast.makeText(this, "请输入安全号码", Toast.LENGTH_SHORT).show();
			return;
		}
		Editor editor = sp.edit();
		editor.putString("safePhone",phone);
		editor.commit();
		Intent intent = new Intent(this, Setup4Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	public void showPre() {

		// TODO Auto-generated method stub
		Intent intent = new Intent(this, Setup2Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}
	
	public void toSelectContact(View view) {
		Intent intent = new Intent(this, SelectContactActivity.class);
		startActivityForResult(intent, REQUEST_CODE);
		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(data == null)
			return;
		String phone = data.getStringExtra("phone").replace("-", "");
		etSafePhone.setText(phone);
		
	}
}
