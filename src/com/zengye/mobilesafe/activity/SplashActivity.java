package com.zengye.mobilesafe.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockActivity;
import com.zengye.mobilesafe.R;
import com.zengye.mobilesafe.utils.StreamTools;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

public class SplashActivity extends SherlockActivity {
	private static final String TAG = "SplashActivity";
	private static final int ENTER_HOME = 0;
	private static final int SHOW_UPDATE_DIALOG = 1;
	private static final int URL_ERROR = 2;
	private static final int NETWORK_ERROR = 3;
	private static final int JSON_ERROR = 4;

//	private TextView versionTV;
	private TextView updateTV;
	private String description;
	private String apkurl;
	private String version;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
//		versionTV = (TextView) findViewById(R.id.splash_version_tv);
//		versionTV.setText("版本:" + getVersionName());
		getSupportActionBar().hide();
		updateTV = (TextView) findViewById(R.id.splash_update_tv);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		cpoyDb("address.db");
		cpoyDb("antivirus.db");
		
		boolean autoUpdate = sp.getBoolean("autoUpdate", false);
		if (autoUpdate) {
			checkUpdate();
		} else {
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					enterHome();
				}
			}, 2000);
		}

		AlphaAnimation aa = new AlphaAnimation(0.2f, 1f);
		aa.setDuration(500);
		findViewById(R.id.ms_splash).startAnimation(aa);
		
		if(!isAddShortCut()) {
			addShortCut();
			Toast.makeText(this, "已创建桌面图标", Toast.LENGTH_SHORT).show();
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case ENTER_HOME:// 进入主页面
				enterHome();
				break;
			case SHOW_UPDATE_DIALOG: // 显示升级的对话框
				Log.i(TAG, "SHOW_UPDATE_DIALOG");
				showUpdateDialog();
				break;
			case URL_ERROR:// url错误
				enterHome();
				Toast.makeText(SplashActivity.this, "url错误", Toast.LENGTH_SHORT)
						.show();
				break;
			case NETWORK_ERROR:// 网络异常
				enterHome();
				Toast.makeText(SplashActivity.this, "网络异常", Toast.LENGTH_SHORT)
						.show();
				break;
			case JSON_ERROR:// json解析错误
				enterHome();
				Toast.makeText(SplashActivity.this, "json解析错误",
						Toast.LENGTH_SHORT).show();
				break;
			}

		}
	};

	private void cpoyDb(String name) {
		try {
			File file = new File(getFilesDir(), name);
			if (file.exists() && file.length() > 0) {

			} else {
				InputStream is = getAssets().open(name);
				byte[] data = new byte[1024];
				int len = 0;
				OutputStream os = new FileOutputStream(file);

				while ((len = is.read(data)) != -1) {
					os.write(data, 0, len);
				}
				is.close();
				os.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void enterHome() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();
	};

	private void showUpdateDialog() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("升级提示");
		builder.setMessage(description);
		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				enterHome();
			}
		});
		builder.setPositiveButton("立刻升级", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					FinalHttp finalHttp = new FinalHttp();
					String fileName = Environment.getExternalStorageDirectory()
							.getAbsolutePath() + "/zyMobileSafe/download";
					File file = new File(fileName);
					if (!file.exists()) {
						file.mkdirs();
					}
					updateTV.setVisibility(View.VISIBLE);
					finalHttp.download(apkurl, file.getAbsolutePath()
							+ "/mobilesafe" + version + ".apk",
							new AjaxCallBack<File>() {

								@Override
								public void onFailure(Throwable t, int errorNo,
										String strMsg) {
									// TODO Auto-generated method stu
									t.printStackTrace();
									Toast.makeText(SplashActivity.this, "下载失败",
											Toast.LENGTH_SHORT);

									super.onFailure(t, errorNo, strMsg);
								}

								@Override
								public void onSuccess(File t) {
									// TODO Auto-generated method stub
									super.onSuccess(t);
									installAPK(t);
								}

								private void installAPK(File t) {
									// TODO Auto-generated method stub
									Intent intent = new Intent();
									intent.setAction("android.intent.action.VIEW");
									intent.addCategory("android.intent.category.DEFAULT");
									intent.setDataAndType(Uri.fromFile(t),
											"application/vnd.android.package-archive");
									startActivity(intent);
								}

								@Override
								public void onLoading(long count, long current) {
									// TODO Auto-generated method stub
									int progress = (int) (current * 100 / count);
									updateTV.setText("下载进度：" + progress + "%");
									super.onLoading(count, current);
								}
							});
				} else {
					Toast.makeText(SplashActivity.this, "没有安装sd卡，请安装再试",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		builder.setNegativeButton("下次再说", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				enterHome();
			}
		});
		builder.show();
	}

	private void checkUpdate() {
		// TODO Auto-generated method stub
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = Message.obtain();
				long startTime = System.currentTimeMillis();
				try {
					URL url = new URL(getString(R.string.updateInfoUrl));
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setReadTimeout(4000);
					conn.setRequestMethod("GET");
					int code = conn.getResponseCode();
					if (code == 200) {
						InputStream is = conn.getInputStream();
						String result = StreamTools.readFromStream(is);
						Log.i(TAG, result);
						JSONObject jsonObject = new JSONObject(result);
						version = jsonObject.getString("version");
						description = jsonObject.getString("description");
						apkurl = jsonObject.getString("apkurl");
						if (getVersionName().equals(version)) {
							msg.what = ENTER_HOME;
						} else {
							msg.what = SHOW_UPDATE_DIALOG;
						}
					}

				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					Log.i(TAG, "MalformedURLException");
					msg.what = URL_ERROR;
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.i(TAG, "IOException");
					msg.what = NETWORK_ERROR;
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					msg.what = JSON_ERROR;
					e.printStackTrace();
				} finally {
					long endTime = System.currentTimeMillis();
					long d = startTime - endTime;
					if (d < 2000) {
						try {
							Thread.sleep(2000 - d);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					handler.sendMessage(msg);
				}
			}
		}.start();
	}

	private String getVersionName() {
		PackageManager pm = getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}

	public boolean isAddShortCut() {

//		boolean isInstallShortcut = false;
//		final ContentResolver cr = this.getContentResolver();
//
//		int versionLevel = android.os.Build.VERSION.SDK_INT;
//		String AUTHORITY = "com.android.launcher2.settings";
//
//		// 2.2以上的系统的文件文件名字是不一样的
//		if (versionLevel >= 8) {
//			AUTHORITY = "com.android.launcher2.settings";
//		} else {
//			AUTHORITY = "com.android.launcher.settings";
//		}
//
//		final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
//				+ "/favorites?notify=true");
//		Cursor c = cr.query(CONTENT_URI,
//				new String[] { "title", "iconResource" }, "title=?",
//				new String[] { getString(R.string.app_name) }, null);
//
//		if (c != null && c.getCount() > 0) {
//			isInstallShortcut = true;
//		}
//		return isInstallShortcut;
		return sp.getBoolean("addShortCut", false);
	}

	public void addShortCut() {

		Intent shortcut = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		// 设置属性
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				getResources().getString(R.string.app_name));
		//设置快捷方式图标
		Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon);

		// 是否允许重复创建
		shortcut.putExtra("duplicate", false);

		// 点击快捷方式的操作
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setClass(this, SplashActivity.class);

		// 设置启动程序
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

		// 广播通知桌面去创建
		this.sendBroadcast(shortcut);
		
		Editor editor = sp.edit();
		editor.putBoolean("addShortCut", true);
		editor.commit();
	}

}
