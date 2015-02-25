package com.zengye.mobilesafe.ui;

import com.zengye.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingClickItemView extends RelativeLayout {

	private TextView descTV;
	private TextView titleTV;
	private String title;
	private String desc;
	
	public SettingClickItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initView(context);
	}

	public SettingClickItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		title = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.zengye.mobilesafe", "setting_title");
		desc = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.zengye.mobilesafe", "desc");
		titleTV.setText(title);
		setDesc(desc);
		// TODO Auto-generated constructor stub
		
	}

	public SettingClickItemView(Context context) {
		super(context);
		initView(context);
		// TODO Auto-generated constructor stub
	}

	public void initView(Context context){
		View.inflate(context, R.layout.setting_click_item_view, this);
		titleTV = (TextView) this.findViewById(R.id.setting_title);
		descTV = (TextView) this.findViewById(R.id.setting_desc);
	}
	
	public void setDesc(CharSequence text) {
		this.descTV.setText(text);
	}
}
