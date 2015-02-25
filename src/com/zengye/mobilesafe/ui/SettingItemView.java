package com.zengye.mobilesafe.ui;

import com.zengye.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingItemView extends RelativeLayout {
	private CheckBox checkBox;
	private TextView descTV;
	private TextView titleTV;
	private String title;
	private String descOn;
	private String descOff;
	
	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initView(context);
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		title = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.zengye.mobilesafe", "setting_title");
		descOn = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.zengye.mobilesafe", "desc_on");
		descOff = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.zengye.mobilesafe", "desc_off");
		titleTV.setText(title);
		setDesc(descOff);
		// TODO Auto-generated constructor stub
		
	}

	public SettingItemView(Context context) {
		super(context);
		initView(context);
		// TODO Auto-generated constructor stub
	}

	public void initView(Context context){
		View.inflate(context, R.layout.setting_item_view, this);
		checkBox = (CheckBox) this.findViewById(R.id.setting_cb);
		titleTV = (TextView) this.findViewById(R.id.setting_title);
		descTV = (TextView) this.findViewById(R.id.setting_desc);
	}
	
	public boolean isChecked() {
		return checkBox.isChecked();
	}
	
	public void setChecked(boolean checked) {
		if(checked) {
			setDesc(descOn);
		} else {
			setDesc(descOff);

		}
		this.checkBox.setChecked(checked);
	}
	public void setDesc(CharSequence text) {
		this.descTV.setText(text);
	}
}
