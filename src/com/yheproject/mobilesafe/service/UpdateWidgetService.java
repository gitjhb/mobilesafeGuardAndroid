package com.yheproject.mobilesafe.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.yheproject.mobilesafe.R;
import com.yheproject.mobilesafe.R.color;
import com.yheproject.mobilesafe.receiver.LockScreenReceiver;
import com.yheproject.mobilesafe.util.TaskUtil;

public class UpdateWidgetService extends Service {
	private Timer timer;
	private TimerTask task;
	private AppWidgetManager widgetmanager;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		timer = new Timer();
		widgetmanager = AppWidgetManager.getInstance(getApplicationContext());
		task = new TimerTask() {

			@Override
			public void run() {

				// 更新widget的界面
				ComponentName name = new ComponentName(
						"com.yheproject.mobilesafe",
						"com.yheproject.mobilesafe.receiver.ProcessWidget");
				RemoteViews views = new RemoteViews(
						"com.yheproject.mobilesafe", R.layout.process_widget);
				views.setTextViewText(R.id.process_count, "Processes Number: "
						+ TaskUtil.getProcessCount(getApplicationContext()));
				//views.setTextColor(R.id.process_count, color.widgetColor);
				views.setTextViewText(R.id.process_memory, "Avaliable Spaces: "
						+ TaskUtil.getMemeorySize(getApplicationContext()));
				//views.setTextColor(R.id.process_memory, color.widgetColor);
				Intent intent = new Intent(UpdateWidgetService.this,
						LockScreenReceiver.class);
				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						getApplicationContext(), 0, intent, 0);
				views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
				widgetmanager.updateAppWidget(name, views);

			}
		};
		timer.scheduleAtFixedRate(task, 1000, 2000);
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		timer.cancel();
		timer = null;
		task = null;
		super.onDestroy();
	}

}
