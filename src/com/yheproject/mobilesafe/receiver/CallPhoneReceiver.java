package com.yheproject.mobilesafe.receiver;

import com.yheproject.mobilesafe.ui.LostProtectedActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CallPhoneReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
			String number = getResultData();
			if("20122012".equals(number)){
				Intent lostIntent = new Intent(context,LostProtectedActivity.class);
				lostIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(lostIntent);
				//终止掉这个外拨电话
				setResultData(null);
			}
	}
}
