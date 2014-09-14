package com.smarttaxi.client;

import java.util.ArrayList;
import java.util.List;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;
import com.smarttaxi.client.R;
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
import com.smart.taxi.utils.Utils;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;

public class SignUpActivity extends Activity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(true);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{    
	   switch (item.getItemId()) 
	   {        
	      case android.R.id.home:            
	         onBackPressed();   
	         return true;        
	      default:            
	         return super.onOptionsItemSelected(item);    
	   }
	}
	
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends BaseFragment
	{

		private CFEditText txtFirstName;
		private CFEditText txtLastName;
		private CFEditText txtEMail;
		private CFEditText txtPassword;
		private CFEditText txtPhone;
		private RadioButton rbMale;
		private RadioButton rbFemale;
		private Button btnCreateAccount;
		private String gender;
		private Button btnFBLogin;

		public PlaceholderFragment() {
		}
		public View getView()
		{
			return rootView;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			rootView = inflater.inflate(R.layout.fragment_sign_up,
					container, false);
			initUI();
			return rootView;
		}
		private void initUI() {
			// TODO Auto-generated method stub
			btnFBLogin = (Button) rootView.findViewById(R.id.btnFBLogin);
			btnFBLogin.setCompoundDrawablesWithIntrinsicBounds(R.drawable.fb_icon, 0, 0, 0);
			btnFBLogin.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.fb_icon_padding));
			btnFBLogin.setOnClickListener(this);
			btnCreateAccount = (Button) rootView.findViewById(R.id.btnCreateAccount);
			btnCreateAccount.setOnClickListener(this);
			txtFirstName = (CFEditText) rootView.findViewById(R.id.txtSignUpFirstName);
			txtLastName = (CFEditText) rootView.findViewById(R.id.cFLastName);
			txtEMail = (CFEditText) rootView.findViewById(R.id.txtSignUpEmail);
			txtPassword = (CFEditText) rootView.findViewById(R.id.txtSignUpPassword);
			txtPhone = (CFEditText) rootView.findViewById(R.id.txtSignUpPhone);
			rbMale = (RadioButton) rootView.findViewById(R.id.rbMale);
			rbFemale = (RadioButton) rootView.findViewById(R.id.rbFemale);
		}
		
		@Override
		public void onResponse(CustomHttpResponse object) {
			LoaderHelper.hideLoaderSafe();
			btnCreateAccount.setOnClickListener(this);
			if(object.getStatusCode() == 0 || object.getStatusCode() == 1092)
			{
				Intent verifyAccountIntent = new Intent(getActivity(), VerifyAccountActivuty.class);
				verifyAccountIntent.putExtra("email", txtEMail.getText().toString());
				verifyAccountIntent.putExtra("password", txtPassword.getText().toString());
				getActivity().startActivity(verifyAccountIntent);
				getActivity().overridePendingTransition(R.anim.slide_left, R.anim.slide_left);
				getActivity().finish();
			}else if(object.getStatusCode() == 1094)
			{
				//already exisits
				CommonUtilities.displayAlert(getActivity(), "An account with same email address already exists", "Duplicate found!", "Change email", "", false);
			}
			Log.e("Response", object.getRawJson());
			
		}
		@Override
		public void onException(CustomHttpException exception) {
			LoaderHelper.hideLoaderSafe();
			btnCreateAccount.setOnClickListener(this);
			//Log.e("Error", exception.toString());
			
		}
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnCreateAccount:
				if(validateForm())
				{
					Log.e("Form", "Valid form");
					if(NetworkAvailability.IsNetworkAvailable(getActivity().getBaseContext()))
					{
						List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
						params.add(new BasicNameValuePair("first_name",txtFirstName.getText().toString()));
						params.add(new BasicNameValuePair("last_name",txtLastName.getText().toString()));
						params.add(new BasicNameValuePair("email",txtEMail.getText().toString()));
						params.add(new BasicNameValuePair("confirm_password",txtPassword.getText().toString()));
						params.add(new BasicNameValuePair("password",txtPassword.getText().toString()));
						params.add(new BasicNameValuePair("phone",txtPhone.getText().toString()));
						params.add(new BasicNameValuePair("group_id","4"));
						params.add(new BasicNameValuePair("gender",gender));
						btnCreateAccount.setOnClickListener(null);
						CommonUtilities.hideSoftKeyboard(getActivity());
						LoaderHelper.showLoader(getActivity(), "Please wait while we sign you up...", "");
						CustomHttpClass.runPostService(this, APIConstants.METHOD_POST_REGISTER_USER, params, false, false);
					}else{
						NetworkAvailability.showNoConnectionDialog(getActivity());
					}
				}
				break;
				
			case R.id.btnFBLogin:
				CommonUtilities.displayAlert(getActivity(), "Facebook login will be available soon", "Coming soon", "Ok", "", false);
				break;
				
			default:
				break;
			}
		}
		
		private boolean validateForm() {
			// TODO Auto-generated method stub
			if(Utils.isEmptyOrNull(txtFirstName.getText().toString()))
			{
				txtFirstName.setError("First name can not be empty.");
				txtFirstName.requestFocus();
				return false;
			}else if(Utils.isEmptyOrNull(txtLastName.getText().toString()))
			{
				txtLastName.setError("Last name can not be empty.");
				txtLastName.requestFocus();
				return false;
			}else if(Utils.isEmptyOrNull(txtEMail.getText().toString())
					||
					!android.util.Patterns.EMAIL_ADDRESS.matcher(txtEMail.getText().toString()).matches())
			{
				txtEMail.setError("Email address is empty or in invalid format.");
				txtEMail.requestFocus();
				return false;
			}
			else if(Utils.isEmptyOrNull(txtPassword.getText().toString()) 
					||
					txtPassword.getText().toString().length() < 4)
			{
				txtPassword.setError("Password should be at least 4 charectors.");
				txtPassword.requestFocus();
				return false;
			}
			else if(Utils.isEmptyOrNull(txtPhone.getText().toString()))
			{
				txtPhone.setError("Phone number can not be empty.");
				txtPhone.requestFocus();
				return false;
			}else if(rbMale.isChecked() || rbFemale.isChecked()){
				gender = rbFemale.isChecked()?"Female":"Male"; 
				return true;
			}else{
				rbFemale.setError("Please specify your gender");
				return false;
			}
		}
	}

}
