package com.smarttaxi.driver.fragments;

import java.util.ArrayList;
import java.util.ResourceBundle.Control;
import android.R.bool;
import android.R.color;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.smarttaxi.driver.R;
import com.smarttaxi.driver.BAL.Driver;
import com.smarttaxi.driver.activities.EndJourneyActivity;
import com.smarttaxi.driver.activities.JourneyDetailActivity;
import com.smarttaxi.driver.activities.MainActivity;
import com.smarttaxi.driver.activities.ServiceLocation;
import com.smarttaxi.driver.components.CabInfoWindow;
import com.smarttaxi.driver.components.ProgressWheelLoader;
import com.smarttaxi.driver.constants.APIConstants;
import com.smarttaxi.driver.custom.design.ActionItem;
import com.smarttaxi.driver.custom.design.DriverOptionsPopup;
import com.smarttaxi.driver.entities.Cab;
import com.smarttaxi.driver.entities.CustomHttpException;
import com.smarttaxi.driver.entities.CustomHttpResponse;
import com.smarttaxi.driver.entities.DeviceRegister;
import com.smarttaxi.driver.exceptions.LocationNotFoundException;
import com.smarttaxi.driver.helpers.GoogleAPIHelper;
import com.smarttaxi.driver.helpers.LoaderHelper;
import com.smarttaxi.driver.helpers.LocationManager;
import com.smarttaxi.driver.interfaces.HttpProgressListener;
import com.smarttaxi.driver.interfaces.HttpResponseListener;
import com.smarttaxi.driver.model.WebServiceModel;
import com.smarttaxi.driver.preferences.PreferencesHandler;
import com.smarttaxi.driver.utils.AppToast;
import com.smarttaxi.driver.utils.Applog;
import com.smarttaxi.driver.utils.NetworkAvailability;
import com.smarttaxi.driver.utils.Utils;

@SuppressLint("Instantiatable")
public class FindARideFragment extends Fragment implements LocationListener,
		HttpResponseListener, HttpProgressListener, OnClickListener,
		OnCameraChangeListener, OnMarkerDragListener {

	public static final String TAG =FindARideFragment.class.getSimpleName(); 
	
	private static String type = "";
	public static DriverOptionsPopup mQuickAction;
	public MapView mapView;
	public GoogleMap googleMap;
	public static Button btnPassengerOnBoard, btnCheckin;
	public static boolean isPathplotted = false;
	private LinearLayout layoutBottomButtons;
	MapLocationChangedReciever locationChangedReciever;
	RelativeLayout parentView;
	Marker lastOpenned = null;
	boolean isFirstRoundOfCallCompleted = false;
	// Button btnFindARide;
	Button btnOptions;
	// Button btnPickUpAddress;
	// Button btnPickUpHere;

	ImageButton locate;
	// public static boolean isPathplotted =false;
	public static LatLng currentLatLng;
	public static Location fusedlocation;
	public ArrayList<Marker> markersList;
	ProgressWheelLoader progressWheelLoader;
	private static TextView txtTaxiStatus, txtLocation;
	
	ArrayList<Cab> cabsList;

	Context context;
	private double latitude;
	private double longitude;
	private int zoomNormal = 17;

	private static Marker userLocationMarker;
	Location location;
	String mapException = "";
	private LocationManager locationManager;
	private String provider;
	private LinearLayout relativeLayoutControls;
	private PreferencesHandler preferencesHandler;
	private static long USER_ID;
	private static final String TYPE_POST_CHECK_IN = "post_check_in",
			TYPE_PASSENGER_ON_BOARD = "pob",
			TYPE_CAR_IS_AVAILABLE = "cab_available",
			TYPE_CAR_IS_NOT_AVAILABLE = "cab_not_available";
	private static Boolean isCarAvailable, isPobEnabled, isPostCheckinEnabled;
	private static Boolean isPobButtonEnabled, isPostCheckinButtonEnabled;
	private static ToggleButton availabilityToggle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// setRetainInstance(true);
		getUpdatedValues();
		getActivity().getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

	}

	public void getUpdatedValues() {
		if (preferencesHandler == null)
			preferencesHandler = new PreferencesHandler(getActivity());

		USER_ID = preferencesHandler.getOriginalDriverUserID();
		isCarAvailable = getCarAvaialibility(preferencesHandler
				.getDriverCabAvailablityStatus());
		isPobEnabled = getPobStatus(preferencesHandler
				.getDriverPassengerOnBoardStatus());
		isPostCheckinEnabled = getPostCheckinStatus(preferencesHandler
				.getDriverPostCheckInStatus());

	}

	public boolean getPobStatus(String pobStatus) {
		if (!Utils.isEmptyOrNull(pobStatus))
			return pobStatus.equalsIgnoreCase("yes") ? true : false;

		return false;
	}

	public boolean getPostCheckinStatus(String postCheckinStatus) {
		if (!Utils.isEmptyOrNull(postCheckinStatus))
			return postCheckinStatus.equalsIgnoreCase("yes") ? true : false;

		return false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_map, container,
				false);
		try
		{
		// AppToast.ShowToast(getActivity(), "fr:onCreateView");
		
		mapView = (MapView) rootView.findViewById(R.id.mapView);
		progressWheelLoader = (ProgressWheelLoader) rootView
				.findViewById(R.id.progressWheelLoader);

		btnOptions = (Button) rootView.findViewById(R.id.btnOptions);
		btnOptions.setOnClickListener(this);
		txtTaxiStatus = (TextView) rootView.findViewById(R.id.txtTaxiStatus);
		txtLocation = (TextView) rootView.findViewById(R.id.txtDriverLocation);
		layoutBottomButtons = (LinearLayout) rootView
				.findViewById(R.id.layoutBottomButtons);
		locate = (ImageButton) rootView.findViewById(R.id.button_locate);
		locate.setOnClickListener(this);

		mapView.onCreate(savedInstanceState);
		startLoadingMap();

		markersList = new ArrayList<Marker>();

		relativeLayoutControls = (LinearLayout) rootView
				.findViewById(R.id.location_layout);
		relativeLayoutControls.bringToFront();

		initMap();
		initOptionsPopUp();
		
		if (fusedlocation != null) {

			if (fusedlocation.getTime() > ServiceLocation.curLocation.getTime())
				setMapMarkerPosition(fusedlocation.getLatitude(),
						fusedlocation.getLongitude(), zoomNormal);
			else {
				setMapMarkerPosition(ServiceLocation.curLocation.getLatitude(),
						ServiceLocation.curLocation.getLongitude(), zoomNormal);

			}

		} else {

			setMapMarkerPosition(ServiceLocation.curLocation.getLatitude(),
					ServiceLocation.curLocation.getLongitude(), zoomNormal);

		}


		/*
		 * if (btnPassengerOnBoard != null && btnCheckin != null) { if
		 * (isPobEnabled) enableServiceButtons(btnPassengerOnBoard, btnCheckin,
		 * "Passenger on board");
		 * 
		 * else if (isPostCheckinEnabled) enableServiceButtons(btnCheckin,
		 * btnPassengerOnBoard, "Post check in"); }
		 */
		}
		catch(Throwable t)
		{
			Applog.Error(TAG + " onCreateView() Exception");
			t.printStackTrace();
			
		}

		return rootView;
		
	}

	public void startLoadingMap() {
		try {

			MapsInitializer.initialize(getActivity());
			if (googleMap == null) {

				googleMap = mapView.getMap();
				
				Applog.Error(">>> Map Loading...");
				if (!isPathplotted)
					LoaderHelper.showProgressLoaderForFragments(
							(Context) getActivity(),
							"Getting location please wait...", "In progress");
				googleMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {

					@Override
					public void onMapLoaded() {
						// TODO Auto-generated method stub
						if (googleMap != null) {
							if (LoaderHelper.mProgressDialog != null) {
								LoaderHelper.closeProgressLoader();
							}
							Applog.Error(">>>> Map Loaded");
						}

					}
				});

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void initOptionsPopUp() {
		// TODO Auto-generated method stub

		mQuickAction = new DriverOptionsPopup(getActivity());

		parentView = (RelativeLayout) mQuickAction.mRootView
				.findViewById(R.id.tracks);
		btnCheckin = (Button) mQuickAction.mRootView
				.findViewById(R.id.check_in);
		btnCheckin.setOnClickListener(this);

		btnPassengerOnBoard = (Button) mQuickAction.mRootView
				.findViewById(R.id.passenger_on_board);
		btnPassengerOnBoard.setOnClickListener(this);

		availabilityToggle = (ToggleButton) mQuickAction.mRootView
				.findViewById(R.id.cab_availability_toggle);
		availabilityToggle.setOnClickListener(this);
		availabilityToggle.setText(null);
		availabilityToggle.setTextOn(null);
		availabilityToggle.setTextOff(null);

		Button btn_cancel = (Button) mQuickAction.mRootView
				.findViewById(R.id.btn_cancel);
		btn_cancel.setOnClickListener(this);

		/*
		 * mQuickAction .setOnDismissListener(new
		 * DriverOptionsPopup.OnDismissListener() {
		 * 
		 * @Override public void onDismiss() { Toast.makeText(getActivity(),
		 * "options..dismissed", Toast.LENGTH_SHORT).show(); } });
		 */

		setDriverStatus();
		// setPopupButtons();

	}

	@SuppressWarnings("deprecation")
	public void disableTaxiAvailalbleMode() {
		parentView.setClickable(false);
		GradientDrawable makeitGray = new GradientDrawable();

		makeitGray.setColor(Color.GRAY);

		btnCheckin.setClickable(false);
		btnCheckin.setText("Post check in");
		reSetColorToBlack(btnCheckin);

		btnPassengerOnBoard.setClickable(false);
		reSetColorToBlack(btnPassengerOnBoard);

		parentView.setBackgroundDrawable(makeitGray);
		changeMapMarkerIconTounAvailable();
		/*
		 * if (availabilityToggle.isChecked()) {
		 * availabilityToggle.setChecked(false);
		 * 
		 * isCarAvailable = false; setDriverStatus();
		 * ///changeMapMarkerupIconTounAvailable(); }
		 */

	}

	public void changeMapMarkerIconTounAvailable() {

		try {
			if (userLocationMarker != null)
				userLocationMarker.remove();

			try {
				googleMap.clear();
				/*userLocationMarker = googleMap
						.addMarker(new MarkerOptions()
								.position(currentLatLng)
								.snippet(
										"Lat: " + currentLatLng.latitude
												+ ", Long: "
												+ currentLatLng.longitude)
								.icon(BitmapDescriptorFactory
										.fromResource(R.drawable.map_icon_driver_not_available)));*/
				
				Drawable customIcon = getResources().getDrawable( R.drawable.map_icon_driver_not_available );
				Bitmap bb= convertDrawableToBitmap(customIcon);
				
				userLocationMarker = googleMap
						.addMarker(new MarkerOptions()
								.position(currentLatLng)
								.snippet(
										"Lat: "
												+ currentLatLng.latitude
												+ ", Long: "
												+ currentLatLng.longitude)
								.icon(BitmapDescriptorFactory
										.fromBitmap(bb)));
				
				
			} catch (Exception e) {
				e.printStackTrace();
				userLocationMarker = googleMap
						.addMarker(new MarkerOptions()
								.position(currentLatLng)
								.snippet(
										"Lat: " + currentLatLng.latitude
												+ ", Long: "
												+ currentLatLng.longitude)
								.icon(BitmapDescriptorFactory
										.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

			}

			// userLocationMarker = googleMap.a

			userLocationMarker.setDraggable(false);

			UiSettings settings = googleMap.getUiSettings();
			googleMap.setMyLocationEnabled(true);
			settings.setMyLocationButtonEnabled(false);
		} catch (Throwable t) {
			t.printStackTrace();

		}

	}

	public void changeMapMarkerIconToAvailable() {

		try {
			if (userLocationMarker != null)
				userLocationMarker.remove();

			try {
				googleMap.clear();
				
				/*userLocationMarker = googleMap.addMarker(new MarkerOptions()
						.position(currentLatLng)
						.snippet(
								"Lat: " + currentLatLng.latitude + ", Long: "
										+ currentLatLng.longitude)
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.map_icon_taxi)));*/
				
				Drawable customIcon = getResources().getDrawable( R.drawable.map_icon_taxi );
				Bitmap bb= convertDrawableToBitmap(customIcon);
				
				userLocationMarker = googleMap
						.addMarker(new MarkerOptions()
								.position(currentLatLng)
								.snippet(
										"Lat: "
												+ currentLatLng.latitude
												+ ", Long: "
												+ currentLatLng.longitude)
								.icon(BitmapDescriptorFactory
										.fromBitmap(bb)));
				
				
			} catch (Exception e) {

				e.printStackTrace();
				userLocationMarker = googleMap
						.addMarker(new MarkerOptions()
								.position(currentLatLng)
								.snippet(
										"Lat: " + currentLatLng.latitude
												+ ", Long: "
												+ currentLatLng.longitude)
								.icon(BitmapDescriptorFactory
										.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
			}

			userLocationMarker.setDraggable(true);

			UiSettings settings = googleMap.getUiSettings();

			settings.setMyLocationButtonEnabled(false);
			googleMap.setMyLocationEnabled(true);
			googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
					currentLatLng, zoomNormal));

			googleMap.setOnMarkerDragListener(this);
		} catch (Throwable t) {
			t.printStackTrace();

		}

	}

	public void enableTaxiPostCheckInMode() {
		btnCheckin.setText("Post check out");
		changeMapMarkerIconToPostCheckIn();

	}

	public void enableTaxiOnPassengerMode() {

		changeMapMarkerIconToPassengerOnBoard();

	}

	public void changeMapMarkerIconToPostCheckIn() {

		try

		{
			if (userLocationMarker != null)
				userLocationMarker.remove();

			try {
				googleMap.clear();
				/*userLocationMarker = googleMap.addMarker(new MarkerOptions()
						.position(currentLatLng)
						.snippet(
								"Lat: " + currentLatLng.latitude + ", Long: "
										+ currentLatLng.longitude)
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.map_icon_post_check)));*/
				
				
				Drawable customIcon = getResources().getDrawable( R.drawable.map_icon_post_check );
				Bitmap bb= convertDrawableToBitmap(customIcon);
				
				userLocationMarker = googleMap
						.addMarker(new MarkerOptions()
								.position(currentLatLng)
								.snippet(
										"Lat: "
												+ currentLatLng.latitude
												+ ", Long: "
												+ currentLatLng.longitude)
								.icon(BitmapDescriptorFactory
										.fromBitmap(bb)));
				
			} catch (Exception e) {

				e.printStackTrace();
				userLocationMarker = googleMap
						.addMarker(new MarkerOptions()
								.position(currentLatLng)
								.snippet(
										"Lat: " + currentLatLng.latitude
												+ ", Long: "
												+ currentLatLng.longitude)
								.icon(BitmapDescriptorFactory
										.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
			}

			// userLocationMarker = googleMap.a

			userLocationMarker.setDraggable(true);

			UiSettings settings = googleMap.getUiSettings();
			googleMap.setMyLocationEnabled(true);
			settings.setMyLocationButtonEnabled(false);

			googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
					currentLatLng, zoomNormal));
		} catch (Throwable t) {
			t.printStackTrace();

		}

	}

	public void changeMapMarkerIconToPassengerOnBoard() {

		try {
			if (userLocationMarker != null)
				userLocationMarker.remove();

			try {
				googleMap.clear();
				/*userLocationMarker = googleMap.addMarker(new MarkerOptions()
						.position(currentLatLng)
						.snippet(
								"Lat: " + currentLatLng.latitude + ", Long: "
										+ currentLatLng.longitude)
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.map_icon_occupied)));*/
				
				Drawable customIcon = getResources().getDrawable( R.drawable.map_icon_occupied );
				Bitmap bb= convertDrawableToBitmap(customIcon);
				
				userLocationMarker = googleMap
						.addMarker(new MarkerOptions()
								.position(currentLatLng)
								.snippet(
										"Lat: "
												+ currentLatLng.latitude
												+ ", Long: "
												+ currentLatLng.longitude)
								.icon(BitmapDescriptorFactory
										.fromBitmap(bb)));
				

			} catch (Exception e) {
				e.printStackTrace();
				userLocationMarker = googleMap
						.addMarker(new MarkerOptions()
								.position(currentLatLng)
								.snippet(
										"Lat: " + currentLatLng.latitude
												+ ", Long: "
												+ currentLatLng.longitude)
								.icon(BitmapDescriptorFactory
										.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

			}

			// userLocationMarker = googleMap.a

			userLocationMarker.setDraggable(true);

			UiSettings settings = googleMap.getUiSettings();
			googleMap.setMyLocationEnabled(true);
			settings.setMyLocationButtonEnabled(false);

			googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
					currentLatLng, zoomNormal));

		} catch (Throwable t) {
			t.printStackTrace();
		}

	}

	@SuppressWarnings("deprecation")
	public void enableTaxiAvailableMode() {
		parentView.setClickable(true);
		GradientDrawable makeitwhite = new GradientDrawable();
		makeitwhite.setColor(Color.WHITE);
		parentView.setBackgroundDrawable(makeitwhite);

		btnCheckin.setClickable(true);
		btnPassengerOnBoard.setClickable(true);
		// setPopupButtons();

		changeMapMarkerIconToAvailable();

		/*
		 * if (!availabilityToggle.isChecked()) {
		 * availabilityToggle.setChecked(true);
		 * 
		 * isCarAvailable = true; setDriverStatus();
		 * 
		 * changeMapMarkerIconToAvailable(); }
		 */
	}

	public void setPopupButtons() {

		if (isPobEnabled || isPostCheckinEnabled) {
			if (isPobEnabled) {
				type = TYPE_PASSENGER_ON_BOARD;
				enableServiceButtons(btnPassengerOnBoard, btnCheckin,
						"Passenger on board");
				enableTaxiOnPassengerMode();
				isPobButtonEnabled = true;

			} else {
				isPobButtonEnabled = false;

			}

			if (isPostCheckinEnabled) {
				type = TYPE_POST_CHECK_IN;

				enableServiceButtons(btnCheckin, btnPassengerOnBoard,
						"Post check in");
				enableTaxiPostCheckInMode();
				isPostCheckinButtonEnabled = true;
			} else
				isPostCheckinButtonEnabled = false;

			if (isCarAvailable) {
				// type=TYPE_CAR_IS_AVAILABLE;
				if (!availabilityToggle.isChecked()) {
					availabilityToggle.setChecked(true);
					enableTaxiAvailableMode();
				}

			} else {
				// type=TYPE_CAR_IS_NOT_AVAILABLE;
				if (availabilityToggle.isChecked()) {
					availabilityToggle.setChecked(false);
					disableTaxiAvailalbleMode();
					// reSetColorToBlack(btnCheckin);
					// reSetColorToBlack(btnPassengerOnBoard);
				}

			}

		} else {
			// /////////////////////
			if (isPobEnabled) {
				type = TYPE_PASSENGER_ON_BOARD;
				enableServiceButtons(btnPassengerOnBoard, btnCheckin,
						"Passenger on board");
				isPobButtonEnabled = true;

			} else
				isPobButtonEnabled = false;

			if (isPostCheckinEnabled) {
				type = TYPE_POST_CHECK_IN;

				enableServiceButtons(btnCheckin, btnPassengerOnBoard,
						"Post check in");
				isPostCheckinButtonEnabled = true;
			} else
				isPostCheckinButtonEnabled = false;

			// //////////////////
			if (isCarAvailable) {
				type = TYPE_CAR_IS_AVAILABLE;
				if (!availabilityToggle.isChecked()) {
					availabilityToggle.setChecked(true);
					enableTaxiAvailableMode();
				}
				// enableTaxiAvailableMode();

			} else {
				type = TYPE_CAR_IS_NOT_AVAILABLE;
				if (availabilityToggle.isChecked()) {
					availabilityToggle.setChecked(false);
					disableTaxiAvailalbleMode();
					// reSetColorToBlack(btnCheckin);
					// reSetColorToBlack(btnPassengerOnBoard);
				}

			}

		}

	}

	private void setDriverStatus() {

		txtTaxiStatus.setText(isCarAvailable ? "Cab Available"
				: "Cab Not Available");
	}

	private void setTaxiStatus(String status) {

		txtTaxiStatus.setText(status);
	}

	private boolean getCarAvaialibility(String availbility) {
		if (!Utils.isEmptyOrNull(availbility))
			return (availbility.equalsIgnoreCase("yes")) ? true : false;

		return false;
	}

	private void initMap() {
		// googleMap = mapView.getMap();

		if (googleMap == null)
			return;

		googleMap.clear();

		googleMap.getUiSettings().setZoomControlsEnabled(false);
		googleMap.setOnCameraChangeListener(this);
		/*
		 * googleMap.setOnMyLocationButtonClickListener(new
		 * OnMyLocationButtonClickListener() {
		 * 
		 * @Override public boolean onMyLocationButtonClick() { // TODO
		 * Auto-generated method stub return false; } });
		 */
		googleMap.getUiSettings().setMyLocationButtonEnabled(false);
		googleMap.setMyLocationEnabled(true);
		googleMap.setInfoWindowAdapter(new InfoWindowAdapter() {

			@Override
			public View getInfoWindow(Marker arg0) {
				// CabInfoWindow cabInfoWindow = new
				// CabInfoWindow(getActivity());
				// return cabInfoWindow;
				return null;
			}

			@Override
			public View getInfoContents(Marker marker) {
				CabInfoWindow cabInfoWindow = new CabInfoWindow(getActivity());
				cabInfoWindow.setDriverName(marker.getTitle());
				cabInfoWindow.setRating(3.5f);
				return cabInfoWindow;
			}
		});
	}

	private void onFindTaxi() {
		progressWheelLoader.setVisibility(View.VISIBLE);
		WebServiceModel.getCabsAroundMe(currentLatLng, this, this);
	}

	private void onPickUpAddressClick() {
		navigateToPickUpLocationActivity();
	}

	private void onPickUpHere() {
		navigateToPickUpLocationActivity();
	}

	private void onOptions(View v) {
		if (USER_ID <= 0)
			return;

		/*
		 * PopupMenu popup = new PopupMenu(getActivity(), v);
		 * popup.getMenu().add(1, 1, 1, getString(R.string.POST_CHECK_IN));
		 * popup.getMenu().add(1, 1, 2, getString(R.string.PASSENGER_ON_BOARD));
		 * popup.getMenu().add(1, 1, 3, getString(R.string.STATUS));
		 * popup.setOnMenuItemClickListener(onMenuItemListener); popup.show();
		 */
		// initOptionsPopUp(v);
		// mQuickAction.show(v,layoutBottomButtons.getHeight());
		mQuickAction.show(v);
		mQuickAction.setAnimStyle(DriverOptionsPopup.ANIM_GROW_FROM_RIGHT);

	}

	OnMenuItemClickListener onMenuItemListener = new PopupMenu.OnMenuItemClickListener() {
		@Override
		public boolean onMenuItemClick(MenuItem item) {
			String type = "";
			switch (item.getOrder()) {
			case 1:
				type = TYPE_POST_CHECK_IN;
				break;
			case 2:
				type = TYPE_PASSENGER_ON_BOARD;
				break;
			case 3:
				setCarAvailibiltyStatus();
				type = isCarAvailable ? TYPE_CAR_IS_AVAILABLE
						: TYPE_CAR_IS_NOT_AVAILABLE;
				break;
			default:
				break;
			}
			if (currentLatLng != null)
				WebServiceModel.setDriverStatus(String.valueOf(USER_ID), type,
						String.valueOf(currentLatLng.latitude),
						String.valueOf(currentLatLng.longitude),
						FindARideFragment.this);
			return false;
		}

	};

	private void setCarAvailibiltyStatus() {
		isCarAvailable = !isCarAvailable;
	}

	private void navigateToPickUpLocationActivity() {

		Applog.Debug("navigateToPickUpLocationActivity Clicked from "
				+ this.getClass());

		/*
		 * try { Location location = LocationManager.getInstance(getActivity())
		 * .getCurrentLocation(); Applog.Debug("Longitude" +
		 * location.getLongitude() + "\nLatitude" + location.getLatitude());
		 * 
		 * setMapMarkerPosition(latitude, longitude, zoomNormal); } catch
		 * (LocationNotFoundException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 * 
		 * Intent intent = new
		 * Intent(getActivity(),PickUpLocationActivity.class); Trip trip = new
		 * Trip(); trip.setPickupLat(currentLatLng.latitude);
		 * trip.setPickupLong(currentLatLng.longitude);
		 * trip.setPickupAddress(Utils
		 * .validateEmptyString(btnPickUpAddress.getText().toString()));
		 * trip.setUserID(FactoryModel.getUser().getId());
		 * intent.putExtra("Trip", trip); // intent.putExtra("LatLng",
		 * currentLatLng); // intent.putExtra("Address",
		 * btnPickUpAddress.getText()); startActivity(intent);
		 */
	}

	public void clickGoToMarker() {
		googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
				currentLatLng, zoomNormal));
		setUpMap();
	}

	private void setUpMap() {

		if (location != null) {
			System.out.println("Provider " + provider + " has been selected.");

			onLocationChanged(location);

			if (!type.equalsIgnoreCase(TYPE_CAR_IS_NOT_AVAILABLE))
				setMapMarkerPosition(latitude, longitude, zoomNormal);
		}
	}

	/*
	 * public static int calculateInSampleSize( BitmapFactory.Options options,
	 * int reqWidth, int reqHeight) { // Raw height and width of image final int
	 * height = options.outHeight; final int width = options.outWidth; int
	 * inSampleSize = 1;
	 * 
	 * if (height > reqHeight || width > reqWidth) {
	 * 
	 * final int halfHeight = height / 2; final int halfWidth = width / 2;
	 * 
	 * // Calculate the largest inSampleSize value that is a power of 2 and
	 * keeps both // height and width larger than the requested height and
	 * width. while ((halfHeight / inSampleSize) > reqHeight && (halfWidth /
	 * inSampleSize) > reqWidth) { inSampleSize *= 2; } }
	 * 
	 * return inSampleSize; }
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}
	
	public static Bitmap convertDrawableToBitmap (Drawable drawable) {
	    if (drawable instanceof BitmapDrawable) {
	        return ((BitmapDrawable)drawable).getBitmap();
	    }

	    Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(bitmap); 
	    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
	    drawable.draw(canvas);

	    return bitmap;
	}

	private void setMapMarkerPosition(Double newLatitude, Double newLongitude,
			int zoom) {

		try {
			Applog.Debug("Latitude= " + newLatitude);
			Applog.Debug("Longitude= " + newLongitude);

			Log.e("TagLocation", " onSetMapMarkerPosition ");
			currentLatLng = null;
			currentLatLng = new LatLng(newLatitude, newLongitude);

			if (userLocationMarker != null)
				userLocationMarker.remove();

		
						

			googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {
				public boolean onMarkerClick(Marker marker) {

					marker.hideInfoWindow();

					return true;
				}
			});

			// //

			if (userLocationMarker == null) {
				try {
					/*
					 * userLocationMarker = googleMap .addMarker(new
					 * MarkerOptions() .position(currentLatLng) .snippet(
					 * "Lat: " + currentLatLng.latitude + ", Long: " +
					 * currentLatLng.longitude) .icon(BitmapDescriptorFactory
					 * .fromResource(R.drawable.map_icon_taxi)));
					 */
					
					Drawable customIcon = getResources().getDrawable( R.drawable.map_icon_taxi );
					Bitmap bb= convertDrawableToBitmap(customIcon);
					
					userLocationMarker = googleMap
							.addMarker(new MarkerOptions()
									.position(currentLatLng)
									.snippet(
											"Lat: "
													+ currentLatLng.latitude
													+ ", Long: "
													+ currentLatLng.longitude)
									.icon(BitmapDescriptorFactory
											.fromBitmap(bb)));
					

				

				} catch (Exception e) {

					e.printStackTrace();
					userLocationMarker = googleMap
							.addMarker(new MarkerOptions()
									.position(currentLatLng)
									.snippet(
											"Lat: " + currentLatLng.latitude
													+ ", Long: "
													+ currentLatLng.longitude)
									.icon(BitmapDescriptorFactory
											.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
				}

				userLocationMarker.setDraggable(true);

				UiSettings settings = googleMap.getUiSettings();

				settings.setMyLocationButtonEnabled(false);

				googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
						currentLatLng, zoom));

				googleMap.setOnMarkerDragListener(this);
			} else {
				if (type.equalsIgnoreCase(TYPE_PASSENGER_ON_BOARD))
					changeMapMarkerIconToPassengerOnBoard();
				else if (type.equalsIgnoreCase(TYPE_POST_CHECK_IN))
					changeMapMarkerIconToPostCheckIn();
				else if (type.equalsIgnoreCase(TYPE_CAR_IS_AVAILABLE))
					changeMapMarkerIconToAvailable();

			}
		} catch (Throwable t) {

			t.printStackTrace();
		}

	}

	private void addCabMarkerToMap(Cab cab) {
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.map_taxi_icon);
		Marker marker = googleMap.addMarker(new MarkerOptions()
				.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
				.position(cab.getLatLng()).title(cab.getDriver().getName()));
		if (marker != null)
			markersList.add(marker);
	}

	private void makeGoogleDistanceMatrixCall() {
		// Cab firstCab = cabsList.get(0);
		GoogleAPIHelper.timeDuration(cabsList, currentLatLng, this);
	}

	private void addMarkersToMap() {
		for (Cab cab : cabsList) {
			addCabMarkerToMap(cab);
		}
	}

	public void zoomToAdjustAllMarkers() {
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		builder.include(currentLatLng);
		for (Marker marker : markersList) {
			builder.include(marker.getPosition());
		}
		LatLngBounds bounds = builder.build();

		int padding = 70; // offset from edges of the map in pixels
		CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

		googleMap.moveCamera(cu);
		// googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
		// currentLatLng, googleMap.getCameraPosition().zoom));
	}

	public void setDriverStatus(String type) {
		if (currentLatLng != null) {
			LoaderHelper.showLoader(getActivity(), "Loading...", "");
			WebServiceModel.setDriverStatus(String.valueOf(USER_ID), type,
					String.valueOf(currentLatLng.latitude),
					String.valueOf(currentLatLng.longitude),
					FindARideFragment.this);
		}
	}

	public void zoomToAdjustAllMarkers(double newlat, double newlng) {
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		builder.include(new LatLng(newlat, newlng));

		for (Marker marker : markersList) {
			builder.include(marker.getPosition());
		}
		LatLngBounds bounds = builder.build();

		int padding = 150; // offset from edges of the map in pixels
		CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

		googleMap.moveCamera(cu);
		// googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
		// currentLatLng, googleMap.getCameraPosition().zoom));
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.btnOptions:
			onOptions(v);
			break;
		case R.id.button_locate:
			clickGoToMarker();
			break;

		case R.id.check_in:
			if (type.equalsIgnoreCase(TYPE_POST_CHECK_IN)) {
				// make it black and call
				type = TYPE_CAR_IS_AVAILABLE;
				setDriverStatus(type);
				break;

			}
			type = TYPE_POST_CHECK_IN;
			setDriverStatus(type);
			break;

		case R.id.passenger_on_board:
			if (type.equalsIgnoreCase(TYPE_PASSENGER_ON_BOARD)) {
				// make it black and call
				type = TYPE_CAR_IS_AVAILABLE;
				setDriverStatus(type);

				break;

			}

			type = TYPE_PASSENGER_ON_BOARD;
			setDriverStatus(type);
			break;

		case R.id.btn_cancel:
			mQuickAction.dismiss();
			break;

		case R.id.cab_availability_toggle:
			if (isCarAvailable) {
				isCarAvailable = false;

			} else
				isCarAvailable = true;

			type = isCarAvailable ? TYPE_CAR_IS_AVAILABLE
					: TYPE_CAR_IS_NOT_AVAILABLE;
			setDriverStatus(type);
			break;
		default:
			break;
		}

	}

	@Override
	public void onStart() {
		// AppToast.ShowToast(getActivity(), "fr: onstart");

		super.onStart();
	}

	@Override
	public void onResume() {
		// AppToast.ShowToast(getActivity(), "fr:onResume");

		super.onResume();

		if (googleMap == null)
			startLoadingMap();

		getUpdatedValues();
		setPopupButtons();
		// AppToast.ShowToast(getActivity(), "fr: onresume");
		mapView.onResume();
		if (locationChangedReciever == null)
			locationChangedReciever = new MapLocationChangedReciever();
		getActivity()
				.registerReceiver(
						locationChangedReciever,
						new IntentFilter(
								LocationManager.LOCATION_CHANGED_INTENT_ACTION));

		if (!MainActivity.isRequestShown) {
			/*
			 * if (fusedlocation != null) {
			 * setMapMarkerPosition(fusedlocation.getLatitude(),
			 * fusedlocation.getLongitude(), zoomNormal); }
			 */

			if (fusedlocation != null) {

				if (fusedlocation.getTime() > ServiceLocation.curLocation
						.getTime())
				{
					Applog.Error("Latest= "+ " fused hai" );
					setMapMarkerPosition(fusedlocation.getLatitude(),
							fusedlocation.getLongitude(), zoomNormal);
				}
				else {
					Applog.Error("Latest= "+ " curLocation hai" );
					setMapMarkerPosition(
							ServiceLocation.curLocation.getLatitude(),
							ServiceLocation.curLocation.getLongitude(),
							zoomNormal);

				}

			}

		}

	}

	public void disableMarkerUpdation() {
		isPathplotted = true;
		// setMapMarkerPosition(2.4444, 2.3333, 12);

	}

	public void enableMarkerUpdation() {
		isPathplotted = false;
		// setMapMarkerPosition(2.4444, 2.3333, 12);

	}

	/*
	 * public void cancelCurrentJourneyAndRestore() {
	 * 
	 * if(preferencesHandler.getSavedJourneyId()!=0 ) {
	 * com.smarttaxi.driver.BAL.Journey _journey = new
	 * com.smarttaxi.driver.BAL.Journey( getActivity());
	 * 
	 * Driver driver = new Driver(getActivity()); driver.enablePobStatus("no");
	 * 
	 * LoaderHelper.showLoader(getActivity(), "Cancel journey...", "");
	 * WebServiceModel.cancelJourney((HttpResponseListener)getActivity(),
	 * String.valueOf(preferencesHandler.getOriginalDriverUserID()) ,
	 * String.valueOf( preferencesHandler.getSavedJourneyId()));// user_id
	 * _journey.removeSavedJourney();
	 * 
	 * }
	 * 
	 * }
	 */
	@Override
	public void onPause() {

		// AppToast.ShowToast(getActivity(), "fr: onPause");
		super.onPause();
		mapView.onPause();
		
		
		if (LoaderHelper.mProgressDialog != null) {
			if(LoaderHelper.isFragmentLoaderShowing())
			LoaderHelper.closeProgressLoader();
		}
		/*
		 * if(locationChangedReciever!=null) {
		 */
		getActivity().unregisterReceiver(locationChangedReciever);
		locationChangedReciever = null;
		// }
		googleMap = null;
	}

	@Override
	public void onDestroy() {

		// AppToast.ShowToast(getActivity(), "fr: onDestroy");
		super.onDestroy();
		mapView.onDestroy();

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	@Override
	public void onLowMemory() {

		// AppToast.ShowToast(getActivity(), "fr: onLowmemory");
		super.onLowMemory();
		mapView.onLowMemory();
	}

	@SuppressWarnings("deprecation")
	public void enableServiceButtons(Button makeYellow, Button resetButton,
			String status) {

		GradientDrawable makeItYellow = new GradientDrawable();
		makeItYellow.setColor(Color.parseColor("#E2A60C"));
		makeItYellow.setCornerRadius((float) 10.0);
		makeItYellow.setStroke(1, Color.BLACK);
		
		if(null != makeYellow)
			makeYellow.setBackgroundDrawable(makeItYellow);
		if(null != resetButton)
			reSetColorToBlack(resetButton);
		if(!Utils.isEmptyOrNull(status))
			setTaxiStatus(status);

	}

	@SuppressWarnings({ "deprecation", "unused" })
	@Override
	public void onResponse(CustomHttpResponse object) {
		if (LoaderHelper.isLoaderShowing())
			LoaderHelper.hideLoader();
		if (object.getMethodName().equals(APIConstants.METHOD_SET_DRIVE_STATUS)) {

			if (object.getStatusCode() == APIConstants.SUCESS_CODE) {

				if (object.getResponse() instanceof com.smarttaxi.driver.entities.Driver) {

					com.smarttaxi.driver.entities.Driver status = (com.smarttaxi.driver.entities.Driver) object
							.getResponse();

					if (status != null) {
						type = status.getType();
						Driver driver = new Driver(getActivity());
						driver.resetEntries(status.getPobStatus(),
								status.getPostCheckInStatus(),
								status.getCabStatus());
					}

					/* TYPE POST CHECK IN */
					if (type.equalsIgnoreCase(TYPE_POST_CHECK_IN)) {

						isCarAvailable = status.getCabStatus()
								.equalsIgnoreCase("no") ? false : true;
						setDriverStatus();

						if (isCarAvailable) {
							if (!availabilityToggle.isChecked())
								availabilityToggle.setChecked(true);
						} else {
							if (availabilityToggle.isChecked())
								availabilityToggle.setChecked(false);

						}

						// *****
						if (status.getPostCheckInStatus().equalsIgnoreCase(
								"yes")) {
							enableTaxiPostCheckInMode();
							if (!isPostCheckinButtonEnabled) {
								enableServiceButtons(btnCheckin,
										btnPassengerOnBoard, "Post check in");

								isPostCheckinButtonEnabled = true;
							} else {
								reSetColorToBlack(btnCheckin);
								btnCheckin.setText("Post check in");
							}
						} else {

							if (isPostCheckinButtonEnabled) {
								reSetColorToBlack(btnCheckin);
								btnCheckin.setText("Post check in");
								isPostCheckinButtonEnabled = false;
							}
							/*
							 * else enableServiceButtons(btnCheckin,
							 * btnPassengerOnBoard, "Post check in");
							 */

						}

						// //
						if (status.getPobStatus().equalsIgnoreCase("yes")) {

							enableTaxiOnPassengerMode();
							if (!isPobButtonEnabled) {
								enableServiceButtons(btnPassengerOnBoard,
										btnCheckin, "Passenger on board");
								btnCheckin.setText("Post check in");
								isPobButtonEnabled = true;
							} else {
								reSetColorToBlack(btnPassengerOnBoard);
							}
						} else {
							// enableTaxiAvailableMode();
							if (isPobButtonEnabled) {
								reSetColorToBlack(btnPassengerOnBoard);

								isPobButtonEnabled = false;
							}

						}

					}
					/* TYPE PASSENGER ON BOARD */
					else if (type.equalsIgnoreCase(TYPE_PASSENGER_ON_BOARD)) {

						isCarAvailable = status.getCabStatus()
								.equalsIgnoreCase("no") ? false : true;
						setDriverStatus();

						if (isCarAvailable) {
							if (!availabilityToggle.isChecked())
								availabilityToggle.setChecked(true);
						} else {
							if (availabilityToggle.isChecked())
								availabilityToggle.setChecked(false);

						}

						// ////////////////////////////////////

						if (status.getPostCheckInStatus().equalsIgnoreCase(
								"yes")) {
							enableTaxiPostCheckInMode();
							if (!isPobButtonEnabled) {
								enableServiceButtons(btnPassengerOnBoard,
										btnCheckin, "Passenger on board");

								isPobButtonEnabled = true;
							} else {
								reSetColorToBlack(btnPassengerOnBoard);
								reSetColorToBlack(btnCheckin);
								btnCheckin.setText("Post check in");
							}
						} else {
							// enableTaxiAvailableMode();
							if (isPobButtonEnabled) {
								reSetColorToBlack(btnPassengerOnBoard);

								isPobButtonEnabled = false;
							}
						}

						if (status.getPobStatus().equalsIgnoreCase("yes")) {
							enableTaxiOnPassengerMode();
							if (!isPobButtonEnabled) {
								enableServiceButtons(btnPassengerOnBoard,
										btnCheckin, "Passenger on board");

								isPobButtonEnabled = true;
							} else {
								reSetColorToBlack(btnPassengerOnBoard);
							}
						}

						else {
							enableTaxiAvailableMode();
							if (isPobButtonEnabled) {
								reSetColorToBlack(btnPassengerOnBoard);

								isPobButtonEnabled = false;
							}

						}

					} else if (type.equalsIgnoreCase(TYPE_CAR_IS_AVAILABLE)
							|| type.equalsIgnoreCase(TYPE_CAR_IS_NOT_AVAILABLE)) {

						isCarAvailable = status.getCabStatus()
								.equalsIgnoreCase("no") ? false : true;
						setDriverStatus();

						if (isCarAvailable) {
							if (!availabilityToggle.isChecked())
								availabilityToggle.setChecked(true);
							enableTaxiAvailableMode();
						} else {
							if (availabilityToggle.isChecked()) {
								availabilityToggle.setChecked(false);

							}
							disableTaxiAvailalbleMode();
						}

						if (status.getPobStatus().equalsIgnoreCase("yes")) {

							isPobButtonEnabled = true;

							/*
							 * isPostCheckinButtonEnabled=true;
							 * isPobButtonEnabled=false;
							 */
							enableServiceButtons(btnPassengerOnBoard,
									btnCheckin, "Passenger on board");

						} else if (status.getPobStatus().equalsIgnoreCase("no")) {
							isPobButtonEnabled = false;

							reSetColorToBlack(btnPassengerOnBoard);

						}

						if (status.getPostCheckInStatus().equalsIgnoreCase(
								"yes")) {

							isPostCheckinButtonEnabled = true;

							/*
							 * isPostCheckinButtonEnabled=true;
							 * isPobButtonEnabled=false;
							 */
							enableServiceButtons(btnCheckin,
									btnPassengerOnBoard, "Post check in");

						} else if (status.getPostCheckInStatus()
								.equalsIgnoreCase("no")) {
							isPostCheckinButtonEnabled = false;
							btnCheckin.setText("Post check in");
							reSetColorToBlack(btnCheckin);

						}

					}

				}
			} else
				AppToast.ShowToast(getActivity(), object.getResponseMsg());

		}

	}

	@SuppressWarnings("deprecation")
	private void reSetColorToBlack(Button bttn) {
		// TODO Auto-generated method stub
		GradientDrawable makeItblack = new GradientDrawable();
		makeItblack.setColor(Color.BLACK);
		makeItblack.setCornerRadius((float) 10.0);
		bttn.setBackgroundDrawable(makeItblack);
	}

	@Override
	public void onException(CustomHttpException exception) {
		// progressWheelLoader.setVisibility(View.GONE);
		// txtTaxiDuration.setVisibility(View.INVISIBLE);
		// swtichButtons();

		if (LoaderHelper.progressDialog != null) {
			if (LoaderHelper.isLoaderShowing()) {
				LoaderHelper.hideLoader();
				AppToast.ShowToast(getActivity(), exception.getMessage());
			}

		}
	}

	@Override
	public void onProgress(Object value) {
		progressWheelLoader.setProgress((Integer) value);
	}

	@Override
	public void onCameraChange(CameraPosition cameraPosition) {
		LatLng address = cameraPosition.target;
		/*
		 * btnPickUpAddress.setText(LocationManager.getInstance(getActivity())
		 * .getLocationAddress(address));
		 */

	}

	public class MapLocationChangedReciever extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			// AppToast.ShowToast(getActivity(), "onReceive() called");
			if (googleMap == null) {
				return;
			}

			try {
				// check just
				if (!isPathplotted)
				{
					new UpdateMapLocation().execute("");
					if(LoaderHelper.mProgressDialog!=null)
					{
						if(LoaderHelper.isFragmentLoaderShowing())
						{
							LoaderHelper.closeProgressLoader();
							
						}
						/*else
						{
							
							
								LoaderHelper.showProgressLoaderForFragments(
										(Context) getActivity(),
										"Getting location please wait...", "In progress");
							googleMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {

								@Override
								public void onMapLoaded() {
									// TODO Auto-generated method stub
									if (googleMap != null) {
										if (LoaderHelper.mProgressDialog != null) {
											LoaderHelper.closeProgressLoader();
										}
										Applog.Error(">>>> Map Loaded");
									}

								}
							});
							
						}*/
						
					}
					

				
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	@Override
	public void onMarkerDrag(Marker m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMarkerDragEnd(Marker m) {
		setMapMarkerPosition(m.getPosition().latitude,
				m.getPosition().longitude, zoomNormal);

	}

	@Override
	public void onMarkerDragStart(Marker m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location location) {
		latitude = location.getLatitude();
		longitude = location.getLongitude();

		if (ServiceLocation.curLocation != null) {
			if (!type.equalsIgnoreCase(TYPE_CAR_IS_NOT_AVAILABLE))
				setMapMarkerPosition(ServiceLocation.curLocation.getLatitude(),
						ServiceLocation.curLocation.getLongitude(), zoomNormal);

		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	private class UpdateMapLocation extends AsyncTask<String, Void, String> {

		String address;
		LatLng userLatLng;

		@Override
		protected String doInBackground(String... params) {

			try {
				

				fusedlocation = LocationManager.getInstance(getActivity())
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
				currentLatLng = userLatLng;
				address = LocationManager.getInstance(getActivity())
						.getLocationAddress(currentLatLng);

				/* isUserMarkerCreated = true; */

			}

			catch (LocationNotFoundException e) {
				e.printStackTrace();
			}

			return "Executed";
		}

		@Override
		protected void onPostExecute(String result) {

			try {
				
				
				if (!type.equalsIgnoreCase(TYPE_CAR_IS_NOT_AVAILABLE))
				{
					Applog.Error(">>>>Marker=" + "Updated");
				
					setMapMarkerPosition(userLatLng.latitude,
							userLatLng.longitude, zoomNormal);
				ServiceLocation.curLocation = fusedlocation;
				ServiceLocation.curAddress = address;
				txtLocation.setText(address);
				}

				
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