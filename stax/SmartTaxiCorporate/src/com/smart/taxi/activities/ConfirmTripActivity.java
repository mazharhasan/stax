package com.smart.taxi.activities;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.smart.taxi.components.CFTextView;
import com.smarttaxi.client.R;

public class ConfirmTripActivity extends BaseActivity {

	private Spinner spCards;
	private String cabId;
	private String driverName;
	private String driverCompanyName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogue_confirm_trip);
		Bundle extras = getIntent().getExtras();
		cabId = extras.get("cabId").toString();
		driverName = extras.get("driverName").toString();
		driverCompanyName = extras.get("driverCompanyName").toString();
		initUI();
	}
	
	private void initUI() {
		getActionBar().hide();
		spCards = (Spinner) findViewById(R.id.spCards);
		List<String> cards = new ArrayList<String>();
		for(int i = 0; i < SplashActivity.loggedInUser.getUserCards().size(); i++)
		{
			cards.add(SplashActivity.loggedInUser.getUserCards().get(i).getCardNumber());
		}
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_card_layout, cards);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spCards.setAdapter(dataAdapter);
		
		((CFTextView) findViewById(R.id.txtDriverName)).setText(driverName);
		((CFTextView) findViewById(R.id.txtDriverCompany)).setText(driverCompanyName);
		
	}

	@Override
	public void onBackPressed()
	{
		finish();
		return;
	}
}
