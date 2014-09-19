package com.smart.taxi.activities;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;

import com.smart.taxi.components.CFEditText;
import com.smart.taxi.constants.APIConstants;
import com.smart.taxi.entities.CustomHttpException;
import com.smart.taxi.entities.CustomHttpResponse;
import com.smart.taxi.helpers.LoaderHelper;
import com.smart.taxi.utils.CommonUtilities;
import com.smart.taxi.utils.NetworkAvailability;
import com.smart.taxi.utils.Utils;
import com.smarttaxi.client.R;

public class ForgetPasswordActivity extends BaseActivity {

	private CFEditText tfEmail;
	private Button btnForgotPasswordSend;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_password);
		tfEmail = (CFEditText)findViewById(R.id.txtForgetPasswordEmail);
		btnForgotPasswordSend = (Button) findViewById(R.id.btnForgetPasswordSend);
		btnForgotPasswordSend.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View view)
	{
		if(validate())
		{
			if(NetworkAvailability.IsNetworkAvailable(this))
			{
				btnForgotPasswordSend.setOnClickListener(null);
				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair("email", tfEmail.getText().toString()));
				CustomHttpClass.runPostService(this, APIConstants.METHOD_POST_FORGOT_PASSWORD, params, false, false);
				LoaderHelper.showLoader(this, "Please wait while sending instructions...", "");
			}else{
				NetworkAvailability.showNoConnectionDialog(this);
			}
		}else{
			tfEmail.setError("Invalid email address");
			tfEmail.requestFocus();
		}
	}
	
	@Override
	public void onResponse(CustomHttpResponse object) {
		btnForgotPasswordSend.setOnClickListener(this);
		LoaderHelper.hideLoaderSafe();
	}
	
	@Override
	public void onException(CustomHttpException exception) {
		btnForgotPasswordSend.setOnClickListener(this);
		LoaderHelper.hideLoaderSafe();
	}
	
	private boolean validate()
	{
		if(Utils.isEmptyOrNull(tfEmail.getText().toString()) || !android.util.Patterns.EMAIL_ADDRESS.matcher(tfEmail.getText().toString()).matches())
		{
			return false;
		}else{
			return true;
		}
	}

}
