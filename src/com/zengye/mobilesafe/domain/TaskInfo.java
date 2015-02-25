package com.zengye.mobilesafe.domain;

import android.graphics.drawable.Drawable;

public class TaskInfo {

	private Drawable icon;
	private String name;
	private String packageName;
	private long memSize;
	private boolean checked;
	private int pid;
	
	
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	private boolean isUserTask;
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
	public boolean isUserTask() {
		return isUserTask;
	}
	public void setUserTask(boolean isUserTask) {
		this.isUserTask = isUserTask;
	}
	public long getMemSize() {
		return memSize;
	}
	public void setMemSize(long memSize) {
		this.memSize = memSize;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
}
