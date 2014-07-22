package com.smarttaxi.driver.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.smarttaxi.driver.R;
import com.smarttaxi.driver.constants.APIConstants;
import com.smarttaxi.driver.entities.CustomHttpException;
import com.smarttaxi.driver.entities.CustomHttpResponse;
import com.smarttaxi.driver.helpers.LoaderHelper;
import com.smarttaxi.driver.interfaces.HttpResponseListener;
import com.smarttaxi.driver.model.FactoryModel;
import com.smarttaxi.driver.model.WebServiceModel;
import com.smarttaxi.driver.preferences.PreferencesHandler;
import com.smarttaxi.driver.utils.AppToast;
import com.smarttaxi.driver.utils.Common;
import com.smarttaxi.driver.components.CFEditText;

public class ChangePassword extends Activity implements OnClickListener, HttpResponseListener{
	private CFEditText edtOldPassword, edtPassword, edtConfirmPassword;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_changepassword);
		((Button)findViewById(R.id.bttSave)).setOnClickListener(this);
		edtConfirmPassword = (CFEditText) findViewById(R.id.edtConfirmPassword);
		edtPassword = (CFEditText) findViewById(R.id.edtPassword);
		edtOldPassword = (CFEditText) findViewById(R.id.edtOldPassword);
	}

	private void onChangePassword() {
		if(!validate())
			return;
		
		LoaderHelper.showLoader(this, "Saving new password", "");
		String oldPassword = edtOldPassword.getText().toString();
		String password = edtPassword.getText().toString();
		String email = new PreferencesHandler(getApplicationContext()).getEmail();
		WebServiceModel.changePassword(email, password,oldPassword, this);
	}
	
	private boolean validate() {
		String oldPassword = edtOldPassword.getText().toString();
		String confirmPassword = edtConfirmPassword.getText().toString();
		String password = edtPassword.getText().toString();
		if(oldPassword == null || oldPassword.equalsIgnoreCase("")){
			Common.showToast(this, "Password is missing");
			return false;
		}
		else if(password == null || password.equalsIgnoreCase("")){
			Common.showToast(this, "New Password is missing");
			return false;
		}
		else if(password.equals(confirmPassword) == false){
			Common.showToast(this, "Password doesnot match.");
			return false;
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bttSave:
			onChangePassword();
			break;

		default:
			break;
		}
	}

	@Override
	public void onResponse(CustomHttpResponse object) {
		if(object.getStatusCode() == APIConstants.SUCESS_CODE){
			finish();
		}
		else
			AppToast.ShowToast(this, object.getResponseMsg());
		LoaderHelper.hideLoader();
	}

	@Override
	public void onException(CustomHttpException exception) {
		// TODO Auto-generated method stub
		
	}

}
