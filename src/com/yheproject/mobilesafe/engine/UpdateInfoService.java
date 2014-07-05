package com.yheproject.mobilesafe.engine;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


import android.content.Context;

import com.yheproject.mobilesafe.domain.UpdateInfo;

public class UpdateInfoService {
	
	private Context context;
	
	public UpdateInfoService(Context context){
		this.context = context;
	}
	
	public UpdateInfo getUpdateInfo(int urlid) throws Exception {
		String path = context.getResources().getString(urlid);
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(2000);
		conn.setRequestMethod("GET");
		InputStream is = conn.getInputStream();
		return UpdateInfoParser.getUpdateInfo(is);
	}

}
