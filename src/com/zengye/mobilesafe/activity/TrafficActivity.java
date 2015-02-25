package com.zengye.mobilesafe.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.zengye.mobilesafe.R;
import com.zengye.mobilesafe.db.TrafficDBHelper;
import com.zengye.mobilesafe.db.dao.TrafficDBDao;
import com.zengye.mobilesafe.domain.AllTraffic;
import com.zengye.mobilesafe.domain.AppTraffic;
import com.zengye.mobilesafe.domain.TrafficInfo;
import com.zengye.mobilesafe.service.TrafficService;
import com.zengye.mobilesafe.utils.ServiceUtils;

public class TrafficActivity extends SherlockActivity {

	protected static final int SURPLUS_COMPLETE = 0;
	private ListView lvTrafficList;
	private PackageManager pm;
	private List<TrafficInfo> trafficInfos;
	private LinearLayout llLoading;
	private TrafficAdapter adapter;
	private TrafficDBDao dao;
	private Map<Integer, AppTraffic> allMap;

	// 所有流量
	private TextView trafficAll;
	// 标题
	private TextView trafficTitle;
	// 单位
	private TextView trafficUnit;
	// 剩余流量
	private TextView trafficSurplus;

	private DecimalFormat fnum = new DecimalFormat("##0.00");

	private SharedPreferences sp;

	private long mTraffic;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SURPLUS_COMPLETE:
				List list = (List) msg.obj;
				float f = (Float) list.get(0);
				if (mTraffic != 0) {
					trafficTitle.setText("剩余流量");
					trafficAll.setText("共"+ (mTraffic /1024/1024)+"M");
				} else {
					trafficTitle.setText("已使用流量");
					trafficAll.setText("共？M");
				}
				String format = fnum.format(f);
				String unit = (String) list.get(1);
				trafficSurplus.setText(format);
				trafficUnit.setText(unit);

				break;

			}

		};
	};

	// private AlertDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_traffic);

		trafficAll = (TextView) findViewById(R.id.tv_traffic_all);
		trafficTitle = (TextView) findViewById(R.id.tv_traffic_title);
		trafficUnit = (TextView) findViewById(R.id.tv_traffic_unit);
		trafficSurplus = (TextView) findViewById(R.id.tv_traffic_surplus);

		pm = getPackageManager();
		lvTrafficList = (ListView) findViewById(R.id.lv_traffic_list);
		llLoading = (LinearLayout) findViewById(R.id.ll_loading);
		trafficInfos = new ArrayList<TrafficInfo>();
		dao = new TrafficDBDao(this);

		sp = getSharedPreferences("config", MODE_PRIVATE);

		mTraffic = sp.getLong("my_traffic", 0);
		fillData();
	}

	private void fillData() {
		llLoading.setVisibility(View.VISIBLE);
		allMap = dao.findAllMap();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				List<ApplicationInfo> infos = pm.getInstalledApplications(0);
				if (trafficInfos != null) {
					trafficInfos.clear();
				}
				for (ApplicationInfo applicationInfo : infos) {
					String packageName = applicationInfo.packageName;
					String name = (String) applicationInfo.loadLabel(pm);
					Drawable icon = applicationInfo.loadIcon(pm);
					int uid = applicationInfo.uid;

					AppTraffic appTraffic = allMap.get(uid);

					TrafficInfo info = new TrafficInfo();
					info.setIcon(icon);
					info.setName(name);
					info.setPackageName(packageName);
					if (appTraffic != null) {
						info.setRx(appTraffic.rx);
						info.setTx(appTraffic.tx);
						trafficInfos.add(info);
					}

					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				runOnUiThread(new Runnable() {
					public void run() {
						if (adapter == null) {
							adapter = new TrafficAdapter();
							lvTrafficList.setAdapter(adapter);
						} else {
							adapter.notifyDataSetChanged();
						}
						llLoading.setVisibility(View.INVISIBLE);
					}
				});

			}
		}).start();

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				AllTraffic allTraffic = dao
						.findAllTr(TrafficDBHelper.TYPE_MOBILE);
				long used = allTraffic.tx + allTraffic.rx;
				float f = 0;
				if (mTraffic != 0) {
					used = mTraffic - used;
				}
				String unit;
				if (used > 1024 * 1024 * 1024) {
					f = used / 1024f / 1024f / 1024f;
					unit = "G";
				} else if (used > 1024 * 1024) {
					f = used / 1024f / 1024f;
					unit = "M";
				} else {
					f = used / 1024f;
					unit = "K";
				}
				List list = new ArrayList();
				list.add(f);
				list.add(unit);
				Message msg = Message.obtain();
				msg.obj = list;
				msg.what = SURPLUS_COMPLETE;
				handler.sendMessage(msg);
			}
		}).start();
	}

	class TrafficAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return trafficInfos.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return trafficInfos.get(position);
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
				convertView = View.inflate(TrafficActivity.this,
						R.layout.list_traffic_info, null);
				holder = new ViewHolder();
				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.iv_traffic_info_icon);
				holder.tvName = (TextView) convertView
						.findViewById(R.id.tv_traffic_info_name);
				holder.tvRx = (TextView) convertView
						.findViewById(R.id.tv_traffic_info_rx);
				holder.tvTx = (TextView) convertView
						.findViewById(R.id.tv_traffic_info_tx);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			TrafficInfo info = trafficInfos.get(position);
			holder.ivIcon.setImageDrawable(info.getIcon());
			holder.tvName.setText(info.getName());
			holder.tvRx.setText(Formatter.formatFileSize(TrafficActivity.this,
					info.getRx()));
			holder.tvTx.setText(Formatter.formatFileSize(TrafficActivity.this,
					info.getTx()));
			return convertView;
		}

	}

	class ViewHolder {
		private ImageView ivIcon;
		private TextView tvName;
		private TextView tvRx;
		private TextView tvTx;
	}

	@Override
	protected void onResume() {
		super.onResume();
		boolean isRunning = ServiceUtils.isServiceRunning(this,
				"com.zengye.mobilesafe.service.TrafficService");
		if (!isRunning) {
			AlertDialog.Builder builder = new Builder(this);
			builder.setMessage("流量监控服务未开启，是否开启？").setTitle("提示")
					.setPositiveButton("确定", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							if (which == DialogInterface.BUTTON_POSITIVE) {
								Intent intent = new Intent(
										TrafficActivity.this,
										TrafficService.class);
								startService(intent);
							}
						}
					}).setNegativeButton("取消", null).show();
		}
	}
	
	 @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.traffic, menu);
		return true;
	}
	 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.menu_setting:
			final Dialog  dialog= new Dialog(this,R.style.dialog);
			dialog.show();
			dialog.getWindow().setContentView(R.layout.dialog_set_traffic);
			 
			Button confirm = (Button) dialog.getWindow().findViewById(R.id.btn_setting_confirm);
			Button cancel = (Button) dialog.getWindow().findViewById(R.id.btn_setting_cancel);
			
			final EditText editText =  (EditText) dialog.getWindow().findViewById(R.id.et_setting_traffic);
			confirm.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String traffic = editText.getText().toString();
					if(TextUtils.isEmpty(traffic)) {
						Toast.makeText(TrafficActivity.this, "请输入套餐流量", Toast.LENGTH_LONG);
						return;
					}
					int temp  = Integer.parseInt(traffic);
					if(temp > 0) {
						Editor edit = sp.edit();
						long result = temp * 1024 *1024;
						edit.putLong("my_traffic", result);
						edit.commit();
						dialog.dismiss();
					} else {
						Toast.makeText(TrafficActivity.this, "请输入正确套餐流量", Toast.LENGTH_LONG);
						return;
					}
				}
			});
			
			cancel.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
			
			break;
		}
		return true;
	}
}
