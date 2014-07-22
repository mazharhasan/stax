package com.smarttaxi.driver.constants;

import android.content.Context;
import android.content.Intent;

public final class APIConstants {

	public static final int GROUP_ID = 6;
	public static final int SUCESS_CODE = 0;
	public static final int UPDATED_CODE = 5401;

	public static final String API_END_POINT = "http://smarttaxi.ca/services/stax/user/api/";
	// public static final String API_END_POINT =
	// "http://technyxsystems.com/demo/stax/user/api/";
	public static final String METHOD_GET_DIRECT_PROFILE = "get_profile.json";
	public static final String METHOD_CREATE_DRIVER_JOURNEY = "create_driver_journey.json";
	public static final String METHOD_UPDATE_DRIVE_LOCATION = "add_driver_location.json";
	public static final String METHOD_LOGIN = "verify_login.json";
	public static final String METHOD_SIGNUP = "verify_signup.json";
	public static final String METHOD_VERIFY_ACCOUNT = "verify_account.json";
	public static final String METHOD_GET_CABS_AROUND_ME = "get_cabs_around_me.json";
	public static final String METHOD_PICKUP_REQUEST = "pickup_request.json";
	public static final String METHOD_ACCEPT_REJECT_JOURNEY = "send_reject_journey_request.json";
	public static final String METHOD_GET_JOURNEY_DETAILS = "get_journey_details.json";
	public static final String METHOD_SEND_BEEP_NOTIFICATION = "send_beep_notification.json";
	public static final String METHOD_CANCEL_JOURNEY = "cancel_journey.json";
	public static final String METHOD_END_JOURNEY = "end_journey.json";
	public static final String METHOD_UPDATE_JOURNEY_STATUS_COMPLETE = "update_journey_status_complete.json";
	public static final String METHOD_POST_REGISTRATION_ID = "add_udid.json";
	public static final String METHOD_CURRENT_JOURNEY = "get_driver_current_journeys.json";
	public static final String METHOD_LOGOUT = "remove_udid.json";

	public static String PASSWORD_SALT = "Cakephpframework";
	public static final String DRIVER_USER_ID = "user_id";
	public static final String USER_GROUP_ID = "group_id";
	public static final String KEY_POST_CHECK_IN_STATUS = "post_check_in_status";
	public static final String KEY_CAB_STATUS = "cab_status";

	public static final String KEY_HEADER = "header";
	public static final String KEY_BODY = "body";
	public static final String KEY_CODE = "code";
	public static final String KEY_MESSAGE = "message";

	public static final String KEY_USER = "User";
	public static final String KEY_USER_ID_TO = "user_id_to";
	public static final String KEY_DRIVER_ID_FROM = "user_id_from"; // TODO
	public static final String KEY_USERNAME = "username";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_FIRST_NAME = "first_name";
	public static final String KEY_LAST_NAME = "last_name";
	public static final String KEY_EMAIL = "email";
	public static final String KEY_CONFIRM_PASSWORD = "confirm_password";
	public static final String KEY_GROUP_ID = "group_id";
	public static final String KEY_FILE = "file";
	public static final String KEY_VERIFICATION_CODE = "verification_code";
	public static final String KEY_ID = "id";

	/* Driver Info */
	public static final String KEY_DRIVER_INFO_ID = "id";
	public static final String KEY_DRIVER_INFO_NAME = "name";
	public static final String KEY_DRIVER_INFO_AGE = "age";
	public static final String KEY_DRIVER_INFO_GENDER = "gender";
	public static final String KEY_DRIVER_INFO_CONTACT = "contact_no";
	public static final String KEY_DRIVER_INFO_LICENSE = "license_code";
	public static final String KEY_DRIVER_INFO_USER_ID = "user_id";
	public static final String KEY_DRIVER_INFO_CAB_ID = "cab_id";
	public static final String KEY_DRIVER_INFO_POB_STATUS = "pob_status";
	public static final String KEY_DRIVER_INFO_POST_CHECK_IN = "post_check_in";
	public static final String KEY_DRIVER_INFO_CAB_PROVIDER_ID = "cab_provider_id";
	public static final String KEY_DRIVER_INFO_IMAGE_URL = "image_url";
	public static final String KEY_DRIVER_INFO_DISPATCHER_ID = "dispatcher_id";
	public static final String KEY_DRIVER_INFO_DRIVER_RATING = "driver_rating";

	public static final String KEY_PICK_REQ_JOURENY_ID = "journey_id";
	public static final String KEY_PICK_REQ_USER_ID = "user_id";
	public static final String KEY_PICK_REQ_USERNAME = "UserName";
	public static final String KEY_PICK_REQ_USER_IMAGE = "UserImage";
	public static final String KEY_PICK_REQ_GROUP_ID = "group_id";
	public static final String KEY_PICK_REQ_CORPORATE_NAME = "corporateName";
	public static final String KEY_PICK_REQ_PICK_UP_TIME = "PickupTime";
	public static final String KEY_PICK_REQ_PICK_UP_LOCATION = "PickupLocation";
	public static final String KEY_PICK_REQ_ADDITIONAL_MESSAGE = "AdditionalMessage";

	public static final String KEY_ACCEPT_REJECT_JOURENY_ID = "journey_id";
	public static final String KEY_ACCEPT_REJECT_STATUS = "status";
	public static final String KEY_ACCEPT_REJECT_DRIVER_USER_ID = "driver_user_id";

	public static final String KEY_GCM_USER_ID = "user_id";
	public static final String KEY_GCM_UDID = "udid";
	public static final String KEY_GCM_DEVICE_TYPE = "device_type";
	public static final String KEY_DEVICE_TYPE = "android";

	public static final String KEY_DETAIL_JOURNEY_JOURENY_ID = "journey_id";

	public static final String KEY_DETAIL_JOURNEYS = "journeys";
	public static final String KEY_DETAIL_JOURNEYS_ID = "id";
	public static final String KEY_DETAIL_JOURNEYS_CAB_ID = "cab_id";
	public static final String KEY_DETAIL_JOURNEYS_CAB_PROVIDER_ID = "cab_provider_id";
	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_OPTION = "journey_option";
	public static final String KEY_DETAIL_JOURNEYS_STATUS = "status";
	public static final String KEY_DETAIL_JOURNEYS_NOTIFICATION_STATUS = "notification_status";
	public static final String KEY_DETAIL_JOURNEYS_CREATED = "created";

	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_USERS = "journey_users";
	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_USERS_ID = "id";
	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_USERS_USER_ID = "user_id";
	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_USERS_JOURNEY_ID = "journey_id";
	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_USERS_MAX_NO_PASSENGER = "max_no_of_passengers";
	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_USERS_PICKUP_ADDRESS = "pickup_door_address";
	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_USERS_PICKUP_LAT = "pickup_lat";
	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_USERS_PICKUP_LNG = "pickup_lng";
	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_USERS_DROP_ADDRESS = "dropOff_door_address";
	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_USERS_DROP_LAT = "dropOff_lat";
	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_USERS_DROP_LNG = "dropOff_lng";
	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_USERS_PICKUP_TIME = "pickup_time";
	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_USERS_DROPOFF_TIME = "dropOff_time";
	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_USERS_NO_OF_BAGS = "no_of_bags";
	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_USERS_CHILD_SEATS = "child_seats";
	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_USERS_STATUS = "status";
	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_USERS_TIP_GIVEN = "tip_given";
	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_USERS_EXTRA_AMOUNT = "extra_amount";
	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_USERS_EXTRAS = "extras";
	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_USERS_OPTIONAL_MESSAGE = "optional_message";
	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_USERS_JOURNEY_TYPE = "journey_type";
	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_USERS_CANCEL_MESSAGE = "cancel_message";
	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_USERS_CREATED = "created";

	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_USERS_USERS = "users";
	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_USERS_USERS_ID = "id";
	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_USERS_USERS_FIRST_NAME = "first_name";
	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_USERS_USERS_LAST_NAME = "last_name";
	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_USERS_USERS_USERNAME = "username";
	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_USERS_USERS_PASSWORD = "password";
	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_USERS_USERS_GENDER = "gender";
	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_USERS_USERS_PHONE = "phone";
	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_USERS_USERS_USER_IMAGE = "user_image";
	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_USERS_USERS_GROUPD_ID = "group_id";
	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_USERS_USERS_TIP = "tip";
	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_USERS_USERS_IPADDRESS = "ip_address";
	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_USERS_USERS_CREATED = "created";
	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_USERS_USERS_MODIFIED = "modified";
	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_USERS_USERS_STATUS = "status";
	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_USERS_USERS_CORPORATE_ID = "corporate_id";
	public static final String KEY_DETAIL_JOURNEYS_JOURNEY_USERS_USERS_CONVERSATION_GROUP_ID = "conversation_group_id";

	public static final String KEY_STATUS = "status";
	public static final String KEY_CREATED = "created";
	public static final String KEY_FRIENDS = "friends";
	public static final String KEY_USER_LAT = "user_lat";
	public static final String KEY_USER_LONG = "user_lng";
	public static final String KEY_CABS = "Cabs";
	public static final String KEY_DRIVERS = "Drivers";
	public static final String KEY_CAB_PROVIDER = "cab_provider";
	public static final String KEY_CAB_PROVIDER_ID = "cab_provider_id";
	public static final String KEY_IMAGE_URL = "image_url";
	public static final String KEY_COLOR = "color";
	public static final String KEY_MAKE = "make";
	public static final String KEY_MODEL = "model";
	public static final String KEY_LATITUDE = "latitude";
	public static final String KEY_LONGITUDE = "longitude";
	public static final String KEY_AVG_RATING = "avgRating";
	public static final String KEY_PUNCTUALITY = "punctuality";
	public static final String KEY_CAB_CONDITION = "cab_condition";
	public static final String KEY_SERVICE = "service";
	public static final String KEY_NAME = "name";
	public static final String KEY_AGE = "age";
	public static final String KEY_GENDER = "gender";
	public static final String KEY_LICENSE_CODE = "license_code";
	public static final String KEY_USER_ID = "user_id";
	public static final String KEY_CAB_ID = "cab_id";
	public static final String KEY_DISPATCHER_ID = "dispatcher_id";
	public static final String KEY_PRICE = "price";
	public static final String KEY_DESCRIPTION = "description";
	public static final String KEY_SEATS_AVAILABLE = "seats_avalaible";
	public static final String KEY_CHILD_SEATS_AVAILABLE = "child_seats_available";
	public static final String KEY_LUGGAGE_CAPCITY = "luggage_capacity";
	public static final String KEY_CHILD_SEATS = "child_seats";
	public static final String KEY_JOURNEY_OPTION = "solo";
	public static final String KEY_MAX_NO_OF_PASSENGERS = "max_no_of_passengers";
	public static final String KEY_NO_OF_BAGS = "no_of_bags";
	public static final String KEY_PAYMENT_DETAIL = "payment_detaiL";
	public static final String KEY_PAYMENT_OPTION = "payment_option";
	public static final String KEY_PICKUP_ADDRESS = "pick_address";
	public static final String KEY_PICKUP_LAT = "pickup_lat";
	public static final String KEY_PICKUP_LNG = "pickup_lng";
	public static final String KEY_PICKUP_TIME = "pickup_time";
	public static final String KEY_JOURNEY_ID = "journey_id";
	public static final String KEY_CANCELLED_BY = "cancelled_by";
	public static final String KEY_CANCELLED_MESSAGE = "cancel_message";

	public static final String KEY_DROPOFF_LAT = "dropOfflat";
	public static final String KEY_DROPOFF_LNG = "dropOfflng";

	public static final String KEY_HTTP_STATUS_CODE = "StatusCode";
	public static final String KEY_HTTP_RESPONSE_MSG = "ResponseMessage";
	public static final String KEY_HTTP_RESPONSE = "Response";
	public static final String KEY_DRIVER_INFORMATION = "driver_information";
	public static final String KEY_PICKUP_REQUEST = "pickup_request";

	public static final String KEY_DRIVERID = "driver_id";
	public static final String KEY_CABID = "cab_id";
	public static final String KEY_LAT = "lat";
	public static final String KEY_LNG = "lng";
	public static final String METHOD_SET_DRIVE_STATUS = "set_driver_status.json";
	public static final String KEY_DRIVER_STATUS_TYPE = "type";

	/*********** Salik Start *********/

	/*
	 * public static final String KEY_HTTP_STATUS_CODE = "StatusCode"; public
	 * static final String KEY_HTTP_RESPONSE_MSG = "ResponseMessage"; public
	 * static final String KEY_HTTP_RESPONSE = "Response"; public static final
	 * String KEY_DRIVER_INFORMATION = "driver_information"; public static final
	 * String KEY_DRIVERID = "driver_id"; public static final String KEY_CABID =
	 * "cab_id"; public static final String KEY_LAT = "lat"; public static final
	 * String KEY_LNG = "lng"; public static final String
	 * METHOD_SET_DRIVE_STATUS = "set_driver_status.json"; public static final
	 * String KEY_DRIVER_STATUS_TYPE = "type";
	 */
	public static String METHOD_FORGOT_PASSWORD = "forgot_password.json";
	public static final String METHOD_CHANGE_PASSWORD = "update_password.json";
	public static final String KEY_OLDPASSWORD = "old_password";
	public static final String KEY_DRIVER_ID = "user_id_from"; // TODO
	public static final String KEY_CONTACT_NO = "contact_no";
	public static final String KEY_POB_STATUS = "pob_status";
	public static final String KEY_POST_CHECK_IN = "post_check_in";
	public static final String KEY_USER_IMAGE = "image_url";
	public static final String KEY_DRIVER_RATING = "driver_rating";
	public static final String KEY_USER_PROVIDER_ID = "cab_provider_id";
	public static final String KEY_PHONE = "phone";
	public static final String METHOD_UPDATE_PROFILE = "update_profile.json";
	public static String METHOD_DRIVER_CABS = "get_driver_cabs.json";
	public static String KEY_USER_DRIVER_ID = "driver_user_id";
	public static String KEY_CAB_NO = "cab_no";
	public static String KEY_DRIVER_ASSIGNED = "driver_assigned";
	public static String METHOD_ASSIGN_CAB_TO_DRIVER = "assign_driver_to_cab.json";
	public static String KEY_TYPE = "type";
	public static String METHOD_GET_DRIVER_CAB = "get_cab_information.json";
	public static String METHOD_GET_TRIP_HISTORY = "get_driver_past_journeys.json";
	public static String KEY_JOURNEYS = "journeys";
	public static String KEY_TIME_FROM = "time_from";
	public static String KEY_TIME_TO = "time_to";
	/*********** Salik End *********/
	
}
