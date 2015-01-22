package com.yheproject.mobilesafe.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

public class ImageUtil {

	/**
	 * 返回一个宽度和高度都为48个像素的bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap getResizedBitmap(BitmapDrawable drawable,
			Context context) {
		// drawable.
		// BitmapFactory.d
		Bitmap bitmap = drawable.getBitmap();
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		int height = display.getHeight();
		int width = display.getWidth();
		if (height < 480 || width < 320) {
			return Bitmap.createScaledBitmap(bitmap, 32, 32, false);
		} else if (height > 600 || width > 480) {
			return Bitmap.createScaledBitmap(bitmap, 75, 75, false);
		}else{
			return Bitmap.createScaledBitmap(bitmap, 48, 48, false);
		}
	}
}
