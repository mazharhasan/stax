package com.smarttaxi.driver.utils;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Process;
import android.provider.Settings;

import com.smarttaxi.driver.activities.BroadCastActivity;
import com.smarttaxi.driver.activities.ConnectionChangeListener;
import com.smarttaxi.driver.activities.GpsLocationReceiver;

public class NetworkAvailability {

	
	
	static AlertDialog.Builder builder;
	
	private static boolean isGpsShowing = false;
	private static boolean isNetworkShowing = false;
	
	public static boolean IsNetworkAvailable(Context context) {

		ConnectivityManager conMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (conMgr.getActiveNetworkInfo() != null
				&& conMgr.getActiveNetworkInfo().isAvailable()
				&& conMgr.getActiveNetworkInfo().isConnected()) {

			return true;

		} else {

			Applog.Debug("Internet Connection Not Present");
			try{
	        	NetworkAvailability.CheckBroadcastEnableDisable(context, "NoConnectionDialog");

				Intent in = new Intent(context.getApplicationContext(), BroadCastActivity.class);
				in.putExtra("CallFrom","NetworkActivity");
				in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				in.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				context.startActivity(in);
//			context.getApplicationContext().startActivity(new Intent(context.getApplicationContext(), BroadCastActivity.class));
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
//			startActivity(new Intent(context,BroadCastActivity.class));
//			showNoConnectionDialog(context);			
			return false;

		}
	}
	
	
	public static boolean IsGpsEnabled(Context context) {

		final LocationManager manager = (LocationManager) context.getSystemService( Context.LOCATION_SERVICE );

		
//		private boolean getGPSStatus()
//		{
		   String allowedLocationProviders = Settings.System.getString(context.getContentResolver(), Settings.System.LOCATION_PROVIDERS_ALLOWED);

		   if (allowedLocationProviders == null) {
		      allowedLocationProviders = "";
		   }

		
//		} 
		
	    if (!allowedLocationProviders.contains(LocationManager.GPS_PROVIDER))
	    {
	    	try{
				
					Intent in = new Intent(context.getApplicationContext(), BroadCastActivity.class);
					in.putExtra("CallFrom","GpsActivity");
					in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	//				in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	//				in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
	//				in.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					context.startActivity(in);
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
	    	return false;
	    }
	   return true;

		} 
	
	
	
	
	public static void showNoGpsDialog(Context ctx1) 
	{
		
		if(!isGpsShowing) 
		{
			isGpsShowing = true;
			
		    final Context ctx = ctx1;
		    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		    builder.setCancelable(true);
		    builder.setMessage("Gps provides better accuracy, please enable it.");
		    builder.setTitle("GPS is Disabled");
		    builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) 
		        {
		            ctx.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
		            dialog.dismiss();
		            isGpsShowing = false;
		            ((BroadCastActivity)(ctx)).finish();
		        }
		    });
	
		    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
		    {
		        public void onClick(DialogInterface dialog, int which) 
		        {
		        	dialog.dismiss();
		        	isGpsShowing = false;
		        	((BroadCastActivity)(ctx)).finish();
		            return;
		        }
		    });
	
		    builder.setOnCancelListener(new DialogInterface.OnCancelListener() 
		    {
		        public void onCancel(DialogInterface dialog) {
		        	((BroadCastActivity)(ctx)).finish();
		        	dialog.dismiss();
		        	isGpsShowing = false;
		            return;
		        }
		    });
	
		    builder.show();
		}
	}
	
	
	
	public static void showNoConnectionDialog(Context ctx1) 
	{
		
		if(!isNetworkShowing)
		{
			
			isNetworkShowing = true;
		    final Context ctx = ctx1;
	//	    if(builder==null)
	//	    {
		     AlertDialog.Builder 
		     
		     builder = new AlertDialog.Builder(ctx);
		    
		    builder.setCancelable(true);
		    builder.setMessage("Please check your internet connection.");
		    builder.setTitle("Internet not found.");
		    builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) 
		        {
	//	        	NetworkAvailability.CheckBroadcastEnableDisable(ctx, "DisablingBroadcast");
		        	Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
		        	//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
		            ctx.startActivity(intent);
		            dialog.dismiss();
		            isNetworkShowing = false;
	//	            builder = null;
	//	            NetworkAvailability.CheckBroadcastEnableDisable(ctx, "EnablingBroadcast");
		            ((BroadCastActivity)(ctx)).finish();
		          
		            
	//	            NetworkAvailability.CheckBroadcastEnableDisable(ctx,"NoConnectionDialog");	             
	//	            Process.killProcess(Process.myPid());
		        }
		    });
	
		    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
		    {
		        public void onClick(DialogInterface dialog, int which) 
		        {
		        	NetworkAvailability.CheckBroadcastEnableDisable(ctx, "NoConnectionDialog");
		        	
		           
		        	
		        	 
		        	
		        	Intent startMain = new Intent(Intent.ACTION_MAIN);
			        startMain.addCategory(Intent.CATEGORY_HOME);
			        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			        ((BroadCastActivity) (ctx)).startActivity(startMain);
	//		        builder=null;
			        dialog.dismiss();
			        isNetworkShowing = false;
			        
			        ((BroadCastActivity)(ctx)).finish();
			       
			        //Process.killProcess(Process.myPid());
		            return;
		        }
		    });
	
		    builder.setOnCancelListener(new DialogInterface.OnCancelListener() 
		    {
		        public void onCancel(DialogInterface dialog) {
		        	NetworkAvailability.CheckBroadcastEnableDisable(ctx, "NoConnectionDialog");
		        	
		        	dialog.dismiss();
		        	isNetworkShowing = false;
		        	
		        	Intent startMain = new Intent(Intent.ACTION_MAIN);
			        startMain.addCategory(Intent.CATEGORY_HOME);
			        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			        ((BroadCastActivity) (ctx)).startActivity(startMain);
	//	        	builder=null;
		        	((BroadCastActivity)(ctx)).finish();
		        	Process.killProcess(Process.myPid());
		        	return;
		        }
		    });
	
		    builder.show();
	    }
	}
	
	public static void CheckBroadcastEnableDisable(Context ctx, String callForm)
	{
//		Context ctx = context
//				.getApplicationContext();
		ComponentName component1 = new ComponentName(ctx, ConnectionChangeListener.class);
		ComponentName component2 = new ComponentName(ctx, GpsLocationReceiver.class);
		
        int status1 = ctx.getPackageManager().getComponentEnabledSetting(component1);
        int status2 = ctx.getPackageManager().getComponentEnabledSetting(component2);
        
        if((status1 == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT || status1 == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) && 
        		(callForm.equalsIgnoreCase("NoConnectionDialog") || callForm.equalsIgnoreCase("DisablingBroadcast"))) {
        	
        	ctx.getPackageManager().setComponentEnabledSetting(component1, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
        }
        else if(status1 == PackageManager.COMPONENT_ENABLED_STATE_DISABLED && callForm.equalsIgnoreCase("EnablingBroadcast"))
        	ctx.getPackageManager().setComponentEnabledSetting(component1, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
 	

        if((status2 == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT || status2 == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) && 
        		(callForm.equalsIgnoreCase("NoConnectionDialog") || callForm.equalsIgnoreCase("DisablingBroadcast"))) {
        	ctx.getPackageManager().setComponentEnabledSetting(component2, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
        }
        else if(status2 == PackageManager.COMPONENT_ENABLED_STATE_DISABLED && callForm.equalsIgnoreCase("EnablingBroadcast"))
        	ctx.getPackageManager().setComponentEnabledSetting(component2, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        
	}
	
	public static boolean isOnline(Context ctx) 
	{
	    ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) 
	    {
	        return true;
	    }
	    return false;
	}
	
}
