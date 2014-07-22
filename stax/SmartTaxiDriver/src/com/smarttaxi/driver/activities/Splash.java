package com.smarttaxi.driver.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.smarttaxi.driver.R;
import com.smarttaxi.driver.constants.APIConstants;
import com.smarttaxi.driver.helpers.AlertBoxHelper;
import com.smarttaxi.driver.preferences.PreferencesHandler;

public class Splash extends Activity implements OnClickListener {

	private Button btnLogin;
	private Button btnSignup;
	private PreferencesHandler preferencesHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		// initUI();
		startService(new Intent(this, ServiceLocation.class));
		
		ConnectivityManager conMgr = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		preferencesHandler = new PreferencesHandler(this);
		if (preferencesHandler.getUserID() <= 0)
		{
			startActivity(new Intent(this, LoginActivity.class));
			finish();
		}
		else if (conMgr.getActiveNetworkInfo() != null
				&& conMgr.getActiveNetworkInfo().isAvailable()
				&& conMgr.getActiveNetworkInfo().isConnected())
		{
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}
		else
		{
			AlertBoxHelper.showAlertBox(this, "No Connection Found", "App is closing due to no internet connection found");
		}
		
	}

	

	private void initUI() {

		btnLogin = (Button) findViewById(R.id.btnLogIn);
		btnSignup = (Button) findViewById(R.id.btnSignup);

		btnLogin.setOnClickListener(this);
		btnSignup.setOnClickListener(this);

	}

	private void onSignup() {
		navigateToSignupActivity(APIConstants.GROUP_ID);
	}

	private void onLogin() {
		startActivity(new Intent(this, LoginActivity.class));
		finish();
	}

	private void navigateToSignupActivity(int groupID) {
		Intent intent = new Intent(this, SignupActivity.class);
		intent.putExtra(APIConstants.KEY_GROUP_ID, groupID);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnSignup:
			onSignup();
			break;
		case R.id.btnLogIn:
			onLogin();
			break;
		default:
			break;
		}

	}
}
