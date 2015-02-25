package com.zengye.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.Mac;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.zengye.mobilesafe.R;
import com.zengye.mobilesafe.domain.AppInfo;
import com.zengye.mobilesafe.engine.AutoStartProvider;
import com.zengye.mobilesafe.fragment.AutoStartManagerFragment;

public class AutoStartManagerACitvity extends SherlockFragmentActivity
		implements OnPageChangeListener, ActionBar.TabListener,AutoStartManagerFragment.CompletedCallBack {

	/**
	 * 顶部Tab的title
	 */
	private String[] mTabTitles;

	/**
	 * ViewPager对象的引用
	 */
	private ViewPager vpAutoStart;

	/**
	 * 装载Fragment的容器，我们的每一个界面都是一个Fragment
	 */
	private List<Fragment> mFragmentList;

	/**
	 * ActionBar对象的引用
	 */
	private ActionBar mActionBar;

	private List<AppInfo> userApps;

	private List<AppInfo> systemApps;
	
	private List<AppInfo>[] data;
	
	private LinearLayout llLoading;

	private static final int DATA_COMPLETE = 0;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DATA_COMPLETE:
				mFragmentList.clear();
				for (int i = 0; i < mTabTitles.length; i++) {
					AutoStartManagerFragment fragment = new AutoStartManagerFragment(data[i],AutoStartManagerACitvity.this);
					mFragmentList.add(fragment);
					vpAutoStart.setAdapter(new TabPagerAdapter(getSupportFragmentManager(),
							mFragmentList));

					vpAutoStart.setOnPageChangeListener(AutoStartManagerACitvity.this);
				}
				
				break;

			default:
				break;
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auto_start);
		mTabTitles = getResources()
				.getStringArray(R.array.auto_start_tab_title);
		data = new List[2];
		
		llLoading = (LinearLayout) findViewById(R.id.ll_loading);
		llLoading.setVisibility(View.VISIBLE);
		vpAutoStart = (ViewPager) findViewById(R.id.vp_auto_start);

		mFragmentList = new ArrayList<Fragment>();

		mActionBar = getSupportActionBar();
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		for (int i = 0; i < mTabTitles.length; i++) {
			Tab tab = mActionBar.newTab();

			tab.setText(mTabTitles[i]);
			tab.setTabListener(this);
			mActionBar.addTab(tab, i);

		}

		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				initData();
			}
		}).start();
	}

	private void initData() {
		List<AppInfo> appInfos = AutoStartProvider
				.getAllAutoStartAppInfo(getPackageManager());
		userApps = new ArrayList<AppInfo>();
		systemApps = new ArrayList<AppInfo>();
		data[0] = userApps;
		data[1] = systemApps;
		for (AppInfo appInfo : appInfos) {
			if (appInfo.isUserApp()) {
				userApps.add(appInfo);
			} else {
				systemApps.add(appInfo);
			}
		}
		
		handler.sendEmptyMessage(DATA_COMPLETE);

	}

	class TabPagerAdapter extends FragmentStatePagerAdapter {

		private List<Fragment> fragments;

		public TabPagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		public TabPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
			super(fm);

			this.fragments = fragments;
		}

		@Override
		public Fragment getItem(int arg0) {
			// TODO Auto-generated method stub
			
			return fragments.get(arg0);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return fragments.size();
		}

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPageSelected(int arg0) {
		mActionBar.setSelectedNavigationItem(arg0); 
	}

	// ActionBar.TabListener
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		vpAutoStart.setCurrentItem(tab.getPosition());  
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void completed() {
		llLoading.setVisibility(View.INVISIBLE);
	}

}
