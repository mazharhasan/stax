package com.smarttaxi.driver.entities;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.smarttaxi.driver.constants.APIConstants;
import com.smarttaxi.driver.entities.base.BaseWebServiceEntity;
import com.smarttaxi.driver.helpers.JsonHelper;

public class User extends BaseWebServiceEntity {

	String driver_id;
	String driver_firstName;
	String driver_age;
	String driver_gender;
	String driver_contact_no;
	String driver_license_code;
	String driver_user_id;
	String driver_cab_id;
	String driver_pob_status;
	String driver_post_check_in;
	String driver_cab_provider_id;
	String driver_image_url;
	String driver_dispatcher_id;
	String driver_rating;
	
	
	@Override
	public void deserializeFromJSON(JsonElement jsonElement) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(User.class, new UserDeserializer());
		Gson gson = gsonBuilder.create();
		User user = gson.fromJson(jsonElement, User.class);
		copy(user);
	}

	private void copy(User user) {
		
		
		this.driver_id =user.driver_id;
		this.driver_firstName=user.driver_firstName;
		this.driver_age =user.driver_age;
		this.driver_gender=user.driver_gender;
		this.driver_contact_no=user.driver_contact_no;
		this.driver_license_code=user.driver_license_code;
		this.driver_user_id=user.driver_user_id;
		this.driver_cab_id=user.driver_cab_id;
		this.driver_pob_status=user.driver_pob_status;
		this.driver_post_check_in=user.driver_post_check_in;
		this.driver_cab_provider_id=user.driver_cab_provider_id;
		this.driver_image_url=user.driver_image_url;
		this.driver_dispatcher_id=user.driver_dispatcher_id;
		this.driver_rating=user.driver_rating;
		/**Salik**/
		this.id = user.id;
		this.DriverID = user.DriverID;
		this.license_code = user.license_code;

		this.age = user.age;
		this.gender = user.gender;
		this.contact_no = user.contact_no;

		this.userName = user.userName;
		this.pob_status = user.pob_status;
		this.post_check_in = user.post_check_in;

		// this.groupId = user.groupId;
		this.cabId = user.cabId;
		this.user_image = user.user_image;
		this.driver_rating = user.driver_rating;

		this.created = user.created;
		this.status = user.status;
		this.cab_provider_id = user.cab_provider_id;
		
		this.firstName = user.firstName;
		this.lastName = user.lastName;
		this.cabProvider = user.cabProvider;

	}

	public String getUserID() {
		return driver_id;
	}
	

	
	public String getDriverFirstName() {
		return driver_firstName;
	}
	
	

	public String getDriverAge() {
		return driver_age;
	}
	
	public String getDriverGender() {
		return driver_gender;
	}
	
	public String getDriverContatcNo()
	{
		return driver_contact_no;
	}
	
	public String getDriverLicenseCode()
	{
		return driver_license_code;
	}
	
	
	public String getDriverUserId()
	{
		return driver_user_id;
	}
	public String getDriverCabId()
	{
		return driver_cab_id;
	}
	
	
	
	public String getDriverPobStatus()
	{
		return driver_pob_status;
	}
	
	
	public String getDriverPostCheckIn()
	{
		return driver_post_check_in;
	}
	
	
	
	
	public String getDrivercabProviderId()
	{
		return driver_cab_provider_id;
	}
	
	
	public String getDriverImageUrl()
	{
		return driver_image_url;
	}
	
	
	public String getDriverDispatcherId()
	{
		return driver_dispatcher_id;
	}
	
	
	
	public String getDriverRating()
	{
		return driver_rating;
	}
	
	
	public class UserDeserializer implements JsonDeserializer<User> {

		@Override
		public User deserialize(JsonElement jsonElement, Type type,
				JsonDeserializationContext context) throws JsonParseException {
			JsonObject rootObj = jsonElement.getAsJsonObject();

			User user = new User();

			user.driver_id = JsonHelper.getString(rootObj,
					APIConstants.KEY_DRIVER_INFO_ID);
			user.driver_firstName = JsonHelper.getString(rootObj,
					APIConstants.KEY_DRIVER_INFO_NAME);

			user.driver_age = JsonHelper.getString(rootObj,
					APIConstants.KEY_DRIVER_INFO_AGE);
			user.driver_gender = JsonHelper.getString(rootObj,
					APIConstants.KEY_DRIVER_INFO_GENDER);
			user.driver_contact_no = JsonHelper.getString(rootObj,
					APIConstants.KEY_DRIVER_INFO_CONTACT);
			user.driver_license_code = JsonHelper.getString(rootObj,
					APIConstants.KEY_DRIVER_INFO_LICENSE);
			user.driver_user_id = JsonHelper.getString(rootObj,
					APIConstants.KEY_DRIVER_INFO_USER_ID);
			user.driver_cab_id = JsonHelper.getString(rootObj,
					APIConstants.KEY_DRIVER_INFO_CAB_ID);
			user.driver_pob_status = JsonHelper.getString(rootObj,
					APIConstants.KEY_DRIVER_INFO_POB_STATUS);
			user.driver_post_check_in = JsonHelper.getString(rootObj,
					APIConstants.KEY_DRIVER_INFO_POST_CHECK_IN);
			user.driver_cab_provider_id = JsonHelper.getString(rootObj,
					APIConstants.KEY_DRIVER_INFO_CAB_PROVIDER_ID);
			user.driver_image_url = JsonHelper.getString(rootObj,
					APIConstants.KEY_DRIVER_INFO_IMAGE_URL);
			user.driver_dispatcher_id = JsonHelper.getString(rootObj,
					APIConstants.KEY_DRIVER_INFO_DISPATCHER_ID);
			user.driver_rating = JsonHelper.getString(rootObj,
					APIConstants.KEY_DRIVER_INFO_DRIVER_RATING);
			user.id = JsonHelper.getString(rootObj, APIConstants.KEY_ID);
			user.DriverID = JsonHelper.getString(rootObj,
					APIConstants.KEY_USER_ID);
			user.license_code = JsonHelper.getString(rootObj,
					APIConstants.KEY_LICENSE_CODE);

			user.age = JsonHelper.getString(rootObj, APIConstants.KEY_AGE);
			user.gender = JsonHelper
					.getString(rootObj, APIConstants.KEY_GENDER);
			user.contact_no = JsonHelper.getString(rootObj,
					APIConstants.KEY_CONTACT_NO);

			user.userName = JsonHelper
					.getString(rootObj, APIConstants.KEY_NAME);
			user.pob_status = JsonHelper.getString(rootObj,
					APIConstants.KEY_POB_STATUS);
			user.post_check_in = JsonHelper.getString(rootObj,
					APIConstants.KEY_POST_CHECK_IN);

			user.cabId = JsonHelper.getString(rootObj, APIConstants.KEY_CAB_ID);
			user.user_image = JsonHelper.getString(rootObj,
					APIConstants.KEY_USER_IMAGE);
			user.driver_rating = JsonHelper.getString(rootObj,
					APIConstants.KEY_DRIVER_RATING);
			user.cab_provider_id = JsonHelper.getString(rootObj,
					APIConstants.KEY_USER_PROVIDER_ID);
			return user;
		}

	}

	@Override
	public void deserializeFromJSON(String json) {
		// TODO Auto-generated method stub

	}
	/*************** SALIK START *****************/
	String id;
	String firstName;
	String lastName;
	String userName;
	String created;
	String status;
	String groupId;
	String cabId;
	String email;
	String gender, phone, user_image, group_id, coporate_id, driverName, age,
	contact_no, license_code, cab_id, pob_status, post_check_in,
	cab_provider_id;
	String DriverID, cabProvider;
	

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String value) {
		this.email = value;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getUserImage() {
		return user_image;
	}

	public void setUserImage(String userImage) {
		this.user_image = userImage;
	}

	public String getGroupID() {
		return group_id;
	}

	public void setGroupID(String groupID) {
		this.group_id = groupID;
	}

	public String getCoporateID() {
		return coporate_id;
	}

	public void setCoporateID(String coporateID) {
		this.coporate_id = coporateID;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getContactNumber() {
		return contact_no;
	}

	public void setContactNumber(String contactNumber) {
		this.contact_no = contactNumber;
	}

	public String getLicenseCode() {
		return license_code;
	}

	public void setLicenseCode(String licenseCode) {
		this.license_code = licenseCode;
	}

	public String getPobStatus() {
		return pob_status;
	}

	public void setPobStatus(String pobStatus) {
		this.pob_status = pobStatus;
	}

	public String getPostCheckIn() {
		return post_check_in;
	}

	public void setPostCheckIn(String postCheckIn) {
		this.post_check_in = postCheckIn;
	}

	public String getCabProviderID() {
		return cab_provider_id;
	}

	public void setCabProviderID(String cabProviderID) {
		this.cab_provider_id = cabProviderID;
	}

	
	public void setDriverRating(String driverRating) {
		this.driver_rating = driverRating;
	}

//	public class UserDeserializer implements JsonDeserializer<User> {
//
//		@Override
//		public User deserialize(JsonElement jsonElement, Type type,
//				JsonDeserializationContext context) throws JsonParseException {
//			JsonObject rootObj = jsonElement.getAsJsonObject();
//
//			User user = new User();
//
//			user.id = JsonHelper.getString(rootObj, APIConstants.KEY_ID);
//			user.DriverID = JsonHelper.getString(rootObj,
//					APIConstants.KEY_USER_ID);
//			user.license_code = JsonHelper.getString(rootObj,
//					APIConstants.KEY_LICENSE_CODE);
//
//			user.age = JsonHelper.getString(rootObj, APIConstants.KEY_AGE);
//			user.gender = JsonHelper
//					.getString(rootObj, APIConstants.KEY_GENDER);
//			user.contact_no = JsonHelper.getString(rootObj,
//					APIConstants.KEY_CONTACT_NO);
//
//			user.userName = JsonHelper
//					.getString(rootObj, APIConstants.KEY_NAME);
//			user.pob_status = JsonHelper.getString(rootObj,
//					APIConstants.KEY_POB_STATUS);
//			user.post_check_in = JsonHelper.getString(rootObj,
//					APIConstants.KEY_POST_CHECK_IN);
//
//			user.cabId = JsonHelper.getString(rootObj, APIConstants.KEY_CAB_ID);
//			user.user_image = JsonHelper.getString(rootObj,
//					APIConstants.KEY_USER_IMAGE);
//			user.driver_rating = JsonHelper.getString(rootObj,
//					APIConstants.KEY_DRIVER_RATING);
//			user.cab_provider_id = JsonHelper.getString(rootObj,
//					APIConstants.KEY_USER_PROVIDER_ID);
//			return user;
//		}
//
//	}

	public String getDriverID() {
		return DriverID;
	}

	public void setDriverID(String driverID) {
		DriverID = driverID;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCabID() {
		return cabId;
	}

	public void setCabID(String cabID) {
		this.cabId = cabID;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}


	private String json;
	private String userType = "driver";
	private String corporateId = "";
	private String corporateName = "";
	private List<String> journeyTypes;

	public void setJSON(String response) {
		json = response;
	}

	public String getJSON() {
		return this.json;
	}

	public String getFullName() {
		return this.firstName + " " + this.lastName;
	}

	public User deserializeUserObjectFromJSON(JsonObject userObject) {
		User user = new User();
		try {

			JsonObject rootObj = userObject.getAsJsonObject();

			user.setFirstName(JsonHelper.getString(rootObj,
					APIConstants.KEY_FIRST_NAME));
			user.setLastName(JsonHelper.getString(rootObj,
					APIConstants.KEY_LAST_NAME));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return user;
	}

	public void setCabProvider(String name) {
		this.cabProvider = name;
	}
	public String getCabProvider(){return this.cabProvider;}

	public void setUserType(String userType) {
		this.userType  = userType;
		
	}
	
	public String getUserType()
	{
		return this.userType;
	}

	public void setCorporateId(String corporateId) {
		this.corporateId  = corporateId;
		
	}
	
	public String getCorporateId()
	{
		return this.corporateId;
	}

	public void setCorporateName(String corporateName) {
		this.corporateName  = corporateName;
		
	}
	
	public String getCorporateName()
	{
		return this.corporateName;
	}

	public void setJourneyTypes(List<String> journeyTypes) {
		this.journeyTypes = journeyTypes;
		
	}
	
	public List<String> getJourneyTypes()
	{
		return this.journeyTypes;
	}

}
