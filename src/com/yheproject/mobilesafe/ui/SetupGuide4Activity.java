package com.yheproject.mobilesafe.ui;

import com.yheproject.mobilesafe.R;
import com.yheproject.mobilesafe.receiver.MyAdmin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

public class SetupGuide4Activity extends Activity implements OnClickListener {
	private CheckBox cb_isprotecting;
	private Button bt_setup_finish;
	private Button bt_previous;
	private SharedPreferences sp;
	DevicePolicyManager manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setupguide4);
		// ≥ı ºªØcheckBox
		cb_isprotecting = (CheckBox) findViewById(R.id.cb_isprotecting);
		bt_setup_finish = (Button) findViewById(R.id.bt_setup_finish);
		bt_previous = (Button) findViewById(R.id.bt_previous);
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		boolean isprotecting = sp.getBoolean("isprotecting", false);
		if (isprotecting) {
			cb_isprotecting.setText("SafeGuard is protecting");
			cb_isprotecting.setChecked(true);
		}

		cb_isprotecting
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							cb_isprotecting.setText("SafeGuard is protecting");
							Editor editor = sp.edit();
							editor.putBoolean("isprotecting", true);
							editor.commit();
						} else {
							cb_isprotecting.setText("SafeGuard Unused");
							Editor editor = sp.edit();
							editor.putBoolean("isprotecting", false);
							editor.commit();
						}
					}
				});

		bt_previous.setOnClickListener(this);
		bt_setup_finish.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_previous:
			Intent intent3 = new Intent(this, SetupGuide3Activity.class);
			finish();
			startActivity(intent3);
			overridePendingTransition(R.anim.translate_in, R.anim.translate_out);
			break;
		case R.id.bt_setup_finish:

			finishSetup();

			if (cb_isprotecting.isChecked()) {
				finish();
			} else {
				AlertDialog.Builder builder = new Builder(
						SetupGuide4Activity.this);
				builder.setTitle("Warning");
				builder.setMessage("Recommand start SafeGuard Protecting, finish setup£ø");
				builder.setPositiveButton("Confirm",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
								finishSetup();
							}
						});
				builder.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}
						});
				builder.create().show();
				return;
			}
			break;
		}

	}

	private void finishSetup() {
		Editor editor = sp.edit();
		editor.putBoolean("issetupalready", true);
		editor.commit();
		manager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);

		ComponentName mAdminName = new ComponentName(this, MyAdmin.class);

		if (!manager.isAdminActive(mAdminName)) {
			Intent intent = new Intent(
					DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
			startActivity(intent);
		}

	}
}
