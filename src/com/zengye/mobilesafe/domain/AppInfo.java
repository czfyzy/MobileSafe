package com.zengye.mobilesafe.domain;

import android.graphics.drawable.Drawable;

public class AppInfo {

	private Drawable icon;
	private String name;
	private String packageName;
	private boolean inRom;
	private boolean userApp;
	private int uid;
	private String receiverClass;
	
	private boolean isAutoStart;
	
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
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
	public boolean isInRom() {
		return inRom;
	}
	public void setInRom(boolean inRom) {
		this.inRom = inRom;
	}
	public boolean isUserApp() {
		return userApp;
	}
	public void setUserApp(boolean userApp) {
		this.userApp = userApp;
	}
	public String getReceiverClass() {
		return receiverClass;
	}
	public void setReceiverClass(String receiverClass) {
		this.receiverClass = receiverClass;
	}
	
	@Override
	public String toString() {
		return "AppInfo [name=" + name + ", packageName=" + packageName
				+ ", inRom=" + inRom + ", userApp=" + userApp + "]";
	}
	public boolean isAutoStart() {
		return isAutoStart;
	}
	public void setAutoStart(boolean isAutoStart) {
		this.isAutoStart = isAutoStart;
	}
	
	
}
