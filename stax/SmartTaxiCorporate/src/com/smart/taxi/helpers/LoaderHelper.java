package com.smart.taxi.helpers;

import android.app.Activity;
import android.app.ProgressDialog;

public class LoaderHelper {

	public static ProgressDialog progressDialog;
	
	public static void showLoader(Activity activity, String message, String title){
		
		progressDialog = new ProgressDialog(activity);
		progressDialog.setMessage(message);
		
		if(!title.equals(""))
			progressDialog.setTitle(title);
		
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
	}
	
	public static void hideLoader(){
		
		if(progressDialog.isShowing())
			progressDialog.dismiss();
	}
	
	public static void hideLoaderSafe()
	{
		if(isLoaderShowing())
		{
			try{
				hideLoader();
			}catch(Exception ex)
			{
				return;
			}
		}
	}
	
	public static boolean isLoaderShowing() {
		try{
		return progressDialog.isShowing();
		}catch(Exception ex)
		{
			return false;
		}
	}
}
