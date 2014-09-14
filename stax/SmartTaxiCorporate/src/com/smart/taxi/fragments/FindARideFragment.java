package com.smart.taxi.fragments;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.client.ClientProtocolException;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.smarttaxi.client.R;
import com.smart.taxi.activities.ConfirmTripActivity;
import com.smart.taxi.activities.ContainerActivity;
import com.smart.taxi.activities.CustomHttpClass;
import com.smart.taxi.activities.SplashActivity;
import com.smart.taxi.adapters.PlacesAutoCompleteAdapter;
import com.smart.taxi.components.CFEditText;
import com.smart.taxi.components.CFTextView;
import com.smart.taxi.components.ConfirmTripDialogue;
import com.smart.taxi.components.CustomTripDialogFragment;
import com.smart.taxi.components.renderers.DriverInfoWindowAdapter;
import com.smart.taxi.constants.APIConstants;
import com.smart.taxi.entities.Cab;
import com.smart.taxi.entities.CustomHttpException;
import com.smart.taxi.entities.CustomHttpResponse;
import com.smart.taxi.entities.TripDetails;
import com.smart.taxi.helpers.JsonHelper;
import com.smart.taxi.helpers.LoaderHelper;
import com.smart.taxi.preferences.PreferencesHandler;
import com.smart.taxi.utils.CommonUtilities;
import com.smart.taxi.utils.HttpAsyncTask;
import com.smart.taxi.utils.NetworkAvailability;
import com.smart.taxi.utils.Utils;

public class FindARideFragment extends BaseFragment implements 
				OnMarkerDragListener, 
				OnMapClickListener, 
				OnMyLocationButtonClickListener, 
				OnMyLocationChangeListener,
				OnClickListener, OnInfoWindowClickListener,
				OnItemClickListener
	{

	public static final String TAG = "findARide";
	private GoogleMap googleMap;
	private MapView mapView;
	private LatLng currentLatLng;
	private Marker userLocationMarker;
	private MapFragment mapFragment;
	private CFTextView tfAddress;
	private static String currentAddress = "";
	private PinAndJourneyTypeDialogue pinDialog;
	public boolean hasPostCheckedInResults;
	String defaultNumPessangers = "1";
	String defaultNumChildSeats = "0";
	String defaultNumBags = "0";
	String defaultJourneyOptions = "solo";
	String defaultOptionalMessage = "";
	private boolean isCustomAddress = false;
	public static Map<Marker,Cab> markers;
	public static boolean isShowingMarkers;
	private Button btnPickUp;
	private Button btnPickUpOptions;
	private ImageButton btnReset;
	private boolean inMarkersDisplayMode = false;
	private LatLng requestPosition;
	private LatLng cabLatLng = null;
	private ImageButton imgBtnSearchAddress;
	private ImageButton imgBtnSearchIcon;
	private TripDetails defaultTripDetails;
	private AutoCompleteTextView autoCompView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setDefaultTripDetails(new TripDetails());
	}
	
	@Override 
	public void onPause()
	{
		/*if(ServiceLocation.isService)
		{
			getActivity().stopService(new Intent(getActivity(), ServiceLocation.class));
		}*/
		if(googleMap != null)
		{
			googleMap.setOnMyLocationChangeListener(null);
		}
		super.onPause();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		/*if(!ServiceLocation.isService)
		{
			getActivity().startService(new Intent(getActivity(), ServiceLocation.class));
			try{
				currentLatLng = new LatLng(ServiceLocation.curLocation.getLatitude(),ServiceLocation.curLocation.getLongitude());
			}catch(Exception ex){}
		}
		if(googleMap == null)
		{
			setupMap();
		}else{
			//moveToMyLocation(2);
			googleMap.setOnMyLocationChangeListener(this);
		}*/
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		try{
			container.removeAllViews();
			rootView = inflater.inflate(R.layout.activity_find_a_ride, container, true);
			//getActivity().getActionBar().hide();
			mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFindARide);
			
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
			//mapView = (MapView)
			
			//mapView.onCreate(savedInstanceState);
			initUI();
			
			return super.onCreateView(inflater, container, savedInstanceState);
		}catch(Exception ex)
		{
			getActivity().finish();
			SplashActivity.forceLogin();
			return rootView;
		}
		
	}
	
	
	
	private void initUI() {
		try {
			
			btnPickUp = (Button) rootView.findViewById(R.id.btnFindARide);
			btnReset = (ImageButton) rootView.findViewById(R.id.btnResetFindRide);
			btnPickUpOptions = (Button) rootView.findViewById(R.id.btnOptionsFindRide);
			imgBtnSearchAddress = (ImageButton) rootView.findViewById(R.id.imgBtnChangeAddress);
			imgBtnSearchIcon = (ImageButton) rootView.findViewById(R.id.imgSearchIcon);
			
			btnPickUp.setOnClickListener(this);
			btnReset.setOnClickListener(this);
			btnPickUpOptions.setOnClickListener(this);
			imgBtnSearchIcon.setOnClickListener(this);
			imgBtnSearchAddress.setOnClickListener(this);
			
			btnReset.setVisibility(View.INVISIBLE);
			
			MapsInitializer.initialize(getActivity());
			setupMap();
			initLocation();
			moveToMyLocation(1.5);
			autoCompView = (AutoCompleteTextView) rootView.findViewById(R.id.autocomplete);
		    autoCompView.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.list_item));
		    autoCompView.setOnItemClickListener(this);
		    autoCompView.setHint("");
		    autoCompView.setText("");
		    //tfAddress = (CFTextView)rootView.findViewById(R.id.txtAddressFindRide);
		    //tfAddress.setVisibility(View.GONE);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("Error", e.toString());
		}
	}

	private void moveToMyLocation(double d) {
		if(googleMap != null && currentLatLng != null)
		{
			CameraPosition cp = new CameraPosition.Builder().target(new LatLng(currentLatLng.latitude, currentLatLng.longitude))
					.zoom(15)
                    .bearing(0)
                    .tilt(0)
                    .build();
			double duration = d * 1000;
			googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp), (int) duration, new CancelableCallback() {
	            @Override
	            public void onFinish() {
	                
	            }

	            @Override
	            public void onCancel() {
	                
	            }
	        });
		}
		
	}

	private void setupMap() {
		if(googleMap == null)
		{
			if(mapFragment == null)
				return;
			googleMap = mapFragment.getMap();
			if (googleMap == null)
				return;
			
			WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			int paddingTop = 100;
			if(size.y > 1280)
				paddingTop = 160;
			Log.e("Size:", size.toString());
			googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			googleMap.setMyLocationEnabled(true);
			googleMap.setPadding(0,paddingTop,0,0);
			googleMap.setMyLocationEnabled(true);
			googleMap.setOnMapClickListener(this);
			googleMap.setOnMyLocationButtonClickListener(this);
			googleMap.setOnMyLocationChangeListener(this);
			googleMap.setInfoWindowAdapter(new DriverInfoWindowAdapter(getActivity()));
			googleMap.setOnInfoWindowClickListener(this);
			//googleMap.clear();
			googleMap.getUiSettings().setZoomControlsEnabled(true);
			LoaderHelper.showLoader(getActivity(), "Getting current location...", "");
		}
		
	}
	
	private void initLocation() {
		if(googleMap != null)
		{
			if (googleMap.getMyLocation() != null)
			{
				Log.e("Initlocation", "using map location");
				currentLatLng = new LatLng(googleMap.getMyLocation().getLatitude(), googleMap.getMyLocation().getLongitude());
				placeUserLocationMarker(15, currentLatLng);
			}
		}
	}
	
	private void placeUserLocationMarker(int zoom, LatLng location) {
		if(googleMap == null)
			setupMap();
		
		Log.e("TagLocation", " onSetMapMarkerPosition ");
		if (userLocationMarker != null)
		{
			userLocationMarker.remove();
			//return;
		}
		if(location == null)
		{
			if(googleMap != null && googleMap.getMyLocation() != null)
			{
				location = new LatLng(googleMap.getMyLocation().getLatitude(), googleMap.getMyLocation().getLongitude());
			}else{
				Toast.makeText(getActivity(), "Unable to find your location...", Toast.LENGTH_LONG).show();
				return;
			}
		}else{
			currentLatLng = location;
		}
		
		userLocationMarker = googleMap.addMarker(new MarkerOptions()
								.position(location)
								.snippet("")
								.icon(BitmapDescriptorFactory
										.fromResource(R.drawable.map_user_icon)));
		userLocationMarker.setDraggable(false);

		moveToMyLocation(1);
		loadAddress(location);
	}
	
	

	@Override
	public void onClick(View buttonSender) {
		super.onClick(buttonSender);
		switch (buttonSender.getId()) {
		case R.id.btnResetFindRide:	
				resetScreen();
			break;
			
		case R.id.btnFindARide:
			if(SplashActivity.isLoggedIn())
			{
				if(NetworkAvailability.IsNetworkAvailable(getActivity()))
				{
					if(isCustomAddress)
					{
						requestPosition = currentLatLng;
					}else{
						if(googleMap != null && googleMap.getMyLocation() != null)
						{
							requestPosition = new LatLng(googleMap.getMyLocation().getLatitude(), googleMap.getMyLocation().getLongitude());
						}else{
							CommonUtilities.displayAlert(getActivity(), "Can not detect your location,  please try again later", "", "Ok", "", false);
							return;
						}
					}
					if(SplashActivity.loggedInUser.isCorporateUser() && SplashActivity.loggedInUser.getCoporateID() > 0)
					{
						toggleButtons(false);
						//stopped looking for posts around me and directly crete the map
						//checkSurroundingPostInformation();
						hasPostCheckedInResults = false;
						//get cabs around me
						runCreateCorporateJourney();
					}else{
						// process for credit card user:
						
						runGetCustomerCabs();
						Log.e("normal user", "getting general map");
						return;
						//pickup_lat=43.587913&pickup_lng=-79.64321&pickup_time=2014-02-19 17:12:32 +0000&user_id=367&optional_message=None&journey_type=3
					}
						
				}else{
					NetworkAvailability.showNoConnectionDialog(getActivity());
				}
			}else{
				getActivity().finish();
			}
			break;
			
		case R.id.btnOptionsFindRide:
		case R.id.imgBtnChangeAddress:
			CustomTripDialogFragment customTripDialog = new CustomTripDialogFragment();
			customTripDialog.parent = this;
			customTripDialog.show(getFragmentManager(), "customtrip");
			break;

		default:
			break;
		}
		
	}

	private void runGetCustomerCabs() {
		toggleButtons(false);
		LoaderHelper.showLoader(getActivity(), "Finding cabs around you...", "");
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("user_lat",String.valueOf(requestPosition.latitude)));
		params.add(new BasicNameValuePair("user_lng",String.valueOf(requestPosition.longitude)));
		CustomHttpClass.runPostService(this, APIConstants.METHOD_POST_LOAD_CUSTOMER_CABS, params, true, true);
		
	}

	public void resetScreen() {
		FindARideFragment.isShowingMarkers = false;
		cabLatLng = null;
		resetMarkers();
		autoCompView.setText("");
		setDefaultTripDetails(new TripDetails());
		SplashActivity.isTripRequested = false;
		SplashActivity.setTripNewDetails(null);
		if(googleMap.getMyLocation() != null)
		{
			placeUserLocationMarker(15, new LatLng(googleMap.getMyLocation().getLatitude(),googleMap.getMyLocation().getLongitude()));
		}else{
			placeUserLocationMarker(15, null);
		}
		//moveToMyLocation(2);
		toggleButtons(true);
		isCustomAddress = false;
		
	}

	private void toggleButtons(boolean enabled) {
		btnPickUp.setEnabled(enabled);
		btnPickUpOptions.setEnabled(enabled);
		inMarkersDisplayMode = !enabled;
		if(enabled)
		{
			isCustomAddress = false;
			if(googleMap != null)
			{
				googleMap.setOnMyLocationButtonClickListener(this);				
			}
			btnPickUp.setOnClickListener(this);
			btnPickUpOptions.setOnClickListener(this);
			btnReset.setVisibility(View.INVISIBLE);
		}else{
			if(googleMap != null)
			{
				googleMap.setOnMyLocationButtonClickListener(null);
			}
			btnPickUpOptions.setOnClickListener(null);
			btnPickUp.setOnClickListener(null);
			btnReset.setVisibility(View.VISIBLE);
		}
		
	}

	private void checkSurroundingPostInformation() {
		LoaderHelper.showLoader(getActivity(), "Finding cabs around you...", "");
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		if(isCustomAddress)
		{
			requestPosition = currentLatLng;
		}else{
			requestPosition = new LatLng(googleMap.getMyLocation().getLatitude(), googleMap.getMyLocation().getLongitude());
		}
		params.add(new BasicNameValuePair("user_lat", String.valueOf(requestPosition.latitude)));
		params.add(new BasicNameValuePair("user_lng", String.valueOf(requestPosition.longitude)));
		HttpAsyncTask task = CustomHttpClass.runPostService(this, APIConstants.METHOD_POST_FIND_STANDS_AROUND_ME, params, false, false);
		
		
	}

	private void showCorporateDialogue(String cabId) 
	{	
		pinDialog = new PinAndJourneyTypeDialogue();
		pinDialog.show(getFragmentManager(), "PinDialogFragment");
		if(!Utils.isEmptyOrNull(cabId))
		{
			pinDialog.setCabId(cabId);
		}else{
			pinDialog.setIsCorporateJourney(true);
		}
		pinDialog.setParent(this);
	}

	@Override
	public void onResponse(CustomHttpResponse response)
	{
		LoaderHelper.hideLoaderSafe();
		if(response != null)
		{
			if(response.getMethodName() == APIConstants.METHOD_POST_FIND_STANDS_AROUND_ME)
			{
				//if no post checked in drivers found
				if(response.getStatusCode() > 0)
				{
					//removed the corporate process to find the posts
					/*hasPostCheckedInResults = false;
					//get cabs around me
					runCreateCorporateJourney();*/
					return;
				}else{
					hasPostCheckedInResults = true;
					LoaderHelper.hideLoaderSafe();
					showCorporateDialogue(null);
					return;
				}
			}else if(response.getMethodName() == APIConstants.METHOD_POST_CREATE_CORP_JOURNEY_FOR_CABS
					||
					response.getMethodName() == APIConstants.METHOD_POST_LOAD_CUSTOMER_CABS)
			{
				if(hasPostCheckedInResults)
				{
					if(response.getStatusCode() == 0)
					{
						JsonObject body = (JsonObject) JsonHelper.parseToJsonObject(response.getRawJson());
						Log.e("post response", body.toString());
					}
					return;
				}else{
					if(FindARideFragment.markers == null)
					{
						FindARideFragment.markers = new HashMap<Marker, Cab>();
					}else{
						resetMarkers();
					}
					googleMap.setOnMapClickListener(null);
					
					JsonObject cabsObj;
					try{
						cabsObj = (JsonObject) CustomHttpClass.getJsonObjectFromBody(response.getRawJson(), "Cabs");
						LatLngBounds.Builder builder = new LatLngBounds.Builder();
						for (Entry<String, JsonElement> entry : cabsObj.getAsJsonObject().entrySet()) {
							Cab cab = new Cab();
							JsonElement jsonElement = entry.getValue();
							JsonObject cabJson = jsonElement.getAsJsonObject();
							cab.deserializeFromJSON(jsonElement);
							double cabLat = cabJson.get("latitude").getAsDouble();
							double cabLng = cabJson.get("longitude").getAsDouble();
							int cabProviderId = Double.valueOf(cab.getCabProviderId()).intValue();
							JsonObject cabProviderJson = JsonHelper.getJsonObject(cabJson, "cab_provider");
							JsonObject cabDetails = cabProviderJson.get(cab.getCabProviderId().toString()).getAsJsonObject();
							Log.e("cab provider name:", cabDetails.get("name").getAsString());
							Marker marker = googleMap.addMarker(new MarkerOptions()
												.position(new LatLng(cabLat, cabLng))
												.title(cab.getDriver().getName())
												.snippet(cabDetails.get("name").getAsString())
												.icon(BitmapDescriptorFactory.fromResource(getIconID(cabProviderId))));
							
							builder.include(marker.getPosition());
							
	
							
							
							markers.put(marker, cab);
							Log.e("Entry " + entry.getKey(), jsonElement.toString());
						}
						if(markers.size() > 0)
						{
							builder.include(currentLatLng);
							LatLngBounds bounds = builder.build();
							int padding = 140; // offset from edges of the map in pixels
							CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
							googleMap.animateCamera(cu);
							FindARideFragment.isShowingMarkers = true;
						}else{
							resetScreen();
							CommonUtilities.displayAlert(getActivity(), "Can not find any available taxi near by,  please try again", "No taxis around", "Retry", "Close", true);
						}
					}catch(Exception ex)
					{
						resetScreen();
						CommonUtilities.displayAlert(getActivity(), "Can not find any available taxi near by,  please try again", "No taxis around", "Retry", "Close", true);
					}
				}
			}
			
			else if(response.getMethodName() == APIConstants.METHOD_CREATE_JOURNEY )
			{
				LoaderHelper.hideLoaderSafe();
				if(response.getStatusCode() == 0)
				{
					SplashActivity.isTripRequested = true;
					JsonObject body = (JsonObject) JsonHelper.parseToJsonObject(response.getRawJson());
					body = body.get("body").getAsJsonObject().get("Journey").getAsJsonObject();
					SplashActivity.getTripNewDetails().setJourneyId(body.get("id").getAsString());
					PreferencesHandler pf = new PreferencesHandler(getActivity().getApplicationContext());
					pf.saveCurrentTrip(SplashActivity.getTripNewDetails());
					((ContainerActivity)getActivity()).selectItem(0);
				}
			}else if(response.getMethodName().startsWith("https://maps.googleapis.com/maps/api/geocode/json"))
			{
				JsonObject results = JsonHelper.parseToJsonObject(response.getRawJson());
				if(results.has("results"))
				{
					JsonArray arr;
					try{
						arr = results.getAsJsonArray("results");
						JsonObject resultObj = arr.get(0).getAsJsonObject();
						if(resultObj != null && resultObj.has("geometry"))
						{
							resultObj = resultObj.get("geometry").getAsJsonObject();
							if(resultObj.has("location"))
							{
								JsonObject location = resultObj.get("location").getAsJsonObject();
								Log.e(location.get("lat").getAsString(), location.get("lng").getAsString());
								LatLng newLocation = new LatLng(location.get("lat").getAsDouble(), location.get("lng").getAsDouble());
								onMapClick(newLocation);
								autoCompView.setSelection(0);
							}
						}
							
						Log.e(TAG,arr.toString());
					}catch(Exception ex)
					{
						
					}
					
				}
				
			}			
		}
	}
	
	private int getIconID(int providerId) {
		switch (providerId)
		{
		case 1:
			return R.drawable.map_icon_taxi_imperial;
			
		case 2:
			return R.drawable.map_icon_taxi_mapple;
			
		}
		return R.drawable.map_taxi_icon;
	}

	private void resetMarkers() {
		if(markers != null && markers.size() >= 0)
		{
			for(Marker marker : FindARideFragment.markers.keySet())
			{
				if(googleMap != null)
				{
					marker.remove();
				}
			}
		}
		FindARideFragment.markers = new HashMap<Marker, Cab>();
		googleMap.setOnMapClickListener(this);
		
	}

	private void runCreateCorporateJourney() {
		toggleButtons(false);
		Date currentDate = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		String pickUpTime = ft.format(currentDate);
		String lat = (isCustomAddress )?String.valueOf(currentLatLng.latitude):String.valueOf(googleMap.getMyLocation().getLatitude());
		String lng = (isCustomAddress )?String.valueOf(currentLatLng.longitude):String.valueOf(googleMap.getMyLocation().getLongitude());
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("child_seats", (defaultTripDetails.isCustomTrip())?defaultTripDetails.getNumChildSeats():"0"));
		params.add(new BasicNameValuePair("pickup_time", (defaultTripDetails.isCustomTrip())?defaultTripDetails.getPikcupTime():pickUpTime));
		params.add(new BasicNameValuePair("max_no_of_passengers", (defaultTripDetails.isCustomTrip())?defaultTripDetails.getNumPassengers():"1"));
		params.add(new BasicNameValuePair("no_of_bags", (defaultTripDetails.isCustomTrip())?defaultTripDetails.getNumBags():"0"));
		params.add(new BasicNameValuePair("journey_option", defaultJourneyOptions));
		params.add(new BasicNameValuePair("pick_address", FindARideFragment.currentAddress));
		params.add(new BasicNameValuePair("pickup_lat", lat));
		params.add(new BasicNameValuePair("pickup_lng", lng));
		params.add(new BasicNameValuePair("user_id", SplashActivity.loggedInUser.getId()));
		params.add(new BasicNameValuePair("optional_message", ""));
		
		//TODO: fix journey type here
		params.add(new BasicNameValuePair("journey_type", "4"));
		createTripObjet(params, true);
		CustomHttpClass.runPostService(this, APIConstants.METHOD_POST_CREATE_CORP_JOURNEY_FOR_CABS, params, true, false);
		
	}

	private void createTripObjet(List<BasicNameValuePair> params, boolean isPostCheckedIn) {
		TripDetails tripDetails = getDefaultTripDetails();
		Map<String, String> values = new HashMap<String, String>();
		for(int i = 0; i < params.size(); i++)
		{
			BasicNameValuePair entry = params.get(i);
			values.put(entry.getName(), entry.getValue());
		}
		tripDetails.setNumChildSeats(values.get("child_seats"));
		tripDetails.setJourneyOptions(values.get("journey_option"));
		tripDetails.setNumPassengers(values.get("max_no_of_passengers"));
		tripDetails.setNumBags(values.get("no_of_bags"));
		tripDetails.setPickupAddress(values.get("pick_address"));
		tripDetails.setLat(values.get("pickup_lat"));
		tripDetails.setLng(values.get("pickup_lng"));
		tripDetails.setPikcupTime(values.get("pickup_time"));
		tripDetails.setUserId(values.get("user_id"));
		tripDetails.setOptionalMessage(values.get("optional_message"));
		tripDetails.setJourneyCorporateType(values.get("journey_type"));
		if(!isPostCheckedIn)
		{
			tripDetails.setCabId(values.get("cabId"));
		}
		tripDetails.setPostCheckedIn(isPostCheckedIn);
		if(cabLatLng  != null)
		{
			tripDetails.setDriverLatLng(cabLatLng);
		}
		if(!SplashActivity.loggedInUser.isCorporateUser())
		{
			tripDetails.setToken(values.get("token"));
		}
		SplashActivity.setTripNewDetails(tripDetails);
		//SplashActivity.isTripRequested = true;
	}

	@Override
	public void onException(CustomHttpException exception)
	{
		LoaderHelper.hideLoaderSafe();
		if(exception.getMethodName() == APIConstants.METHOD_POST_FIND_STANDS_AROUND_ME)
		{
			//clean up
			return;
		}
		if(exception.getMethodName() == APIConstants.METHOD_POST_CREATE_CORP_JOURNEY_FOR_CABS)
		{
			CommonUtilities.displayAlert(getActivity(), "Can not find any available taxi near by,  please try again", "No taxis around", "Retry", "Close", true);
			return;
		}
		if(exception.getMethodName() == APIConstants.METHOD_CREATE_JOURNEY)
		{
			SplashActivity.isTripRequested = false;
		}
		
	}

	@Override
	public void onMarkerDrag(Marker marker) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMarkerDragEnd(Marker marker) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMarkerDragStart(Marker marker) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMapClick(LatLng point) {
		isCustomAddress = true;
		currentLatLng = point;
		//tfAddress.setText("Loading address...");
		autoCompView.setHint("Loading address...");
		placeUserLocationMarker(15, point);
	}
	
	private void loadAddress(LatLng point)
	{
		if(NetworkAvailability.IsNetworkAvailable(getActivity().getBaseContext()))
		{
			if (Build.VERSION.SDK_INT >=
	                Build.VERSION_CODES.GINGERBREAD
	                            &&
	                Geocoder.isPresent()) {
	            // Show the activity indicator
	            /*
	             * Reverse geocoding is long-running and synchronous.
	             * Run it on a background thread.
	             * Pass the current location to the background task.
	             * When the task finishes,
	             * onPostExecute() displays the address.
	             */
	            (new GetAddressTask(getActivity())).execute(point);
	        }
		}else{
			NetworkAvailability.showNoConnectionDialog(getActivity());
		}
		
	}
	
	public static String getCurrentAddress() {
		return currentAddress;
	}

	public static void setCurrentAddress(String currentAddress) {
		FindARideFragment.currentAddress = currentAddress;
	}

	class GetAddressTask extends AsyncTask<LatLng, Void, String>{
		Context mContext;
		public GetAddressTask(Context context) {
		    super();
		    mContext = context;
		}

		@Override
		protected String doInBackground(LatLng... params) {
		    Geocoder geocoder =
		            new Geocoder(mContext, Locale.getDefault());
		    // Get the current location from the input parameter list
		    LatLng loc = params[0];
		    // Create a list to contain the result address
		    List<Address> addresses = null;
		    try {
		        /*
		         * Return 1 address.
		         */
		        addresses = geocoder.getFromLocation(loc.latitude,
		                loc.longitude, 1);
		    } catch (IOException e1) {
			    Log.e("LocationSampleActivity",
			            "IO Exception in getFromLocation()");
			    e1.printStackTrace();
			    return ("IO Exception trying to get address");
		    } catch (IllegalArgumentException e2) {
		    // Error message to post in the log
			    String errorString = "Illegal arguments " +
			            Double.toString(loc.latitude) +
			            " , " +
			            Double.toString(loc.longitude) +
			            " passed to address service";
			    Log.e("LocationSampleActivity", errorString);
			    e2.printStackTrace();
			    return errorString;
		    }
		    // If the reverse geocode returned an address
		    if (addresses != null && addresses.size() > 0) {
		        // Get the first address
		        Address address = addresses.get(0);
		        /*
		         * Format the first line of address (if available),
		         * city, and country name.
		         */
		        /*if(!address.getCountryName().toLowerCase().equals("canada"))
		        	return "";*/
		        FindARideFragment.currentAddress = String.format(
		                "%s, %s, %s %s",
		                // If there's a street address, add it
		                address.getMaxAddressLineIndex() > 0 ?
		                        address.getAddressLine(0) : "",
		                // Locality is usually a city
		                address.getLocality(),
		        	//,
		                // The country of the address
		                address.getCountryName(), address.getPostalCode());;
		        String addressText = String.format(
		                "%s",
		                // If there's a street address, add it
		                address.getMaxAddressLineIndex() > 0 ?
		                        address.getAddressLine(0) : "");
		        return addressText;
		    } else {
		    	FindARideFragment.currentAddress = "";
		        return "No address found, tap somewhere on map to get your address.";
		    }
		}
		
		@Override
		protected void onPostExecute(String address) {
			if(!address.toLowerCase().contains("exception"))
			{
				//tfAddress.setText(address);
				if(autoCompView != null)
				{
					//autoCompView.setText("");
					autoCompView.setText(address);
				}
			}else{
				autoCompView.setText("");
				autoCompView.setHint("Type address...");
			}
			/*else
				tfAddress.setText("Address not found for this location.");*/
        }

	}

	@Override
	public void onMyLocationChange(Location location) {
		if(LoaderHelper.isLoaderShowing())
		{
			LoaderHelper.hideLoaderSafe();
		}
		if(btnPickUp.isEnabled() && !isCustomAddress)
		{
			Log.e("Location changed", location.toString());
			currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
			placeUserLocationMarker(15, currentLatLng);
		}
		
	}

	@Override
	public boolean onMyLocationButtonClick() {
		Log.e("My location button clicked","");
		if(googleMap != null && googleMap.getMyLocation() != null)
		{
			isCustomAddress = false;
			if(!inMarkersDisplayMode )
				placeUserLocationMarker(15, null);
		}
		return false;
	}

	

	public void triggerPostPinOperations(String pin, String message, String journeyType, String fileNumer)
	{
		Log.e("Data received", "");
		Date currentDate = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		String pickUpTime = ft.format(currentDate);
		//child_seats=0&journey_option=solo&max_no_of_passengers=1&no_of_bags=0&pick_address=150 Paisley Blvd. West, Mississauga, ON, Canada L5B1E8&pickup_lat=43.587913&pickup_lng=-79.64321&pickup_time=2014-02-19 17:12:32 +0000&user_id=367&optional_message=None&journey_type=3
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("child_seats", (defaultTripDetails.isCustomTrip())?defaultTripDetails.getNumChildSeats():"0"));
		params.add(new BasicNameValuePair("pickup_time", (defaultTripDetails.isCustomTrip())?defaultTripDetails.getPikcupTime():pickUpTime));
		params.add(new BasicNameValuePair("max_no_of_passengers", (defaultTripDetails.isCustomTrip())?defaultTripDetails.getNumPassengers():"1"));
		params.add(new BasicNameValuePair("no_of_bags", (defaultTripDetails.isCustomTrip())?defaultTripDetails.getNumBags():"0"));
		params.add(new BasicNameValuePair("pick_address", FindARideFragment.currentAddress));
		params.add(new BasicNameValuePair("pickup_lat", String.valueOf(requestPosition.latitude)));
		params.add(new BasicNameValuePair("pickup_lng", String.valueOf(requestPosition.longitude)));
		params.add(new BasicNameValuePair("journey_option", "solo"));
		params.add(new BasicNameValuePair("optional_message", message));
		params.add(new BasicNameValuePair("user_id", SplashActivity.loggedInUser.getId()));
		params.add(new BasicNameValuePair("journey_type", journeyType));
		params.add(new BasicNameValuePair("group_id", "5"));
		if(hasPostCheckedInResults)
		{
			createTripObjet(params, true);
			CustomHttpClass.runPostService(this, APIConstants.METHOD_POST_CREATE_CORP_JOURNEY_FOR_CABS, params, true, false);	
		}else{
			params.add(new BasicNameValuePair("cab_id", pinDialog.getCabId()));
			params.add(new BasicNameValuePair("payment_option", "corporate"));
			params.add(new BasicNameValuePair("payment_status", "authorized"));
			params.add(new BasicNameValuePair("payment_type", "webservice"));
			params.add(new BasicNameValuePair("payment_detail", "0"));
			createTripObjet(params, false);
			CustomHttpClass.runPostService(this, APIConstants.METHOD_CREATE_JOURNEY, params, true, false);
		}
		
		LoaderHelper.showLoader(getActivity(), "Creating trip...", "");

		
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		if(pinDialog != null)
		{
			CFEditText pinField = (CFEditText)pinDialog.getDialogueView().findViewById(R.id.txtPin);
			CFEditText messageField = (CFEditText)pinDialog.getDialogueView().findViewById(R.id.txtMessageForDriver);
		}
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		cabLatLng = marker.getPosition();
		Cab cab = FindARideFragment.markers.get(marker);
		Log.i("Cab id:", cab.getDriver().getCabID());
		if(SplashActivity.loggedInUser.isCorporateUser())
		{
			showCorporateDialogue(cab.getDriver().getCabID());
		}else{
			showConfirmBookingDialogue(cab);
		}
		//
		/*StringBuilder urlString = new StringBuilder();
        urlString.append("http://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(marker.getPosition().latitude));
        urlString.append(",");
        urlString
                .append(Double.toString( marker.getPosition().longitude));
        urlString.append("&destination=");// to
        urlString
                .append(Double.toString(ServiceLocation.curLocation.getLatitude()));
        urlString.append(",");
        urlString.append(Double.toString(ServiceLocation.curLocation.getLongitude()));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        Log.e("Url", urlString.toString());
*/        
        //http://maps.googleapis.com/maps/api/directions/json?origin=43.58749992126725,-79.64369997382164&destination=43.5879589,-79.6435415&sensor=false&mode=driving&alternatives=true

	}

	private void showConfirmBookingDialogue(Cab cab) {
		/*
		 * &cab_id=16&payment_option=card&
		 * payment_status=authorized&payment_type=webservice&payment_detail=0&token=7318552101012683
		 */
		/*Date currentDate = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		String pickUpTime = ft.format(currentDate);
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("child_seats", (defaultTripDetails.isCustomTrip())?defaultTripDetails.getNumChildSeats():"0"));
		params.add(new BasicNameValuePair("pickup_time", (defaultTripDetails.isCustomTrip())?defaultTripDetails.getPikcupTime():pickUpTime));
		params.add(new BasicNameValuePair("max_no_of_passengers", (defaultTripDetails.isCustomTrip())?defaultTripDetails.getNumPassengers():"1"));
		params.add(new BasicNameValuePair("no_of_bags", (defaultTripDetails.isCustomTrip())?defaultTripDetails.getNumBags():"0"));
		params.add(new BasicNameValuePair("pick_address", FindARideFragment.currentAddress));
		params.add(new BasicNameValuePair("pickup_lat", String.valueOf(requestPosition.latitude)));
		params.add(new BasicNameValuePair("pickup_lng", String.valueOf(requestPosition.longitude)));
		params.add(new BasicNameValuePair("journey_option", "solo"));
		params.add(new BasicNameValuePair("optional_message", ""));
		params.add(new BasicNameValuePair("user_id", SplashActivity.loggedInUser.getId()));
		params.add(new BasicNameValuePair("journey_type", "solo"));
		params.add(new BasicNameValuePair("group_id", "4"));
		params.add(new BasicNameValuePair("cab_id", pinDialog.getCabId()));
		params.add(new BasicNameValuePair("payment_option", "card"));
		params.add(new BasicNameValuePair("payment_status", "authorized"));
		params.add(new BasicNameValuePair("payment_type", "webservice"));
		params.add(new BasicNameValuePair("payment_detail", "0"));
		//params.add(new BasicNameValuePair("token", value))*/
		//createTripObjet(params, false);

			//CustomHttpClass.runPostService(this, APIConstants.METHOD_CREATE_JOURNEY, params, true, false);
		
		ConfirmTripDialogue dialog = new ConfirmTripDialogue(cab, 
										FindARideFragment.currentAddress,
										(defaultTripDetails.isCustomTrip())?defaultTripDetails.getNumPassengers():"1",
										(defaultTripDetails.isCustomTrip())?defaultTripDetails.getNumBags():"0");
		dialog.setParent(FindARideFragment.this);
		dialog.show(getFragmentManager(), "ConfirmTrip");
	}
	
	public void customerApprovalResult(String cardNumber, String cabId)
	{
		String token = "";
		Log.e("Card number received as:", cardNumber);
		for(int i = 0; i < SplashActivity.loggedInUser.getUserCards().size(); i++)
		{
			if(SplashActivity.loggedInUser.getUserCards().get(i).getCardNumber().equals(cardNumber))
			{
				token = SplashActivity.loggedInUser.getUserCards().get(i).getCardToken();
				Log.e("Token received as:", token);
				Date currentDate = new Date();
				SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				
				String pickUpTime = ft.format(currentDate);
				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair("child_seats", (defaultTripDetails.isCustomTrip())?defaultTripDetails.getNumChildSeats():"0"));
				params.add(new BasicNameValuePair("pickup_time", (defaultTripDetails.isCustomTrip())?defaultTripDetails.getPikcupTime():pickUpTime));
				params.add(new BasicNameValuePair("max_no_of_passengers", (defaultTripDetails.isCustomTrip())?defaultTripDetails.getNumPassengers():"1"));
				params.add(new BasicNameValuePair("no_of_bags", (defaultTripDetails.isCustomTrip())?defaultTripDetails.getNumBags():"0"));
				params.add(new BasicNameValuePair("pick_address", FindARideFragment.currentAddress));
				params.add(new BasicNameValuePair("pickup_lat", String.valueOf(requestPosition.latitude)));
				params.add(new BasicNameValuePair("pickup_lng", String.valueOf(requestPosition.longitude)));
				params.add(new BasicNameValuePair("journey_option", "solo"));
				params.add(new BasicNameValuePair("optional_message", ""));
				params.add(new BasicNameValuePair("user_id", SplashActivity.loggedInUser.getId()));
				params.add(new BasicNameValuePair("journey_type", "solo"));
				params.add(new BasicNameValuePair("group_id", "4"));
				params.add(new BasicNameValuePair("cab_id", cabId));
				params.add(new BasicNameValuePair("payment_option", "card"));
				params.add(new BasicNameValuePair("payment_status", "authorized"));
				params.add(new BasicNameValuePair("payment_type", "webservice"));
				params.add(new BasicNameValuePair("payment_detail", "0"));
				params.add(new BasicNameValuePair("token", token));
				createTripObjet(params, false);
				LoaderHelper.showLoader(getActivity(), "Creating trip...", "");
				CustomHttpClass.runPostService(this, APIConstants.METHOD_CREATE_JOURNEY, params, true, false);
				break;
			}
		}
		
	}

	public TripDetails getDefaultTripDetails() {
		return defaultTripDetails;
	}

	public void setDefaultTripDetails(TripDetails defaultTripDetails) {
		this.defaultTripDetails = defaultTripDetails;
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View arg1, int position, long id) {
		String str = (String) adapterView.getItemAtPosition(position);
		try {
			reverseLookUp(str);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
		
	}

	private void reverseLookUp(String str) throws ClientProtocolException, IOException {
		// TODO Auto-generated method stub
		CommonUtilities.hideSoftKeyboard(getActivity());
		LoaderHelper.showLoader(getActivity(), "Locating address on map...", "");
		String url = "https://maps.googleapis.com/maps/api/geocode/json?key=AIzaSyBBxHuIp1b8FdoD3oG47TmIZ9UikDjjcJ0&address=" + URLEncoder.encode(str, "UTF-8");
		CustomHttpClass.runDirectPostService(this, url, null, true, false);
	}
	
}





