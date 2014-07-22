package com.smarttaxi.driver.BAL;

import java.util.ArrayList;
import java.util.Map.Entry;

import android.content.ContentValues;
import android.content.Context;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.smarttaxi.driver.constants.APIConstants;
import com.smarttaxi.driver.database.SqliteHelper;
import com.smarttaxi.driver.entities.Cab;
import com.smarttaxi.driver.entities.Journey;
import com.smarttaxi.driver.entities.User;
import com.smarttaxi.driver.helpers.JsonHelper;
import com.smarttaxi.driver.interfaces.HttpResponseListener;
import com.smarttaxi.driver.model.WebServiceModel;
import com.smarttaxi.driver.preferences.PrefKeys;
import com.smarttaxi.driver.preferences.PreferencesHandler;
import com.smarttaxi.driver.utils.Applog;
import com.smarttaxi.driver.utils.Utils;

public class Driver {
	static PreferencesHandler prefernecesHandler;
	private static Context context;

	public Driver(Context context) {
		prefernecesHandler = new PreferencesHandler(context);
		Driver.context = context;
	}

	/*
	 * public void Save(User user, Boolean isAuthenticated) { if (user == null)
	 * return;
	 * 
	 * Applog.Debug("Driver Save: getId " + user.getDriverUserId());
	 * Applog.Debug("Driver Save: getCabID " + user.getDriverCabId());
	 * 
	 * prefernecesHandler.setUserLoggedIn(user.getDriverUserId(),
	 * user.getDriverFirstName(), isAuthenticated);
	 * prefernecesHandler.setCabID(user.getDriverCabId()); // TODO Save
	 * Information in DB
	 * 
	 * }
	 * 
	 * public void UpdateLocation(String userId, String cabId, String lat,
	 * String lng) {
	 * 
	 * }
	 * 
	 * public void UpdateLocationFromService(double latitude, double longitude,
	 * HttpResponseListener responseListener) { Applog.Debug("2Latitude" +
	 * latitude); Applog.Debug("2Longitude" + longitude); long userId =
	 * prefernecesHandler.getUserID();
	 * 
	 * // User is loggedIn if (userId > 0) { String cabId =
	 * prefernecesHandler.getString(PrefKeys.CAB_ID);
	 * WebServiceModel.updateDriverLocation(String.valueOf(userId), cabId,
	 * String.valueOf(latitude), String.valueOf(longitude), responseListener); }
	 * }
	 */
	public void resetEntries(String pob, String checkin, String carAvailability) {
		if (!Utils.isEmptyOrNull(pob))
			prefernecesHandler
					.putValue(PrefKeys.PASSENGER_ON_BOARD_STATUS, pob);
		if (!Utils.isEmptyOrNull(checkin))
			prefernecesHandler.putValue(PrefKeys.POST_CHECK_IN_STATUS, checkin);

		if (!Utils.isEmptyOrNull(carAvailability))
			prefernecesHandler.putValue(PrefKeys.CAB_AVAILABLITY_STATUS,
					carAvailability);

	}

	public void enablePobStatus(String pob) {
		if (!Utils.isEmptyOrNull(pob))
			prefernecesHandler
					.putValue(PrefKeys.PASSENGER_ON_BOARD_STATUS, pob);

	}

	public void resetTaxiStatus(String taxiAvailability) {
		if (!Utils.isEmptyOrNull(taxiAvailability))
			prefernecesHandler.putValue(PrefKeys.CAB_AVAILABLITY_STATUS,
					taxiAvailability);

	}

	/****** SALIK *****/
	public void Save(User user, Boolean isAuthenticated) {
		if (user == null)
			return;

		Applog.Debug("Driver Save: getId " + user.getId());
		Applog.Debug("Driver Save: getCabID " + user.getCabID());
		Applog.Debug("Driver Save: username " + user.getUserName());
		Applog.Debug("Driver Save: driverUserId " + user.getDriverID());

		prefernecesHandler.setUserLoggedIn(user.getId(), user.getDriverID(),
				user.getUserName(), user.getPostCheckIn(), user.getPobStatus(),
				"yes", isAuthenticated);

		prefernecesHandler.setCabID(user.getCabID());
		// TODO Save Information in DB
		ContentValues cv = new ContentValues();

		cv.put("email", user.getEmail());

		cv.put("id", user.getId());
		cv.put("driver_id", user.getDriverID());
		cv.put("license_code", user.getLicenseCode());

		cv.put("age", user.getAge());
		cv.put("gender", user.getGender());
		cv.put("contact_number", user.getContactNumber());

		cv.put("username", user.getUserName());
		cv.put("pob_status", user.getPobStatus());
		cv.put("post_check_in", user.getPostCheckIn());

		cv.put("user_image", user.getUserImage());
		cv.put("cab_id", user.getCabID());
		cv.put("cab_provider_id", user.getCabProviderID());
		cv.put("cab_provider_name", user.getCabProvider());

		cv.put("first_name", user.getFirstName());
		cv.put("last_name", user.getLastName());
		cv.put("driver_rating", user.getDriverRating());

		SqliteHelper sqliteHelper = new SqliteHelper(context);
		sqliteHelper.saveUser(cv, Long.parseLong(user.getId()));
	}

	public void UpdateLocation(String userId, String cabId, String lat,
			String lng) {

	}

	public void UpdateLocationFromService(double latitude, double longitude,
			HttpResponseListener responseListener) {
		Applog.Debug("2Latitude" + latitude);
		Applog.Debug("2Longitude" + longitude);
		long userId = prefernecesHandler.getUserID();

		// User is loggedIn
		if (userId > 0) {
			String cabId = prefernecesHandler.getString(PrefKeys.CAB_ID);
			WebServiceModel.updateDriverLocation(String.valueOf(userId), cabId,
					String.valueOf(latitude), String.valueOf(longitude),
					responseListener);
		}
	}

	public static ArrayList<Cab> getCabs(JsonObject json, String activeCabID) {
		ArrayList<Cab> list = new ArrayList<Cab>();
		String cabNo, cab_provider_id, id;
		boolean status, isActive;

		try {

			for (Entry<String, JsonElement> entry : json.getAsJsonObject()
					.entrySet()) {
				JsonElement jsonElement = entry.getValue();

				cabNo = JsonHelper.getString(jsonElement.getAsJsonObject(),
						APIConstants.KEY_CAB_NO);
				cab_provider_id = JsonHelper.getString(
						jsonElement.getAsJsonObject(),
						APIConstants.KEY_CAB_PROVIDER_ID);
				id = JsonHelper.getString(jsonElement.getAsJsonObject(),
						APIConstants.KEY_ID);
				String _status = JsonHelper.getString(
						jsonElement.getAsJsonObject(), APIConstants.KEY_STATUS);
				String _isActive = JsonHelper.getString(
						jsonElement.getAsJsonObject(),
						APIConstants.KEY_DRIVER_ASSIGNED);
				status = _status != null && _status.equalsIgnoreCase("active");
				isActive = _isActive != null
						&& _isActive.equalsIgnoreCase("yes");
				Cab cab = new Cab();
				cab.setCabNo(cabNo);
				cab.setCabProviderId(cab_provider_id);
				cab.setID(id);
				cab.setStatus(status);
				// cab.isActive(isActive);
				cab.isActive(id.equalsIgnoreCase(activeCabID));
				list.add(cab);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*
		 * JsonArray jsonArray = json.getAsJsonArray(); if(jsonArray == null)
		 * return list;
		 * 
		 * for (int i = 0; i < jsonArray.size(); i++) { JsonElement obj =
		 * jsonArray.get(i); String name =
		 * JsonHelper.getString(obj.getAsJsonObject(), APIConstants.KEY_NAME);
		 * Applog.Debug("Naame " + name); }
		 */
		return list;
	}

	public static ArrayList<Journey> parseHistory(JsonObject json) {
		ArrayList<Journey> list = new ArrayList<Journey>();
		String firstName = "", lastName = "", corporateName = "", pickFromAddress = "", pickFromTime = "", dropToAddress = "", dropToTime = "";
		String paymentType = "", paymentAmout = "", tipGiven = "";
		boolean taxIncluded = false;

		try {
			/****** JOURNEYS ****/
			for (Entry<String, JsonElement> entry : json.getAsJsonObject()
					.entrySet()) {
				JsonElement jsonElement = entry.getValue();
				JsonObject journeyUsersObject = JsonHelper.getJsonObject(
						jsonElement.getAsJsonObject(), "journey_users");
				// Applog.Debug("journeyUsersObject " + journeyUsersObject);

				/** JOURNEY_USERS **/
				for (Entry<String, JsonElement> journey : journeyUsersObject
						.getAsJsonObject().entrySet()) {
					JsonElement jsonJourneyElement = journey.getValue();

					pickFromTime = JsonHelper
							.getString(jsonJourneyElement.getAsJsonObject(),
									"pickup_time");
					pickFromAddress = JsonHelper.getString(
							jsonJourneyElement.getAsJsonObject(),
							"pickup_door_address");
					dropToAddress = JsonHelper.getString(
							jsonJourneyElement.getAsJsonObject(),
							"dropOff_door_address");

					taxIncluded = Utils.isEmptyOrNull(JsonHelper.getString(
							jsonJourneyElement.getAsJsonObject(), "extras")) ? false
							: true;
					dropToTime = JsonHelper.getString(
							jsonJourneyElement.getAsJsonObject(),
							"dropOff_time");
					tipGiven = JsonHelper.getString(
							jsonJourneyElement.getAsJsonObject(), "tip_given");
					Applog.Debug("TRIP : Pick From " + pickFromAddress);

					JsonObject usersObject = JsonHelper.getJsonObject(
							jsonJourneyElement.getAsJsonObject(), "users");

					/******* USERS *******/
					for (Entry<String, JsonElement> users : usersObject
							.entrySet()) {
						JsonElement usersJsonElement = users.getValue();
						firstName = JsonHelper.getString(
								usersJsonElement.getAsJsonObject(),
								APIConstants.KEY_FIRST_NAME);
						lastName = JsonHelper.getString(
								usersJsonElement.getAsJsonObject(),
								APIConstants.KEY_LAST_NAME);
						Applog.Debug("TRIP : firstName " + firstName);
						/******* Coparate Users *******/
						try {
							JsonObject coparteUsersObject = JsonHelper
									.getJsonObject(
											usersJsonElement.getAsJsonObject(),
											"corporate_users");
							corporateName = JsonHelper.getString(
									coparteUsersObject, APIConstants.KEY_NAME);
							Applog.Debug("TRIP : coparteUsersObject "
									+ corporateName);
						} catch (Exception e) {
							corporateName = "";
						}

					}
					/***** USERS END ******/

					/******* PAYMENT *******/
					JsonObject paymentObject = JsonHelper.getJsonObject(
							jsonJourneyElement.getAsJsonObject(), "Payment");
					for (Entry<String, JsonElement> paymentEntry : paymentObject
							.entrySet()) {
						JsonElement paymentJsonElement = paymentEntry
								.getValue();
						paymentAmout = JsonHelper.getString(
								paymentJsonElement.getAsJsonObject(), "amount");
						paymentType = JsonHelper.getString(
								paymentJsonElement.getAsJsonObject(),
								"payment_type");
						Applog.Debug("TRIP : paymentAMount " + paymentAmout);
					}
				}

				Journey journey = new Journey();
				journey.setCorporateName(corporateName);
				journey.setDropToAddress(dropToAddress);
				journey.setDropToTime(dropToTime);
				journey.setFirstName(firstName);
				journey.setLastName(lastName);
				journey.setPaymentAmout(paymentAmout);
				journey.setExtras(taxIncluded);
				journey.setPaymentType(paymentType);
				journey.setPickFromAddress(pickFromAddress);
				journey.setPickFromTime(pickFromTime);
				journey.setTipGiven(tipGiven);
				list.add(journey);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}
	/********* END SALIK **********/
}
