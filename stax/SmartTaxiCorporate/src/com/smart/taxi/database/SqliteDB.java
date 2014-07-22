package com.smart.taxi.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteDB extends SQLiteOpenHelper {

	// Database Version
	private static final int DATABASE_VERSION = 1;
	// Database Name
	private static final String DATABASE_NAME = "BookDB";

	public SqliteDB(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_USER_TABLE = "CREATE TABLE users (_id INTEGER PRIMARY KEY AUTOINCREMENT, id TEXT, driver_id TEXT, first_name TEXT, last_name TEXT, username TEXT, email TEXT, gender TEXT, phone TEXT, user_image TEXT, created"
				+ "TEXT, group_id TEXT, coporate_id TEXT, driver_name TEXT, age TEXT, contact_number TEXT, license_code TEXT, cab_id TEXT, pob_status TEXT, post_check_in TEXT,"
				+ "cab_provider_id TEXT, cab_provider_name TEXT, driver_rating TEXT)";
		String CREATE_JSON_TABLE = "CREATE TABLE json (_id INTEGER PRIMARY KEY AUTOINCREMENT, json_key TEXT, json TEXT)";
		db.execSQL(CREATE_USER_TABLE);
		db.execSQL(CREATE_JSON_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		db.execSQL("DROP TABLE IF EXISTS users");
		db.execSQL("DROP TABLE IF EXISTS json");

		this.onCreate(db);
	}


}
