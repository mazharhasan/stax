package com.smart.taxi.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

public class NetworkAvailability {

	private static AlertDialog prevDialog;

	public static boolean IsNetworkAvailable(Context context) {

		ConnectivityManager conMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (conMgr.getActiveNetworkInfo() != null
				&& conMgr.getActiveNetworkInfo().isAvailable()
				&& conMgr.getActiveNetworkInfo().isConnected()) {

			return true;

		} else {

			Applog.Debug("Internet Connection Not Present");

			return false;

		}
	}
	
	
	public static boolean IsGpsEnabled(Context context) {

		final LocationManager manager = (LocationManager) context.getSystemService( Context.LOCATION_SERVICE );

	    if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) 
	    	return false;

			return true;

		} 
	
	
	public static void showNoGpsDialog(Context ctx1) 
	{
	    final Context ctx = ctx1;
	    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
	    builder.setCancelable(true);
	    builder.setMessage("Gps provides better accuracy, please enable it.");
	    builder.setTitle("GPS is Disabled");
	    builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) 
	        {
	            ctx.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	        }
	    });

	    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
	    {
	        public void onClick(DialogInterface dialog, int which) 
	        {
	            return;
	        }
	    });

	    builder.setOnCancelListener(new DialogInterface.OnCancelListener() 
	    {
	        public void onCancel(DialogInterface dialog) {
	            return;
	        }
	    });

	    builder.show();
	}
	
	
	
	public static void showNoConnectionDialog(Context ctx1) 
	{
	    final Context ctx = ctx1;
	    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
	    builder.setCancelable(true);
	    builder.setMessage("Please check your internet connection.");
	    builder.setTitle("Internet not found.");
	    builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) 
	        {
	            ctx.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
	            prevDialog = null;
	        }
	    });

	    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
	    {
	        public void onClick(DialogInterface dialog, int which) 
	        {
	        	prevDialog = null;
	            return;
	        }
	    });

	    builder.setOnCancelListener(new DialogInterface.OnCancelListener() 
	    {
	        public void onCancel(DialogInterface dialog) {
	            return;
	        }
	    });
	    if(prevDialog == null)
	    {
	    	prevDialog = builder.show();
	    }else{
	    	return;
	    }
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
