package com.smarttaxi.driver.activities;

import com.smarttaxi.driver.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

public class DrawerCurrentTripActivity extends Activity {

	@SuppressLint("ShowToast")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_drawer_current_trip);
	
	Button call = (Button)findViewById(R.id.call);
	//call.setEnabled(false);
		//setContentView(R.layout.journey_trip_completed);
		//setContentView(R.layout.passenger_request);
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}
}
