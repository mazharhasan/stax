package com.smart.taxi.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.smart.taxi.entities.CustomHttpException;
import com.smart.taxi.entities.CustomHttpResponse;
import com.smart.taxi.interfaces.HttpResponseListener;
import com.smart.taxi.utils.Applog;
import com.smart.taxi.utils.Common;

public class ServiceLocation extends Service implements HttpResponseListener {
	private LocationManager locMan;
	private Boolean locationChanged;
	private Handler handler = new Handler();
	public static Location curLocation;
	public static boolean isService = false;

	LocationListener gpsListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			
			
			
			
			////
			if (curLocation == null) {
				curLocation = location;
			
				locationChanged = true;
			} else if (curLocation.getLatitude() == location.getLatitude()
					&& curLocation.getLongitude() == location.getLongitude()) {
				locationChanged = false;
				
				
				return;
			} else
				locationChanged = true;

			curLocation = location;

			if (locationChanged)
				locMan.removeUpdates(gpsListener);

			
			
		}

		public void onProviderDisabled(String provider) {
			Applog.Debug(TAG +  " GPS is disabled" );
		}

		public void onProviderEnabled(String provider) {
			Applog.Debug(TAG +  " GPS is enabled" );
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			if (status == 0)// UnAvailable
			{
				Applog.Debug(TAG +  " GPS is disabled" );
				
			} else if (status == 1)// Trying to Connect
			{
				Applog.Debug(TAG +  " GPS is trying to connect" );
				
			} else if (status == 2) {// Available
				Applog.Debug(TAG +  " GPS is availaable now" );
			}
			

		}

	};

	@Override
	public void onCreate() {
		super.onCreate();

		curLocation = getBestLocation();
		//driver = new Driver(getBaseContext());
		if (curLocation == null)
			Toast.makeText(getBaseContext(), "Unable to get your location",
					Toast.LENGTH_SHORT).show();
			
		else {
			
			
			
		}

		isService = true;
	}
	
	
	
	public static boolean IsLocationUpdated()
	{
		if(curLocation!=null)
			return true;
		else
			return false;
	}


	final String TAG = "LocationService";

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	public void onStart(Intent i, int startId) {
		handler.postDelayed(GpsFinder, 1);
	}

	@Override
	public void onDestroy() {
		isService = false;
		handler.removeCallbacks(GpsFinder);
		handler = null;
		//AppToast.ShowToast(this, "Stop all services..");
		turnGPSOff();
		
	}
	
	public void turnGPSOn()
	{
//	     Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
//	     intent.putExtra("enabled", true);
//	     sendBroadcast(intent);
//
//	    String provider = Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
//	    if(!provider.contains("gps")){ //if gps is disabled
//	        final Intent poke = new Intent();
//	        poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider"); 
//	        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
//	        poke.setData(Uri.parse("3")); 
//	        sendBroadcast(poke);
//
//
//	    }
	}
	public void turnGPSOff()
	{
		return;
		
//	     Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
//	     intent.putExtra("enabled", false);
//	     sendBroadcast(intent);
//		
//		
//	    String provider = Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
//	    if(provider.contains("gps")){ //if gps is enabled
//	        final Intent poke = new Intent();
//	        poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
//	        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
//	        poke.setData(Uri.parse("3")); 
//	        sendBroadcast(poke);
//	    }
	}
	

	public IBinder onBind(Intent arg0) {
		return null;
	}


	public Runnable GpsFinder = new Runnable() {
		public void run() {

			Location tempLoc = getBestLocation();
			if (tempLoc != null)
				curLocation = tempLoc;
			tempLoc = null;
			
			//if(driver!=null && curLocation!=null )
			//	driver.UpdateLocationFromService(curLocation.getLatitude(), curLocation.getLongitude(), ServiceLocation.this);
			handler.postDelayed(GpsFinder, Common.DURATION);
			
		}
	};

	private Location getBestLocation() {
		Location gpslocation = null;
		Location networkLocation = null;

		if (locMan == null) {
			locMan = (LocationManager) getApplicationContext()
					.getSystemService(Context.LOCATION_SERVICE);
		}
		try {
			if (locMan.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER,
						1000, 1, gpsListener);// here you can set the 2nd
												// argument time interval also
												// that after how much time it
												// will get the gps location
				gpslocation = locMan
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);

			}
			
			
			
				if (locMan.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
						1000, 1, gpsListener);
				networkLocation = locMan
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			}
		} catch (IllegalArgumentException e) {
			// Log.e(ErrorCode.ILLEGALARGUMENTERROR, e.toString());
			Log.e("error", e.toString());
		}
		if (gpslocation == null && networkLocation == null)
			return null;

		if (gpslocation != null && networkLocation != null) {
			if (gpslocation.getTime() < networkLocation.getTime()) {
				gpslocation = null;
				return networkLocation;
			} else {
				networkLocation = null;
				return gpslocation;
			}
		}
		if (gpslocation == null) {
			return networkLocation;
		}
		if (networkLocation == null) {
			return gpslocation;
		}
		return null;
	}

	@Override
	public void onResponse(CustomHttpResponse object) {
		Boolean status = (Boolean) object.getResponse();
		if(status){
			// Location Updated
			Applog.Debug("ServiceLocation onResponse : location updated");
		}
		else
			Applog.Debug("ServiceLocation onResponse ERROR : location updated");
	}

	@Override
	public void onException(CustomHttpException exception) {
		Applog.Debug("ServiceLocation onException : "+exception.toString());
	}
}	