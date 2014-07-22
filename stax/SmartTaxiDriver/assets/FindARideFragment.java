package com.smarttaxi.driver.fragments;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.smarttaxi.driver.R;
import com.smarttaxi.driver.components.CabInfoWindow;
import com.smarttaxi.driver.components.ProgressWheelLoader;
import com.smarttaxi.driver.constants.APIConstants;
import com.smarttaxi.driver.entities.Cab;
import com.smarttaxi.driver.entities.CustomHttpException;
import com.smarttaxi.driver.entities.CustomHttpResponse;
import com.smarttaxi.driver.entities.Trip;
import com.smarttaxi.driver.exceptions.LocationNotFoundException;
import com.smarttaxi.driver.helpers.GoogleAPIHelper;
import com.smarttaxi.driver.helpers.LoaderHelper;
import com.smarttaxi.driver.helpers.LocationManager;
import com.smarttaxi.driver.interfaces.HttpProgressListener;
import com.smarttaxi.driver.interfaces.HttpResponseListener;
import com.smarttaxi.driver.model.FactoryModel;
import com.smarttaxi.driver.model.WebServiceModel;
import com.smarttaxi.driver.utils.AppToast;
import com.smarttaxi.driver.utils.Applog;
import com.smarttaxi.driver.utils.Utils;

public class FindARideFragment extends Fragment implements
		HttpResponseListener, HttpProgressListener, OnClickListener,
		OnCameraChangeListener {

	MapView mapView;
	GoogleMap googleMap;
	MapLocationChangedReciever locationChangedReciever;
	boolean isFirstRoundOfCallCompleted = false;
	Button btnFindARide;
	Button btnOptions;
	Button btnPickUpAddress;
	Button btnPickUpHere;
	boolean isUserMarkerCreated = false;
	LatLng currentLatLng;
	ArrayList<Marker> markersList;
	ProgressWheelLoader progressWheelLoader;
	TextView txtTaxiDuration;

	ArrayList<Cab> cabsList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_map, container,
				false);
		mapView = (MapView) rootView.findViewById(R.id.mapView);
//		progressWheelLoader = (ProgressWheelLoader) rootView
//				.findViewById(R.id.progressWheelLoader);
//		btnPickUpAddress = (Button) rootView
//				.findViewById(R.id.btnPickUpAddress);
//		btnPickUpAddress.setOnClickListener(this);
//		btnFindARide = (Button) rootView.findViewById(R.id.btnFindTaxi);
//		btnFindARide.setOnClickListener(this);
//		btnPickUpHere = (Button) rootView.findViewById(R.id.btnPickUpHere);
//		btnPickUpHere.setOnClickListener(this);
//		btnOptions = (Button) rootView.findViewById(R.id.btnOptions);
//		btnOptions.setOnClickListener(this);
//		txtTaxiDuration = (TextView) rootView.findViewById(R.id.txtTaxiDuration);
//		LocationManager locationManager;
//		locationManager = (LocationManager) (getActivity()).getSystemService(Context.LOCATION_SERVICE);
//		locationManager.requestLocationUpdates(android.location.LocationManager.GPS_PROVIDER, 0, 0, this);
	
		try {
			MapsInitializer.initialize(getActivity());
		} catch (GooglePlayServicesNotAvailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		markersList = new ArrayList<Marker>();
		initMap();
		return rootView;
	}

	private void initMap() {
		googleMap = mapView.getMap();

		if (googleMap == null)
			return;

		googleMap.clear();
		googleMap.getUiSettings().setZoomControlsEnabled(false);
		googleMap.setOnCameraChangeListener(this);
		googleMap.setInfoWindowAdapter(new InfoWindowAdapter() {
			
			@Override
			public View getInfoWindow(Marker arg0) {
//				CabInfoWindow cabInfoWindow = new CabInfoWindow(getActivity());
//				return cabInfoWindow;
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

	public void onFindTaxi() {
		progressWheelLoader.setVisibility(View.VISIBLE);
		WebServiceModel.getCabsAroundMe(currentLatLng, this, this);
	}

	private void onPickUpAddressClick() {
		navigateToPickUpLocationActivity();
	}

	private void onPickUpHere() {
		navigateToPickUpLocationActivity();
	}
	
	private void onOptions() {
		navigateToPickUpLocationActivity();
	}
	
	private void navigateToPickUpLocationActivity() {
		try {
			Location location = LocationManager.getInstance(getActivity())
					.getCurrentLocation();
			Applog.Debug("urrentLatLng.latitude " +location.getLongitude());
		} catch (LocationNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		Intent intent = new Intent(getActivity(), PickUpLocationActivity.class);
//		Trip trip = new Trip();
//		trip.setPickupLat(currentLatLng.latitude);
//		trip.setPickupLong(currentLatLng.longitude);
//		trip.setPickupAddress(Utils.validateEmptyString(btnPickUpAddress.getText().toString()));
//		trip.setUserID(FactoryModel.getUser().getId());
//		intent.putExtra("Trip", trip);
////		intent.putExtra("LatLng", currentLatLng);
////		intent.putExtra("Address", btnPickUpAddress.getText());
//		startActivity(intent);
	}

	private void addCabMarkerToMap(Cab cab) {
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.map_taxi_icon);
		Marker marker = googleMap.addMarker(new MarkerOptions().icon(
				BitmapDescriptorFactory.fromBitmap(bitmap)).position(
				cab.getLatLng()).title(cab.getDriver().getName()));
		if (marker != null)
			markersList.add(marker);
	}

	private void makeGoogleDistanceMatrixCall() {
//		Cab firstCab = cabsList.get(0);
		GoogleAPIHelper.timeDuration(cabsList, currentLatLng, this);
	}

	private void addMarkersToMap() {
		for (Cab cab : cabsList) {
			addCabMarkerToMap(cab);
		}
	}

	private void zoomToAdjustAllMarkers() {
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

	private void swtichButtons() {
		if (Utils.isEmptyOrNull(cabsList)) {
			btnFindARide.setVisibility(View.VISIBLE);
			btnPickUpHere.setVisibility(View.GONE);
			return;
		}

		btnFindARide.setVisibility(View.GONE);
		btnPickUpHere.setVisibility(View.VISIBLE);
	}

	private void showMinimumDuration() {
		txtTaxiDuration.setText("Get a taxi in "
				+ cabsList.get(0).getFormattedTime(false));
		txtTaxiDuration.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnFindTaxi:
			onFindTaxi();
			break;
		case R.id.btnPickUpHere:
			onPickUpHere();
			break;
		case R.id.btnPickUpAddress:
			onPickUpAddressClick();
			break;
		case R.id.btnOptions:
			onOptions();
			break;
		default:
			break;
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		mapView.onResume();
//		if (locationChangedReciever == null)
//			locationChangedReciever = new MapLocationChangedReciever();
//		
//		getActivity()
//				.registerReceiver(
//						locationChangedReciever,
//						new IntentFilter(
//								LocationManager.LOCATION_CHANGED_INTENT_ACTION));
	}

	@Override
	public void onPause() {
		super.onPause();
		mapView.onPause();
//		getActivity().unregisterReceiver(locationChangedReciever);
//		locationChangedReciever = null;
	}

	@Override
	public void onDestroy() {
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
		super.onLowMemory();
		mapView.onLowMemory();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onResponse(CustomHttpResponse object) {
		if (object.getMethodName().equals(
				APIConstants.METHOD_GET_CABS_AROUND_ME)) {

			cabsList = (ArrayList<Cab>) object.getResponse();
			FactoryModel.setCabsList(cabsList);
			makeGoogleDistanceMatrixCall();
			addMarkersToMap();
			zoomToAdjustAllMarkers();
			swtichButtons();
			return;
		}

		if (object.getMethodName().equals(
				GoogleAPIHelper.DISTANCE_MATRIX_API_BASE)) {
			progressWheelLoader.setVisibility(View.GONE);
			cabsList = (ArrayList<Cab>)object.getResponse();
			FactoryModel.setCabsList(cabsList);
			showMinimumDuration();
		}
	}

	@Override
	public void onException(CustomHttpException exception) {
		progressWheelLoader.setVisibility(View.GONE);
		txtTaxiDuration.setVisibility(View.INVISIBLE);
		swtichButtons();
		
		if (LoaderHelper.isLoaderShowing())
		{
			LoaderHelper.hideLoader();
			AppToast.ShowToast(this, exception.getMessage());
			
			
		}
	}

	@Override
	public void onProgress(Object value) {
		progressWheelLoader.setProgress((Integer) value);
	}

	@Override
	public void onCameraChange(CameraPosition cameraPosition) {
		LatLng address = cameraPosition.target;
//		btnPickUpAddress.setText(LocationManager.getInstance(getActivity())
//				.getLocationAddress(address));

	}

	class MapLocationChangedReciever extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (googleMap == null) {
				return;
			}

			if (isUserMarkerCreated)
				return;

			try {
				Location location = LocationManager.getInstance(getActivity())
						.getCurrentLocation();
				LatLng userLatLng = new LatLng(location.getLatitude(),
						location.getLongitude());

				if (userLatLng.latitude == 0.0 && userLatLng.longitude == 0.0) {
					return;
				}
Applog.Debug(""+userLatLng.latitude);
				currentLatLng = userLatLng;
				String address = LocationManager.getInstance(getActivity())
						.getCurrentLocationAddress();
				btnPickUpAddress.setText(address);
//				googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
//						userLatLng, 15));
				isUserMarkerCreated = true;

			} catch (LocationNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
}
