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
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGestureListener;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
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
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.google.android.gms.internal.ex;
//import com.google.android.gms.internal.fr;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.smarttaxi.driver.R;
import com.smarttaxi.driver.BAL.Driver;
import com.smarttaxi.driver.adapters.LeftMenuAdapter;
import com.smarttaxi.driver.constants.APIConstants;
import com.smarttaxi.driver.custom.design.CircularImageView;
import com.smarttaxi.driver.custom.design.CustomDialog;
import com.smarttaxi.driver.custom.design.TransparentPanel;
import com.smarttaxi.driver.entities.CustomHttpException;
import com.smarttaxi.driver.entities.CustomHttpResponse;
import com.smarttaxi.driver.entities.Journey;
import com.smarttaxi.driver.fragments.FindARideFragment;
import com.smarttaxi.driver.gcm.GCMIntentService;
import com.smarttaxi.driver.gcm.WakeLocker;
import com.smarttaxi.driver.helpers.AlertBoxHelper;
import com.smarttaxi.driver.helpers.DownloadImageAsync;
import com.smarttaxi.driver.helpers.LoaderHelper;
import com.smarttaxi.driver.helpers.LocationManager;
import com.smarttaxi.driver.interfaces.HttpResponseListener;
import com.smarttaxi.driver.model.WebServiceModel;
import com.smarttaxi.driver.utils.AppToast;
import com.smarttaxi.driver.utils.Applog;
import com.smarttaxi.driver.utils.NetworkAvailability;
import com.smarttaxi.driver.utils.Utils;

public class EndJourneyActivity extends ActionBarActivity implements
		OnGestureListener, OnClickListener, HttpResponseListener,
		OnMarkerDragListener {

	private float lastX;
	private static Animation animShow, animHide;

	DrawerLayout drawerLayout;
	ListView listViewDrawer;
	RelativeLayout layoutDrawer;
	ActionBarDrawerToggle actionBarDrawerToggle;
	LeftMenuAdapter leftMenuAdapter;
	boolean isEnterfareClicked = false;
	Button cancelJourney;
	Button beep;
	Button endJourney;
	public AlertDialog statusUpdatedAlert;
	public Button popupBottomBarButton;
	com.smarttaxi.driver.custom.design.TransparentPanel popupEndjourney;
	TextView companyname;
	Button button;
	private Marker userLocationMarker;
	FrameLayout mainPopup;
	FrameLayout frameLayoutContent;
	FindARideFragment fragment;

	TextView mDisplay;
	private String pickUpTime, pickUpAddress, dropOffAddress, dropOffTime,
			currentjourneyId, driverName, companyName;
	double dropOfflng;
	double dropOfflat;
	private static double currentLat, currentLng;
	private String passcode;
	private String tip;
	private CheckBox tax;
	private TextView tiptxt;
	private static Double pick_lat;
	private static Double pick_lng;
	LatLng currentLatLng;

	@SuppressLint("ShowToast")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		Bundle b = getIntent().getBundleExtra("end_journey_data");

		if (b != null) {

			try {
				pickUpTime = b.getString("pickUptime");
				pickUpAddress = b.getString("pickUp_door_address");
				dropOffAddress = b.getString("dropOff_door_address");
				dropOffTime = b.getString("dropOfftime");
				dropOfflat = b.getDouble("dropOffLat");
				dropOfflng = b.getDouble("dropOffLng");
				currentjourneyId = b.getString("journey_id");
				tip = b.getString("tip");
				driverName = b.getString("driver_name");
				passcode = b.getString("password");
				currentLat = dropOfflat;
				currentLng = dropOfflng;
				companyName = b.getString("company_name");
				pick_lat = Double.parseDouble(b.getString("pickup_lat"));
				pick_lng = Double.parseDouble(b.getString("pickup_lng"));
				pickUpTime = Utils.getFormattedDate(pickUpTime);

			} catch (Exception e) {
				e.printStackTrace();
			}
			// PlotPath();

		}

		try {

			initUI();

//			if (NetworkAvailability.IsNetworkAvailable(this.getBaseContext())) {
//
//				NetworkAvailability.CheckBroadcastEnableDisable(
//						this.getApplicationContext(), "EnablingBroadcast");
//				NetworkAvailability.IsGpsEnabled(this.getApplicationContext());
//			}

			fragment = new FindARideFragment();
			fragment.disableMarkerUpdation();
			FragmentManager fragmentManager = getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();
			fragmentTransaction.replace(R.id.frameLayoutContent, fragment);
			fragmentTransaction.commit();

			popupBottomBarButton.setOnClickListener(this);

			PlotPath(pick_lat, pick_lng);

		} catch (Exception e) {

			Applog.Error("com.smarttaxi.driver.activities : OnCreate() : Message= "
					+ e.getMessage());
			// TODO: handle exception
		}
	}

	@Override
	public void onPause() {

		NetworkAvailability.CheckBroadcastEnableDisable(
				this.getApplicationContext(), "DisablingBroadcast");
		GCMIntentService.cancelNotification(getApplicationContext());
		super.onPause();
		fragment.mapView.onPause();
	}

	@Override
	protected void onStop() {

		// AppToast.ShowToast(this, "onStop");
		super.onStop();

	}
	
	
	@Override
	protected void onRestart() {

		/*
		 * NetworkAvailability.CheckBroadcastEnableDisable(this.
		 * getApplicationContext(), "EnablingBroadcast");
		 * NetworkAvailability.IsGpsEnabled(this);
		 */
		
		
		if (pick_lat != null && pick_lng != null)
			PlotPath(pick_lat, pick_lng);
		super.onRestart();

	}

	private void PlotPath(Double _pickLat, Double _pickLong) {
		// TODO Auto-generated method stub

		try {
			if (fragment.googleMap != null)
				fragment.googleMap.clear();

			if (userLocationMarker != null)
				userLocationMarker.remove();

			// setStaticMapMarker(currentLat, currentLng, 17);
			final LatLng ToLoc, FromLoc;
			if ((!_pickLat.equals(null) && !_pickLat.equals(""))
					&& (!_pickLong.equals(null) && !_pickLong.equals(""))) {

				ToLoc = new LatLng(_pickLat, _pickLong);

			} else
				ToLoc = new LatLng(Double.parseDouble("25.407384"),
						Double.parseDouble("68.355635"));

			FromLoc = new LatLng(currentLat, currentLng);

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
					/*Marker markerobj = fragment.googleMap.addMarker(new MarkerOptions()
							.position(ToLoc)
							.title("Location")
							.snippet(
									"Lat: " + ToLoc.latitude + ", Long: "
											+ ToLoc.longitude)
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.map_user_icon)));*/
					
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

					fragment.zoomToAdjustAllMarkers(currentLat, currentLng);

					if (LoaderHelper.isLoaderShowing())
						LoaderHelper.hideLoader();
					// googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new
					// LatLng((currentLatLng.latitude/currentLatLng.longitude)*2,
					// (dest.latitude/dest.longitude)*2), zoomNormal));
					setStaticMapMarker(currentLat, currentLng, 17);
					
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
			setUI();

		} catch (Exception e) {

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

			if (userLocationMarker != null)
				userLocationMarker.remove();

			/*
			 * googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			 * 
			 * @Override public boolean onMarkerClick(Marker marker) { boolean
			 * doNotMoveCameraToCenterMarker = true; public boolean
			 * onMarkerClick(Marker marker) { //Do whatever you need to do here
			 * .... return doNotMoveCameraToCenterMarker; } } });
			 */

			/*
			 * fragment.googleMap .setOnMarkerClickListener(new
			 * OnMarkerClickListener() { public boolean onMarkerClick(Marker
			 * marker) {
			 * 
			 * marker.hideInfoWindow();
			 * 
			 * return true; } });
			 */

			if (userLocationMarker == null) {
				try {
					/*userLocationMarker = fragment.googleMap
							.addMarker(new MarkerOptions()
									.position(currentLatLng)
									.snippet(
											"Lat: " + currentLatLng.latitude
													+ ", Long: "
													+ currentLatLng.longitude)
									.icon(BitmapDescriptorFactory
											.fromResource(R.drawable.map_icon_occupied)));*/
					
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

				settings.setMyLocationButtonEnabled(true);

				/*fragment.googleMap.animateCamera(CameraUpdateFactory
						.newLatLngZoom(currentLatLng, zoom));*/

				fragment.googleMap.setOnMarkerDragListener(this);
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	private void setUI() {
		// TODO Auto-generated method stub

		tiptxt.setText(tip + "%");

		TextView tvDriverName = (TextView) findViewById(R.id.name);
		if (!driverName.equals("") && driverName != null)
			tvDriverName.setText(driverName);

		TextView tvCompanyName = (TextView) findViewById(R.id.company);
		if (!companyName.equals("") && companyName != null)
			tvCompanyName.setText(companyName);

		TextView pickTime = (TextView) findViewById(R.id.pick_time);
		if (!pickUpTime.equals("") || pickUpTime != null)
			pickTime.setText(pickUpTime);

		TextView pickAddrs = (TextView) findViewById(R.id.pick_loc);
		if (pickUpAddress != null)
			pickAddrs.setText(pickUpAddress);

		TextView dropTime = (TextView) findViewById(R.id.dep_time);
		if (!dropOffTime.equals(""))
			dropTime.setText(dropOffTime);

		TextView dropAddrs = (TextView) findViewById(R.id.dep_loc);
		if (!dropOffAddress.equals(""))
			dropAddrs.setText(dropOffAddress);

	}

	private void getBottomBarPopup() {

		// Start out with the popup initially hidden.
		if (popupEndjourney.isShown())
			popupEndjourney.setVisibility(View.GONE);

		animShow = AnimationUtils.loadAnimation(this, R.anim.popup_show);
		animHide = AnimationUtils.loadAnimation(this, R.anim.popup_hide);

		popupBottomBarButton = (Button) findViewById(R.id.end_show_popup_button);
		// final Button hideButton = (Button)
		// findViewById(R.id.hide_popup_button);

		popupBottomBarButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				if (!popupEndjourney.isShown()) {
					popupEndjourney.setVisibility(View.VISIBLE);
					popupEndjourney.startAnimation(animShow);
					popupBottomBarButton.setEnabled(true);
					// hideButton.setEnabled(true);

				} else {

					popupEndjourney.startAnimation(animHide);
					popupBottomBarButton.setEnabled(true);
					// hideButton.setEnabled(false);
					popupEndjourney.setVisibility(View.GONE);

				}

			}
		});

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
	protected void onResume() {

		NetworkAvailability.CheckBroadcastEnableDisable(
				this.getApplicationContext(), "EnablingBroadcast");
		NetworkAvailability.IsNetworkAvailable(this.getBaseContext());
		NetworkAvailability.IsGpsEnabled(this.getBaseContext());
		
		
		super.onResume();
		fragment.mapView.onResume();
		/*
		 * if(!isEnterfareClicked) { if(pick_lat!=null && pick_lng !=null)
		 * PlotPath(pick_lat, pick_lng); }
		 */
		/*
		 * if(mHandleMessageReceiver==null)
		 * registerReceiver(mHandleMessageReceiver, new IntentFilter(
		 * DISPLAY_MESSAGE_ACTION));
		 */

	}

	@Override
	public void onBackPressed() {

		// fragment.enableMarkerUpdation();
		super.onBackPressed();
		// finish();
	}

	private void initUI() {

		try {
			setContentView(R.layout.activity_end_journey);

			popupBottomBarButton = (Button) findViewById(R.id.end_show_popup_button);
			popupEndjourney = (TransparentPanel) findViewById(R.id.popup_window_end);
			companyname = (TextView) findViewById(R.id.company);
			if (!Utils.isEmptyOrNull(companyName))
				companyname.setText(companyName);
			else
				companyname.setText("");
			// drawerLayout = (DrawerLayout)
			// findViewById(R.id.drawerLayoutMain);

			// layoutDrawer = (RelativeLayout) findViewById(R.id.layoutDrawer);
			// listViewDrawer = (ListView) findViewById(R.id.listViewDrawer);

			tax = (CheckBox) findViewById(R.id.tax_value_end);
			tiptxt = (TextView) findViewById(R.id.tip_value);

			findViewById(R.id.btn_confirm_end_journey).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							String fare = ((EditText) findViewById(R.id.journeyfare))
									.getText().toString();
							// String tipAmount = "";
							if (Double.parseDouble(Utils.validateEmptyString(
									fare, "0")) > 0) {
								String tempTip = tip;
								// if (!tip.equals(null) && !fare.equals(null))
								// {
								// tipAmount =
								// String.valueOf((Double.parseDouble(fare) *
								// (Double
								// .parseDouble(tip))) / 100);
								// }

								// if (tax.isChecked()) {
								// fare =
								// String.valueOf(Double.parseDouble(fare) +
								// 15);
								// }
								// isEnterfareClicked=true;
								/*CustomDialog.showTripCompletedDialog(
										EndJourneyActivity.this, tempTip, fare,
										tax.isChecked(), currentLat,
										currentLng, currentjourneyId,
										dropOffTime, dropOffAddress, passcode);*/
								Log.e("Tip text", tiptxt.getText().toString());
								String tip = tiptxt.getText().toString();
								if(tip.indexOf("%") >= 0)
								{
									tip = tip.substring(0, tip.indexOf("%"));
								}
								double tipAmount = Double.valueOf(tip);
								if(tip.length() > 0)
								{
									Log.e("Tip in double", String.valueOf(tipAmount));
									if(tipAmount > 0)
									{
										tipAmount /= 100;
										Log.e("Tip in %age", String.valueOf(tipAmount));
										tipAmount = Double.valueOf(fare) * tipAmount;
										Log.e("Final tip amount", String.valueOf(tipAmount));
									}
								}
								LoaderHelper.showLoader(EndJourneyActivity.this, "Finalizing trip...", "");
								WebServiceModel.endJourney(
										(HttpResponseListener) EndJourneyActivity.this,
										currentjourneyId, String.valueOf(currentLat),
										String.valueOf(currentLng), fare, dropOffTime,
										dropOffAddress, tax.isChecked(),
										String.valueOf(tipAmount));
							} else
								AlertBoxHelper.showAlertBox(
										EndJourneyActivity.this, "Alert",
										"Please enter valid fare");
						}
					});

			/*
			 * leftMenuAdapter = new LeftMenuAdapter(this);
			 * listViewDrawer.setAdapter(leftMenuAdapter); actionBarDrawerToggle
			 * = new ActionBarDrawerToggle(this, drawerLayout,
			 * R.drawable.ic_drawer, R.string.drawer_open,
			 * R.string.drawer_close) {
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
			 * getSupportActionBar().setTitle("");
			 * supportInvalidateOptionsMenu(); }
			 * 
			 * }; drawerLayout.setDrawerListener(actionBarDrawerToggle);
			 */
			// actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setIcon(R.drawable.btn_menu_normal);
			// getSupportActionBar().setHomeButtonEnabled(true);

			registerReceiver(mHandleMessageReceiver, new IntentFilter(
					DISPLAY_MESSAGE_ACTION));

		} catch (Exception e) {

			// AppToast.ShowToast(this, "");
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/*
		 * switch (item.getItemId()) { // Respond to the action bar's Up/Home
		 * button case android.R.id.home: NavUtils.navigateUpFromSameTask(this);
		 * return true; } return super.onOptionsItemSelected(item);
		 */
		// fragment.enableMarkerUpdation();
		super.onBackPressed();
		return true;
	}

	public void onResponse(CustomHttpResponse object) {

		try {
			// TODO Auto-generated method stub
			if (LoaderHelper.progressDialog != null)
				if (LoaderHelper.isLoaderShowing())
					LoaderHelper.hideLoader();
			if (object.getStatusCode() == APIConstants.SUCESS_CODE) {

				if (object.getMethodName().equals(
						APIConstants.METHOD_UPDATE_JOURNEY_STATUS_COMPLETE)) {
					try {
						// Journey journeyData = (Journey) object.getResponse();
						if (LoaderHelper.progressDialog != null) {
							if (LoaderHelper.isLoaderShowing())
								LoaderHelper.hideLoader();

							/*
							 * AlertBoxHelper.showAlertBox(EndJourneyActivity.this
							 * , "", object.getResponseMsg());
							 */

							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
									EndJourneyActivity.this);

							// set title
							alertDialogBuilder.setTitle("");

							// set dialog message
							alertDialogBuilder
									.setMessage(object.getResponseMsg())
									.setCancelable(false)
									.setPositiveButton(
											"Ok",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int id) {

													Driver driver = new Driver(
															EndJourneyActivity.this);
													driver.enablePobStatus("no");
													driver.resetTaxiStatus("yes");
													com.smarttaxi.driver.BAL.Journey _journey = new com.smarttaxi.driver.BAL.Journey(
															EndJourneyActivity.this);
													_journey.removeSavedJourney();

													Intent i = new Intent(
															getBaseContext(),
															MainActivity.class);
													i.putExtra("IsRegistered",
															true);

													/*
													 * i.addFlags(Intent.
													 * FLAG_ACTIVITY_NEW_TASK);
													 * i.addFlags (Intent.
													 * FLAG_ACTIVITY_SINGLE_TOP
													 * ); i.addFlags(Intent.
													 * FLAG_ACTIVITY_CLEAR_TOP);
													 */
													fragment.enableMarkerUpdation();
													MainActivity.isRequestShown = false;

													startActivity(i);
													finish();
													statusUpdatedAlert
															.dismiss();
												}
											});

							// create alert dialog
							statusUpdatedAlert = alertDialogBuilder.create();

							// show it
							statusUpdatedAlert.show();
						}

					}

					catch (Exception e) {

						if (LoaderHelper.isLoaderShowing())
							LoaderHelper.hideLoader();
					}
				}

			} else
				AppToast.ShowToast(this, object.getResponseMsg());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onException(CustomHttpException exception) {
		// TODO Auto-generated method stub

		if (LoaderHelper.progressDialog != null) {
			if (LoaderHelper.isLoaderShowing()) {
				LoaderHelper.hideLoader();
				AppToast.ShowToast(this, exception.getMessage());
			}

		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.end_show_popup_button:
			getBottomBarPopup();
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
							EndJourneyActivity.this);

					// set title
					alertDialogBuilder.setTitle("Journey Cancelled");

					// set dialog message
					alertDialogBuilder
							.setMessage("Journey cancelled by the customer.")
							.setCancelable(false)
							.setPositiveButton("Ok",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											// if this button is clicked, close
											// current activity

											Driver driver = new Driver(
													EndJourneyActivity.this);

											driver.enablePobStatus("no");
											driver.resetTaxiStatus("yes");
											com.smarttaxi.driver.BAL.Journey jrny = new com.smarttaxi.driver.BAL.Journey(
													EndJourneyActivity.this);
											fragment.enableMarkerUpdation();
											jrny.removeSavedJourney();

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

				// Releasing wake lock
				WakeLocker.release();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	};

}
