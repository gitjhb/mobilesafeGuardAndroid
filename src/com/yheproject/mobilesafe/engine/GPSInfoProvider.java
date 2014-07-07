package com.yheproject.mobilesafe.engine;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class GPSInfoProvider {
	LocationManager manager;
	private static GPSInfoProvider mGpsInfoProvider;
	private static Context context;
	private static MyLocationListener listener;

	private GPSInfoProvider() {
	};

	public static synchronized GPSInfoProvider getInstance(Context context) {
		if (mGpsInfoProvider == null) {
			mGpsInfoProvider = new GPSInfoProvider();
			GPSInfoProvider.context = context;
		}
		return mGpsInfoProvider;
	}

	public String getLocation() {
		manager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		String provider = getProvider(manager);
		manager.requestLocationUpdates(provider, 6000, 50, getListener());
		SharedPreferences sp = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		String location = sp.getString("location", "");
		return location;
	}

	public void stopGPSListener(){
		manager.removeUpdates(getListener());
	}
	
	private MyLocationListener getListener() {
		if (listener == null) {
			listener = new MyLocationListener();
		}
		return listener;
	}

	private class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			String latitude = "latitude" + location.getLatitude();
			String longitude = "longitude" + location.getLongitude();
			SharedPreferences sp = context.getSharedPreferences("config",
					Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString("location", latitude + "-" + longitude);
			editor.commit();
		}

		// 设备从可用到不可用

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

	// 返回最好的位置提供者
	private String getProvider(LocationManager manager) {
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
		criteria.setSpeedRequired(true);
		criteria.setCostAllowed(true);
		return manager.getBestProvider(criteria, true);
	}
}
