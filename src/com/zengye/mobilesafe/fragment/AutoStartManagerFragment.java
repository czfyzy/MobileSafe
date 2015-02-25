package com.zengye.mobilesafe.fragment;

import java.util.List;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.zengye.mobilesafe.R;
import com.zengye.mobilesafe.domain.AppInfo;
import com.zengye.mobilesafe.utils.CommonUtils;

public class AutoStartManagerFragment extends SherlockFragment {

	private static final String TAG = "AutoStartManagerFragment";
//	private LinearLayout llLoading;
	private ListView lvAutoStart;
	private PackageManager pm;
	private List<AppInfo> appInfos;
	private CompletedCallBack callBack;
	
	
	public AutoStartManagerFragment(List<AppInfo> appInfos,CompletedCallBack callBack) {
		super();
		// TODO Auto-generated constructor stub
		this.appInfos = appInfos;
		this.callBack = callBack;
	}



	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view  = inflater.inflate(R.layout.fragment_auto_start, null);
//		llLoading = (LinearLayout) view.findViewById(R.id.ll_loading);
		lvAutoStart = (ListView) view.findViewById(R.id.lv_auto_start);
		pm = getActivity().getPackageManager();
		
//		llLoading.setVisibility(View.VISIBLE);
		lvAutoStart.setAdapter(new AutoStartAdapter());
		
		lvAutoStart.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				AppInfo appInfo = appInfos.get(position);
				StringBuilder builder = new StringBuilder();
				//"pm disable " + resolveInfo.activityInfo.packageName + "/" + resolveInfo.activityInfo.name
				boolean is = appInfo.isAutoStart();
				builder.append("pm ");
				if(is) {
					builder.append("disable ");
				} else {
					builder.append("enable ");
				}
				builder.append(appInfo.getPackageName());
				builder.append("/");
				builder.append(appInfo.getReceiverClass());
				
				boolean success = CommonUtils.execCommandWithSu(builder.toString());
				if(success) {
					ViewHolder holder = (ViewHolder) view.getTag();
					holder.isStart.setChecked(!is);
					appInfo.setAutoStart(!is);
				} else {
					Toast.makeText(getActivity(), "禁用自启失败", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		callBack.completed();
		return view;
	}
	
	class AutoStartAdapter extends BaseAdapter {
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return appInfos.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return appInfos.get(position);
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
			if(convertView == null) {
				convertView = View.inflate(getActivity(), R.layout.list_item_auto_start, null);
				holder = new ViewHolder();
				holder.icon = (ImageView) convertView.findViewById(R.id.icon);
				holder.appName = (TextView) convertView.findViewById(R.id.name);
				holder.isStart = (CheckBox) convertView.findViewById(R.id.is_start);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			AppInfo appInfo = appInfos.get(position);
			if(appInfo.getIcon() != null) {
				
				holder.icon.setBackground(appInfo.getIcon());
			} else {
				holder.icon.setBackgroundResource(R.drawable.defual_icon);
			}
			holder.appName.setText(appInfo.getName());
			holder.isStart.setChecked(appInfo.isAutoStart());
			
			return convertView;
		}
		
	}
	
	class ViewHolder {
		ImageView icon;
		TextView appName;
		CheckBox isStart;
	}
	
    public interface CompletedCallBack {
		public void completed();
	}
	
	
}
