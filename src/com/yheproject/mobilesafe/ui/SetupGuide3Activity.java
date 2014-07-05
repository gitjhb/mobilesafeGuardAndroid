package com.yheproject.mobilesafe.ui;

import com.yheproject.mobilesafe.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SetupGuide3Activity extends Activity implements OnClickListener {

	private Button bt_select_contact;
	private Button bt_previous;
	private Button bt_next;
	private EditText et_setup3_number;
	private SharedPreferences sp;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(data!=null){
			String number = data.getStringExtra("number");
			et_setup3_number.setText(number);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setupguide3);
		et_setup3_number = (EditText) findViewById(R.id.et_setup3_number);
		bt_next = (Button) findViewById(R.id.bt_next);
		bt_previous = (Button) findViewById(R.id.bt_previous);
		bt_select_contact = (Button) findViewById(R.id.bt_select_contact);
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		
		bt_select_contact.setOnClickListener(this);
		bt_next.setOnClickListener(this);
		bt_previous.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_next:
			String number = et_setup3_number.getText().toString().trim();
			if (TextUtils.isEmpty(number)) {
				Toast.makeText(this, "Safe Number cannot be empty", 0).show();
				return;
			}else {
				Editor editor = sp.edit();
				editor.putString("safenumber", number);
				editor.commit();
			}
			Intent intent4 = new Intent(this, SetupGuide4Activity.class);
			finish();
			startActivity(intent4);
			overridePendingTransition(R.anim.translate_in, R.anim.translate_out);
			break;
		case R.id.bt_previous:
			Intent intent2 = new Intent(this, SetupGuide2Activity.class);
			finish();
			startActivity(intent2);
			overridePendingTransition(R.anim.translate_in, R.anim.translate_out);
			break;
		case R.id.bt_select_contact:
			Intent intent = new Intent(this,SelectContactActivity.class);
			startActivityForResult(intent, 0);
			break;
		}

	}
}
