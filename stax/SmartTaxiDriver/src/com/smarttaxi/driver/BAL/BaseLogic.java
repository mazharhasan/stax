package com.smarttaxi.driver.BAL;

import com.smarttaxi.driver.database.SqliteDB;
import com.smarttaxi.driver.database.SqliteHelper;

import android.content.ContentValues;
import android.content.Context;

public class BaseLogic {

	private Context context;
	public BaseLogic(Context context){
		this.context= context;
		
	}
	public void saveJson(String key, String value){
		ContentValues cv = new ContentValues();
		cv.put("json_key",key);
		cv.put("json", value);
		SqliteHelper sqliteHelper = new SqliteHelper(context);
		sqliteHelper.saveJson(cv);
		
	}
}
