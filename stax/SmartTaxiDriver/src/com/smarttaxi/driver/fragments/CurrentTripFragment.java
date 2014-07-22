package com.smarttaxi.driver.fragments;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.smarttaxi.driver.R;
import com.smarttaxi.driver.BAL.Driver;
import com.smarttaxi.driver.activities.EndJourneyActivity;
import com.smarttaxi.driver.activities.JourneyDetailActivity;
import com.smarttaxi.driver.activities.MainActivity;
import com.smarttaxi.driver.activities.ServiceLocation;
import com.smarttaxi.driver.constants.APIConstants;
import com.smarttaxi.driver.entities.CustomHttpException;
import com.smarttaxi.driver.entities.CustomHttpResponse;
import com.smarttaxi.driver.entities.Journey;
import com.smarttaxi.driver.exceptions.LocationNotFoundException;
import com.smarttaxi.driver.helpers.AlertBoxHelper;
import com.smarttaxi.driver.helpers.LoaderHelper;
import com.smarttaxi.driver.helpers.LocationManager;
import com.smarttaxi.driver.interfaces.HttpResponseListener;
import com.smarttaxi.driver.model.WebServiceModel;
import com.smarttaxi.driver.preferences.PreferencesHandler;
import com.smarttaxi.driver.utils.AppToast;
import com.smarttaxi.driver.utils.Applog;
import com.smarttaxi.driver.utils.Utils;

public class CurrentTripFragment extends Fragment implements
		android.view.View.OnClickListener, HttpResponseListener {
	LayoutInflater inflater;
	ViewGroup container;
	private int saved_journey_id;
	private Journey currentjourneyData;
	private long driver_id;
	private PreferencesHandler preferencesHandler;
	RelativeLayout journeyDetail;
	public TextView journeystatus;
	public String contactNo = "";
	public String emailAddress;
	View parentView, replacingView;
	RelativeLayout main_container_current;
	TextView customer_name, pickup_addrs, booking_time;
	Button bttn_call, bttn_msg, bttn_email, bttn_end_this_journey, bttn_beep,
			bttn_endjourney;
	public LatLng currentLatLng;
	private Boolean hasTelephony;
	private Boolean hasCamera;
	GetCurrentLocation getLocationAsync;

	/*public boolean isTelePhonyServiceAvailable() {
		if (((TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE)).getPhoneType() == TelephonyManager.PHONE_TYPE_NONE
				|| (((TelephonyManager) getActivity().getSystemService(
						Context.TELEPHONY_SERVICE)).getLine1Number() == null))
			return false;

		return true;

	}*/
	
	
	public boolean isDevicehasTelephonyFeature()
	{
		PackageManager mgr = getActivity().getPackageManager();
	    return mgr.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
		
	}
	
	/*public boolean hasTelephony()
	{
	    if(hasTelephony==null)
	    {
	        TelephonyManager tm=(TelephonyManager )getActivity().getSystemService(Context.TELEPHONY_SERVICE);
	        if(tm==null)
	        {
	            hasTelephony=new Boolean(false);
	            return hasTelephony.booleanValue();
	        }
	        if(Build.VERSION.SDK_INT < 5)
	        {
	            hasTelephony=new Boolean(true);
	            return hasTelephony;
	        }
	        PackageManager pm = getActivity().getPackageManager();
	        Method method=null;
	        if(pm==null)
	            return hasCamera=new Boolean(false);
	        else
	        {
	            try
	            {
	                Class[] parameters=new Class[1];
	                parameters[0]=String.class;
	                method=pm.getClass().getMethod("hasSystemFeature", parameters);
	                Object[] parm=new Object[1];
	                parm[0]=new String(PackageManager.FEATURE_TELEPHONY);
	                Object retValue=method.invoke(pm, parm);
	                if(retValue instanceof Boolean)
	                    hasTelephony=new Boolean(((Boolean )retValue).booleanValue());
	                else
	                    hasTelephony=new Boolean(false);
	            }
	            catch(Exception e)
	            {
	                hasTelephony=new Boolean(false);
	            }
	        }
	    }
	    return hasTelephony;
	}*/


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		getActivity().getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getLocationAsync = (GetCurrentLocation) new GetCurrentLocation()
				.execute("");

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		this.container = container;
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater
				.inflate(R.layout.activity_drawer_current_trip_fetcher,
						container, false);
		journeystatus = (TextView) rootView.findViewById(R.id.journeystatus);
		parentView = (ViewGroup) rootView
				.findViewById(R.id.main_container_current);

		replacingView = getLayoutInflater(savedInstanceState).inflate(
				R.layout.activity_drawer_current_trip, (ViewGroup) parentView,
				false);
		journeyDetail = (RelativeLayout) replacingView
				.findViewById(R.id.mainCon);

		customer_name = (TextView) replacingView.findViewById(R.id.cust_Text);
		pickup_addrs = (TextView) replacingView.findViewById(R.id.pickText);
		booking_time = (TextView) replacingView.findViewById(R.id.bookingvalue);
		bttn_call = (Button) replacingView.findViewById(R.id.call);
		bttn_msg = (Button) replacingView.findViewById(R.id.msg);
		bttn_email = (Button) replacingView.findViewById(R.id.email);
		bttn_end_this_journey = (Button) replacingView
				.findViewById(R.id.textBtn23);
		bttn_beep = (Button) replacingView.findViewById(R.id.btn_beep);
		bttn_endjourney = (Button) replacingView.findViewById(R.id.btn_cancell);
		journeyDetail.setOnClickListener(this);
		bttn_call.setOnClickListener(this);
		bttn_msg.setOnClickListener(this);
		bttn_email.setOnClickListener(this);
		bttn_end_this_journey.setOnClickListener(this);
		bttn_beep.setOnClickListener(this);
		bttn_endjourney.setOnClickListener(this);
		load(rootView);
		return rootView;

	}

	private void load(View rootView) {
		preferencesHandler = new PreferencesHandler(getActivity());

		driver_id = preferencesHandler.getOriginalDriverUserID();
		saved_journey_id = preferencesHandler.getSavedJourneyId();
		// saved_journey_id = 825;
		if (!Utils.isEmptyOrNull(String.valueOf(driver_id))
				&& saved_journey_id > 0) {

			LoaderHelper.showLoader(getActivity(), "Loading...", "");
			WebServiceModel.getCurrentJourney(String.valueOf(driver_id),
					String.valueOf(saved_journey_id), this);
		} else
			journeystatus.setText("Driver current journey may not exist.");
	}

	private void endThisJourney() {
		try {
			if (currentLatLng == null)
				return;

			String DropOffTime = Utils.getFormatedTimeString();
			LatLng _latlng = new LatLng(currentLatLng.latitude,
					currentLatLng.longitude);
			// String DropOffAddress =
			// Utils.getAddressString(currentLatitude,
			// currentLongitude, getBaseContext());
			String DropOffAddress = LocationManager.getInstance(getActivity())
					.getLocationAddress(_latlng);
			if (DropOffAddress == null)
				DropOffAddress = "";
			Intent myIntent = new Intent(getActivity(),
					EndJourneyActivity.class);
			Bundle b = new Bundle();

			if (!Utils.isEmptyOrNull(String.valueOf(saved_journey_id)))
				b.putString("journey_id", String.valueOf(saved_journey_id));
			else
				return;

			if (!Utils.isEmptyOrNull(String.valueOf(currentLatLng.latitude)))
				b.putDouble("dropOffLat", currentLatLng.latitude);
			else
				return;

			if (!Utils.isEmptyOrNull(String.valueOf(currentLatLng.longitude)))
				b.putDouble("dropOffLng", currentLatLng.longitude);
			else
				return;

			if (!Utils.isNull(currentjourneyData.getPickUpAddress()))
				b.putString("pickUp_door_address",
						currentjourneyData.getPickUpAddress());
			else
				return;

			if (!Utils.isNull(currentjourneyData.getUserPickUpTime()))
				b.putString("pickUptime",
						currentjourneyData.getUserPickUpTime());
			else
				return;

			if (!Utils.isNull(DropOffTime))
				b.putString("dropOfftime", DropOffTime);
			else
				return;

			if (!Utils.isNull(DropOffAddress))
				b.putString("dropOff_door_address", DropOffAddress);
			else
				return;

			if (!Utils.isEmptyOrNull(currentjourneyData.getUserPassword()))
				b.putString("password", currentjourneyData.getUserPassword());
			else
				return;

			if (!Utils.isNull(currentjourneyData.getUserTip()))
				b.putString("tip", currentjourneyData.getUserTip());
			else
				return;

			if (!Utils.isNull(currentjourneyData.getGetPickUpLatitude()))
				b.putString("pickup_lat",
						currentjourneyData.getGetPickUpLatitude());
			else
				return;

			if (!Utils.isNull(currentjourneyData.getGetPickUpLongitude()))
				b.putString("pickup_lng",
						currentjourneyData.getGetPickUpLongitude());
			else
				return;

			if (!Utils.isNull(currentjourneyData.getUserFirstName()))
				b.putString("driver_name",
						currentjourneyData.getUserFirstName() + " "
								+ currentjourneyData.getUserLastName());
			else
				return;

			if (!Utils.isNull(currentjourneyData.getCorporateName()))
				b.putString("company_name",
						currentjourneyData.getCorporateName());
			else
				b.putString("company_name", "");

			// b.putDouble("currentLat", currentLatitude);
			// b.putDouble("currentLng", currentLongitude);
			myIntent.putExtra("end_journey_data", b);
			startActivity(myIntent);

		} catch (Exception e) {
			// TODO: handle exception
			// AppToast.ShowToast(getActivity(), e.getMessage());
		}

	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onResponse(CustomHttpResponse object) {
		// TODO Auto-generated method stub
		String msg = null;
		if (LoaderHelper.progressDialog != null) {
			if (LoaderHelper.isLoaderShowing())
				LoaderHelper.hideLoader();
		}
		if (object.getStatusCode() == APIConstants.SUCESS_CODE) {

			/* ALL JOURNEY DETAIL DATA HERE */

			if (object.getResponse() instanceof Journey) {
				currentjourneyData = (Journey) object.getResponse();

				if (currentjourneyData != null) {

					// rootView
					// =inflater.inflate(R.layout.activity_drawer_current_trip,
					// container, false);
					/*
					 * TextView customer_name =
					 * (TextView)importPanel.findViewById(R.id.cust_Text);
					 * TextView pickup_addrs =
					 * (TextView)importPanel.findViewById(R.id.pickText);
					 * TextView booking_time =
					 * (TextView)importPanel.findViewById(R.id.bookingvalue);
					 */
					if (!Utils.isNull(currentjourneyData.getUserFirstName())
							&& !Utils.isNull(currentjourneyData
									.getUserLastName()))
						customer_name.setText(currentjourneyData
								.getUserFirstName()
								+ " "
								+ currentjourneyData.getUserLastName());

					// customer_name.setText("");
					if (!Utils.isNull(currentjourneyData.getPickUpAddress()))
						pickup_addrs.setText(currentjourneyData
								.getPickUpAddress());

					if (!Utils.isNull(currentjourneyData.getUserCreated()))
						booking_time.setText(currentjourneyData
								.getUserCreated());

					if (!Utils.isNull(currentjourneyData.getUserPhoneNo()))
						contactNo = currentjourneyData.getUserPhoneNo();

					if (!Utils.isNull(currentjourneyData.getUserUserName()))
						emailAddress = currentjourneyData.getUserUserName();

					// emailAddress
					((ViewGroup) parentView).removeAllViews();
					((ViewGroup) parentView).addView(replacingView);

				}
			} else if (object.getMethodName().equals(
					APIConstants.METHOD_CANCEL_JOURNEY)) {
				Driver driver = new Driver(getActivity());

				
				if (!object.getResponseMsg().contains("not")) {

					driver.enablePobStatus("no");
					driver.resetTaxiStatus("yes");
					com.smarttaxi.driver.BAL.Journey jrny = new com.smarttaxi.driver.BAL.Journey(
							getActivity());
					jrny.removeSavedJourney();
					// MainActivity.mFragmentStack.clear();

					//Intent i = new Intent(getActivity(), MainActivity.class);
					//i.putExtra("IsRegistered", true);
					/*
					 * i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					 * i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					 * i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					 */
					MainActivity.isRequestShown = false;

					//getActivity().startActivity(i);
					getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
					getActivity().getActionBar().setTitle("");

				}
				else
					AlertBoxHelper.showAlertBox(getActivity(), "",
							object.getResponseMsg());

			}

			else if (object.getMethodName().equals(
					APIConstants.METHOD_SEND_BEEP_NOTIFICATION)) {
				AlertBoxHelper.showAlertBox(getActivity(), "",
						object.getResponseMsg());
			}

		} else {
			if (object != null) {
				journeystatus.setText(object.getResponseMsg());
				AppToast.ShowToast(getActivity(), object.getResponseMsg());
			}
			// com.smarttaxi.driver.BAL.Journey _journey = new
			// com.smarttaxi.driver.BAL.Journey(getActivity());
			// _journey.removeSavedJourney();
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		if (getLocationAsync.isCancelled())
			getLocationAsync = (GetCurrentLocation) new GetCurrentLocation()
					.execute("");

	}

	@Override
	public void onPause() {
		super.onPause();
		getLocationAsync.cancel(true);
	}

	private void sendSms(String contactNo) {

		/*if (isTelePhonyServiceAvailable()) {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"
					+ contactNo));
			intent.putExtra("sms_body", "Write your message.");
			startActivity(intent);
		} else
			featureNotFoundAlert("Can't Send Message.",
					"Seems your device does'nt have the telephony service.");*/

	}

	private void call(String ContactNo) {
		try {

			if (!isDevicehasTelephonyFeature())
				featureNotFoundAlert("Can't Make Call.",
						"Seems your device does'nt have the telephony service.");

			else {

				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:" + ContactNo));
				startActivity(callIntent);
			}

		} catch (ActivityNotFoundException e) {
			Log.e("Current Trip", "Call failed", e);
		}
	}

	private void sendEmail(String emailAddrs) {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL, new String[] { emailAddrs });
		i.putExtra(Intent.EXTRA_SUBJECT, "Subject");
		i.putExtra(Intent.EXTRA_TEXT, "Write your email");
		try {
			startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(getActivity(),
					"There are no email clients installed.", Toast.LENGTH_SHORT)
					.show();
		}

	}

	@SuppressWarnings("deprecation")
	private void featureNotFoundAlert(String title, String message) {

		// Creating alert Dialog with one Button

		AlertDialog noCallAlert = new AlertDialog.Builder(getActivity())
				.create();

		// Setting Dialog Title
		noCallAlert.setTitle(title);

		// Setting Dialog Message
		noCallAlert.setMessage(message);

		// Setting Icon to Dialog
		// alertDialog1.setIcon(R.drawable.tick);

		// Setting OK Button
		noCallAlert.setButton("OK", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		});

		// Showing Alert Message
		noCallAlert.show();

	}

	@Override
	public void onException(CustomHttpException exception) {
		// TODO Auto-generated method stub
		if (LoaderHelper.progressDialog != null) {
			if (LoaderHelper.isLoaderShowing()) {
				LoaderHelper.hideLoader();
				AppToast.ShowToast(getActivity(), exception.getMessage());
			}

		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.call:
			if (!Utils.isEmptyOrNull(contactNo))
				call(contactNo);
			else

				AppToast.ShowToast(getActivity(), "Contact no is not available");
			break;

		case R.id.msg:
			if (!Utils.isEmptyOrNull(contactNo))
				sendSms(contactNo);
			break;

		case R.id.email:
			if (!Utils.isEmptyOrNull(emailAddress))
				sendEmail(emailAddress);
			break;

		case R.id.textBtn23:
			endThisJourney();
			break;

		case R.id.btn_beep:
			String userid = currentjourneyData.getUserId();
			String driverId = String.valueOf(preferencesHandler
					.getOriginalDriverUserID());
			if (!Utils.isEmptyOrNull(userid) && !Utils.isEmptyOrNull(driverId)) {

				WebServiceModel.sendBeep(this, userid, driverId,
						"Your driver has arrived.");
				LoaderHelper.showLoader(getActivity(), "Loading...", "");
			}
			break;

		case R.id.btn_cancell:

			
			try {
				
				// CALL CANCEL SERVICE
				if (!Utils.isEmptyOrNull(currentjourneyData.getUserId())
						&& !Utils.isEmptyOrNull(currentjourneyData.getJourneysId())) {
					LoaderHelper.showLoader(getActivity(), "Loading...", "");
					WebServiceModel.cancelJourney(
							CurrentTripFragment.this,
							currentjourneyData.getUserId(),
							currentjourneyData.getJourneysId());// user_id

				}

				/*Driver driver = new Driver(getActivity());
				driver.enablePobStatus("no");
				com.smarttaxi.driver.BAL.Journey jrny = new com.smarttaxi.driver.BAL.Journey(
						getActivity());
				jrny.removeSavedJourney();
				// MainActivity.mFragmentStack.clear();

				Intent i = new Intent(getActivity(), MainActivity.class);
				i.putExtra("IsRegistered", true);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				MainActivity.isRequestShown = false;

				startActivity(i);*/

			} catch (Exception e) {
				// TODO: handle exception
				AppToast.ShowToast(getActivity(), e.getMessage());
			}

			break;

		case R.id.mainCon:
			if (currentjourneyData != null) {
				Intent myIntent = new Intent(v.getContext(),
						JourneyDetailActivity.class);
				myIntent.setType("text/plain");
				myIntent.putExtra(android.content.Intent.EXTRA_TEXT,
						currentjourneyData.getJourneysId());

				myIntent.putExtra("isFromFindARide", false);

				// myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				myIntent.putExtra("currentLat",
						ServiceLocation.curLocation.getLatitude());
				myIntent.putExtra("currentLng",
						ServiceLocation.curLocation.getLongitude());
				v.getContext().startActivity(myIntent);
			}
			break;

		default:
			break;
		}

	}

	private class GetCurrentLocation extends AsyncTask<String, Void, String> {

		// String address;
		LatLng userLatLng;

		@Override
		protected String doInBackground(String... params) {

			try {

				Location location = LocationManager.getInstance(getActivity())
						.getCurrentLocation();
				userLatLng = new LatLng(location.getLatitude(),
						location.getLongitude());

				if (userLatLng.latitude == 0.0 && userLatLng.longitude == 0.0) {
					return "";
				}

				Applog.Debug("1Latitude" + userLatLng.latitude);
				Applog.Debug("1Longitude" + userLatLng.longitude);
				currentLatLng = userLatLng;
				/*
				 * address = LocationManager.getInstance(getActivity())
				 * .getLocationAddress(currentLatLng);
				 */

				/* isUserMarkerCreated = true; */

			}

			catch (LocationNotFoundException e) {
				e.printStackTrace();
			}

			return "Executed";
		}

		@Override
		protected void onPostExecute(String result) {

		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}
}
