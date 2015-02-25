package com.zengye.mobilesafe.domain;

import android.graphics.drawable.Drawable;

public class TrafficInfo {

	/**
	 * 上传
	 */
	private long tx;
	/**
	 * 下载
	 */
	private long rx;
	
	private String name;
	
	private String packageName;
	
	private Drawable icon;

	public long getTx() {
		return tx;
	}

	public void setTx(long tx) {
		this.tx = tx;
	}

	public long getRx() {
		return rx;
	}

	public void setRx(long rx) {
		this.rx = rx;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	
	
}
