package com.smarttaxi.driver.entities;

import java.lang.reflect.Type;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import com.smarttaxi.driver.constants.APIConstants;
import com.smarttaxi.driver.entities.base.BaseWebServiceEntity;
import com.smarttaxi.driver.helpers.JsonHelper;

public class Driver extends BaseWebServiceEntity{
	
	String name;
	/*String age;
	String gender;
	String licenseCode;
	String userID;
	String cabID;
	String dispatcherID;
	HashMap<String, String> avgRating;*/
	
	
	String driver_id;
	String type;
	String lat;
	String lng;
	String pob_status;
	String post_check_in_status;
	String cab_status;
	
	
	 
	
	@Override
	public void deserializeFromJSON(JsonElement jsonElement) {
		GsonBuilder gsonBuilder = new GsonBuilder();
	    gsonBuilder.registerTypeAdapter(Driver.class, new DriverDeserializer());
	    Gson gson = gsonBuilder.create();
	    Driver driver = gson.fromJson(jsonElement, Driver.class);
	    copy(driver);
		
	    
	    /*CurrentDriverStatus status = gson.fromJson(json, CurrentDriverStatus.class);
		copy(status);*/
	}
	
	public String getDriverId() {
		return driver_id;
	}
	
	
	public String getType() {
		return type;
	}
	
	public String getLatitude() {
		return lat;
	}
	
	
	
	public String getLongitude() {
		return lng;
	}
	
	
	public String getPobStatus() {
		return pob_status;
	}
	
	public String getPostCheckInStatus() {
		return post_check_in_status;
	}
	
	public String getCabStatus() {
		return cab_status;
	}
	
	
	
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/*public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getLicenseCode() {
		return licenseCode;
	}

	public void setLicenseCode(String licenseCode) {
		this.licenseCode = licenseCode;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getCabID() {
		return cabID;
	}

	public void setCabID(String cabID) {
		this.cabID = cabID;
	}

	public String getDispatcherID() {
		return dispatcherID;
	}

	public void setDispatcherID(String dispatcherID) {
		this.dispatcherID = dispatcherID;
	}

	public HashMap<String, String> getAvgRating() {
		return avgRating;
	}

	public void setAvgRating(HashMap<String, String> avgRating) {
		this.avgRating = avgRating;
	}*/

	private void copy(Driver status) {
		/*this.name = driver.name;
		this.age = driver.age;
		this.gender = driver.gender;
		this.licenseCode = driver.licenseCode;
		this.userID = driver.userID;
		this.cabID = driver.cabID;
		this.dispatcherID = driver.dispatcherID;		*/
		
		this.driver_id =status.driver_id;
		this.type=status.type;
		this.lat=status.lat;
		this.lng=status.lng;
		this.pob_status=status.pob_status;
		this.post_check_in_status=status.post_check_in_status;
		this.cab_status = status.cab_status;
	}
	
	public class DriverDeserializer implements JsonDeserializer<Driver> {

		@Override
		public Driver deserialize(JsonElement jsonElement, Type type,
		JsonDeserializationContext context) throws JsonParseException {
			JsonObject rootObj = jsonElement.getAsJsonObject();		
			
			Driver driver = new Driver();
			/*driver.name = JsonHelper.getString(rootObj, APIConstants.KEY_NAME);
			driver.age = JsonHelper.getString(rootObj, APIConstants.KEY_AGE);
			driver.gender = JsonHelper.getString(rootObj, APIConstants.KEY_GENDER);
			driver.licenseCode = JsonHelper.getString(rootObj, APIConstants.KEY_LICENSE_CODE);
			driver.userID = JsonHelper.getString(rootObj, APIConstants.KEY_USER_ID);
			driver.cabID = JsonHelper.getString(rootObj, APIConstants.KEY_CAB_ID);
			driver.dispatcherID = JsonHelper.getString(rootObj, APIConstants.KEY_DISPATCHER_ID);*/
			
			
			
			driver.driver_id = JsonHelper.getString(rootObj, APIConstants.KEY_DRIVERID);
			driver.type = JsonHelper.getString(rootObj, APIConstants.KEY_DRIVER_STATUS_TYPE);
			driver.lat = JsonHelper.getString(rootObj, APIConstants.KEY_LAT);
			driver.lng = JsonHelper.getString(rootObj, APIConstants.KEY_LNG);
			driver.pob_status = JsonHelper.getString(rootObj, APIConstants.KEY_DRIVER_INFO_POB_STATUS);
			
			driver.post_check_in_status = JsonHelper.getString(rootObj, APIConstants.KEY_POST_CHECK_IN_STATUS);
			driver.cab_status = JsonHelper.getString(rootObj, APIConstants.KEY_CAB_STATUS);
		
			
			
			/*if(rootObj.has(APIConstants.KEY_AVG_RATING)) {
				avgRating = new HashMap<String, String>();
				JsonObject avgRatingObj = JsonHelper.getJsonObject(rootObj, APIConstants.KEY_AVG_RATING);
				avgRating.put(APIConstants.KEY_PUNCTUALITY, JsonHelper
						.getString(avgRatingObj, APIConstants.KEY_PUNCTUALITY));
				avgRating.put(APIConstants.KEY_CAB_CONDITION, JsonHelper
						.getString(avgRatingObj, APIConstants.KEY_CAB_CONDITION));
				avgRating.put(APIConstants.KEY_SERVICE, JsonHelper
						.getString(avgRatingObj, APIConstants.KEY_SERVICE));
			}*/
			
			return driver;
		}
		
	}

	@Override
	public void deserializeFromJSON(String json) {
		// TODO parse
//		 {"header":{"code":"0","message":"success"},"body":{"driver_id":"1","type":"pob","lat":"3","lng":"3","pob_status":null,"post_check_in_status":null,"cab_status":null}}
		Gson gson = new Gson();
		Driver status = gson.fromJson(json, Driver.class);
		copy(status);
	
	}
	
	private class CurrentDriverStatus {

		@SerializedName("body")
		private String bodyMsg;
		
		@SerializedName("driver_id")
		private String driver_id;
		
		@SerializedName("type")
		private String type;
		
		@SerializedName("lat")
		private String lat;
		
		@SerializedName("lng")
		private String lng;
		
		@SerializedName("pob_status")
		private String pob_status;
		
		@SerializedName("post_check_in_status")
		private String post_check_in_status;
		
		
		@SerializedName("cab_status")
		private String cab_status;
		
		

	}

}
