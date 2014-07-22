package com.smarttaxi.driver.helpers;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;
import com.smarttaxi.driver.activities.ServiceLocation;
import com.smarttaxi.driver.exceptions.LocationNotFoundException;
import com.smarttaxi.driver.utils.AppToast;
import com.smarttaxi.driver.utils.Applog;
import com.smarttaxi.driver.utils.Common;

public class LocationManager implements LocationListener, ConnectionCallbacks,
		OnConnectionFailedListener {

	static LocationManager locationManager;
	private Handler handler = new Handler();
	public final static int LOCATION_SERVICE_REQUEST_CODE = 5011;

	private static final int MILLISECONDS_PER_SECOND = 1000;
	public static final int UPDATE_INTERVAL_IN_SECONDS = 2;
	public static final int UPDATE_INTERVAL_IN_SECONDS_FOR_BACKGROUND = 4;

	//
	private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND
			* UPDATE_INTERVAL_IN_SECONDS;
	private static final long UPDATE_INTERVAL_FOR_BACKGROUND = MILLISECONDS_PER_SECOND
			* UPDATE_INTERVAL_IN_SECONDS_FOR_BACKGROUND;
	private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
	private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND
			* FASTEST_INTERVAL_IN_SECONDS;

	public static final String LOCATION_CHANGED_INTENT_ACTION = "LocationChanged";

	Location currentLocation;
	Location tempcurrentLocation;
	public static boolean isBackgroundMode;

	LocationClient locationClient;

	Context context;

	LocationManager() {

	}

	public static LocationManager getInstance(Context context) {
		if (locationManager == null) {
			locationManager = new LocationManager();
		}

		locationManager.context = context;
		return locationManager;
	}

	/*
	 * public Runnable GpsFinder = new Runnable() { public void run() {
	 * 
	 * requestLocationUpdatesForForeground(context);
	 * handler.postDelayed(GpsFinder, Common.DURATION);
	 * 
	 * } };
	 */

	public String getCurrentLocationAddress() {
		return getLocationAddress(currentLocation);
	}

	public void startLocationService() {
		isBackgroundMode = false;
		Applog.Debug("startLocationService");
		// AppToast.ShowToast(context, "LM-startLocationService()");
		startLocationClient();
	}

	public void resumeLocationService() {

		// AppToast.ShowToast(context, "LM-resumeLocationService()");
		isBackgroundMode = false;
		Applog.Debug("resumeLocationService");
		startLocationClient();
	}

	// public void startAlarm() {
	// // isBackgroundMode = false;
	// // AlarmManager alarm =
	// (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	// // alarm.setRepeating(AlarmManager.RTC_WAKEUP,
	// System.currentTimeMillis(),
	// // 60 * 1000, getPendingIntentForLocationService());
	// }

	// public void stopAlarm() {
	// // AlarmManager alarm =
	// (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	// // alarm.cancel(getPendingIntentForLocationService());
	// }

	public void stopLocationService() {
		isBackgroundMode = true;
		// stopAlarm();
		stopLocationUpdates(this);
		// AppToast.ShowToast(context, "LM-stopLocation started");
		requestLocationUpdatesForBackground(this);

	}

	//
	// private PendingIntent getPendingIntentForLocationService() {
	//
	// PendingIntent pendingIntent = PendingIntent
	// .getService(context, LOCATION_SERVICE_REQUEST_CODE,
	// getIntentForLocationService(), 0);
	// return pendingIntent;
	// }

	// private Intent getIntentForLocationService() {
	// return new Intent(context, LocationUpdateService.class);
	// }

	private LocationRequest getLocationRequestForForeground() {
		Applog.Debug("getLocationRequestForForeground");
		LocationRequest locationRequest = LocationRequest.create();
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		locationRequest.setInterval(UPDATE_INTERVAL);
		locationRequest.setFastestInterval(FASTEST_INTERVAL);
		locationRequest.setSmallestDisplacement(1);
		return locationRequest;
	}

	private LocationRequest getLocationRequestForBackground() {
		LocationRequest locationRequest = LocationRequest.create();
		locationRequest
				.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
		locationRequest.setInterval(UPDATE_INTERVAL_FOR_BACKGROUND);
		return locationRequest;
	}

	public void startLocationClient() {
		if (locationClient != null && locationClient.isConnected()) {
			requestLocationUpdatesForForeground(this);
			return;
		}

		LoaderHelper.showLoader((Activity) context,
				"Getting location please wait...", "In progress");
		locationClient = new LocationClient(context, this, this);
		Applog.Debug("startLocationClient");
		locationClient.connect();
	}

	public void requestLocationUpdatesForForeground(
			LocationListener locationListener) {

		// AppToast.ShowToast(context, "LM-foreground started");
		Applog.Debug("requestLocationUpdatesForForeground");
		if (locationClient != null && locationClient.isConnected()) {
			locationClient.requestLocationUpdates(
					getLocationRequestForForeground(), locationListener);
		}
	}

	public void requestLocationUpdatesForBackground(
			LocationListener locationListener) {

		// AppToast.ShowToast(context, "LM-Backgroiund started");
		if (locationClient != null && locationClient.isConnected()) {
			locationClient.requestLocationUpdates(
					getLocationRequestForBackground(), locationListener);

		}
	}

	public void stopLocationUpdates(LocationListener locationListener) {
		if (locationClient != null && locationClient.isConnected()) {
			locationClient.removeLocationUpdates(locationListener);
		}
	}

	public Location getCurrentLocation() throws LocationNotFoundException {
		if (locationClient == null || !locationClient.isConnected()) {
			Applog.Error(">>>>>>> locationClient =null Or not connected");
			throw new LocationNotFoundException();
		}
		if (currentLocation != null) {
			Applog.Error(">>>>>>> OK");
			tempcurrentLocation = currentLocation;
			return currentLocation;
		}
		Location location = tempcurrentLocation == null ? locationClient.getLastLocation()
				: tempcurrentLocation;
		if (location == null)
			throw new LocationNotFoundException();
		else
			Applog.Error(">>>>>>> LastLocation");

		return location;
	}

	@Override
	public void onConnected(Bundle arg0) {
		// stopLocationUpdates(this);

		if (LoaderHelper.isLoaderShowing())
			LoaderHelper.hideLoader();
		Applog.Debug("onConnected");
		requestLocationUpdatesForForeground(this);
		// startAlarm();
		// Toast.makeText(context, "CONNECTED", Toast.LENGTH_SHORT).show();
		// AppToast.ShowToast(context, "CONNECTED" + " Method= " +
		// "onConnected()");
	}

	@Override
	public void onDisconnected() {

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		// Toast.makeText(context, "CONNECTION FAILED",
		// Toast.LENGTH_SHORT).show();
		if (connectionResult.hasResolution()) {
			try {
				if (context != null)
					connectionResult.startResolutionForResult(
							(Activity) context, 5000);
			} catch (IntentSender.SendIntentException e) {
				e.printStackTrace();
			}
		} else {

		}
	}

	@Override
	public void onLocationChanged(Location location) {
		
		if(location==null)
			return;

		if (currentLocation == null)
			currentLocation = location;
		
		
			
		

		Applog.Debug("" + currentLocation.getLatitude());
		// AppToast.ShowToast(context, "Loc-Mana Changed");

		/*if (currentLocation.getLatitude() != location.getLatitude()
				&& currentLocation.getLongitude() != location.getLongitude()) {*/
			Intent intent = new Intent(LOCATION_CHANGED_INTENT_ACTION);
			// intent.putExtra("Location", location);
			currentLocation = location;
			context.sendBroadcast(intent);

			/*} else
			currentLocation = location;*/

	}

	public String getLocationAddress(LatLng latLng) {
		Geocoder geocoder;
		List<Address> addresses;
		String completeAddress = "";

		if (latLng == null)
			return completeAddress;

		try {
			geocoder = new Geocoder(context, Locale.getDefault());
			addresses = geocoder.getFromLocation(latLng.latitude,
					latLng.longitude, 1);
			String address = addresses.get(0).getAddressLine(0);
			String city = addresses.get(0).getAddressLine(1);
			String country = addresses.get(0).getAddressLine(2);
			completeAddress = address + ", " + city + ", " + country;

		} catch (IOException ex) {
			completeAddress = "";
			ex.printStackTrace();
		} catch (Exception e1) {
			completeAddress = "";
			e1.printStackTrace();
		}

		return completeAddress;
	}

	public LatLng getLatLngForAddres(String address) {
		Geocoder geoCoder;
		LatLng latLng = null;
		try {
			geoCoder = new Geocoder(context, Locale.getDefault());
			List<Address> addresses = geoCoder.getFromLocationName(address, 1);
			if (addresses.size() > 0) {
				latLng = new LatLng(addresses.get(0).getLatitude(), addresses
						.get(0).getLongitude());
			}
		} catch (Exception e) {
			e.printStackTrace();
			latLng = null;
		}

		return latLng;
	}

	public String getLocationAddress(Location location) {
		if (location == null)
			return "";
		return getLocationAddress(new LatLng(location.getLatitude(),
				location.getLongitude()));
	}

	public float getDistance(LatLng firstPoint, LatLng secondPoint) {

		double earthRadius = 3958.75;
		int meterConversion = 1609;

		double lat1 = firstPoint.latitude;
		double lat2 = secondPoint.latitude;
		double lon1 = firstPoint.longitude;
		double lon2 = secondPoint.longitude;
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
				* Math.sin(dLon / 2);
		double c = 2 * Math.asin(Math.sqrt(a));
		// return 6366000 * c;
		//
		//
		// double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
		// Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
		// Math.sin(dLng/2) * Math.sin(dLng/2);
		// double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double dist = earthRadius * c;

		return Float.valueOf((float) (dist * meterConversion));
	}

}