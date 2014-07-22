package com.smart.taxi.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;

import com.smart.taxi.R;
import com.smart.taxi.activities.CustomHttpClass;
import com.smart.taxi.activities.SplashActivity;
import com.smart.taxi.components.CFEditText;
import com.smart.taxi.constants.APIConstants;
import com.smart.taxi.entities.CustomHttpException;
import com.smart.taxi.entities.CustomHttpResponse;
import com.smart.taxi.helpers.LoaderHelper;
import com.smart.taxi.preferences.PreferencesHandler;
import com.smart.taxi.utils.CommonUtilities;
import com.smart.taxi.utils.NetworkAvailability;

public class ChangeTipFragment extends BaseFragment{
	public static final String TAG = "changeTipAcivity";
	private RadioButton[] tipOptions = new RadioButton[5];
	private LinearLayout lt;
	private CFEditText txtCustomTip;
	private boolean isCustomTip;
	private String userTip = "0";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		userTip = SplashActivity.loggedInUser.getTip();
		if(isCustomTip)
		{
			clearButtons(R.id.radioTipCustom);
			txtCustomTip.setText(userTip);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		rootView = inflater.inflate(R.layout.activity_change_tip, container, false);
		if(SplashActivity.isLoggedIn())
		{
			initUI();
		}else{
			getActivity().onBackPressed();
		}
		return rootView;
	}
	
	private void initUI() {
		fillRadioButtons();
		txtCustomTip = (CFEditText) rootView.findViewById(R.id.txtCustomTip);
		lt = (LinearLayout) rootView.findViewById(R.id.lvCustomTip);
		Button btnSaveTip = (Button)rootView.findViewById(R.id.btnSaveTipPreferences);
		btnSaveTip.setOnClickListener(this);		
	}

	private void fillRadioButtons() {
		tipOptions[0] = (RadioButton)rootView.findViewById(R.id.radioTipZero);
		tipOptions[1] = (RadioButton)rootView.findViewById(R.id.radioTip15);
		tipOptions[2] = (RadioButton)rootView.findViewById(R.id.radioTip20);
		tipOptions[3] = (RadioButton)rootView.findViewById(R.id.radioTip25);
		tipOptions[4] = (RadioButton)rootView.findViewById(R.id.radioTipCustom);
		setupRadioListener();
	}

	private void setupRadioListener() {
		userTip = SplashActivity.loggedInUser.getTip();
		Log.e("Tip value:", userTip);
		isCustomTip = true;
		for(int i = 0; i < tipOptions.length; i++)
		{
			if(tipOptions[i].getTag().toString().equals(userTip))
			{
				isCustomTip = false;
				tipOptions[i].setChecked(true);
			}
			tipOptions[i].setOnClickListener(this);
		}
		
	}

	public void onRadioButtonClicked(View view) {
	    // Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    // Check which radio button was clicked

	    switch(view.getId()) {
	        case R.id.radioTipCustom:
	        	clearButtons(R.id.radioTipCustom);
	            if (checked){
	                lt.setVisibility(View.VISIBLE);
	            }
	            	
	            break;
	        default:
	        	clearButtons(view.getId());
	            if (checked){
	            	lt.setVisibility(View.GONE);
	            }
	    }
	}
	
	private void clearButtons(int radio) {
		for(int i = 0; i < 5; i++)
		{
			if(tipOptions[i].getId() != radio)
			{
				tipOptions[i].setChecked(false);
			}
		}
		
	}

	@Override
	public void onClick(View view) {
		if (view instanceof RadioButton) {
			onRadioButtonClicked(view);
			return;
		}
		if(view.getId() == R.id.btnSaveTipPreferences)
		{
			if(NetworkAvailability.IsNetworkAvailable(getActivity()))
					{
						List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
						params.add(new BasicNameValuePair("user_id", SplashActivity.loggedInUser.getId()));
						params.add(new BasicNameValuePair("tip", getTipValue()));
						LoaderHelper.showLoader(getActivity(), "Changing tip...", "");
						CustomHttpClass.runPostService(this, APIConstants.METHOD_POST_UPDATE_TIP, params, false, false);
					}else{
						NetworkAvailability.showNoConnectionDialog(getActivity());
					}
		}
		
	}

	private String getTipValue() {
		String tipValue = "0";
		if(lt.getVisibility() == View.VISIBLE)
		{
			tipValue = txtCustomTip.getText().toString();
		}
		for(int i = 0; i < 5; i++)
		{
			if(tipOptions[i].isChecked())
			{
				if(tipOptions[i].getTag().toString().equals("0"))
				{
					tipValue = txtCustomTip.getText().toString();
					break;
				}
				else
				{
					tipValue = tipOptions[i].getTag().toString();
					break;
				}
			}
		}
		
		return tipValue;
	}
	
	@Override
	public void onResponse(CustomHttpResponse object) {
		LoaderHelper.hideLoaderSafe();
		if(object.getStatusCode() == 0 && object.getMethodName() == APIConstants.METHOD_POST_UPDATE_TIP)
		{
			SplashActivity.loggedInUser.setTip(getTipValue());
			PreferencesHandler pf = new PreferencesHandler(getActivity().getApplicationContext());
			pf.setTip(getTipValue());
			CommonUtilities.displayAlert(getActivity(), "Your tip has been updated successfully.", "Success!", "Ok", "Close", false);
		}else{
			CommonUtilities.displayAlert(getActivity(), "Could not update tip at this time, please try later.", "Failed!", "Ok", "Close", false);
		}

	}

	@Override
	public void onException(CustomHttpException exception) {
		LoaderHelper.hideLoaderSafe();
		CommonUtilities.displayAlert(getActivity(), "Could not update tip at this time, please try later.", "Failed!", "Ok", "Close", false);

	}
}
