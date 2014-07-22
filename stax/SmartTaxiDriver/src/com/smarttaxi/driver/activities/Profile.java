package com.smarttaxi.driver.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.smarttaxi.driver.R;
import com.smarttaxi.driver.constants.APIConstants;
import com.smarttaxi.driver.entities.CustomHttpException;
import com.smarttaxi.driver.entities.CustomHttpResponse;
import com.smarttaxi.driver.helpers.LoaderHelper;
import com.smarttaxi.driver.interfaces.HttpResponseListener;
import com.smarttaxi.driver.model.WebServiceModel;
import com.smarttaxi.driver.utils.AppToast;
import com.smarttaxi.driver.components.CFTextView;
import com.smarttaxi.driver.components.CFEditText;

public class Profile extends Activity implements OnClickListener,
		HttpResponseListener {
	private String driverID, imageURL, firstName, lastName, gender,
			contactNumber, licenseCode;
	private boolean ChangeOccur;
	private Bitmap bitmap;
	private static final int SELECT_PICTURE = 0;
	private static CFTextView editphoto;
	private static CFEditText edtFirstName, edtLastName, edtContactNumber,
			edtLicenseCode;
	private static RadioButton rdlMale, rdlFemale;
	private static ImageView imgUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_profile);
		((Button) findViewById(R.id.bttSave)).setOnClickListener(this);
		Intent intent = this.getIntent();
		Bundle extras = intent.getExtras();
		if (extras == null || extras.containsKey("driver_id") == false)
			finish();

		driverID = extras.getString("driver_id");
		imageURL = extras.getString("image_url");
		firstName = extras.getString("first_name");
		lastName = extras.getString("last_name");
		gender = extras.getString("gender");
		contactNumber = extras.getString("contact_number");
		licenseCode = extras.getString("license_code");

		init();
		setUserInfo();
	}

	private void init() {
		edtFirstName = (CFEditText) findViewById(R.id.editTxtFirstName);
		edtLastName = (CFEditText) findViewById(R.id.editTxtLastName);
		edtContactNumber = (CFEditText) findViewById(R.id.editTxtPhone);
		edtLicenseCode = (CFEditText) findViewById(R.id.editLicenseCode);
		rdlFemale = (RadioButton) findViewById(R.id.radioFemale);
		rdlMale = (RadioButton) findViewById(R.id.radioMale);
		imgUser = (ImageView) findViewById(R.id.imgPhoto);
		editphoto  = (CFTextView) findViewById(R.id.editphoto);
//		bitmap = imgUser.ge
		
		//editphoto.setOnClickListener(this);
		imgUser.setOnClickListener(this);
	}

	private void setUserInfo() {
		edtFirstName.setText(firstName);
		edtLastName.setText(lastName);
		edtContactNumber.setText(contactNumber);
		edtLicenseCode.setText(licenseCode);
		rdlMale.setChecked(gender.equalsIgnoreCase("male"));
		rdlFemale.setChecked(gender.equalsIgnoreCase("female"));
//		rdlMale.setSelected(;
//		rdlFemale.setSelected();
		setUserPhoto();

		//Applog.Debug("Gender " + gender);
	}

	private void setUserPhoto() {
		AQuery aq = new AQuery(this);
		ImageOptions options = new ImageOptions();

		options.ratio = 1;
		options.anchor = 1.0f;
		aq.id(imgUser).image(imageURL, options);
	}

	private boolean validate() {
		String firstName = edtFirstName.getText().toString();
//		String lastName = edtLastName.getText().toString();
		String contact = edtContactNumber.getText().toString();
		String licenseCode =  edtLicenseCode.getText().toString();
		
		String requiredField = isFieldValid(contact, "Contact Number");
		if(requiredField != null){
			AppToast.ShowToast(this, requiredField +" is missing");
			return false;
		}
		
		requiredField = isFieldValid(licenseCode, "License Number");
		if(requiredField != null){
			AppToast.ShowToast(this, requiredField +" is missing");
			return false;
		}
		
		requiredField = isFieldValid(firstName, "First Name");
		if(requiredField != null){
			AppToast.ShowToast(this, requiredField +" is missing");
			return false;
		}	
		
		return true;
	}

	private String isFieldValid(String str, String fieldName){
		if(str == null || str.equalsIgnoreCase(""))
			return fieldName;
		else
			return null;
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bttSave:
			onUpdate();
			break;

		default:
			createPopup();
			break;
		}
	}

	private void createPopup() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
		builder.setTitle("Choose Image Source");
		builder.setItems(new CharSequence[] {"Gallery", "Camera", "Cancel"}, 
		        new DialogInterface.OnClickListener() {

		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        switch (which) {
		        case 0:

		            // GET IMAGE FROM THE GALLERY
		            Intent intent = new Intent(Intent.ACTION_PICK);
		            intent.setType("image/*");

		            Intent chooser = Intent.createChooser(intent, "Choose a Picture");
		            startActivityForResult(chooser,SELECT_PICTURE);

		            break;

		        case 1:
		            Intent getCameraImage = new Intent("android.media.action.IMAGE_CAPTURE");
		            startActivityForResult(getCameraImage, 1);

		            break;

		        default:
		        	dialog.dismiss();
		            break;
		        }
		    }
		});

		builder.show();
		
	}

	private void onUpdate() {
		if(validate() == false)
			return;
		
		CheckForChange();
		
		LoaderHelper.showLoader(this, "Loading...", "");
		firstName = edtFirstName.getText().toString();
		lastName = edtLastName.getText().toString();
		contactNumber = edtContactNumber.getText().toString();
		licenseCode =  edtLicenseCode.getText().toString();
		gender = rdlFemale.isSelected() ? "Female" : "Male";
		WebServiceModel.updateDriverProfile(driverID, firstName, lastName, contactNumber, licenseCode, gender, imageURL, this);
	}

	private void CheckForChange() {
		// TODO Auto-generated method stub
		
		String firstName_new = edtFirstName.getText().toString();
		String lastName_new = edtLastName.getText().toString();
		String contactNumber_new = edtContactNumber.getText().toString();
		String licenseCode_new =  edtLicenseCode.getText().toString();
		String gender_new = rdlFemale.isSelected() ? "Female" : "Male";
		
		if(!firstName_new.equals(firstName) || 
				!lastName_new.equals(lastName)||
				!contactNumber_new.equals(contactNumber)||
				!licenseCode_new.equals(licenseCode)||
				!gender_new.equals(gender))
			
			ChangeOccur = true;

		else
			ChangeOccur = false;
			
	}

	@Override
	public void onResponse(CustomHttpResponse object) {
		if (object.getStatusCode() == APIConstants.SUCESS_CODE) {
			AppToast.ShowToast(this, object.getResponseMsg());
			Intent intent = new Intent();
			
			if(ChangeOccur)
			{
				intent.putExtra("change_occur", true);
				intent.putExtra("first_name", firstName);
				intent.putExtra("last_name", lastName);
				intent.putExtra("gender", gender);
				intent.putExtra("contact_number", contactNumber);
				intent.putExtra("license_code", licenseCode);
			}
			else
				intent.putExtra("change_occur", false);
			
			if (getParent() == null) {
			    setResult(Activity.RESULT_OK, intent);
			} else {
			    getParent().setResult(Activity.RESULT_OK, intent);
			}
			finish();
//			finishActivity(RESULT_OK);
			
		} else
			AppToast.ShowToast(this, object.getResponseMsg());
		LoaderHelper.hideLoader();
	}

	@Override
	public void onException(CustomHttpException exception) {
		// TODO Auto-generated method stub
		
		if (LoaderHelper.isLoaderShowing()) {
			LoaderHelper.hideLoader();
			AppToast.ShowToast(this, exception.getMessage());

		}

	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                String selectedImagePath = getPath(selectedImageUri);
                System.out.println("Image Path : " + selectedImagePath);
                imgUser.setImageURI(selectedImageUri);
                imageURL = selectedImagePath;
//                bitmap  = BitmapFactory.decodeFile(selectedImagePath);
            }
        }
    }
 
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}
