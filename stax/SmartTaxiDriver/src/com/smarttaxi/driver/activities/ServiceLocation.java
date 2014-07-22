package com.smarttaxi.driver.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.smarttaxi.driver.R;
import com.smarttaxi.driver.BAL.Driver;
import com.smarttaxi.driver.entities.CustomHttpException;
import com.smarttaxi.driver.entities.CustomHttpResponse;
import com.smarttaxi.driver.exceptions.LocationNotFoundException;
import com.smarttaxi.driver.fragments.FindARideFragment;
import com.smarttaxi.driver.gcm.ConnectionDetector;
import com.smarttaxi.driver.helpers.LoaderHelper;
import com.smarttaxi.driver.interfaces.HttpResponseListener;
import com.smarttaxi.driver.utils.AppToast;
import com.smarttaxi.driver.utils.Applog;
import com.smarttaxi.driver.utils.Common;

public class ServiceLocation extends Service implements HttpResponseListener {

	public static final int STATUS_GPS_ENABLED = 2;
	public static final int STATUS_GPS_DISABLED = 0;
	public static final int STATUS_GPS_CONNECTING = 1;
	public static int STATUS_GPS;
	private LocationManager locMan;
	private Boolean locationChanged;
	private Handler handler = new Handler();
	private static Driver driver;
	public static Location curLocation;
	public static boolean isService = false;
	public static String curAddress = "";
	// ConnectionDetector cd;
	LocationListener gpsListener = new LocationListener() {
		public void onLocationChanged(Location location) {

			// //
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
			Applog.Debug(TAG + " GPS is disabled");
		}

		public void onProviderEnabled(String provider) {
			Applog.Debug(TAG + " GPS is enabled");
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			if (status == STATUS_GPS_DISABLED)// UnAvailable
			{
				STATUS_GPS = STATUS_GPS_DISABLED;
				Applog.Debug(TAG + " GPS is disabled");

			} else if (status == STATUS_GPS_CONNECTING)// Trying to Connect
			{
				STATUS_GPS = STATUS_GPS_CONNECTING;
				Applog.Debug(TAG + " GPS is trying to connect");

			} else if (status == STATUS_GPS_ENABLED) {// Available
				STATUS_GPS = STATUS_GPS_ENABLED;
				Applog.Debug(TAG + " GPS is availaable now");
			}

		}

	};

	public boolean isGpsEnabled() {
		return STATUS_GPS==2 ?true :false;

	}

	@Override
	public void onCreate() {
		super.onCreate();

		new UpdateLocationInBackground().execute("");

		/*
		 * curLocation = getBestLocation(); driver = new
		 * Driver(getBaseContext()); if (curLocation == null)
		 * Toast.makeText(getBaseContext(), "Unable to get your location",
		 * Toast.LENGTH_SHORT).show();
		 */

		// cd = new ConnectionDetector(getApplicationContext());

	}

	private class UpdateLocationInBackground extends
			AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			try {

				curLocation = getBestLocation();

				/* isUserMarkerCreated = true; */

			}

			catch (Exception e) {
				e.printStackTrace();
			}

			return "Executed";
		}

		@Override
		protected void onPostExecute(String result) {

			/*
			 * if (LoaderHelper.isLoaderShowing()) { LoaderHelper.hideLoader();
			 * }
			 */
			// if(!isService){
			// isService = true;
			driver = new Driver(getBaseContext());
			/*
			 * if (curLocation == null) Toast.makeText(getBaseContext(),
			 * "Unable to get your location", Toast.LENGTH_SHORT).show();
			 */
			// }

		}

		@Override
		protected void onPreExecute() {

			// LoaderHelper.showLoader((Activity)getApplicationContext(),
			// "Getting location please wait...", "In progress");
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

	public static boolean IsLocationUpdated() {
		if (curLocation != null)
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
		handler.removeCallbacks(GpsFinder);
		handler = null;
		// AppToast.ShowToast(this, "Stop all services..");
		// turnGPSOff();
		isService = false;
	}

	public void turnGPSOn() {
		Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
		intent.putExtra("enabled", true);
		sendBroadcast(intent);

		String provider = Settings.Secure.getString(getBaseContext()
				.getContentResolver(),
				Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if (!provider.contains("gps")) { // if gps is disabled
			final Intent poke = new Intent();
			poke.setClassName("com.android.settings",
					"com.android.settings.widget.SettingsAppWidgetProvider");
			poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
			poke.setData(Uri.parse("3"));
			sendBroadcast(poke);

		}
	}

	public void turnGPSOff() {

		Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
		intent.putExtra("enabled", false);
		sendBroadcast(intent);

		String provider = Settings.Secure.getString(getBaseContext()
				.getContentResolver(),
				Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if (provider.contains("gps")) { // if gps is enabled
			final Intent poke = new Intent();
			poke.setClassName("com.android.settings",
					"com.android.settings.widget.SettingsAppWidgetProvider");
			poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
			poke.setData(Uri.parse("3"));
			sendBroadcast(poke);
		}
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

			if (driver != null && curLocation != null)
				driver.UpdateLocationFromService(curLocation.getLatitude(),
						curLocation.getLongitude(), ServiceLocation.this);
			handler.postDelayed(GpsFinder, Common.DURATION);

		}
	};

	/*
	 * @SuppressWarnings("deprecation") public void showAlertDialog(Context
	 * context, String title, String message, Boolean status) { AlertDialog
	 * alertDialog = new AlertDialog.Builder(context).create();
	 * 
	 * // Setting Dialog Title alertDialog.setTitle(title);
	 * 
	 * // Setting Dialog Message alertDialog.setMessage(message);
	 * 
	 * // Setting alert dialog icon alertDialog.setIcon((status) ?
	 * R.drawable.success : R.drawable.fail);
	 * 
	 * // Setting OK Button alertDialog.setButton("OK", new
	 * DialogInterface.OnClickListener() { public void onClick(DialogInterface
	 * dialog, int which) { } });
	 * 
	 * // Showing Alert Message alertDialog.show(); }
	 */

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
		if (status) {
			// Location Updated
			Applog.Error(">>>ServiceLocation onResponse : location updated");
		} else
			Applog.Error(">>>ServiceLocation onResponse ERROR : location updated");
	}

	@Override
	public void onException(CustomHttpException exception) {
		Applog.Error(">>>ServiceLocation onException : " + exception.toString());
	}

}