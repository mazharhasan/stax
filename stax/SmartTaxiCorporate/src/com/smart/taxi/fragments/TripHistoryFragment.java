package com.smart.taxi.fragments;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.smart.taxi.R;
import com.smart.taxi.activities.CustomHttpClass;
import com.smart.taxi.activities.SplashActivity;
import com.smart.taxi.components.renderers.TripHistoryRenderer;
import com.smart.taxi.constants.APIConstants;
import com.smart.taxi.entities.CustomHttpException;
import com.smart.taxi.entities.CustomHttpResponse;
import com.smart.taxi.entities.Journey;
import com.smart.taxi.helpers.LoaderHelper;
import com.smart.taxi.interfaces.HttpResponseListener;
import com.smart.taxi.utils.NetworkAvailability;

public class TripHistoryFragment extends ListFragment implements HttpResponseListener {

	
	public static final String TAG = "tripHistory";
	private ListView list;

	private static class CustomArrayAdapter extends ArrayAdapter<Journey> {

        /**
         * @param demos An array containing the details of the demos to be displayed.
         */
        public CustomArrayAdapter(Context context, List<Journey> journeies) {
        	super(context, R.id.txtPassengerName);
        	this.addAll(journeies);
            //super(context, R.layout.feature, R.id.title, labels);
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	TripHistoryRenderer renderer;
            if (convertView instanceof TripHistoryRenderer) {
            	renderer = (TripHistoryRenderer) convertView;
            } else {
            	renderer = new TripHistoryRenderer(getContext());
            }
            Journey journey = getItem(position);
            renderer.setData(journey);
            

            return renderer;
        }
        
    }
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.activity_testing, container, false);
		//list = (ListView)findViewById(R.id.list);
		loadTripHistory();
		/*String[] labels = {"Welcome","To","A", "List", "Item"};
		ListAdapter data = new CustomArrayAdapter(getActivity(), labels);
		setListAdapter(data);*/
		return rootView;
	}
	
	private void loadTripHistory() {
		if(NetworkAvailability.IsNetworkAvailable(getActivity()))
		{
			if(SplashActivity.isLoggedIn())
			{
				LoaderHelper.showLoader(getActivity(), "Loading your trip history...", "");
				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair("user_id", SplashActivity.loggedInUser.getId()));
				CustomHttpClass.runPostService((HttpResponseListener) this, APIConstants.METHOD_POST_TRIP_HISTORY, params, true, true);
			}else{
				//TODO: login user
			}
			
		}else{
			NetworkAvailability.showNoConnectionDialog(getActivity());
		}
		
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		//TripHistoryRenderer renderer = (TripHistoryRenderer) getListAdapter().getItem(position);
		//Log.e("Click message", getListAdapter().getItem(position).toString());
        //startActivity(new Intent(this, demo.activityClass));
    }

	@Override
	public void onResponse(CustomHttpResponse response) {
		LoaderHelper.hideLoaderSafe();
		// TODO Auto-generated method stub
		if(response.getMethodName() == APIConstants.METHOD_POST_TRIP_HISTORY)
		{
			List<Journey> journies = new ArrayList<Journey>();
			JsonObject journiesObj = (JsonObject) CustomHttpClass.getJsonObjectFromBody(response.getRawJson(), "journeys");
			double tip = 0, fare = 0, total = 0.0f;
			for (Entry<String, JsonElement> entry : journiesObj.getAsJsonObject().entrySet()) {
				Journey journey = new Journey();
				JsonElement jsonElement = entry.getValue();
				journey.deserializeFromJSON(jsonElement);
				JsonObject obj = jsonElement.getAsJsonObject();
				String cabId = obj.get("cab_provider_id").getAsString();
				if(obj.has("journey_users"))
				{
					JsonObject multiObject = obj.get("journey_users").getAsJsonObject();
					if(multiObject.entrySet().size() > 0)
					{
						String key = multiObject.entrySet().iterator().next().getKey(); 
						JsonObject tripInfo = multiObject.getAsJsonObject(key).getAsJsonObject();
						if(tripInfo.has("tip_given"))
						{
							tip = Double.valueOf(tripInfo.get("tip_given").getAsString());
							journey.setTipGiven("$" + tripInfo.get("tip_given").getAsString());
							Log.e("Tip amount:", journey.getTipGiven());
						}
						if(tripInfo.has("pickup_time"))
						{
							journey.setPickFromTime(tripInfo.get("pickup_time").getAsString());
							Log.e("Pickup time:", journey.getPickFromTime());
						}
						
						
						if(tripInfo.has("dropOff_time"))
						{
							journey.setDropToTime(tripInfo.get("dropOff_time").getAsString());
							Log.e("DropOff time:", journey.getDropToTime());
						}
						
						if(tripInfo.has("dropOff_door_address"))
						{
							journey.setDropToAddress(tripInfo.get("dropOff_door_address").getAsString());
							Log.e("DropOff Address:", journey.getDropToAddress());
						}
						JsonObject driver = multiObject.getAsJsonObject(key).get("DriverInformation").getAsJsonObject();
						try{
							driver = driver.get(entry.getKey()).getAsJsonObject();
							journey.setDriverName(driver.get("name").getAsString());
							Log.e("Driverinfo", driver.get("name").getAsString());
						}catch(Exception ex){}
						JsonObject payment = multiObject.getAsJsonObject(key).get("Payment").getAsJsonObject();
						try{
							payment = payment.get(entry.getKey()).getAsJsonObject();
							fare = Double.valueOf(payment.get("amount").getAsString());
							journey.setPaymentAmout("$" + payment.get("amount").getAsString());
							journey.setPaymentAmout((journey.getPaymentAmout().indexOf(".") >= 0)?journey.getPaymentAmout():journey.getPaymentAmout() + ".00");
							Log.e("Payment info", payment.get("amount").getAsString());
						}catch(Exception ex){}
						JsonObject cabProvider = multiObject.getAsJsonObject(key).get("cab_provider").getAsJsonObject();
						try{
							cabProvider = cabProvider.get(cabId).getAsJsonObject();
							journey.setCabProviderName(cabProvider.get("name").getAsString());
							Log.e("Cab provider name", cabProvider.get("name").getAsString());
						}catch(Exception ex){}
						
					}
					
					
				}
				total = fare - tip;
				journey.setDriverAmount(String.valueOf(total));
				
				//Log.e("Entry " + entry.getKey(), jsonElement.toString());
				
				journies.add(journey);
			}
			if(journies.size()>0)
			{
				ListAdapter data = new CustomArrayAdapter(getActivity(), journies);
				setListAdapter(data);
			}
		}
		
	}

	@Override
	public void onException(CustomHttpException exception) {
		LoaderHelper.hideLoaderSafe();
		// TODO Auto-generated method stub
		if(exception.getMethodName() == APIConstants.METHOD_POST_TRIP_HISTORY)
		{
			
		}
		
	}
}
