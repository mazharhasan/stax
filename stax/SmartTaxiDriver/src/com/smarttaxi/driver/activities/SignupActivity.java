package com.smarttaxi.driver.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.smarttaxi.driver.R;
import com.smarttaxi.driver.constants.APIConstants;
import com.smarttaxi.driver.entities.CustomHttpException;
import com.smarttaxi.driver.entities.CustomHttpResponse;
import com.smarttaxi.driver.helpers.LoaderHelper;
import com.smarttaxi.driver.interfaces.HttpResponseListener;
import com.smarttaxi.driver.utils.Utils;

public class SignupActivity extends Activity implements OnClickListener,
		HttpResponseListener {

	EditText editTextFirstName;
	EditText editTextLastName;
	EditText editTextPhone;
	EditText editTextEmail;
	EditText editTextPassword;
	EditText editTextConfirmPassword;
	RadioButton radioButtonMale;
	RadioButton radioButtonFemale;
	Button btnSignup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_signup);
		initUI();
	}

	private void initUI() {
		editTextFirstName = (EditText) findViewById(R.id.editTxtFirstName);
		editTextLastName = (EditText) findViewById(R.id.editTxtLastName);
		editTextPhone = (EditText) findViewById(R.id.editTxtPhone);
		editTextEmail = (EditText) findViewById(R.id.editTxtEmail);
		editTextPassword = (EditText) findViewById(R.id.editTxtEnterPassword);
		editTextConfirmPassword = (EditText) findViewById(R.id.editTxtConfirmPassword);
		radioButtonFemale = (RadioButton) findViewById(R.id.radioFemale);
		radioButtonMale = (RadioButton) findViewById(R.id.radioMale);
		radioButtonMale.setChecked(true);
		btnSignup = (Button) findViewById(R.id.btnSignup);
		btnSignup.setOnClickListener(this);

	}

	private void onSignup() {

		if (!validate())
			return;

		LoaderHelper.showLoader(this, "Signing Up..", "");
		String firstName = editTextFirstName.getText().toString();
		String lastName = editTextLastName.getText().toString();
		String email = editTextEmail.getText().toString();
		String password = editTextPassword.getText().toString();
		String groupId = String.valueOf(APIConstants.GROUP_ID);

		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.logo);
//		WebServiceModel.signup(firstName, lastName, email, password, groupId, bitmap, "hello.jpg", this);
	}

	private boolean validate() {
		String firstName = editTextFirstName.getText().toString();
		String lastName = editTextLastName.getText().toString();
		String email = editTextEmail.getText().toString();
		String password = editTextPassword.getText().toString();
		String confirmPassword = editTextConfirmPassword.getText().toString();
		String phoneNumber = editTextPhone.getText().toString();

		boolean isValid = true;

		if (Utils.isEmptyOrNull(firstName)) {
			isValid = false;
			editTextFirstName.setError("First name cannot be empty");
		}

		if (Utils.isEmptyOrNull(lastName)) {
			isValid = false;
			editTextLastName.setError("Last name cannot be empty");
		}

		if (Utils.isEmptyOrNull(email)) {
			isValid = false;
			editTextEmail.setError("Email cannot be empty");
		}

		if (Utils.isEmptyOrNull(password)) {
			isValid = false;
			editTextPassword.setError("Password cannot be empty");
		}

		if (Utils.isEmptyOrNull(confirmPassword)) {
			isValid = false;
			editTextConfirmPassword
					.setError("Confirm Password cannot be empty");
		}

		if (Utils.isEmptyOrNull(phoneNumber)) {
			isValid = false;
			editTextPhone.setError("Phone number cannot be empty");
		}

		if (!password.equals(confirmPassword)) {
			isValid = false;
			editTextConfirmPassword
					.setError("Password & Confirm Password do not match");
		}

		return isValid;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnSignup:
			onSignup();
			break;

		default:
			break;
		}

	}

	@Override
	public void onResponse(CustomHttpResponse object) {
		// LoaderHelper.hideLoader();
		// String email = editTextEmail.getText().toString();
		// PreferencesHelper.setUserEmailAddress(email, this);
		// startActivity(new Intent(this, VerifyCodeActivity.class));
	}

	@Override
	public void onException(CustomHttpException exception) {
		LoaderHelper.hideLoader();
		Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
	}
}
