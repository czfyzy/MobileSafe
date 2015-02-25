package com.zengye.mobilesafe.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zengye.mobilesafe.R;
import com.zengye.mobilesafe.domain.TaskInfo;
import com.zengye.mobilesafe.engine.TaskInfoProvider;
import com.zengye.mobilesafe.utils.SystemInfoUtils;

public class TaskManagerActivity extends Activity {

	private TextView tvMemInfo;
	private TextView tvRunningProcess;
	private LinearLayout llLoading;
	private ListView lvTaskList;
	private List<TaskInfo> taskInfos;
	private List<TaskInfo> systemTaskInfos;
	private List<TaskInfo> userTaskInfos;
	private TaskListAdapter adapter;
	private TextView tvTaskFlag;
	private TaskInfo onClickTaskInfo;
	private int processCount;
	private long availMem;
	private long totalMem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_manager);
		tvRunningProcess = (TextView) findViewById(R.id.tv_running_process);
		tvMemInfo = (TextView) findViewById(R.id.tv_memory_info);

		llLoading = (LinearLayout) findViewById(R.id.ll_loading);
		tvTaskFlag = (TextView) findViewById(R.id.tv_task_flag);
		lvTaskList = (ListView) findViewById(R.id.lv_task_list);
		lvTaskList.setOnScrollListener(new TaskListScrollListener());
		lvTaskList.setOnItemClickListener(new TaskListItemOnclickListener());
	}

	private void setHead() {
		processCount = SystemInfoUtils.getRunningProcessCount(this);

		availMem = SystemInfoUtils.getAvailableMemory(this);
		totalMem = SystemInfoUtils.getTotalMemory(this);

		tvRunningProcess.setText(processCount + "个");
		tvMemInfo.setText(Formatter.formatFileSize(this, availMem) + "/"
				+ Formatter.formatFileSize(this, totalMem));
	}

	private void fillData() {
		llLoading.setVisibility(View.VISIBLE);
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (taskInfos != null) {
					taskInfos.clear();
				}

				taskInfos = TaskInfoProvider
						.getTaskInfos(TaskManagerActivity.this);
				systemTaskInfos = new ArrayList<TaskInfo>();
				userTaskInfos = new ArrayList<TaskInfo>();
				for (TaskInfo taskInfo : taskInfos) {
					if (taskInfo.isUserTask()) {
						userTaskInfos.add(taskInfo);
					} else {
						systemTaskInfos.add(taskInfo);
					}
				}
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						llLoading.setVisibility(View.INVISIBLE);
						// TODO Auto-generated method stub
						if (adapter == null) {
							adapter = new TaskListAdapter();
							lvTaskList.setAdapter(adapter);
						} else {
							adapter.notifyDataSetChanged();
						}
						setHead();
					}
				});

			}
		}).start();
	}

	class TaskListAdapter extends BaseAdapter {
		private static final String TAG = "TaskListAdapter";
		TextView tvUserLab;
		TextView tvSystemLab;

		public TaskListAdapter() {
			// TODO Auto-generated constructor stub
			super();
			tvUserLab = new TextView(TaskManagerActivity.this);
			tvUserLab.setBackgroundColor(Color.GRAY);
			tvUserLab.setTextColor(Color.WHITE);
			tvSystemLab = new TextView(TaskManagerActivity.this);
			tvSystemLab.setBackgroundColor(Color.GRAY);
			tvSystemLab.setTextColor(Color.WHITE);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			SharedPreferences sp = getSharedPreferences("config", Context.MODE_PRIVATE);
			boolean systemTask = sp.getBoolean("systemTaskVisible",false);
			int count;
			if(systemTask) {
				count =	taskInfos.size() + 2;
			} else {
				count =userTaskInfos.size() + 1;
			}
			return count;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			TaskInfo taskInfo;
			if (position == 0 || position == userTaskInfos.size() + 1) {
				return null;
			} else if (position < userTaskInfos.size() + 1) {

				taskInfo = onClickTaskInfo = userTaskInfos.get(position - 1);

			} else {
				taskInfo = systemTaskInfos.get(position - userTaskInfos.size()
						- 2);
			}
			return taskInfo;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub\
			
			TaskInfo taskInfo;
			if (position == 0) {
				// 用户进程标签
				tvUserLab.setText("用户进程：" + userTaskInfos.size() + "个");
				return tvUserLab;
			} else if (position == userTaskInfos.size() + 1) {
				tvSystemLab.setText("系统进程：" + systemTaskInfos.size() + "个");
				return tvSystemLab;
			} else if (position < userTaskInfos.size() + 1) {
				taskInfo = userTaskInfos.get(position - 1);

			} else {
				taskInfo = systemTaskInfos.get(position - userTaskInfos.size()
						- 2);
			}

			View view;
			ViewHolder holder;
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
				Log.i(TAG, "复用");
			} else {
				Log.i(TAG, "创建");
				view = View.inflate(TaskManagerActivity.this,
						R.layout.list_task_info_item, null);
				holder = new ViewHolder();
				holder.ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
				holder.tvMemSize = (TextView) view
						.findViewById(R.id.tv_mem_size);
				holder.tvName = (TextView) view.findViewById(R.id.tv_name);
				holder.cbStatus = (CheckBox) view.findViewById(R.id.cb_status);
				view.setTag(holder);
			}
			holder.ivIcon.setImageDrawable(taskInfo.getIcon());
			holder.tvMemSize.setText("内存占用："
					+ Formatter.formatFileSize(TaskManagerActivity.this,
							taskInfo.getMemSize()));
			holder.tvName.setText(taskInfo.getName());
			holder.cbStatus.setChecked(taskInfo.isChecked());
			if(getPackageName().equals(taskInfo.getPackageName())) {
				holder.cbStatus.setVisibility(View.INVISIBLE);
			} else {
				holder.cbStatus.setVisibility(View.VISIBLE);
			}
			return view;
		}

	}

	class ViewHolder {
		private ImageView ivIcon;
		private TextView tvName;
		private TextView tvMemSize;
		private CheckBox cbStatus;
	}

	class TaskListScrollListener implements
			android.widget.AbsListView.OnScrollListener {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// TODO Auto-generated method stub
			if (firstVisibleItem == 0
					|| firstVisibleItem == userTaskInfos.size() + 1) {
				if (tvTaskFlag.getVisibility() == View.VISIBLE) {
					tvTaskFlag.setVisibility(View.INVISIBLE);
				}
			} else {
				if (tvTaskFlag.getVisibility() == View.INVISIBLE) {
					if (userTaskInfos != null && systemTaskInfos != null) {
						if (firstVisibleItem < userTaskInfos.size() + 1) {
							tvTaskFlag.setText("用户进程：" + userTaskInfos.size()
									+ "个");
						} else {
							tvTaskFlag.setText("系统进程：" + systemTaskInfos.size()
									+ "个");
						}
					}
					tvTaskFlag.setVisibility(View.VISIBLE);
				}
			}
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		fillData();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (userTaskInfos != null) {
			userTaskInfos.clear();
			userTaskInfos = null;
		}
		if (systemTaskInfos != null) {
			systemTaskInfos.clear();
			systemTaskInfos = null;
		}
	}

	class TaskListItemOnclickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			onClickTaskInfo = (TaskInfo) parent.getItemAtPosition(position);
			if(getPackageName().equals(onClickTaskInfo.getPackageName())) {
				return;
			}
			CheckBox cbStatus = ((ViewHolder) view.getTag()).cbStatus;
			if (onClickTaskInfo != null) {
				if (onClickTaskInfo.isChecked()) {
					onClickTaskInfo.setChecked(false);
					cbStatus.setChecked(false);
				} else {
					onClickTaskInfo.setChecked(true);
					cbStatus.setChecked(true);
				}

			}
		}

	}

	public void selectAll(View view) {
		for (TaskInfo info : taskInfos) {
			if(getPackageName().equals(info.getPackageName())) {
				continue;
			}
			info.setChecked(true);
		}
		adapter.notifyDataSetChanged();
	}

	public void selectOppo(View view) {
		for (TaskInfo info : taskInfos) {
			if(getPackageName().equals(info.getPackageName())) {
				continue;
			}
			info.setChecked(!info.isChecked());
		}
		adapter.notifyDataSetChanged();
	}

	public void clear(View view) {
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		int count = 0;
		long killMemSize = 0l;
		Iterator<TaskInfo> iterator = taskInfos.iterator();
		while (iterator.hasNext()) {
			TaskInfo info = iterator.next();
			if (info.isChecked()) {
				am.killBackgroundProcesses(info.getPackageName());
				if (info.isUserTask()) {
					userTaskInfos.remove(info);
				} else {
					systemTaskInfos.remove(info);
				}
				killMemSize += info.getMemSize();
				count++;
				iterator.remove();
			}
		}
		adapter.notifyDataSetChanged();
		processCount -= count;
		availMem += killMemSize;
		tvRunningProcess.setText(processCount + "个");
		tvMemInfo.setText(Formatter.formatFileSize(this, availMem) + "/"
				+ Formatter.formatFileSize(this, totalMem));
		Toast.makeText(
				this,
				"结束了" + count + "个进程，释放了"
						+ Formatter.formatFileSize(this, killMemSize) + "内存",
				Toast.LENGTH_SHORT).show();
		
	}

	public void setting(View view) {
		Intent intent = new Intent(this, TaskSettingActivity.class);
		startActivity(intent);
	}
}
