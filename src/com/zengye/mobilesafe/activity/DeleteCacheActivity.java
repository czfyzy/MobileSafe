package com.zengye.mobilesafe.activity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.zengye.mobilesafe.R;
import com.zengye.mobilesafe.domain.CacheInfo;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class DeleteCacheActivity extends Activity {

	public static final int NO_CACHE = 0;
	public static final int COMPLETED = 1;
	public static final int SCAN_COMPLETE = 2;
	public static final int SCANNING = 3;
	private ListView lvCacheList;
	private TextView tvScanStatus;
	private ProgressBar progressBar;
	private PackageManager pm;
	private List<CacheInfo> cacheInfos;
	private DeleteCacheListAdapter adapter;
	private int currentapiVersion = android.os.Build.VERSION.SDK_INT;

	private CacheHandler handler = new CacheHandler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_delete_cache);
		lvCacheList = (ListView) findViewById(R.id.lv_delete_cache_list);
		tvScanStatus = (TextView) findViewById(R.id.tv_scan_status);
		progressBar = (ProgressBar) findViewById(R.id.progress);
		pm = getPackageManager();
		cacheInfos = new ArrayList<CacheInfo>();
		scanCache();

	}

	class CacheHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub

			switch (msg.what) {
			case SCANNING:
				String appName = (String) msg.obj;
				tvScanStatus.setText("正在扫描：" + appName);
				break;
			case NO_CACHE:
				Toast.makeText(DeleteCacheActivity.this, "所有缓存都已清空",
						Toast.LENGTH_SHORT).show();
				break;
			case COMPLETED:
				if (adapter == null) {
					adapter = new DeleteCacheListAdapter();
					lvCacheList.setAdapter(adapter);
				} else {
					adapter.notifyDataSetChanged();
				}
				break;
			case SCAN_COMPLETE:
				tvScanStatus.setText("扫描完成");
				break;
			}

		}
	}

	private void scanCache() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Method getPackageSizeInfoMethod = null;
				Method[] methods = PackageManager.class.getDeclaredMethods();
				for (Method method : methods) {
					if (method.getName().equals("getPackageSizeInfo")) {
						getPackageSizeInfoMethod = method;
						break;
					}
				}
				List<ApplicationInfo> infos = pm.getInstalledApplications(0);
				progressBar.setMax(infos.size());
				int progress = 0;
				for (final ApplicationInfo info : infos) {
					// String packageName, IPackageStatsObserver observer

					try {
						String appName = (String) info.loadLabel(pm);
						Message msg = Message.obtain();
						msg.obj = appName;
						if (currentapiVersion >= 17) {
							getPackageSizeInfoMethod.invoke(pm,
									info.packageName, 0,
									new DeleteCacheObserver());
						} else {
							getPackageSizeInfoMethod
									.invoke(pm, info.packageName,
											new DeleteCacheObserver());
						}

						progress++;
						progressBar.setProgress(progress);
						Thread.sleep(20);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				handler.sendEmptyMessage(SCAN_COMPLETE);
				if (cacheInfos.size() == 0) {
					handler.sendEmptyMessage(NO_CACHE);
				}
			}
		}).start();
	}

	class DeleteCacheObserver extends IPackageStatsObserver.Stub {

		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			// TODO Auto-generated method stub
			CacheInfo cacheInfo = new CacheInfo();
			long cache = pStats.cacheSize;
			if (cache > 0) {
				cacheInfo.setCache(cache);
				String packageName = pStats.packageName;
				cacheInfo.setPackageName(packageName);
				try {
					Drawable icon = pm.getApplicationIcon(packageName);
					cacheInfo.setIcon(icon);
					ApplicationInfo info = pm
							.getApplicationInfo(packageName, 0);
					String name = (String) info.loadLabel(pm);
					cacheInfo.setName(name);

				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				cacheInfos.add(0,cacheInfo);
				handler.sendEmptyMessage(1);
			}
		}
	}

	class DeleteCacheListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return cacheInfos.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return cacheInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(DeleteCacheActivity.this,
						R.layout.list_delete_cache, null);
				ImageView ivIcon = (ImageView) convertView
						.findViewById(R.id.iv_delete_cache_icon);
				TextView tvName = (TextView) convertView
						.findViewById(R.id.tv_delete_cache_name);
				TextView tvCache = (TextView) convertView
						.findViewById(R.id.tv_delete_cache);
				holder = new ViewHolder();
				holder.ivIcon = ivIcon;
				holder.tvCache = tvCache;
				holder.tvName = tvName;
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			CacheInfo info = cacheInfos.get(position);
			holder.ivIcon.setImageDrawable(info.getIcon());
			holder.tvCache.setText(Formatter.formatFileSize(
					DeleteCacheActivity.this, info.getCache()));
			holder.tvName.setText(info.getName());
			return convertView;
		}

	}

	class ViewHolder {
		private ImageView ivIcon;
		private TextView tvName;
		private TextView tvCache;
	}

	public void onkeyClean(View view) {
		Method freeStorageAndNotifyMethod = null;
		Method[] methods = PackageManager.class.getDeclaredMethods();
		for (Method method : methods) {
			if (method.getName().equals("freeStorageAndNotify")) {
				freeStorageAndNotifyMethod = method;
				break;
			}
		}
		// long freeStorageSize, IPackageDataObserver observer)
		try {
			freeStorageAndNotifyMethod.invoke(pm, Integer.MAX_VALUE,
					new CleanObserver());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cacheInfos.clear();
		adapter.notifyDataSetChanged();
	}

	class CleanObserver extends IPackageDataObserver.Stub {

		@Override
		public void onRemoveCompleted(String packageName, boolean succeeded)
				throws RemoteException {
			// TODO Auto-generated method stub
		}

	}
}
