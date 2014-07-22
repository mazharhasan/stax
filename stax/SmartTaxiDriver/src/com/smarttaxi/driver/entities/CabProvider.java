package com.smarttaxi.driver.entities;

import java.lang.reflect.Type;

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

public class CabProvider extends BaseWebServiceEntity{
	String id;
	String name;
	String price;
	String description;
	String seatsAvailable;
	String childSeatsAvailable;
	String luggageCapacity;
	String licenseCode;
	String dispatcherID;
	String imageUrl;

	@Override
	public void deserializeFromJSON(JsonElement jsonElement) {
		GsonBuilder gsonBuilder = new GsonBuilder();
	    gsonBuilder.registerTypeAdapter(CabProvider.class, new CabProviderDeserialize());
	    Gson gson = gsonBuilder.create();
	    CabProvider cabProvider = gson.fromJson(jsonElement, CabProvider.class);
	    copy(cabProvider);
	}
	
	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getPrice() {
		return price;
	}


	public void setPrice(String price) {
		this.price = price;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getSeatsAvailable() {
		return seatsAvailable;
	}


	public void setSeatsAvailable(String seatsAvailable) {
		this.seatsAvailable = seatsAvailable;
	}


	public String getChildSeatsAvailable() {
		return childSeatsAvailable;
	}


	public void setChildSeatsAvailable(String childSeatsAvailable) {
		this.childSeatsAvailable = childSeatsAvailable;
	}


	public String getLuggageCapacity() {
		return luggageCapacity;
	}


	public void setLuggageCapacity(String luggageCapacity) {
		this.luggageCapacity = luggageCapacity;
	}


	public String getLicenseCode() {
		return licenseCode;
	}


	public void setLicenseCode(String licenseCode) {
		this.licenseCode = licenseCode;
	}


	public String getDispatcherID() {
		return dispatcherID;
	}


	public void setDispatcherID(String dispatcherID) {
		this.dispatcherID = dispatcherID;
	}


	public String getImageUrl() {
		return imageUrl;
	}


	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}


	private void copy(CabProvider cabProvider) {
		this.name = cabProvider.name;
		this.price = cabProvider.price;
		this.description = cabProvider.description;
		this.seatsAvailable = cabProvider.seatsAvailable;
		this.childSeatsAvailable = cabProvider.childSeatsAvailable;
		this.luggageCapacity = cabProvider.luggageCapacity;
		this.licenseCode = cabProvider.licenseCode;
		this.dispatcherID = cabProvider.dispatcherID;
		this.imageUrl = cabProvider.imageUrl;
		this.id = cabProvider.id;
	}
	
	public class CabProviderDeserialize implements JsonDeserializer<CabProvider> {

		@Override
		public CabProvider deserialize(JsonElement jsonElement, Type type,
		JsonDeserializationContext context) throws JsonParseException {
			JsonObject rootObj = jsonElement.getAsJsonObject();
			
			CabProvider cabProvider = new CabProvider();
			cabProvider.name = JsonHelper.getString(rootObj, APIConstants.KEY_NAME);
			cabProvider.price = JsonHelper.getString(rootObj, APIConstants.KEY_PRICE);
			cabProvider.description = JsonHelper.getString(rootObj, APIConstants.KEY_DESCRIPTION);
			cabProvider.seatsAvailable = JsonHelper.getString(rootObj, APIConstants.KEY_SEATS_AVAILABLE);
			cabProvider.childSeatsAvailable = JsonHelper.getString(rootObj, APIConstants.KEY_CHILD_SEATS_AVAILABLE);
			cabProvider.luggageCapacity = JsonHelper.getString(rootObj, APIConstants.KEY_LUGGAGE_CAPCITY);
//			cabProvider.licenseCode = JsonHelper.getString(rootObj, APIConstants.KEY_LICENSE_CODE);
//			cabProvider.dispatcherID = JsonHelper.getString(rootObj, APIConstants.KEY_DISPATCHER_ID);
//			cabProvider.imageUrl = JsonHelper.getString(rootObj, APIConstants.KEY_IMAGE_URL);
			cabProvider.id = JsonHelper.getString(rootObj, APIConstants.KEY_ID);
			
			return cabProvider;
		}
		
	}

	@Override
	public void deserializeFromJSON(String json) {
		// TODO Auto-generated method stub
		
	}

	

}
