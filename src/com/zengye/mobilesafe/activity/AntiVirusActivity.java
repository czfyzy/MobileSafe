package com.zengye.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zengye.mobilesafe.R;
import com.zengye.mobilesafe.db.dao.AntiVirusDao;
import com.zengye.mobilesafe.ui.RoundProgressBar;
import com.zengye.mobilesafe.utils.MD5Utils;

public class AntiVirusActivity extends Activity {

	protected static final int SCANNING = 0;
	protected static final int SCAN_COMPLETE = 1;
	private ImageView ivActScanning;
	private TextView tvScanningAppName;
	private ListView lvAntiVirusList;
	private PackageManager pm;
	private RoundProgressBar progressBar;
	private List<ScanInfo> scanInfos;
	private AntiVirusListAdapter adapter;
	private Button cancel;
	private Thread scanThread;
	private boolean isStop = false;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SCANNING:
				ScanInfo info = (ScanInfo) msg.obj;
				scanInfos.add(0, info);
				tvScanningAppName.setText("正在扫描：" + info.name);
				if (adapter == null) {
					adapter = new AntiVirusListAdapter();
					lvAntiVirusList.setAdapter(adapter);
				} else {
					adapter.notifyDataSetChanged();
				}
				break;
			case SCAN_COMPLETE:
				if(msg.arg1 == 0) {
					tvScanningAppName.setText("扫描完毕");
				} else if(msg.arg1 == 1) {
					tvScanningAppName.setText("扫描任务已取消");
				}
				ivActScanning.clearAnimation();
				cancel.setText("重新开始");
				break;
			}
		}
	};
	private RotateAnimation ra;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anti_virus);
		pm = getPackageManager();
		ivActScanning = (ImageView) findViewById(R.id.iv_act_scanning);
		tvScanningAppName = (TextView) findViewById(R.id.tv_scanning_app);
		lvAntiVirusList = (ListView) findViewById(R.id.lv_anti_virus_list);
		progressBar = (RoundProgressBar) findViewById(R.id.roundProgressBar);
		ra = (RotateAnimation) AnimationUtils.loadAnimation(
				this, R.anim.radar_scan);
		LinearInterpolator interpolator = new LinearInterpolator();
		ra.setInterpolator(interpolator);
		ivActScanning.setAnimation(ra);
		scanInfos = new ArrayList<AntiVirusActivity.ScanInfo>();
		cancel = (Button) findViewById(R.id.bt_cancel);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (scanThread != null && scanThread.isAlive()) {
					isStop = true;
					progressBar.setProgress(0);
					cancel.setText("重新开始");
					scanThread = null;
				} else {
					isStop = false;
					ivActScanning.startAnimation(ra);
					scanVirus();
					cancel.setText("取消");
				}
			}
		});

		

		scanVirus();
	}


	private void scanVirus() {
		if(scanInfos != null && scanInfos.size() != 0) {
			scanInfos.clear();
		}
		scanThread = new Thread(new Runnable() {
			public void run() {
				List<ApplicationInfo> infos = pm.getInstalledApplications(0);
				progressBar.setMax(infos.size());
				int progress = 0;
				for (ApplicationInfo info : infos) {
					if(isStop) {
						break;
					}
					String sourceDir = info.sourceDir;
					ScanInfo scanInfo = new ScanInfo();
					scanInfo.name = (String) info.loadLabel(pm);
					scanInfo.packageName = info.packageName;
					scanInfo.icon = info.loadIcon(pm);
					String md5 = MD5Utils.getFileMD5(sourceDir);
					boolean isVirus = AntiVirusDao.find(md5);
					if (isVirus) {
						scanInfo.isVirus = true;
					} else {
						scanInfo.isVirus = false;
					}
					Message message = Message.obtain();
					message.what = SCANNING;
					message.obj = scanInfo;
					handler.sendMessage(message);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					progress++;
					progressBar.setProgress(progress);
				}
				Message message = Message.obtain();
				if(progress != infos.size()) {
					message.arg1 = 1;
				} else {
					message.arg1 = 0;
				}
				message.what = SCAN_COMPLETE;
				handler.sendMessage(message);
			}
		});
		scanThread.start();
	}

	class ScanInfo {
		String packageName;
		String name;
		boolean isVirus;
		Drawable icon;

	}

	class AntiVirusListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return scanInfos.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return scanInfos.get(position);
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
				convertView = View.inflate(AntiVirusActivity.this,
						R.layout.list_anti_virus_item, null);
				holder = new ViewHolder();
				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.iv_app_icon);
				holder.tvIsVirus = (TextView) convertView
						.findViewById(R.id.tv_is_virus);
				holder.tvName = (TextView) convertView
						.findViewById(R.id.tv_app_name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			ScanInfo scanInfo = scanInfos.get(position);
			holder.ivIcon.setImageDrawable(scanInfo.icon);
			holder.tvName.setText(scanInfo.name);
			if (scanInfo.isVirus) {

				holder.tvIsVirus.setText("不安全");
				holder.tvIsVirus.setTextColor(Color.RED);
			} else {
				holder.tvIsVirus.setText("安全");
				holder.tvIsVirus.setTextColor(Color.BLACK);
			}

			return convertView;
		}

	}

	class ViewHolder {
		ImageView ivIcon;
		TextView tvName;
		TextView tvIsVirus;
	}
}
