package com.yheproject.mobilesafe.ui;

import java.util.List;

import com.yheproject.mobilesafe.R;
import com.yheproject.mobilesafe.domain.ContactInfo;
import com.yheproject.mobilesafe.engine.ContactInfoService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SelectContactActivity extends Activity {
	
	private ListView lv;
	private List<ContactInfo> infos;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_contact);
		
		ContactInfoService contactInfoService = new ContactInfoService(this);
		infos = contactInfoService.getContactInfos();
		lv = (ListView) findViewById(R.id.lv_select_contact);
		lv.setAdapter(new SelectContactAdapter());
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String phone = infos.get(position).getPhone();
				Intent intent = new Intent();
				intent.putExtra("number", phone);
				setResult(0,intent);
				finish();
			}
		});
	}
	private class SelectContactAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return infos.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return infos.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ContactInfo info = infos.get(position);
			LinearLayout ll = new LinearLayout(SelectContactActivity.this);
			ll.setOrientation(LinearLayout.VERTICAL);
			TextView tv_name = new TextView(SelectContactActivity.this);
			tv_name.setText("name  :  "+info.getName());
			TextView tv_number = new TextView(SelectContactActivity.this);
			tv_number.setText("phone  :  "+info.getPhone());
			ll.addView(tv_name);
			ll.addView(tv_number);
			return ll;
		}
		
	}
}
