package com.zengye.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zengye.mobilesafe.R;
import com.zengye.mobilesafe.db.dao.AppLockDBDao;
import com.zengye.mobilesafe.domain.AppInfo;
import com.zengye.mobilesafe.engine.AppInfoProvider;
import com.zengye.mobilesafe.utils.DensityUtils;

public class AppManagerActivity extends Activity implements android.view.View.OnClickListener{

	private LinearLayout llLoading;
	private ListView lvAppList;

	private TextView romAvailable;
	private TextView sdAvailable;
	private List<AppInfo> infos;
	private List<AppInfo> systemAppInfos;
	private List<AppInfo> userAppInfos;
	private AppManagerAdapter adapter;
	private PopupWindow popupWindow;
	private LinearLayout llShare;
	private LinearLayout llUninstall;
	private LinearLayout llStart;
	private AppInfo appInfo;
	private AppLockDBDao dao;
	

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			llLoading.setVisibility(View.INVISIBLE);
			// 加载listview的数据
			if (adapter == null) {
				adapter = new AppManagerAdapter();
				lvAppList.setAdapter(adapter);
			} else {
				adapter.notifyDataSetChanged();
			}
		};
	};
	private TextView tvAppFlag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		romAvailable = (TextView) findViewById(R.id.tv_rom_available);
		sdAvailable = (TextView) findViewById(R.id.tv_sd_available);
		dao = new AppLockDBDao(this);
		long sdSize = getAvailSpace(Environment.getExternalStorageDirectory()
				.getAbsolutePath());
		long romSize = getAvailSpace(Environment.getDataDirectory()
				.getAbsolutePath());
		llLoading = (LinearLayout) findViewById(R.id.ll_loading);
		llLoading.setVisibility(View.VISIBLE);
		romAvailable.setText(Formatter.formatFileSize(this, romSize));
		sdAvailable.setText(Formatter.formatFileSize(this, sdSize));
		lvAppList = (ListView) findViewById(R.id.lv_app_list);
		tvAppFlag = (TextView) findViewById(R.id.tv_app_flag);
		fillData();
		lvAppList.setOnScrollListener(new AppListOnScrollListener());
		lvAppList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
//				if(position == 0 || position == userAppInfos.size() + 1) {
//					return;
//				} else if (position < userAppInfos.size() + 1) {
//					
//					appInfo = userAppInfos.get(position - 1);
//					
//				} else {
//					appInfo = systemAppInfos.get(position - userAppInfos.size() - 2);
//				}
				appInfo = (AppInfo) parent.getItemAtPosition(position);
				if (appInfo != null) {
					// contentview 要显示的view对象. width 宽度 height 高度
					View contentView = View.inflate(getApplicationContext(),
							R.layout.popup_app_item, null);
					llShare = (LinearLayout) contentView.findViewById(R.id.ll_share);
					llStart = (LinearLayout) contentView.findViewById(R.id.ll_start);
					llUninstall = (LinearLayout) contentView.findViewById(R.id.ll_uninstall);
					
					llShare.setOnClickListener(AppManagerActivity.this);
					llStart.setOnClickListener(AppManagerActivity.this);
					llUninstall.setOnClickListener(AppManagerActivity.this);
					
					
					dismissPopupWindows();
					popupWindow = new PopupWindow(contentView,
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT);
					popupWindow.setBackgroundDrawable(new ColorDrawable(
							Color.TRANSPARENT));
					
					int[] location = new int[2];
					view.getLocationInWindow(location);
					popupWindow.showAtLocation(parent, Gravity.LEFT
							| Gravity.TOP,
							DensityUtils.dip2px(AppManagerActivity.this, 60),
							location[1]);
					AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
					aa.setDuration(300);
					ScaleAnimation sa = new ScaleAnimation(0.3f, 1.0f, 0.3f, 1.0f, Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF, 0.5f);
					sa.setDuration(300);
					AnimationSet set = new AnimationSet(true);
					set.addAnimation(sa);
					set.addAnimation(aa);
					contentView.startAnimation(set);
				}
			}

		});
		
		lvAppList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if(position == 0 || position == userAppInfos.size() + 1){
					return true;
				}
				AppInfo appInfo = (AppInfo) parent.getItemAtPosition(position);
				if(appInfo != null) {
					String pkgName = appInfo.getPackageName();
					ViewHolder holder = (ViewHolder) view.getTag();
					if(dao.find(pkgName)) {
						dao.delete(pkgName);
						holder.ivLockStatus.setImageResource(R.drawable.unlock);
					} else {
						dao.add(pkgName);
						holder.ivLockStatus.setImageResource(R.drawable.lock);
					}
				}
				return true;
			}
		});
	}

	private void dismissPopupWindows() {
		// TODO Auto-generated method stub
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			popupWindow = null;
		}
	}

	private void fillData() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				infos = AppInfoProvider.getAppInfos(AppManagerActivity.this);
				userAppInfos = new ArrayList<AppInfo>();
				systemAppInfos = new ArrayList<AppInfo>();
				for (AppInfo info : infos) {
					if (info.isUserApp()) {
						userAppInfos.add(info);
					} else {
						systemAppInfos.add(info);
					}
				}
				handler.sendEmptyMessage(0);
			}
		}).start();
	}

	class AppManagerAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return infos.size() + 2;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			if (position == 0) {
				return null;
			} else if (position <= userAppInfos.size()) {
				return userAppInfos.get(position - 1);
			} else if (position == userAppInfos.size() + 1) {
				return null;
			} else {
				return systemAppInfos.get(position - userAppInfos.size() - 1
						- 1);
			}
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			AppInfo appInfo;
			if (position == 0) {
				TextView tv = new TextView(AppManagerActivity.this);
				tv.setBackgroundColor(0x9957ca03);
				tv.setTextColor(Color.WHITE);
				tv.setText("用户应用：" + userAppInfos.size() + "个");
				return tv;
			} else if (position <= userAppInfos.size()) {
				appInfo = userAppInfos.get(position - 1);
			} else if (position == userAppInfos.size() + 1) {
				TextView tv = new TextView(AppManagerActivity.this);
				tv.setBackgroundColor(0x9957ca03);
				tv.setTextColor(Color.WHITE);
				tv.setText("系统应用：" + systemAppInfos.size() + "个");
				return tv;
			} else {
				appInfo = systemAppInfos.get(position - userAppInfos.size() - 1
						- 1);
			}
			View view;
			ViewHolder holder;
			if (convertView != null & convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (ViewHolder) view.getTag();

			} else {
				view = View.inflate(AppManagerActivity.this,
						R.layout.app_manager_item, null);
				holder = new ViewHolder();
				holder.ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
				holder.tvLoaction = (TextView) view
						.findViewById(R.id.tv_location);
				holder.tvName = (TextView) view.findViewById(R.id.tv_name);
				holder.ivLockStatus = (ImageView) view.findViewById(R.id.iv_lock_status);
				view.setTag(holder);
			}
			holder.ivIcon.setImageDrawable(appInfo.getIcon());
			
			holder.tvName.setText(appInfo.getName());
			if (appInfo.isInRom()) {
				holder.tvLoaction.setText("手机内存");
			} else {
				holder.tvLoaction.setText("外部存储卡");
			}
			if(dao.find(appInfo.getPackageName())) {
				holder.ivLockStatus.setImageResource(R.drawable.lock);
			} else {
				holder.ivLockStatus.setImageResource(R.drawable.unlock);
			}
			return view;
		}

	}

	class ViewHolder {
		public TextView tvName;
		public TextView tvLoaction;
		public ImageView ivIcon;
		public ImageView ivLockStatus;
	}

	private long getAvailSpace(String path) {
		StatFs statFs = new StatFs(path);
		int size = statFs.getBlockSize();
		int count = statFs.getAvailableBlocks();
		return size * count;
	}

	class AppListOnScrollListener implements OnScrollListener {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// TODO Auto-generated method stub
			dismissPopupWindows();
			if (userAppInfos != null && systemAppInfos != null) {
				if (firstVisibleItem == 0
						|| firstVisibleItem == userAppInfos.size() + 1) {
					tvAppFlag.setVisibility(View.INVISIBLE);
				} else {
					tvAppFlag.setVisibility(View.VISIBLE);
					if (firstVisibleItem > userAppInfos.size()) {
						tvAppFlag
								.setText("系统程序：" + systemAppInfos.size() + "个");
					} else {
						tvAppFlag.setText("用户程序：" + userAppInfos.size() + "个");
					}
				}
			}

		}

	}

	@Override
	protected void onDestroy() {
		dismissPopupWindows();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		dismissPopupWindows();
		switch (v.getId()) {
		case R.id.ll_share:
			shareApp();
			break;
		case R.id.ll_start:
			startApp();
			break;
		case R.id.ll_uninstall:
			uninstallApp();
			break;

		
		}
	}

	private void uninstallApp() {
		// TODO Auto-generated method stub
		if(appInfo.isUserApp()) {
			Intent intent = new Intent();
			intent.setAction("android.intent.action.DELETE");
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			intent.setData(Uri.parse("package:" + appInfo.getPackageName()));
			startActivityForResult(intent, 0);
		} else {
			Toast.makeText(this, "系统应用只有获取root权限后才能卸载", Toast.LENGTH_SHORT).show();
		}
	}

	private void startApp() {
		// TODO Auto-generated method stub
		PackageManager pm = getPackageManager();
		
		Intent intent = pm.getLaunchIntentForPackage(appInfo.getPackageName());
		if(intent != null) {
			startActivity(intent);
		} else {
			Toast.makeText(this, "当前应用程序没有界面.", 0).show();
		}
	}

	private void shareApp() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setAction("android.intent.action.SEND");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT,
				"推荐您使用一款软件,下载地址为:https://play.google.com/store/apps/details?id="
						+ appInfo.getPackageName());
		startActivity(intent);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		fillData();
	}
}
