package com.zengye.mobilesafe.domain;

import android.graphics.drawable.Drawable;

public class CacheInfo {

	private long cache;
	private Drawable icon;
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	private String packageName;
	private String name;
	public long getCache() {
		return cache;
	}
	public void setCache(long cache) {
		this.cache = cache;
	}
	 
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
