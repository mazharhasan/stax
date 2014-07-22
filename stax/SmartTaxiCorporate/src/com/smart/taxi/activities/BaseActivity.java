package com.smart.taxi.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.smart.taxi.entities.CustomHttpException;
import com.smart.taxi.entities.CustomHttpResponse;
import com.smart.taxi.interfaces.HttpResponseListener;

public class BaseActivity extends FragmentActivity implements OnClickListener, HttpResponseListener {

	public BaseActivity() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
	    //getActionBar().hide();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		// ActionBar actionBar = getActionBar();
		// actionBar.hide();
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
		return super.onOptionsItemSelected(item);
	}
	

	@Override
	public void onResponse(CustomHttpResponse object) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onException(CustomHttpException exception) {
		// TODO Auto-generated method stub
		
	}

}
