package com.yheproject.mobilesafe.ui;

import com.yheproject.mobilesafe.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SetupGuide2Activity extends Activity implements OnClickListener {

	private Button bt_bind;
	private Button bt_previous;
	private Button bt_next;
	private CheckBox cb_bind;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setupguide2);
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		bt_bind = (Button) findViewById(R.id.bt_bind);
		bt_next = (Button) findViewById(R.id.bt_next);
		bt_previous = (Button) findViewById(R.id.bt_previous);
		cb_bind = (CheckBox) findViewById(R.id.cb_bind);

		bt_bind.setOnClickListener(this);
		bt_next.setOnClickListener(this);
		bt_previous.setOnClickListener(this);
		String sim = sp.getString("sim", null);
		if(TextUtils.isEmpty(sim)){
			cb_bind.setText("Sim Card Unbounded");
			cb_bind.setChecked(false);
			resetSimInfo();
		}else {
			cb_bind.setText("Sim Card Bounded");
			cb_bind.setChecked(true);
		}

		cb_bind.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if(isChecked){
					cb_bind.setText("Sim Card Bounded");
					setSimInfo();
				}else{
					cb_bind.setText("Sim Card Unbounded");
					resetSimInfo();
				}

			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_bind:
			setSimInfo();
			cb_bind.setText("Sim Card Bounded");
			cb_bind.setChecked(true);
			break;
		case R.id.bt_next:
			Intent intent3 = new Intent(this, SetupGuide3Activity.class);
			finish();
			startActivity(intent3);
			overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
			break;
		case R.id.bt_previous:
			Intent intent1 = new Intent(this, SetupGuide1Activity.class);
			finish();
			startActivity(intent1);
			overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
			break;

		}
	}

	private void setSimInfo() {
		TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		String simserial = manager.getSimSerialNumber();
		Editor editor = sp.edit();
		editor.putString("sim", simserial);
		editor.commit();
	}
	private void resetSimInfo(){
		Editor editor = sp.edit();
		editor.putString("sim", null);
		editor.commit();
	}
}
