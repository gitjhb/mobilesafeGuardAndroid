package com.yheproject.mobilesafe.ui;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;

import com.yheproject.mobilesafe.R;
import com.yheproject.mobilesafe.adapter.MainUIAdapter;

public class MainAcitivity extends Activity implements OnItemClickListener {

	private static final String tag = "MainAcitivity";
	private static final int lOGO_INVISIBLE = 0;
	private static final int lOGO_VISIBLE = 1;
	private GridView gv_main;
	private MainUIAdapter adapter;
	private SharedPreferences sp;
	private SlidingDrawer sd;
	private ImageView im_logo_info;
	private LinearLayout ll_main_show_logo;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case lOGO_INVISIBLE:
				AlphaAnimation aa = new AlphaAnimation(1.0f, 0.0f);
				aa.setDuration(500);
				im_logo_info.setClickable(false);
				ll_main_show_logo.startAnimation(aa);
				ll_main_show_logo.setVisibility(View.INVISIBLE);

				break;
			case lOGO_VISIBLE:
				AlphaAnimation aa2 = new AlphaAnimation(0.0f, 1.0f);
				aa2.setDuration(1000);
				ll_main_show_logo.startAnimation(aa2);
				ll_main_show_logo.setVisibility(View.VISIBLE);
				im_logo_info.setClickable(true);
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainscreen);
		sp = this.getSharedPreferences("config", Context.MODE_PRIVATE);
		gv_main = (GridView) findViewById(R.id.gv_main);
		sd = (SlidingDrawer) findViewById(R.id.sd);
		ll_main_show_logo = (LinearLayout) findViewById(R.id.ll_main_show_logo);
		im_logo_info = (ImageView) findViewById(R.id.im_logo_info);
		im_logo_info.setClickable(true);
		sd.setOnDrawerOpenListener(new OnDrawerOpenListener() {

			@Override
			public void onDrawerOpened() {
				ImageView iv = (ImageView) sd.findViewById(R.id.handle);
				iv.setBackgroundResource(R.drawable.slide_down__selector);
				handler.sendEmptyMessage(lOGO_INVISIBLE);
			}
		});
		sd.setOnDrawerCloseListener(new OnDrawerCloseListener() {

			@Override
			public void onDrawerClosed() {
				ImageView iv = (ImageView) sd.findViewById(R.id.handle);
				iv.setBackgroundResource(R.drawable.slide_up__selector);
				handler.sendEmptyMessage(lOGO_VISIBLE);
			}
		});
		adapter = new MainUIAdapter(this);
		gv_main.setAdapter(adapter);
		gv_main.setOnItemClickListener(this);
		gv_main.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent,
					final View view, int position, long id) {
				if (position == 0) {

					AlertDialog.Builder builder = new Builder(
							MainAcitivity.this);
					builder.setTitle("Config");
					builder.setCancelable(false);
					builder.setMessage("Please input the name you want to change");
					final EditText et = new EditText(MainAcitivity.this);
					et.setHint("Please input text");
					builder.setView(et);
					builder.setPositiveButton("Confirm", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							String name = et.getText().toString().trim();
							if ("".equals(name)) {
								Toast.makeText(getApplicationContext(),
										"text cannot be empty", 0).show();
								return;
							} else {
								Editor editor = sp.edit();
								editor.putString("lost_name", name);
								editor.commit();
								TextView tv = (TextView) view
										.findViewById(R.id.tv_main_name);
								tv.setText(name);
							}
						}
					});

					builder.setNegativeButton("Cancel", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});

					AlertDialog dialog = builder.create();
					dialog.show();
				}
				return true;
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Log.i(tag, "点击的位置" + position);
		switch (position) {
		case 0:
			Log.i(tag, "进入手机防盗" + position);
			Intent lostIntent = new Intent(MainAcitivity.this,
					LostProtectedActivity.class);
			startActivity(lostIntent);
			break;
		case 1:
			Log.i(tag, "进入通讯卫士" + position);
			Intent callIntent = new Intent(MainAcitivity.this,
					CallSmsActivity.class);
			startActivity(callIntent);
			break;
		case 2:
			Log.i(tag, "进入软件管家" + position);
			Intent appIntent = new Intent(MainAcitivity.this,
					AppManagerActivity.class);
			startActivity(appIntent);
			break;
		case 3:
			Log.i(tag, "进入任务管理" + position);
			Intent taskmanagerIntent = new Intent(MainAcitivity.this,
					TaskManagerActivity.class);
			startActivity(taskmanagerIntent);
			break;
		case 4:
			Log.i(tag, "进入任务管理" + position);
			Intent trafficmanagerIntent = new Intent(MainAcitivity.this,
					TrafficManagerActivity.class);
			startActivity(trafficmanagerIntent);
			break;
		case 5:
			Log.i(tag, "进入手机杀毒" + position);
			Intent antiIntent = new Intent(MainAcitivity.this,
					AntiVirusActivity.class);
			startActivity(antiIntent);
			break;
		case 6:
			Log.i(tag, "进入系统优化" + position);
			Intent clearIntent = new Intent(MainAcitivity.this,
					ClearSystemActivity.class);
			startActivity(clearIntent);
			break;
		case 7:
			Log.i(tag, "进入高级工具" + position);
			Intent atoolsIntent = new Intent(MainAcitivity.this,
					AtoolsActivity.class);
			startActivity(atoolsIntent);
			break;
		case 8:
			Log.i(tag, "进入设置中心" + position);
			Intent settingIntent = new Intent(MainAcitivity.this,
					SettingCenterActivity.class);
			startActivity(settingIntent);
			break;
		}
	}

	public void info(View view) {
		Intent intent = new Intent(this, InformationActivity.class);
		startActivity(intent);
	}
}
