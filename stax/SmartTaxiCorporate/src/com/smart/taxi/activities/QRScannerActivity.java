package com.smart.taxi.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.smarttaxi.client.R;
import com.smart.taxi.constants.APIConstants;
import com.smart.taxi.entities.CustomHttpException;
import com.smart.taxi.entities.CustomHttpResponse;
import com.smart.taxi.helpers.LoaderHelper;
import com.smart.taxi.interfaces.HttpResponseListener;
import com.smart.taxi.services.ServiceLocation;
import com.smart.taxi.utils.NetworkAvailability;
import com.smart.taxi.utils.Utils;

public class QRScannerActivity extends FragmentActivity implements 
										OnClickListener, HttpResponseListener,
										DialogInterface.OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qrscanner);
		if(getIntent().getExtras() != null)
		{
			Bundle bundle = (Bundle) getIntent().getExtras();
			Log.e("Yes bundle", bundle.toString());
		}else{
			Log.e("No bundle", "Ponka");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		
		
		return true;
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		String result = scanResult.getContents().toString();
		if (!Utils.isEmptyOrNull(result) && result.contains("-")) {
			//((TextView)findViewById(R.id.labelQRData)).setText(((CharSequence)scanResult.getContents()));
			String[] chunks = result.split("-");
			if(chunks.length == 2)
			{
				int userId = Double.valueOf(chunks[1]).intValue();
				if(userId > 0)
				{
					if(NetworkAvailability.IsNetworkAvailable(getBaseContext()))
					{
						if(!ServiceLocation.isService)
						{
							startService(new Intent(this, ServiceLocation.class));
						}
						List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
						params.add(new BasicNameValuePair("user_id", String.valueOf(userId)));
						//LoaderHelper.showLoader(this, "Loading profile...", "");
						CustomHttpClass.runPostService(this, "get_profile.json", params, true, false);
					}else{
						NetworkAvailability.showNoConnectionDialog(this);
					}
				}else{
					// TODO: Show invalid bar code alert dialogue
				}
			}
		}
		 // else continue with any other code you need in the method
		 
	}

	@Override
	public void onClick(View v) {
		IntentIntegrator integrator = new IntentIntegrator(this);
		integrator.initiateScan();
		return;
		/*String jsonData = "{ \"body\" : { \"CustomerInformation\" : { \"address\" : \"0\",\"city\" : \"0\",\"country\" : \"0\",\"facebook\" : \"0\",\"gender\" : \"0\",\"id\" : \"0\",\"linkedin\" : \"0\",\"phone\" : \"0\",\"state\" : \"0\",\"twitter\" : \"0\",\"user_id\" : \"0\",\"zip\" : \"0\"  },\"TotalJourneysCount\" : \"368\",\"TotalSharedJourneysCount\" : \"0\",\"User\" : { \"corporate_id\" : \"2\",\"created\" : \"January 10, 2014\",\"first_name\" : \"Mazhar\",\"friends\" : null,\"gender\" : \"Male\",\"group_id\" : \"5\",\"id\" : \"364\",\"last_name\" : \"Hasan\",\"phone\" : \"923333373075\",\"status\" : \"active\",\"user_image\" : \"http://smarttaxi.ca/services/stax/uploads/images_profile/0dbf38e936a8d42e939db943b7ceb656.jpg\",\"username\" : \"smazherhq@gmail.com\"  },\"UserCardInformation\" : { \"card_ccv\" : \"0\",\"card_expiry\" : \"0\",\"card_number\" : \"0\",\"card_title\" : \"0\",\"card_type\" : \"0\",\"created\" : \"0\",\"id\" : \"0\",\"modified\" : \"0\",\"status\" : \"0\",\"user_id\" : \"0\"  },\"UserPayment\" : { \"amount\" : \"100\",\"created\" : \"2014-01-17 02:30:11\",\"customer_id\" : \"364\",\"id\" : \"841\",\"journey_id\" : \"851\",\"payment_type\" : \"cash\",\"status\" : \"0\"  },\"UserRating\" : { \"total_rating_count\" : \"0\",\"total_report_rating_count\" : \"0\",\"total_thumbsup_rating_count\" : \"0\"  },\"UserSocialAccount\" : { \"access_token\" : \"0\",\"access_token_secret\" : \"0\",\"created\" : null,\"email_address\" : null,\"friends\" : null,\"id\" : \"0\",\"image_url\" : \"0\",\"link_id\" : \"0\",\"modified\" : null,\"screen_name\" : \"0\",\"status\" : \"0\",\"type_id\" : \"0\",\"user_id\" : \"0\"  }},\"header\" : { \"code\" : \"0\",\"message\" : \"Success\"}}";
		Gson gson = new Gson();
		Object o = gson.fromJson(jsonData, Object.class);
		System.out.print(o);*/
	}

	@Override
	public void onResponse(CustomHttpResponse object) {
		
		Location currentLocation = ServiceLocation.curLocation;
		/*try{
			if(LoaderHelper.isLoaderShowing())
				LoaderHelper.hideLoader();
		}catch(Exception e)
		{
			
		}*/
		
		if(object.getMethodName() == APIConstants.METHOD_GET_USER_PROFILE)
		{
			
		}else if(object.getMethodName() == APIConstants.METHOD_CREATE_DIRECT_TRIP)
		{
			
		}
		
		
	}

	@Override
	public void onException(CustomHttpException exception) {
		if(LoaderHelper.isLoaderShowing())
			LoaderHelper.hideLoader();
		Log.e("Exception", "Busted");
		
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		Log.e("Dialogue response", String.valueOf(which));
		
	}

}
