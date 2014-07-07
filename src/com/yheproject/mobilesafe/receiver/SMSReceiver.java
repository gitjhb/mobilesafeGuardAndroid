package com.yheproject.mobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.yheproject.mobilesafe.R;
import com.yheproject.mobilesafe.db.dao.BlackNumberDao;
import com.yheproject.mobilesafe.engine.GPSInfoProvider;

public class SMSReceiver extends BroadcastReceiver {

	private static final String TAG = "SMSReceiver";
	private BlackNumberDao dao;
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// 获取短信的内容
		// #*location*#123456
		Object[] pdus = (Object[]) intent.getExtras().get("pdus");
		for (Object pdu : pdus) {
			SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
			String content = sms.getMessageBody();
			Log.i(TAG, "短信内容" + content);
			String sender = sms.getOriginatingAddress();
			if ("#*location*#".equals(content)) {
				// 终止广播
				abortBroadcast();
				GPSInfoProvider provider = GPSInfoProvider.getInstance(context);
				String location = provider.getLocation();
				SmsManager smsmanager = SmsManager.getDefault();
				if ("".equals(location)) {

				} else {
					smsmanager.sendTextMessage(sender, null, location, null,
							null);
				}
			} else if ("#*locknow*#".equals(content)) {
				DevicePolicyManager manager = (DevicePolicyManager) context
						.getSystemService(Context.DEVICE_POLICY_SERVICE);
				manager.resetPassword("123", 0);
				manager.lockNow();
				abortBroadcast();
			} else if ("#*wipedata*#".equals(content)) {
				DevicePolicyManager manager = (DevicePolicyManager) context
						.getSystemService(Context.DEVICE_POLICY_SERVICE);
				manager.wipeData(0);
				abortBroadcast();
			} else if ("#*alarm*#".equals(content)) {
				MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
				player.setVolume(1.0f, 1.0f);
				player.start();
				abortBroadcast();
			}
			if (dao.find(sender)) {
				abortBroadcast();
			}
		}

	}
}
