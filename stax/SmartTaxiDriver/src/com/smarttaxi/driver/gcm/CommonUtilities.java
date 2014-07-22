package com.smarttaxi.driver.gcm;

import com.smarttaxi.driver.utils.Utils;

import android.content.Context;
import android.content.Intent;

public final class CommonUtilities {
	
	// give your server registration url here
	public static final String EXTRA_MESSAGE = "message";
	public static final String JOURNEY_ID = "journeyId";
	public static final String NOTI_STATUS = "status";
	public static final String SERVER_URL = "http://technyxsystems.com/demo/gcm/register.php"; 

    // Google project id
	public static final String SENDER_ID = "1057965424733"; 

    /**
     * Tag used on log messages.
     */
	public static final String TAG = "AndroidHive GCM";

	public static final String DISPLAY_MESSAGE_ACTION =
            "com.smarttaxt.driver.gcm.DISPLAY_MESSAGE";

	
	public static  final String REGID ="";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
	public static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        
        context.sendBroadcast(intent);
    }
	
	public static void displayMessage(Context context, String journeyId,
			   String notiStatus, String message, String regID) {
		
		//temp
		/*if(Utils.isEmptyOrNull(journeyId))
			return;*/
		
			  Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
			  intent.putExtra(JOURNEY_ID, journeyId);
			  intent.putExtra(NOTI_STATUS, notiStatus);
			  intent.putExtra(EXTRA_MESSAGE, message);
			  intent.putExtra(REGID, regID);
			  context.sendBroadcast(intent);
			  
			 }
}
