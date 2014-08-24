package com.smart.taxi.activities;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.smarttaxi.client.R;

public class ConfirmTripActivity extends BaseActivity {

	private Spinner spCards;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogue_confirm_trip);
		initUI();
	}
	
	private void initUI() {
		spCards = (Spinner) findViewById(R.id.spCards);
		List<String> cards = new ArrayList<String>();
		for(int i = 0; i < SplashActivity.loggedInUser.getUserCards().size(); i++)
		{
			cards.add(SplashActivity.loggedInUser.getUserCards().get(i).getCardNumber());
		}
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cards);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spCards.setAdapter(dataAdapter);
	}

	@Override
	public void onBackPressed()
	{
		finish();
		return;
	}
}
