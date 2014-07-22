package com.smarttaxi.driver.preferences;

import com.smarttaxi.driver.utils.Applog;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;


public class AppPreferences {
	private static SharedPreferences preferences;
	private String fileName;
	
	public AppPreferences(Context context, String fileName) {
		this.fileName = fileName;
		preferences = context.getSharedPreferences(fileName, Application.MODE_PRIVATE); 
	}


	public void putValue(String key, String value) {
		preferences.edit().putString(key, value).commit(); 
		
	}
	
	public void removeValue(String key) {
		
		if(preferences.contains(key))
			preferences.edit().remove(key).commit();
		
		
	}
	
	public void putValue(String key, int value) {
		preferences.edit().putInt(key, value).commit(); 
	}
	public void putValue(String key, Boolean value) {
		preferences.edit().putBoolean(key, value).commit(); 
	}
	
	public String getString(String key, String defaultValue) {
		try {
			return preferences.getString(key, defaultValue);
		} catch (Exception e) {
			Applog.Error("Couldn't retrieve Preference Key "+e.getMessage());
			return "";
		}
	}
	public String getString(String key) {
		return getString(key, "");
	}
	
	public int getInt(String key, int defaultValue) {
		try {
			return preferences.getInt(key, defaultValue);
		} catch (Exception e) {
			Applog.Error("Couldn't retrieve Preference Key " +e.getMessage());
			return 0;
		}
	}
	public int getInt(String key) {
		return getInt(key, 0);
	}
	
	public Boolean getBoolean(String key, Boolean defaultValue) {
		try {
			return preferences.getBoolean(key, defaultValue);
		} catch (Exception e) {
			Applog.Error("Couldn't retrieve Preference Key " +e.getMessage());
			return false;
		}
	}
	public Boolean getBoolean(String key) {
		return getBoolean(key, false);
	}
	
	public void unset(String key) {
		try {
			preferences.edit().remove(key).commit();
		} catch (Exception e) {
			Applog.Error(e.getMessage());
		}
	}
	
	public void clearAll() {
		preferences.edit().clear().commit();
	}
}