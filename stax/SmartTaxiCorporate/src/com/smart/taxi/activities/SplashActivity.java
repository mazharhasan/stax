package com.smart.taxi.activities;

import static com.smart.taxi.utils.CommonUtilities.BACKOFF_MILLI_SECONDS;
import static com.smart.taxi.utils.CommonUtilities.PLAY_SERVICES_RESOLUTION_REQUEST;
import static com.smart.taxi.utils.CommonUtilities.PROPERTY_APP_VERSION;
import static com.smart.taxi.utils.CommonUtilities.PROPERTY_REG_ID;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.smarttaxi.client.AddCardsActivity;
import com.smarttaxi.client.R;
import com.smarttaxi.client.SignUpActivity;
import com.smarttaxi.client.VerifyAccountActivuty;
import com.smart.taxi.constants.APIConstants;
import com.smart.taxi.entities.Cab;
import com.smart.taxi.entities.Card;
import com.smart.taxi.entities.CorporateInfo;
import com.smart.taxi.entities.CustomHttpException;
import com.smart.taxi.entities.CustomHttpResponse;
import com.smart.taxi.entities.TripDetails;
import com.smart.taxi.entities.User;
import com.smart.taxi.helpers.LoaderHelper;
import com.smart.taxi.preferences.PreferencesHandler;
import com.smart.taxi.utils.CommonUtilities;
import com.smart.taxi.utils.NetworkAvailability;
import com.smart.taxi.utils.Utils;


public class SplashActivity extends BaseActivity {
	private static final String TAG = "GCMWork";
	private EditText tfEmail;
	private EditText tfPassword;
	private Button btnLogin;
	public static SplashActivity splashActivity;
	private GoogleCloudMessaging gcm;
	Context context;
	protected String regid;
	
	
	private static final Random random = new Random();
	public static User loggedInUser;
	private static boolean loggedIn = false;
	public static boolean doLogoutAction = false;
	public static boolean isTripRequested = false;
	private static TripDetails tripNewDetails = null;
	private static boolean onceRegistered = false;
	private static boolean isRestoring = false;
	private Bundle bundle;
	private Button btnRegister;
	private static boolean launched = false;
	private static long launchTime = 0;
	
	public SplashActivity()
	{
		super();
		SplashActivity.splashActivity = this;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		//getActionBar().hide();
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
		initUI();
		PreferencesHandler pf = new PreferencesHandler(this);
		long uid = pf.getUserID();
		if(uid > 0)
		{
			User oldUser = pf.getLastUserObject();
			if(oldUser != null && oldUser.getId().equals(String.valueOf(uid)))
			{
				SplashActivity.loggedInUser = oldUser;
				SplashActivity.setLoggedIn(true);
				String reg = pf.getRegId();
				if(!Utils.isEmptyOrNull(reg))
				{
					Map<String, String> regParams = new Gson().fromJson(reg, HashMap.class);
					if(regParams != null && regParams.get("userId").equals(String.valueOf(uid)))
					{
						regid = regParams.get("key");
						isRestoring = true;
					}
				}
			}
		}
		
		
		//this.startActivity(i);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		bundle = getIntent().getExtras();
    	
		if(doLogoutAction)
		{
			return;
		}
		
		if(SplashActivity.isLoggedIn())
		{
			
			defaultLoginAction();
			//return;
		}
		
		if(btnLogin != null)
		{
			btnLogin.setOnClickListener(this);
		}else{
			initUI();
		}
			
	}

	private void initUI() {
		tfEmail = (EditText) findViewById(R.id.login_email);
		tfPassword = (EditText) findViewById(R.id.login_password);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnRegister = (Button) findViewById(R.id.btnRegister);
		btnLogin.setOnClickListener(this);
		btnRegister.setOnClickListener(this);
	}

	
    @Override
    protected void onActivityResult(int requestCode,
            int resultCode, Intent data) {
    	if(requestCode == AddCardsActivity.CARD_ADD_REQUEST)
		{
			if(resultCode == AddCardsActivity.CARD_RESULT_SUCCESS)
			{
				if(loggedInUser.hasCards())
				{
					defaultLoginAction();
				}
			}else{
				Toast.makeText(this, "Add card cancelled", Toast.LENGTH_LONG).show();
			}
		}
    	
    	Log.e("On activity result", String.valueOf(requestCode).concat(String.valueOf(requestCode)));
		/*super.onActivityResult(requestCode, resultCode, data);
		EditText et = (EditText)findViewById(R.id.url);
		if(resultCode == 100){
			Log.e("asd", data.getExtras().get("results").toString());
			et.setText("Welcome mazhar");
		}else 
		{
			et.setText("The fuck ho gaya");
		}*/
    }
    
    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
    
    public void getRequest(TextView txtResult, EditText txtUrl){
        String url = txtUrl.getText().toString();
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        try{
            HttpResponse response = client.execute(request);
            txtResult.setText(HttpHelper.request(response));
        }catch(Exception ex){
            txtResult.setText(ex.toString());
        }
    }
    
    public void showLogo()
    {
    	Log.e("into hide logo", "into hide logo");
    	findViewById(R.id.imgSplashLogo).setVisibility(View.VISIBLE);
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnLogin:	
			doLogin();
			break;
			
		case R.id.btnRegister:
			doRegister();
			break;

		default:
			break;
		}
		
		//startLocationService();
		
	}

	

	private void doRegister() {
		// TODO Auto-generated method stub
		//Intent signUpActivity = new Intent(getApplicationContext(), AddCardsActivity.class);
		Intent signUpActivity = new Intent(getApplicationContext(), SignUpActivity.class);
		//signUpActivity.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(signUpActivity);
		overridePendingTransition(R.anim.slide_left, R.anim.slide_left);
	}

	private void doLogin() {
		if(SplashActivity.isLoggedIn())
		{
			defaultLoginAction();
			return;
		}
		if(validate())
		{
			Log.e("Login function", "Validated");
			if(NetworkAvailability.IsNetworkAvailable(getBaseContext()))
			{
				LoaderHelper.showLoader(this, "Logging in...", "");
				//btnLogin.setOnClickListener(null);
				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair(
						APIConstants.KEY_USERNAME, tfEmail.getText().toString()));
				//Utils.validateEmptyString(tfEmail.getText().toString().trim())));
				params.add(new BasicNameValuePair(
						APIConstants.KEY_PASSWORD, tfPassword.getText().toString()));
				CommonUtilities.hideSoftKeyboard(this);
				CustomHttpClass.runPostService(this, APIConstants.METHOD_LOGIN, params, false, false);
				//HttpAsyncTask htask =  runLoginService(tfEmail.getText().toString(), tfPassword.getText().toString(),this);
			}else{
				NetworkAvailability.showNoConnectionDialog(this);
			}
		}else{
			
			Log.e("Login function", "Not validated");
		}
		
	}
	
	public static void doForcedLogin(String email, String password)
	{
		if(NetworkAvailability.IsNetworkAvailable(SplashActivity.splashActivity))
		{
			LoaderHelper.showLoader(SplashActivity.splashActivity, "Logging in...", "");
			List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair(
					APIConstants.KEY_USERNAME, email));
			//Utils.validateEmptyString(tfEmail.getText().toString().trim())));
			params.add(new BasicNameValuePair(
					APIConstants.KEY_PASSWORD, password));
			CustomHttpClass.runPostService(SplashActivity.splashActivity, APIConstants.METHOD_LOGIN, params, false, false);
			//HttpAsyncTask htask =  runLoginService(tfEmail.getText().toString(), tfPassword.getText().toString(),this);
		}else{
			NetworkAvailability.showNoConnectionDialog(SplashActivity.splashActivity);
		}
	}

	
	

	private boolean validate() {
		// TODO Auto-generated method stub
		boolean isInValid = true;
		String email = tfEmail.getText().toString();
		String password = tfPassword.getText().toString();
		isInValid = Utils.isEmptyOrNull(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
		
		if(isInValid)
		{
			tfEmail.setError("Please provide a valid email address");
			tfEmail.requestFocus();
			//CommonUtilities.displayAlert(this, "Please provide your email address.", "Missing credentials!", "", "Close");
			return false;
		}
		isInValid = Utils.isEmptyOrNull(password);
		if(isInValid)
		{
			tfPassword.setError("Please provide a password");
			tfPassword.requestFocus();
			//CommonUtilities.displayAlert(this, "Please provide your password.", "Missing credentials!", "", "Close");
			
		}
		
		return !isInValid;
	}

	@Override
	public void onResponse(CustomHttpResponse object) {
		//btnLogin.setOnClickListener(null);
		LoaderHelper.hideLoaderSafe();
		if(object.getMethodName() == APIConstants.METHOD_LOGIN)
		{
			Log.e("Login reply", object.getRawJson());
			if(object.getStatusCode() > 0)
			{
				showInvalidCredentialsMessage(object.getStatusCode());
				return;
			}
			try{
				JsonObject userObject = CustomHttpClass.getJsonObjectFromBody(object.getRawJson(), APIConstants.KEY_USER);
				User user = new User();
				JsonObject corporateInfo, cardsInfo;
				JsonArray journeyTypes;
				user = user.deserializeUserObjectFromJSON(userObject);
				if(user.isCorporateUser())
				{
					corporateInfo = CustomHttpClass.getJsonObjectFromBody(object.getRawJson(), APIConstants.KEY_CORPORATE_INFO);
					if(corporateInfo != null)
					{
						CorporateInfo cInfo = CorporateInfo.deserializeFromJson(corporateInfo);
						user.setCorporateInfo(cInfo);
					}
						
					journeyTypes = CustomHttpClass.getJsonArrayFromBody(object.getRawJson(), APIConstants.KEY_JOURNEY_TYPE);
					if(journeyTypes != null && journeyTypes.size() > 0)
					{
						ArrayList<String> types = new ArrayList<String>();
						for(int i = 0; i < journeyTypes.size(); i++){
							types.add(journeyTypes.get(i).getAsString());
						}
						user.setJourneyTypes(types);
					}
				}else{
					cardsInfo = CustomHttpClass.getJsonObjectFromBody(object.getRawJson(), APIConstants.KEY_CARDS_INFO);
					if(cardsInfo != null)
					{
						ArrayList<Card> cards = new ArrayList<Card>();
						for (Entry<String, JsonElement> entry : cardsInfo.getAsJsonObject().entrySet()) {
							if(!Utils.isEmptyOrNull(entry.getKey()))
							{
								Card card;
								JsonObject cardJson = entry.getValue().getAsJsonObject(); 
								card = Card.parseCard(cardJson);
								Log.e("Card loaded", cardJson.get("card_number").getAsString());
								cards.add(card);
							}
						}
						user.setUserCards(cards);
					}
					Log.e("Customer here", user.toString());
					//return;
				}
	
				
				user.setJSON(object.getRawJson());
				Log.i("group_id", user.getGroupId().toString());
				user.setPassword(tfPassword.getText().toString());
				SplashActivity.setLoggedIn(true);
				SplashActivity.loggedInUser = user;
				if(user.isCorporateUser() || user.hasCards())
				{
					updatePreferences();
					defaultLoginAction();
				}
				else{
					startAddCardsActivity();
					return;
				}
			}catch(Exception ex)
			{
				resetLoginState();
				CommonUtilities.displayAlert(this, "Server error occured, please try again", "", "Retry", "", true);
			}
		}else if(object.getMethodName() == APIConstants.METHOD_LOGOUT)
		{
			doLogoutAction = false;
			if(object.getStatusCode() > 0)
			{
				//until customer logout is fixed
				CommonUtilities.displayAlert(this, "Can not log you out right now, please try late", "Logout failed!", "Ok"	, "", false);
				defaultLoginAction();
			}else{
				//defaultLoginAction();
				//CommonUtilities.displayAlert(this, "Can not log you out right now, please try late", "Logout failed!", "Ok"	, "", false);
				postLogout();
			}
		}
			
		
	}

	public void startAddCardsActivity() {
		Intent addCardIntent = new Intent(this, AddCardsActivity.class);
		addCardIntent.putExtra(APIConstants.IS_FIRST_CARD, "true");
		startActivityForResult(addCardIntent,  999);
		overridePendingTransition(R.anim.slide_left, R.anim.slide_left);
	}

	private void updatePreferences() {
		PreferencesHandler pf = new PreferencesHandler(this);
		pf.setUserLoggedIn();
		
	}

	private void showInvalidCredentialsMessage(int statusCode) {
		// TODO Auto-generated method stub
		if(statusCode == 1064)
		{
			startVerifyAccountActivity();
		}else{
			resetLoginState();
			CommonUtilities.displayAlert(this, "Please provide valid username and password.", "Invalid credentials!", "Retry", "Close", true);
		}
		
		
		
	}

	private void startVerifyAccountActivity() {
		Intent verifyAccountIntent = new Intent(this, VerifyAccountActivuty.class);
		verifyAccountIntent.putExtra("email", tfEmail.getText().toString());
		verifyAccountIntent.putExtra("password", tfPassword.getText().toString());
		startActivity(verifyAccountIntent);
		overridePendingTransition(R.anim.slide_left, R.anim.slide_left);
	}

	public void defaultLoginAction() {
		if(!SplashActivity.loggedInUser.isCorporateUser() && !SplashActivity.loggedInUser.hasCards())
		{
			startAddCardsActivity();
			return;
		}
		//startService(new Intent(getApplicationContext(), ServiceLocation.class));
		if(Utils.isEmptyOrNull(regid))
		{
			LoaderHelper.showLoader(splashActivity, "Setting up application...", "");
			saveRegId();
		}else{
			registerIdAndProceed(true);
		}
		
		
		/*new Thread(new Runnable() {
			
			@Override
			public void run() {
				try{
					//saveRegId();
				}catch(Exception ex)
				{
					Log.e("IOException in registration", ex.toString());
				}
				// TODO Auto-generated method stub
				
			}
		}).start();*/
		//saveRegId();
	}

	

	@Override
	public void onException(CustomHttpException exception) {
		// TODO Auto-generated method stub
		if(exception.getMethodName() == APIConstants.METHOD_LOGIN)
		{
			showInvalidCredentialsMessage(-1);
		}else if(exception.getMethodName() == APIConstants.METHOD_LOGOUT){
			doLogoutAction = false;
			CommonUtilities.displayAlert(splashActivity, "Can not logout at the moment,  please try again.", "Error in logout:", "Retyr", "Close", true);
			
		}
		
		
	}
	
	private void resetLoginState() {
		btnLogin.setOnClickListener(this);
		LoaderHelper.hideLoader();
		SplashActivity.setLoggedIn(false);
		
	}

	public Button getLoginButton()
	{
		return btnLogin;
	}

	public static boolean isLoggedIn() {
		return loggedIn;
	}

	public static void setLoggedIn(boolean loggedIn) {
		SplashActivity.loggedIn = loggedIn;
	}
	
	private void saveRegId() {
		
		if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            registerInBackground();
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
	}
	
	private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }
	
	private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(SplashActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

	private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
	
	
	private void registerInBackground() {
        new AsyncTask<Object, Object, Object>() {

			@Override
        	protected void onPostExecute(Object result) {
				registerIdAndProceed(true);
				Log.i(TAG, result.toString());
            }

            @Override
        	protected String doInBackground(Object... objects){
				String msg = "";
				
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    //regid = gcm.register("387001418175");
                    regid = gcm.register("413670147999");
                    msg = "Device registered, registration ID=" + regid;
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    
                }
                return msg;
			}
        }.execute(null, null, null);
    }
	
	protected void registerIdAndProceed(boolean needToRegister) {
		if(!Utils.isEmptyOrNull(regid) && needToRegister && !onceRegistered)
		{
			SplashActivity.register(getApplicationContext(), regid);
    		Log.e("Regid:", regid.toString());
		}else{
			startContainerActivity();
		}
		
	}

	public static void register(final Context context, final String regId) {
		if(onceRegistered)
		{
			if(!Utils.isEmptyOrNull(regId))
			{
				splashActivity.startContainerActivity();
			}
			return;
		}
		if(!Utils.isEmptyOrNull(regId) && !SplashActivity.isRestoring )
		{
			PreferencesHandler pf = new PreferencesHandler(splashActivity);
			pf.setRegId(regId);
		}
			
        Log.i(TAG, "registering device (regId = " + regId + ")");
        String serverUrl = APIConstants.API_END_POINT + "add_udid.json";
        Map<String, String> params = new HashMap<String, String>();
        
    	GCMRegistrar.setRegisteredOnServer(context, true);
    	params.put("udid", regId);
    	params.put("device_type", "Android");
    	params.put("user_id", SplashActivity.loggedInUser.getId());
    	long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        // Once GCM returns a registration id, we need to register it in the
        // demo server. As the server might be down, we will retry it a couple
        // times.
        for (int i = 1; i <= 1; i++) {
            Log.d(TAG, "Attempt #" + i + " to register");
            try {
                post(serverUrl, params);
                //GCMRegistrar.setRegisteredOnServer(context, true);
                onceRegistered  = true;
                splashActivity.startContainerActivity();
                
                return;
            } catch (IOException e) {
                // Here we are simplifying and retrying on any error; in a real
                // application, it should retry only on unrecoverable errors
                // (like HTTP error code 503).
                Log.e(TAG, "Failed to register on attempt " + i + ":" + e);
                if (i == 2) {
                    break;
                }
                try {
                    Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    // Activity finished before we complete - exit.
                    Log.d(TAG, "Thread interrupted: abort remaining retries!");
                    Thread.currentThread().interrupt();
                    return;
                }
                // increase backoff exponentially
                backoff *= 2;
            }
        }
        
        
        //String message = context.getString(R.string.server_register_error, MAX_ATTEMPTS);
        //CommonUtilities.displayMessage(context, message);
    }
	

	private static void post(String endpoint, Map<String, String> params)
            throws IOException {
        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
            Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }
        String body = bodyBuilder.toString();
        Log.v(TAG, "Posting '" + body + "' to " + url);
        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            // handle the response
            int status = conn.getResponseCode();
            if (status != 200) {
              throw new IOException("Post failed with error code " + status);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
      }


	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 *
	 * @param context application's context.
	 * @param regId registration ID
	 */
	private void storeRegistrationId(Context context, String regId) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    int appVersion = getAppVersion(context);
	    Log.i(TAG, "Saving regId on app version " + appVersion);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(PROPERTY_REG_ID, regId);
	    editor.putInt(PROPERTY_APP_VERSION, appVersion);
	    editor.commit();
	}
    

	private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

	public void startContainerActivity() {
		/*if(!ServiceLocation.isService)
			startService(new Intent(this, ServiceLocation.class));*/
		try{
			LoaderHelper.hideLoaderSafe();
		}catch(Exception ex)
		{
			Log.e("Loader hide error", "loder doesnt exists");
		}
		if(SplashActivity.launchTime == 0 || SplashActivity.launchTime + 1000 < (new Date()).getTime())
		{
			Intent mainActivityLaunchIntent = new Intent(getApplicationContext(), ContainerActivity.class);
			mainActivityLaunchIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
			if(bundle != null)
			{
				Log.i("Putting bundle as ", bundle.toString());
				mainActivityLaunchIntent.putExtra("score",bundle.get("to_courage").toString());
			}
			SplashActivity.launchTime = (new Date()).getTime();
			startActivityForResult(mainActivityLaunchIntent, 100);
			overridePendingTransition(R.anim.slide_left, R.anim.slide_left);
		}
	}

	@Override
    protected void onDestroy() {
        try{
        	
        	logout();
        	postLogout();
        //unregisterReceiver(mHandleMessageReceiver);
        GCMRegistrar.onDestroy(this);
        }catch(Exception ex)
        {
        	Log.i("GCM", "Receiver wasn't registered");
        }
        super.onDestroy();
    }

	

	public static void logout() {
		if(NetworkAvailability.IsNetworkAvailable(splashActivity))
		{
			LoaderHelper.showLoader(splashActivity, "Logging out..", "Please wait");
			List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("user_id", SplashActivity.loggedInUser.getId()));
			params.add(new BasicNameValuePair("group_id", SplashActivity.loggedInUser.getGroupId()));
			CustomHttpClass.runPostService(splashActivity, APIConstants.METHOD_LOGOUT, params, false, false);
			Log.e(TAG, "Logging you out bitches");
		}else{
			
		}
	}
	
	private void postLogout()
	{
		onceRegistered = false;
		//Log.e("Service location status", String.valueOf(ServiceLocation.isService));
		PreferencesHandler pf = new PreferencesHandler(this);
		pf.logout();
		/*if(ServiceLocation.isService)
		{
			splashActivity.stopService(new Intent(splashActivity, ServiceLocation.class));
		}*/
		regid = null;
		/*Log.e("Service location status", String.valueOf(ServiceLocation.isService));*/
		clearNotifications();
		setLoggedIn(false);
		loggedInUser = null;
		splashActivity.getLoginButton().setOnClickListener(splashActivity);
	}

	private void clearNotifications() {
		NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nManager.cancelAll();
		
	}

	public static void forceLogin() {
		splashActivity.defaultLoginAction();
	}

	public static TripDetails getTripNewDetails() {
		return tripNewDetails;
	}

	public static void setTripNewDetails(TripDetails tripNewDetails) {
		SplashActivity.tripNewDetails = tripNewDetails;
	}

	public static void doPrepareForLogin(String newEMail, String newPassword) {
		splashActivity.tfEmail.setText(newEMail);
		splashActivity.tfPassword.setText(newPassword);
		splashActivity.doLogin();
	}
}

class IntervalTask extends TimerTask {
	private SplashActivity innerCallingActivity;
	public IntervalTask(SplashActivity callingActivity) {
	// TODO Auto-generated constructor stub
		this.innerCallingActivity = callingActivity;
	}
    //times member represent calling times.
    private int times = 0;
 
 
    public void run() {
    	Log.e("ahem", "ahem");
    	try
    	{
	    	innerCallingActivity.showLogo();
	    	
	    	
    	}catch(Exception e)
    	{
    		Log.e("asd",e.toString());
    		System.out.print(e);
    	}finally{
    		this.cancel();
    	}
    	
        /*times++;
        if (times <= 5) {
            System.out.println("I'm alive...");
        } else {
            System.out.println("Timer stops now...");
 
            //Stop Timer.
            
        }*/
    }

}
