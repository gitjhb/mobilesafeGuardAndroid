package com.yheproject.mobilesafe.adapter;

import com.yheproject.mobilesafe.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainUIAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private static ImageView iv_icon;
	private static TextView tv_name;
	private SharedPreferences sp;
	

	public MainUIAdapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
	}

	private static String[] names = { "Safe Guard", "Comm Guard", "App Manager", "Task Manager", "Traffic Manager",
			"Antivirus", "System Opt", "Advanced Tools", "Setting center" };
	private static int[] icons = { R.drawable.main_item1, R.drawable.main_item2,
			R.drawable.main_item3, R.drawable.main_item4, R.drawable.main_item5,
			R.drawable.main_item6, R.drawable.main_item7, R.drawable.main_item8,
			R.drawable.main_item9 };

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return names.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = inflater.inflate(R.layout.mainscreen_item, null);
		iv_icon = (ImageView) view.findViewById(R.id.iv_main_icon);
		tv_name = (TextView) view.findViewById(R.id.tv_main_name);
		iv_icon.setImageResource(icons[position]);
		tv_name.setText(names[position]);
		if(position==0){
			String name = sp.getString("lost_name", null);
			if(name!=null){
				tv_name.setText(name);
			}
		}
		
		
		return view;
	}

}
