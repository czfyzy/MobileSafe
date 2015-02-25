package com.zengye.mobilesafe.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.zengye.mobilesafe.R;
import com.zengye.mobilesafe.ui.MyRingWave;
import com.zengye.mobilesafe.utils.MD5Utils;

public class HomeActivity extends SherlockActivity {

	private GridView listHome;
	private MyAdapter adapter;
	private static String[] names = { "手机防盗", "通讯卫士", "软件管理", "进程管理", "流量统计",
			"缓存清理", "高级工具", "自启管理" };
	private static int[] ids = { R.drawable.icon_disturb_block, R.drawable.icon_communication_tools,
			R.drawable.icon_softmgr, R.drawable.icon_process, R.drawable.icon_traffic,
			R.drawable.icon_cache_delete, R.drawable.icon_tool, R.drawable.icon_auto_start };

	private SharedPreferences sp;

	private MyRingWave ringWave;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		sp = getSharedPreferences("config", MODE_PRIVATE);
		setContentView(R.layout.activity_home);

		ringWave = (MyRingWave) findViewById(R.id.wave);

		listHome = (GridView) findViewById(R.id.list_home);
		adapter = new MyAdapter();
		listHome.setAdapter(adapter);
		listHome.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = null;
				switch (position) {
				case 0:
					showLostFindDialog();
					break;

				case 1:
					intent = new Intent(HomeActivity.this,
							BlackNumberActivity.class);
					break;

				case 2:
					intent = new Intent(HomeActivity.this,
							AppManagerActivity.class);
					break;

				case 3:
					intent = new Intent(HomeActivity.this,
							TaskManagerActivity.class);
					break;

				case 4:
					intent = new Intent(HomeActivity.this,
							TrafficActivity.class);
					break;

				case 5:
					intent = new Intent(HomeActivity.this,
							DeleteCacheActivity.class);

					break;

				case 6:
					intent = new Intent(HomeActivity.this, AtoolsAvtivity.class);
					break;

				case 7:
					intent = new Intent(HomeActivity.this,
							AutoStartManagerACitvity.class);
					break;

				}
				if (intent != null) {
					startActivity(intent);
				}
			}
		});
	}

	public class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return names.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = View.inflate(HomeActivity.this,
					R.layout.list_home_item, null);
			ImageView itemIV = (ImageView) view
					.findViewById(R.id.list_home_item_iv);
			TextView itemTV = (TextView) view
					.findViewById(R.id.list_home_item_tv);
			itemTV.setText(names[position]);
			itemIV.setImageResource(ids[position]);
			return view;
		}

	}

	private void showLostFindDialog() {
		if (isSetupPwd()) {
			showEnterDialog();
		} else {
			showSetupPwd();
		}
	}

	private EditText pwdET;
	private EditText confirmET;
	private Button confirmBT;
	private Button cancelBT;
	private AlertDialog dialog;

	/**
	 * 设置密码对话框
	 */
	private void showSetupPwd() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new Builder(this);

		View view = View.inflate(this, R.layout.dialog_setup_pwd, null);

		pwdET = (EditText) view.findViewById(R.id.et_set_pwd);
		confirmET = (EditText) view.findViewById(R.id.et_set_pwd_confirm);
		confirmBT = (Button) view.findViewById(R.id.confirm);
		cancelBT = (Button) view.findViewById(R.id.cancel);

		dialog = builder.create();
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();

		cancelBT.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		confirmBT.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String password = pwdET.getText().toString().trim();
				String pwdConfirm = confirmET.getText().toString().trim();
				if (TextUtils.isEmpty(password)
						|| TextUtils.isEmpty(pwdConfirm)) {
					Toast.makeText(HomeActivity.this, "密码不能为空",
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (!password.equals(pwdConfirm)) {
					Toast.makeText(HomeActivity.this, "两次输入密码不一致",
							Toast.LENGTH_SHORT).show();
					return;
				}
				Editor editor = sp.edit();
				editor.putString("password", MD5Utils.encrypt(password));
				editor.commit();
				dialog.dismiss();
				Intent intent = new Intent(HomeActivity.this,
						LostFindActivity.class);
				startActivity(intent);
			}
		});

	}

	/**
	 * 输入密码对话框
	 */
	private void showEnterDialog() {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new Builder(this);

		View view = View.inflate(this, R.layout.dialog_enter_pwd, null);

		pwdET = (EditText) view.findViewById(R.id.et_set_pwd);
		confirmBT = (Button) view.findViewById(R.id.confirm);
		cancelBT = (Button) view.findViewById(R.id.cancel);

		dialog = builder.create();
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();

		cancelBT.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		confirmBT.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String password = pwdET.getText().toString().trim();
				if (TextUtils.isEmpty(password)) {
					Toast.makeText(HomeActivity.this, "密码不能为空",
							Toast.LENGTH_SHORT).show();
					return;
				}

				String savePwd = sp.getString("password", "");
				if (savePwd.equals(MD5Utils.encrypt(password))) {
					dialog.dismiss();
					Intent intent = new Intent(HomeActivity.this,
							LostFindActivity.class);
					startActivity(intent);
				} else {
					pwdET.setText("");
					Toast.makeText(HomeActivity.this, "密码错误", Toast.LENGTH_LONG)
							.show();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.menu_setting:
			Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
		return true;
	}

	private boolean isSetupPwd() {
		String password = sp.getString("password", null);
		return !TextUtils.isEmpty(password);
	}

	public void quickScan(View view) {
		Intent intent = new Intent(this, AntiVirusActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		ringWave.ringRun();
	}

	@Override
	protected void onPause() {
		super.onPause();
		ringWave.cancel();
	}
}
