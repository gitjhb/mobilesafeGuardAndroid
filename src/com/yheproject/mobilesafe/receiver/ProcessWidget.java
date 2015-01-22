package com.yheproject.mobilesafe.receiver;

import com.yheproject.mobilesafe.service.UpdateWidgetService;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class ProcessWidget extends AppWidgetProvider {
	Intent intent;

	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		super.onEnabled(context);
		intent = new Intent(context, UpdateWidgetService.class);
		context.startService(intent);
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onDeleted(context, appWidgetIds);
		intent = new Intent(context, UpdateWidgetService.class);
		context.stopService(intent);
	}
}
