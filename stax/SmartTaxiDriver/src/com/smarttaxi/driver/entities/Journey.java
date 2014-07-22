package com.smarttaxi.driver.entities;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Iterator;

import org.json.JSONObject;

import android.R.string;
import android.util.JsonReader;

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

public class Journey extends BaseWebServiceEntity {

	

	/* journeys */
	String journeys_id ="";
	String journeys_cab_id="";
	String journeys_cab_provider_id="";
	String journeys_journey_option="";
	String journeys_status="";
	String journeys_notification_status="";
	String journeys_created="";

	/* journey_users */
	String journey_users_id="";
	String journey_users_user_id="";
	String journey_users_journey_id="";
	String journey_users_max_no_of_passengers="";
	String journey_users_pickup_door_address="";
	String journey_users_pickup_lat="";
	String journey_users_pickup_lng="";
	String journey_users_dropOff_door_address="";
	String journey_users_dropOff_lat="";
	String journey_users_dropOff_lng="";
	String journey_users_pickup_time="";
	String journey_users_dropOff_time="";
	String journey_users_no_of_bags="";
	String journey_users_child_seats="";
	String journey_users_status="";
	String journey_users_tip_given="";
	String journey_users_extra_amount="";
	String journey_users_extras="";
	String journey_users_optional_message="";
	String journey_users_journey_type="";
	String journey_users_cancel_message="";
	String journey_users_created="";

	/* users */
	String users_id="";
	String users_first_name="";
	String users_last_name="";
	String users_username="";
	String users_password="";
	String users_gender="";
	String users_phone="";
	String users_user_image="";
	String users_group_id="";
	String users_tip="";
	String users_ip_address="";
	String users_created="";
	String users_modified="";
	String users_status="";
	String users_corporate_id="";
	String users_conversation_group_id="";

	/* corporate_users */
	String corporate_users_id="";
	String corporate_users_name="";
	String corporate_users_license_no="";
	String corporate_users_email="";
	String corporate_users_phone_no="";
	String corporate_users_address="";

	/* cab_provider */
	String cab_provider_cpID="";
	String cab_provider_name="";
	String cab_provider_price="";
	String cab_provider_description="";
	String cab_provider_seats_avalaible="";
	String cab_provider_child_seats_available="";
	String cab_provider_luggage_capacity="";
	String cab_provider_image_url="";
	String cab_provider_image_rating_count="";

	/* avgRating */
	String avgRating__empty_="";
	String avgRating_punctuality="";
	String avgRating_cab_condition="";
	String avgRating_service="";

	/* Cab */
	String Cab_id="";
	String Cab_cab_provider_id="";
	String Cab_cab_no="";
	String Cab_color="";
	String Cab_model="";
	String Cab_make="";
	String Cab_image_url="";
	String Cab_cab_available="";
	String Cab_driver_assigned="";
	String Cab_status="";

	/* CabLocation */
	String CabLocation_id="";
	String CabLocation_driver_id="";
	String CabLocation_cab_id="";
	String CabLocation_latitude="";
	String CabLocation_longitude="";

	/* Payment */
	String Payment_id="";
	String Payment_payment_type="";
	String Payment_amount="";
	String Payment_customer_id="";
	String Payment_journey_id="";
	String Payment_status="";
	String Payment_created="";

	/* DriverInformation */
	String DriverInformation_id="";
	String DriverInformation_name="";
	String DriverInformation_age="";
	String DriverInformation_gender="";
	String DriverInformation_contact_no="";
	String DriverInformation_license_code="";
	String DriverInformation_user_id="";
	String DriverInformation_cab_id="";
	String DriverInformation_pob_status="";
	String DriverInformation_post_check_in="";
	String DriverInformation_cab_provider_id="";
	String DriverInformation_image_url="";
	String DriverInformation_dispatcher_id="";
	String DriverInformation_driver_rating="";

	
	@Override
	public void deserializeFromJSON(String json) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deserializeFromJSON(JsonElement jsonElement) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Journey.class, new UserDeserializer());
		Gson gson = gsonBuilder.create();
		Journey journey = gson.fromJson(jsonElement, Journey.class);
		copy(journey);
		
	}

	

	

	private void copy(Journey data) {

		
		 this.journeys_id = data.journeys_id;
		this.journeys_cab_id = data.journeys_cab_id;
		this.journeys_cab_provider_id = data.journeys_cab_provider_id;
		this.journeys_journey_option = data.journeys_journey_option;
		this.journeys_status = data.journeys_status;
		this.journeys_notification_status = data.journeys_notification_status;
		this.journeys_created = data.journeys_created;

		this.journey_users_id = data.journey_users_id;
		this.journey_users_journey_id = data.journey_users_id;
		this.journey_users_max_no_of_passengers = data.journey_users_max_no_of_passengers;
		this.journey_users_pickup_door_address = data.journey_users_pickup_door_address;
		this.journey_users_pickup_lat = data.journey_users_pickup_lat;
		this.journey_users_pickup_lng = data.journey_users_pickup_lng;
		this.journey_users_dropOff_door_address = data.journey_users_dropOff_door_address;
		this.journey_users_dropOff_lat = data.journey_users_dropOff_lat;
		this.journey_users_dropOff_lng = data.journey_users_dropOff_lng;
		this.journey_users_pickup_time = data.journey_users_pickup_time;
		this.journey_users_dropOff_time = data.journey_users_pickup_time;
		this.journey_users_no_of_bags = data.journey_users_no_of_bags;
		this.journey_users_child_seats = data.journey_users_child_seats;
		this.journey_users_status = data.journey_users_status;
		this.journey_users_tip_given = data.journey_users_tip_given;
		this.journey_users_extra_amount = data.journey_users_extra_amount;
		this.journey_users_extras = data.journey_users_extras;
		this.journey_users_optional_message = data.journey_users_optional_message;
		this.journey_users_journey_type = data.journey_users_journey_type;
		this.journey_users_cancel_message = data.journey_users_cancel_message;
		this.journey_users_created = data.journey_users_created;

		this.users_id = data.users_id;
		this.users_first_name = data.users_first_name;
		this.users_last_name = data.users_last_name;
		this.users_username = data.users_username;
		this.users_password = data.users_password;
		this.users_gender = data.users_gender;
		this.users_phone = data.users_phone;
		this.users_user_image = data.users_user_image;
		this.users_group_id = data.users_group_id;
		this.users_tip = data.users_tip;
		this.users_ip_address = data.users_ip_address;
		this.users_created = data.users_created;
		this.users_modified = data.users_modified;
		this.users_status = data.users_status;
		this.users_corporate_id = data.users_corporate_id;
		this.users_conversation_group_id = data.users_conversation_group_id;

	

	}

	public class UserDeserializer implements JsonDeserializer<Journey> {

		@Override
		public Journey deserialize(JsonElement jsonElement, Type type,
				JsonDeserializationContext context) throws JsonParseException {
			JsonObject rootObj = jsonElement.getAsJsonObject();		
			
			Journey data = new Journey();	
			
			data.journeys_id = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_ID);
			data.journeys_cab_id = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_CAB_ID);
			data.journeys_cab_provider_id = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_CAB_PROVIDER_ID);
			data.journeys_journey_option= JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_OPTION);
			data.journeys_status = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_STATUS);
			data.journeys_notification_status= JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_NOTIFICATION_STATUS);
			data.journeys_created = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_CREATED);
			
			
			
			if(rootObj.getAsJsonObject(APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS) instanceof JsonObject)
				rootObj=  ignoreNestedObject(rootObj,APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS);
			
					
			data.journey_users_id = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_ID);
			data.journey_users_user_id = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_USER_ID);
			data.journey_users_journey_id = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_JOURNEY_ID);
			data.journey_users_max_no_of_passengers = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_MAX_NO_PASSENGER);
			data.journey_users_pickup_door_address = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_PICKUP_ADDRESS);
			data.journey_users_pickup_lat = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_PICKUP_LAT);
			data.journey_users_pickup_lng = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_PICKUP_LNG);
			data.journey_users_dropOff_door_address = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_DROP_ADDRESS);
			data.journey_users_dropOff_lat = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_DROP_LAT);
			data.journey_users_dropOff_lng = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_DROP_LNG);
			data.journey_users_pickup_time = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_PICKUP_TIME);
			data.journey_users_dropOff_time = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_DROPOFF_TIME);
			data.journey_users_no_of_bags = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_NO_OF_BAGS);
			data.journey_users_child_seats = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_CHILD_SEATS);
			data.journey_users_status = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_STATUS);
			data.journey_users_tip_given = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_TIP_GIVEN);
			data.journey_users_extra_amount = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_EXTRA_AMOUNT);
			data.journey_users_extras = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_EXTRAS);
			data.journey_users_optional_message = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_OPTIONAL_MESSAGE);
			data.journey_users_journey_type = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_JOURNEY_TYPE);
			data.journey_users_cancel_message = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_CANCEL_MESSAGE);
			data.journey_users_created = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_CREATED);
			
			
			if(rootObj.getAsJsonObject(APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_USERS) instanceof JsonObject)
				rootObj=  ignoreNestedObject(rootObj,APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_USERS);
			
			data.users_id = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_USERS_ID);
			data.users_first_name = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_USERS_FIRST_NAME);
			data.users_last_name = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_USERS_LAST_NAME);
			data.users_username = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_USERS_USERNAME);
			data.users_password = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_USERS_PASSWORD);
			data.users_gender = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_USERS_GENDER);
			data.users_phone = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_USERS_PHONE);
			data.users_user_image = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_USERS_USER_IMAGE);
			data.users_group_id = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_USERS_GROUPD_ID);
			data.users_tip = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_USERS_TIP);
			data.users_ip_address = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_USERS_IPADDRESS);
			data.users_created = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_USERS_CREATED);
			data.users_modified = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_USERS_MODIFIED);
			data.users_status = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_USERS_STATUS);
			data.users_corporate_id = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_USERS_CORPORATE_ID);
			data.users_conversation_group_id = JsonHelper.getString(rootObj, APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_USERS_CONVERSATION_GROUP_ID);
				
			
				

			
			return data;
		}

	}

	boolean tryParseInt(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	public JsonObject ignoreNestedObject(JsonObject parentjsonObject,
			String parentKey) {

		String mainJson = parentjsonObject.getAsJsonObject(parentKey)
				.getAsJsonObject().entrySet().iterator().next().getValue()
				.toString();
		return JsonHelper.parseToJsonObject(mainJson);

	}

	public String getJourneysId() {
		return journeys_id;
	}

	public String getJourneyCapId() {
		return journeys_cab_id;
	}
	public String getJourneyCapProviderId() {
		return journeys_cab_provider_id;
	}
	
	
	public String getJourneyoptions() {
		return journeys_journey_option;
	}
	
	
	
	public String getJourneystatus() {
		return journeys_status;
	}
	
	
	public String getJourneyNotificationStatus() {
		return journeys_notification_status;
	}
	///////////
	
	public String getJourneyCreationData() {
		return journeys_created;
	}
	
	public String getJourneyUserId() {
		return journey_users_id;
	}
	
	
	
	public String getJourneyUseresUserId() {
		return journey_users_user_id;
	}
	public String getUserJourneyId() {
		return journey_users_journey_id;
	}
	
	
	public String getMaxNoOfPassenger() {
		return journey_users_max_no_of_passengers;
	}
	
	
	
	public String getPickUpAddress() {
		return journey_users_pickup_door_address;
	}
	
	public String getGetPickUpLatitude() {
		return journey_users_pickup_lat;
	}
	public String getGetPickUpLongitude() {
		return journey_users_pickup_lng;
	}
	
	
	public String getUserDropOffAddress() {
		return journey_users_dropOff_door_address;
	}
	public String getUserDropOffLatitude() {
		return journey_users_dropOff_lat;
	}
	public String getUserDropOffLongitude() {
		return journey_users_dropOff_lng;
	}
	
	
	
	
	
	
	////////////
	public String getUserPickUpTime() {
		return journey_users_pickup_time;
	}
	public String getUserDropOffTime() {
		return journey_users_dropOff_time;
	}
	public String getUserNoOfBags() {
		return journey_users_no_of_bags;
	}
	public String getUserChildSeats() {
		return journey_users_child_seats;
	}
	public String getUserStatus() {
		return journey_users_status;
	}
	public String getUserTipGiven() {
		return journey_users_tip_given;
	}
	public String getUserExtraAmount() {
		return journey_users_extra_amount;
	}
	//////////////
	
	
	
	public String getUserExtras() {
		return journey_users_extras;
	}
	public String getUserOptionalMessage() {
		return journey_users_optional_message;
	}
	public String getUsersJourneyType() {
		return journey_users_journey_type;
	}
	public String getUserCancelMessage() {
		return journey_users_cancel_message;
	}
	public String getUserCreatedDate() {
		return journey_users_created;
	}
	
	
	
	
	
	

	public String getUserId() {
		return users_id;

	}
	public String getUserFirstName() {
		return users_first_name;
	}
	
	
	public String getUserLastName() {
		return users_last_name;
	}
	public String getUserUserName() {
		return users_username;
	}
	public String getUserPassword() {
		return users_password;
	}
	public String getUserGender() {
		return users_gender;
	}
	public String getUserPhoneNo() {
		return users_phone;
	}
	public String getUserImage() {
		return users_user_image;
	}
	public String getUserGroupId() {
		return users_group_id;
	}
	
	
	public String getUserTip() {
		return users_tip;
	}
	
	public String getUserIpAddress() {
		return users_ip_address;
	}
	public String getUserCreated() {
		return users_created;
	}
	public String getUserModified() {
		return users_modified;
	}
	public String getUsersStatus() {
		return users_status;
	}
	
	
	public String getUsersCorporateId() {
		return users_corporate_id;
	}
	public String getUsersConversationGroupId() {
		return users_conversation_group_id;
	}
	
	private String firstName = "", lastName, corporateName, pickFromAddress, pickFromTime, dropToAddress, dropToTime;
	private String paymentType, paymentAmout, tipGiven;
	private boolean extras = false;
	
	public boolean getExtras() {
		return extras;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getCorporateName() {
		return corporateName;
	}
	public void setExtras(boolean extras) {
		this.extras = extras;
	}
	public void setCorporateName(String corporateName) {
		this.corporateName = corporateName;
	}
	public String getPickFromAddress() {
		return pickFromAddress;
	}
	public void setPickFromAddress(String pickFromAddress) {
		this.pickFromAddress = pickFromAddress;
	}
	public String getPickFromTime() {
		return pickFromTime;
	}
	public void setPickFromTime(String pickFromTime) {
		this.pickFromTime = pickFromTime;
	}
	public String getDropToAddress() {
		return dropToAddress;
	}
	public void setDropToAddress(String dropToAddress) {
		this.dropToAddress = dropToAddress;
	}
	public String getDropToTime() {
		return dropToTime;
	}
	public void setDropToTime(String dropToTime) {
		this.dropToTime = dropToTime;
	}
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	public String getPaymentAmout() {
		return paymentAmout;
	}
	public void setPaymentAmout(String paymentAmout) {
		this.paymentAmout = paymentAmout;
	}
	public String getTipGiven() {
		return tipGiven;
	}
	public void setTipGiven(String tipGiven) {
		this.tipGiven = tipGiven;
	}
	public CharSequence getUsername() {
		return  lastName + " "+ firstName;
	}
	
	/* class */

	/*
	 * public String getId() { return id; }
	 * 
	 * public void setId(String id) { this.id = id; }
	 * 
	 * public String getFirstName() { return firstName; }
	 * 
	 * public void setFirstName(String firstName) { this.firstName = firstName;
	 * }
	 * 
	 * public String getLastName() { return lastName; }
	 * 
	 * public void setLastName(String lastName) { this.lastName = lastName; }
	 * 
	 * public String getUserName() { return userName; }
	 * 
	 * public void setUserName(String userName) { this.userName = userName; }
	 * public String getCabID() { return cabId; }
	 * 
	 * public void setCabID(String cabID) { this.cabId = cabID; } public String
	 * getCreated() { return created; }
	 * 
	 * public void setCreated(String created) { this.created = created; }
	 * 
	 * public String getStatus() { return status; }
	 * 
	 * public void setStatus(String status) { this.status = status; }
	 * 
	 * public String getGroupId() { return groupId; }
	 * 
	 * public void setGroupId(String groupId) { this.groupId = groupId; }
	 */

}
