package com.yheproject.mobilesafe.db.dao;

import android.database.sqlite.SQLiteDatabase;

public class AddressDao {

	public static SQLiteDatabase getAddressDB(String path) {
		return SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
	}
}
