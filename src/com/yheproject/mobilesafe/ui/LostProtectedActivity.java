package com.yheproject.mobilesafe.ui;

import com.yheproject.mobilesafe.R;
import com.yheproject.mobilesafe.util.MD5Encoder;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LostProtectedActivity extends Activity implements OnClickListener {
	
	private static final String tag = "LostProtectedActivity";
	private SharedPreferences sp;
	private Dialog dialog;
	private EditText et_first_entry_pwd;
	private EditText et_first_entry_confirm;
	private TextView tv_lost_protected_number;
	private TextView tv_reentry_setup_guide;
	private CheckBox cb_isprotecting;
	private LinearLayout ll_reentry_setup_guide;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		if (isPWDSetup()) {
			Log.i(tag, "设置了密码，正常登陆");
			showNormalDialog();
		} else {
			Log.i(tag, "没有设置密码");
			showFirstEntryDialog();
		}
	}

	private void showNormalDialog() {
		dialog = new Dialog(this, R.style.MyDialog);
		dialog.setCancelable(false);
		// dialog.setContentView(R.layout.first_entry_dialog);
		View view = View.inflate(this, R.layout.normal_entry_dialog, null);
		et_first_entry_pwd = (EditText) view
				.findViewById(R.id.et_normal_entry_pwd);

		Button bt_normal_dialog_cancel = (Button) view
				.findViewById(R.id.bt_normal_dialog_cancel);
		Button bt_normal_dialog_confirm = (Button) view
				.findViewById(R.id.bt_normal_dialog_confirm);

		bt_normal_dialog_cancel.setOnClickListener(this);
		bt_normal_dialog_confirm.setOnClickListener(this);

		dialog.setContentView(view);
		dialog.show();
	}

	private void showFirstEntryDialog() {
		dialog = new Dialog(this, R.style.MyDialog);
		dialog.setCancelable(false);
		// dialog.setContentView(R.layout.first_entry_dialog);
		View view = View.inflate(this, R.layout.first_entry_dialog, null);
		et_first_entry_pwd = (EditText) view
				.findViewById(R.id.et_first_entry_pwd);
		et_first_entry_confirm = (EditText) view
				.findViewById(R.id.et_first_entry_confirm);
		Button bt_first_dialog_confirm = (Button) view
				.findViewById(R.id.bt_first_dialog_confirm);
		Button bt_first_dialog_cancel = (Button) view
				.findViewById(R.id.bt_first_dialog_cancel);

		bt_first_dialog_confirm.setOnClickListener(this);
		bt_first_dialog_cancel.setOnClickListener(this);

		dialog.setContentView(view);
		dialog.show();
	}

	private boolean isPWDSetup() {
		String password = sp.getString("password", null);
		if (TextUtils.isEmpty(password)) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_first_dialog_confirm:
			String pwd = et_first_entry_pwd.getText().toString().trim();
			String confirm = et_first_entry_confirm.getText().toString().trim();
			if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(confirm)) {
				Toast.makeText(getApplicationContext(), "Password cannot be empty", 0).show();
				return;
			} else {
				if (pwd.equals(confirm)) {
					Editor editor = sp.edit();
					editor.putString("password", MD5Encoder.encode(pwd));
					editor.commit();
				} else {
					Toast.makeText(getApplicationContext(), "Wrong Password", 0).show();
					return;
				}
			}
			dialog.dismiss();
			finish();
			break;
		case R.id.bt_first_dialog_cancel:
			dialog.dismiss();
			finish();
			break;
		case R.id.bt_normal_dialog_cancel:
			dialog.dismiss();
			finish();
			break;
		case R.id.bt_normal_dialog_confirm:
			String password = et_first_entry_pwd.getText().toString().trim();
			if (TextUtils.isEmpty(password)) {
				Toast.makeText(getApplicationContext(), "Please input password", 0).show();
			}else{
				String realpwd = sp.getString("password", "");
				if(realpwd.equals(MD5Encoder.encode(password))){
					
					if(isSetup()){
						Log.i(tag, "加载主界面");
						setContentView(R.layout.lost_protected);
						ll_reentry_setup_guide = (LinearLayout) findViewById(R.id.ll_reentry_setup_guide);
						cb_isprotecting = (CheckBox) findViewById(R.id.cb_isprotecting);
						tv_reentry_setup_guide = (TextView) findViewById(R.id.tv_reentry_setup_guide);
						tv_lost_protected_number = (TextView) findViewById(R.id.tv_lost_protected_number);
						//initial 
						String number = sp.getString("safenumber", "");
						tv_lost_protected_number.setText("Safe Number is：   "+number);
						ll_reentry_setup_guide.setOnClickListener(this);
						boolean isprotecting = sp.getBoolean("isprotecting", false);
						if (isprotecting) {
							cb_isprotecting.setText("SafeGuard is protecting");
							cb_isprotecting.setChecked(true);
						}
						
						cb_isprotecting.setOnCheckedChangeListener(new OnCheckedChangeListener() {
							
							@Override
							public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
								if (isChecked) {
									cb_isprotecting.setText("SafeGuard is protecting");
									Editor editor = sp.edit();
									editor.putBoolean("isprotecting", true);
									editor.commit();
								}else {
									cb_isprotecting.setText("SafeGuard is unused");
									Editor editor = sp.edit();
									editor.putBoolean("isprotecting", false);
									editor.commit();
								}
							}
						});
						
					}else{
						Log.i(tag, "设置向导界面");
						Intent intent = new Intent(LostProtectedActivity.this,SetupGuide1Activity.class);
						startActivity(intent);
						finish();
					}
					
				}else {
					Toast.makeText(getApplicationContext(), "Wrong Password", 0).show();
					return;
				}
			}
			dialog.dismiss();

			break;
		case R.id.ll_reentry_setup_guide:
			finish();
			Intent intent = new Intent(LostProtectedActivity.this,SetupGuide1Activity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.translate_in, R.anim.translate_out);
			break;
		}

	}
	
	private boolean isSetup(){
		return sp.getBoolean("issetupalready", false);
	}
}
