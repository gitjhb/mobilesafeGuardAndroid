package com.yheproject.mobilesafe.ui;

import com.yheproject.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SetupGuide1Activity extends Activity implements OnClickListener {
	
	private Button bt_next;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setupguide1);
		bt_next = (Button) findViewById(R.id.bt_next);
		bt_next.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_next:
			Intent intent = new Intent(this, SetupGuide2Activity.class);
			finish();
			startActivity(intent);
			overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
			break;
		}
		
	}
}
