package com.zengye.mobilesafe.activity;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zengye.mobilesafe.R;
import com.zengye.mobilesafe.db.dao.BlackNumberDBDao;
import com.zengye.mobilesafe.domain.BlackNumberInfo;

public class BlackNumberActivity extends Activity {

	private ListView lvBlackNumber;
	private BlackNumberAdapter mAdapter;
	private List<BlackNumberInfo> infos;
	private BlackNumberDBDao dao;
	private View dialogView;
	private AlertDialog dialog;
	private CheckBox messageBlock;
	private CheckBox callBlock;
	private EditText phoneNo;
	private Button cancel;
	private Button confirm;
	private int max = 20;
	private int offset = 0;

	private LinearLayout llLoading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_black_number);
		dao = new BlackNumberDBDao(this);
		llLoading = (LinearLayout) findViewById(R.id.loading);

		llLoading.setVisibility(View.VISIBLE);
		lvBlackNumber = (ListView) findViewById(R.id.lv_black_number);

		fillData();
		
		lvBlackNumber.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:
					int lastPosition = lvBlackNumber.getLastVisiblePosition();
					if(lastPosition == infos.size() - 1) {
						offset += max;
						fillData();
					}
					break;

				default:
					break;
				}
			}
			//正在滚动
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}

	private void fillData() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(infos == null) {
					
					infos = dao.findPart(offset, max);
				} else {
					infos.addAll(dao.findPart(offset, max));
				}

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						llLoading.setVisibility(View.INVISIBLE);
						if(mAdapter == null) {
							mAdapter = new BlackNumberAdapter();
							lvBlackNumber.setAdapter(mAdapter);
						} else {
							mAdapter.notifyDataSetChanged();
						}
						
					}
				});
			}
		}).start();
	}

	class BlackNumberAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return infos.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return infos.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			TextView number;
			TextView mode;
			ImageView delete;
			ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(BlackNumberActivity.this,
						R.layout.black_number_list_item, null);
				number = (TextView) convertView
						.findViewById(R.id.tv_black_number);
				mode = (TextView) convertView
						.findViewById(R.id.tv_black_number_mode);
				delete = (ImageView) convertView
						.findViewById(R.id.iv_black_number_delete);
				delete.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						AlertDialog.Builder builder = new AlertDialog.Builder(
								BlackNumberActivity.this);
						builder.setTitle("提示");
						builder.setMessage("是否确认删除");
						builder.setPositiveButton("确认",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										dao.delete(infos.get(position)
												.getNubmer());
										infos.remove(position);
										mAdapter.notifyDataSetChanged();
									}
								});
						builder.setNegativeButton("取消", null);
						builder.show();
					}
				});

				holder = new ViewHolder(number, mode, delete);
				convertView.setTag(holder);

			} else {
				ViewHolder handler = (ViewHolder) convertView.getTag();
				mode = handler.mode;
				number = handler.number;
				delete = handler.delete;
			}
			BlackNumberInfo info = infos.get(position);
			number.setText(info.getNubmer());
			String modeStr = info.getMode();
			if ("1".equals(modeStr)) {
				mode.setText("电话拦截");
			} else if ("2".equals(modeStr)) {
				mode.setText("短信拦截");
			} else if ("3".equals(modeStr)) {
				mode.setText("全部拦截");
			}
			return convertView;
		}

	}

	static class ViewHolder {
		public ViewHolder(TextView number, TextView mode, ImageView delete) {
			super();
			this.number = number;
			this.mode = mode;
			this.delete = delete;
		}

		public TextView number;
		public TextView mode;
		public ImageView delete;
	}

	public void addBlackNumber(View view) {
		if (dialog == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			dialog = builder.create();
			if (dialogView == null) {
				dialogView = View.inflate(this,
						R.layout.dialog_add_black_number, null);
				messageBlock = (CheckBox) dialogView
						.findViewById(R.id.cb_msg_block);
				callBlock = (CheckBox) dialogView
						.findViewById(R.id.cb_call_block);
				phoneNo = (EditText) dialogView
						.findViewById(R.id.et_phone_number);
				cancel = (Button) dialogView.findViewById(R.id.cancel);
				confirm = (Button) dialogView.findViewById(R.id.confirm);
				cancel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						messageBlock.setChecked(false);
						callBlock.setChecked(false);
						phoneNo.setText("");
					}
				});
				confirm.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						String phoneNoStr = phoneNo.getText().toString();
						boolean msg = messageBlock.isChecked();
						boolean call = callBlock.isChecked();

						if (TextUtils.isEmpty(phoneNoStr)) {
							Toast.makeText(BlackNumberActivity.this, "请输入号码",
									Toast.LENGTH_SHORT).show();
							return;
						}

						String mode = null;
						if (msg && call) {
							mode = "3";
						} else if (msg) {
							mode = "2";
						} else if (call) {
							mode = "1";
						} else {
							Toast.makeText(BlackNumberActivity.this, "请勾选拦截类型",
									Toast.LENGTH_SHORT).show();
							return;
						}
						if (dao.find(phoneNoStr)) {
							Toast.makeText(BlackNumberActivity.this,
									"此号码号码已在黑名单中", Toast.LENGTH_SHORT).show();
							return;
						}

						dao.add(phoneNoStr, mode);
						BlackNumberInfo info = new BlackNumberInfo();
						info.setMode(mode);
						info.setNubmer(phoneNoStr);
						infos.add(0, info);
						mAdapter.notifyDataSetChanged();

						dialog.dismiss();
						messageBlock.setChecked(false);
						callBlock.setChecked(false);
						phoneNo.setText("");

					}
				});
			}

			dialog.setView(dialogView, 0, 0, 0, 0);

		}

		dialog.show();

	}
}
