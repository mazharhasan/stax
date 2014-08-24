package com.smarttaxi.client;

import java.util.ArrayList;
import java.util.List;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;
import com.google.android.gms.internal.ex;
import com.smarttaxi.client.R;
import com.smart.taxi.activities.BaseActivity;
import com.smart.taxi.activities.CustomHttpClass;
import com.smart.taxi.activities.SplashActivity;
import com.smart.taxi.components.CFEditText;
import com.smart.taxi.constants.APIConstants;
import com.smart.taxi.entities.CustomHttpException;
import com.smart.taxi.entities.CustomHttpResponse;
import com.smart.taxi.fragments.BaseFragment;
import com.smart.taxi.helpers.LoaderHelper;
import com.smart.taxi.interfaces.HttpResponseListener;
import com.smart.taxi.utils.CommonUtilities;
import com.smart.taxi.utils.NetworkAvailability;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.os.Build;

public class VerifyAccountActivuty extends BaseActivity {

		@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_verify_account_activuty);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		
	}

	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends BaseFragment {

		private String newEMail;
		
		private CFEditText txtVerifyCode;
		private Button btnVerifyAccount;
		private Button btnResendCode;

		private String newPassword;
		
		public PlaceholderFragment() {
		}
		
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			rootView = inflater
					.inflate(R.layout.fragment_verify_account_activuty,
							container, false);
			Bundle extras = getActivity().getIntent().getExtras();
			if(extras != null && extras.get("email").toString() != null)
			{
				newEMail = extras.get("email").toString();
				newPassword = extras.get("password").toString();
				Log.e("new email:", newEMail);
			}else{
				getActivity().finish();
			}
			initUI();
			return rootView;
		}
		
		private void initUI() {
			// TODO Auto-generated method stub
			txtVerifyCode = (CFEditText) rootView.findViewById(R.id.txtVerifyAccountCode);
			btnVerifyAccount = (Button) rootView.findViewById(R.id.btnVerifyAccount);
			btnVerifyAccount.setOnClickListener(this);
			
		}

		@Override
		public void onClick(View view) {
			if(txtVerifyCode.getText().length() <= 0)
			{
				txtVerifyCode.setError("Please enter the verification code.");
				txtVerifyCode.requestFocus();
				return;
			}
			if(NetworkAvailability.IsNetworkAvailable(getActivity().getBaseContext()))
			{
				txtVerifyCode.setError(null);
				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair("email", newEMail));
				params.add(new BasicNameValuePair("verification_code", txtVerifyCode.getText().toString()));
				LoaderHelper.showLoader(getActivity(), "Please wait while we are verifying your account...,","");
				btnVerifyAccount.setOnClickListener(null);
				CustomHttpClass.runPostService(this, APIConstants.METHOD_VERIFY_ACCOUNT, params, false, false);

			}else{
				NetworkAvailability.showNoConnectionDialog(getActivity());
			}
		}
		
		@Override
		public void onResponse(CustomHttpResponse object) {
			btnVerifyAccount.setOnClickListener(this);
			LoaderHelper.hideLoaderSafe();
			if(object.getStatusCode() == 2123)
			{
				CommonUtilities.displayAlert(getActivity(), "Invalid verification code", "", "Change code", "", false);
			}else if(object.getStatusCode() == 0 
					|| 
					//if already activated
					object.getStatusCode() == 2122){
				SplashActivity.doPrepareForLogin(newEMail, newPassword);
				getActivity().finish();
			}
		}
		
		@Override
		public void onException(CustomHttpException exception) {
		{
			btnVerifyAccount.setOnClickListener(this);
			LoaderHelper.hideLoaderSafe();
			Log.e("Exception", exception.getMethodName());
		}
	}


	}
}
