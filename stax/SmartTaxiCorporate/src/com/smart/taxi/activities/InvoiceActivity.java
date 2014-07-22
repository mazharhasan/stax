package com.smart.taxi.activities;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;

import com.google.gson.JsonObject;
import com.smart.taxi.R;
import com.smart.taxi.components.CFTextView;
import com.smart.taxi.constants.APIConstants;
import com.smart.taxi.entities.CustomHttpException;
import com.smart.taxi.entities.CustomHttpResponse;
import com.smart.taxi.helpers.JsonHelper;
import com.smart.taxi.helpers.LoaderHelper;
import com.smart.taxi.interfaces.HttpResponseListener;
import com.smart.taxi.utils.NetworkAvailability;
import com.smart.taxi.utils.Utils;

public class InvoiceActivity extends Activity implements OnClickListener, HttpResponseListener {

	private LinearLayout vlContents;
	private String journeyId;
	private String userId;
	private ImageView profileImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invoice);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
		// Show the Up button in the action bar.
		//setupActionBar();
		getActionBar().hide();
		ImageButton backButton = (ImageButton) findViewById(R.id.imgButtonBack);
		profileImage = (ImageView) findViewById(R.id.profileImage);
		vlContents = (LinearLayout) findViewById(R.id.vlInvoiceContents);
		backButton.setOnClickListener(this);
		try{
			journeyId = getIntent().getExtras().getString("journeyId");
			userId = SplashActivity.loggedInUser.getId();
			if(NetworkAvailability.IsNetworkAvailable(this))
			{
				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair("journey_id", journeyId));
				params.add(new BasicNameValuePair("user_id", userId ));
				LoaderHelper.showLoader(this, "Loading trip information...", "");
				CustomHttpClass.runPostService(this, APIConstants.METHOD_GET_JOURNEY_DETAILS, params, true, false);
			}else{
				NetworkAvailability.showNoConnectionDialog(this);
			}
			
		}catch(Exception ex)
		{
			Log.e("Invoice screen", "Journey id not found");
			finish();
		}
		
	}

	@Override
	public void onClick(View arg0) {
		finish();
	}

	@Override
	public void onResponse(CustomHttpResponse object) {
		LoaderHelper.hideLoaderSafe();
		if(object.getMethodName() == APIConstants.METHOD_GET_JOURNEY_DETAILS)
		{
			JsonObject journeyJosn = JsonHelper.parseToJsonObject(object.getRawJson()).get("body").getAsJsonObject();
			journeyJosn = journeyJosn.get("journeys").getAsJsonObject();
			if(journeyJosn.has(journeyId))
			{
				journeyJosn = journeyJosn.get(journeyId).getAsJsonObject();
				String cabProviderId = journeyJosn.get("cab_provider_id").getAsString();
				String cabId = journeyJosn.get("cab_id").getAsString();
				journeyJosn = journeyJosn.get("journey_users").getAsJsonObject();
				String key = journeyJosn.entrySet().iterator().next().getKey(); 
				JsonObject tripInfo = journeyJosn.getAsJsonObject(key).getAsJsonObject();
				double tip = 0, fare = 0, total = 0, extraAmount = 0;
				boolean hasExtras = false;
				String driverImageUrl = "", pickupTime = "", dropOffAddress = "", dropOffTime = "", driverName = "", cabProviderName="", pickupAddress = "";
				if(tripInfo.has("pickup_door_address"))
				{
					pickupAddress = tripInfo.get("pickup_door_address").getAsString();
					Log.e("Pickup address:", pickupAddress);
				}
				if(tripInfo.has("tip_given"))
				{
					tip = Double.valueOf(tripInfo.get("tip_given").getAsString());
					Log.e("tip", String.valueOf(tip));
				}
				if(tripInfo.has("extra_amount"))
				{
					extraAmount = Double.valueOf(tripInfo.get("extra_amount").getAsString());
					hasExtras = (extraAmount > 0);
					Log.e("Extra payment", String.valueOf(extraAmount));
				}
				if(tripInfo.has("pickup_time"))
				{
					pickupTime = tripInfo.get("pickup_time").getAsString();
					Log.e("p-time", pickupTime);
				}
				
				if(tripInfo.has("dropOff_time"))
				{
					dropOffTime = tripInfo.get("dropOff_time").getAsString();
					Log.e("d-time", dropOffTime);
				}
				
				if(tripInfo.has("dropOff_door_address"))
				{
					dropOffAddress = tripInfo.get("dropOff_door_address").getAsString();
					Log.e("d-address", dropOffAddress);
				}
				JsonObject driver = tripInfo.get("DriverInformation").getAsJsonObject();
				try{
					driver = driver.get(journeyId).getAsJsonObject();
					driverName = driver.get("name").getAsString();
					Log.e("Driverinfo", driverName);
					driverImageUrl = driver.get("image_url").getAsString();
					Log.e("profileImage", driverImageUrl);
					if(!Utils.isEmptyOrNull(driverImageUrl) && !driverImageUrl.equals("0"))
					{
						URL thumb_u = new URL(driverImageUrl);
					    Drawable thumb_d = Drawable.createFromStream(thumb_u.openStream(), "src");
					    profileImage.setImageDrawable(thumb_d);
					}
				}catch(Exception ex){
					Log.e("driver info error", ex.toString());
				}
				if(tripInfo.has("cab_provider"))
				{
					cabProviderName = tripInfo.get("cab_provider").getAsJsonObject().get(cabProviderId).getAsJsonObject().get("name").getAsString();
					Log.e("Cab provider:", cabProviderName);
				}
				if(tripInfo.has("Payment"))
				{
					JsonObject payment = tripInfo.get("Payment").getAsJsonObject();
					payment = payment.get(journeyId).getAsJsonObject();
					total = Double.valueOf(payment.get("amount").getAsString());
					Log.e("Payment info", payment.get("amount").getAsString());
				}
				fare = total - tip;
				if(hasExtras)
				{
					fare -= extraAmount;
				}
				((CFTextView)findViewById(R.id.txtDriverNameInvoice)).setText(driverName);
				((CFTextView)findViewById(R.id.txtCompanyNameInvoice)).setText(cabProviderName);
				((CFTextView)findViewById(R.id.txtPickupDetails)).setText(pickupTime);
				((CFTextView)findViewById(R.id.txtPickupDetails)).setText(pickupAddress.concat(" on ").concat(pickupTime));
				((CFTextView)findViewById(R.id.txtDropOffDetails)).setText(dropOffAddress.concat(" on ").concat(dropOffTime));
				((CFTextView)findViewById(R.id.txtTipValue)).setText(parseToMoney(tip));
				if(hasExtras)
					((CFTextView)findViewById(R.id.txtTaxValue)).setText(parseToMoney(extraAmount));
				else
					((CFTextView)findViewById(R.id.txtTaxValue)).setText(parseToMoney(0.0));
				((CFTextView)findViewById(R.id.txtTotalValue)).setText(parseToMoney(total));
				((CFTextView)findViewById(R.id.txtFarevalue)).setText(parseToMoney(fare));
				
				
			}
			vlContents.setVisibility(View.VISIBLE);
		}
		
	}

	private String parseToMoney(double amount) {
		// TODO Auto-generated method stub
		if(amount > 0)
		{
			String value = String.valueOf(amount);
			value = "$".concat(value);
			if(value.indexOf(".")>0)
			{
				String fractionalPart = value.substring(value.indexOf("."));
				if(fractionalPart.length() < 3)
				{
					value = value.concat("0");
				}
			}else
			{
				value = value.concat(".00");
			}
			return value;
		}
		return "$0.00";
	}

	@Override
	public void onException(CustomHttpException exception) {
		LoaderHelper.hideLoaderSafe();
		Log.e("Invoice screen", exception.toString());
		
	}

}
