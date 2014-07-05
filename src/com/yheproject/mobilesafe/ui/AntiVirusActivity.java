package com.yheproject.mobilesafe.ui;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.yheproject.mobilesafe.R;
import com.yheproject.mobilesafe.util.MD5Encoder;

public class AntiVirusActivity extends Activity {

	protected static final int STOP = 100;
	private ImageView iv_antivirus;
	private ProgressBar pb_antivirus;
	private LinearLayout ll_antivirus;
	private AnimationDrawable anim;
	private ScrollView sv_antivirus;
	private SQLiteDatabase db;
	private boolean flagscanning = false;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
			if (msg.what == STOP) {
				ll_antivirus.removeAllViews();
				anim.stop();
				iv_antivirus.setBackgroundResource(R.drawable.main_status_baohu);
			}
			String str = (String) msg.obj;
			TextView tv = new TextView(getApplicationContext());
			tv.setText(str);
			ll_antivirus.setOrientation(LinearLayout.VERTICAL);
			ll_antivirus.addView(tv);
			sv_antivirus.scrollBy(0, 20);

			System.out.println(str);

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.antivirus);

		// 判断手机内存里面是否有数据库存在
		File file = new File(
				"/data/data/com.yheproject.mobilesafe/files/antivirus.db");
		if (!file.exists()) {
			copyfile();
		}
		db = SQLiteDatabase.openDatabase("/data/data/com.yheproject.mobilesafe/files/antivirus.db", null,
				SQLiteDatabase.OPEN_READONLY);
		iv_antivirus = (ImageView) this.findViewById(R.id.iv_antivirus);
		pb_antivirus = (ProgressBar) this.findViewById(R.id.pb_antivirus);
		ll_antivirus = (LinearLayout) this.findViewById(R.id.ll_antivirus);
		iv_antivirus.setBackgroundResource(R.drawable.anti_anim);
		sv_antivirus = (ScrollView) this.findViewById(R.id.sv_antivirus);
		anim = (AnimationDrawable) iv_antivirus.getBackground();
	}

	private void copyfile() {
		try {
			InputStream is = getClass().getClassLoader().getResourceAsStream(
					"antivirus.db");
			OutputStream fos = this
					.openFileOutput("antivirus.db", MODE_PRIVATE);
			byte[] buffer = new byte[1024];
			int len = 0;

			while ((len = is.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (flagscanning)
			return false;

		if (event.getAction() == MotionEvent.ACTION_UP) {
			flagscanning = true;
			iv_antivirus.setBackgroundResource(R.drawable.anti_anim);
			anim = (AnimationDrawable) iv_antivirus.getBackground();
			anim.start();
			new Thread() {
				public void run() {
					List<PackageInfo> infos = getPackageManager()
							.getInstalledPackages(
									PackageManager.GET_UNINSTALLED_PACKAGES
											| PackageManager.GET_SIGNATURES);
					// 获取每一个应用程序的签名 获取到这个签名后 需要在数据库里面查询
					pb_antivirus.setMax(infos.size());
					int total = 0;
					int virustotal = 0;
					for (PackageInfo info : infos) {
						total++;
						try {
							sleep(20);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Message msg = Message.obtain();
						msg.obj = "Scanning  :" + info.packageName;
						handler.sendMessage(msg);
						Signature[] signs = info.signatures;
						String str = signs[0].toCharsString();
						String md5 = MD5Encoder.encode(str);

						if ("6c20c50051ad1dedce0528e0f3dfbc96".equals(md5)) {
							System.err.println("----------");
						}
						Cursor cursor = db.rawQuery(
								"select desc from datable where md5=?",
								new String[] { md5 });
						if (cursor.moveToFirst()) {
							String desc = cursor.getString(0);
							msg = Message.obtain();
							msg.obj = info.packageName + ": " + desc;

							handler.sendMessage(msg);
							virustotal++;
						}
						cursor.close();
						pb_antivirus.setProgress(total);

					}
					Message msg = Message.obtain();
					msg.what = STOP;
					msg.obj = "Scan Finish, find " + virustotal + " virus.";
					handler.sendMessage(msg);
					flagscanning = false;
					pb_antivirus.setProgress(0);
				};
			}.start();
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onDestroy() {
		if (db.isOpen())
			db.close();
		super.onDestroy();
	}
}
