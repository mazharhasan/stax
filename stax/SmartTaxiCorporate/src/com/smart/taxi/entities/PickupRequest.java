package com.smart.taxi.entities;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.smart.taxi.constants.APIConstants;
import com.smart.taxi.entities.base.BaseWebServiceEntity;
import com.smart.taxi.helpers.JsonHelper;

public class PickupRequest extends BaseWebServiceEntity {

	String journey_id;
	String user_id;
	String UserName;
	String UserImage;
	String group_id;
	String corporateName;
	String PickupTime;
	String PickupLocation;
	String AdditionalMessage;


	@Override
	public void deserializeFromJSON(JsonElement jsonElement) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(User.class, new UserDeserializer());
		Gson gson = gsonBuilder.create();
		PickupRequest pickrequest = gson.fromJson(jsonElement, PickupRequest.class);
		copy(pickrequest);
	}

	private void copy(PickupRequest pickrequest) {
	
		
		this.journey_id = pickrequest.journey_id;
		this.user_id = pickrequest.user_id;
		this.UserName= pickrequest.UserName;
		this.UserImage =pickrequest.UserImage;
		this.group_id= pickrequest.group_id;
		this.corporateName=pickrequest.corporateName;
		this.PickupTime = pickrequest.PickupTime;
		this.PickupLocation=pickrequest.PickupLocation;
		this.AdditionalMessage =pickrequest.AdditionalMessage;
	}

	public String getjourneyId() {
		return journey_id;
	}

	public String getUserId() {
		return user_id;
	}
	
	public String getUserName() {
		return UserName;
	}
	
	
	public String getUserImage() {
		return UserImage;
	}
	
	public String getGroupId() {
		return group_id;
	}
	
	
	public String getCorporateName() {
		return corporateName;
	}
	
	
	public String getPickupTime() {
		return PickupTime;
	}
	
	
	public String getPickupLocation() {
		return PickupLocation;
	}
	

	public String getAdditionalMessage() {
		return AdditionalMessage;
	}

	
	public class UserDeserializer implements JsonDeserializer<PickupRequest> {

		@Override
		public PickupRequest deserialize(JsonElement jsonElement, Type type,
				JsonDeserializationContext context) throws JsonParseException {
			JsonObject rootObj = jsonElement.getAsJsonObject();

			PickupRequest pickrequest = new PickupRequest();

			
			/*	"journey_id": "486",
		    "user_id": "108",
		    "UserName": "ahbab sami",
		    "UserImage": "b9305-Screen-Shot-2013-10-27-at-10.59.47-pm.png",
		    "group_id": "5",
		    "corporateName": "Amani Systems",
		    "PickupTime": "2013-11-11 01:26:26",
		    "PickupLocation": "Shahrah-e-Faisal Service Rd N,Karachi",
		    "AdditionalMessage": "Hello this is the test message"
			*/
			pickrequest.journey_id = JsonHelper.getString(rootObj, APIConstants.KEY_PICK_REQ_JOURENY_ID);
			pickrequest.user_id = JsonHelper.getString(rootObj, APIConstants.KEY_PICK_REQ_USER_ID);
			pickrequest.UserName = JsonHelper.getString(rootObj, APIConstants.KEY_PICK_REQ_USERNAME);
			pickrequest.UserImage = JsonHelper.getString(rootObj, APIConstants.KEY_PICK_REQ_USER_IMAGE);
			pickrequest.group_id = JsonHelper.getString(rootObj, APIConstants.KEY_PICK_REQ_GROUP_ID);
			pickrequest.corporateName = JsonHelper.getString(rootObj, APIConstants.KEY_PICK_REQ_CORPORATE_NAME);
			pickrequest.PickupTime = JsonHelper.getString(rootObj, APIConstants.KEY_PICK_REQ_PICK_UP_TIME);
			pickrequest.PickupLocation = JsonHelper.getString(rootObj, APIConstants.KEY_PICK_REQ_PICK_UP_LOCATION);
			pickrequest.AdditionalMessage = JsonHelper.getString(rootObj, APIConstants.KEY_PICK_REQ_ADDITIONAL_MESSAGE);
			// user.firstName = JsonHelper.getString(rootObj,
			// APIConstants.KEY_FIRST_NAME);
			// user.lastName = JsonHelper.getString(rootObj,
			// APIConstants.KEY_LAST_NAME);
			

			return pickrequest;
		}

	}

	@Override
	public void deserializeFromJSON(String json) {
		// TODO Auto-generated method stub

	}

}
