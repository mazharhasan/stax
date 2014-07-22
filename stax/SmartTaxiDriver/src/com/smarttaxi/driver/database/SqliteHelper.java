package com.smarttaxi.driver.database;

import com.smarttaxi.driver.entities.User;
import com.smarttaxi.driver.utils.Applog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SqliteHelper {
	SqliteDB sqliteDB;
	SQLiteDatabase sqliteDatabase;
	private final String USERS_TABLE = "users";
	private final String JSON_TABLE = "json";

	public SqliteHelper(Context context) {
		sqliteDB = new SqliteDB(context);

	}

	public void saveUser(ContentValues cv, long userId){
		try {
			
			User user = getUser(userId);
			OpenDatabase();
			if(user == null)
				sqliteDatabase.insert(USERS_TABLE, null, cv);
			else
				sqliteDatabase.update(USERS_TABLE, cv, "id=?", new String[]{String.valueOf(userId)});
			closeDatabase();
			} catch (Exception e) {
				// TODO: handle exception
			}
		OpenDatabase();
		
		closeDatabase();
		
	}

	private void closeDatabase() {
		if (sqliteDatabase == null)
			return;
		if (sqliteDatabase.isOpen())
			sqliteDatabase.close();
	}

	private void OpenDatabase() {
		// if(sqliteDatabase == null) return;
		if (sqliteDatabase == null || sqliteDatabase.isOpen() == false)
			sqliteDatabase = sqliteDB.getWritableDatabase();
	}

	public void saveJson(ContentValues cv) {
		OpenDatabase();
		sqliteDatabase.insert(JSON_TABLE, null, cv);
		closeDatabase();
	}

	public User getUser(long userID) {
		OpenDatabase();
		Cursor cursor = sqliteDatabase
				.rawQuery(
						"SELECT first_name, last_name, email, username, id, driver_id, cab_provider_name, cab_provider_id, cab_id, age, gender, contact_number, license_code, user_image FROM users WHERE id = "
								+ userID, null);
		if (cursor == null || cursor.getCount() <= 0) {
			cursor.close();
			closeDatabase();
			return null;
		}
		Applog.Debug("Users Found with Same ID "+ cursor.getCount() );
		cursor.moveToFirst();
		User user = new User();
		user.setUserName(cursor.getString(cursor.getColumnIndex("username")));
		user.setEmail(cursor.getString(cursor.getColumnIndex("email")));
		user.setAge(cursor.getString(cursor.getColumnIndex("age")));
		user.setId(cursor.getString(cursor.getColumnIndex("id")));
		user.setDriverID(cursor.getString(cursor.getColumnIndex("driver_id")));
		user.setCabProviderID(cursor.getString(cursor
				.getColumnIndex("cab_provider_id")));
		user.setCabID(cursor.getString(cursor
				.getColumnIndex("cab_id")));
		user.setFirstName(cursor.getString(cursor.getColumnIndex("first_name")));
		user.setLastName(cursor.getString(cursor.getColumnIndex("last_name")));
		user.setGender(cursor.getString(cursor.getColumnIndex("gender")));
		user.setUserImage(cursor.getString(cursor.getColumnIndex("user_image")));
		user.setContactNumber(cursor.getString(cursor
				.getColumnIndex("contact_number")));
		user.setLicenseCode(cursor.getString(cursor
				.getColumnIndex("license_code")));
		user.setCabProvider(cursor.getString(cursor
				.getColumnIndex("cab_provider_name")));
		cursor.close();
		closeDatabase();
		return user;
	}

	public void updateDriverInfo(String cabId, long userID) {
		ContentValues cv = new ContentValues();
		cv.put("cab_id", cabId);
		OpenDatabase();
		sqliteDatabase.update(USERS_TABLE, cv, "id=?", new String[]{String.valueOf(userID)});
		closeDatabase();
	}

}
