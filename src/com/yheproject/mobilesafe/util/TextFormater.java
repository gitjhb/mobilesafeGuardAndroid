package com.yheproject.mobilesafe.util;

import java.text.DecimalFormat;

import android.util.Log;

public class TextFormater {

	private static final String tag = "TextFormater";

	/**
	 * ����byte�����ݴ�С��Ӧ���ı�
	 * 
	 * @param size
	 * @return
	 */
	public static String getDataSize(long size) {
		if (size < 0)
			size = 0;
		DecimalFormat formater = new DecimalFormat("####.00");
		Log.i(tag, "size:" + size);
		if (size < 1024) {
			return size + "bytes";
		} else if (size < 1024 * 1024) {
			float kbsize = size / 1024f;
			return formater.format(kbsize) + "KB";
		} else if (size < 1024 * 1024 * 1024) {
			float mbsize = size / 1024f / 1024f;
			return formater.format(mbsize) + "MB";
		} else if (size > 1024 * 1024 * 1024) {
			float gbsize = size / 1024f / 1024f / 1024f;
			return formater.format(gbsize) + "GB";
		} else {
			return "size: error";
		}

	}

	/**
	 * ����kb�����ݴ�С��Ӧ���ı�
	 * 
	 * @param size
	 * @return
	 */
	public static String getKBDataSize(long size) {
		if (size < 0)
			size = 0;
		return getDataSize(size * 1024);

	}
}
