package com.smart.taxi.fragments;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.HttpStatus;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;

import com.smart.taxi.R;
import com.smart.taxi.activities.CustomHttpClass;
import com.smart.taxi.activities.SplashActivity;
import com.smart.taxi.components.CFEditText;
import com.smart.taxi.constants.APIConstants;
import com.smart.taxi.entities.CustomHttpException;
import com.smart.taxi.entities.CustomHttpResponse;
import com.smart.taxi.helpers.LoaderHelper;
import com.smart.taxi.utils.CommonUtilities;
import com.smart.taxi.utils.NetworkAvailability;
import com.smart.taxi.utils.Utils;

public class EditProfileFragment extends BaseFragment implements OnCheckedChangeListener {
	
	Button btnUpdateProfile;
	CFEditText tfFirstName, tfLastName, tfPhone;
	String gender;
	private RadioButton rbYes;
	private RadioButton rbNo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		rootView = inflater.inflate(R.layout.activity_edit_profile, container, false);
		initUI();
		return rootView;
	}
	
	private void initUI() {
		//setContentView(R.layout.activity_edit_profile);
		if(SplashActivity.isLoggedIn())
		{
			tfFirstName = (CFEditText)rootView.findViewById(R.id.txtFirstName);
			tfLastName = (CFEditText)rootView.findViewById(R.id.txtLastName);
			tfPhone = (CFEditText)rootView.findViewById(R.id.txtPhone);
			btnUpdateProfile = ((Button)rootView.findViewById(R.id.btnUpdateProfile));
			btnUpdateProfile.setOnClickListener(this);
			tfFirstName.setText(SplashActivity.loggedInUser.getFirstName());
			tfLastName.setText(SplashActivity.loggedInUser.getLastName());
			tfPhone.setText(SplashActivity.loggedInUser.getPhone());
			gender = SplashActivity.loggedInUser.getGender();
			rbYes = ((RadioButton)rootView.findViewById(R.id.radioYes));
			rbNo = ((RadioButton)rootView.findViewById(R.id.radioNo));
			if(gender.equals("Male"))
			{
				rbYes.setChecked(true);
				rbNo.setChecked(false);
			}else{
				rbYes.setChecked(false);
				rbNo.setChecked(true);
			}
			/*rbYes.setOnCheckedChangeListener(this);
			rbNo.setOnCheckedChangeListener(this);
			rbYes.setOnClickListener(this);
			rbNo.setOnClickListener(this);*/
		}
	}

	public void onRadioButtonClicked(View view) {
	    // Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        case R.id.radioYes:
	            if (checked)
	                // Pirates are the best
	            break;
	        case R.id.radioNo:
	            if (checked)
	                // Ninjas rule
	            break;
	    }
	}
	
	@Override
	public void onClick(View btn)
	{
		super.onClick(btn);
		//finish();
		if(btn instanceof RadioButton)
		{
			onRadioButtonClicked(btn);
			return;
		}
		if(NetworkAvailability.IsNetworkAvailable(getActivity().getBaseContext()))
		{
			if(validateForm())
			{
				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair("user_id",SplashActivity.loggedInUser.getId()));
				params.add(new BasicNameValuePair("gender",getGender()));
				params.add(new BasicNameValuePair("name",tfFirstName.getText().toString() + " " + tfLastName.getText().toString()));
				params.add(new BasicNameValuePair("first_name",tfFirstName.getText().toString()));
				params.add(new BasicNameValuePair("last_name",tfLastName.getText().toString()));
				params.add(new BasicNameValuePair("phone",tfPhone.getText().toString()));
				btnUpdateProfile.setOnClickListener(null);
				LoaderHelper.showLoader(getActivity(), "Updating profile...", "");
				CustomHttpClass.runPostService(this, "update_profile.json", params, false, false);
				Log.e("Validated", "true");
			}else{
				Log.e("Validated", "false");
			}
		}else{
			NetworkAvailability.showNoConnectionDialog(getActivity());
		}
		
		/*InputStream iStream = retrieveStream("http://smarttaxi.ca/json.html");
		Gson gson = new Gson();

		Reader reader = new InputStreamReader(iStream);

		  JsonObject response = gson.fromJson(reader, JsonObject.class);
		  JsonObject body = response.get("body").getAsJsonObject();
		  JsonObject arr = body.get("Cabs").getAsJsonObject();
		  Log.e("HTTP Body", arr.toString());
		  for (Entry<String, JsonElement> entry : arr.getAsJsonObject().entrySet()) {
				JsonElement jsonElement = entry.getValue();
				Log.e("Entry", entry.toString());
				//JsonObject journeyUsersObject = JsonHelper.getJsonObject(jsonElement.getAsJsonObject(), "journey_users");
		  }*/
	}
	
	private String getGender() {
		return (((RadioButton)rootView.findViewById(R.id.radioYes)).isChecked()?"Male":"Female");
	}

	@Override
	public void onResponse(CustomHttpResponse response)
	{
		super.onResponse(response);
		LoaderHelper.hideLoader();
		btnUpdateProfile.setOnClickListener(this);
		if(response.getStatusCode() == APIConstants.SUCESS_CODE)
		{
			SplashActivity.loggedInUser.setFirstName(tfFirstName.getText().toString());
			SplashActivity.loggedInUser.setLastName(tfLastName.getText().toString());
			SplashActivity.loggedInUser.setPhone(tfPhone.getText().toString());
			SplashActivity.loggedInUser.setGender(getGender());
			btnUpdateProfile.setOnClickListener(this);
			CommonUtilities.displayAlert(getActivity(), "Your profile has been updated successfully.","Profile updated!", "Ok", "Close", false);
		}else{
			CommonUtilities.displayAlert(getActivity(), "Could not update profile, please try again.","Update failed!", "Retry", "Cancel", true);
		}
		Log.e("Response","Received");
	}
	
	@Override
	public void onException(CustomHttpException exception) {
		super.onException(exception);
		LoaderHelper.hideLoader();
		btnUpdateProfile.setOnClickListener(this);
		CommonUtilities.displayAlert(getActivity(), "Could not update profile, please try again.","Update failed", "Retry", "Cancel", true);
	}
	
	
	private boolean validateForm() {
		// TODO Auto-generated method stub
		if(Utils.isEmptyOrNull(tfFirstName.getText().toString()))
		{
			tfFirstName.setError("First name can not be empty.");
			tfFirstName.requestFocus();
			return false;
		}else if(Utils.isEmptyOrNull(tfLastName.getText().toString()))
		{
			tfLastName.setError("Last name can not be empty.");
			tfLastName.requestFocus();
			return false;
		}
		return true;
	}
	private InputStream retrieveStream(String url) {

		  DefaultHttpClient client = new DefaultHttpClient(); 

		  HttpGet getRequest = new HttpGet(url);

		  try {

		     HttpResponse getResponse = client.execute(getRequest);

		     final int statusCode = getResponse.getStatusLine().getStatusCode();

		     if (statusCode != HttpStatus.SC_OK) { 

		  Log.w(getClass().getSimpleName(), 

		"Error " + statusCode + " for URL " + url); 

		  return null;

		     }

		     HttpEntity getResponseEntity = getResponse.getEntity();

		     return getResponseEntity.getContent();

		  } 

		  catch (IOException e) {

		     getRequest.abort();

		     Log.w(getClass().getSimpleName(), "Error for URL " + url, e);

		  }

		  return null;

		     }

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		// TODO Auto-generated method stub
		
	}

}
