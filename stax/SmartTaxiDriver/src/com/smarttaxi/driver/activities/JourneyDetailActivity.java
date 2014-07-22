package com.smarttaxi.driver.activities;

import static com.smarttaxi.driver.gcm.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGestureListener;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
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

import com.smarttaxi.driver.BAL.Driver;
import com.smarttaxi.driver.activities.ServiceLocation;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.internal.bt;
//import com.google.android.gms.internal.fr;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.smarttaxi.driver.R;
import com.smarttaxi.driver.adapters.LeftMenuAdapter;
import com.smarttaxi.driver.constants.APIConstants;
import com.smarttaxi.driver.custom.design.CircularImageView;
import com.smarttaxi.driver.custom.design.TransparentPanel;
import com.smarttaxi.driver.entities.AcceptOrRejectJourney;
import com.smarttaxi.driver.entities.CustomHttpException;
import com.smarttaxi.driver.entities.CustomHttpResponse;
import com.smarttaxi.driver.entities.Journey;
import com.smarttaxi.driver.exceptions.LocationNotFoundException;
import com.smarttaxi.driver.fragments.FindARideFragment;
import com.smarttaxi.driver.fragments.UserProfileFragment;
import com.smarttaxi.driver.gcm.CommonUtilities;
import com.smarttaxi.driver.gcm.GCMIntentService;
import com.smarttaxi.driver.gcm.WakeLocker;
import com.smarttaxi.driver.helpers.AlertBoxHelper;
import com.smarttaxi.driver.helpers.DownloadImageAsync;
import com.smarttaxi.driver.helpers.LoaderHelper;
import com.smarttaxi.driver.helpers.LocationManager;
import com.smarttaxi.driver.interfaces.HttpResponseListener;
import com.smarttaxi.driver.model.WebServiceModel;

import com.smarttaxi.driver.preferences.PreferencesHandler;
import com.smarttaxi.driver.utils.AppToast;
import com.smarttaxi.driver.utils.Applog;
import com.smarttaxi.driver.utils.NetworkAvailability;
import com.smarttaxi.driver.utils.Utils;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;

public class JourneyDetailActivity extends ActionBarActivity implements
		OnGestureListener, OnClickListener, HttpResponseListener,
		OnMarkerDragListener {

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
	LatLng currentLatLng;
	private Marker userLocationMarker;
	private PreferencesHandler preferencesHandler;
	MapLocationChangedReciever locationChangedReciever;
	public Button popupBottomBarButton;
	com.smarttaxi.driver.custom.design.TransparentPanel popup;
	String DropOffTime;
	String DropOffAddress;
	LatLng _latlng;
	Button button;
	public static AlertDialog alertDialog;
	FrameLayout mainPopup;
	FrameLayout frameLayoutContent;
	FindARideFragment fragment;
	private static Double currentLatitude, currentLongitude;
	TextView mDisplay;
	static boolean isFromFindAride = false;
	public boolean isPathPlotted = false;

	@SuppressLint("ShowToast")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		Bundle extras = getIntent().getExtras();
		if (extras == null)
			return;

		currentJourneyID = extras.getString(Intent.EXTRA_TEXT);
		currentLatitude = extras.getDouble("currentLat");
		currentLongitude = extras.getDouble("currentLng");
		isFromFindAride = getIntent().getExtras().getBoolean("isFromFindARide");
		initUI();
		try {

//			if (NetworkAvailability.IsNetworkAvailable(this.getBaseContext())) {
//
//				NetworkAvailability.CheckBroadcastEnableDisable(
//						this.getApplicationContext(), "EnablingBroadcast");
//				NetworkAvailability.IsGpsEnabled(this.getApplicationContext());
//			}

			int resultCode = GooglePlayServicesUtil
					.isGooglePlayServicesAvailable(this);
			if (ConnectionResult.SUCCESS == resultCode) {
				LocationManager.getInstance(this).startLocationService();
			} else {
				Toast.makeText(this, "GOOGLE PLAY SERVICES ARE NOT AVAILABLE",
						Toast.LENGTH_LONG).show();
			}

			// if
			// (NetworkAvailability.IsNetworkAvailable(this.getBaseContext())) {

			// starting Service
			// startService(new Intent(MainActivity.this,
			// ServiceLocation.class));

			/*
			 * InitiateGcm gcm = new InitiateGcm(this); if (gcm.isRegistered())
			 * Applog.Debug("DEVICE IS REGISTERED");
			 */

			// registerReceiver(mHandleMessageReceiver, new IntentFilter(
			// APIConstants.DISPLAY_MESSAGE_ACTION));

			// fragment = new FindARideFragment();

			FragmentManager fragmentManager = getSupportFragmentManager();
			fragment = (FindARideFragment) fragmentManager
					.findFragmentByTag("findaRideFragment");

			if (fragment == null) {
				preferencesHandler = new PreferencesHandler(this);
				fragment = new FindARideFragment();
				fragment.disableMarkerUpdation();
				if (currentJourneyID != null && isFromFindAride) {
					LoaderHelper.showLoader(this, "Loading please wait...", "");
					// String userName = editTxtUserName.getText().toString();
					// String password = editTextPassword.getText().toString();

					if (preferencesHandler.getOriginalDriverUserID() > 0) {
						String UserID = String.valueOf(preferencesHandler
								.getOriginalDriverUserID());
						WebServiceModel.callAccepted("accept", UserID,
								currentJourneyID, this);
					}

				}

			}

			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();
			fragmentTransaction.replace(R.id.frameLayoutContent, fragment);
			fragmentTransaction.commit();

			popupBottomBarButton.setOnClickListener(this);
			if(isFromFindAride)
			{
				beep.setOnClickListener(this);
			}
			beep.setEnabled(isFromFindAride);
			cancelJourney.setOnClickListener(this);
			endJourney.setOnClickListener(this);

		} catch (Exception e) {

			Applog.Error("com.smarttaxi.driver.activities : OnCreate() : Message= "
					+ e.getMessage());
			// TODO: handle exception
		}
	}

	@SuppressLint("InlinedApi")
	public void getCircularImage() {

		// inner class
		// DownloadImageAsync
		/*
		 * new DownloadImageAsync((ImageView)
		 * findViewById(R.id.testImage),JourneyDetailActivity.this) .execute(
		 * "http://i1279.photobucket.com/albums/y523/Kate_Fever/twittericon_zpsfe1b268d.jpg"
		 * );
		 */

		/*
		 * CircularImageView img = (CircularImageView)
		 * findViewById(R.id.circular_image_view);
		 * img.setDrawingCacheEnabled(true); new DownloadImageAsync((ImageView)
		 * img, JourneyDetailActivity.this) .execute(
		 * "http://i1279.photobucket.com/albums/y523/Kate_Fever/twittericon_zpsfe1b268d.jpg"
		 * );
		 */
	}

	private void getBottomBarPopup() {

		// Start out with the popup initially hidden.

		// getCircularImage();

		/*
		 * if (popup.isShown()) popup.setVisibility(View.GONE);
		 */

		animShow = AnimationUtils.loadAnimation(this, R.anim.popup_show);
		animHide = AnimationUtils.loadAnimation(this, R.anim.popup_hide);

		// popupBottomBarButton = (Button) findViewById(R.id.show_popup_button);
		// final Button hideButton = (Button)
		// findViewById(R.id.hide_popup_button);

		/*
		 * popupBottomBarButton.setOnClickListener(new View.OnClickListener() {
		 * public void onClick(View view) {
		 */

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

		/*
		 * popup.setVisibility(View.VISIBLE); popup.startAnimation( animShow );
		 * popupBottomBarButton.setEnabled(false); hideButton.setEnabled(true);
		 */

		/*
		 * } });
		 */

		/*
		 * hideButton.setOnClickListener(new View.OnClickListener() { public
		 * void onClick(View view) { popup.startAnimation( animHide );
		 * popupBottomBarButton.setEnabled(true); hideButton.setEnabled(false);
		 * popup.setVisibility(View.GONE); }});
		 */

		/*
		 * final TextView locationName = (TextView)
		 * findViewById(R.id.location_name); final TextView locationDescription
		 * = (TextView) findViewById(R.id.location_description);
		 * 
		 * locationName.setText("Animated Popup"); locationDescription.setText(
		 * "Animated popup is created by http://www.androidbysravan.blogspot.com"
		 * +
		 * " Transparent layout is used on this example, and animation xml is also used"
		 * );
		 */
	}

	@Override
	protected void onDestroy() {
		/*
		 * if (mRegisterTask != null) { mRegisterTask.cancel(true); }
		 */
		unregisterReceiver(mHandleMessageReceiver);
		// GCMRegistrar.onDestroy(this);
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {

		/*
		 * if(isFromFindAride) { fragment.enableMarkerUpdation(); Intent i = new
		 * Intent(getBaseContext(), MainActivity.class);
		 * i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); startActivity(i);
		 * finish(); fragment.enableMarkerUpdation(); Intent i = new
		 * Intent(getBaseContext(), MainActivity.class);
		 * 
		 * i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 * //i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		 * i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //
		 * i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		 * MainActivity.isRequestShown = true;
		 * 
		 * 
		 * startActivity(i); finish();
		 * 
		 * } else { super.onBackPressed(); }
		 */

		if (isFromFindAride) {
			Applog.Debug("isFronFindAride =true");
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);

			// set title
			alertDialogBuilder.setTitle("Alert");

			// set dialog message
			alertDialogBuilder
					.setMessage("Are you sure you want to cancel this journey?")
					.setCancelable(true)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {

									if (!isNullOrEmpty(journeyData.getUserId())
											&& !isNullOrEmpty(currentJourneyID)) {
										LoaderHelper.showLoader(
												JourneyDetailActivity.this,
												"Cancel journey...", "");
										WebServiceModel.cancelJourney(
												JourneyDetailActivity.this,
												journeyData.getUserId(),
												currentJourneyID);// user_id
									}

									return;
								}
							});
			alertDialogBuilder.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// if this button is clicked, close
							// current activity

							return;
						}
					});

			// create alert dialog
			alertDialogBuilder.create();

			// show it
			alertDialogBuilder.show();
		} else {

			fragment.enableMarkerUpdation();
			super.onBackPressed();
			Applog.Debug("isFronFindAride =false");
		}

	}

	
	@Override
	protected void onStop() {
		
		// AppToast.ShowToast(this, "onStop");
		super.onStop();

	}

	
	@Override
	protected void onRestart() {
		// AppToast.ShowToast(this, "onRestart");
		
		
		Driver driver = new Driver(this);
		driver.enablePobStatus("yes");
		// /driver.resetTaxiStatus("no");
		LoaderHelper.showLoader(this, "Fetching journey details...", "");
		com.smarttaxi.driver.BAL.Journey _journey = new com.smarttaxi.driver.BAL.Journey(
				this);
		_journey.SaveJourney(currentJourneyID);

		getDetailsOfJourney(currentJourneyID);
		if(!isFromFindAride)
			isFromFindAride=true;

		/*
		 * if (currentLatitude != null && currentLongitude != null)
		 * PlotPath(currentLatitude, currentLongitude);
		 */

		super.onRestart();

	}

	@Override
	protected void onResume() {
		// AppToast.ShowToast(this, "onResume");
		
		NetworkAvailability.CheckBroadcastEnableDisable(
				this.getApplicationContext(), "EnablingBroadcast");
		NetworkAvailability.IsNetworkAvailable(this.getBaseContext());
		NetworkAvailability.IsGpsEnabled(this.getBaseContext());
		
		
		if (locationChangedReciever == null)
			locationChangedReciever = new MapLocationChangedReciever();
		
				registerReceiver(
						locationChangedReciever,
						new IntentFilter(
								LocationManager.LOCATION_CHANGED_INTENT_ACTION));
		beep.setEnabled(isFromFindAride);
		if (!isFromFindAride) {
			beep.setVisibility(View.INVISIBLE);
			Driver driver = new Driver(this);
			driver.enablePobStatus("yes");
			// /driver.resetTaxiStatus("no");
			LoaderHelper.showLoader(this, "Fetching journey details...", "");
			com.smarttaxi.driver.BAL.Journey _journey = new com.smarttaxi.driver.BAL.Journey(
					this);
			_journey.SaveJourney(currentJourneyID);

			getDetailsOfJourney(currentJourneyID);

		}else{
			beep.setVisibility(View.VISIBLE);
		}

		super.onResume();
		fragment.mapView.onResume();

		/*
		 * if(mHandleMessageReceiver==null)
		 * registerReceiver(mHandleMessageReceiver, new IntentFilter(
		 * DISPLAY_MESSAGE_ACTION));
		 */

		/*
		 * if (JourneyDetails.getJourneyData() != null) {
		 * 
		 * FragmentManager fragmentManager = getSupportFragmentManager();
		 * fragment = (FindARideFragment) fragmentManager
		 * .findFragmentByTag("findaRideFragment");
		 * 
		 * if (fragment == null) fragment = new FindARideFragment();
		 * 
		 * FragmentTransaction fragmentTransaction = fragmentManager
		 * .beginTransaction();
		 * fragmentTransaction.replace(R.id.frameLayoutContent, fragment);
		 * fragmentTransaction.commit();
		 * 
		 * We have all the details here For journey Log.v("1",
		 * journeyData.getJourneyCapId()); Log.v("2",
		 * journeyData.getJourneyUserId()); Log.v("3", journeyData.getUserId());
		 * 
		 * // AppToast.ShowToast(this, "Original PathPlot Called.");
		 * PlotPath(Double.parseDouble(journeyData .getGetPickUpLatitude()),
		 * Double.parseDouble(journeyData .getGetPickUpLongitude()));
		 * 
		 * UpdatePopUpData(journeyData.getJourneyNotificationStatus(),
		 * journeyData.getPickUpAddress(), journeyData.getUserPickUpTime(),
		 * journeyData.getUserOptionalMessage(),
		 * journeyData.getMaxNoOfPassenger(), journeyData.getUserNoOfBags(),
		 * journeyData.getUserChildSeats(), journeyData.getUserFirstName(),
		 * journeyData.getUserLastName(), journeyData.getUserImage());
		 * 
		 * }
		 */

	}

	@Override
	public void onPause() {

		NetworkAvailability.CheckBroadcastEnableDisable(
				this.getApplicationContext(), "DisablingBroadcast");
		
		unregisterReceiver(locationChangedReciever);
		locationChangedReciever = null;

		GCMIntentService.cancelNotification(getApplicationContext());
		super.onPause();
		fragment.mapView.onPause();
	}

	private void initUI() {

		setContentView(R.layout.activity_journeydetail);
		cancelJourney = (Button) findViewById(R.id.cancelJourney);
		beep = (Button) findViewById(R.id.beep);
		endJourney = (Button) findViewById(R.id.endjourney);
		beep.setEnabled(isFromFindAride);
		beep.setVisibility((isFromFindAride)?View.VISIBLE:View.INVISIBLE);
		// profileImage =
		// (CircularImageView)findViewById(R.id.circular_image_view);
		popupBottomBarButton = (Button) findViewById(R.id.show_popup_button);
		popup = (TransparentPanel) findViewById(R.id.popup_window);
		
		/*
		 * drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayoutMain);
		 * 
		 * layoutDrawer = (RelativeLayout) findViewById(R.id.layoutDrawer);
		 * listViewDrawer = (ListView) findViewById(R.id.listViewDrawer);
		 * 
		 * leftMenuAdapter = new LeftMenuAdapter(this);
		 * listViewDrawer.setAdapter(leftMenuAdapter); actionBarDrawerToggle =
		 * new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer,
		 * R.string.drawer_open, R.string.drawer_close) {
		 * 
		 * @Override public void onDrawerSlide(View drawerView, float
		 * slideOffset) { super.onDrawerSlide(drawerView, slideOffset);
		 * drawerLayout.bringChildToFront(drawerView);
		 * drawerLayout.requestLayout(); }
		 * 
		 * @Override public void onDrawerOpened(View view) {
		 * getSupportActionBar().setTitle("Driver Menu");
		 * supportInvalidateOptionsMenu(); }
		 * 
		 * @Override public void onDrawerClosed(View view) {
		 * getSupportActionBar().setTitle(""); supportInvalidateOptionsMenu(); }
		 * 
		 * };
		 * 
		 * drawerLayout.setDrawerListener(actionBarDrawerToggle);
		 */
		// actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIcon(R.drawable.btn_menu_normal);
		// getSupportActionBar().setHomeButtonEnabled(true);

		popup.setVisibility(View.GONE);

		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				DISPLAY_MESSAGE_ACTION));

		DropOffTime = Utils.getFormatedTimeString();
		_latlng = new LatLng(currentLatitude, currentLongitude);
		// String DropOffAddress =
		// Utils.getAddressString(currentLatitude,
		// currentLongitude, getBaseContext());
		DropOffAddress = LocationManager.getInstance(this).getLocationAddress(
				_latlng);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		/*
		 * fragment.enableMarkerUpdation(); if(isFromFindAride) {
		 * 
		 * Intent i = new Intent(getBaseContext(), MainActivity.class);
		 * startActivity(i); finish();
		 * 
		 * } else { super.onBackPressed(); }
		 */
		if (isFromFindAride) {
			Applog.Debug("isFronFindAride =true");
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);

			// set title
			alertDialogBuilder.setTitle("Alert");

			// set dialog message
			alertDialogBuilder
					.setMessage("Are you sure you want to cancel this journey?")
					.setCancelable(true)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// if this button is clicked, close
									// current activity
									if (!isNullOrEmpty(journeyData.getUserId())
											&& !isNullOrEmpty(currentJourneyID)) {
										LoaderHelper.showLoader(
												JourneyDetailActivity.this,
												"Cancel journey...", "");
										WebServiceModel.cancelJourney(
												JourneyDetailActivity.this,
												journeyData.getUserId(),
												currentJourneyID);// user_id
									}

									return;
								}
							});
			alertDialogBuilder.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// if this button is clicked, close
							// current activity

							return;
						}
					});

			// create alert dialog
			alertDialogBuilder.create();

			// show it
			alertDialogBuilder.show();
		} else {
			fragment.enableMarkerUpdation();
			super.onBackPressed();
			Applog.Debug("isFronFindAride =false");

		}

		// super.onBackPressed();
		return true;

		// fragment.cancelCurrentJourneyAndRestore();
		// return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResponse(CustomHttpResponse object) {

		try {
			// TODO Auto-generated method stub
			String msg = null;

			if (LoaderHelper.progressDialog != null) {
				if (LoaderHelper.isLoaderShowing())
					LoaderHelper.hideLoader();

				if (object != null)
					AppToast.ShowToast(this, object.getResponseMsg());
			}

			else {
				AppToast.ShowToast(this, object.getResponseMsg());
				if (LoaderHelper.progressDialog != null) {
					if (LoaderHelper.isLoaderShowing())
						LoaderHelper.hideLoader();
				}

			}

			if (object.getStatusCode() == APIConstants.SUCESS_CODE) {

				if (object.getResponse() instanceof AcceptOrRejectJourney) {
					AcceptOrRejectJourney acceptReq = (AcceptOrRejectJourney) object
							.getResponse();
					if (acceptReq != null) {
						Driver driver = new Driver(this);
						driver.enablePobStatus("yes");
						// /driver.resetTaxiStatus("no");
						LoaderHelper.showLoader(this,
								"Fetching journey details...", "");
						com.smarttaxi.driver.BAL.Journey _journey = new com.smarttaxi.driver.BAL.Journey(
								this);

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

						// AppToast.ShowToast(this,
						// "Original PathPlot Called.");
						PlotPath(Double.parseDouble(journeyData
								.getGetPickUpLatitude()),
								Double.parseDouble(journeyData
										.getGetPickUpLongitude()));

						UpdatePopUpData(
								journeyData.getJourneyNotificationStatus(),
								journeyData.getPickUpAddress(),
								journeyData.getUserPickUpTime(),
								journeyData.getUserOptionalMessage(),
								journeyData.getMaxNoOfPassenger(),
								journeyData.getUserNoOfBags(),
								journeyData.getUserChildSeats(),
								journeyData.getUserFirstName(),
								journeyData.getUserLastName(),
								journeyData.getUserImage(),
								journeyData.getUserPhoneNo());

					}
				}

				else if (object.getMethodName().equals(
						APIConstants.METHOD_CANCEL_JOURNEY)) {

					Driver driver = new Driver(this);

					if (!object.getResponseMsg().contains("not")) {
						/*
						 * AlertBoxHelper.showAlertBox( (Activity)
						 * getApplicationContext(), "Alert",
						 * "Journey cancelled by the customer.");
						 */

						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
								JourneyDetailActivity.this);

						alertDialogBuilder.setCancelable(false);
						// set title
						alertDialogBuilder.setTitle("Alert");

						// set dialog message
						alertDialogBuilder
								.setMessage(
										"This journey has been cancelled successfully.")
								.setCancelable(false)
								.setPositiveButton("Ok",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												// if this button is clicked,
												// close
												// current activity

												Driver driver = new Driver(
														JourneyDetailActivity.this);

												driver.enablePobStatus("no");
												driver.resetTaxiStatus("yes");
												com.smarttaxi.driver.BAL.Journey jrny = new com.smarttaxi.driver.BAL.Journey(
														JourneyDetailActivity.this);
												fragment.enableMarkerUpdation();
												jrny.removeSavedJourney();
												journeyData = null;
												// MainActivity.mFragmentStack.clear();

												Intent i = new Intent(
														getBaseContext(),
														MainActivity.class);
												i.putExtra("IsRegistered", true);
												// i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
												// i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
												// i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
												// i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
												MainActivity.isRequestShown = false;

												startActivity(i);
												finish();

												return;
											}
										});

						alertDialogBuilder.create();

						alertDialogBuilder.show();

					} else {
						AlertBoxHelper.showAlertBox(JourneyDetailActivity.this,
								"", object.getResponseMsg());

					}

				} else if (object.getMethodName().equals(
						APIConstants.METHOD_SEND_BEEP_NOTIFICATION)) {
					AlertBoxHelper.showAlertBox(JourneyDetailActivity.this, "",
							object.getResponseMsg());
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void PlotPath(Double _pickLat, Double _pickLong) {
		// TODO Auto-generated method stub

		try {
			
			if (fragment.googleMap != null)
				fragment.googleMap.clear();

			final LatLng ToLoc,FromLoc;
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

			 FromLoc = new LatLng(currentLatitude, currentLongitude);

			Applog.Debug("JLatitude= " + currentLatitude);
			Applog.Debug("JLongitude= " + currentLongitude);
			Routing routing = new Routing(Routing.TravelMode.DRIVING);

			routing.registerListener(new RoutingListener() {

				@Override
				public void onRoutingSuccess(PolylineOptions mPolyOptions) {
					// TODO Auto-generated method stub
					PolylineOptions polyoptions = new PolylineOptions();
					polyoptions.color(Color.GRAY);
					polyoptions.width(8);
					polyoptions.addAll(mPolyOptions.getPoints());
					fragment.googleMap.addPolyline(polyoptions);
					/*
					 * Marker markerobj = fragment.googleMap.addMarker(new
					 * MarkerOptions() .position(ToLoc) .title("Location")
					 * .snippet( "Lat: " + ToLoc.latitude + ", Long: " +
					 * ToLoc.longitude) .icon(BitmapDescriptorFactory
					 * .fromResource(R.drawable.map_user_icon)));
					 */

					Drawable customIcon = getResources().getDrawable(
							R.drawable.map_user_icon);
					Bitmap bb = convertDrawableToBitmap(customIcon);

					Marker markerobj = fragment.googleMap
							.addMarker(new MarkerOptions()
									.position(ToLoc)
									.title("Location")
									.snippet(
											"Lat: " + ToLoc.latitude
													+ ", Long: "
													+ ToLoc.longitude)
									.icon(BitmapDescriptorFactory
											.fromBitmap(bb)));

					fragment.markersList.add(markerobj);

					fragment.zoomToAdjustAllMarkers(currentLatitude,
							currentLongitude);

					if (LoaderHelper.isLoaderShowing())
						LoaderHelper.hideLoader();
					// googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new
					// LatLng((currentLatLng.latitude/currentLatLng.longitude)*2,
					// (dest.latitude/dest.longitude)*2), zoomNormal));
					// if(isFromFindAride)
					isPathPlotted = true;
					setStaticMapMarker(currentLatitude, currentLongitude, 17);
					
					LatLngBounds.Builder builder = new LatLngBounds.Builder();
					builder.include(ToLoc);
					builder.include(FromLoc);
					LatLngBounds bounds = builder.build();
					fragment.googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));

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
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	public static Bitmap convertDrawableToBitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}

		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}

	private void setStaticMapMarker(Double currentLatitude,
			Double currentLongitude, int zoom) {
		// TODO Auto-generated method stub

		try {
			if (currentLatitude == null || currentLongitude == null)
				return;

			Applog.Debug("Static Latitude= " + currentLatitude);
			Applog.Debug("Static Longitude= " + currentLongitude);

			Log.e("TagLocation", " onSetMapMarkerPosition ");
			currentLatLng = null;
			currentLatLng = new LatLng(currentLatitude, currentLongitude);

			if (userLocationMarker != null) {
				userLocationMarker.remove();
				userLocationMarker = null;
			}

			/*
			 * googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			 * 
			 * @Override public boolean onMarkerClick(Marker marker) { boolean
			 * doNotMoveCameraToCenterMarker = true; public boolean
			 * onMarkerClick(Marker marker) { //Do whatever you need to do here
			 * .... return doNotMoveCameraToCenterMarker; } } });
			 */

			fragment.googleMap
					.setOnMarkerClickListener(new OnMarkerClickListener() {
						public boolean onMarkerClick(Marker marker) {

							marker.hideInfoWindow();

							return true;
						}
					});

			if (userLocationMarker == null) {
				try {
					/*
					 * userLocationMarker = fragment.googleMap .addMarker(new
					 * MarkerOptions() .position(currentLatLng) .snippet(
					 * "Lat: " + currentLatLng.latitude + ", Long: " +
					 * currentLatLng.longitude) .icon(BitmapDescriptorFactory
					 * .fromResource(R.drawable.map_icon_occupied)));
					 */
					Drawable customIcon = getResources().getDrawable(
							R.drawable.map_icon_occupied);
					Bitmap bb = convertDrawableToBitmap(customIcon);

					userLocationMarker = fragment.googleMap
							.addMarker(new MarkerOptions()
									.position(currentLatLng)
									.snippet(
											"Lat: " + currentLatLng.latitude
													+ ", Long: "
													+ currentLatLng.longitude)
									.icon(BitmapDescriptorFactory
											.fromBitmap(bb)));

				} catch (Exception e) {

					e.printStackTrace();
					userLocationMarker = fragment.googleMap
							.addMarker(new MarkerOptions()
									.position(currentLatLng)
									.snippet(
											"Lat: " + currentLatLng.latitude
													+ ", Long: "
													+ currentLatLng.longitude)
									.icon(BitmapDescriptorFactory
											.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
				}

				userLocationMarker.setDraggable(true);

				UiSettings settings = fragment.googleMap.getUiSettings();

				//settings.setMyLocationButtonEnabled(true);

				fragment.googleMap.animateCamera(CameraUpdateFactory
						.newLatLngZoom(currentLatLng, zoom));

				fragment.googleMap.setOnMarkerDragListener(this);
			}
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	private void UpdatePopUpData(String journeyNotificationStatus,
			String pickUpAddress, String userPickUpTime,
			String userOptionalMessage, String maxNoOfPassenger,
			String userNoOfBags, String userChildSeats, String userFirstName,
			String userLastName, String userImage, String userPhone) {
		// TODO Auto-generated method stub

		TextView handleStatus = (TextView) findViewById(R.id.handler_status);
		if (!journeyNotificationStatus.equals(""))
			handleStatus.setText(journeyNotificationStatus.toLowerCase()
					.equals("driver assigned") ? "Driver assigned" : "");
		else
			handleStatus.setText("pending...");

		TextView pickAddrs = (TextView) findViewById(R.id.pickText);
		if (!pickUpAddress.equals(""))
			pickAddrs.setText(pickUpAddress);

		TextView pickTime = (TextView) findViewById(R.id.pickTime);
		if (!userPickUpTime.equals(""))
			pickTime.setText(Utils.getFormattedDate(userPickUpTime));

		TextView opnMsg = (TextView) findViewById(R.id.opnMsg);
		if (!userOptionalMessage.equals(""))
			opnMsg.setText(userOptionalMessage);

		TextView passenger = (TextView) findViewById(R.id.passenger_value);
		if (!maxNoOfPassenger.equals(""))
			passenger.setText(maxNoOfPassenger);

		TextView bags = (TextView) findViewById(R.id.bags_value);
		if (!userNoOfBags.equals(""))
			bags.setText(userNoOfBags);

		TextView childSeat = (TextView) findViewById(R.id.childseats_value);
		if (!userChildSeats.equals(""))
			childSeat.setText(userChildSeats);

		CircularImageView userProfileImage = (CircularImageView) findViewById(R.id.circular_image_view);
		userProfileImage.setDrawingCacheEnabled(true);
		if (!userImage.equals(""))
			new DownloadImageAsync((ImageView) userProfileImage,
					JourneyDetailActivity.this).execute(userImage);

		TextView Customername = (TextView) findViewById(R.id.user_name);
		if (!userFirstName.equals("") && !userLastName.equals(""))
			Customername
					.setText(userFirstName.concat(" ").concat(userLastName));
		if(!Utils.isEmptyOrNull(userPhone))
			Customername.append("\n" + userPhone);

	}

	private void getDetailsOfJourney(String currentJId) {
		// TODO Auto-generated method stub

		WebServiceModel.getJourneyDetails(currentJId, this);

	}

	public void onException(CustomHttpException exception) {
		// TODO Auto-generated method stub

		if (LoaderHelper.progressDialog != null) {
			if (LoaderHelper.isLoaderShowing()) {
				LoaderHelper.hideLoader();
				AppToast.ShowToast(this, exception.getMessage());
			}

		}
	}

	public boolean isNullOrEmpty(String _field) {
		if (_field.equals(null) || _field.equals(""))
			return true;
		else
			return false;

	}

	public boolean isNull(String _field) {
		if (_field.equals(null))
			return true;
		else
			return false;

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if (journeyData == null)
			return;
		switch (v.getId()) {

		case R.id.show_popup_button:
			getBottomBarPopup();
			break;
		case R.id.cancelJourney:
			// CALL CANCEL SERVICE
			if (!isNullOrEmpty(journeyData.getUserId())
					&& !isNullOrEmpty(currentJourneyID)) {
				LoaderHelper.showLoader(JourneyDetailActivity.this,
						"Loading...", "");
				WebServiceModel.cancelJourney(JourneyDetailActivity.this,
						journeyData.getUserId(), currentJourneyID);// user_id

			}
			break;
		case R.id.beep:
			// CALL BEEP SERVICE
			if (!isNullOrEmpty(journeyData.getUserId())
					&& !isNull((journeyData.getUserOptionalMessage()))) {

				String driver_id = String.valueOf(preferencesHandler
						.getOriginalDriverUserID());
				WebServiceModel.sendBeep(JourneyDetailActivity.this,
						journeyData.getUserId(), driver_id,
						"Your driver has arrived."); // user_is_to
				// &
				// message
				LoaderHelper.showLoader(JourneyDetailActivity.this,
						"Loading...", "");
			}
			break;
		case R.id.endjourney:
			// CALL END JOURNEY
			try {
				// LoaderHelper.showLoader(this, "Please Wait...", "");
				if (fragment.googleMap != null)
					fragment.googleMap.clear();
				
				if (userLocationMarker != null) {
					userLocationMarker.remove();
					
				}

				if (DropOffTime == null)
					return;

				if (_latlng == null)
					return;

				// DropOffTime = Utils.getFormatedTimeString();
				// _latlng = new LatLng(currentLatitude, currentLongitude);
				// String DropOffAddress =
				// Utils.getAddressString(currentLatitude,
				// currentLongitude, getBaseContext());
				/*
				 * DropOffAddress = LocationManager.getInstance(this)
				 * .getLocationAddress(_latlng);
				 */
				if (DropOffAddress == null)
					DropOffAddress = "";
				Intent myIntent = new Intent(this, EndJourneyActivity.class);
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
					/*
					 * if(LoaderHelper.isLoaderShowing())
					 * LoaderHelper.hideLoader();
					 */
					// /unregisterReceiver(mHandleMessageReceiver);
					startActivity(myIntent);
				}
			} catch (Exception e) {
				// TODO: handle exception
				// AppToast.ShowToast(this, e.getMessage());
			}
			break;

		default:
			break;
		}

	}

	@Override
	public void onGesture(GestureOverlayView overlay, MotionEvent event) {
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

	/*
	 * class JourneyCancellBroadcastReciever extends BroadcastReceiver {
	 * 
	 * @Override public void onReceive(Context context, Intent intent) {
	 * 
	 * // AppToast.ShowToast(getActivity(), "onReceive() called"); if (googleMap
	 * == null) { return; }
	 * 
	 * try { // check just if (!isPathplotted) new
	 * UpdateMapLocation().execute("");
	 * 
	 * } catch (Exception e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); }
	 * 
	 * }
	 * 
	 * }
	 */

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			try {
				String journeyId = intent.getExtras().getString(
						com.smarttaxi.driver.gcm.CommonUtilities.JOURNEY_ID);
				String status = intent.getExtras().getString(
						com.smarttaxi.driver.gcm.CommonUtilities.NOTI_STATUS);

				if (journeyId != null && status.equals("2")) {
					/*
					 * AlertBoxHelper.showAlertBox( (Activity)
					 * getApplicationContext(), "Alert",
					 * "Journey cancelled by the customer.");
					 */

					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							JourneyDetailActivity.this);

					// set title
					alertDialogBuilder.setTitle("Alert");

					// set dialog message
					alertDialogBuilder
							.setMessage(
									"This journey has been cancelled by the customer.")
							.setCancelable(false)
							.setPositiveButton("Ok",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											// if this button is clicked, close
											// current activity

											Driver driver = new Driver(
													JourneyDetailActivity.this);

											driver.enablePobStatus("no");
											driver.resetTaxiStatus("yes");
											com.smarttaxi.driver.BAL.Journey jrny = new com.smarttaxi.driver.BAL.Journey(
													JourneyDetailActivity.this);
											fragment.enableMarkerUpdation();
											jrny.removeSavedJourney();
											journeyData = null;
											// MainActivity.mFragmentStack.clear();

											Intent i = new Intent(
													getBaseContext(),
													MainActivity.class);
											i.putExtra("IsRegistered", true);
											// i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
											// i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
											// i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
											// i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
											MainActivity.isRequestShown = false;

											startActivity(i);
											finish();

											return;
										}
									});

					// create alert dialog
					alertDialogBuilder.create();

					// show it
					alertDialogBuilder.show();

				} else if (status.equals("1")) {
					GCMIntentService
							.cancelNotification(getApplicationContext());

				}
				// AppToast.ShowToast(context, journeyId + "cancelled");
				/*
				 * registrationId = intent.getExtras().getString(
				 * CommonUtilities.REGID);
				 * 
				 * if (registrationId != null && IsRegistered == false) { //
				 * AppToast.ShowToast(getBaseContext(), reg);
				 * preferencesHandler.setDriverUdid(registrationId);
				 * PostRegistrationIdtoServer(registrationId, driverUserId);
				 * 
				 * } if (journeyId != null && status.equals("1")) {
				 * 
				 * GetPickupRequest(journeyId); //
				 * AppToast.ShowToast(getBaseContext(), newMessage); } if
				 * (journeyId != null && status.equals("2")) {
				 * AlertBoxHelper.showAlertBox
				 * ((Activity)getApplicationContext(), "Alert",
				 * "Journey cancelled by the customer."); }
				 * 
				 * // Waking up mobile if it is sleeping
				 * WakeLocker.acquire(getApplicationContext());
				 */
				// PostRegistrationIdtoServer(RegId, "driver@test.com");
				/**
				 * Take appropriate action on this message depending upon your
				 * app requirement For now i am just displaying it on the screen
				 * */

				// Showing received message
				// lblMessage.append(newMessage + "\n");
				// AppToast.ShowToast(getApplicationContext(), "New Message");

				// Releasing wake lock
				WakeLocker.release();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	};
	
	class MapLocationChangedReciever extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			// AppToast.ShowToast(getActivity(), "onReceive() called");
		

			try {
			
				if(isPathPlotted)
					new UpdateMapLocation().execute("");
					

				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private class UpdateMapLocation extends AsyncTask<String, Void, String> {

		Location fusedlocation;
		LatLng userLatLng;

		@Override
		protected String doInBackground(String... params) {

			try {
				
				
				
			 fusedlocation = LocationManager.getInstance(JourneyDetailActivity.this)
						.getCurrentLocation();

			

				
			}

			catch (LocationNotFoundException e) {
				e.printStackTrace();
			}

			return "Executed";
		}

		@Override
		protected void onPostExecute(String result) {

			try {
				
				/*if (fragment.googleMap != null)
					fragment.googleMap.clear();*/
				setStaticMapMarker(fusedlocation.getLatitude(),fusedlocation.getLongitude(),17);
				
				

				
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

	/*@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		AppToast.ShowToast(this, "changed");
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
		
	}*/
	

}
