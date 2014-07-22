package com.smarttaxi.driver.custom.design;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;
import java.util.TimerTask;

import com.google.android.gms.internal.di;
import com.smarttaxi.driver.R;
import com.smarttaxi.driver.BAL.Driver;
import com.smarttaxi.driver.activities.EndJourneyActivity;
import com.smarttaxi.driver.activities.JourneyDetailActivity;
import com.smarttaxi.driver.activities.MainActivity;
import com.smarttaxi.driver.activities.ServiceLocation;
import com.smarttaxi.driver.entities.CustomHttpException;
import com.smarttaxi.driver.entities.CustomHttpResponse;
import com.smarttaxi.driver.fragments.FindARideFragment;
import com.smarttaxi.driver.helpers.DownloadImageAsync;
import com.smarttaxi.driver.helpers.LoaderHelper;
import com.smarttaxi.driver.helpers.TimerHelper;
import com.smarttaxi.driver.interfaces.HttpResponseListener;
import com.smarttaxi.driver.model.WebServiceModel;
import com.smarttaxi.driver.preferences.PreferencesHandler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.sax.StartElementListener;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smarttaxi.driver.R;
import com.smarttaxi.driver.activities.JourneyDetailActivity;
import com.smarttaxi.driver.activities.ServiceLocation;
import com.smarttaxi.driver.constants.APIConstants;
import com.smarttaxi.driver.interfaces.HttpResponseListener;
import com.smarttaxi.driver.model.WebServiceModel;
import com.smarttaxi.driver.preferences.PreferencesHandler;
import com.smarttaxi.driver.utils.Utils;

@SuppressLint("DefaultLocale")
public class CustomDialog {

	public static Dialog dialog;
	public static TextView pickupAddress;

	public static TextView pickupTime;
	public static TextView remaingTime;
	public static TextView additionalMsg;
	public static TextView passengerName, coperateName;
	public static CircularImageView passengerImage;
	public static Button acceptRequestButton;
	public static Button rejectRequestButton;
	public static CountDownTimer cdt;
	public static String timer;
	// private static Thread _trd1;

	public static boolean isJourneyAccepted = false;
	public static boolean isActionperformed = false;
	private static TextView tiptxt;
	private static TextView faretxt;
	private static Button approvebtn;
	private static EditText passcode;
	private static String passcodetxt;
	private static String FinalHash;
	public static String _userHashCode;
	private static String tipAmount;

	public static void showDialog(final Activity activity, String imgUrl,
			String userName, String pickupAdrs, String pickTime, String adMsg,
			final String remTime, final String currentJourneyId,
			final String usercoperateName) {

		dialog = new Dialog(activity);

		dialog.setContentView(R.layout.passenger_request);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

		passengerName = (TextView) dialog.findViewById(R.id.passengerName);
		coperateName = (TextView) dialog
				.findViewById(R.id.passengerCoperateName);
		if (!usercoperateName.equals(""))
			coperateName.setText(usercoperateName);
		if (!userName.equals(""))
			passengerName.setText(userName);
		// set the custom dialog components - text, image and button
		passengerImage = (CircularImageView) dialog
				.findViewById(R.id.passengerImage);
		if (!imgUrl.isEmpty() && !imgUrl.equals(null)) {

			passengerImage.setDrawingCacheEnabled(true);
			new DownloadImageAsync((ImageView) passengerImage, activity)
					.execute(imgUrl);
		}

		pickupAddress = (TextView) dialog.findViewById(R.id.pickupAddress);

		if (!pickupAdrs.equals(""))
			pickupAddress.setText(pickupAdrs);

		pickupTime = (TextView) dialog.findViewById(R.id.pickupTime);

		if (!pickTime.equals(""))
			pickupTime.setText(Utils.getFormattedDate(pickTime));

		remaingTime = (TextView) dialog.findViewById(R.id.remainingTime);

		if (!remTime.equals(""))

			additionalMsg = (TextView) dialog
					.findViewById(R.id.additionalMessage);

		if (Utils.isEmptyOrNull(adMsg))
			additionalMsg.setText("N/A");
		else
			additionalMsg.setText(adMsg);

		acceptRequestButton = (Button) dialog
				.findViewById(R.id.acceptRequestButton);

		rejectRequestButton = (Button) dialog
				.findViewById(R.id.rejectRequestButton);
		dialog.show();

		remaingTime.setText(String.valueOf(remTime));

		cdt = new CountDownTimer(15000, 1000) {

			@Override
			public void onTick(long millisUntilFinished) {

				// int time =
				// Integer.parseInt(remaingTime.getText().toString());
				// time--;
				remaingTime.setText(String.valueOf(millisUntilFinished / 1000)
						+ " sec");
			}

			@Override
			public void onFinish() {

				MainActivity.isRequestShown = false;
				// TODO Auto-generated method stub
				if (!isJourneyAccepted) {
					PreferencesHandler preferencesHandler = new PreferencesHandler(
							rejectRequestButton.getContext());
					if (!isActionperformed) {
						if (preferencesHandler.getOriginalDriverUserID() > 0)
							WebServiceModel.callRejected("rejected", String
									.valueOf(preferencesHandler
											.getOriginalDriverUserID()),
									currentJourneyId,
									(HttpResponseListener) activity);
						dialog.dismiss();
					}

				} else {
					isJourneyAccepted = false;
				}
			}
		};

		cdt.start();

		rejectRequestButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				cdt.cancel();
				isActionperformed = true;
				isJourneyAccepted = false;
				MainActivity.isRequestShown = false;
				// TODO Auto-generated method stub
				PreferencesHandler preferencesHandler = new PreferencesHandler(
						rejectRequestButton.getContext());
				if (preferencesHandler.getOriginalDriverUserID() > 0)
					WebServiceModel.callRejected("rejected", String
							.valueOf(preferencesHandler
									.getOriginalDriverUserID()),
							currentJourneyId, (HttpResponseListener) activity);
				// _trd1.interrupt();
				// _trd1.stop();

				dialog.dismiss();

			}
		});
		// if button is clicked, close the custom dialog
		acceptRequestButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				try {
					cdt.cancel();
					isActionperformed = true;
					dialog.dismiss();
					isJourneyAccepted = true;
					// MainActivity.isRequestShown = true;
					Driver driver = new Driver((Context) activity);
					driver.enablePobStatus("yes");
					MainActivity.pickupCollection.clear();
					// fragment

					Intent myIntent = new Intent(v.getContext(),
							JourneyDetailActivity.class);
					myIntent.setType("text/plain");
					myIntent.putExtra(android.content.Intent.EXTRA_TEXT,
							currentJourneyId);

					// myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

					myIntent.putExtra("isFromFindARide", true);
					myIntent.putExtra("currentLat",
							ServiceLocation.curLocation.getLatitude());
					myIntent.putExtra("currentLng",
							ServiceLocation.curLocation.getLongitude());
					v.getContext().startActivity(myIntent);

				} catch (Exception e) {
					e.printStackTrace();

				}
				// _trd1.interrupt();
				// _trd1.stop();

			}
		});

		dialog.show();

		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		dialog.show();
	}

	public static void showTripCompletedDialog(final Activity activity,
			String imgUrl, String userName, String pickupAdrs, String pickTime,
			String adMsg, final String remTime, final String currentJourneyId) {

		Dialog dialog = new Dialog(activity);

		dialog.setContentView(R.layout.journey_trip_completed);
		/*
		 * dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
		 * 
		 * passengerName = (TextView) dialog.findViewById(R.id.passengerName);
		 * if (!userName.equals("")) passengerName.setText(userName); // set the
		 * custom dialog components - text, image and button passengerImage =
		 * (ImageView) dialog.findViewById(R.id.passengerImage);
		 * passengerImage.setImageResource(R.drawable.ic_launcher);
		 * 
		 * pickupAddress = (TextView) dialog.findViewById(R.id.pickupAddress);
		 * 
		 * if (!pickupAdrs.equals("")) pickupAddress.setText(pickupAdrs);
		 * 
		 * pickupTime = (TextView) dialog.findViewById(R.id.pickupTime);
		 * 
		 * if (!pickTime.equals("")) pickupTime.setText(pickTime);
		 * 
		 * remaingTime = (TextView) dialog.findViewById(R.id.remainingTime);
		 * 
		 * if (!remTime.equals("")) remaingTime.setText(remTime);
		 * 
		 * additionalMsg = (TextView)
		 * dialog.findViewById(R.id.additionalMessage);
		 * 
		 * if (adMsg.equals(null)) additionalMsg.setText("N/A"); else
		 * additionalMsg.setText(adMsg);
		 * 
		 * acceptRequestButton = (Button) dialog
		 * .findViewById(R.id.acceptRequestButton);
		 * 
		 * rejectRequestButton = (Button) dialog
		 * .findViewById(R.id.rejectRequestButton); dialog.show();
		 * 
		 * remaingTime.setText(String.valueOf(remTime));
		 * 
		 * _trd1 = new Thread() { public void run() { try { while (true) {
		 * activity.runOnUiThread(new Runnable() {
		 * 
		 * @Override public void run() { int time = Integer.parseInt(remaingTime
		 * .getText().toString()); time--;
		 * remaingTime.setText(String.valueOf(time)); if (time <= 0) {
		 * dialog.dismiss(); _trd1.interrupt(); } } }); sleep(1000); }
		 * 
		 * } catch (InterruptedException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } } }; _trd1.start();
		 * 
		 * 
		 * 
		 * rejectRequestButton.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub PreferencesHandler preferencesHandler = new PreferencesHandler(
		 * rejectRequestButton.getContext()); if
		 * (preferencesHandler.getDriverUserID() > 0)
		 * WebServiceModel.callRejected("rejected", String
		 * .valueOf(preferencesHandler.getDriverUserID()), currentJourneyId,
		 * (HttpResponseListener) activity); _trd1.interrupt();
		 * dialog.dismiss(); } }); // if button is clicked, close the custom
		 * dialog acceptRequestButton.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) {
		 * 
		 * Intent myIntent = new Intent(v.getContext(),
		 * JourneyDetailActivity.class); myIntent.setType("text/plain");
		 * myIntent.putExtra(android.content.Intent.EXTRA_TEXT,
		 * currentJourneyId);
		 * 
		 * myIntent.putExtra("currentLat",
		 * ServiceLocation.curLocation.getLatitude());
		 * myIntent.putExtra("currentLng",
		 * ServiceLocation.curLocation.getLongitude());
		 * v.getContext().startActivity(myIntent);
		 * 
		 * _trd1.interrupt(); dialog.dismiss(); } });
		 * 
		 * dialog.show();
		 * 
		 * dialog.setCanceledOnTouchOutside(false); dialog.show();
		 */
	}

	public static void hideLoader() {

		if (dialog.isShowing())
			dialog.dismiss();
	}

	public static boolean isLoaderShowing() {
		return dialog.isShowing();
	}

	public static void showTripCompletedDialog(final Context activity,
			final String tip, final String fare, final Boolean taxIncluded,
			final Double currentLat, final Double currentLng,
			final String currentjourneyId, final String DropOffTime,
			final String dropOffAddress, final String hashcode) {

		_userHashCode = hashcode;
		final Dialog dialog = new Dialog(activity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.journey_trip_completed);

		tiptxt = (TextView) dialog.findViewById(R.id.tip_value);
		String tipAmount = "0";
		if (!tip.equals(null) && !fare.equals(null)) {
			double tipD = Double.parseDouble(fare) * (Double.parseDouble(tip)) / 100;
			tipAmount = String.format("%1$,.2f", tipD);
		}
		if(tipAmount.indexOf(".0") > 0)
		{
			tipAmount = tipAmount.substring(0, tipAmount.indexOf(".0"));
		}
		if(tipAmount.equalsIgnoreCase("0.0"))
		{
			tipAmount = "0";
		}
		tiptxt.setText(tipAmount);

		faretxt = (TextView) dialog.findViewById(R.id.fare_value);
		String fareAmount = "0";
		fareAmount = String.valueOf(Double.parseDouble(fare)
				+ Double.parseDouble(tipAmount));
		if (taxIncluded) {
			fareAmount = String.valueOf(Double.parseDouble(fareAmount) + 15);
		}
		faretxt.setText(fareAmount);

		passcode = (EditText) dialog.findViewById(R.id.pin_approve);
		approvebtn = (Button) dialog.findViewById(R.id.approve_end_journey);

		tiptxt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String test = "";
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				String test = "";
			}

			@Override
			public void afterTextChanged(Editable s) {
				String afterTipChangeTipAmount = Utils.isEmptyOrNull(String
						.valueOf(tiptxt.getText())) ? "0" : String
						.valueOf(tiptxt.getText());
				String afterTipChangeFareAmount = "0";
				afterTipChangeFareAmount = String.valueOf(Double
						.parseDouble(fare)
						+ Double.parseDouble(afterTipChangeTipAmount));
				if (taxIncluded) {
					afterTipChangeFareAmount = String.valueOf(Double
							.parseDouble(afterTipChangeFareAmount) + 15);
				}
				faretxt.setText(afterTipChangeFareAmount);
			}
		});

		approvebtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				passcodetxt = passcode.getText().toString();

				if (!Utils.isEmptyOrNull(passcodetxt)) {
					FinalHash = md5hash(APIConstants.PASSWORD_SALT
							+ passcodetxt);
					if (FinalHash.equalsIgnoreCase(_userHashCode)) {
						LoaderHelper.showLoader((Activity) activity,
								"Loading...", "");
						WebServiceModel.endJourney(
								(HttpResponseListener) activity,
								currentjourneyId, String.valueOf(currentLat),
								String.valueOf(currentLng), fare, DropOffTime,
								dropOffAddress, taxIncluded,
								String.valueOf(tiptxt.getText()));
						dialog.dismiss();
						// dialog=null;
					} else {
						Toast.makeText(activity,
								"Invalid code. Please re-enter",
								Toast.LENGTH_LONG).show();
					}
				}
			}
		});

		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		dialog.show();
	}

	private static String md5hash(String s) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
			digest.reset();
			digest.update(s.getBytes());
			byte[] a = digest.digest();
			int len = a.length;
			StringBuilder sb = new StringBuilder(len << 1);
			for (int i = 0; i < len; i++) {
				sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
				sb.append(Character.forDigit(a[i] & 0x0f, 16));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;

	}
}
