package com.yheproject.mobilesafe.service;

import java.lang.reflect.Method;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.internal.telephony.ITelephony;
import com.yheproject.mobilesafe.R;
import com.yheproject.mobilesafe.db.dao.BlackNumberDao;
import com.yheproject.mobilesafe.engine.NumberAddressService;
import com.yheproject.mobilesafe.ui.CallSmsActivity;
import com.yheproject.mobilesafe.util.Constants;

public class AddressService extends Service {
	public static final String TAG = "AddressService";
	public static final int MESSAGE_DISMISS_TOAST = 1;
	private TelephonyManager manager;
	private MyPhoneListener listener;
	private WindowManager windowmanager;
	private SharedPreferences sp;
	private View view;
	private BlackNumberDao dao;
	private long firstRingTime;
	private long endRingTime;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_DISMISS_TOAST:
				if (view != null) {
					windowmanager.removeView(view);
					view = null;
				}
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		dao = new BlackNumberDao(this);
		listener = new MyPhoneListener();
		sp = getSharedPreferences("config", MODE_PRIVATE);
		windowmanager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
		// ע��ϵͳ�绰�������ļ�����
		manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		String action = intent.getStringExtra("action");
		String incomingNumber = intent.getStringExtra("textString");
		if (Constants.SHOW_ADDRESS.equals(action)) {
			String address = NumberAddressService.getAddress(incomingNumber);
			showLoaction(address);
			handler.sendEmptyMessageDelayed(MESSAGE_DISMISS_TOAST, 3000);

		}

		return super.onStartCommand(intent, flags, startId);
	}

	private class MyPhoneListener extends PhoneStateListener {

		// �绰״̬�����ı��ʱ�� ���õķ���
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE: // ���ھ�ֹ״̬: û�к���
				endRingTime = System.currentTimeMillis();
				if (view != null) {
					windowmanager.removeView(view);
					view = null;
				}
				long callTime = endRingTime - firstRingTime;
				if (endRingTime > firstRingTime && callTime < 2000
						&& callTime > 0) {
					Log.i(TAG, "��һ���ĺ���" + incomingNumber);
					endRingTime = 0;
					firstRingTime = 0;
					// ����notification
					showNotification(incomingNumber);
				}
				// �ٻ�ȡһ��ϵͳ��ʱ��
				break;

			case TelephonyManager.CALL_STATE_RINGING: // ����״̬
				Log.i(TAG, "�������Ϊ" + incomingNumber);
				firstRingTime = System.currentTimeMillis();
				if (dao.find(incomingNumber)) {
					endCall();
					// deleteCallLog(incomingNumber);
					getContentResolver().registerContentObserver(
							CallLog.Calls.CONTENT_URI, true,
							new MyObserver(new Handler(), incomingNumber));
				}
				String address = NumberAddressService
						.getAddress(incomingNumber);

				Log.i(TAG, "������Ϊ" + address);
				// Toast.makeText(getApplicationContext(), "������Ϊ"+ address,
				// 1).show();
				showLoaction(address);
				// ��ȡһ�µ�ǰϵͳ��ʱ��

				break;
			case TelephonyManager.CALL_STATE_OFFHOOK: // ��ͨ�绰״̬
				if (view != null) {
					windowmanager.removeView(view);
					view = null;
				}
				break;
			}

		}

		public void endCall() {
			try {
				Method method = Class.forName("android.os.ServiceManager")
						.getMethod("getService", String.class);
				IBinder binder = (IBinder) method.invoke(null,
						new Object[] { TELEPHONY_SERVICE });
				ITelephony telephony = ITelephony.Stub.asInterface(binder);
				telephony.endCall();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		manager.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
	}

	public void deleteCallLog(String incomingNumber) {
		ContentResolver resolver = getContentResolver();
		Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI, null,
				"number=?", new String[] { incomingNumber }, null);
		if (cursor.moveToFirst()) {// ��ѯ���˺��м�¼
			String id = cursor.getString(cursor.getColumnIndex("_id"));
			resolver.delete(CallLog.Calls.CONTENT_URI, "_id=?",
					new String[] { id });
		}
	}

	public void showNotification(String incomingNumber) {
		// 1. ��ȡnotification�Ĺ������
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// 2 ��һ��Ҫ����ʾ��notification ���󴴽�����
		int icon = R.drawable.notification;
		CharSequence tickerText = "������һ������";
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);
		// 3 .����notification��һЩ����
		Context context = getApplicationContext();
		CharSequence contentTitle = "��һ������";
		CharSequence contentText = incomingNumber;
		notification.flags = Notification.FLAG_AUTO_CANCEL;

		Intent notificationIntent = new Intent(this, CallSmsActivity.class);
		// ����һ���ĺ��� ���õ�intent��������
		notificationIntent.putExtra("number", incomingNumber);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);

		// 4. ͨ��manger��notification ����
		manager.notify(0, notification);
	}

	/**
	 * �ڴ�������ʾ����λ����Ϣ
	 * 
	 * @param address
	 */
	public void showLoaction(String address) {
		WindowManager.LayoutParams params = new LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		params.type = WindowManager.LayoutParams.TYPE_TOAST;
		params.setTitle("Toast");
		params.gravity = Gravity.LEFT | Gravity.TOP;

		params.x = sp.getInt("lastx", 0);
		params.y = sp.getInt("lasty", 0);

		view = View.inflate(getApplicationContext(), R.layout.show_location,
				null);
		LinearLayout ll = (LinearLayout) view.findViewById(R.id.ll_location);

		int backgroundid = sp.getInt("background", 0);
		if (backgroundid == 0) {
			ll.setBackgroundResource(R.drawable.call_locate_gray);
		} else if (backgroundid == 1) {
			ll.setBackgroundResource(R.drawable.call_locate_orange);
		} else {
			ll.setBackgroundResource(R.drawable.call_locate_green);
		}

		TextView tv = (TextView) view.findViewById(R.id.tv_location);
		tv.setTextSize(30);
		tv.setTextColor(Color.RED);
		tv.setText(address);
		windowmanager.addView(view, params);
	}

	private class MyObserver extends ContentObserver {
		private String incomingnumber;

		public MyObserver(Handler handler, String incomingnumber) {
			super(handler);
			this.incomingnumber = incomingnumber;
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			deleteCallLog(incomingnumber);

			// ��ɾ���˺��м�¼�� ��ע�����ݹ۲���
			getContentResolver().unregisterContentObserver(this);
		}
	}
}
