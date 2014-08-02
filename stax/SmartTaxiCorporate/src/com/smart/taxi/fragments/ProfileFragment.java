package com.smart.taxi.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.smarttaxi.client.R;
import com.smart.taxi.activities.SplashActivity;
import com.smart.taxi.components.CFTextView;
import com.smart.taxi.entities.User;
import com.smart.taxi.utils.DownloadImagesTask;
import com.smart.taxi.utils.Utils;

public class ProfileFragment extends BaseFragment {
	
	public static final String TAG = "profileActivity";
	private CFTextView fullName, companyName, gender, phone;
	Button btnEditProfile, btnChangePassword, btnChangeTip;
	ImageView profileImage;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		Log.e("onResume", "Called");
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		rootView = inflater.inflate(R.layout.activity_profile, container, false);
		initUI();
		return rootView;
	}

	private void initUI() {
		// TODO Auto-generated method stub
		
		fullName = (CFTextView)rootView.findViewById(R.id.profileLabelName);
		companyName = (CFTextView)rootView.findViewById(R.id.profileLabelUserType);
		gender = (CFTextView)rootView.findViewById(R.id.profileLabelGender);
		phone = (CFTextView)rootView.findViewById(R.id.profileLabelPhone);
		btnEditProfile = (Button)rootView.findViewById(R.id.buttonEditProfile);
		btnChangePassword = (Button)rootView.findViewById(R.id.buttonChangePassword);
		btnChangeTip = (Button)rootView.findViewById(R.id.buttonChangeMyTip);
		profileImage = (ImageView)rootView.findViewById(R.id.profileImage);
		
		btnEditProfile.setOnClickListener(this);
		btnChangePassword.setOnClickListener(this);
		btnChangeTip.setOnClickListener(this);
		if(SplashActivity.isLoggedIn())
		{
			User currentUser = SplashActivity.loggedInUser;
			fullName.setText(currentUser.getFullName());
			gender.setText(currentUser.getGender());
			phone.setText(currentUser.getPhone());
			if(currentUser.isCorporateUser())
			{
				companyName.setText(currentUser.getCorporateInfo().getName());
			}
			if(!Utils.isEmptyOrNull(currentUser.getProfileImage()) && !currentUser.getProfileImage().isEmpty())
			{
				Log.e("ImageURL", currentUser.getProfileImage());
				try {
					profileImage.setTag(currentUser.getProfileImage());
					new DownloadImagesTask().execute(profileImage);
				    /*URL thumb_u = new URL(currentUser.getProfileImage());
				    Drawable thumb_d = Drawable.createFromStream(thumb_u.openStream(), "src");
				    profileImage.setImageDrawable(thumb_d);*/
				}
				catch (Exception e) {
				    Log.e("Image view", "Error in loading image");
				}
			}
		}
	}
	
	@Override
	public void onClick(View button)
	{
		super.onClick(button);
		Fragment fragment;
		String tag = "";
		switch (button.getId()) {
		case R.id.buttonEditProfile:
			//startActivityForResult(new Intent(getActivity(), EditProfileFragment.class), 100);
			fragment = new EditProfileFragment();
			tag = "editProfile";
			break;
		
		case R.id.buttonChangePassword:
			//startActivityForResult(new Intent(getActivity(), ChangePasswordFragment.class), 200);
			fragment = new ChangePasswordFragment();
			tag = "editPassword";
			break;
			
		case R.id.buttonChangeMyTip:
			fragment = new ChangeTipFragment();
			tag = "editTip";
			break;

		default:
			return;
		}
		
		FragmentManager manager = getFragmentManager();
		manager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(tag).commit();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch(requestCode)
		{
		case 100:
			if(resultCode == 0){      
		         Log.e("Activity Result","Profile updated successfully.");          
		     }
			break;
			
			case 200:
				if(resultCode == 0){      
					Log.e("Activity Result","Password changed successfully.");          
			    }
			break;
		}
	
	}
}
