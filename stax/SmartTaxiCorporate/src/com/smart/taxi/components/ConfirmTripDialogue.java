package com.smart.taxi.components;

import java.util.ArrayList;
import java.util.List;

import com.smart.taxi.activities.SplashActivity;
import com.smart.taxi.entities.Cab;
import com.smart.taxi.fragments.FindARideFragment;
import com.smart.taxi.utils.Utils;
import com.smarttaxi.client.R;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

@SuppressLint("ValidFragment")
public class ConfirmTripDialogue extends DialogFragment implements OnClickListener {
	private View rootView;
	private Spinner spCards;
	private Cab cab;
	private String numBags;
	private String numPassengers;
	private String currentAddress;
	private FindARideFragment parent;
	public ConfirmTripDialogue(Cab cab, String currentAddress, String numPassengers, String numBags) {
		// TODO Auto-generated constructor stub
		this.currentAddress = currentAddress;
		this.cab = cab;
		this.numPassengers = numPassengers;
		this.numBags = numBags;
	}

	@Override
	public void dismissAllowingStateLoss() {
		// TODO Auto-generated method stub
		
		Log.e("Confir trip Dialogue", "dismissAllowingStateLoss()");
		super.dismissAllowingStateLoss();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setStyle(DialogFragment.STYLE_NORMAL, R.style.JourneyRequestTheme);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.dialogue_confirm_trip, container);
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		initUI();
		return rootView;
	}
	
	private void initUI() {
		spCards = (Spinner) rootView.findViewById(R.id.spCards);
		List<String> cards = new ArrayList<String>();
		for(int i = 0; i < SplashActivity.loggedInUser.getUserCards().size(); i++)
		{
			cards.add(SplashActivity.loggedInUser.getUserCards().get(i).getCardNumber());
		}
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_card_layout, cards);
		dataAdapter.setDropDownViewResource(R.layout.spinner_popup_layout);
		spCards.setAdapter(dataAdapter);
		
		((CFTextView) rootView.findViewById(R.id.txtDriverName)).setText(cab.getDriver().getName());
		((CFTextView) rootView.findViewById(R.id.txtDriverCompany)).setText(cab.getCabProvider().getName());
		((CFTextView) rootView.findViewById(R.id.txtNumPeople)).setText(numPassengers);
		((CFTextView) rootView.findViewById(R.id.txtNumBags)).setText(numBags);
		((CFTextView) rootView.findViewById(R.id.txtPickUpAddress)).setText(currentAddress);
		((Button) rootView.findViewById(R.id.btnConfirmCustomerTrip)).setOnClickListener(this);
		
	}
	

	public void setParent(FindARideFragment parent) {
		// TODO Auto-generated method stub
		this.parent = parent;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String cardNumber = (String) spCards.getSelectedItem();
		if(!Utils.isEmptyOrNull(cardNumber)){
			parent.customerApprovalResult(cardNumber, cab.getDriver().getCabID());
			dismiss();
		}
	}
}
