package com.smarttaxi.driver.activities;
import com.smarttaxi.driver.utils.NetworkAvailability;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class BroadCastActivity extends Activity  {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		String activityCall = getIntent().getExtras().getString("CallFrom");
		
		if(activityCall.equalsIgnoreCase("NetworkActivity"))
			NetworkAvailability.showNoConnectionDialog(this);
		
		else if(activityCall.equalsIgnoreCase("GpsActivity"))
			NetworkAvailability.showNoGpsDialog(this);
		
		
		
		
//		this.finish();
	}
	
	@Override
	protected void onResume() {
	    // TODO Auto-generated method stub
	    super.onResume();
	}


	@Override
	protected void onPause() {
	    // TODO Auto-generated method stub
	    super.onPause();
	}
	
	
	@Override
	protected void onDestroy() {
	    // TODO Auto-generated method stub
	    super.onDestroy();
	}
	
}
