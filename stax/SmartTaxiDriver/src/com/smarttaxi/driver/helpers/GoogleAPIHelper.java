package com.smarttaxi.driver.helpers;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.smarttaxi.driver.entities.Cab;
import com.smarttaxi.driver.entities.CustomHttpException;
import com.smarttaxi.driver.entities.CustomHttpResponse;
import com.smarttaxi.driver.interfaces.HttpRequestListener;
import com.smarttaxi.driver.interfaces.HttpResponseListener;
import com.smarttaxi.driver.utils.HttpAsyncTask;

public class GoogleAPIHelper {

	private static final String GOOGLE_API_BASE = "https://maps.googleapis.com/maps/api";
	public static final String DISTANCE_MATRIX_API_BASE = GOOGLE_API_BASE
			+ "/distancematrix";
	private static final String PLACES_API_BASE = GOOGLE_API_BASE + "/place";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String TYPE_DETAILS = "details";
	private static final String OUT_JSON = "/json";

	private static final String API_KEY = "AIzaSyBYHB6Z-YPNSAzg2nwxYfzQLtIfukfMYgk";

	private static final String KEY_PREDICTIONS = "predictions";
	private static final String KEY_DESCRIPTION = "description";
	private static final String KEY_RESULT = "result";
	private static final String KEY_GEOMETRY = "geometry";
	private static final String KEY_LOCATION = "location";
	private static final String KEY_LAT = "lat";
	private static final String KEY_LNG = "lng";
	private static final String KEY_REFERENCE = "reference";
	private static final String KEY_ROWS = "rows";
	private static final String KEY_ELEMENTS = "elements";
	private static final String KEY_DURATION = "duration";
	public static final String KEY_TEXT = "text";
	public static final String KEY_VALUE = "value";

	public static ArrayList<AutoCompleteLocation> autocomplete(String input) {
		ArrayList<AutoCompleteLocation> resultList = null;
		try {
			StringBuilder sb = new StringBuilder(PLACES_API_BASE
					+ TYPE_AUTOCOMPLETE + OUT_JSON);
			sb.append("?sensor=false&key=" + API_KEY);
			sb.append("&components=country:ca");
			sb.append("&input=" + URLEncoder.encode(input, "utf8"));
			// Locale.getDefault().getISO3Country()
			String response = HttpRequestHelper2.httpGet(sb.toString());
			JsonObject jsonObject = JsonHelper.parseToJsonObject(response);
			JsonArray predsJsonArray = JsonHelper.getJsonArray(jsonObject,
					KEY_PREDICTIONS);

			resultList = new ArrayList<AutoCompleteLocation>(
					predsJsonArray.size());
			for (JsonElement predElement : predsJsonArray) {
				String address = JsonHelper.getString(
						predElement.getAsJsonObject(), KEY_DESCRIPTION);
				String reference = JsonHelper.getString(
						predElement.getAsJsonObject(), KEY_REFERENCE);
				resultList.add(new AutoCompleteLocation(address, reference));
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultList;
	}

	public static LatLng placeLatLng(String reference) {

		LatLng latLng;

		try {
			StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_DETAILS
					+ OUT_JSON);
			sb.append("?sensor=false&key=" + API_KEY);
			sb.append("reference=" + reference);
			// Locale.getDefault().getISO3Country()
			String response = HttpRequestHelper2.httpGet(sb.toString());
			JsonObject jsonObject = JsonHelper.parseToJsonObject(response);
			JsonObject resultObject = JsonHelper.getJsonObject(jsonObject,
					KEY_RESULT);
			JsonObject geoObject = JsonHelper.getJsonObject(resultObject,
					KEY_GEOMETRY);
			JsonObject locObject = JsonHelper.getJsonObject(geoObject,
					KEY_LOCATION);
			Double lat = JsonHelper.getDouble(locObject, KEY_LAT);
			Double lng = JsonHelper.getDouble(locObject, KEY_LNG);
			latLng = new LatLng(lat, lng);

		} catch (Exception e) {
			latLng = null;
			e.printStackTrace();
		}

		return latLng;
	}

	@SuppressLint("NewApi")
	public static HttpAsyncTask timeDuration(final LatLng originLatLng,
			final LatLng destLatLng, HttpResponseListener responseListener) {

		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(responseListener,
				new HttpRequestListener() {

					@Override
					public Object makeRequest(HttpAsyncTask httpAsyncTask)
							throws CustomHttpException {
						try {
							StringBuilder sb = new StringBuilder(
									DISTANCE_MATRIX_API_BASE + OUT_JSON);
							sb.append("?sensor=false");
							sb.append("&origins="
									+ String.valueOf(originLatLng.latitude)
									+ ","
									+ String.valueOf(originLatLng.longitude));
							sb.append("&destinations="
									+ String.valueOf(destLatLng.latitude) + ","
									+ String.valueOf(destLatLng.longitude));

							String response = HttpRequestHelper2.httpGet(sb
									.toString());
							JsonObject rootObj = JsonHelper
									.parseToJsonObject(response);
							JsonArray rowsArray = JsonHelper.getJsonArray(
									rootObj, KEY_ROWS);
							JsonArray elementsArray = JsonHelper.getJsonArray(
									rowsArray.get(0).getAsJsonObject(),
									KEY_ELEMENTS);
							JsonObject durationObject = JsonHelper
									.getJsonObject(elementsArray.get(0)
											.getAsJsonObject(), KEY_DURATION);
							HashMap<String, String> durationMap = new HashMap<String, String>();
							durationMap.put(KEY_TEXT, JsonHelper.getString(
									durationObject, KEY_TEXT));
							durationMap.put(KEY_VALUE, JsonHelper.getString(
									durationObject, KEY_VALUE));
							return new CustomHttpResponse(durationMap, DISTANCE_MATRIX_API_BASE, 200, "");
						} catch (Exception ex) {
							return new CustomHttpException(DISTANCE_MATRIX_API_BASE, "");
						}
					}
				});
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			return (HttpAsyncTask) httpAsyncTask.execute();
		} else {
			return  (HttpAsyncTask)httpAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}

	}
	
	@SuppressLint("NewApi")
	public static HttpAsyncTask timeDuration(final ArrayList<Cab> cabsList,
			final LatLng destLatLng, HttpResponseListener responseListener) {

		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(responseListener,
				new HttpRequestListener() {

					@Override
					public Object makeRequest(HttpAsyncTask httpAsyncTask)
							throws CustomHttpException {
						try {
							StringBuilder sb = new StringBuilder(
									DISTANCE_MATRIX_API_BASE + OUT_JSON);
							sb.append("?sensor=false");
							sb.append("&origins=");
							for(Cab cab : cabsList) {
								sb.append(String.valueOf(cab.getLatLng().latitude)
										+ ","
										+ String.valueOf(cab.getLatLng().longitude));
								if(cabsList.indexOf(cab) != (cabsList.size() - 1))
									sb.append("|");
							}
							sb.append("&destinations="
									+ String.valueOf(destLatLng.latitude) + ","
									+ String.valueOf(destLatLng.longitude));

							String response = HttpRequestHelper2.httpGet(sb
									.toString());
							JsonObject rootObj = JsonHelper
									.parseToJsonObject(response);
							JsonArray rowsArray = JsonHelper.getJsonArray(
									rootObj, KEY_ROWS);
							for(int i = 0; i < rowsArray.size(); i++) {
								JsonArray elementsArray = JsonHelper.getJsonArray(
										rowsArray.get(i).getAsJsonObject(),
										KEY_ELEMENTS);
								JsonObject durationObject = JsonHelper
										.getJsonObject(elementsArray.get(0)
												.getAsJsonObject(), KEY_DURATION);
								int value = JsonHelper.getInt(
										durationObject, KEY_VALUE);
								Cab cab = cabsList.get(i);
								cab.setTimeDurationInSeconds(value);
								cabsList.set(i, cab);
								
							}
							
							return new CustomHttpResponse(cabsList, DISTANCE_MATRIX_API_BASE, 200, "");
						} catch (Exception ex) {
							return new CustomHttpException(DISTANCE_MATRIX_API_BASE, "");
						}
					}
				});
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			return (HttpAsyncTask) httpAsyncTask.execute();
		} else {
			return  (HttpAsyncTask)httpAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}

	}

	public static class AutoCompleteLocation {
		String locationAddress;
		String reference;

		public AutoCompleteLocation(String locationAddress, String reference) {
			this.locationAddress = locationAddress;
			this.reference = reference;
		}

		public String getLocationAddress() {
			return locationAddress;
		}

		public String getReference() {
			return reference;
		}
	}

}
