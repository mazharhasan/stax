package com.smarttaxi.driver.gcm;

import static com.smarttaxi.driver.gcm.CommonUtilities.SENDER_ID;
import static com.smarttaxi.driver.gcm.CommonUtilities.displayMessage;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.smarttaxi.driver.R;
import com.smarttaxi.driver.activities.MainActivity;
import com.smarttaxi.driver.utils.AppToast;
import com.smarttaxi.driver.utils.Utils;

public class GCMIntentService extends GCMBaseIntentService {

	public static final String TAG = "GCMIntentService";
	public static String REGID;

	public GCMIntentService() {
		super(SENDER_ID);
	}

	/*
	 * GCMInterface delegate; // initialize it with your activity's context
	 * public GCMIntentService(Context ctx) {
	 * 
	 * delegate = (GCMInterface)ctx; }
	 */

	/**
	 * Method called on device registered
	 **/

	@Override
	public void onRegistered(Context context, String registrationId) {

		REGID = registrationId;
		Log.i(TAG, "Device registered: regId = " + registrationId);
		// displayMessage(context, "Your device registred with GCM" +
		// registrationId);

		// delegate = (GCMInterface)context;
		/*
		 * if(delegate != null) { delegate.mOnRegistered(registrationId); }
		 */

		// MainActivity.PostRegistrationIdtoServer(registrationId,"90");

		ServerUtilities.register(context, android.os.Build.MODEL,
				android.os.Build.MODEL, registrationId);

	}

	/**
	 * Method called on device un registred
	 * */
	@Override
	public void onUnregistered(Context context, String registrationId) {
		Log.i(TAG, "Device unregistered");
		// displayMessage(context, getString(R.string.gcm_unregistered));
		ServerUtilities.unregister(context, registrationId);
	}

	/**
	 * Method called on Receiving a new message
	 * */
	@Override
	public void onMessage(Context context, Intent intent) {

		// extras.putString("ID",REGID);
		Log.i(TAG, "Received message");
		// intent.putExtras(extras);
		String journeyId = intent.getExtras().getString("journey_id");
		String message = intent.getExtras().getString("message");
		String notiStatus = intent.getExtras().getString("status");
		// message = message + "|" + REGID;

		displayMessage(context, journeyId, notiStatus, message, REGID);
		// notifies user
		generateNotification(context, message);

	}

	/**
	 * Method called on receiving a deleted message
	 * */
	@Override
	public void onDeletedMessages(Context context, int total) {
		Log.i(TAG, "Received deleted messages notification");
		String message = getString(R.string.gcm_deleted, total);
		// displayMessage(context, message);
		// notifies user
		generateNotification(context, message);
	}

	/**
	 * Method called on Error
	 * */
	@Override
	public void onError(Context context, String errorId) {
		Log.i(TAG, "Received error: " + errorId);
		// displayMessage(context, getString(R.string.gcm_error, errorId));

	}

	@Override
	public boolean onRecoverableError(Context context, String errorId) {
		// log message
		Log.i(TAG, "Received recoverable error: " + errorId);
		displayMessage(context,
				getString(R.string.gcm_recoverable_error, errorId));
		return super.onRecoverableError(context, errorId);
	}

	public static void cancelNotification(Context context)

	{

		//AppToast.ShowToast(context, "Cancel notification celled");
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		// mNotificationManager.cancel((String)getAppName(context),
		// MY_NOTIFICATION_ID);

		mNotificationManager.cancelAll();

	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	public static void generateNotification(Context context, String message) {

		if (!Utils.isEmptyOrNull(message)) {
			int icon = R.drawable.ic_launcher;
			long when = System.currentTimeMillis();
			NotificationManager notificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			Notification notification = new Notification(icon, message, when);

			String title = context.getString(R.string.app_name);

			Intent notificationIntent = new Intent(context, MainActivity.class);
			// set intent so it does not start a new activity
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent intent = PendingIntent.getActivity(context, 0,
					notificationIntent, 0);
			notification.setLatestEventInfo(context, title, message, intent);
			notification.flags |= Notification.FLAG_AUTO_CANCEL;

			// Play default notification sound
			notification.defaults |= Notification.DEFAULT_SOUND;

			// notification.sound = Uri.parse("android.resource://" +
			// context.getPackageName() + "your_sound_file_name.mp3");

			// Vibrate if vibrate is enabled
			notification.defaults |= Notification.DEFAULT_VIBRATE;
			notificationManager.notify(0, notification);

		}

	}

	/*
	 * static String setRegistrationId(Context context, String regId) {
	 * Log.v(TAG, "REGISTRATION ID IN SET: "+regId); final SharedPreferences
	 * prefs = getGCMPreferences(context); String oldRegistrationId =
	 * prefs.getString("REG_ID", ""); // int appVersion =
	 * getAppVersion(context); //Log.v(TAG, "Saving regId on app version " +
	 * appVersion); Editor editor = prefs.edit(); editor.putString("REG_ID",
	 * regId); //editor.putInt(PROPERTY_APP_VERSION, appVersion);
	 * editor.commit(); return oldRegistrationId; }
	 */

}
