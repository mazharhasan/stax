package com.smarttaxi.driver.activities;

import static com.smarttaxi.driver.gcm.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.smarttaxi.driver.gcm.CommonUtilities.EXTRA_MESSAGE;
import static com.smarttaxi.driver.gcm.CommonUtilities.SENDER_ID;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGestureListener;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.internal.bu;
import com.google.android.gms.maps.MapView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.smarttaxi.driver.R;
import com.smarttaxi.driver.BAL.Driver;
import com.smarttaxi.driver.adapters.LeftMenuAdapter;
import com.smarttaxi.driver.adapters.MyFragmentAdapter;
import com.smarttaxi.driver.constants.APIConstants;
import com.smarttaxi.driver.custom.design.CircularImageView;
import com.smarttaxi.driver.custom.design.CustomDialog;
import com.smarttaxi.driver.custom.design.RequestDialogFragment;
import com.smarttaxi.driver.entities.AcceptOrRejectJourney;
import com.smarttaxi.driver.entities.CustomHttpException;
import com.smarttaxi.driver.entities.CustomHttpResponse;
import com.smarttaxi.driver.entities.DeviceRegister;
import com.smarttaxi.driver.entities.Journey;
import com.smarttaxi.driver.entities.LogoutRequest;
import com.smarttaxi.driver.entities.Pick;
import com.smarttaxi.driver.entities.PickupRequest;
import com.smarttaxi.driver.entities.PickupRequestCollection;
import com.smarttaxi.driver.entities.User;
import com.smarttaxi.driver.fragments.CurrentTripFragment;
import com.smarttaxi.driver.fragments.EarningFragment;
import com.smarttaxi.driver.fragments.FindARideFragment;
import com.smarttaxi.driver.fragments.HistoryTripFragment;
import com.smarttaxi.driver.fragments.JourneyDetailFragment;
import com.smarttaxi.driver.fragments.ProfileDisplayDialog;
import com.smarttaxi.driver.fragments.UserProfileFragment;
import com.smarttaxi.driver.gcm.CommonUtilities;
import com.smarttaxi.driver.gcm.ConnectionDetector;
import com.smarttaxi.driver.gcm.GCMIntentService;
import com.smarttaxi.driver.gcm.ServerUtilities;
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
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

public class MainActivity extends ActionBarActivity implements
		OnGestureListener, HttpResponseListener, OnItemClickListener {

	private final String TAG = MainActivity.this.getClass().getSimpleName();

	/* FRAGMENT TAGS */
	private static final String TAG_FIND_A_RIDE_FRAGMENT = "initialfindaRideFragment";
	private static final String TAG_CURRENT_TRIP_FRAGMENT = "currentTripFragment";
	private static final String TAG_TRIP_HISTORY_FRAGMENT = "tripHistory";
	private static final String TAG_DRIVER_EARNING_FRAGMENT = "driverEarningFragment";
	private static final String TAG_USER_PROFILE_FRAGMENT = "userProfileFragment";
	public static final String TAG_REQUEST_DIALOG = "passenger_request_dialog";

	private static final int TAG_ACTIVE_FRAGMENT_FIND_A_RIDE = 0;
	private static final int TAG_INACTIVE_FRAGMENT_CURRENT_TRIP = 1;
	private static final int TAG_INACTIVE_FRAGMENT_TRIP_HISTORY = 2;
	private static final int TAG_INACTIVE_FRAGMENT_DRIVER_EARNING = 3;
	private static final int TAG_INACTIVE_FRAGMENT_USER_PROFILE = 4;
	private static int TAG_ACTIVE_FRAGMENT;

	/* FRAGMENT VARIABLES */
	private FindARideFragment findArideFragment;
	private CurrentTripFragment curentTripFragment;
	private HistoryTripFragment tripHistory;
	private EarningFragment earningFragment;
	private UserProfileFragment userProfileFragment;
	private JourneyDetailFragment journeyDetailFragment;

	/* NAVIGATION DRAWER */
	public DrawerLayout drawerLayout;
	public ListView listViewDrawer;
	public RelativeLayout layoutDrawer;
	public ActionBarDrawerToggle actionBarDrawerToggle;
	public LeftMenuAdapter leftMenuAdapter;

	/* DIALOG DECLARATION */
	private static JourneyRequestDialog pagerDialog;
	public static Dialog dialog;

	/* VIEWS DECLARATIONS */
	public static TextView pickupAddress;
	public static TextView pickupTime;
	public static TextView remaingTime;
	public static TextView additionalMsg;
	public static TextView passengerName, coperateName;
	public static CircularImageView passengerImage;
	public static Button acceptRequestButton;
	public static Button rejectRequestButton;
	public static CountDownTimer cdt;
	public static String timer;
	public TextView mDisplay;
	public static Fragment FragmentHolder;
	public Fragment FragmentInUI;
	public FrameLayout mainPopup;
	public Button button;
	public FrameLayout frameLayoutContent;

	/* CLASS VARIABLES */
	public static final String JOURNEY_ID = "journey_id";
	public static boolean isDialogStucked = false;
	public static boolean isRequestShown = false;
	public static boolean isJourneyAccepted = false;
	public static ArrayList<String> pickupCollection;
	public static String registrationId;
	public static Stack<Fragment> mFragmentStack = new Stack<Fragment>();
	public static boolean isLoggedin;
	private PreferencesHandler preferencesHandler;
	public AsyncTask<Void, Void, Void> mRegisterTask;
	public FragmentManager fragmentManager;
	public int mStackLevel;
	private float lastX;
	public Boolean IsRegistered = false;
	private ViewFlipper vf;
	// private boolean isAppExitAllow = true;
	public String driverUserId;
	public String currentJourneyId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {

			// if you don't want to register Devive in every instance
			/*
			 * Bundle bundle = getIntent().getExtras();
			 * 
			 * if (bundle != null) {
			 * 
			 * if(bundle.containsKey("IsRegistered")) IsRegistered =true; }
			 */

			if (!deviceHasGoogleAccount()) {
				addGoogleAccountAlert();

			} else {
				preferencesHandler = new PreferencesHandler(this);
				if (preferencesHandler.getOriginalDriverUserID() > 0) {
					driverUserId = String.valueOf(preferencesHandler
							.getOriginalDriverUserID());
					// isLoggedin =true;
				}
				initGCM();
				initUI();

				if (NetworkAvailability.IsNetworkAvailable(this
						.getBaseContext())) {

//					NetworkAvailability.CheckBroadcastEnableDisable(
//							this.getApplicationContext(), "EnablingBroadcast");
//					NetworkAvailability.IsGpsEnabled(this.getApplicationContext());

					// Getting Google Play availability status
					int status = GooglePlayServicesUtil
							.isGooglePlayServicesAvailable(getBaseContext());

					if (ConnectionResult.SUCCESS == status) {
						LocationManager.getInstance(this)
								.startLocationService();
					} else {
						int requestCode = 10;
						Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
								status, this, requestCode);
						dialog.show();

					}

					FragmentManager fm = getSupportFragmentManager();

					findArideFragment = (FindARideFragment) fm
							.findFragmentByTag(TAG_FIND_A_RIDE_FRAGMENT);

					if (findArideFragment == null)
						findArideFragment = new FindARideFragment();
					fm.beginTransaction()
							.add(R.id.frameLayoutContent, findArideFragment,
									TAG_FIND_A_RIDE_FRAGMENT).commit();
					TAG_ACTIVE_FRAGMENT = TAG_ACTIVE_FRAGMENT_FIND_A_RIDE;
					// FragmentHolder = findArideFragment;

				}
			}

		} catch (Exception e) {

			Applog.Error(TAG + "ON EXCEPTION  onCreate()");
			e.printStackTrace();
			// TODO: handle exception
		}

	}

	/*
	 * @Override protected void onUserLeaveHint() { // TODO Auto-generated
	 * method stub super.onUserLeaveHint();
	 * 
	 * if(isAppExitAllow) exitApp(); }
	 */

	private void exitApp() {
		// stopService(new Intent(MainActivity.this, ServiceLocation.class));
		try {
			if (!LocationManager.isBackgroundMode)
				LocationManager.getInstance(this).stopLocationService();
			NetworkAvailability.CheckBroadcastEnableDisable(this,
					"DisablingBroadcast");

			Intent startMain = new Intent(Intent.ACTION_MAIN);
			startMain.addCategory(Intent.CATEGORY_HOME);
			startMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// this.finish();

			startActivity(startMain);
			System.gc();
			/* finish(); */
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void addGoogleAccountAlert() {

		try {
			AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
			helpBuilder.setCancelable(false);
			helpBuilder.setTitle("Add Gmail account");
			helpBuilder
					.setMessage("These options rely on a Gmail account, but you don't seem to have one configured. Would you like to configure one now?");

			helpBuilder.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						// @Override
						public void onClick(DialogInterface dialog, int which) {
							// try/ catch block was here

							AccountManager acm = AccountManager
									.get(getApplicationContext());
							acm.addAccount("com.google", null, null, null,
									MainActivity.this, null, null);

							exitApp();
						}
					});

			helpBuilder.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// close the dialog, return to activity
							exitApp();
						}
					});

			AlertDialog helpDialog = helpBuilder.create();
			helpDialog.show();

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	private void initGCM() {
		// TODO Auto-generated method stub
		try {
			// Getting name, email from intent
			Intent i = getIntent();

			// Make sure the device has the proper dependencies.
			GCMRegistrar.checkDevice(this);

			// Make sure the manifest was properly set - comment out this line
			// while developing the app, then uncomment it when it's ready.
			GCMRegistrar.checkManifest(this);

			registerReceiver(mHandleMessageReceiver, new IntentFilter(
					DISPLAY_MESSAGE_ACTION));

			// Get GCM registration id
			final String regId = GCMRegistrar.getRegistrationId(this);

			// Check if regid already presents
			if (regId.equals("")) {
				// Registration is not present, register now with GCM
				GCMRegistrar.register(this, SENDER_ID);
			} else {
				// Device is already registered on GCM
				if (GCMRegistrar.isRegisteredOnServer(this)) {
					// Skips registration.
					/*
					 * Toast.makeText(getApplicationContext(),
					 * "Already registered with GCM", Toast.LENGTH_LONG)
					 * .show();
					 */
					// if(!isLoggedin)
					//if (registrationId != null && !IsRegistered)
						PostRegistrationIdtoServer(regId, driverUserId);
				} else {
					// Try to register again, but not in the UI thread.
					// It's also necessary to cancel the thread onDestroy(),
					// hence the use of AsyncTask instead of a raw thread.
					final Context context = this;
					mRegisterTask = new AsyncTask<Void, Void, Void>() {

						@Override
						protected Void doInBackground(Void... params) {
							// Register on our server
							// On server creates a new user
							ServerUtilities.register(context, "", "", regId);
							return null;
						}

						@Override
						protected void onPostExecute(Void result) {
							mRegisterTask = null;
						}

					};
					mRegisterTask.execute(null, null, null);
				}
			}
		} catch (Exception e) {
			Applog.Error(TAG + "ON EXCEPTION  initGCM()");
			e.printStackTrace();

		}

	}

	private boolean deviceHasGoogleAccount() {

		AccountManager accMan = AccountManager.get(this);
		Account[] accArray = accMan.getAccountsByType("com.google");
		return accArray.length >= 1 ? true : false;

	}

	/*
	 * @Override public void onBackPressed() { // TODO Auto-generated method
	 * stub
	 * 
	 * if (!mFragmentStack.isEmpty()) { mFragmentStack.pop(); if
	 * (mFragmentStack.size() >0) { Fragment fragment =
	 * MainActivity.mFragmentStack.peek(); if (fragment != null) {
	 * FragmentManager fragmentManager = getSupportFragmentManager();
	 * FragmentTransaction fragmentTransaction = fragmentManager
	 * .beginTransaction(); fragmentTransaction.replace(R.id.frameLayoutContent,
	 * fragment).commit(); FragmentHolder = fragment; FragmentInUI = fragment; }
	 * if (mFragmentStack.size() > 1) FragmentHolder = null;
	 * 
	 * } else { exitApp();
	 * 
	 * super.onBackPressed(); }
	 * 
	 * } else { exitApp();
	 * 
	 * super.onBackPressed(); }
	 * 
	 * }
	 */

	@Override
	public void onBackPressed() {

		FragmentManager fragmentManager = getSupportFragmentManager();
		if (fragmentManager.getBackStackEntryCount() == 0) {
			Applog.Error("EXIT APP");
			exitApp();
		} else {

			Applog.Error("BACK PRESSED");

			try {
				super.onBackPressed();
				Fragment fragment = getPreviousFragment();

				if (fragment != null) {
					if (fragment instanceof FindARideFragment) {
						getSupportActionBar().setTitle("My Location");

					}

					if (fragment instanceof CurrentTripFragment) {
						getSupportActionBar().setTitle("Current Trip");
						// return;
					}

					if (fragment instanceof HistoryTripFragment) {
						getSupportActionBar().setTitle("Trip History");
						// return;
					}

					if (fragment instanceof EarningFragment) {
						getSupportActionBar().setTitle("Driver Earning");
						// return;
					}

					if (fragment instanceof UserProfileFragment) {
						getSupportActionBar().setTitle("User Profile");
						// return;
					}
				}

			} catch (Throwable t) {
				exitApp();
				t.printStackTrace();
			}

		}
	}

	/**
	 * Receiving push messages
	 * */
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			try {
				String journeyId = intent.getExtras().getString(
						com.smarttaxi.driver.gcm.CommonUtilities.JOURNEY_ID);
				String status = intent.getExtras().getString(
						com.smarttaxi.driver.gcm.CommonUtilities.NOTI_STATUS);

				/*
				 * if (status != null) { if (status.equals("1")) { journeyId =
				 * intent .getExtras() .getString(
				 * com.smarttaxi.driver.gcm.CommonUtilities.JOURNEY_ID); } }
				 */

				registrationId = intent.getExtras().getString(
						CommonUtilities.REGID);

				if (registrationId != null && IsRegistered == false) {
					// AppToast.ShowToast(getBaseContext(), reg);
					preferencesHandler.setDriverUdid(registrationId);
					PostRegistrationIdtoServer(registrationId, driverUserId);

				}
				if (journeyId != null && status != null) {

					if (status.equals("1"))
						GetPickupRequest(journeyId);
					// AppToast.ShowToast(getBaseContext(), newMessage);
				}
				/*
				 * if (journeyId != null && status.equals("2")) {
				 * AlertBoxHelper.
				 * showAlertBox((Activity)getApplicationContext(), "Alert",
				 * "Journey cancelled by the customer."); }
				 */

				// Waking up mobile if it is sleeping
				WakeLocker.acquire(getApplicationContext());
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
	private RequestDialogFragment newFragment;
	private int position;
	private MyFragmentAdapter madapter;

	@Override
	protected void onStart() {
		
		
		// AppToast.ShowToast(this, "onStart");

		super.onStart();
	}

	@Override
	protected void onRestart() {

		
		// AppToast.ShowToast(this, "onRestart");

		/*
		 * if(isRequestShown) { if (!isNullOrEmpty(journeyData.getUserId()) &&
		 * !isNullOrEmpty(currentJourneyID)) {
		 * LoaderHelper.showLoader(MainActivity.this, "Loading...", "");
		 * WebServiceModel.cancelJourney(JourneyDetailActivity.this,
		 * journeyData.getUserId(), currentJourneyID);// user_id
		 * 
		 * }
		 * 
		 * }
		 */
		/*
		 * FragmentManager fm = getSupportFragmentManager(); findArideFragment =
		 * (FindARideFragment) fm
		 * .findFragmentByTag("initialfindaRideFragment");
		 * 
		 * if (findArideFragment == null) findArideFragment = new
		 * FindARideFragment(); // fm.beginTransaction().add(findArideFragment,
		 * // "task").commit(); fm.beginTransaction()
		 * .add(R.id.frameLayoutContent, findArideFragment,
		 * "initialfindaRideFragment").commit();
		 */

		super.onRestart();
	}

	@Override
	protected void onResume() {

		NetworkAvailability.CheckBroadcastEnableDisable(
				this.getApplicationContext(), "EnablingBroadcast");
		NetworkAvailability.IsNetworkAvailable(this.getBaseContext());
		NetworkAvailability.IsGpsEnabled(this.getBaseContext());
		
		
		// AppToast.ShowToast(this, "M:onResume");
		super.onResume();
		
		// AppToast.ShowToast(this, "onResume");
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (ConnectionResult.SUCCESS == resultCode) {

			if (LocationManager.isBackgroundMode)
				LocationManager.getInstance(this).resumeLocationService();
		}

	}

	@Override
	protected void onPause() {
		// AppToast.ShowToast(this, "onPause");
		NetworkAvailability.CheckBroadcastEnableDisable(
				this.getApplicationContext(), "DisablingBroadcast");
		GCMIntentService.cancelNotification(getApplicationContext());
		super.onPause();

	}

	@Override
	protected void onStop() {

		// AppToast.ShowToast(this, "onStop");

		super.onStop();
	}

	
	@Override
	protected void onDestroy() {
		try {
			if (mRegisterTask != null) {
				mRegisterTask.cancel(true);
			}

			if (!LocationManager.isBackgroundMode)
				LocationManager.getInstance(this).stopLocationService();

			unregisterReceiver(mHandleMessageReceiver);
			GCMRegistrar.onDestroy(this);
		} catch (Exception e) {
			Log.e("UnRegister Receiver Error", "> " + e.getMessage());
		}
		super.onDestroy();
	}

	public void PostRegistrationIdtoServer(String registrationId, String userID) {
		// TODO Auto-generated method stub

		try

		{
			if (registrationId != null) {

				WebServiceModel.postRegistrationIdToServer(registrationId,
						userID, this);
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (MotionEvent.ACTION_UP == event.getAction()) {
			return true;
		}

		return super.onTouchEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent touchevent) {
		switch (touchevent.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			lastX = touchevent.getX();
			break;
		}
		case MotionEvent.ACTION_UP: {
			float currentX = touchevent.getX();
			if (lastX < currentX) {
				if (vf.getDisplayedChild() == 0)
					break;
				vf.setInAnimation(this, R.anim.in_from_left);
				vf.setOutAnimation(this, R.anim.out_to_right);
				vf.showNext();
			}
			if (lastX > currentX) {
				if (vf.getDisplayedChild() == 1)
					break;
				vf.setInAnimation(this, R.anim.in_from_right);
				vf.setOutAnimation(this, R.anim.out_to_left);
				vf.showPrevious();
			}
			break;
		}
		}
		return false;
		// return super.onTouchEvent(event)
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		actionBarDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		actionBarDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initUI() {

		try {
			// mSckLevel = 0;
			pickupCollection = new ArrayList<String>();
			setContentView(R.layout.activity_main);
			drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayoutMain);

			layoutDrawer = (RelativeLayout) findViewById(R.id.layoutDrawer);
			listViewDrawer = (ListView) findViewById(R.id.listViewDrawer);

			leftMenuAdapter = new LeftMenuAdapter(this);
			listViewDrawer.setOnItemClickListener(this);

			listViewDrawer.setAdapter(leftMenuAdapter);
			//drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);

			actionBarDrawerToggle = new ActionBarDrawerToggle(this,
					drawerLayout, R.drawable.ic_drawer, R.string.drawer_open,
					R.string.drawer_close) {

				@Override
				public void onDrawerSlide(View drawerView, float slideOffset) {
					super.onDrawerSlide(drawerView, slideOffset);
					drawerLayout.bringChildToFront(drawerView);
					drawerLayout.requestLayout();

					if (getSupportActionBar().getTitle().equals("Driver Menu"))
						getSupportActionBar().setTitle("");

				}

				@Override
				public void onDrawerOpened(View view) {

					getSupportActionBar().setTitle("Driver Menu");
					supportInvalidateOptionsMenu();
				}

				@Override
				public void onDrawerClosed(View view) {

					// getSupportActionBar().setTitle("");
					supportInvalidateOptionsMenu();
				}

			};
			actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
			drawerLayout.setDrawerListener(actionBarDrawerToggle);
			// actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setIcon(R.drawable.btn_menu);
			getSupportActionBar().setHomeButtonEnabled(true);

		} catch (Throwable t) {
			// AppToast.ShowToast(this, t.getMessage());

		}
	}

	public void setActionBarTitle() {

		switch (TAG_ACTIVE_FRAGMENT) {
		case TAG_ACTIVE_FRAGMENT_FIND_A_RIDE:
			getSupportActionBar().setTitle("My Location");
			break;

		case TAG_INACTIVE_FRAGMENT_CURRENT_TRIP:
			getSupportActionBar().setTitle("Current Trip");
			break;

		case TAG_INACTIVE_FRAGMENT_TRIP_HISTORY:
			getSupportActionBar().setTitle("Trip History");
			break;

		case TAG_INACTIVE_FRAGMENT_DRIVER_EARNING:
			getSupportActionBar().setTitle("Driver Earning");
			break;

		case TAG_INACTIVE_FRAGMENT_USER_PROFILE:
			getSupportActionBar().setTitle("Profile");
			break;
		default:
			getSupportActionBar().setTitle("");
			break;
		}

	}

	public boolean onDown(MotionEvent e) {
		return true;
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return true;
	}

	public void onLongPress(MotionEvent e) {
	}

	public void onShowPress(MotionEvent e) {
	}

	public boolean onSingleTapUp(MotionEvent e) {
		return true;
	}

	@Override
	public void onGesture(GestureOverlayView overlay, MotionEvent event) {
		Toast.makeText(this.getApplicationContext(), "Left", Toast.LENGTH_SHORT)
				.show();

	}

	@Override
	public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
		Toast.makeText(this.getApplicationContext(), "Right",
				Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onGestureEnded(GestureOverlayView overlay, MotionEvent event) {
		Toast.makeText(this.getApplicationContext(), "Up", Toast.LENGTH_SHORT)
				.show();

	}

	@Override
	public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {
		Toast.makeText(this.getApplicationContext(), "Down", Toast.LENGTH_SHORT)
				.show();

	}

	/*
	 * public void GetPickupRequest(String journeyId) { if
	 * (!journeyId.equals("")) { currentJourneyId = journeyId;
	 * WebServiceModel.getPickupRequest(journeyId, this); }
	 * 
	 * }
	 */

	public void GetPickupRequest(String journeyId) {

		try

		{
			if (Utils.isEmptyOrNull(journeyId))
				return;

			/*
			 * if (!journeyId.equals("") && !isRequestShown) { currentJourneyId
			 * = journeyId; isRequestShown = true;
			 * WebServiceModel.getPickupRequest(journeyId, this); } else {
			 * pickupCollection.add(journeyId); }
			 */

			currentJourneyId = journeyId;
			isRequestShown = true;
			WebServiceModel.getPickupRequest(journeyId, this);

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	void showDialog() {
		mStackLevel++;

		try

		{
			// DialogFragment.show() will take care of adding the fragment
			// in a transaction. We also want to remove any currently showing
			// dialog, so make our own transaction and take care of that here.
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
			if (prev != null) {
				ft.remove(prev);
			}

			ft.addToBackStack(null);

			// Create and show the dialog.
			// MyDialogFragment(MainActivity.this).show(getFragmentManager(),
			// "MyDialog");
			// ((MyDialogFragment)getFragmentManager().findFragmentByTag("MyDialog")).getDialog().dismiss();
			DialogFragment newFragment = RequestDialogFragment.newInstance(
					mStackLevel, this);
			newFragment.show(ft, "dialog");
		} catch (Throwable t) {

			AppToast.ShowToast(this, t.getMessage());
		}
	}

	/*
	 * public void showDialog(final Activity activity, String imgUrl, String
	 * userName, String pickupAdrs, String pickTime, String adMsg, final String
	 * remTime, final String currentJourneyId, final String usercoperateName) {
	 * 
	 * final Dialog dialog = new Dialog(activity);
	 * 
	 * dialog.setContentView(R.layout.passenger_request);
	 * dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
	 * 
	 * passengerName = (TextView) dialog.findViewById(R.id.passengerName);
	 * coperateName = (TextView) dialog
	 * .findViewById(R.id.passengerCoperateName); if
	 * (!usercoperateName.equals("")) coperateName.setText(usercoperateName); if
	 * (!userName.equals("")) passengerName.setText(userName); // set the custom
	 * dialog components - text, image and button passengerImage =
	 * (CircularImageView) dialog .findViewById(R.id.passengerImage); if
	 * (!imgUrl.isEmpty() && !imgUrl.equals(null)) {
	 * 
	 * passengerImage.setDrawingCacheEnabled(true); new
	 * DownloadImageAsync((ImageView) passengerImage, activity)
	 * .execute(imgUrl); }
	 * 
	 * pickupAddress = (TextView) dialog.findViewById(R.id.pickupAddress);
	 * 
	 * if (!pickupAdrs.equals("")) pickupAddress.setText(pickupAdrs);
	 * 
	 * pickupTime = (TextView) dialog.findViewById(R.id.pickupTime);
	 * 
	 * if (!pickTime.equals(""))
	 * pickupTime.setText(Utils.getFormattedDate(pickTime));
	 * 
	 * remaingTime = (TextView) dialog.findViewById(R.id.remainingTime);
	 * 
	 * if (!remTime.equals(""))
	 * 
	 * additionalMsg = (TextView) dialog .findViewById(R.id.additionalMessage);
	 * 
	 * if (Utils.isEmptyOrNull(adMsg)) additionalMsg.setText("N/A"); else
	 * additionalMsg.setText(adMsg);
	 * 
	 * acceptRequestButton = (Button) dialog
	 * .findViewById(R.id.acceptRequestButton);
	 * 
	 * rejectRequestButton = (Button) dialog
	 * .findViewById(R.id.rejectRequestButton); dialog.show();
	 * 
	 * remaingTime.setText(String.valueOf(remTime));
	 * 
	 * cdt = new CountDownTimer(15000, 1000) {
	 * 
	 * @Override public void onTick(long millisUntilFinished) {
	 * 
	 * // int time = // Integer.parseInt(remaingTime.getText().toString()); //
	 * time--; remaingTime.setText(String.valueOf(millisUntilFinished / 1000) +
	 * " sec"); }
	 * 
	 * @Override public void onFinish() {
	 * 
	 * MainActivity.isRequestShown = false; // TODO Auto-generated method stub
	 * if (!isJourneyAccepted) { PreferencesHandler preferencesHandler = new
	 * PreferencesHandler( rejectRequestButton.getContext()); if
	 * (preferencesHandler.getOriginalDriverUserID() > 0)
	 * WebServiceModel.callRejected("rejected", String
	 * .valueOf(preferencesHandler .getOriginalDriverUserID()),
	 * currentJourneyId, (HttpResponseListener) activity); //
	 * WebServiceModel.callRejected("", "", "", // (HttpResponseListener)this);
	 * dialog.dismiss(); } else { isJourneyAccepted = false; } } };
	 * 
	 * cdt.start();
	 * 
	 * rejectRequestButton.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) {
	 * 
	 * isJourneyAccepted = false; MainActivity.isRequestShown = false; // TODO
	 * Auto-generated method stub PreferencesHandler preferencesHandler = new
	 * PreferencesHandler( rejectRequestButton.getContext()); if
	 * (preferencesHandler.getOriginalDriverUserID() > 0)
	 * WebServiceModel.callRejected("rejected", String
	 * .valueOf(preferencesHandler .getOriginalDriverUserID()),
	 * currentJourneyId, (HttpResponseListener) activity); // _trd1.interrupt();
	 * // _trd1.stop(); cdt.cancel(); dialog.dismiss();
	 * 
	 * } }); // if button is clicked, close the custom dialog
	 * acceptRequestButton.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) {
	 * 
	 * isJourneyAccepted = true; MainActivity.isRequestShown = true; Driver
	 * driver = new Driver(activity); driver.enablePobStatus("yes");
	 * MainActivity.pickupCollection.clear(); // fragment
	 * 
	 * FragmentTransaction fragmentManager; FragmentManager fm =
	 * getSupportFragmentManager();
	 * 
	 * journeyDetailFragment = (JourneyDetailFragment) fm
	 * .findFragmentByTag("journeyDetailFragment");
	 * 
	 * if (journeyDetailFragment == null) journeyDetailFragment = new
	 * JourneyDetailFragment();
	 * 
	 * // JourneyDetailFragment newFragment = //
	 * JourneyDetailFragment.newInstance(0); fragmentManager =
	 * getSupportFragmentManager() .beginTransaction();
	 * 
	 * fragmentManager
	 * .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
	 * fragmentManager.add(R.id.frameLayoutContent, journeyDetailFragment,
	 * "journeyDetailFragment") .commit();
	 * 
	 * 
	 * Intent myIntent = new Intent(v.getContext(),
	 * JourneyDetailActivity.class); myIntent.setType("text/plain");
	 * myIntent.putExtra(android.content.Intent.EXTRA_TEXT, currentJourneyId);
	 * 
	 * myIntent.putExtra("currentLat",
	 * ServiceLocation.curLocation.getLatitude());
	 * myIntent.putExtra("currentLng",
	 * ServiceLocation.curLocation.getLongitude());
	 * v.getContext().startActivity(myIntent);
	 * 
	 * 
	 * // _trd1.interrupt(); // _trd1.stop(); cdt.cancel(); dialog.dismiss(); }
	 * });
	 * 
	 * dialog.show();
	 * 
	 * dialog.setCanceledOnTouchOutside(true); dialog.setCancelable(true);
	 * dialog.show(); }
	 */

	@Override
	public void onResponse(CustomHttpResponse object) {
		// TODO Auto-generated method stub
		try {
			if (LoaderHelper.progressDialog != null)
				if (LoaderHelper.isLoaderShowing())
					LoaderHelper.hideLoader();
			if(object.getStatusCode() == APIConstants.SUCESS_CODE && object.getMethodName() == APIConstants.METHOD_GET_DIRECT_PROFILE)
			{
				JsonObject profile = (JsonObject)object.getResponse();
				User customer = new User();
				customer.setUserType("customer");
				if(profile.has("User"))
				{
					
					JsonObject userInfo = profile.get("User").getAsJsonObject();
					customer.setFirstName(userInfo.get("first_name").getAsString());
					customer.setId(userInfo.get("id").getAsString());
					customer.setLastName(userInfo.get("last_name").getAsString());
					customer.setGender(userInfo.get("gender").getAsString());
					customer.setUserImage(userInfo.get("user_image").getAsString());
					customer.setStatus(userInfo.get("status").getAsString());
					customer.setCorporateId(userInfo.get("corporate_id").getAsString());
					if(customer.getStatus().equals("active"))
					{
						if(profile.has("corporate_info"))
						{
							customer.setCorporateName(profile.get("corporate_info").getAsJsonObject().get("name").getAsString());
							Log.e("Corporate name:", customer.getCorporateName());
						}
						if(profile.has("journey_type"))
						{
							JsonArray jTypes = profile.get("journey_type").getAsJsonArray();
							List<String> jTypeLabels = new ArrayList<String>();
							for(int i = 0; i < jTypes.size(); i++)
							{
								jTypeLabels.add(jTypes.get(i).getAsString());
							}
							customer.setJourneyTypes(jTypeLabels);
							Log.e("Journey types:", customer.getJourneyTypes().toString());
						}
					}
					ProfileDisplayDialog dialog = new ProfileDisplayDialog();
					dialog.parent = this;
					dialog.setUserInfo(customer);
					dialog.show(getSupportFragmentManager(), "ProfileDisplayDialog");
					Log.e("First name:", customer.getFirstName());
				}
				return;
			}else if(object.getStatusCode() == APIConstants.SUCESS_CODE && object.getMethodName() == APIConstants.METHOD_CREATE_DRIVER_JOURNEY)
			{
				JsonObject body = (JsonObject) object.getResponse();
				if(body.has("Journey"))
				{
					body = body.get("Journey").getAsJsonObject();
					String journeyId = body.get("id").getAsString();
					postCreateDirectTripAction(journeyId);
				}
				return;
			}
			if (object.getStatusCode() == APIConstants.SUCESS_CODE
					|| object.getStatusCode() == APIConstants.UPDATED_CODE) {

				if (object.getResponse() instanceof DeviceRegister) {
					IsRegistered = true;
					DeviceRegister reg = (DeviceRegister) object.getResponse();
					if (!Utils.isEmptyOrNull(reg.getNotificationMsg()))
						AppToast.ShowToast(this, reg.getNotificationMsg());

				} else if (object.getResponse() instanceof PickupRequest) {

					// isRequestShown = true;
					PickupRequest pickup_req = (PickupRequest) object
							.getResponse();
					
					
					/*
					 * CustomDialog.showDialog(this, pickup_req.getUserImage(),
					 * pickup_req.getUserName(), pickup_req.getPickupLocation(),
					 * pickup_req.getPickupTime(),
					 * pickup_req.getAdditionalMessage(), "15",
					 * currentJourneyId, pickup_req.getCorporateName());
					 */

					if (pagerDialog == null) {
						// TODO Auto-generated method stub
						// clearAllBackEntries();
						pagerDialog = new JourneyRequestDialog();

						pagerDialog.setCancelable(false);
						pagerDialog.setShowsDialog(true);
						pagerDialog.initiatePagerDialoge(pickup_req,
								new ViewPagerDialogeNotifier() {

									@Override
									public void rejectJourney(String journeyId) {
										

										PreferencesHandler preferencesHandler = new PreferencesHandler(
												MainActivity.this);
										if (preferencesHandler
												.getOriginalDriverUserID() > 0)
											WebServiceModel.callRejected(
													"rejected",
													String.valueOf(preferencesHandler
															.getOriginalDriverUserID()),
													journeyId,
													MainActivity.this);

									}

									@Override
									public void closeDialog() {
										try {

											Applog.Error(TAG + "="
													+ "CloseDialoge()");

											FragmentTransaction ft = getSupportFragmentManager()
													.beginTransaction();
											Fragment dialogFragment = getSupportFragmentManager()
													.findFragmentByTag(
															TAG_REQUEST_DIALOG);
											ft.remove(dialogFragment);
											ft.commit();
											
											GCMIntentService
											.cancelNotification(getApplicationContext());
											pagerDialog
													.dismissAllowingStateLoss();
											pagerDialog.dismiss();
											pagerDialog = null;

											

											// clearAllBackEntries();
										} catch (Exception e) {
											Applog.Error(TAG + "ONEXCEPTION"
													+ "closeDialog");
											e.printStackTrace();
										}

									}

									@Override
									public void actionCompletedOnDialoge(String acceptedJourneyId) {

										try {
											if (!Utils.isEmptyOrNull(acceptedJourneyId)) {
												// call accepted
												Driver driver = new Driver(
														MainActivity.this);
												driver.enablePobStatus("yes");
												

												Intent myIntent = new Intent(
														MainActivity.this,
														JourneyDetailActivity.class);
												myIntent.setType("text/plain");
												myIntent.putExtra(
														android.content.Intent.EXTRA_TEXT,
														acceptedJourneyId);

												

												myIntent.putExtra(
														"isFromFindARide", true);
												myIntent.putExtra(
														"currentLat",
														ServiceLocation.curLocation
																.getLatitude());
												myIntent.putExtra(
														"currentLng",
														ServiceLocation.curLocation
																.getLongitude());
												startActivity(myIntent);

												
												finish();
												if(pagerDialog != null)
												{
													pagerDialog
															.dismissAllowingStateLoss();
													pagerDialog.dismiss();
													pagerDialog = null;
												}
												
												
											}
										} catch (Exception e) {
											Applog.Error(TAG
													+ "ONEXCEPTION"
													+ "actionCompletedOnDialoge");
											e.printStackTrace();
										}

									}

									/*
									 * @Override public void
									 * rejectsRemainingJourneys( List<Fragment>
									 * otherFragments) { // TODO Auto-generated
									 * method stub for (int i = 0; i <
									 * otherFragments.size(); i++) {
									 * 
									 * Fragment fr= (Fragment)
									 * otherFragments.get(i); String journeyId=
									 * fr.getArguments().getString(JOURNEY_ID);
									 * PreferencesHandler preferencesHandler =
									 * new PreferencesHandler(
									 * MainActivity.this); if
									 * (preferencesHandler
									 * .getOriginalDriverUserID() > 0)
									 * WebServiceModel.callRejected("rejected",
									 * String .valueOf(preferencesHandler
									 * .getOriginalDriverUserID()), journeyId,
									 * MainActivity.this); }
									 * 
									 * 
									 * }
									 */
								});

						// pagerDialog.show(getSupportFragmentManager(),
						// TAG_REQUEST_DIALOG);

						// dialog.show(getSupportFragmentManager(), null);
						FragmentTransaction ft = getSupportFragmentManager()
								.beginTransaction();
						ft.add(pagerDialog, TAG_REQUEST_DIALOG);
						ft.commitAllowingStateLoss();

					} else {

						pagerDialog.addItemInPagerDialoge(pickup_req);
					}

					/*
					 * showDialog(this,pickup_req.getUserImage(),
					 * pickup_req.getUserName(), pickup_req.getPickupLocation(),
					 * pickup_req.getPickupTime(),
					 * pickup_req.getAdditionalMessage(), "15",
					 * currentJourneyId, pickup_req.getCorporateName());
					 */

				} else if (object.getResponse() instanceof AcceptOrRejectJourney) {
					AcceptOrRejectJourney pickup_req = (AcceptOrRejectJourney) object
							.getResponse();

					/*
					 * if (!isRequestShown && !pickupCollection.isEmpty()) {
					 * String journey_id = Utils.isEmptyOrNull(pickupCollection
					 * .get(0)) ? "" : pickupCollection.get(0);
					 * GetPickupRequest(journey_id); pickupCollection.remove(0);
					 * }
					 */

					if (pickup_req != null
							&& !Utils.isEmptyOrNull(pickup_req
									.getNotificationMsg()))
						AppToast.ShowToast(this,
								pickup_req.getNotificationMsg());

				} else if (object.getResponse() instanceof LogoutRequest) {
					// isLoggedin = false;

					AppToast.ShowToast(this, "Logged out successfully");
					PreferencesHandler logoutPh = new PreferencesHandler(this);
					logoutPh.logout();
					clearAllBackEntries();
					Intent intent = new Intent(this, LoginActivity.class);
					finish();
					startActivity(intent);
				}

			} else {
				if (!Utils.isEmptyOrNull(object.getResponseMsg()))
					

					if (object.getResponse() instanceof LogoutRequest) {
						PreferencesHandler logoutPh = new PreferencesHandler(
								this);
						logoutPh.logout();
						// mFragmentStack.clear();
						clearAllBackEntries();
						Intent intent = new Intent(this, LoginActivity.class);
						// isAppExitAllow = false;
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						finish();
						startActivity(intent);
					}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private void postCreateDirectTripAction(String acceptedJourneyId)
	{
		Driver driver = new Driver(MainActivity.this);
		driver.enablePobStatus("yes");
		

		Intent myIntent = new Intent(MainActivity.this, JourneyDetailActivity.class);
		myIntent.setType("text/plain");
		myIntent.putExtra(android.content.Intent.EXTRA_TEXT, acceptedJourneyId);

		myIntent.putExtra("isFromFindARide", false);
		myIntent.putExtra("currentLat", ServiceLocation.curLocation.getLatitude());
		myIntent.putExtra("currentLng", ServiceLocation.curLocation.getLongitude());
		startActivity(myIntent);
		finish();
	}

	public void clearAllBackEntries() {
		try {
			int backStackCount = getSupportFragmentManager()
					.getBackStackEntryCount();
			for (int i = 0; i < backStackCount; i++) {

				// Get the back stack fragment id.
				int backStackId = getSupportFragmentManager()
						.getBackStackEntryAt(i).getId();

				getSupportFragmentManager().popBackStack(backStackId,
						FragmentManager.POP_BACK_STACK_INCLUSIVE);

			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	@Override
	public void onException(CustomHttpException exception) {
		// TODO Auto-generated method stub
		try {
			if (LoaderHelper.isLoaderShowing()) {
				LoaderHelper.hideLoader();
				AppToast.ShowToast(this, exception.getMessage());

			}
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	private Fragment getPreviousFragment() {
		String fragmentTag;
		Fragment previousFragment;
		FragmentManager fragmentManager = getSupportFragmentManager();
		if (fragmentManager.getBackStackEntryCount() > 0) {
			fragmentTag = fragmentManager.getBackStackEntryAt(
					fragmentManager.getBackStackEntryCount() - 1).getName();
			previousFragment = getSupportFragmentManager().findFragmentByTag(
					fragmentTag);

		} else
			return findArideFragment;

		return previousFragment;
	}

	private Fragment getCurrentFragment() {
		String fragmentTag;
		Fragment currentFragment;
		FragmentManager fragmentManager = getSupportFragmentManager();
		if (fragmentManager.getBackStackEntryCount() > 0) {
			fragmentTag = fragmentManager.getBackStackEntryAt(
					fragmentManager.getBackStackEntryCount() - 1).getName();
			currentFragment = getSupportFragmentManager().findFragmentByTag(
					fragmentTag);

		} else
			return findArideFragment;

		return currentFragment;
	}

	public void performTransaction(Fragment currentFragment, String fragmentTag) {


		if (FragmentHolder == null) {
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.replace(R.id.frameLayoutContent, currentFragment, fragmentTag);
			ft.commit();
			FragmentHolder = currentFragment;
			FragmentInUI = currentFragment;
		} else {

			FragmentHolder = (Fragment) fragmentManager
					.findFragmentByTag(fragmentTag);

			if (currentFragment.getClass() != FragmentInUI.getClass()) {
				FragmentTransaction ft = getSupportFragmentManager()
						.beginTransaction();
				ft.replace(R.id.frameLayoutContent,
						mFragmentStack.push(currentFragment), "");
				ft.commit();
				FragmentHolder = currentFragment;
				FragmentInUI = currentFragment;
			}

		}

	}

	/*private AdapterView.OnItemClickListener onItemClickListner = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			

		}
	};
*/
	public void LogoutCurrentUser() {
		LoaderHelper.showLoader(MainActivity.this, "Please wait...", "");
		String userId = "";
		preferencesHandler = new PreferencesHandler(this);
		if (preferencesHandler.getOriginalDriverUserID() > 0) {
			userId = String.valueOf(preferencesHandler
					.getOriginalDriverUserID());
			WebServiceModel.logoutCurrentUser(userId, "6", this);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		FragmentTransaction fragmentManager;
		FragmentManager fm = getSupportFragmentManager();

		Applog.Debug("On Item Click " + position);
		switch (position) {
		case 0:
			TAG_ACTIVE_FRAGMENT = TAG_ACTIVE_FRAGMENT_FIND_A_RIDE;
			setActionBarTitle();
			boolean iscurrentFragmentTypeFindAride = false;
			Fragment fragmentBeforeBackPress = getCurrentFragment();
			if (fragmentBeforeBackPress == null)
				iscurrentFragmentTypeFindAride = false;
			else if (fragmentBeforeBackPress instanceof FindARideFragment)
				iscurrentFragmentTypeFindAride = true;

			if (findArideFragment == null
					|| !iscurrentFragmentTypeFindAride) {

				findArideFragment = (FindARideFragment) fm
						.findFragmentByTag(TAG_FIND_A_RIDE_FRAGMENT);

				if (findArideFragment == null)
					findArideFragment = new FindARideFragment();
				fm.beginTransaction()
						.replace(R.id.frameLayoutContent,
								findArideFragment, TAG_FIND_A_RIDE_FRAGMENT)
						.commit();

				getSupportActionBar().setTitle("My Location");

			}

			break;
			
		case 1:
			IntentIntegrator integrator = new IntentIntegrator(this);
			//Use 1 for front camera
			integrator.initiateScan(1);
			break;

		case 2: // CURRENT TRIP
			TAG_ACTIVE_FRAGMENT = TAG_INACTIVE_FRAGMENT_CURRENT_TRIP;
			// setActionBarTitle();
			curentTripFragment = (CurrentTripFragment) fm
					.findFragmentByTag(TAG_CURRENT_TRIP_FRAGMENT);

			if (curentTripFragment == null)
				curentTripFragment = new CurrentTripFragment();

			fragmentManager = getSupportFragmentManager()
					.beginTransaction();
			// performTransaction(userProfileFragment,
			// "userProfileFragment");
			fragmentManager
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			fragmentManager.replace(R.id.frameLayoutContent,
					curentTripFragment, TAG_CURRENT_TRIP_FRAGMENT);
			fragmentManager.addToBackStack(TAG_CURRENT_TRIP_FRAGMENT)
					.commit();

			getSupportActionBar().setTitle("Current Trip");

			break;

		case 3: // TRIP HISTORY
			TAG_ACTIVE_FRAGMENT = TAG_INACTIVE_FRAGMENT_TRIP_HISTORY;
			// setActionBarTitle();
			tripHistory = (HistoryTripFragment) fm
					.findFragmentByTag(TAG_TRIP_HISTORY_FRAGMENT);
			if (tripHistory == null)
				tripHistory = new HistoryTripFragment();
			fragmentManager = getSupportFragmentManager()
					.beginTransaction();
			// performTransaction(tripHistory, "tripHistory");
			fragmentManager
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			fragmentManager.replace(R.id.frameLayoutContent, tripHistory,
					TAG_TRIP_HISTORY_FRAGMENT);
			fragmentManager.addToBackStack(TAG_TRIP_HISTORY_FRAGMENT)
					.commit();

			getSupportActionBar().setTitle("Trip History");

			break;
		case 4:
			// DRIVER EARNING
			TAG_ACTIVE_FRAGMENT = TAG_INACTIVE_FRAGMENT_DRIVER_EARNING;
			// setActionBarTitle();
			earningFragment = (EarningFragment) fm
					.findFragmentByTag(TAG_DRIVER_EARNING_FRAGMENT);
			if (earningFragment == null)
				earningFragment = new EarningFragment();
			fragmentManager = getSupportFragmentManager()
					.beginTransaction();
			// performTransaction(tripHistory, "tripHistory");
			fragmentManager
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			fragmentManager.replace(R.id.frameLayoutContent,
					earningFragment, TAG_DRIVER_EARNING_FRAGMENT);
			fragmentManager.addToBackStack(TAG_DRIVER_EARNING_FRAGMENT)
					.commit();

			getSupportActionBar().setTitle("Driver Earning");

			break;
		case 5:
			// USER PROFILE
			TAG_ACTIVE_FRAGMENT = TAG_INACTIVE_FRAGMENT_USER_PROFILE;
			// setActionBarTitle();
			userProfileFragment = (UserProfileFragment) fm
					.findFragmentByTag(TAG_USER_PROFILE_FRAGMENT);

			if (userProfileFragment == null)
				userProfileFragment = new UserProfileFragment();
			fragmentManager = getSupportFragmentManager()
					.beginTransaction();
			// performTransaction(userProfileFragment,
			// "userProfileFragment");
			fragmentManager
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			fragmentManager.replace(R.id.frameLayoutContent,
					userProfileFragment, TAG_USER_PROFILE_FRAGMENT);
			fragmentManager.addToBackStack(TAG_USER_PROFILE_FRAGMENT)
					.commit();

			getSupportActionBar().setTitle("Profile");

			break;
		case 6:
			LogoutCurrentUser();
			break;
		case 7:

			// AppToast.ShowToast(getBaseContext(), "Logout clicked");

			break;
		default:
			break;
		}

		drawerLayout.closeDrawers();
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		try{
			IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
			String result = scanResult.getContents().toString();
			if (!Utils.isEmptyOrNull(result) && result.contains("-")) {
				//((TextView)findViewById(R.id.labelQRData)).setText(((CharSequence)scanResult.getContents()));
				String[] chunks = result.split("-");
				if(chunks.length == 2)
				{
					int userId = Double.valueOf(chunks[1]).intValue();
					if(userId > 0)
					{
						if(NetworkAvailability.IsNetworkAvailable(getBaseContext()))
						{
							/*if(!ServiceLocation.isService)
							{
								startService(new Intent(this, ServiceLocation.class));
							}*/
							List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
							params.add(new BasicNameValuePair("user_id", String.valueOf(userId)));
							LoaderHelper.showLoader(this, "Please wait while retrieving your profile...", "Loading profile");
							CustomHttpClass.runPostService(this, APIConstants.METHOD_GET_DIRECT_PROFILE, params, true, false);
						}else{
							NetworkAvailability.showNoConnectionDialog(this);
						}
					}else{
						// TODO: Show invalid bar code alert dialogue
					}
				}
			}
		}catch(Exception ex)
		{
			Log.e("onActivity result exception", EXTRA_MESSAGE.toString());
		}
			
		 // else continue with any other code you need in the method
		 
	}

	public void createDirectJourney(String userId, String journeyType) {
		Date currentDate = new Date();
		preferencesHandler = new PreferencesHandler(this);
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String pickUpTime = ft.format(currentDate);
		String lat = String.valueOf(ServiceLocation.curLocation.getLatitude());
		String lng = String.valueOf(ServiceLocation.curLocation.getLongitude());
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("child_seats", "0"));
		params.add(new BasicNameValuePair("journey_option", "solo"));
		params.add(new BasicNameValuePair("max_no_of_passengers", "1"));
		params.add(new BasicNameValuePair("no_of_bags", "1"));
		params.add(new BasicNameValuePair("pick_address", ServiceLocation.curAddress));
		params.add(new BasicNameValuePair("pickup_lat", lat));
		params.add(new BasicNameValuePair("pickup_lng", lng));
		params.add(new BasicNameValuePair("pickup_time", pickUpTime));
		params.add(new BasicNameValuePair("user_id", userId));
		params.add(new BasicNameValuePair("optional_message", ""));
		params.add(new BasicNameValuePair("journey_type", journeyType));
		params.add(new BasicNameValuePair("cab_id", preferencesHandler.getCabID()));
		Log.e("params", params.toString());
		LoaderHelper.showLoader(this, "Please wait while creating the trip...", "Creating trip");
		CustomHttpClass.runPostService(this, APIConstants.METHOD_CREATE_DRIVER_JOURNEY, params, true, false);
	}

}