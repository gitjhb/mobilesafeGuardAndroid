package com.yheproject.mobilesafe.ui;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.yheproject.mobilesafe.R;

public class DragviewActivity extends Activity implements OnTouchListener {

	private ImageView iv_drag_view;
	private TextView tv_drag_view;
	int startX;
	int startY;
	private SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.drag_view);
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		tv_drag_view = (TextView) findViewById(R.id.tv_drag_view);
		iv_drag_view = (ImageView) findViewById(R.id.iv_drag_view);
		
		iv_drag_view.setOnTouchListener(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		int x = sp.getInt("lastx", 0);
		int y = sp.getInt("lasty", 0);
		//iv_drag_view.layout(iv_drag_view.getLeft()+x, iv_drag_view.getTop()+y, iv_drag_view.getRight()+x, iv_drag_view.getBottom()+y);
		//iv_drag_view.invalidate();
		LayoutParams layoutParams = (LayoutParams) iv_drag_view.getLayoutParams();
		layoutParams.leftMargin = x;
		layoutParams.topMargin = y;
		iv_drag_view.setLayoutParams(layoutParams);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.iv_drag_view:
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				startX = (int) event.getRawX();
				startY = (int) event.getRawY();
				break;
			case MotionEvent.ACTION_MOVE:
				
				WindowManager manager = (WindowManager) DragviewActivity.this.getSystemService(Context.WINDOW_SERVICE);
				Display display= manager.getDefaultDisplay();
				int height= display.getHeight();
				int width= display.getWidth();
				int x = (int) event.getRawX();
				int y = (int) event.getRawY();
				if(height>800 || width> 480){
					if (y<900) {
						tv_drag_view.layout(tv_drag_view.getLeft(), 1400, tv_drag_view.getRight(), 1640);
					}else {
						tv_drag_view.layout(tv_drag_view.getLeft(), 120, tv_drag_view.getRight(), 360);
					}
				}
				else {
					if (y<400) {
						tv_drag_view.layout(tv_drag_view.getLeft(), 500, tv_drag_view.getRight(), 600);
					}else {
						tv_drag_view.layout(tv_drag_view.getLeft(), 120, tv_drag_view.getRight(), 220);
					}
					
				}
				
				// 获取手指移动的距离
				int dx = x - startX;
				int dy = y - startY;
				int l = iv_drag_view.getLeft();
				int r = iv_drag_view.getRight();
				int t = iv_drag_view.getTop();
				int b = iv_drag_view.getBottom();

				iv_drag_view.layout(l + dx, t + dy, r + dx, b + dy);

				// 获取到移动后的位置
				startX = (int) event.getRawX();
				startY = (int) event.getRawY();
				break;
			case MotionEvent.ACTION_UP:
				int lasty = iv_drag_view.getTop();
				int lastx = iv_drag_view.getLeft();
				Editor editor = sp.edit();
				editor.putInt("lastx", lastx);
				editor.putInt("lasty", lasty);
				editor.commit();
				break;
			}

			break;
		}
		return true;//不中断触摸事件
	}
}
