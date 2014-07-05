package com.yheproject.mobilesafe.ui;

import java.io.File;

import com.yheproject.mobilesafe.R;
import com.yheproject.mobilesafe.R.layout;
import com.yheproject.mobilesafe.R.menu;
import com.yheproject.mobilesafe.domain.UpdateInfo;
import com.yheproject.mobilesafe.engine.DownLoadFileTask;
import com.yheproject.mobilesafe.engine.UpdateInfoService;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SplashActivity extends Activity {
	private static final String tag = "SplashActivity";
	private TextView tv_splash_version;
	private LinearLayout ll_splash_main;
	private UpdateInfo info;
	private ProgressDialog pd;
	private String versiontext;
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			//判断两个版本号是否相同
			if (isNeedUpdate(versiontext)) {
				Log.i(tag, "弹出来升级对话框");
				showUpdateDialog();
			}
		}
	
	};
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		pd = new ProgressDialog(this);
		pd.setMessage("Downloading。。。。。");
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setCancelable(false);
		ll_splash_main = (LinearLayout) findViewById(R.id.ll_splash_main);
		tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		versiontext = getVersion();
		//先延时
		new Thread(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				try {
					sleep(2000);
					handler.sendEmptyMessage(0);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}.start();
		
		
		
		tv_splash_version.setText("Version: "+ versiontext);
		AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);
		aa.setDuration(2000);
		ll_splash_main.startAnimation(aa);
		ll_splash_main.setBackgroundResource(R.drawable.home_top_normal_bg);
		// 完成窗體的全屏显示
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
	
	private void showUpdateDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setIcon(R.drawable.icon5);
		builder.setTitle("Upgrade Reminding");
		builder.setMessage(info.getDescription());
		builder.setCancelable(false);
		builder.setPositiveButton("Confirm", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.i(tag, "下载apk"+info.getApkurl());
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					DownLoadFileThreadTask task = new DownLoadFileThreadTask(info.getApkurl(), "/sdcard/new.apk");
					pd.show();
					new Thread(task).start();
				}else {
					Toast.makeText(getApplicationContext(), "SD card cannot be used", 0).show();
					loadMainUI();
				}
			}
		});
		builder.setNegativeButton("Cancel", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.i(tag, "取消,进入主界面");
				loadMainUI();
				
			}
		});
		builder.create().show();
	}

	private class DownLoadFileThreadTask implements Runnable{
		private String path;
		private String filepath;
		
		public DownLoadFileThreadTask(String path,String filepath){
			this.path = path;
			this.filepath = filepath;
		}
		@Override
		public void run() {
			try {
				File file = DownLoadFileTask.getFile(path, filepath, pd);
				pd.dismiss();
				install(file);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "Download Failed", 0).show();
				pd.dismiss();
				loadMainUI();
			}
		}
		
	}
	
	private boolean isNeedUpdate(String versiontext) {
		UpdateInfoService service = new UpdateInfoService(this);
		try {
			info = service.getUpdateInfo(R.string.updateurl);
			String version = info.getVersion();
			if(versiontext.equals(version)){
				Log.i(tag, "版本号相同，无需升级，进入主界面");
				loadMainUI();
				return false;
			}else {
				Log.i(tag, "版本号不同，需要升级");
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(this, "Require Upgrading Information Exception, Please check your network.", 0).show();
			Log.i(tag, "获取更新信息异常，进入主界面");
			loadMainUI();
			return false;
		}
		
		
	}

	private String getVersion(){
		try {
			PackageManager manager = getPackageManager();
			PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
			return info.versionName;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Unknown Version Number";
		}
	}
	
	private void loadMainUI(){
		Intent intent = new Intent(this,MainAcitivity.class);
		startActivity(intent);
		finish();
	}
	
	private void install(File file){
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		finish();
		startActivity(intent);
	}
}
