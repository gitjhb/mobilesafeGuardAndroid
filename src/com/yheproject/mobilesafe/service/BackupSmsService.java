package com.yheproject.mobilesafe.service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.xmlpull.v1.XmlSerializer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.util.Xml;
import android.widget.Toast;

import com.yheproject.mobilesafe.domain.SmsInfo;
import com.yheproject.mobilesafe.engine.SmsInfoService;

public class BackupSmsService extends Service {
	private SmsInfoService smsInfoService;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		smsInfoService = new SmsInfoService(this);
		super.onCreate();
		new Thread() {

			@Override
			public void run() {
				try {
					List<SmsInfo> smsinfos = smsInfoService.getSmsInfos();
					File file = new File("/sdcard/smsbackup.xml");
					XmlSerializer serializer = Xml.newSerializer();
					FileOutputStream os = new FileOutputStream(file);
					serializer.setOutput(os, "utf-8");
					serializer.startDocument("utf-8", true);
					serializer.startTag(null, "smss");
					serializer.startTag(null, "count");
					serializer.text(smsinfos.size() + "");
					serializer.endTag(null, "count");
					for (SmsInfo info : smsinfos) {
						serializer.startTag(null, "sms");
						serializer.startTag(null, "id");
						serializer.text(info.getId());
						serializer.endTag(null, "id");

						serializer.startTag(null, "address");
						serializer.text(info.getAddress());
						serializer.endTag(null, "address");

						serializer.startTag(null, "date");
						serializer.text(info.getDate());
						serializer.endTag(null, "date");

						serializer.startTag(null, "type");
						serializer.text(info.getType() + "");
						serializer.endTag(null, "type");

						serializer.startTag(null, "body");
						serializer.text(info.getBody());
						serializer.endTag(null, "body");

						serializer.endTag(null, "sms");
					}
					serializer.endTag(null, "smss");
					serializer.endDocument();
					// 把文件缓冲区的数据写出去
					os.flush();
					os.close();
					Looper.prepare();
					Toast.makeText(getApplicationContext(), "备份完成", 1).show();
					Looper.loop();
				} catch (Exception e) {
					e.printStackTrace();
					Looper.prepare();
					Toast.makeText(getApplicationContext(), "备份失败", 1).show();
					Looper.loop();
				}
			}

		}.start();
	}

}
