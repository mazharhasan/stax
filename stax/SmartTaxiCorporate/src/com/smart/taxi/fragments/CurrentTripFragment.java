package com.smart.taxi.fragments;


import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;

import com.smarttaxi.client.R;
import com.smart.taxi.activities.ContainerActivity;
import com.smart.taxi.activities.CustomHttpClass;
import com.smart.taxi.activities.SplashActivity;
import com.smart.taxi.components.CFTextView;
import com.smart.taxi.constants.APIConstants;
import com.smart.taxi.entities.CustomHttpException;
import com.smart.taxi.entities.CustomHttpResponse;
import com.smart.taxi.entities.TripDetails;
import com.smart.taxi.helpers.LoaderHelper;
import com.smart.taxi.preferences.PreferencesHandler;
import com.smart.taxi.utils.CommonUtilities;
import com.smart.taxi.utils.NetworkAvailability;
import com.smart.taxi.utils.Utils;

public class CurrentTripFragment extends BaseFragment {

	public static final String TAG = "CurrentTripFragment";
	private CFTextView labelCurrentTrip;
	private LinearLayout llCurrentTripContents;
	private Button btnCancelCurrentTrip;
	private Button btnGotoCurrentTrip;
	private TripDetails currentTrip;
	private View btnGotoHome;
	public CurrentTripFragment() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		rootView = inflater.inflate(R.layout.fragment_current_trip, container, false);
		
		return rootView;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		initUI();
		Log.e("resuming", "adsasdas");
	}
	
	private void initUI() {
		labelCurrentTrip = (CFTextView) rootView.findViewById(R.id.labelCurrentTrip);
		llCurrentTripContents = (LinearLayout) rootView.findViewById(R.id.llCurrentTripContents);
		btnCancelCurrentTrip = (Button) rootView.findViewById(R.id.btnCancelCurrentTrip);
		btnGotoCurrentTrip = (Button) rootView.findViewById(R.id.btnGotoCurrentTrip);
		btnGotoHome = (Button) rootView.findViewById(R.id.btnGotoHome);
		btnCancelCurrentTrip.setOnClickListener(this);
		btnGotoCurrentTrip.setOnClickListener(this);
		btnGotoHome.setOnClickListener(this);
		PreferencesHandler pf = new PreferencesHandler(getActivity().getApplicationContext());
		String tripId = pf.getCurrentTripId();
		if(!Utils.isEmptyOrNull(tripId) && !tripId.equals("0"))
		{
			llCurrentTripContents.setVisibility(View.VISIBLE);
			//labelCurrentTrip.setText("A trip is in process: " + tripId);
			currentTrip = pf.getCurrentTrip();
			if(currentTrip != null)
			{
				labelCurrentTrip.append("Trip requested from: " + currentTrip.getPickupAddress() + "\n");
			}else{
				labelCurrentTrip.append("Loading from server");
			}
		}else{
			llCurrentTripContents.setVisibility(View.INVISIBLE);
			llCurrentTripContents.setVisibility(View.GONE);
			btnGotoHome.setVisibility(View.VISIBLE);
			CommonUtilities.displayAlert(getActivity(), "No current trips found.", "Sorry!", "Close", "cancel", false);
		}
	}
	
	@Override
	public void onClick(View button)
	{
		super.onClick(button);
		switch(button.getId())
		{
		case R.id.btnGotoCurrentTrip:
			SplashActivity.setTripNewDetails(currentTrip);
			SplashActivity.isTripRequested = true;
			((ContainerActivity)getActivity()).selectItem(0);
			break;
			
		case R.id.btnCancelCurrentTrip:
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Confirm!")
			.setMessage("Are you sure you want to cancel thhis trip?")
			.setCancelable(true)
			.setPositiveButton("Yes", new OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("journey_id", SplashActivity.getTripNewDetails().getJourneyId()));
					params.add(new BasicNameValuePair("user_id", SplashActivity.loggedInUser.getId()));
					params.add(new BasicNameValuePair("cancelled_by", "user"));
					LoaderHelper.showLoader(getActivity(), "Cancelling your trip...", "");
					CustomHttpClass.runPostService(CurrentTripFragment.this, APIConstants.METHOD_CANCEL_JOURNEY, params, false, false);
				}
				
			})
			.setNegativeButton("No",null);
			builder.create();
			builder.show();
		break;
		case R.id.btnGotoHome:
			((ContainerActivity)getActivity()).selectItem(0);
			break;
		}
		if(NetworkAvailability.IsNetworkAvailable(getActivity()))
		{
			
		}else{
			NetworkAvailability.showNoConnectionDialog(getActivity());
		}
		
	}
	
	@Override
	public void onResponse(CustomHttpResponse response)
	{
		super.onResponse(response);
		LoaderHelper.hideLoader();
		if(response.getMethodName() == APIConstants.METHOD_CANCEL_JOURNEY && response.getStatusCode() == APIConstants.SUCESS_CODE)
		{
			SplashActivity.isTripRequested = false;
			SplashActivity.setTripNewDetails(null);
			llCurrentTripContents.setVisibility(View.INVISIBLE);
			llCurrentTripContents.setVisibility(View.GONE);
			btnGotoHome.setVisibility(View.VISIBLE);
			PreferencesHandler pf = new PreferencesHandler(getActivity().getApplicationContext());
			pf.clearTrip();
		}else{
			CommonUtilities.displayAlert(getActivity(), "Could not cancel the trip now, please try later.","Failed.", "Retry", "Cancel", false);
		}
	}
	
	@Override
	public void onException(CustomHttpException exception) {
		super.onException(exception);
		LoaderHelper.hideLoader();
		CommonUtilities.displayAlert(getActivity(), "Could not cancel the trip now, please try later.","Failed.", "Retry", "Cancel", false);
	}


}
