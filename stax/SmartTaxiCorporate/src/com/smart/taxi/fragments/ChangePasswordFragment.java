package com.smart.taxi.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;

import com.smarttaxi.client.R;
import com.smart.taxi.activities.CustomHttpClass;
import com.smart.taxi.activities.SplashActivity;
import com.smart.taxi.components.CFEditText;
import com.smart.taxi.components.CFTextView;
import com.smart.taxi.constants.APIConstants;
import com.smart.taxi.entities.CustomHttpException;
import com.smart.taxi.entities.CustomHttpResponse;
import com.smart.taxi.helpers.LoaderHelper;
import com.smart.taxi.utils.CommonUtilities;
import com.smart.taxi.utils.NetworkAvailability;
import com.smart.taxi.utils.Utils;

public class ChangePasswordFragment extends BaseFragment {
	
	Button buttonUpdatePassword;
	CFEditText tfOldPassword, tfNewPassword, tfRetypePassword;
	private CFTextView labelEditProfilePictureInPassword;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		rootView = inflater.inflate(R.layout.activity_change_password, container, false);
		initUI();
		return rootView;
	}
	
	private void initUI() {
		labelEditProfilePictureInPassword = (CFTextView)rootView.findViewById(R.id.labelEditProfilePictureInPassword);
		buttonUpdatePassword = (Button)rootView.findViewById(R.id.buttonUpdatePassword);
		tfOldPassword = (CFEditText)rootView.findViewById(R.id.txtOldPassword);
		tfNewPassword = (CFEditText)rootView.findViewById(R.id.txtNewPassword);
		tfRetypePassword = (CFEditText)rootView.findViewById(R.id.txtRetypePassword);
		buttonUpdatePassword.setOnClickListener(this);
		labelEditProfilePictureInPassword.setText(SplashActivity.loggedInUser.getFullName());
	}

	@Override
	public void onClick(View button)
	{
		super.onClick(button);
		if(NetworkAvailability.IsNetworkAvailable(getActivity()))
		{
			if(validateForm())
			{
				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair("old_password",tfOldPassword.getText().toString()));
				params.add(new BasicNameValuePair("email",SplashActivity.loggedInUser.getUserName()));
				params.add(new BasicNameValuePair("password",tfNewPassword.getText().toString()));
				buttonUpdatePassword.setOnClickListener(null);
				LoaderHelper.showLoader(getActivity(), "Changing password...", "");
				CustomHttpClass.runPostService(this, "update_password.json", params, false, false);
				Log.e("Validated", "true");
			}else{
				Log.e("Validated", "false");
			}
		}else{
			NetworkAvailability.showNoConnectionDialog(getActivity());
		}
		
	}

	private boolean validateForm() {
		if((!Utils.isEmptyOrNull(tfOldPassword.getText().toString())
				&& !Utils.isEmptyOrNull(tfNewPassword.getText().toString())
				&& !Utils.isEmptyOrNull(tfRetypePassword.getText().toString())
				))
		{
			if(tfOldPassword.getText().toString().equals(SplashActivity.loggedInUser.getPassword()))
			{
				if(!tfRetypePassword.getText().toString().equals(tfNewPassword.getText().toString()))
				{
					CommonUtilities.displayAlert(getActivity(), "Your new password and retyped password did not match.", "Invalid new password", "Retry", "", false);
					return false;
				}else{
					return true;
				}
			}else{
				CommonUtilities.displayAlert(getActivity(), "Your have provided an invalid current password.", "Invalid password", "Retry", "", false);
				return false;
			}
			
		}else{
			if(Utils.isEmptyOrNull(tfOldPassword.getText().toString()))
			{
				tfOldPassword.setError("Please provide current password.");
				tfOldPassword.requestFocus();
				return false;
			}
			
			if(Utils.isEmptyOrNull(tfNewPassword.getText().toString()))
			{
				tfNewPassword.setError("Please provide a new password.");
				tfNewPassword.requestFocus();
				return false;
			}
			
			if(Utils.isEmptyOrNull(tfRetypePassword.getText().toString()))
			{
				tfRetypePassword.setError("Please retype your new password.");
				tfRetypePassword.requestFocus();
				return false;
			}
		}
		return false;
	}
	
	@Override
	public void onResponse(CustomHttpResponse response)
	{
		super.onResponse(response);
		LoaderHelper.hideLoader();
		if(response.getStatusCode() == APIConstants.SUCESS_CODE)
		{
			SplashActivity.loggedInUser.setPassword(tfNewPassword.getText().toString());
			buttonUpdatePassword.setOnClickListener(this);
			Log.e("New password", SplashActivity.loggedInUser.getPassword());
			CommonUtilities.displayAlert(getActivity(), "Password changed successfully.", "Success", "Ok", "Retry", false);
			//setResult(0,returnIntent);  
			//finish();
		}else{
			CommonUtilities.displayAlert(getActivity(), "Could not change password, make sure your old password is correct and try again","Invalid old password.", "Retry", "Cancel", true);
		}
	}
	
	@Override
	public void onException(CustomHttpException exception) {
		super.onException(exception);
		LoaderHelper.hideLoader();
		CommonUtilities.displayAlert(getActivity(), "Could not change password, make sure your old password is correct and try again","Invalid old password.", "Retry", "Cancel", true);
	}

}
