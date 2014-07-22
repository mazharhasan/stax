package com.smarttaxi.driver.activities;

import static com.smarttaxi.driver.gcm.CommonUtilities.SENDER_ID;
import static com.smarttaxi.driver.gcm.CommonUtilities.SERVER_URL;

import com.smarttaxi.driver.R;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.smarttaxi.driver.BAL.Driver;
import com.smarttaxi.driver.constants.APIConstants;
import com.smarttaxi.driver.entities.CustomHttpException;
import com.smarttaxi.driver.entities.CustomHttpResponse;
import com.smarttaxi.driver.entities.DeviceRegister;
import com.smarttaxi.driver.entities.User;
import com.smarttaxi.driver.gcm.ConnectionDetector;
import com.smarttaxi.driver.helpers.AlertBoxHelper;
import com.smarttaxi.driver.helpers.LoaderHelper;
import com.smarttaxi.driver.interfaces.HttpResponseListener;
import com.smarttaxi.driver.model.FactoryModel;
import com.smarttaxi.driver.model.WebServiceModel;
import com.smarttaxi.driver.preferences.PreferencesHandler;
import com.smarttaxi.driver.utils.AppToast;
import com.smarttaxi.driver.utils.Applog;
import com.smarttaxi.driver.utils.NetworkAvailability;
import com.smarttaxi.driver.utils.Utils;

public class LoginActivity extends Activity implements OnClickListener,
		HttpResponseListener {
	private ViewFlipper vf;
	private float lastX;
	public static AlertDialog alertDialog;
	private EditText editTxtUserName;
	private EditText editTextPassword;

	private String userName;

	// Internet detector
	ConnectionDetector cd;

	private String password;

	private Button btnLogin;
	private Button btnSignup;
	private Button btnForgotPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);

		initUI();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

		// stopService(new Intent(LoginActivity.this, ServiceLocation.class));

		exitApp();

	}

	private void exitApp() {
		// stopService(new Intent(MainActivity.this, ServiceLocation.class));
		try {
			Intent startMain = new Intent(Intent.ACTION_MAIN);
			startMain.addCategory(Intent.CATEGORY_HOME);
			// this.finish();

			// System.gc();
			startActivity(startMain);
			finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initUI() {

		try {
			editTxtUserName = (EditText) findViewById(R.id.editTxtUserName);
			editTextPassword = (EditText) findViewById(R.id.editTxtPassword);
			btnLogin = (Button) findViewById(R.id.btnLogIn);
			btnSignup = (Button) findViewById(R.id.btnSignup);
			btnForgotPassword = (Button) findViewById(R.id.btnForgotPassword);

			btnLogin.setOnClickListener(this);
			btnSignup.setOnClickListener(this);
			btnForgotPassword.setOnClickListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void onSignup() {
		navigateToSignupActivity(APIConstants.GROUP_ID);
	}

	private void onLogin() {
		try {
			if (!validate())
				return;

			if (isInternetEnabled()) {
				LoaderHelper.showLoader(this, "Logging in...", "");
				userName = editTxtUserName.getText().toString();
				password = editTextPassword.getText().toString();
				WebServiceModel.login(userName, password, this);
				// starting Service
				// startService(new Intent(LoginActivity.this,
				// ServiceLocation.class));
			} else

				Toast.makeText(this, "Check your internet connection.",
						Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public boolean isInternetEnabled() {

		ConnectivityManager conMgr = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (conMgr.getActiveNetworkInfo() != null
				&& conMgr.getActiveNetworkInfo().isAvailable()
				&& conMgr.getActiveNetworkInfo().isConnected()) {

			return true;

		}
		return false;
	}

	private void onForgotPassword() {

		userName = editTxtUserName.getText().toString();
		
		if(!Utils.isEmptyOrNull(userName))
		{/*
			Intent i = new Intent(LoginActivity.this,ForgotPassword.class);
			i.putExtra("email", userName);
			startActivity(i);*/
		}
		else
			editTxtUserName.setError("Username cannot be empty");
	}

	private void navigateToSignupActivity(int groupID) {
		try

		{
			Intent intent = new Intent(this, SignupActivity.class);
			intent.putExtra(APIConstants.KEY_GROUP_ID, groupID);
			startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean validate() {
		String userName = editTxtUserName.getText().toString();
		String password = editTextPassword.getText().toString();
		boolean isValid = true;
		if (Utils.isEmptyOrNull(userName)) {
			isValid = false;
			editTxtUserName.setError("Username cannot be empty");
		}

		if (Utils.isEmptyOrNull(password)) {
			isValid = false;
			editTextPassword.setError("Password cannot be empty");
		}

		return isValid;
	}

	@Override
	public void onClick(View v) {
		try {
			switch (v.getId()) {
			case R.id.btnSignup:
				showAlertBox(this, "Account Request",
						"Driver accounts are invite only right now, you can send the request.");
				// onSignup();
				break;
			case R.id.btnLogIn:
				Applog.Debug("login call");
				onLogin();
				break;
			case R.id.btnForgotPassword:
				onForgotPassword();
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	private void sendEmail(String emailAddrs) {
		try {
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("message/rfc822");
			i.putExtra(Intent.EXTRA_EMAIL, new String[] { emailAddrs });
			i.putExtra(Intent.EXTRA_SUBJECT, "Driver account invite request.");
			i.putExtra(Intent.EXTRA_TEXT, "Write your email");
			try {
				startActivity(Intent.createChooser(i, "Send mail..."));
			} catch (android.content.ActivityNotFoundException ex) {
				Toast.makeText(this, "There are no email clients installed.",
						Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showAlertBox(final Activity activity, final String title,
			String Msg) {

		try {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					activity);

			// set title
			alertDialogBuilder.setTitle(title);

			// set dialog message
			alertDialogBuilder
					.setMessage(Msg)
					.setCancelable(true)
					.setPositiveButton("Email now",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// if this button is clicked, close
									// current activity
									// if(title.contains("No Connection Found"))
									sendEmail("info@smarttaxi.ca");
									// activity.finish();
									alertDialog.dismiss();
								}
							});

			alertDialogBuilder
					.setMessage(Msg)
					.setCancelable(true)
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// if this button is clicked, close
									// current activity
									// if(title.contains("No Connection Found"))

									// activity.finish();
									alertDialog.dismiss();
								}
							});

			// create alert dialog
			alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onResponse(CustomHttpResponse object) {

		try {	
			Driver driver = new Driver(this);
			if (LoaderHelper.progressDialog != null) {
				if (LoaderHelper.isLoaderShowing())
					LoaderHelper.hideLoader();
			}
			if (object.getStatusCode() == APIConstants.SUCESS_CODE) {
				if (object.getResponse() instanceof User) {
					User user = (User) object.getResponse();
					FactoryModel.setUser(user);
					driver.Save(user, user != null);
					PreferencesHandler ph = new PreferencesHandler(this);
					ph.setEmail(editTxtUserName.getText().toString());
					long driverUserId = ph.getOriginalDriverUserID();
					String driverUdid = ph.getDriverUdid();
					if (!Utils.isEmptyOrNull(driverUdid)
							&& !Utils.isEmptyOrNull(String
									.valueOf(driverUserId))) {
						LoaderHelper.showLoader(this, "Loading...", "");
						Applog.Debug("Postregistration call");
						WebServiceModel.postRegistrationIdToServer(driverUdid,
								String.valueOf(driverUserId), this);
					} else {
						Intent i = new Intent(this, MainActivity.class);
						startActivity(i);
						finish();
					}
				} else if (object.getResponse() instanceof DeviceRegister) {
					Intent i = new Intent(this, MainActivity.class);
					startActivity(i);
					finish();
				}
			} else if (object.getResponseMsg() != null)
				AppToast.ShowToast(this, object.getResponseMsg());
		} catch (Exception e) {
			e.printStackTrace();
			

		}
	}

	private void registerDeviceToGCM(String userName, String password) {

		try {
			// TODO Auto-generated method stub
			cd = new ConnectionDetector(getApplicationContext());

			// Check if Internet present
			if (!cd.isConnectingToInternet()) {
				// Internet Connection is not present
				AppToast.ShowToast(getBaseContext(),
						"Internet Connection Error");
				// stop executing code by return
				return;
			}

			// Check if GCM configuration is set
			if (SERVER_URL == null || SENDER_ID == null
					|| SERVER_URL.length() == 0 || SENDER_ID.length() == 0) {
				// GCM sernder id / server url is missing
				AppToast.ShowToast(getBaseContext(), "Configuration Error!");
				// stop executing code by return
				return;
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	@Override
	public void onException(CustomHttpException exception) {
		LoaderHelper.hideLoader();
		Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();

	}

}
