package com.smarttaxi.driver.entities;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.smarttaxi.driver.constants.APIConstants;
import com.smarttaxi.driver.entities.base.BaseWebServiceEntity;
import com.smarttaxi.driver.helpers.JsonHelper;

public class Cab extends BaseWebServiceEntity{
	
	String color;
	String model;
	String make; 
	String imageUrl;
	String status;
	String latutude;
	String longitude;
	String cabProviderId;
	Driver driver;	
	CabProvider cabProvider;
	int timeDurationInSeconds;
		

	@Override
	public void deserializeFromJSON(JsonElement jsonElement) {
		GsonBuilder gsonBuilder = new GsonBuilder();
	    gsonBuilder.registerTypeAdapter(Cab.class, new CabDeserializer());
	    Gson gson = gsonBuilder.create();
	    Cab cab = gson.fromJson(jsonElement, Cab.class);
	    copy(cab);				
	}
	
	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLatutude() {
		return latutude;
	}

	public void setLatutude(String latutude) {
		this.latutude = latutude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getCabProviderId() {
		return cabProviderId;
	}

	public void setCabProviderId(String cabProviderId) {
		this.cabProviderId = cabProviderId;
	}

	public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}

	public CabProvider getCabProvider() {
		return cabProvider;
	}

	public void setCabProvider(CabProvider cabProvider) {
		this.cabProvider = cabProvider;
	}

	public int getTimeDurationInSeconds() {
		return timeDurationInSeconds;
	}

	public void setTimeDurationInSeconds(int timeDurationInSeconds) {
		this.timeDurationInSeconds = timeDurationInSeconds;
	}
	
	public String getFormattedTime(boolean shortUnit) {
		int timeInMin = (int)Math.ceil((float)timeDurationInSeconds/60.0f);
		if(shortUnit)
			return timeInMin + "m";
		return timeInMin + " min";
	}

	private void copy(Cab cab){
		this.color = cab.color;
		this.model = cab.model;
		this.make = cab.make; 
		this.imageUrl = cab.imageUrl;
		this.status = cab.status;
		this.latutude = cab.latutude;
		this.longitude = cab.longitude;
		this.cabProviderId = cab.cabProviderId;	
	}
	
	public LatLng getLatLng() {
		return new LatLng(Double.valueOf(latutude), Double.valueOf(longitude));
	}
	
	public class CabDeserializer implements JsonDeserializer<Cab> {

		@Override
		public Cab deserialize(JsonElement jsonElement, Type type,
		JsonDeserializationContext context) throws JsonParseException {
			JsonObject rootObj = jsonElement.getAsJsonObject();		
			
			Cab cab = new Cab();			
			cab.color = JsonHelper.getString(rootObj, APIConstants.KEY_COLOR);
			cab.model = JsonHelper.getString(rootObj, APIConstants.KEY_MODEL);
			cab.make = JsonHelper.getString(rootObj, APIConstants.KEY_MAKE); 
			cab.imageUrl = JsonHelper.getString(rootObj, APIConstants.KEY_IMAGE_URL);
			cab.status = JsonHelper.getString(rootObj, APIConstants.KEY_STATUS);
			cab.latutude = JsonHelper.getString(rootObj, APIConstants.KEY_LATITUDE);
			cab.longitude = JsonHelper.getString(rootObj, APIConstants.KEY_LONGITUDE);
			cab.cabProviderId = JsonHelper.getString(rootObj, APIConstants.KEY_CAB_PROVIDER_ID);
			
			if(rootObj.has(APIConstants.KEY_DRIVERS)) {
				driver = new Driver();
				JsonObject driversObj = JsonHelper.getJsonObject(rootObj, APIConstants.KEY_DRIVERS);
				@SuppressWarnings("unchecked")
				Map.Entry<String,JsonElement> entry = (Entry<String, JsonElement>) driversObj.entrySet().toArray()[0];
				
				driver.deserializeFromJSON(entry.getValue());
			}
			
			if(rootObj.has(APIConstants.KEY_CAB_PROVIDER)) {
				cabProvider = new CabProvider();
				JsonObject cabProviderObj = JsonHelper.getJsonObject(rootObj, APIConstants.KEY_CAB_PROVIDER);
				@SuppressWarnings("unchecked")
				Map.Entry<String,JsonElement> entry = (Entry<String, JsonElement>) cabProviderObj.entrySet().toArray()[0];
				cabProvider.deserializeFromJSON(entry.getValue());
			}
			
			return cab;
		}
		
	}

	@Override
	public void deserializeFromJSON(String json) {
		// TODO Auto-generated method stub
		
	}

	
	public void setCabNo(String cabNo) {
		this.cabNo = cabNo;
	}

	public void setID(String id) {
		this.id = id;
	}

	public void setStatus(boolean status) {
		this._status = status;
	}

	public void isActive(boolean isActive) {
			this.isActive = isActive;
	}
	
	
	public String getCabNo() {
		return cabNo;
	}

	public String getId() {
		return id;
	}

	public boolean is_status() {
		return _status;
	}

	public boolean isActive() {
		return isActive;
	}



	private String cabNo, id;
	private boolean _status, isActive;

}
