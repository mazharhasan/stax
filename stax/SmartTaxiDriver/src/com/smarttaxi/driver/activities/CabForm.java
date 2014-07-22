package com.smarttaxi.driver.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smarttaxi.driver.R;
import com.smarttaxi.driver.constants.APIConstants;
import com.smarttaxi.driver.database.SqliteHelper;
import com.smarttaxi.driver.entities.Cab;
import com.smarttaxi.driver.entities.CustomHttpException;
import com.smarttaxi.driver.entities.CustomHttpResponse;
import com.smarttaxi.driver.entities.User;
import com.smarttaxi.driver.helpers.LoaderHelper;
import com.smarttaxi.driver.interfaces.HttpResponseListener;
import com.smarttaxi.driver.model.FactoryModel;
import com.smarttaxi.driver.model.WebServiceModel;
import com.smarttaxi.driver.preferences.PreferencesHandler;
import com.smarttaxi.driver.utils.AppToast;
import com.smarttaxi.driver.utils.Applog;
import com.smarttaxi.driver.utils.Utils;

public class CabForm extends Activity implements HttpResponseListener {
	private EditText edtCabNo;
	private String driverID;
	private Long userID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cab_form);
		Intent intent = this.getIntent();
		Bundle extras = intent.getExtras();
		if (extras == null || extras.containsKey("driver_id") == false)
			finish();
		driverID = extras.getString("driver_id");
		PreferencesHandler preferencesHandler = new PreferencesHandler(this);
		userID = preferencesHandler.getUserID();
		Applog.Debug("Cab - Driver ID : " + driverID);
		init();
	}

	private void init() {
		edtCabNo = (EditText) findViewById(R.id.edtCabNo);
		((Button) findViewById(R.id.bttSave))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						LoaderHelper.showLoader(CabForm.this, "Verifying...",
								"");
						String cabNo = String.valueOf(edtCabNo.getText());
						WebServiceModel.getCab(cabNo, CabForm.this);
					}
				});
	}

	@Override
	public void onResponse(CustomHttpResponse object) {
		if (object.getStatusCode() == APIConstants.SUCESS_CODE) {
			
			if (object.getMethodName().equalsIgnoreCase(
					APIConstants.METHOD_GET_DRIVER_CAB)) {
				LoaderHelper.hideLoader();
				LoaderHelper.showLoader(CabForm.this, "Verifying...", "");
				WebServiceModel.assignCab("", driverID, "add", this);
			} else if (object.getMethodName().equalsIgnoreCase(
					APIConstants.METHOD_ASSIGN_CAB_TO_DRIVER)) {
				Cab cab = (Cab) object.getResponse();
				LoaderHelper.hideLoader();
				if (cab == null) {
					Intent intent = new Intent();
					setResult(RESULT_CANCELED, intent);
					AppToast.ShowToast(this, "An error occured.");
				} else {
					AppToast.ShowToast(this, "Cab assigned successfully.");
					SqliteHelper sqliteHelper = new SqliteHelper(this);
					sqliteHelper.updateDriverInfo(cab.getId(), userID);
					Intent intent = new Intent();
					setResult(RESULT_OK, intent);
				}
				finish();
			}
		} else {
			if (object.getMethodName().equalsIgnoreCase(
					APIConstants.METHOD_GET_DRIVER_CAB)) {

				LoaderHelper.hideLoader();
				Intent intent = new Intent();
				setResult(RESULT_CANCELED, intent);
				AppToast.ShowToast(this, object.getResponseMsg());
				finish();
			}
		}
	}

	@Override
	public void onException(CustomHttpException exception) {
		// TODO Auto-generated method stub

	}

}
