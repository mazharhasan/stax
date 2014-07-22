package com.smarttaxi.driver.preferences;

import com.smarttaxi.driver.utils.Applog;

import android.content.Context;

public class PreferencesHandler extends AppPreferences {
	private final static String PREFERENCES_FILE_NAME = "smart_driver_pref";

	public PreferencesHandler(Context context) {
		super(context, PREFERENCES_FILE_NAME);
	}

	public Boolean isUserLogin() {
		return !getString(PrefKeys.USER_SIGNIN).equalsIgnoreCase("");
	}

	public long getUserID() {
		try {
			String u = getString(PrefKeys.USER_SIGNIN);
			Applog.Debug("pref : getUserID : user id " + u);
			u = u.equalsIgnoreCase("") ? "0" : u;
			return Long.parseLong(u);
		} catch (Exception e) {
			return 0;
		}
	}
	
	public void setDriverUdid(String udid) {

		  putValue(PrefKeys.DRIVER_UDID, udid);

		 }
		 
		 public String getDriverUdid() {
		  try {
		   String udid = getString(PrefKeys.DRIVER_UDID);
		   udid = udid.equalsIgnoreCase("") ? "" : udid;
		   return udid;
		  } catch (Exception e) {
		   return "";
		  }
		 }
		

	// public long getDriverInfoID() {
	// try {
	// String u = getString(PrefKeys.DRIVER_USER_ID);
	// Applog.Debug("pref : getDriverId : user id " + u);
	// u = u.equalsIgnoreCase("") ? "0" : u;
	// return Long.parseLong(u);
	// } catch (Exception e) {
	// return 0;
	// }
	// }

	public long getOriginalDriverUserID() {
		try {
			String u = getString(PrefKeys.USER_ID);
			Applog.Debug("pref : getOriginalDriverUserID : user id " + u);
			u = u.equalsIgnoreCase("") ? "0" : u;
			return Long.parseLong(u);
		} catch (Exception e) {
			return 0;
		}
	}
	
	public int getSavedJourneyId() {
		try {
			String u = getString(PrefKeys.STORED_JOURNEY_ID);
			Applog.Debug("pref : getSavedJourneyId : saved journey id " + u);
			u = u.equalsIgnoreCase("") ? "0" : u;
			return Integer.parseInt(u);
		} catch (Exception e) {
			return 0;
		}
	}

	public String getUserName() {
		String userName = "";
		try {
			userName = getString(PrefKeys.USER_NAME, "");
			userName = userName.equalsIgnoreCase("") ? "" : userName;
			return userName;
		} catch (Exception e) {
			return userName;
		}
	}

	public String getDriverCabAvailablityStatus() {
		String driverCabAvailablityStatus = "";
		try {
			driverCabAvailablityStatus = getString(
					PrefKeys.CAB_AVAILABLITY_STATUS, "");
			driverCabAvailablityStatus = driverCabAvailablityStatus.equalsIgnoreCase("") ? "" : driverCabAvailablityStatus;
			return driverCabAvailablityStatus;
		} catch (Exception e) {
			return driverCabAvailablityStatus;
		}
	}

	public String getDriverPostCheckInStatus() {
		String driverPostCheckInStatus = "";
		try {
			driverPostCheckInStatus = getString(PrefKeys.POST_CHECK_IN_STATUS,
					"");
			driverPostCheckInStatus = driverPostCheckInStatus
					.equalsIgnoreCase("") ? "" : driverPostCheckInStatus;
			return driverPostCheckInStatus;
		} catch (Exception e) {
			return driverPostCheckInStatus;
		}
	}

	public String getDriverPassengerOnBoardStatus() {
		String driverPassengerOnBoardStatus = "";
		try {
			driverPassengerOnBoardStatus = getString(
					PrefKeys.PASSENGER_ON_BOARD_STATUS, "");
			driverPassengerOnBoardStatus = driverPassengerOnBoardStatus
					.equalsIgnoreCase("") ? "" : driverPassengerOnBoardStatus;
			return driverPassengerOnBoardStatus;
		} catch (Exception e) {
			return driverPassengerOnBoardStatus;
		}
	}

	public int isUserConfigured() {
		return getInt(PrefKeys.USER_CONFIGURED);
	}

	public void setUserLoggedIn(String userId, String driverUserId,
			String userName, String postCheckIn, String passengerOnBoard,
			String isCabAvailable, Boolean isAuthenticated) {

		putValue(PrefKeys.USER_NAME, isAuthenticated ? userName : "");
		putValue(PrefKeys.USER_SIGNIN, isAuthenticated ? userId : "");
		putValue(PrefKeys.USER_ID, isAuthenticated ? driverUserId : "");
		putValue(PrefKeys.DRIVER_USER_ID, isAuthenticated ? userId : "");
		putValue(PrefKeys.POST_CHECK_IN_STATUS, isAuthenticated ? postCheckIn
				: "");
		putValue(PrefKeys.PASSENGER_ON_BOARD_STATUS,
				isAuthenticated ? passengerOnBoard : "");
		putValue(PrefKeys.CAB_AVAILABLITY_STATUS,
				isAuthenticated ? isCabAvailable : "");

	}

	public void setCabID(String cabId) {

		putValue(PrefKeys.CAB_ID, cabId);

	}
	
	public String getCabID()
	{
		return getString(PrefKeys.CAB_ID);
	}
	
	public void setEmail(String email)
	{
		putValue("Email", email);
	}
	
	public String getEmail()
	{
		return getString("Email");
	}
	
	public void saveJourney(String journey) {

		putValue(PrefKeys.STORED_JOURNEY_ID, journey);

	}
	
	public void removeJourney() {

		removeValue(PrefKeys.STORED_JOURNEY_ID);
	}
	
	
	
	

	public void logout() {
		putValue(PrefKeys.USER_SIGNIN, "");
		putValue(PrefKeys.USER_NAME, "");
		putValue(PrefKeys.CAB_ID, "");
		putValue("Email", "");

	}
}
