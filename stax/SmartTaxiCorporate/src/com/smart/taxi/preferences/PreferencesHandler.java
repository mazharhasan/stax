package com.smart.taxi.preferences;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.smart.taxi.activities.SplashActivity;
import com.smart.taxi.entities.TripDetails;
import com.smart.taxi.entities.User;
import com.smart.taxi.utils.Utils;

public class PreferencesHandler extends AppPreferences {
	private final static String PREFERENCES_FILE_NAME = "smart_taxi_pref";

	public PreferencesHandler(Context context) {
		super(context, PREFERENCES_FILE_NAME);
	}

	public Boolean isUserLogedIn() {
		return !getString(PrefKeys.USER_SIGNIN).equalsIgnoreCase("false");
	}

	public long getUserID() {
		try {
			String u = getString(PrefKeys.USER_ID);
			Log.e("AppPreference", "pref : getUserID : user id " + u);
			u = u.equalsIgnoreCase("") ? "0" : u;
			return Long.parseLong(u);
		} catch (Exception e) {
			return 0;
		}
	}
	
	public void setRegId(String regId)
	{
		if(SplashActivity.isLoggedIn() && String.valueOf(getUserID()).equals(SplashActivity.loggedInUser.getId()))
		{
			Map<String, String> o = new HashMap<String, String>();
			o.put("userId", SplashActivity.loggedInUser.getId());
			o.put("key",regId);
			putValue(PrefKeys.REG_ID, new Gson().toJson(o, HashMap.class));
		}
	}
	
	public String getRegId()
	{
		return getString(PrefKeys.REG_ID);
	}
	
	public User getLastUserObject() throws NullPointerException
	{
		if(isUserLogedIn() && getUserID() > 0)
		{
			String userJson = getString(PrefKeys.JSON_USER_DUMP);
			if(!Utils.isEmptyOrNull(userJson))
			{
				return new Gson().fromJson(userJson, User.class);
			}
		}
		throw new NullPointerException("No save user found");
	}
	
	public String getTip()
	{
		return getString(PrefKeys.TIP);
	}
	
	public void setTip(String tip)
	{
		putValue(PrefKeys.TIP, tip);
	}
	
	public void setUserLoggedIn() {
		if(SplashActivity.isLoggedIn())
		{
			Gson gson = new Gson();
			User user = SplashActivity.loggedInUser;
			String result = gson.toJson(user, User.class);
			putValue(PrefKeys.JSON_USER_DUMP, result);
			putValue(PrefKeys.USER_NAME, user.getFullName());
			putValue(PrefKeys.USER_SIGNIN, "true");
			putValue(PrefKeys.USER_ID, user.getId());
			putValue(PrefKeys.TIP, user.getTip());
			putValue(PrefKeys.IS_CORPORATE, user.isCorporateUser() ? "true" : "false");
			if(user.isCorporateUser())
			{
				putValue(PrefKeys.CORPORATE_ID, user.getCoporateID());
				putValue(PrefKeys.CORPORATE_NAME, user.getCorporateInfo().getName());
			}
		}
	}

	public void logout() {
		putValue(PrefKeys.USER_SIGNIN, "false");
		putValue(PrefKeys.REG_ID, "");
		putValue(PrefKeys.JSON_USER_DUMP, "");
		putValue(PrefKeys.USER_NAME, "");
		putValue(PrefKeys.USER_ID, "");
		putValue(PrefKeys.IS_CORPORATE, "");
		putValue(PrefKeys.CORPORATE_ID, "");
		putValue(PrefKeys.CORPORATE_NAME, "");
		putValue(PrefKeys.TIP, "0");
		putValue(PrefKeys.CURRENT_TRIP_ID, "");
		putValue(PrefKeys.JSON_CURRENT_TRIP, "");
	}

	public void saveCurrentTrip(TripDetails tripNewDetails) {
		Gson gson = new Gson();
		String result = gson.toJson(tripNewDetails, TripDetails.class);
		putValue(PrefKeys.JSON_CURRENT_TRIP, result);
		putValue(PrefKeys.CURRENT_TRIP_ID, tripNewDetails.getJourneyId());
		
	}
	
	public TripDetails getCurrentTrip()
	{
		String tripJson = (getString(PrefKeys.JSON_CURRENT_TRIP) != null)?getString(PrefKeys.JSON_CURRENT_TRIP):"";
		if(!Utils.isEmptyOrNull(tripJson))
		{
			Gson gson = new Gson();
			TripDetails result = gson.fromJson(tripJson, TripDetails.class);
			return result;
		}else{
			return null;
		}
	}
	
	public String getCurrentTripId()
	{
		try{
		return getString(PrefKeys.CURRENT_TRIP_ID);
		}catch(Exception ex)
		{
			return "";
		}
	}
	
	public void clearTrip()
	{
		putValue(PrefKeys.JSON_CURRENT_TRIP, "");
		putValue(PrefKeys.CURRENT_TRIP_ID, "");
	}
}
