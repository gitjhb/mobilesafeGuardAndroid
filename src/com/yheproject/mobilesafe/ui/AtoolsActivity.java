package com.yheproject.mobilesafe.ui;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.yheproject.mobilesafe.R;
import com.yheproject.mobilesafe.engine.DownLoadFileTask;
import com.yheproject.mobilesafe.engine.SmsInfoService;
import com.yheproject.mobilesafe.service.AddressService;
import com.yheproject.mobilesafe.service.BackupSmsService;

public class AtoolsActivity extends Activity implements OnClickListener {

	protected static final int ERROR = 10;
	protected static final int SUCCESS = 0;
	private TextView tv_atools_query;
	private TextView tv_atools_change_location;
	private TextView tv_atools_select_bg;
	private TextView tv_atools_address;
	private TextView tv_atools_sms_backup;
	private TextView tv_atools_sms_restore;
	private TextView tv_atools_app_lock;
	private CheckBox cb_atools_address;
	private TextView tv_atools_common_number;
	private Intent serviceIntent;
	private SharedPreferences sp;
	private ProgressDialog pd;
	private SmsInfoService mSmsInfoService;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case ERROR:
				Toast.makeText(getApplicationContext(),
						"Downloading database Failed", 0).show();
				break;
			case SUCCESS:
				Toast.makeText(getApplicationContext(),
						"Downloading database Successed", 0).show();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.atools);
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		tv_atools_common_number = (TextView) findViewById(R.id.tv_atools_common_number);
		tv_atools_app_lock = (TextView) findViewById(R.id.tv_atools_app_lock);
		tv_atools_query = (TextView) findViewById(R.id.tv_atools_query);
		tv_atools_select_bg = (TextView) findViewById(R.id.tv_atools_select_bg);
		tv_atools_change_location = (TextView) findViewById(R.id.tv_atools_change_location);
		tv_atools_address = (TextView) findViewById(R.id.tv_atools_address);
		tv_atools_sms_backup = (TextView) findViewById(R.id.tv_atools_sms_backup);
		tv_atools_sms_restore = (TextView) findViewById(R.id.tv_atools_sms_restore);
		cb_atools_address = (CheckBox) findViewById(R.id.cb_atools_address);
		serviceIntent = new Intent(this, AddressService.class);
		cb_atools_address
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							startService(serviceIntent);
							tv_atools_address.setTextColor(Color.WHITE);
							tv_atools_address.setText("Query Service Used");
						} else {
							stopService(serviceIntent);
							tv_atools_address.setTextColor(Color.RED);
							tv_atools_address.setText("Query Service Unused");
						}

					}
				});
		tv_atools_common_number.setOnClickListener(this);
		tv_atools_app_lock.setOnClickListener(this);
		tv_atools_sms_backup.setOnClickListener(this);
		tv_atools_sms_restore.setOnClickListener(this);
		tv_atools_change_location.setOnClickListener(this);
		tv_atools_query.setOnClickListener(this);
		tv_atools_select_bg.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_atools_query:
			if (isDBexist()) {
				Intent intent = new Intent(this, QueryNumberActivity.class);
				startActivity(intent);
			} else {
				// 下载数据库
				pd = new ProgressDialog(this);
				pd.setMessage("Downloading Database");
				pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				pd.show();
				pd.setCanceledOnTouchOutside(false);
				new Thread() {
					public void run() {
						String path = getResources().getString(
								R.string.addressdburl);
						String filepath = "/sdcard/address.db";
						try {
							DownLoadFileTask.getFile(path, filepath, pd);
							pd.dismiss();
							Message msg = new Message();
							msg.what = SUCCESS;
							handler.sendMessage(msg);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							pd.dismiss();
							Message msg = new Message();
							msg.what = ERROR;
							handler.sendMessage(msg);
						}
					};
				}.start();
			}

			break;
		case R.id.tv_atools_select_bg:
			AlertDialog.Builder builder = new Builder(this);
			builder.setTitle("Location Dsiplay Style");
			String[] items = new String[] { "translucence", "orange", "green" };
			builder.setSingleChoiceItems(items, 0,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							Editor editor = sp.edit();
							editor.putInt("background", which);
							editor.commit();
						}
					});
			builder.setPositiveButton("Confirm",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub

						}
					});
			builder.create().show();
			break;
		case R.id.tv_atools_change_location:
			Intent intent = new Intent(this, DragviewActivity.class);
			startActivity(intent);

			break;
		case R.id.tv_atools_sms_backup:
			// 用service来备份短信
			Intent service = new Intent(this, BackupSmsService.class);
			startService(service);
			break;

		case R.id.tv_atools_sms_restore:
			// 读取备份的xml文件
			// 解析文件里面的信息
			// 插入到短信应用里面
			final ProgressDialog pd = new ProgressDialog(this);
			pd.setCancelable(false);
			pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pd.setMessage("Messages Backuping");
			pd.show();
			mSmsInfoService = new SmsInfoService(this);
			new Thread() {
				@Override
				public void run() {
					try {
						mSmsInfoService.restoreSms("/sdcard/smsbackup.xml", pd);
						pd.dismiss();
						Looper.prepare();
						Toast.makeText(getApplicationContext(),
								"Backup Successed", 0).show();
						Looper.loop();
					} catch (Exception e) {
						e.printStackTrace();
						pd.dismiss();
						Looper.prepare();
						Toast.makeText(getApplicationContext(),
								"Backup Failed", 0).show();
						Looper.loop();
					}
				}
			}.start();

			break;
		case R.id.tv_atools_app_lock:
			Intent applockIntent = new Intent(this, AppLockActivity.class);
			startActivity(applockIntent);
			break;
		case R.id.tv_atools_common_number:
			Intent commonNumIntent = new Intent(this,CommonNumActivity.class);
			startActivity(commonNumIntent);
			break;
		}
	}

	public boolean isDBexist() {
		File file = new File("/sdcard/address.db");
		return file.exists();
	}
}
