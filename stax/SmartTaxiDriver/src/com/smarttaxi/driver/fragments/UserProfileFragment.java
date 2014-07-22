package com.smarttaxi.driver.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import com.smarttaxi.driver.components.CFTextView;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.smarttaxi.driver.R;
import com.smarttaxi.driver.activities.Cabs;
import com.smarttaxi.driver.activities.ChangePassword;
import com.smarttaxi.driver.activities.Profile;
import com.smarttaxi.driver.custom.design.CircularImageView;
import com.smarttaxi.driver.database.SqliteHelper;
import com.smarttaxi.driver.entities.User;
import com.smarttaxi.driver.helpers.DownloadImageAsync;
import com.smarttaxi.driver.preferences.PreferencesHandler;

public class UserProfileFragment extends Fragment implements OnClickListener {

	private static Button bttEditProfile, bttChangeTaxi, bttChangePassword;
	private static User user;
	private final static int requestCodeEditProfile = 1,
			requestCodeChangeCab = 2;
	private View rootView;
	private SqliteHelper sqliteHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRetainInstance(true);
		getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		rootView = inflater.inflate(R.layout.fragment_user_profile,
				container, false);
		bttChangePassword = (Button) rootView
				.findViewById(R.id.bttChangePassword);
		bttEditProfile = (Button) rootView.findViewById(R.id.bttEditProfile);
		//bttChangeTaxi = (Button) rootView.findViewById(R.id.bttChangeTaxi);

		bttChangePassword.setOnClickListener(this);
		bttEditProfile.setOnClickListener(this);
		//bttChangeTaxi.setOnClickListener(this);

		setUserInformation(rootView);

		return rootView;
	}

	@SuppressLint("NewApi")
	private void setUserInformation(View rootView) {
		sqliteHelper = new SqliteHelper(getActivity());
		PreferencesHandler preferencesHandler = new PreferencesHandler(
				getActivity());
		long userID = preferencesHandler.getUserID();
		user = sqliteHelper.getUser(userID);
		if (user == null)
			return;
		
		((CFTextView) rootView.findViewById(R.id.txtDriverName)).setText(user
				.getFullName());
		((CFTextView) rootView.findViewById(R.id.txtCabProvider)).setText(user
				.getCabProvider());
		((CFTextView) rootView.findViewById(R.id.txtGender)).setText(user
				.getGender());
		((CFTextView) rootView.findViewById(R.id.txtLicenseNo)).setText(user
				.getLicenseCode());
		((CFTextView) rootView.findViewById(R.id.txtContactNo)).setText(user
				.getContactNumber());
		CircularImageView imgUser = (CircularImageView) rootView
				.findViewById(R.id.imgPhoto);
		AQuery aq = new AQuery(rootView);
		ImageOptions options = new ImageOptions();

		options.ratio = 1;
		options.anchor = 1.0f;
		// Applog.Debug("User Image @ UserProfileFragment " +
		// user.getUserImage());
		// aq.id(imgUser).image(user.getUserImage(), options);

		// CircularImageView img = (CircularImageView)
		// findViewById(R.id.circular_image_view);
		if (!user.getUserImage().isEmpty() && !user.getUserImage().equals(null)) {
			imgUser.setDrawingCacheEnabled(true);
			new DownloadImageAsync((ImageView) imgUser, getActivity())
					.execute(user.getUserImage());

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bttChangePassword:
			startActivity(new Intent(getActivity(), ChangePassword.class));
			break;
		/*case R.id.bttChangeTaxi:
			onChangeCab();
			break;*/
		case R.id.bttEditProfile:
			onEditProfile();
			break;
		default:
			break;
		}
	}

	private void onChangeCab() {
		Intent intent = new Intent(getActivity(), Cabs.class);
		intent.putExtra("driver_id", user.getDriverID());
		startActivityForResult(intent, requestCodeChangeCab);
	}

	private void onEditProfile() {
		Intent intent = new Intent(getActivity(), Profile.class);
		intent.putExtra("driver_id", user.getDriverID());
		intent.putExtra("image_url", user.getUserImage());
		intent.putExtra("first_name", user.getFirstName());
		intent.putExtra("last_name", user.getLastName());
		intent.putExtra("gender", user.getGender());
		intent.putExtra("contact_number", user.getContactNumber());
		intent.putExtra("license_code", user.getLicenseCode());
		startActivityForResult(intent, requestCodeEditProfile);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		case requestCodeEditProfile:
			if (resultCode == getActivity().RESULT_OK) {
				// TODO
				
				if(data!= null)
				{
					Bundle extras = data.getExtras();
					
					if(extras !=null && extras.getBoolean("change_occur"))
					{
						String firstName = extras.getString("first_name");
						String lastName = extras.getString("last_name");
						String gender = extras.getString("gender");
						String contactNumber = extras.getString("contact_number");
						String licenseCode = extras.getString("license_code");
						
						
						ContentValues cv = new ContentValues();
						
						cv.put("first_name", firstName);
						cv.put("last_name", lastName);
						cv.put("gender", gender);
						cv.put("contact_number", contactNumber);
						cv.put("license_code", licenseCode);

						
						sqliteHelper.saveUser(cv, Long.parseLong(user.getId()));
						
						setUserInformation(rootView);
					}
				}
			}
			break;

		default:
			break;
		}
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}
	
}
