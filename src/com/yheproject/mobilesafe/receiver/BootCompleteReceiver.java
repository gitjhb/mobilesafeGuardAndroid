package com.yheproject.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class BootCompleteReceiver extends BroadcastReceiver {
	
	private SharedPreferences sp;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		//判断手机是否处于保护状态
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		boolean isprotecting = sp.getBoolean("isprotecting", false);
		if (isprotecting) {
			TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String currentsim = manager.getSimSerialNumber();
			String realsim = sp.getString("sim", "");
			if (!currentsim.equals(realsim)) {
				SmsManager smsManager = SmsManager.getDefault();
				String destinationAddress = sp.getString("safenumber", "");
				smsManager.sendTextMessage(destinationAddress, null, "sim卡发生了改变，手机可能被盗", null, null);
			}
		}
	}

}
