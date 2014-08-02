package com.smart.taxi.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.maps.GeoPoint;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.smarttaxi.client.R;
import com.smart.taxi.activities.ContainerActivity;
import com.smart.taxi.activities.CustomHttpClass;
import com.smart.taxi.activities.SplashActivity;
import com.smart.taxi.components.CFTextView;
import com.smart.taxi.constants.APIConstants;
import com.smart.taxi.entities.CustomHttpException;
import com.smart.taxi.entities.CustomHttpResponse;
import com.smart.taxi.entities.TripDetails;
import com.smart.taxi.helpers.JsonHelper;
import com.smart.taxi.helpers.LoaderHelper;
import com.smart.taxi.interfaces.HttpResponseListener;
import com.smart.taxi.utils.CommonUtilities;
import com.smart.taxi.utils.Utils;

public class TripRequestFragment extends BaseFragment implements
		HttpResponseListener,
		OnClickListener, 
		OnMyLocationButtonClickListener{
	public static final String TAG = "tripRequestFragment";
	private MapFragment mapFragment;	
	public CFTextView txtTripStatus;
	public CFTextView txtDriverStatus;
	private Button btnCancelTrip;
	private GoogleMap googleMap;
	private ContainerActivity parent;
	private Marker myLocationMarker;
	private Marker driverLocationMarker;
	private boolean isSetupTrip = false;
	private Timer timer;
	private MyTimerTask myTimerTask;
	private HttpResponseListener listener;
	private int[] polyLineColors = new int[]{Color.GREEN, Color.BLUE, Color.MAGENTA, Color.CYAN, Color.YELLOW, Color.WHITE};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parent = ((ContainerActivity)getActivity());
		parent.tripRequestRef = this;
		listener = this;
	}
	
	@Override 
	public void onPause()
	{
		super.onPause();
	}
	
	@Override
	public void onResume()
	{
		getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onResume();
		setupMap();
		if(SplashActivity.isTripRequested && SplashActivity.getTripNewDetails() != null &&SplashActivity.getTripNewDetails().getTripStatus().equals(TripDetails.ACCEPTED))
		{
			requestPath();
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		try{
			rootView = inflater.inflate(R.layout.fragment_trip_details, container, true);
			mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapTripStatus);
			initUI();
			return super.onCreateView(inflater, container, savedInstanceState);
		}catch(Exception ex)
		{
			return rootView;
		}
		
	}
	
	private void initUI() {
		try{
			txtTripStatus = (CFTextView) rootView.findViewById(R.id.txtTripStatus);
			txtDriverStatus = (CFTextView) rootView.findViewById(R.id.txtDriverStatus);
			btnCancelTrip = (Button) rootView.findViewById(R.id.btnCancelTrip);
			btnCancelTrip.setOnClickListener(this);
			//Log.e("Trip status", SplashActivity.getTripNewDetails().getTripStatus());
			setupMap();
			if(SplashActivity.getTripNewDetails().getTripStatus().equals(TripDetails.ACCEPTED))
			{
				txtTripStatus.setText("Driver confirmed");
				startListeneingForDriverPosition();
			}else if(SplashActivity.getTripNewDetails().getTripStatus().equals(TripDetails.ARRIVED))
			{
				txtTripStatus.setText("Your driver has arrived");
				stopListeningForDriverPosition();
			}
		}catch(Exception ex)
		{
			
		}
		
		
	}

	private void setupMap() {
		// TODO Auto-generated method stub
		try{
			MapsInitializer.initialize(getActivity());
		}catch(Exception ex)
		{
			Log.e("TripFragment->setupMap","Error in map initialization");
			return;
		}
		if(googleMap == null)
		{
			if(mapFragment == null)
				return;
			googleMap = mapFragment.getMap();
			if (googleMap == null)
				return;
			googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			googleMap.setMyLocationEnabled(false);
			googleMap.setPadding(10, 10, 10, 100);
			googleMap.getUiSettings().setZoomControlsEnabled(true);

		}
		setupTrip();
	}

	private void setupTrip() {
		if(isSetupTrip)
			return;
		if(SplashActivity.getTripNewDetails() != null)
		{
			LatLng position = new LatLng(Double.valueOf(SplashActivity.getTripNewDetails().getLat()), Double.valueOf(SplashActivity.getTripNewDetails().getLng()));
			myLocationMarker = googleMap.addMarker(new MarkerOptions()
												.position(position)
												.snippet("")
												.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_user_icon)));
			CameraPosition cp;
			if(SplashActivity.getTripNewDetails().getDriverLatLng() != null)
			{
				driverLocationMarker = googleMap.addMarker(new MarkerOptions()
											.position(SplashActivity.getTripNewDetails().getDriverLatLng())
											.snippet("")
											.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_taxi_icon)));
				cp = new CameraPosition.Builder().target(SplashActivity.getTripNewDetails().getDriverLatLng())
						.zoom(15)
		                .bearing(0)
		                .tilt(0)
		                .build();
			}else{
			
				 cp = new CameraPosition.Builder().target(new LatLng(position.latitude, position.longitude))
						.zoom(15)
		                .bearing(0)
		                .tilt(0)
		                .build();
			}
			googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp), 2000, new CancelableCallback() {
	            @Override
	            public void onFinish() {
	            	//requestPath();
	            }

	            @Override
	            public void onCancel() {
	                //requestPath();
	            }
	        });
			isSetupTrip  = true;
			//startListeneingForDriverPosition();
		}
	}

	public void startListeneingForDriverPosition() {
		requestPath();
		if(timer != null){
		     timer.cancel();
		    }
	    timer = new Timer();
	    myTimerTask = new MyTimerTask();
	    timer.schedule(myTimerTask, 5000, 10000);
	    plotDriverPosition();
		
	}

	protected void requestPath() {
		if(driverLocationMarker != null && myLocationMarker != null)
		{
			
			String url = String.format("http://maps.googleapis.com/maps/api/directions/json?origin=%s,%s&destination=%s,%s&sensor=true&mode=driving&alternatives=true"
					,Double.valueOf(driverLocationMarker.getPosition().latitude),
					Double.valueOf(driverLocationMarker.getPosition().longitude),
					Double.valueOf(myLocationMarker.getPosition().latitude),
					Double.valueOf(myLocationMarker.getPosition().longitude));
			LoaderHelper.showLoader(getActivity(), "Loading routes...", "");
			CustomHttpClass.runDirectPostService(this, url, null, false, false);
			/*Routing routing = new Routing(TravelMode.DRIVING);
			routing.registerListener(new RoutingListener() {
				
				@Override
				public void onRoutingSuccess(PolylineOptions mPolyOptions) {
					List<LatLng> points = mPolyOptions.getPoints();
					PolylineOptions polyoptions = new PolylineOptions();
					polyoptions.color(Color.RED);
					polyoptions.width(10);
					polyoptions.addAll(points);
					googleMap.addPolyline(polyoptions);
					
				}
				
				@Override
				public void onRoutingStart() {
					Log.e("Routing", "Started");
				}
				
				@Override
				public void onRoutingFailure() {
					Log.e("Routing", "Failed");
					
				}
			});
			Log.e("Ployline call", url);
			routing.execute(new LatLng(driverLocationMarker.getPosition().latitude, driverLocationMarker.getPosition().latitude),
					new LatLng(myLocationMarker.getPosition().latitude, myLocationMarker.getPosition().latitude));*/
		}
		
	}
	
	private List<GeoPoint> decodePoly(String encoded) {

		List<GeoPoint> poly = new ArrayList<GeoPoint>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			GeoPoint p = new GeoPoint((int) (((double) lat / 1E5) * 1E6),
				 (int) (((double) lng / 1E5) * 1E6));
			poly.add(p);
		}

		return poly;
	}

	@Override
	public void onClick(View buttonSender)
	{
		super.onClick(buttonSender);
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("journey_id", SplashActivity.getTripNewDetails().getJourneyId()));
		params.add(new BasicNameValuePair("user_id", SplashActivity.loggedInUser.getId()));
		params.add(new BasicNameValuePair("cancelled_by", "user"));
		LoaderHelper.showLoader(getActivity(), "Cancelling your trip...", "");
		CustomHttpClass.runPostService(this, APIConstants.METHOD_CANCEL_JOURNEY, params, false, false);
	}
	
	@Override
	public void onResponse(CustomHttpResponse response)
	{
		super.onResponse(response);
		if(response.getMethodName() == APIConstants.METHOD_CANCEL_JOURNEY && response.getStatusCode() == 0)
		{
			LoaderHelper.hideLoaderSafe();
			try{
				SplashActivity.isTripRequested = false;
				SplashActivity.setTripNewDetails(null);
				ContainerActivity.container.selectItem(0);
			}catch (Exception ex)
			{
				Log.i("asda","asdasd");
			}
			
		}
		else if(response.getMethodName() == APIConstants.METHOD_GET_JOURNEY_DETAILS)
		{
			LatLng newDriverLocation = parseForLatLng(response);
			if(newDriverLocation != null && driverLocationMarker != null)
			{
				Log.e("New lat:", String.valueOf(newDriverLocation.latitude));
				Log.e("New lng:", String.valueOf(newDriverLocation.longitude));
				driverLocationMarker.setPosition(newDriverLocation);
			}
		}
		else{
			LoaderHelper.hideLoaderSafe();
			JsonObject object = JsonHelper.parseToJsonObject(response.getRawJson());
			if(object.has("routes"))
			{
				JsonArray routes = JsonHelper.getJsonArray(object, "routes");
				if(routes != null && routes.size() > 0)
				{
					for(int i = 0; i < routes.size(); i++)
					{
						JsonObject route = routes.get(i).getAsJsonObject();
						//Log.e("Actual route", route.getAsString());
						if(route.has("overview_polyline"))
						{
							String points = route.get("overview_polyline").getAsJsonObject().get("points").getAsString();
							Log.e("Route: ", points);
							if(!Utils.isEmptyOrNull(points))
							{
								/*List<GeoPoint> pointsInLatLng = decodePoly(points);
								Log.e("Polyline steps count", String.valueOf(pointsInLatLng.size()));*/
								List<LatLng> positions = new ArrayList<LatLng>();
								positions = decodePolyLatLng(points);
								/*for(i = 0; i < pointsInLatLng.size(); i++)
								{
									positions.add(i, new LatLng(pointsInLatLng.get(i).getLatitudeE6(), pointsInLatLng.get(i).getLongitudeE6()));
								}*/
								googleMap.addPolyline(new PolylineOptions()
							     .addAll(positions)
							     .width(5)
							     .color(polyLineColors[i%5]).geodesic(true));
							}
						}
					}
				}
				
			}
		}
	}
	
	private ArrayList<LatLng> decodePolyLatLng(String encoded) {
        ArrayList<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            poly.add(position);
        }
        return poly;
    }
	
	private LatLng parseForLatLng(CustomHttpResponse object) {
		JsonObject journeyJosn = JsonHelper.parseToJsonObject(object.getRawJson()).get("body").getAsJsonObject();
		journeyJosn = journeyJosn.get("journeys").getAsJsonObject();
		LatLng result = null;
		try{
			String journeyId = SplashActivity.getTripNewDetails().getJourneyId();
			if(journeyJosn.has(journeyId))
			{
				journeyJosn = journeyJosn.get(journeyId).getAsJsonObject();
				String cabProviderId = journeyJosn.get("cab_provider_id").getAsString();
				String cabId = journeyJosn.get("cab_id").getAsString();
				journeyJosn = journeyJosn.get("journey_users").getAsJsonObject();
				String key = journeyJosn.entrySet().iterator().next().getKey(); 
				JsonObject tripInfo = journeyJosn.getAsJsonObject(key).getAsJsonObject();
				JsonObject cabLocation = tripInfo.get("CabLocation").getAsJsonObject();
				cabLocation = cabLocation.get(cabId).getAsJsonObject();
				result = new LatLng(cabLocation.get("latitude").getAsDouble(), cabLocation.get("longitude").getAsDouble());
			}
		}catch(Exception ex)
		{
			Log.e("Exception:", ex.toString());
			return null;
		}
		return result;
	}

	@Override
	public void onException(CustomHttpException exception)
	{
		super.onException(exception);
		if(exception.getMethodName() == APIConstants.METHOD_CANCEL_JOURNEY)
		{
			CommonUtilities.displayAlert(getActivity(), "Trip could not be cancelled at the moment,  please try later.", "Cancel failed:", "Retry", "Cancel", true);
		}
	}

	@Override
	public boolean onMyLocationButtonClick() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void reset(Object data)
	{
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				
				}
			});
		
	}

	public void stopListeningForDriverPosition() {
		if (timer!=null){
			timer.cancel();
			timer = null;
	    }
		
	}

	public void plotDriverPosition() {
		// TODO Auto-generated method stub
		
	}
	
	class MyTimerTask extends TimerTask {

		  @Override
		  public void run() {
			  if(SplashActivity.isTripRequested && !Utils.isEmptyOrNull(SplashActivity.getTripNewDetails().getJourneyId()))
			  {
				  List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				  params.add(new BasicNameValuePair("user_id", SplashActivity.loggedInUser.getId()));
				  params.add(new BasicNameValuePair("journey_id", SplashActivity.getTripNewDetails().getJourneyId()));
				  CustomHttpClass.runPostService(listener, APIConstants.METHOD_GET_JOURNEY_DETAILS, params, true, false);
			  }else{
				  timer.cancel();
				  timer = null;
			  }
			  
		  }
		  
		 }
	
	
}
