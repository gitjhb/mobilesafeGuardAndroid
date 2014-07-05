package com.yheproject.mobilesafe.ui;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yheproject.mobilesafe.R;
import com.yheproject.mobilesafe.domain.CacheInfo;
import com.yheproject.mobilesafe.ui.TrafficManagerActivity.ViewHolder;
import com.yheproject.mobilesafe.util.ImageUtil;
import com.yheproject.mobilesafe.util.TextFormater;

public class ClearSystemActivity extends ListActivity {
	private PackageManager pm;
	private ListView lv;
	private MyAdapter adapter;

	private Map<String, CacheInfo> maps;
	private TextView tv_clean;
	private ProgressBar pb_clean;
	private SQLiteDatabase db;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			String text = (String) msg.obj;
			tv_clean.setText(text);
		}

	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.clear);
		pm = getPackageManager();
		maps = new HashMap<String, CacheInfo>();
		lv = getListView();
		tv_clean = (TextView) this.findViewById(R.id.tv_clean);
		pb_clean = (ProgressBar) this.findViewById(R.id.pb_clean);

		// 判断手机内存里面是否有数据库存在
		File file = new File("/data/data/com.yheproject.mobilesafe/files/clearpath.db");
		if (!file.exists()) {
			copyfile();
		}
		// lv.setAdapter(adapter);

		// 1.获取所有的安装好的应用程序

		List<PackageInfo> packageinfos = pm
				.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		for (PackageInfo info : packageinfos) {
			String name = info.applicationInfo.loadLabel(pm).toString();
			String packname = info.packageName;
			Drawable icon = info.applicationInfo.loadIcon(pm);
			CacheInfo cacheinfo = new CacheInfo();
			cacheinfo.setName(name);
			cacheinfo.setIcon(icon);
			cacheinfo.setPackname(packname);
			maps.put(packname, cacheinfo);
			getAppSize(packname);
		}

		adapter = new MyAdapter();
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				/**
				 * <intent-filter> <action android:name=
				 * "android.settings.APPLICATION_DETAILS_SETTINGS" /> <category
				 * android:name="android.intent.category.DEFAULT" /> <data
				 * android:scheme="package" /> </intent-filter>
				 */
				/**
				 * <intent-filter> <action
				 * android:name="android.intent.action.VIEW" /> <category
				 * android:name="android.intent.category.DEFAULT" /> <category
				 * android:name="android.intent.category.VOICE_LAUNCH" />
				 * </intent-filter>
				 */

				Intent intent = new Intent();
				intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
				intent.addCategory("android.intent.category.DEFAULT");
				CacheInfo info = (CacheInfo) lv.getItemAtPosition(position);
				intent.setData(Uri.parse("package:" + info.getPackname()));
				// intent.putExtra("pkg", info.getPackname());
				Toast.makeText(getApplicationContext(),
						"Forwarding you to setting to clear.", 0).show();
				startActivity(intent);
			}
		});

	}

	/**
	 * 根据包名获取应用程序的体积信息 注意: 这个方法是一个异步的方法 程序的体积要花一定时间才能获取到.
	 * 
	 * @param packname
	 */
	private void getAppSize(final String packname) {
		try {
			Method method = PackageManager.class.getMethod(
					"getPackageSizeInfo", new Class[] { String.class,
							IPackageStatsObserver.class });

			method.invoke(pm, new Object[] { packname,
					new IPackageStatsObserver.Stub() {

						public void onGetStatsCompleted(PackageStats pStats,
								boolean succeeded) throws RemoteException {
							// 注意这个操作是一个异步的操作
							long cachesize = pStats.cacheSize;
							long codesize = pStats.codeSize;
							long datasize = pStats.dataSize;

							CacheInfo info = maps.get(packname);
							info.setCache_size(TextFormater
									.getDataSize(cachesize));
							info.setData_size(TextFormater
									.getDataSize(datasize));
							info.setCode_size(TextFormater
									.getDataSize(codesize));
							maps.put(packname, info);

						}
					} });

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void copyfile() {

		try {
			InputStream is = getClass().getClassLoader().getResourceAsStream(
					"clearpath.db");
			OutputStream fos = this
					.openFileOutput("clearpath.db", MODE_PRIVATE);
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

	private class MyAdapter extends BaseAdapter {

		private Set<Entry<String, CacheInfo>> sets;
		private List<CacheInfo> cacheinfos;

		public MyAdapter() {
			sets = maps.entrySet();
			cacheinfos = new ArrayList<CacheInfo>();
			for (Entry<String, CacheInfo> entry : sets) {
				cacheinfos.add(entry.getValue());
			}
		}

		public int getCount() {

			return cacheinfos.size();
		}

		public Object getItem(int position) {

			return cacheinfos.get(position);
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			CacheInfo info = cacheinfos.get(position);
			if (convertView == null) {
				view = View.inflate(ClearSystemActivity.this,
						R.layout.clear_item, null);
			} else {
				view = convertView;
			}
			ViewHolder holder = new ViewHolder();
			holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
			holder.tv_cache_size = (TextView) view
					.findViewById(R.id.tv_cache_size);
			holder.tv_code_size = (TextView) view
					.findViewById(R.id.tv_code_size);
			holder.tv_data_size = (TextView) view
					.findViewById(R.id.tv_data_size);
			holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
			holder.iv_icon.setImageDrawable(info.getIcon());
			holder.tv_cache_size.setText(info.getCache_size());
			holder.tv_code_size.setText(info.getCode_size());
			holder.tv_data_size.setText(info.getData_size());
			holder.tv_name.setText(info.getName());

			return view;
		}
	}

	static class ViewHolder {
		ImageView iv_icon;
		TextView tv_cache_size;
		TextView tv_data_size;
		TextView tv_name;
		TextView tv_code_size;
	}

	public void clean(View view) {
		db = SQLiteDatabase.openDatabase(
				"/data/data/com.yheproject.mobilesafe/files/clearpath.db", null,
				SQLiteDatabase.OPEN_READONLY);
		new Thread() {
			@Override
			public void run() {
				List<PackageInfo> packinfos = getPackageManager()
						.getInstalledPackages(0);
				pb_clean.setMax(packinfos.size());// 设置进度条的最大条目个数
				int total = 0;
				for (PackageInfo info : packinfos) {
					String packname = info.packageName;
					Cursor cursor = db.rawQuery(
							"select filepath from softdetail where apkname=?",
							new String[] { packname });
					if (cursor.moveToFirst()) {
						String path = cursor.getString(0);
						System.out.println("清除" + packname + "sd卡缓存" + path);
						File file = new File(
								Environment.getExternalStorageDirectory(), path);
						//deleteDir(file);
						try {
							sleep(500);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					total++;
					pb_clean.setProgress(total);
					cursor.close();
					Message msg = Message.obtain();
					msg.obj = "clean " + packname;
					handler.sendMessage(msg);
				}

				Message msg = Message.obtain();
				msg.obj = "All Finish.";
				handler.sendMessage(msg);
				db.close();
			}

		}.start();
	}

	private void deleteDir(File file) {
		//容易造成死循环
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				deleteDir(file);
			}
		} else {
			file.delete();
		}
	}
}
