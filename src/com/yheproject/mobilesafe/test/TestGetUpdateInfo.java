package com.yheproject.mobilesafe.test;

import com.yheproject.mobilesafe.R;
import com.yheproject.mobilesafe.domain.UpdateInfo;
import com.yheproject.mobilesafe.engine.UpdateInfoService;

import android.test.AndroidTestCase;

public class TestGetUpdateInfo extends AndroidTestCase{

	
	public void testGetInfo() throws Exception{
		UpdateInfoService service = new UpdateInfoService(getContext());
		UpdateInfo info = service.getUpdateInfo(R.string.updateurl);
		System.out.println(info.getVersion());
		assertEquals("2.0", info.getVersion());
		
	}
	
}
