package com.smarttaxi.driver.fragments;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGestureListener;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.smarttaxi.driver.R;
import com.smarttaxi.driver.BAL.Driver;
import com.smarttaxi.driver.activities.EndJourneyActivity;
import com.smarttaxi.driver.activities.JourneyDetailActivity;
import com.smarttaxi.driver.activities.ServiceLocation;
import com.smarttaxi.driver.adapters.LeftMenuAdapter;
import com.smarttaxi.driver.constants.APIConstants;
import com.smarttaxi.driver.custom.design.CircularImageView;
import com.smarttaxi.driver.custom.design.TransparentPanel;
import com.smarttaxi.driver.entities.AcceptOrRejectJourney;
import com.smarttaxi.driver.entities.CustomHttpException;
import com.smarttaxi.driver.entities.CustomHttpResponse;
import com.smarttaxi.driver.entities.Journey;
import com.smarttaxi.driver.exceptions.LocationNotFoundException;
import com.smarttaxi.driver.helpers.AlertBoxHelper;
import com.smarttaxi.driver.helpers.DownloadImageAsync;
import com.smarttaxi.driver.helpers.LoaderHelper;
import com.smarttaxi.driver.helpers.LocationManager;
import com.smarttaxi.driver.interfaces.HttpResponseListener;
import com.smarttaxi.driver.model.WebServiceModel;
import com.smarttaxi.driver.preferences.PreferencesHandler;
import com.smarttaxi.driver.utils.AppToast;
import com.smarttaxi.driver.utils.Applog;
import com.smarttaxi.driver.utils.Utils;

public class JourneyDetailFragment extends Fragment implements
OnGestureListener, OnClickListener, HttpResponseListener {

	private FindARideFragment findArideFragment;
	private float lastX;
	private static Animation animShow, animHide;
	public static Journey journeyData;
	DrawerLayout drawerLayout;
	ListView listViewDrawer;
	RelativeLayout layoutDrawer;
	ActionBarDrawerToggle actionBarDrawerToggle;
	LeftMenuAdapter leftMenuAdapter;
	String currentJourneyID;
	Button cancelJourney;
	Button beep;
	Button endJourney;
	private PreferencesHandler preferencesHandler;
	public Button popupBottomBarButton;
	com.smarttaxi.driver.custom.design.TransparentPanel popup;
	View rootView;
	Button button;

	FrameLayout mainPopup;
	FrameLayout frameLayoutContent;
	FindARideFragment fragment;
	private Double currentLatitude, currentLongitude;
	TextView mDisplay;
	FindARideFragment findaRide;
	
	
	/* public static JourneyDetailFragment newInstance(int index) {
		 JourneyDetailFragment f = new JourneyDetailFragment();

	        // Supply index input as an argument.
	        Bundle args = new Bundle();
	        
	        args.putInt("index", index);
	        f.setArguments(args);

	        return f;
	    }
	 
	 public int getShownIndex() {
	        return getArguments().getInt("index", 0);
	    }*/
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Bundle extras = getActivity().getIntent().getExtras();
		if (extras == null)
			return;

		/*currentJourneyID = extras.getString(Intent.EXTRA_TEXT);
		currentLatitude = extras.getDouble("currentLat");
		currentLongitude = extras.getDouble("currentLng");
		if (currentJourneyID != null) {
			LoaderHelper.showLoader(getActivity(), "Loading...", "");
			// String userName = editTxtUserName.getText().toString();
			// String password = editTextPassword.getText().toString();

			preferencesHandler = new PreferencesHandler(getActivity());
			if (preferencesHandler.getOriginalDriverUserID() > 0) {
				String UserID = String.valueOf(preferencesHandler
						.getOriginalDriverUserID());
				WebServiceModel.callAccepted("accept", UserID,
						currentJourneyID, this);
			}

		}*/
        
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		
		 rootView = inflater.inflate(R.layout.activity_journeydetail,
				container, false);
		 
		 FragmentManager fm = getChildFragmentManager();
			findArideFragment = (FindARideFragment) fm
					.findFragmentByTag("initialfindaRideFragment");

			if (findArideFragment == null)
				findArideFragment = new FindARideFragment();
				// fm.beginTransaction().add(findArideFragment,
				// "task").commit();
				/*fm.beginTransaction()
						.replace(R.id.frameLayoutContent,
								findArideFragment,
								"initialfindaRideFragment").commit();*/
			
		
		initUI();
		
		try {
			int resultCode = GooglePlayServicesUtil
					.isGooglePlayServicesAvailable(getActivity());
			if (ConnectionResult.SUCCESS == resultCode) {
				LocationManager.getInstance(getActivity()).startLocationService();
			} else {
				Toast.makeText(getActivity(), "GOOGLE PLAY SERVICES ARE NOT AVAILABLE",
						Toast.LENGTH_LONG).show();
			}

			
			

			/*fragment = new FindARideFragment();
			FragmentManager fragmentManager =  getChildFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();
			fragmentTransaction.replace(R.id.frameLayoutContent, fragment);
			fragmentTransaction.commit();*/

			popupBottomBarButton.setOnClickListener(this);

			beep.setOnClickListener(this);
			cancelJourney.setOnClickListener(this);
			endJourney.setOnClickListener(this);

		} catch (Exception e) {

			Applog.Error("com.smarttaxi.driver.activities : OnCreate() : Message= "
					+ e.getMessage());
			// TODO: handle exception
		}

		return rootView;
	}
	private void initUI() {

		
		cancelJourney = (Button) rootView.findViewById(R.id.cancelJourney);
		beep = (Button) rootView.findViewById(R.id.beep);
		endJourney = (Button) rootView.findViewById(R.id.endjourney);
		// profileImage =
		// (CircularImageView)findViewById(R.id.circular_image_view);
		popupBottomBarButton = (Button) rootView.findViewById(R.id.show_popup_button);
		popup = (TransparentPanel) rootView.findViewById(R.id.popup_window);
	
		popup.setVisibility(View.GONE);

	}
	
	private void getBottomBarPopup() {

		animShow = AnimationUtils.loadAnimation(getActivity(), R.anim.popup_show);
		animHide = AnimationUtils.loadAnimation(getActivity(), R.anim.popup_hide);

		

				if (!popup.isShown()) {
					popup.setVisibility(View.VISIBLE);
					popup.startAnimation(animShow);
					popupBottomBarButton.setEnabled(true);
					// hideButton.setEnabled(true);

				} else {

					popup.startAnimation(animHide);
					popupBottomBarButton.setEnabled(true);
					// hideButton.setEnabled(false);
					popup.setVisibility(View.GONE);

				}

		
	}
	
	private void getDetailsOfJourney(String currentJId) {
		// TODO Auto-generated method stub
		
			
		WebServiceModel.getJourneyDetails(currentJId, this);

	}
	private void PlotPath(Double _pickLat, Double _pickLong) {
		// TODO Auto-generated method stub

		final LatLng ToLoc;
		if ((!_pickLat.equals(null) && !_pickLat.equals(""))
				&& (!_pickLong.equals(null) && !_pickLong.equals(""))) {
			/*
			 * if ((getApplication().getApplicationInfo().flags &
			 * ApplicationInfo.FLAG_DEBUGGABLE) != 0) { ToLoc = new
			 * LatLng(Double.parseDouble("25.407384"),
			 * Double.parseDouble("68.355635")); } else ToLoc = new
			 * LatLng(_pickLat, _pickLong);
			 */
			
			ToLoc = new LatLng(_pickLat, _pickLong);

		} else
			ToLoc = new LatLng(Double.parseDouble("25.407384"),
					Double.parseDouble("68.355635"));

		LatLng FromLoc = new LatLng(currentLatitude, currentLongitude);

		Routing routing = new Routing(Routing.TravelMode.DRIVING);

		routing.registerListener(new RoutingListener() {

			@Override
			public void onRoutingSuccess(PolylineOptions mPolyOptions) {
				// TODO Auto-generated method stub
				PolylineOptions polyoptions = new PolylineOptions();
				polyoptions.color(Color.BLACK);
				polyoptions.width(10);
				polyoptions.addAll(mPolyOptions.getPoints());
				fragment.googleMap.addPolyline(polyoptions);
				Marker markerobj = fragment.googleMap.addMarker(new MarkerOptions()
						.position(ToLoc)
						.title("Location")
						.snippet(
								"Lat: " + ToLoc.latitude + ", Long: "
										+ ToLoc.longitude)
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.map_user_icon)));

				fragment.markersList.add(markerobj);

				fragment.zoomToAdjustAllMarkers(currentLatitude,
						currentLongitude);

				if (LoaderHelper.isLoaderShowing())
					LoaderHelper.hideLoader();
				// googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new
				// LatLng((currentLatLng.latitude/currentLatLng.longitude)*2,
				// (dest.latitude/dest.longitude)*2), zoomNormal));
			}

			@Override
			public void onRoutingStart() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onRoutingFailure() {
				// TODO Auto-generated method stub

			}
		});
		routing.execute(FromLoc, ToLoc);

	}
	
	private void UpdatePopUpData(String journeyNotificationStatus,
			String pickUpAddress, String userPickUpTime,
			String userOptionalMessage, String maxNoOfPassenger,
			String userNoOfBags, String userChildSeats, String userFirstName,
			String userLastName, String userImage) {
		// TODO Auto-generated method stub

		TextView handleStatus = (TextView) rootView.findViewById(R.id.handler_status);
		if (!journeyNotificationStatus.equals(""))
			handleStatus.setText(journeyNotificationStatus.toLowerCase()
					.equals("driver assigned") ? "Driver assigned" : "");
		else
			handleStatus.setText("pending...");

		TextView pickAddrs = (TextView) rootView.findViewById(R.id.pickText);
		if (!pickUpAddress.equals(""))
			pickAddrs.setText(pickUpAddress);

		TextView pickTime = (TextView)rootView.findViewById(R.id.pickTime);
		if (!userPickUpTime.equals(""))
			pickTime.setText(Utils.getFormattedDate(userPickUpTime));

		TextView opnMsg = (TextView) rootView.findViewById(R.id.opnMsg);
		if (!userOptionalMessage.equals(""))
			opnMsg.setText(userOptionalMessage);

		TextView passenger = (TextView) rootView.findViewById(R.id.passenger_value);
		if (!maxNoOfPassenger.equals(""))
			passenger.setText(maxNoOfPassenger);

		TextView bags = (TextView) rootView.findViewById(R.id.bags_value);
		if (!userNoOfBags.equals(""))
			bags.setText(userNoOfBags);

		TextView childSeat = (TextView) rootView.findViewById(R.id.childseats_value);
		if (!userChildSeats.equals(""))
			childSeat.setText(userChildSeats);

		CircularImageView userProfileImage = (CircularImageView) rootView.findViewById(R.id.circular_image_view);
		userProfileImage.setDrawingCacheEnabled(true);
		if (!userImage.equals(""))
			new DownloadImageAsync((ImageView) userProfileImage,
					getActivity()).execute(userImage);

		TextView Customername = (TextView) rootView.findViewById(R.id.user_name);
		if (!userFirstName.equals("") && !userLastName.equals(""))
			Customername
					.setText(userFirstName.concat(" ").concat(userLastName));

	}
	
	@Override
	public void onDestroy() {
		/*
		 * if (mRegisterTask != null) { mRegisterTask.cancel(true); }
		 */
		// unregisterReceiver(mHandleMessageReceiver);
		// GCMRegistrar.onDestroy(this);
		super.onDestroy();
	}

	@Override
	 public void onResponse(CustomHttpResponse object) {
	  // TODO Auto-generated method stub
	  String msg = null;
	  
	  if (object.getStatusCode() == APIConstants.SUCESS_CODE) {

	   if (object.getResponse() instanceof AcceptOrRejectJourney) {
	    AcceptOrRejectJourney acceptReq = (AcceptOrRejectJourney) object
	      .getResponse();
	    if(acceptReq!=null)
	    {
	    Driver driver = new Driver(getActivity());
	    driver.enablePobStatus("yes");
	    ///driver.resetTaxiStatus("no");
	    
	    com.smarttaxi.driver.BAL.Journey _journey = new com.smarttaxi.driver.BAL.Journey(getActivity());
	    
	    msg = acceptReq.getNotificationMsg();
	    _journey.SaveJourney(currentJourneyID);
	    getDetailsOfJourney(currentJourneyID);
	   
	    
	    }
	    

	    
	   } else if (object.getResponse() instanceof Journey) {

	    /* ALL JOURNEY DETAIL DATA HERE */
	    journeyData = (Journey) object.getResponse();

	    if (journeyData != null) {
	     
	     /* We have all the details here For journey */
	     Log.v("1", journeyData.getJourneyCapId());
	     Log.v("2", journeyData.getJourneyUserId());
	     Log.v("3", journeyData.getUserId());

	     /*PlotPath(Double.parseDouble(journeyData
	       .getGetPickUpLatitude()),
	       Double.parseDouble(journeyData
	         .getGetPickUpLongitude()));*/
	     if(currentLatitude!=null && currentLongitude!=null)
	     PlotPath(currentLatitude,
	  	       currentLongitude);
	     

	     UpdatePopUpData(journeyData.getJourneyNotificationStatus(),
	       journeyData.getPickUpAddress(),
	       journeyData.getUserPickUpTime(),
	       journeyData.getUserOptionalMessage(),
	       journeyData.getMaxNoOfPassenger(),
	       journeyData.getUserNoOfBags(),
	       journeyData.getUserChildSeats(),
	       journeyData.getUserFirstName(),
	       journeyData.getUserLastName(),
	       journeyData.getUserImage());

	    }
	   }
	   
	   else if (object.getMethodName().equals(
	     APIConstants.METHOD_SEND_BEEP_NOTIFICATION)
	     || object.getMethodName().equals(
	       APIConstants.METHOD_CANCEL_JOURNEY)) {
	    AlertBoxHelper.showAlertBox(getActivity(), "",
	      object.getResponseMsg());
	    
	    //if journey is cancelled then reset preference values
	    if(!object.getResponseMsg().contains("not"))
	    {
	    	 Driver driver = new Driver(getActivity());
	    	    driver.enablePobStatus("no");
	    }

	   }

	   

	  }  
	  if (LoaderHelper.progressDialog != null) {
	   if (LoaderHelper.isLoaderShowing())
	    LoaderHelper.hideLoader();
	  }
	  
	  else
	  {
	   AppToast.ShowToast(getActivity(), object.getResponseMsg());
	   if (LoaderHelper.progressDialog != null) {
	    if (LoaderHelper.isLoaderShowing())
	     LoaderHelper.hideLoader();
	   }
	   
	   
	  }

	 }


	@Override
	public void onException(CustomHttpException exception) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if(journeyData==null)
			return;
		switch (v.getId()) {

		case R.id.show_popup_button:
			getBottomBarPopup();
			break;
		case R.id.cancelJourney:
			// CALL CANCEL SERVICE
			if (!Utils.isEmptyOrNull(journeyData.getUserId())
					&& !Utils.isEmptyOrNull(currentJourneyID)) {
				LoaderHelper.showLoader(getActivity(),
						"Loading...", "");
				WebServiceModel.cancelJourney(this,
						journeyData.getUserId(), currentJourneyID);// user_id
				
			}
			break;
		case R.id.beep:
			// CALL BEEP SERVICE
			if (!Utils.isEmptyOrNull(journeyData.getUserId())
					&& !Utils.isNull((journeyData.getUserOptionalMessage()))) {
				WebServiceModel.sendBeep(this,
						journeyData.getUserId(), String
								.valueOf(preferencesHandler
										.getOriginalDriverUserID()),
						"Your driver has arrived."); // user_is_to &
				// message
				LoaderHelper.showLoader(getActivity(),
						"Loading...", "");
			}
			break;
		case R.id.endjourney:
			// CALL END JOURNEY
			try {
				String DropOffTime = Utils.getFormatedTimeString();
				LatLng _latlng = new LatLng(currentLatitude, currentLongitude);
				// String DropOffAddress =
				// Utils.getAddressString(currentLatitude,
				// currentLongitude, getBaseContext());
				String DropOffAddress = LocationManager.getInstance(getActivity())
						.getLocationAddress(_latlng);
				if (DropOffAddress == null)
					DropOffAddress = "";
				Intent myIntent = new Intent(getActivity(), EndJourneyActivity.class);
				Bundle b = new Bundle();
				if (journeyData != null) {
					if (!Utils.isEmptyOrNull(currentJourneyID))
						b.putString("journey_id", currentJourneyID);
					else
						return;

					if (!Utils.isEmptyOrNull(String.valueOf(currentLatitude)))
						b.putDouble("dropOffLat", currentLatitude);
					else
						return;

					if (!Utils.isEmptyOrNull(String.valueOf(currentLongitude)))
						b.putDouble("dropOffLng", currentLongitude);
					else
						return;

					if (!Utils.isNull(journeyData.getPickUpAddress()))
						b.putString("pickUp_door_address",
								journeyData.getPickUpAddress());
					else
						return;

					if (!Utils.isNull(journeyData.getUserPickUpTime()))
						b.putString("pickUptime",
								journeyData.getUserPickUpTime());
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

					if (!Utils.isEmptyOrNull(journeyData.getUserPassword()))
						b.putString("password", journeyData.getUserPassword());
					else
						return;

					if (!Utils.isNull(journeyData.getUserTip()))
						b.putString("tip", journeyData.getUserTip());
					else
						return;

					if (!Utils.isNull(journeyData.getGetPickUpLatitude()))
						b.putString("pickup_lat",
								journeyData.getGetPickUpLatitude());
					else
						return;

					if (!Utils.isNull(journeyData.getGetPickUpLongitude()))
						b.putString("pickup_lng",
								journeyData.getGetPickUpLongitude());
					else
						return;

					if (!Utils.isNull(journeyData.getUserFirstName()))
						b.putString("driver_name",
								journeyData.getUserFirstName() + " "
										+ journeyData.getUserLastName());
					else
						return;

					if (!Utils.isNull(journeyData.getCorporateName()))
						b.putString("company_name",
								journeyData.getCorporateName());
					else
						b.putString("company_name", "");

					// b.putDouble("currentLat", currentLatitude);
					// b.putDouble("currentLng", currentLongitude);
					myIntent.putExtra("end_journey_data", b);
					startActivity(myIntent);
				}
			} catch (Exception e) {
				// TODO: handle exception
				AppToast.ShowToast(getActivity(), e.getMessage());
			}
			break;

		default:
			break;
		}

	}

	@Override
	public void onGesture(GestureOverlayView arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGestureEnded(GestureOverlayView overlay, MotionEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	class MapLocationChangedReciever extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			// AppToast.ShowToast(getActivity(), "onReceive() called");
			if ( fragment.googleMap == null) {
				return;
			}

			try {
				// check just
				
					new UpdateMapLocation().execute("");
					

				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private class UpdateMapLocation extends AsyncTask<String, Void, String> {

		
		LatLng userLatLng;

		@Override
		protected String doInBackground(String... params) {

			try {
				

			Location fusedlocation = LocationManager.getInstance(getActivity())
						.getCurrentLocation();

				userLatLng = new LatLng(fusedlocation.getLatitude(),
						fusedlocation.getLongitude());
				/*
				 * ServiceLocation.curLocation.setLatitude(userLatLng.latitude);
				 * ServiceLocation
				 * .curLocation.setLongitude(userLatLng.longitude);
				 */

				if (userLatLng.latitude == 0.0 && userLatLng.longitude == 0.0) {
					return "";
				}

				Applog.Error("1Latitude" + userLatLng.latitude);
				Applog.Error("1Longitude" + userLatLng.longitude);
				
				

				
			}

			catch (LocationNotFoundException e) {
				e.printStackTrace();
			}

			return "Executed";
		}

		@Override
		protected void onPostExecute(String result) {

			try {
				
				
				//update marker
				Applog.Error("Start Updating");

				
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}
	
	
	

}
	

