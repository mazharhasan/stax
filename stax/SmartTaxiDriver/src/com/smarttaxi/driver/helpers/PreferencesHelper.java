package com.smarttaxi.driver.helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesHelper {

	public static final String USER_PREFERENCES = "UserPreferences";
	
	public static final String KEY_EMAIL = "Email";
	public static final String KEY_USER_VERIFIED = "UserVerified";

	public static String getUserEmailAddress(Context context) {
		SharedPreferences userPreferences = context.getSharedPreferences(
				USER_PREFERENCES, 0);
		return userPreferences.getString(KEY_EMAIL, "");
	}

	public static void setUserEmailAddress(String emailAddress, Context context) {
		SharedPreferences userPreferences = context.getSharedPreferences(
				USER_PREFERENCES, 0);
		SharedPreferences.Editor editor = userPreferences.edit();
		editor.putString(KEY_EMAIL, emailAddress);
		editor.commit();
	}
	
	public static boolean isUserVerified(Context context) {
		SharedPreferences userPreferences = context.getSharedPreferences(
				USER_PREFERENCES, 0);
		return userPreferences.getBoolean(KEY_USER_VERIFIED, false);
	}
	
	public static void setUserVerified(boolean isVerified, Context context) {
		SharedPreferences userPreferences = context.getSharedPreferences(
				USER_PREFERENCES, 0);
		SharedPreferences.Editor editor = userPreferences.edit();
		editor.putBoolean(KEY_USER_VERIFIED, isVerified);
		editor.commit();
	}

}
