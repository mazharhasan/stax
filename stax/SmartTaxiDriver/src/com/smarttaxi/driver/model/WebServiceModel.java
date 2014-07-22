package com.smarttaxi.driver.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.http.NameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.smarttaxi.driver.constants.APIConstants;
import com.smarttaxi.driver.entities.AcceptOrRejectJourney;
import com.smarttaxi.driver.entities.Cab;
import com.smarttaxi.driver.entities.CabProvider;
import com.smarttaxi.driver.entities.CustomHttpException;
import com.smarttaxi.driver.entities.CustomHttpResponse;
import com.smarttaxi.driver.entities.DeviceRegister;
import com.smarttaxi.driver.entities.Driver;
import com.smarttaxi.driver.entities.Journey;
import com.smarttaxi.driver.entities.LogoutRequest;
import com.smarttaxi.driver.entities.PickupRequest;
import com.smarttaxi.driver.entities.User;
import com.smarttaxi.driver.helpers.HttpRequestHelper;
import com.smarttaxi.driver.helpers.HttpRequestHelper2;
import com.smarttaxi.driver.helpers.JsonHelper;
import com.smarttaxi.driver.helpers.LoaderHelper;
import com.smarttaxi.driver.interfaces.HttpProgressListener;
import com.smarttaxi.driver.interfaces.HttpRequestListener;
import com.smarttaxi.driver.interfaces.HttpResponseListener;
import com.smarttaxi.driver.preferences.PreferencesHandler;
import com.smarttaxi.driver.utils.Applog;
import com.smarttaxi.driver.utils.HttpAsyncTask;
import com.smarttaxi.driver.utils.Response;
import com.smarttaxi.driver.utils.Utils;

public class WebServiceModel {

	private static Response webResponse;

	public static boolean isErrorCode(int statusCode) {

		if (statusCode == 400 || statusCode == 401 || statusCode == 403
				|| statusCode == 404 || statusCode == 405 || statusCode == 500
				|| statusCode == 1061 || statusCode == 1010
				|| statusCode == 5402)
			return true;
		else
			return false;
	}

	public static HttpAsyncTask logoutCurrentUser(final String driverUserId,
			final String userGroupId, HttpResponseListener responseListener) {
		final String methodName = APIConstants.METHOD_LOGOUT;
		final String url = getCompleteURLForMethod(methodName);
		webResponse = new Response();
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(responseListener,
				new HttpRequestListener() {

			
			
					@Override
					public Object makeRequest(HttpAsyncTask httpAsyncTask)
							throws CustomHttpException {
						if (httpAsyncTask.isCancelled())
							return null;

						LogoutRequest logoutRequest = new LogoutRequest();

						List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
						params.add(new BasicNameValuePair(
								APIConstants.DRIVER_USER_ID, Utils
										.validateEmptyString(driverUserId)));
						params.add(new BasicNameValuePair(
								APIConstants.USER_GROUP_ID, Utils
										.validateEmptyString(userGroupId)));

						try {

							String response = HttpRequestHelper2.httpPost(url,
									params);

							if (Utils.isEmptyOrNull(response)) {
								throw new Exception();
							}

							String responseMessage = getMessageFromResponseHeader(response);
							int responseCode = getCodeFromResponseHeader(response);
							boolean status = (responseCode == APIConstants.SUCESS_CODE);
							if (isErrorCode(responseCode)) {
								throw new Exception(responseMessage);
							} else if (status) {
								// JsonObject pickObject =
								// getJsonObjectFromBody(
								// response, "");
								// logoutRequest.deserializeFromJSON(pickObject);
							}

							return new CustomHttpResponse(logoutRequest,
									methodName, responseCode, responseMessage);
						} catch (Exception e) {
							e.printStackTrace();
							return new CustomHttpException(methodName, e
									.getMessage());
						}
					}
				});

		return executeAsyncTask(httpAsyncTask);
	}

	public static HttpAsyncTask postRegistrationIdToServer(
			final String registrationId, final String userID,
			HttpResponseListener responseListener) {

		final String methodName = APIConstants.METHOD_POST_REGISTRATION_ID;
		final String url = getCompleteURLForMethod(methodName);
		webResponse = new Response();
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(responseListener,
				new HttpRequestListener() {

					@Override
					public Object makeRequest(HttpAsyncTask httpAsyncTask)
							throws CustomHttpException {
						if (httpAsyncTask.isCancelled())
							return null;

						DeviceRegister register = new DeviceRegister();

						List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();

						params.add(new BasicNameValuePair(
								APIConstants.KEY_GCM_USER_ID, Utils
										.validateEmptyString(userID)));

						params.add(new BasicNameValuePair(
								APIConstants.KEY_GCM_UDID, Utils
										.validateEmptyString(registrationId)));

						params.add(new BasicNameValuePair(
								APIConstants.KEY_GCM_DEVICE_TYPE,
								Utils.validateEmptyString(APIConstants.KEY_DEVICE_TYPE)));

						try {

							String response = HttpRequestHelper2.httpPost(url,
									params);
							if (Utils.isEmptyOrNull(response)) {
								throw new Exception();
							}

							String responseMessage = getMessageFromResponseHeader(response);
							int responseCode = getCodeFromResponseHeader(response);
							boolean status = (responseCode == APIConstants.SUCESS_CODE)
									|| (responseCode == APIConstants.UPDATED_CODE);
							if (isErrorCode(responseCode)) {
								throw new Exception(responseMessage);
							} else if (status) {

								register.deserializeFromJSON(response);

								/*
								 * JSONObject userObject1 = new
								 * JSONObject(response); JSONObject user3 =
								 * userObject1.getJSONObject(cirrentJourneyId);
								 */
							}

							return new CustomHttpResponse(register, methodName,
									responseCode, responseMessage);
						} catch (Exception e) {
							e.printStackTrace();
							return new CustomHttpException(methodName, e
									.getMessage());
						}
					}
				});
		return executeAsyncTask(httpAsyncTask);

	}

	public static HttpAsyncTask getJourneyDetails(
			final String currentJourneyId, HttpResponseListener responseListener) {

		// final String _currentId= currentJourneyId;
		final String methodName = APIConstants.METHOD_GET_JOURNEY_DETAILS;
		final String url = getCompleteURLForMethod(methodName);
		webResponse = new Response();
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(responseListener,
				new HttpRequestListener() {

					@Override
					public Object makeRequest(HttpAsyncTask httpAsyncTask)
							throws CustomHttpException {
						if (httpAsyncTask.isCancelled())
							return null;

						Journey journey = new Journey();

						List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
						params.add(new BasicNameValuePair(
								APIConstants.KEY_DETAIL_JOURNEY_JOURENY_ID,
								Utils.validateEmptyString(currentJourneyId)));

						try {

							String response = HttpRequestHelper2.httpPost(url,
									params);
							if (Utils.isEmptyOrNull(response)) {
								throw new Exception();
							}

							String responseMessage = getMessageFromResponseHeader(response);
							int responseCode = getCodeFromResponseHeader(response);
							boolean status = (responseCode == APIConstants.SUCESS_CODE);
							if (isErrorCode(responseCode)) {
								throw new Exception(responseMessage);
							} else if (status) {
								JsonObject userObject = getJsonObjectFromBody(
										response,
										APIConstants.KEY_DETAIL_JOURNEYS)
										.getAsJsonObject(currentJourneyId);

								journey.deserializeFromJSON(userObject);

								/*
								 * JSONObject userObject1 = new
								 * JSONObject(response); JSONObject user3 =
								 * userObject1.getJSONObject(cirrentJourneyId);
								 */
							}

							return new CustomHttpResponse(journey, methodName,
									responseCode, responseMessage);
						} catch (Exception e) {
							e.printStackTrace();
							return new CustomHttpException(methodName, e
									.getMessage());
						}
					}
				});
		return executeAsyncTask(httpAsyncTask);
	}

	public static HttpAsyncTask callAccepted(final String status,
			final String driverUserId, final String CurrentJourneyId,
			HttpResponseListener responseListener) {

		final String methodName = APIConstants.METHOD_ACCEPT_REJECT_JOURNEY;
		final String url = getCompleteURLForMethod(methodName);
		webResponse = new Response();
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(responseListener,
				new HttpRequestListener() {

					@Override
					public Object makeRequest(HttpAsyncTask httpAsyncTask)
							throws CustomHttpException {
						if (httpAsyncTask.isCancelled())
							return null;

						// User user = new User();
						// PickupRequest pick = new PickupRequest();
						AcceptOrRejectJourney accptJourney = new AcceptOrRejectJourney();

						List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();

						params.add(new BasicNameValuePair(
								APIConstants.KEY_ACCEPT_REJECT_STATUS, Utils
										.validateEmptyString(status)));

						// hard coded
						params.add(new BasicNameValuePair(
								APIConstants.KEY_ACCEPT_REJECT_DRIVER_USER_ID,
								Utils.validateEmptyString(driverUserId)));

						params.add(new BasicNameValuePair(
								APIConstants.KEY_ACCEPT_REJECT_JOURENY_ID,
								Utils.validateEmptyString(CurrentJourneyId)));

						try {

							String response = HttpRequestHelper2.httpPost(url,
									params);

							if (Utils.isEmptyOrNull(response)) {
								throw new Exception();
							}

							String responseMessage = getMessageFromResponseHeader(response);
							int responseCode = getCodeFromResponseHeader(response);
							boolean status = (responseCode == APIConstants.SUCESS_CODE);
							if (isErrorCode(responseCode)) {
								throw new Exception(responseMessage);
							} else if (status) {

								/*
								 * JsonObject pickObject =
								 * getJsonObjectFromBody( response,
								 * APIConstants.KEY_BODY);
								 * accptJourney.deserializeFromJSON(pickObject);
								 */
								accptJourney.deserializeFromJSON(response);
							}

							return new CustomHttpResponse(accptJourney,
									methodName, responseCode, responseMessage);
						}

						catch (Exception e) {
							e.printStackTrace();
							return new CustomHttpException(methodName, e
									.getMessage());
						}
					}
				});

		return executeAsyncTask(httpAsyncTask);

	}

	public static HttpAsyncTask getPickupRequest(final String journeyId,
			HttpResponseListener responseListener) {
		final String methodName = APIConstants.METHOD_PICKUP_REQUEST;
		final String url = getCompleteURLForMethod(methodName);
		webResponse = new Response();
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(responseListener,
				new HttpRequestListener() {

					@Override
					public Object makeRequest(HttpAsyncTask httpAsyncTask)
							throws CustomHttpException {
						if (httpAsyncTask.isCancelled())
							return null;

						PickupRequest pick = new PickupRequest();

						List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
						params.add(new BasicNameValuePair(
								APIConstants.KEY_PICK_REQ_JOURENY_ID, Utils
										.validateEmptyString(journeyId)));

						try {

							String response = HttpRequestHelper2.httpPost(url,
									params);

							if (Utils.isEmptyOrNull(response)) {
								throw new Exception();
							}

							String responseMessage = getMessageFromResponseHeader(response);
							int responseCode = getCodeFromResponseHeader(response);
							boolean status = (responseCode == APIConstants.SUCESS_CODE);
							if (isErrorCode(responseCode)) {
								throw new Exception(responseMessage);
							} else if (status) {
								JsonObject pickObject = getJsonObjectFromBody(
										response,
										APIConstants.KEY_PICKUP_REQUEST);
								pick.deserializeFromJSON(pickObject);
							}

							return new CustomHttpResponse(pick, methodName,
									responseCode, responseMessage);
						} catch (Exception e) {
							e.printStackTrace();
							return new CustomHttpException(methodName, e
									.getMessage());
						}
					}
				});

		return executeAsyncTask(httpAsyncTask);
	}

	public static HttpAsyncTask login(final String userName,
			final String password, HttpResponseListener responseListener) {
		final String methodName = APIConstants.METHOD_LOGIN;
		final String url = getCompleteURLForMethod(methodName);
		webResponse = new Response();
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(responseListener,
				new HttpRequestListener() {

					@Override
					public Object makeRequest(HttpAsyncTask httpAsyncTask)
							throws CustomHttpException {
						if (httpAsyncTask.isCancelled())
							return null;

						User user = new User();

						List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
						params.add(new BasicNameValuePair(
								APIConstants.KEY_USERNAME, Utils
										.validateEmptyString(userName)));
						params.add(new BasicNameValuePair(
								APIConstants.KEY_PASSWORD, Utils
										.validateEmptyString(password)));

						try {

							String response = HttpRequestHelper2.httpPost(url,
									params);

							if (Utils.isEmptyOrNull(response)) {
								throw new Exception();
							}

							String responseMessage = getMessageFromResponseHeader(response);
							int responseCode = getCodeFromResponseHeader(response);
							boolean status = (responseCode == APIConstants.SUCESS_CODE);
							if (isErrorCode(responseCode)) {
								throw new Exception(responseMessage);
							} else if (status) {
								JsonObject driverObject = getJsonObjectFromBody(
										response,
										APIConstants.KEY_DRIVER_INFORMATION);
								user.deserializeFromJSON(driverObject);

								JsonObject userObject = getJsonObjectFromBody(
										response, APIConstants.KEY_USER);
								User userinfo = new User();
								userinfo = userinfo
										.deserializeUserObjectFromJSON(userObject);

								JsonObject cabObject = getJsonObjectFromBody(
										response, APIConstants.KEY_CAB_PROVIDER);

								CabProvider cab = new CabProvider();
								cab.deserializeFromJSON(cabObject);

								user.setFirstName(userinfo.getFirstName());
								user.setLastName(userinfo.getLastName());
								user.setCabProvider(cab.getName());
								user.setJSON(response);
							}

							return new CustomHttpResponse(user, methodName,
									responseCode, responseMessage);
						} catch (Exception e) {
							e.printStackTrace();
							return new CustomHttpException(methodName, e
									.getMessage());
						}
					}
				});
		return executeAsyncTask(httpAsyncTask);
	}

	// not calling from any where
	public static HttpAsyncTask signup(final String firstName,
			final String lastName, final String email, final String password,
			final String groupID, final Bitmap bitmap, final String fileName,
			HttpResponseListener responseListener) {

		final String methodName = APIConstants.METHOD_SIGNUP;
		final String url = getCompleteURLForMethod(methodName);
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(responseListener,
				new HttpRequestListener() {

					@Override
					public Object makeRequest(HttpAsyncTask httpAsyncTask)
							throws CustomHttpException {
						if (httpAsyncTask.isCancelled())
							return null;

						List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
						params.add(new BasicNameValuePair(
								APIConstants.KEY_FIRST_NAME, Utils
										.validateEmptyString(firstName)));
						params.add(new BasicNameValuePair(
								APIConstants.KEY_LAST_NAME, Utils
										.validateEmptyString(lastName)));
						params.add(new BasicNameValuePair(
								APIConstants.KEY_EMAIL, Utils
										.validateEmptyString(email)));
						params.add(new BasicNameValuePair(
								APIConstants.KEY_CONFIRM_PASSWORD, Utils
										.validateEmptyString(password)));
						params.add(new BasicNameValuePair(
								APIConstants.KEY_GROUP_ID, Utils
										.validateEmptyString(groupID)));
						params.add(new BasicNameValuePair(
								APIConstants.KEY_PASSWORD, Utils
										.validateEmptyString(password)));

						try {
							Applog.Debug("url " + url);
							String response = HttpRequestHelper2.httpPost(url,
									params);// tWithImage(url, params, bitmap,
											// fileName);
							Applog.Debug("Res " + response);
							if (Utils.isEmptyOrNull(response)) {
								throw new Exception();
							}

							String responseMessage = getMessageFromResponseHeader(response);
							int responseCode = getCodeFromResponseHeader(response);

							if (isErrorCode(responseCode)) {
								throw new Exception(responseMessage);
							}

							return new CustomHttpResponse(response, methodName,
									200, responseMessage);
						} catch (Exception e) {
							e.printStackTrace();
							return new CustomHttpException(methodName, e
									.getMessage());
						}
					}
				});
		return executeAsyncTask(httpAsyncTask);
	}

	// not calling from any where
	public static HttpAsyncTask verifyAccount(final String verificationCode,
			final String email, HttpResponseListener responseListener) {
		final String methodName = APIConstants.METHOD_VERIFY_ACCOUNT;
		final String url = getCompleteURLForMethod(methodName);
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(responseListener,
				new HttpRequestListener() {

					@Override
					public Object makeRequest(HttpAsyncTask httpAsyncTask)
							throws CustomHttpException {
						if (httpAsyncTask.isCancelled())
							return null;

						List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
						params.add(new BasicNameValuePair(
								APIConstants.KEY_VERIFICATION_CODE, Utils
										.validateEmptyString(verificationCode)));
						params.add(new BasicNameValuePair(
								APIConstants.KEY_EMAIL, Utils
										.validateEmptyString(email)));

						try {
							String response = HttpRequestHelper2.httpPost(url,
									params);

							if (Utils.isEmptyOrNull(response)) {
								throw new Exception();
							}

							String responseMessage = getMessageFromResponseHeader(response);
							int responseCode = getCodeFromResponseHeader(response);

							if (isErrorCode(responseCode)) {
								throw new Exception(responseMessage);
							}

							return new CustomHttpResponse(response, methodName,
									200, responseMessage);
						} catch (Exception e) {
							e.printStackTrace();
							return new CustomHttpException(methodName, e
									.getMessage());
						}
					}
				});
		return executeAsyncTask(httpAsyncTask);
	}

	// not calling from any where
	public static HttpAsyncTask getCabsAroundMe(final LatLng latLng,
			HttpResponseListener responseListener,
			HttpProgressListener httpProgressListener) {
		final String methodName = APIConstants.METHOD_GET_CABS_AROUND_ME;
		final String url = getCompleteURLForMethod(methodName);
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(responseListener,
				new HttpRequestListener() {

					@Override
					public Object makeRequest(HttpAsyncTask httpAsyncTask)
							throws CustomHttpException {
						if (httpAsyncTask.isCancelled())
							return null;
						try {
							httpAsyncTask.updateProgress(1);
							List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
							params.add(new BasicNameValuePair(
									APIConstants.KEY_USER_LAT, String
											.valueOf(latLng.latitude)));
							httpAsyncTask.updateProgress(2);
							params.add(new BasicNameValuePair(
									APIConstants.KEY_USER_LONG, String
											.valueOf(latLng.longitude)));
							httpAsyncTask.updateProgress(3);

							String response = HttpRequestHelper2.httpPost(url,
									params);
							for (int i = 4; i <= 50; i++) {
								httpAsyncTask.updateProgress(i);
							}
							httpAsyncTask.updateProgress(55);
							if (Utils.isEmptyOrNull(response)) {
								throw new Exception();
							}
							httpAsyncTask.updateProgress(56);
							String responseMessage = getMessageFromResponseHeader(response);
							httpAsyncTask.updateProgress(57);
							int responseCode = getCodeFromResponseHeader(response);
							httpAsyncTask.updateProgress(58);
							httpAsyncTask.updateProgress(60);
							if (isErrorCode(responseCode)) {
								throw new Exception(responseMessage);
							}
							httpAsyncTask.updateProgress(61);
							ArrayList<Cab> cabsList = new ArrayList<Cab>();
							JsonObject cabsObject = getJsonObjectFromBody(
									response, APIConstants.KEY_CABS);
							httpAsyncTask.updateProgress(70);
							int count = cabsObject.entrySet().size();
							for (Map.Entry<String, JsonElement> entry : cabsObject
									.entrySet()) {
								JsonElement value = entry.getValue();
								Cab cab = new Cab();
								cab.deserializeFromJSON(value);
								cabsList.add(cab);
								int position = Integer.parseInt(entry.getKey());
								httpAsyncTask
										.updateProgress(70 + (int) ((position / (float) count) * 30));
							}
							httpAsyncTask.updateProgress(100);
							if (Utils.isEmptyOrNull(cabsList))
								throw new Exception("No Cabs found");
							return new CustomHttpResponse(cabsList, methodName,
									200, responseMessage);
						} catch (Exception e) {
							e.printStackTrace();
							return new CustomHttpException(methodName, e
									.getMessage());
						}
					}
				}, httpProgressListener);
		return executeAsyncTask(httpAsyncTask);
	}

	private static int getCodeFromResponseHeader(String response) {
		if (Utils.isEmptyOrNull(response))
			return -1;

		JsonObject mainObject = JsonHelper.parseToJsonObject(response);

		if (!mainObject.has(APIConstants.KEY_HEADER))
			return -1;

		JsonObject headerObject = JsonHelper.getJsonObject(mainObject,
				APIConstants.KEY_HEADER);

		if (!headerObject.has(APIConstants.KEY_CODE))
			return -1;

		return Integer.valueOf(JsonHelper.getString(headerObject,
				APIConstants.KEY_CODE));
	}

	private static String getMessageFromResponseHeader(String response) {
		if (Utils.isEmptyOrNull(response))
			return "";

		JsonObject mainObject = JsonHelper.parseToJsonObject(response);

		if (!mainObject.has(APIConstants.KEY_HEADER))
			return "";
		JsonObject headerObject = JsonHelper.getJsonObject(mainObject,
				APIConstants.KEY_HEADER);

		if (!headerObject.has(APIConstants.KEY_MESSAGE))
			return "";

		return JsonHelper.getString(headerObject, APIConstants.KEY_MESSAGE);
	}

	private static JsonObject getJsonObjectFromBody(String response, String key) {
		if (Utils.isEmptyOrNull(response))
			return null;

		JsonObject mainObject = JsonHelper.parseToJsonObject(response);

		if (!mainObject.has(APIConstants.KEY_BODY))
			return null;
		JsonObject bodyObject = JsonHelper.getJsonObject(mainObject,
				APIConstants.KEY_BODY);

		if (!bodyObject.has(key))
			return null;

		return JsonHelper.getJsonObject(bodyObject, key);
	}

	private static JsonObject getJsonObjectFromBody(String response) {
		if (Utils.isEmptyOrNull(response))
			return null;

		JsonObject mainObject = JsonHelper.parseToJsonObject(response);

		if (!mainObject.has(APIConstants.KEY_BODY))
			return null;
		JsonObject bodyObject = JsonHelper.getJsonObject(mainObject,
				APIConstants.KEY_BODY);

		if (bodyObject != null)
			return bodyObject;

		return null;
	}

	private static String getStringFromBody(String response) {
		// TODO Auto-generated method stub
		if (Utils.isEmptyOrNull(response))
			return "";

		JsonObject mainObject = JsonHelper.parseToJsonObject(response);

		if (!mainObject.has(APIConstants.KEY_BODY))
			return "";

		return JsonHelper.getString(mainObject, APIConstants.KEY_BODY);

	}

	@SuppressLint("NewApi")
	private static HttpAsyncTask executeAsyncTask(HttpAsyncTask httpAsyncTask) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			return (HttpAsyncTask) httpAsyncTask.execute();
		} else {
			return (HttpAsyncTask) httpAsyncTask
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}

	private static String getCompleteURLForMethod(String methodName) {
		return APIConstants.API_END_POINT + methodName;
	}

	public static HttpAsyncTask updateDriverLocation(final String id,
			final String cabID, final String lat, final String lng,
			HttpResponseListener responseListener) {
		final String methodName = APIConstants.METHOD_UPDATE_DRIVE_LOCATION;
		final String url = getCompleteURLForMethod(methodName);
		webResponse = new Response();
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(responseListener,
				new HttpRequestListener() {

					@Override
					public Object makeRequest(HttpAsyncTask httpAsyncTask)
							throws CustomHttpException {
						if (httpAsyncTask.isCancelled())
							return null;

						List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
						params.add(new BasicNameValuePair(
								APIConstants.KEY_DRIVERID, Utils
										.validateEmptyString(id)));
						params.add(new BasicNameValuePair(
								APIConstants.KEY_CABID, Utils
										.validateEmptyString(cabID)));
						params.add(new BasicNameValuePair(APIConstants.KEY_LAT,
								Utils.validateEmptyString(lat)));
						params.add(new BasicNameValuePair(APIConstants.KEY_LNG,
								Utils.validateEmptyString(lng)));
						try {
							Applog.Debug("driverID : " + id + " | cabID "
									+ cabID);
							String response = HttpRequestHelper2.httpPost(url,
									params);

							if (Utils.isEmptyOrNull(response)) {
								throw new Exception();
							}

							String responseMessage = getMessageFromResponseHeader(response);
							int responseCode = getCodeFromResponseHeader(response);
							boolean status = (responseCode == APIConstants.SUCESS_CODE);
							if (isErrorCode(responseCode)) {
								throw new Exception(responseMessage);
							} else if (status) {
								// TODO : SAVE DRIVER LOCATION to SQliteDB
							}

							return new CustomHttpResponse(status, methodName,
									responseCode, responseMessage);
						} catch (Exception e) {
							e.printStackTrace();
							return new CustomHttpException(methodName, e
									.getMessage());
						}
					}
				});
		return executeAsyncTask(httpAsyncTask);
	}

	public static HttpAsyncTask setDriverStatus(final String id,
			final String type, final String lat, final String lng,
			HttpResponseListener responseListener) {
		final String methodName = APIConstants.METHOD_SET_DRIVE_STATUS;
		final String url = getCompleteURLForMethod(methodName);
		webResponse = new Response();
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(responseListener,
				new HttpRequestListener() {
					Driver driver = new Driver();

					@Override
					public Object makeRequest(HttpAsyncTask httpAsyncTask)
							throws CustomHttpException {
						if (httpAsyncTask.isCancelled())
							return null;

						List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
						params.add(new BasicNameValuePair(
								APIConstants.KEY_DRIVERID, Utils
										.validateEmptyString(id)));
						params.add(new BasicNameValuePair(
								APIConstants.KEY_DRIVER_STATUS_TYPE, Utils
										.validateEmptyString(type)));
						params.add(new BasicNameValuePair(APIConstants.KEY_LAT,
								Utils.validateEmptyString(lat)));
						params.add(new BasicNameValuePair(APIConstants.KEY_LNG,
								Utils.validateEmptyString(lng)));
						try {
							Applog.Debug("driverID : " + id + " | type " + type);
							String response = HttpRequestHelper2.httpPost(url,
									params);

							if (Utils.isEmptyOrNull(response)) {
								throw new Exception();
							}

							String responseMessage = getMessageFromResponseHeader(response);
							int responseCode = getCodeFromResponseHeader(response);
							boolean status = (responseCode == APIConstants.SUCESS_CODE);
							if (isErrorCode(responseCode)) {
								throw new Exception(responseMessage);
							} else if (status) {
								/*
								 * JsonObject _driverObj =
								 * getJsonObjectFromBody( response,
								 * APIConstants.KEY_HEADER).getAsJsonObject();
								 */

								JsonObject tempObj = getJsonObjectFromBody(response);

								// String res= getStringFromBody(response);
								// JsonObject _driverObj =
								// getJsonObjectFromBody(responseMessage, key)
								driver.deserializeFromJSON(tempObj);

								// driver.deserializeFromJSON(res);
							}
							Applog.Debug(response);
							return new CustomHttpResponse(driver, methodName,
									responseCode, responseMessage);
						} catch (Exception e) {
							e.printStackTrace();
							return new CustomHttpException(methodName, e
									.getMessage());
						}
					}
				});
		return executeAsyncTask(httpAsyncTask);
	}

	public static HttpAsyncTask sendBeep(HttpResponseListener responseListener,
			final String user_id, final String driver_id, final String Message) {
		final String methodName = APIConstants.METHOD_SEND_BEEP_NOTIFICATION;
		final String url = getCompleteURLForMethod(methodName);
		
		Applog.Error(">>>>>> user_id= " + user_id + " driver_id=" + driver_id);
		webResponse = new Response();
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(responseListener,
				new HttpRequestListener() {

					@Override
					public Object makeRequest(HttpAsyncTask httpAsyncTask)
							throws CustomHttpException {
						if (httpAsyncTask.isCancelled())
							return null;

						List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
						params.add(new BasicNameValuePair(
								APIConstants.KEY_USER_ID_TO, Utils
										.validateEmptyString(user_id)));
						params.add(new BasicNameValuePair(
								APIConstants.KEY_DRIVER_ID_FROM, Utils
										.validateEmptyString(driver_id)));
						params.add(new BasicNameValuePair(
								APIConstants.KEY_MESSAGE, Utils
										.validateEmptyString(Message)));

						try {
							Applog.Debug(url);

							String response = HttpRequestHelper2.httpPost(url,
									params);
							// JsonObject jsonObject =
							// JsonHelper.parseToJsonObject(response);

							if (Utils.isEmptyOrNull(response)) {
								throw new Exception();
							}

							String responseMessage = getMessageFromResponseHeader(response);
							int responseCode = getCodeFromResponseHeader(response);
							boolean status = (responseCode == APIConstants.SUCESS_CODE);
							if (isErrorCode(responseCode)) {
								throw new Exception(responseMessage);
							} else if (status) {
								responseMessage = getStringFromBody(response);
							}
							return new CustomHttpResponse(status, methodName,
									responseCode, responseMessage);

						} catch (Exception e) {
							e.printStackTrace();
							return new CustomHttpException(methodName, e
									.getMessage());
						}
					}
				});
		/*
		 * Uri notification =
		 * RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		 * Ringtone r = RingtoneManager.getRingtone((Activity)responseListener,
		 * notification); r.play();
		 */
		return executeAsyncTask(httpAsyncTask);
	}

	public static HttpAsyncTask cancelJourney(
			HttpResponseListener responseListener, final String user_id,
			final String journey_id) {
		// TODO Auto-generated method stub

		final String methodName = APIConstants.METHOD_CANCEL_JOURNEY;
		final String url = getCompleteURLForMethod(methodName);
		webResponse = new Response();
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(responseListener,
				new HttpRequestListener() {

					@Override
					public Object makeRequest(HttpAsyncTask httpAsyncTask)
							throws CustomHttpException {
						if (httpAsyncTask.isCancelled())
							return null;

						List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
						params.add(new BasicNameValuePair(
								APIConstants.KEY_USER_ID, Utils
										.validateEmptyString(user_id)));
						params.add(new BasicNameValuePair(
								APIConstants.KEY_JOURNEY_ID, Utils
										.validateEmptyString(journey_id)));
						params.add(new BasicNameValuePair(
								APIConstants.KEY_CANCELLED_BY, Utils
										.validateEmptyString("driver")));
						params.add(new BasicNameValuePair(
								APIConstants.KEY_CANCELLED_MESSAGE, ""));

						try {
							// Applog.Debug("driverID : " + id + " | type " +
							// type);
							String response = HttpRequestHelper2.httpPost(url,
									params);

							if (Utils.isEmptyOrNull(response)) {
								throw new Exception();
							}

							String responseMessage = getMessageFromResponseHeader(response);
							int responseCode = getCodeFromResponseHeader(response);
							boolean status = (responseCode == APIConstants.SUCESS_CODE);
							if (isErrorCode(responseCode)) {
								throw new Exception(responseMessage);
							} else if (status) {
								responseMessage = getStringFromBody(response);
							}

							return new CustomHttpResponse(status, methodName,
									responseCode, responseMessage);
						} catch (Exception e) {
							e.printStackTrace();
							return new CustomHttpException(methodName, e
									.getMessage());
						}
					}
				});
		return executeAsyncTask(httpAsyncTask);
	}

	/*
	 * public static HttpAsyncTask endJourney(HttpResponseListener
	 * responseListener, final String journey_id, final String dropOfflat, final
	 * String dropOfflng, final String price, final String dropOfftime, final
	 * String dropOffAdd,final String tip,final String extraAmount,final String
	 * extras) { // TODO Auto-generated method stub
	 * 
	 * final String methodName = APIConstants.METHOD_END_JOURNEY; final String
	 * url = getCompleteURLForMethod(methodName); webResponse = new Response();
	 * HttpAsyncTask httpAsyncTask = new HttpAsyncTask(responseListener, new
	 * HttpRequestListener() {
	 * 
	 * @Override public Object makeRequest(HttpAsyncTask httpAsyncTask) throws
	 * CustomHttpException { if (httpAsyncTask.isCancelled()) return null;
	 * 
	 * List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
	 * params.add(new BasicNameValuePair(APIConstants.KEY_JOURNEY_ID,
	 * Utils.validateEmptyString(journey_id))); params.add(new
	 * BasicNameValuePair(APIConstants.KEY_DROPOFF_LAT,
	 * Utils.validateEmptyString(dropOfflat))); params.add(new
	 * BasicNameValuePair(APIConstants.KEY_DROPOFF_LNG,
	 * Utils.validateEmptyString(dropOfflng))); params.add(new
	 * BasicNameValuePair(APIConstants.KEY_PRICE,
	 * Utils.validateEmptyString(price))); params.add(new
	 * BasicNameValuePair(APIConstants
	 * .KEY_DETAIL_JOURNEYS_JOURNEY_USERS_DROPOFF_TIME,
	 * Utils.validateEmptyString(dropOfftime))); params.add(new
	 * BasicNameValuePair
	 * (APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_DROP_ADDRESS,
	 * Utils.validateEmptyString(dropOffAdd))); params.add(new
	 * BasicNameValuePair
	 * (APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_TIP_GIVEN,
	 * Utils.validateEmptyString(tip))); params.add(new
	 * BasicNameValuePair(APIConstants
	 * .KEY_DETAIL_JOURNEYS_JOURNEY_USERS_EXTRA_AMOUNT,
	 * Utils.validateEmptyString(extraAmount))); params.add(new
	 * BasicNameValuePair(APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_EXTRAS,
	 * Utils.validateEmptyString(extras)));
	 * 
	 * 
	 * 
	 * 
	 * try { String response = HttpRequestHelper2.httpPost(url,params);
	 * 
	 * if (Utils.isEmptyOrNull(response)) { throw new Exception(); }
	 * 
	 * String responseMessage = getMessageFromResponseHeader(response); int
	 * responseCode = getCodeFromResponseHeader(response); boolean status =
	 * (responseCode == APIConstants.SUCESS_CODE); if
	 * (isErrorCode(responseCode)) { throw new Exception(responseMessage); }
	 * else if (status) { responseMessage = getStringFromBody(response); }
	 * 
	 * 
	 * return new CustomHttpResponse(status, methodName, responseCode,
	 * responseMessage); } catch (Exception e) { e.printStackTrace(); return new
	 * CustomHttpException(methodName, e.getMessage()); } } }); return
	 * executeAsyncTask(httpAsyncTask); }
	 */

	public static HttpAsyncTask endJourney(
			HttpResponseListener responseListener, final String journey_id,
			final String dropOfflat, final String dropOfflng,
			final String price, final String dropOfftime,
			final String dropOffAdd, final Boolean taxIncluded, final String tip) {
		// TODO Auto-generated method stub

		final String methodName = APIConstants.METHOD_UPDATE_JOURNEY_STATUS_COMPLETE;
		final String url = getCompleteURLForMethod(methodName);
		webResponse = new Response();
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(responseListener,
				new HttpRequestListener() {

					@Override
					public Object makeRequest(HttpAsyncTask httpAsyncTask)
							throws CustomHttpException {
						if (httpAsyncTask.isCancelled())
							return null;

						String tempPrice = price;
						Applog.Debug("price before-------------------- "
								+ tempPrice);

						List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();

						if (taxIncluded) {
							params.add(new BasicNameValuePair(
									APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_EXTRAS,
									"airports"));
							params.add(new BasicNameValuePair(
									APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_EXTRA_AMOUNT,
									"15"));
							Applog.Debug("price before tax-------------------- "
									+ tempPrice);

							tempPrice = String.valueOf(Double
									.parseDouble(tempPrice) + 15);
							Applog.Debug("price after tax-------------------- "
									+ tempPrice);

						}
						if (!Utils.isEmptyOrNull(tip)) {
							params.add(new BasicNameValuePair(
									APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_TIP_GIVEN,
									tip));
							tempPrice = String.valueOf(Double
									.parseDouble(tempPrice)
									+ Double.parseDouble(tip));
						}
						params.add(new BasicNameValuePair(
								APIConstants.KEY_JOURNEY_ID, Utils
										.validateEmptyString(journey_id)));
						params.add(new BasicNameValuePair(
								APIConstants.KEY_DROPOFF_LAT, Utils
										.validateEmptyString(dropOfflat)));
						params.add(new BasicNameValuePair(
								APIConstants.KEY_DROPOFF_LNG, Utils
										.validateEmptyString(dropOfflng)));
						params.add(new BasicNameValuePair(
								APIConstants.KEY_PRICE, Utils
										.validateEmptyString(tempPrice)));
						params.add(new BasicNameValuePair(
								APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_DROPOFF_TIME,
								Utils.validateEmptyString(dropOfftime)));
						params.add(new BasicNameValuePair(
								APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_DROP_ADDRESS,
								Utils.validateEmptyString(dropOffAdd)));

						Applog.Debug("price after-------------------- "
								+ tempPrice);
						Applog.Debug("tip---------------- " + tip);

						try {
							String response = HttpRequestHelper2.httpPost(url,
									params);

							if (Utils.isEmptyOrNull(response)) {
								throw new Exception();
							}
							LoaderHelper.hideLoader();
							String responseMessage = getMessageFromResponseHeader(response);
							int responseCode = getCodeFromResponseHeader(response);
							boolean status = (responseCode == APIConstants.SUCESS_CODE);
							if (isErrorCode(responseCode)) {
								throw new Exception(responseMessage);
							} else if (status) {
								responseMessage = getStringFromBody(response);
							}

							return new CustomHttpResponse(status, methodName,
									responseCode, responseMessage);
						} catch (Exception e) {
							e.printStackTrace();
							return new CustomHttpException(methodName, e
									.getMessage());
						}
					}
				});
		return executeAsyncTask(httpAsyncTask);
	}

	/*
	 * public static HttpAsyncTask endJourney(HttpResponseListener
	 * responseListener, final String journey_id, final String dropOfflat, final
	 * String dropOfflng, final String price, final String dropOfftime, final
	 * String dropOffAdd) { // TODO Auto-generated method stub
	 * 
	 * final String methodName =
	 * APIConstants.METHOD_UPDATE_JOURNEY_STATUS_COMPLETE; final String url =
	 * getCompleteURLForMethod(methodName); webResponse = new Response();
	 * HttpAsyncTask httpAsyncTask = new HttpAsyncTask(responseListener, new
	 * HttpRequestListener() {
	 * 
	 * @Override public Object makeRequest(HttpAsyncTask httpAsyncTask) throws
	 * CustomHttpException { if (httpAsyncTask.isCancelled()) return null;
	 * 
	 * List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
	 * params.add(new BasicNameValuePair(APIConstants.KEY_JOURNEY_ID,
	 * Utils.validateEmptyString(journey_id))); params.add(new
	 * BasicNameValuePair(APIConstants.KEY_DROPOFF_LAT,
	 * Utils.validateEmptyString(dropOfflat))); params.add(new
	 * BasicNameValuePair(APIConstants.KEY_DROPOFF_LNG,
	 * Utils.validateEmptyString(dropOfflng))); params.add(new
	 * BasicNameValuePair(APIConstants.KEY_PRICE,
	 * Utils.validateEmptyString(price))); params.add(new
	 * BasicNameValuePair(APIConstants
	 * .KEY_DETAIL_JOURNEYS_JOURNEY_USERS_DROPOFF_TIME,
	 * Utils.validateEmptyString(dropOfftime))); params.add(new
	 * BasicNameValuePair
	 * (APIConstants.KEY_DETAIL_JOURNEYS_JOURNEY_USERS_DROP_ADDRESS,
	 * Utils.validateEmptyString(dropOffAdd)));
	 * 
	 * try { String response = HttpRequestHelper2.httpPost(url,params);
	 * 
	 * if (Utils.isEmptyOrNull(response)) { throw new Exception(); }
	 * 
	 * String responseMessage = getMessageFromResponseHeader(response); int
	 * responseCode = getCodeFromResponseHeader(response); boolean status =
	 * (responseCode == APIConstants.SUCESS_CODE); if
	 * (isErrorCode(responseCode)) { throw new Exception(responseMessage); }
	 * else if (status) { responseMessage = getStringFromBody(response); }
	 * 
	 * 
	 * return new CustomHttpResponse(status, methodName, responseCode,
	 * responseMessage); } catch (Exception e) { e.printStackTrace(); return new
	 * CustomHttpException(methodName, e.getMessage()); } } }); return
	 * executeAsyncTask(httpAsyncTask); }
	 */

	public static HttpAsyncTask callRejected(final String status,
			final String driverUserId, final String CurrentJourneyId,
			HttpResponseListener responseListener) {
		// TODO Auto-generated method stub

		final String methodName = APIConstants.METHOD_ACCEPT_REJECT_JOURNEY;
		final String url = getCompleteURLForMethod(methodName);
		webResponse = new Response();
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(responseListener,
				new HttpRequestListener() {

					@Override
					public Object makeRequest(HttpAsyncTask httpAsyncTask)
							throws CustomHttpException {
						if (httpAsyncTask.isCancelled())
							return null;

						// User user = new User();
						// PickupRequest pick = new PickupRequest();
						AcceptOrRejectJourney accptJourney = new AcceptOrRejectJourney();

						List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();

						params.add(new BasicNameValuePair(
								APIConstants.KEY_ACCEPT_REJECT_STATUS, Utils
										.validateEmptyString(status)));

						params.add(new BasicNameValuePair(
								APIConstants.KEY_ACCEPT_REJECT_DRIVER_USER_ID,
								Utils.validateEmptyString(driverUserId)));

						params.add(new BasicNameValuePair(
								APIConstants.KEY_ACCEPT_REJECT_JOURENY_ID,
								Utils.validateEmptyString(CurrentJourneyId)));

						try {

							String response = HttpRequestHelper2.httpPost(url,
									params);

							if (Utils.isEmptyOrNull(response)) {
								throw new Exception();
							}

							String responseMessage = getMessageFromResponseHeader(response);
							int responseCode = getCodeFromResponseHeader(response);
							boolean status = (responseCode == APIConstants.SUCESS_CODE);
							if (isErrorCode(responseCode)) {
								throw new Exception(responseMessage);
							} else if (status) {

								// JsonObject pickObject =
								// getJsonObjectFromBody(
								// response,
								// APIConstants.KEY_BODY);
								// accptJourney.(pickObject);
								accptJourney.deserializeFromJSON(response);
							}

							return new CustomHttpResponse(accptJourney,
									methodName, responseCode, responseMessage);
						}

						catch (Exception e) {
							e.printStackTrace();
							return new CustomHttpException(methodName, e
									.getMessage());
						}
					}
				});

		return executeAsyncTask(httpAsyncTask);

	}

	/********** SALIK START ********/

	public static HttpAsyncTask changePassword(final String email,
			final String password, final String oldPassword,
			HttpResponseListener responseListener) {
		final String methodName = APIConstants.METHOD_CHANGE_PASSWORD;
		final String url = getCompleteURLForMethod(methodName);
		webResponse = new Response();
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(responseListener,
				new HttpRequestListener() {

					@Override
					public Object makeRequest(HttpAsyncTask httpAsyncTask)
							throws CustomHttpException {
						if (httpAsyncTask.isCancelled())
							return null;

						User user = new User();

						List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
						params.add(new BasicNameValuePair(
								APIConstants.KEY_EMAIL, Utils
										.validateEmptyString(email)));
						params.add(new BasicNameValuePair(
								APIConstants.KEY_PASSWORD, Utils
										.validateEmptyString(password)));
						params.add(new BasicNameValuePair(
								APIConstants.KEY_OLDPASSWORD, Utils
										.validateEmptyString(oldPassword)));

						try {

							String response = HttpRequestHelper2.httpPost(url,
									params);

							if (Utils.isEmptyOrNull(response)) {
								throw new Exception();
							}

							String responseMessage = getMessageFromResponseHeader(response);
							int responseCode = getCodeFromResponseHeader(response);
							boolean status = (responseCode == APIConstants.SUCESS_CODE);
							if (isErrorCode(responseCode)) {
								throw new Exception(responseMessage);
							} else if (status) {
								/*
								 * JsonObject userObject =
								 * getJsonObjectFromBody( response,
								 * APIConstants.KEY_DRIVER_INFORMATION);
								 * user.deserializeFromJSON(userObject);
								 */

							}
							Applog.Debug("CHange Password : " + response);
							return new CustomHttpResponse(user, methodName,
									responseCode, responseMessage);
						} catch (Exception e) {
							e.printStackTrace();
							return new CustomHttpException(methodName, e
									.getMessage());
						}
					}

				});
		return executeAsyncTask(httpAsyncTask);
	}

	// gender
	// first_name
	// last_name
	// group_id
	// license_code
	// phone
	// user_id
	public static HttpAsyncTask updateDriverProfile(final String driverID,
			final String firstName, final String lastName,
			final String contactNumber, final String licenseCode,
			final String gender, final String imageUrl,
			HttpResponseListener responseListener) {
		final String methodName = APIConstants.METHOD_UPDATE_PROFILE;
		final String url = getCompleteURLForMethod(methodName);
		webResponse = new Response();
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(responseListener,
				new HttpRequestListener() {

					@Override
					public Object makeRequest(HttpAsyncTask httpAsyncTask)
							throws CustomHttpException {
						if (httpAsyncTask.isCancelled())
							return null;

						User user = new User();

						ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>() ;
						params.add(new BasicNameValuePair(
								APIConstants.KEY_USER_ID, Utils
										.validateEmptyString(driverID)));
						params.add(new BasicNameValuePair(
								APIConstants.KEY_FIRST_NAME, Utils
										.validateEmptyString(firstName)));
						params.add(new BasicNameValuePair(
								APIConstants.KEY_LAST_NAME, Utils
										.validateEmptyString(lastName)));
						params.add(new BasicNameValuePair(
								APIConstants.KEY_PHONE, Utils
										.validateEmptyString(contactNumber)));
						params.add(new BasicNameValuePair(
								APIConstants.KEY_LICENSE_CODE, Utils
										.validateEmptyString(licenseCode)));
						params.add(new BasicNameValuePair(
								APIConstants.KEY_GENDER, Utils
										.validateEmptyString(gender)));
						params.add(new BasicNameValuePair(
								APIConstants.KEY_GROUP_ID, String
										.valueOf(APIConstants.GROUP_ID)));
						try {

//							String response = HttpRequestHelper2.httpPost(url,
//									params);
							String response = HttpRequestHelper2.httpPostWithImage(url, params, imageUrl, "SmartTaxiDriver-upload");
							

							if (Utils.isEmptyOrNull(response)) {
								throw new Exception();
							}

							String responseMessage = getMessageFromResponseHeader(response);
							int responseCode = getCodeFromResponseHeader(response);
							boolean status = (responseCode == APIConstants.SUCESS_CODE);
							if (isErrorCode(responseCode)) {
								throw new Exception(responseMessage);
							} else if (status) {
								// JsonObject userObject =
								// getJsonObjectFromBody(
								// response,
								// APIConstants.KEY_DRIVER_INFORMATION);
								// user.deserializeFromJSON(userObject);

							}
							Applog.Debug("Update Profile : " + response);

							return new CustomHttpResponse(user, methodName,
									responseCode, responseMessage);
						} catch (Exception e) {
							e.printStackTrace();
							return new CustomHttpException(methodName, e
									.getMessage());
						}
					}

				});
		return executeAsyncTask(httpAsyncTask);
	}

	public static HttpAsyncTask getMyCabs(final String activeCabId,
			final String driverID, HttpResponseListener responseListener) {
		final String methodName = APIConstants.METHOD_DRIVER_CABS;
		final String url = getCompleteURLForMethod(methodName);
		webResponse = new Response();
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(responseListener,
				new HttpRequestListener() {

					@Override
					public Object makeRequest(HttpAsyncTask httpAsyncTask)
							throws CustomHttpException {
						if (httpAsyncTask.isCancelled())
							return null;

						ArrayList<Cab> list = new ArrayList<Cab>();

						List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
						params.add(new BasicNameValuePair(
								APIConstants.KEY_USER_DRIVER_ID, Utils
										.validateEmptyString(driverID)));
						try {

							String response = HttpRequestHelper2.httpPost(url,
									params);

							if (Utils.isEmptyOrNull(response)) {
								throw new Exception();
							}

							String responseMessage = getMessageFromResponseHeader(response);
							int responseCode = getCodeFromResponseHeader(response);
							boolean status = (responseCode == APIConstants.SUCESS_CODE);
							if (isErrorCode(responseCode)) {
								throw new Exception(responseMessage);
							} else if (status) {
								JsonObject cabObject = getJsonObjectFromBody(
										response, APIConstants.KEY_CABS);
								Applog.Debug("Update Profile : " + cabObject);

								list = com.smarttaxi.driver.BAL.Driver.getCabs(
										cabObject, activeCabId);

							}

							return new CustomHttpResponse(list, methodName,
									responseCode, responseMessage);
						} catch (Exception e) {
							e.printStackTrace();
							return new CustomHttpException(methodName, e
									.getMessage());
						}
					}

				});
		return executeAsyncTask(httpAsyncTask);
	}

	public static HttpAsyncTask assignCab(final String cabId,
			final String driverID, final String type,
			HttpResponseListener responseListener) {
		final String methodName = APIConstants.METHOD_ASSIGN_CAB_TO_DRIVER;
		final String url = getCompleteURLForMethod(methodName);
		webResponse = new Response();
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(responseListener,
				new HttpRequestListener() {

					@Override
					public Object makeRequest(HttpAsyncTask httpAsyncTask)
							throws CustomHttpException {
						if (httpAsyncTask.isCancelled())
							return null;

						Cab cab = null;

						List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
						params.add(new BasicNameValuePair(
								APIConstants.KEY_CAB_ID, Utils
										.validateEmptyString(driverID)));
						params.add(new BasicNameValuePair(
								APIConstants.KEY_USER_DRIVER_ID, Utils
										.validateEmptyString(cabId)));
						params.add(new BasicNameValuePair(
								APIConstants.KEY_TYPE, Utils
										.validateEmptyString(type)));
						try {

							String response = HttpRequestHelper2.httpPost(url,
									params);

							if (Utils.isEmptyOrNull(response)) {
								throw new Exception();
							}

							String responseMessage = getMessageFromResponseHeader(response);
							int responseCode = getCodeFromResponseHeader(response);
							boolean status = (responseCode == APIConstants.SUCESS_CODE);
							if (isErrorCode(responseCode)) {
								throw new Exception(responseMessage);
							} else if (status) {
								cab = new Cab();
								cab.setID(cabId);
							}
							Applog.Debug("cab Assigned : " + response);

							return new CustomHttpResponse(cab, methodName,
									responseCode, responseMessage);
						} catch (Exception e) {
							e.printStackTrace();
							return new CustomHttpException(methodName, e
									.getMessage());
						}
					}

				});
		return executeAsyncTask(httpAsyncTask);
	}

	public static HttpAsyncTask getCab(final String cabNo,
			HttpResponseListener responseListener) {
		final String methodName = APIConstants.METHOD_GET_DRIVER_CAB;
		final String url = getCompleteURLForMethod(methodName);
		webResponse = new Response();
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(responseListener,
				new HttpRequestListener() {

					@Override
					public Object makeRequest(HttpAsyncTask httpAsyncTask)
							throws CustomHttpException {
						if (httpAsyncTask.isCancelled())
							return null;

						ArrayList<Cab> list = new ArrayList<Cab>();

						List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
						params.add(new BasicNameValuePair(
								APIConstants.KEY_CAB_NO, Utils
										.validateEmptyString(cabNo)));
						try {

							String response = HttpRequestHelper2.httpPost(url,
									params);

							if (Utils.isEmptyOrNull(response)) {
								throw new Exception();
							}

							String responseMessage = getMessageFromResponseHeader(response);
							int responseCode = getCodeFromResponseHeader(response);
							boolean status = (responseCode == APIConstants.SUCESS_CODE);
							if (isErrorCode(responseCode)) {
								throw new Exception(responseMessage);
							} else if (status) {
								//

							}
							Applog.Debug("get Cab Information : " + response);

							return new CustomHttpResponse(list, methodName,
									responseCode, responseMessage);
						} catch (Exception e) {
							e.printStackTrace();
							return new CustomHttpException(methodName, e
									.getMessage());
						}
					}

				});
		return executeAsyncTask(httpAsyncTask);
	}

	public static HttpAsyncTask GetTripHistory(final String driverID,
			final String to, final String from,
			HttpResponseListener responseListener) {
		Applog.Debug("driver ID while getting Trip History " + driverID);
		final String methodName = APIConstants.METHOD_GET_TRIP_HISTORY;
		final String url = getCompleteURLForMethod(methodName);
		webResponse = new Response();
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(responseListener,
				new HttpRequestListener() {

					@Override
					public Object makeRequest(HttpAsyncTask httpAsyncTask)
							throws CustomHttpException {
						if (httpAsyncTask.isCancelled())
							return null;

						ArrayList<Journey> list = new ArrayList<Journey>();

						List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
						params.add(new BasicNameValuePair(
								APIConstants.KEY_DRIVERID, Utils
										.validateEmptyString(driverID)));
						// if (to != null)
						// params.add(new BasicNameValuePair(
						// APIConstants.KEY_TIME_TO, Utils
						// .validateEmptyString(to)));
						// if (from != null)
						// params.add(new BasicNameValuePair(
						// APIConstants.KEY_TIME_FROM, Utils
						// .validateEmptyString(from)));
						try {

							String response = HttpRequestHelper2.httpPost(url,
									params);

							if (Utils.isEmptyOrNull(response)) {
								throw new Exception();
							}

							String responseMessage = getMessageFromResponseHeader(response);
							int responseCode = getCodeFromResponseHeader(response);
							boolean status = (responseCode == APIConstants.SUCESS_CODE);
							if (isErrorCode(responseCode)) {
								throw new Exception(responseMessage);
							} else if (status) {
								JsonObject journeyObject = getJsonObjectFromBody(
										response, APIConstants.KEY_JOURNEYS);
								list = com.smarttaxi.driver.BAL.Driver
										.parseHistory(journeyObject);
								Applog.Debug("get Trip History : "
										+ journeyObject);
							}

							return new CustomHttpResponse(list, methodName,
									responseCode, responseMessage);
						} catch (Exception e) {
							e.printStackTrace();
							return new CustomHttpException(methodName, e
									.getMessage());
						}
					}

				});
		return executeAsyncTask(httpAsyncTask);
	}

	// Date Format : yyy-MM-dd HH:mm:ss
	public static HttpAsyncTask GetTripHistory(String driverID,
			HttpResponseListener responseListener) {
		return GetTripHistory(driverID, null, null, responseListener);

	}

	/********** SALIK END **************/

	public static HttpAsyncTask getCurrentJourney(final String driver_id,
			final String journey_id, HttpResponseListener responseListener) {

		// final String _currentId= currentJourneyId;
		final String methodName = APIConstants.METHOD_CURRENT_JOURNEY;
		final String url = getCompleteURLForMethod(methodName);
		webResponse = new Response();
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(responseListener,
				new HttpRequestListener() {

					@Override
					public Object makeRequest(HttpAsyncTask httpAsyncTask)
							throws CustomHttpException {
						if (httpAsyncTask.isCancelled())
							return null;

						Journey journey = new Journey();

						List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
						params.add(new BasicNameValuePair(
								APIConstants.KEY_DRIVERID, Utils
										.validateEmptyString(driver_id)));

						try {

							String response = HttpRequestHelper2.httpPost(url,
									params);
							if (Utils.isEmptyOrNull(response)) {
								throw new Exception();
							}

							String responseMessage = getMessageFromResponseHeader(response);
							int responseCode = getCodeFromResponseHeader(response);
							boolean status = (responseCode == APIConstants.SUCESS_CODE);
							if (isErrorCode(responseCode)) {
								throw new Exception(responseMessage);
							} else if (status) {
								JsonObject userObject = getJsonObjectFromBody(
										response,
										APIConstants.KEY_DETAIL_JOURNEYS)
										.getAsJsonObject(journey_id);

								journey.deserializeFromJSON(userObject);

								/*
								 * JSONObject userObject1 = new
								 * JSONObject(response); JSONObject user3 =
								 * userObject1.getJSONObject(cirrentJourneyId);
								 */
							}

							return new CustomHttpResponse(journey, methodName,
									responseCode, responseMessage);
						} catch (Exception e) {
							e.printStackTrace();
							return new CustomHttpException(methodName, e
									.getMessage());
						}
					}
				});
		return executeAsyncTask(httpAsyncTask);
	}

	public static HttpAsyncTask forgotPassword(final String email,
			HttpResponseListener responseListener) {
		final String methodName = APIConstants.METHOD_FORGOT_PASSWORD;
		final String url = getCompleteURLForMethod(methodName);
		webResponse = new Response();
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(responseListener,
				new HttpRequestListener() {

					@Override
					public Object makeRequest(HttpAsyncTask httpAsyncTask)
							throws CustomHttpException {
						if (httpAsyncTask.isCancelled())
							return null;

						User user = new User();

						List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
						params.add(new BasicNameValuePair(
								APIConstants.KEY_EMAIL, Utils
										.validateEmptyString(email)));
//						params.add(new BasicNameValuePair(
//								APIConstants.KEY_PASSWORD, Utils
//										.validateEmptyString(password)));
//						params.add(new BasicNameValuePair(
//								APIConstants.KEY_OLDPASSWORD, Utils
//										.validateEmptyString(oldPassword)));

						try {

							String response = HttpRequestHelper2.httpPost(url,
									params);

							if (Utils.isEmptyOrNull(response)) {
								throw new Exception();
							}

							String responseMessage = getMessageFromResponseHeader(response);
							int responseCode = getCodeFromResponseHeader(response);
							boolean status = (responseCode == APIConstants.SUCESS_CODE);
							if (isErrorCode(responseCode)) {
								throw new Exception(responseMessage);
							} else if (status) {
								/*
								 * JsonObject userObject =
								 * getJsonObjectFromBody( response,
								 * APIConstants.KEY_DRIVER_INFORMATION);
								 * user.deserializeFromJSON(userObject);
								 */

							}
							Applog.Debug("CHange Password : " + response);
							return new CustomHttpResponse(user, methodName,
									responseCode, responseMessage);
						} catch (Exception e) {
							e.printStackTrace();
							return new CustomHttpException(methodName, e
									.getMessage());
						}
					}

				});
		return executeAsyncTask(httpAsyncTask);
	}
}
