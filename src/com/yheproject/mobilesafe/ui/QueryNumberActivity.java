package com.yheproject.mobilesafe.ui;

import com.yheproject.mobilesafe.R;
import com.yheproject.mobilesafe.engine.NumberAddressService;
import com.yheproject.mobilesafe.service.AddressService;
import com.yheproject.mobilesafe.util.Constants;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class QueryNumberActivity extends Activity {

	private TextView tv_query_number_address;
	private EditText et_query_number;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.query_number);
		tv_query_number_address = (TextView) findViewById(R.id.tv_query_number_address);
		et_query_number = (EditText) findViewById(R.id.et_query_number);
		
	}

	public void query(View view) {
		String number = et_query_number.getText().toString().trim();
		if (TextUtils.isEmpty(number)) {
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			et_query_number.startAnimation(shake);
		}else {
			String address = NumberAddressService.getAddress(number);
			String textString = "Location is£º"+address;
			tv_query_number_address.setText(textString);
			Intent intent = new Intent(this,AddressService.class);
			intent.putExtra("action", Constants.SHOW_ADDRESS);
			intent.putExtra("textString", address);
			startService(intent);
		}
	}
}
