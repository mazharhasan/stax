package com.smarttaxi.driver.custom.design;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.smarttaxi.driver.R;
import com.smarttaxi.driver.activities.JourneyDetailActivity;
import com.smarttaxi.driver.activities.MainActivity;
import com.smarttaxi.driver.activities.ServiceLocation;
import com.smarttaxi.driver.entities.PickupRequest;
import com.smarttaxi.driver.interfaces.HttpResponseListener;
import com.smarttaxi.driver.model.WebServiceModel;
import com.smarttaxi.driver.preferences.PreferencesHandler;
import com.smarttaxi.driver.utils.AppToast;

public  class RequestDialogFragment extends DialogFragment{
   
	
    int mNum;
    Context mContext;
	private TextView passengerName;
	private ImageView passengerImage;
	private TextView pickupAddress;
	private TextView pickupTime;
	private TextView remaingTime;
	private Button acceptRequestButton;
	private TextView additionalMsg;
	private Thread _trd1;
	private View rejectRequestButton;
	
	public static PickupRequest pickupReq;
	public static String JourneyId;
	private static String pickupAdrs;
	private static  String username;
	private static  String pickTime;
	private static  String adMsg;
	private static  String remTime;
	private Dialog dialog;
//	private int count = 0 ;
//	private final MyFragmentAdapter adapter = new MyFragmentAdapter(getChildFragmentManager(),MainActivity.pickupCollection);
//	private ViewPager pager;
	
//	public RequestDialogFragment(String JourneyId, PickupRequest pickupReq, ViewPager pager)
//	{  
//		this.JourneyId = JourneyId;
//		this.pickupReq = pickupReq;
//		
//		
//		//Pickup Request req data 
//		this.username = this.pickupReq.getUserName();
//		this.pickupAdrs = this.pickupReq.getPickupLocation();
//		this.pickTime = this.pickupReq.getPickupTime();
//		this.adMsg = this.pickupReq.getPickupTime();
//		this.remTime = "60";
//	
//		//For adapter Setting
//		this.pager = pager;
//	}

	public RequestDialogFragment()
	{
		mContext = getActivity();
	}
	/*public RequestDialogFragment(Context ctx){  
		mContext = getActivity();
	}*/
	
	
    
	/*public static RequestDialogFragment newInstance(int position)
    {
		
	
		RequestDialogFragment df = new RequestDialogFragment();
		
		df.pickupReq = MainActivity.pickupCollection.GetPickRequest(position);
		df.username = df.pickupReq.getUserName();
		df.pickupAdrs = df.pickupReq.getPickupLocation();
		df.pickTime = df.pickupReq.getPickupTime();
		df.adMsg = df.pickupReq.getPickupTime();
		df.remTime = "60";
		
		return df;
		
    }*/
	
	/*public static RequestDialogFragment newInstance(String journeyId, PickupRequest pickupReq)
    {       
        RequestDialogFragment df = new RequestDialogFragment();
        
        df.JourneyId = journeyId;
        df.pickupReq = pickupReq;
        df.username = pickupReq.getUserName();
		df.pickupAdrs = pickupReq.getPickupLocation();
		df.pickTime = pickupReq.getPickupTime();
		df.adMsg = pickupReq.getPickupTime();
		df.remTime = "60";
		

	
		
        return df;       
    }*/
	
	

/////////////Umair
public static RequestDialogFragment newInstance(int num,Context ctx) {
        
	try
	{
		
        RequestDialogFragment f = new RequestDialogFragment();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }
	catch(Throwable t)
	{
		
		
	}
	return null;
}

	@SuppressLint("InlinedApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments().getInt("num");

        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NORMAL, theme = 0;
        switch ((mNum-1)%6) {
            case 1: style = DialogFragment.STYLE_NO_TITLE; break;
            case 2: style = DialogFragment.STYLE_NO_FRAME; break;
            case 3: style = DialogFragment.STYLE_NO_INPUT; break;
            case 4: style = DialogFragment.STYLE_NORMAL; break;
            case 5: style = DialogFragment.STYLE_NORMAL; break;
            case 6: style = DialogFragment.STYLE_NO_TITLE; break;
            case 7: style = DialogFragment.STYLE_NO_FRAME; break;
            case 8: style = DialogFragment.STYLE_NORMAL; break;
        }
        switch ((mNum-1)%6) {
            case 4: theme = android.R.style.Theme_Holo; break;
            case 5: theme = android.R.style.Theme_Holo_Light_Dialog; break;
            case 6: theme = android.R.style.Theme_Holo_Light; break;
            case 7: theme = android.R.style.Theme_Holo_Light_Panel; break;
            case 8: theme = android.R.style.Theme_Holo_Light; break;
        }
        setStyle(style, theme);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
	
	 @Override
	 public Dialog onCreateDialog (Bundle savedInstanceState)
	 {
	   //Create custom dialog
	   if (dialog == null)
	     super.setShowsDialog (false);

	   return dialog;
	 }
	
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
		 try
		 {
	        View v = inflater.inflate(R.layout.passenger_request, container, false);
	        View passengerName = v.findViewById(R.id.passengerName);
	        ((TextView)passengerName).setText("Dialog #" + mNum + ": using style ");

	        View passengerAddress = v.findViewById(R.id.pickupAddress);
	        ((TextView)passengerAddress).setText("Test address");
	        
	        View pickTime = v.findViewById(R.id.pickupTime);
	        ((TextView)pickTime).setText("test time");
	        
	        View additionalTym = v.findViewById(R.id.additionalMessage);
	        ((TextView)additionalTym).setText("Additional Time");
	        
	        
	        View remTime = v.findViewById(R.id.remainingTime);
	        ((TextView)remTime).setText("14 Sec");
	        
	        return v;
	        //setRetainInstance(true);
		 }
		 catch(Throwable t)
			{
				
				
			}
	     
	        /*// Watch for button clicks.
	        Button button = (Button)v.findViewById(R.id.acceptRequestButton);
	        button.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	                // When button is clicked, call up to owning activity.
	                ((DialogFragment)getActivity()).showDialog();
	            }
	        });*/
		 return null;
	        
	    }

   /* @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
//        MainActivity.mPager.setAdapter(adapter);
        
        dialog = new Dialog(getActivity());

   		dialog.setContentView(R.layout.passenger_request);
   		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
   		
//   		if(count  == 0)
//   		{
   			
//   			count++;
//   		}
//   		else
//   		{
//   			adapter.notifyDataSetChanged();
//   		}
   		
   		passengerName = (TextView) dialog.findViewById(R.id.passengerName);
   		if (!username.equals(""))
   			passengerName.setText(username);
   		// set the custom dialog components - text, image and button
   		passengerImage = (ImageView) dialog.findViewById(R.id.passengerImage);
   		passengerImage.setImageResource(R.drawable.ic_launcher);

   		pickupAddress = (TextView) dialog.findViewById(R.id.pickupAddress);

   		if (!pickupAdrs.equals(""))
   			pickupAddress.setText(pickupAdrs);

   		pickupTime = (TextView) dialog.findViewById(R.id.pickupTime);

   		if (!pickTime.equals(""))
   			pickupTime.setText(pickTime);

   		remaingTime = (TextView) dialog.findViewById(R.id.remainingTime);

   		if (!remTime.equals(""))
   			remaingTime.setText(remTime);

   		additionalMsg = (TextView) dialog.findViewById(R.id.additionalMessage);

   		if (adMsg.equals(null))
   			additionalMsg.setText("N/A");
   		else
   			additionalMsg.setText(adMsg);

   		acceptRequestButton = (Button) dialog
   				.findViewById(R.id.acceptRequestButton);

   		rejectRequestButton = (Button) dialog
   				.findViewById(R.id.rejectRequestButton);
   		

   		remaingTime.setText(String.valueOf(remTime));

   		_trd1 = new Thread() {
   			public void run() {
   				try {
   					while (true) {
   						getActivity().runOnUiThread(new Runnable() {
   							@Override
   							public void run() {
   								int time = Integer.parseInt(remaingTime
   										.getText().toString());
   								time--;
   								remaingTime.setText(String.valueOf(time));
   								if (time <= 0) {
   									dialog.dismiss();
   									_trd1.interrupt();
   								}
   							}
   						});
   						sleep(1000);
   					}

   				} catch (InterruptedException e) {
   					// TODO Auto-generated catch block
   					e.printStackTrace();
   				}
   			}
   		};
   		_trd1.start();

   		

   		rejectRequestButton.setOnClickListener(new OnClickListener() {

   			@Override
   			public void onClick(View v) {
   				// TODO Auto-generated method stub
   				PreferencesHandler preferencesHandler = new PreferencesHandler(
   						rejectRequestButton.getContext());
   				if (preferencesHandler.getDriverUserID() > 0)
   					WebServiceModel.callRejected("rejected", String
   							.valueOf(preferencesHandler.getDriverUserID()),
   							JourneyId, (HttpResponseListener) getActivity());
   				_trd1.interrupt();
   				dialog.dismiss();
   			}
   		});
   		// if button is clicked, close the custom dialog
   		acceptRequestButton.setOnClickListener(new OnClickListener() {
   			@Override
   			public void onClick(View v) {

   				Intent myIntent = new Intent(v.getContext(),
   						JourneyDetailActivity.class);
   				myIntent.setType("text/plain");
   				myIntent.putExtra(android.content.Intent.EXTRA_TEXT,
   						JourneyId);
   				
   				myIntent.putExtra("currentLat", ServiceLocation.curLocation.getLatitude());
   				myIntent.putExtra("currentLng", ServiceLocation.curLocation.getLongitude());
   				v.getContext().startActivity(myIntent);

   				_trd1.interrupt();
   				dialog.dismiss();
   			}
   		});

   		dialog.setCanceledOnTouchOutside(false);
   		
   		dialog.show();
    }*/
    
	
}   
