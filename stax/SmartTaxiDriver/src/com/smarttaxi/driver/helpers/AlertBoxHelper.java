package com.smarttaxi.driver.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class AlertBoxHelper {

	public static AlertDialog alertDialog;
	
	public static void showAlertBox(final Activity activity, final String title, String Msg )
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
	
			// set title
			alertDialogBuilder.setTitle(title);
	
			// set dialog message
			alertDialogBuilder
				.setMessage(Msg)
				.setCancelable(false)
				.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, close
						// current activity
						if(title.contains("No Connection Found"))
							activity.finish();
						alertDialog.dismiss();
					}
				  });

			// create alert dialog
			alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();
		}
}
