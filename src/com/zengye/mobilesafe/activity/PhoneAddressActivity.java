package com.zengye.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zengye.mobilesafe.R;
import com.zengye.mobilesafe.db.dao.PhoneAddressDaoUtils;

public class PhoneAddressActivity extends Activity{

	private EditText etPhoneNo;
	private TextView tvAddressResult;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_address);
		etPhoneNo = (EditText) findViewById(R.id.et_phone_no);
		tvAddressResult = (TextView) findViewById(R.id.tv_address_result);
		etPhoneNo.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(s != null && s.length() > 2) {
					String address = PhoneAddressDaoUtils.queryAddress(s.toString());
					tvAddressResult.setText(address);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public void queryAddress(View view) {
		String phoneNo = etPhoneNo.getText().toString().trim();
		if(TextUtils.isEmpty(phoneNo)) {
			Toast.makeText(this, "请输入电话号码", Toast.LENGTH_SHORT).show();
			return;
		}
		String address = PhoneAddressDaoUtils.queryAddress(phoneNo);
		tvAddressResult.setText(address);
	}
	
}
