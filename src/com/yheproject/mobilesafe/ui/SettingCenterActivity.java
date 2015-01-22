package com.yheproject.mobilesafe.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.yheproject.mobilesafe.R;
import com.yheproject.mobilesafe.service.WatchDogService;

public class SettingCenterActivity extends Activity {
	private TextView tv_setting_applock;
	private CheckBox cb_setting_applock;
	private Intent watchdogintent;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean islockserviceopen = sp.getBoolean("islockserviceopen", false);
		setContentView(R.layout.setting_center);
		cb_setting_applock = (CheckBox) this
				.findViewById(R.id.cb_setting_applock);
		tv_setting_applock = (TextView) this
				.findViewById(R.id.tv_setting_applock);
		if (islockserviceopen) {
			tv_setting_applock.setText("Software Lock Service Used");
			cb_setting_applock.setChecked(true);
		} else {
			tv_setting_applock.setText("Software Lock Service Unused");
		}
		watchdogintent = new Intent(this, WatchDogService.class);

		cb_setting_applock
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							startService(watchdogintent);
							tv_setting_applock.setText("Software Lock Service Used");
							Editor editor = sp.edit();
							editor.putBoolean("islockserviceopen", true);
							editor.commit();
						} else {
							stopService(watchdogintent); // ->ondestroy->flag
															// false->Í£Ö¹×ÓÏß³Ì
							tv_setting_applock.setText("Software Lock Service Unused");
							Editor editor = sp.edit();
							editor.putBoolean("islockserviceopen", false);
							editor.commit();
						}

					}
				});

	}

}
