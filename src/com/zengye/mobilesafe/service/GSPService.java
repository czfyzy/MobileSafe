package com.zengye.mobilesafe.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class GSPService extends Service {

	private LocationManager lm;
	private MyLocationListener mListener;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		lm = (LocationManager)getSystemService(LOCATION_SERVICE);
		mListener = new MyLocationListener();
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String providerName = lm.getBestProvider(criteria, true);
		if (providerName != null) {   
			lm.requestLocationUpdates(providerName, 0, 0, mListener);
		}  

		
		
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		lm.removeUpdates(mListener);
		mListener = null;
	}
	
	public class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			//精确度
			float accuracy = location.getAccuracy();
			//经度
			double longitude = location.getLongitude();
			//纬度
			double  latitude = location.getLatitude();
			
			SharedPreferences sp = getSharedPreferences("config", Context.MODE_PRIVATE);
			
			Editor editor = sp.edit();
			editor.putString("lastLocation", "longitude:" + longitude + " latitude:" + latitude + " accuracy:" + accuracy);
			editor.commit();
			GSPService.this.stopSelf();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
	}

}
