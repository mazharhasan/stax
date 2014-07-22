package com.smarttaxi.driver.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.smarttaxi.driver.R;
import com.smarttaxi.driver.activities.MainActivity;
import com.smarttaxi.driver.activities.ViewPagerItemNotify;
import com.smarttaxi.driver.custom.design.CircularImageView;
import com.smarttaxi.driver.entities.PickupRequest;
import com.smarttaxi.driver.helpers.DownloadImageAsync;
import com.smarttaxi.driver.interfaces.HttpResponseListener;
import com.smarttaxi.driver.model.WebServiceModel;
import com.smarttaxi.driver.preferences.PreferencesHandler;
import com.smarttaxi.driver.utils.Applog;
import com.smarttaxi.driver.utils.Utils;

public class FragmentJourney extends Fragment {

	// public static boolean isJourneyAccepted = false;
	
	private final String TAG = FragmentJourney.this.getClass().getSimpleName();

	ViewPagerItemNotify interf;
    public boolean isActionPerformed = false;
	public static final String JOURNEY_ID = "journey_id";
	public static final String FRAGMENT_INDEX_IN_PAGER = "fragment_index";
	public static final int JOURNEY_ACCEPTED = 1;
	public static final int JOURNEY_REJECTED = 0;
	public static final int JOURNEY_REJECTION_QUEUE = 2;
	private PickupRequest pickUpRequest;

	// View Variable
	public TextView pickupAddress;
	public int remTime = 20;
	private TextView pickupTime;
	private TextView remaingTime;
	private TextView additionalMsg;
	private TextView passengerName, coperateName;
	private CircularImageView passengerImage;
	private Button acceptRequestButton;
	private Button rejectRequestButton;
	private CountDownTimer cdt;
	private String timer;

	public static final FragmentJourney newInstance(PickupRequest pick,
			int counter,String journey_id,  ViewPagerItemNotify interf) {
		FragmentJourney f = new FragmentJourney();
		Bundle bdl = new Bundle(counter);
		// f.item_index = item;
		f.interf = interf;
		f.pickUpRequest = pick;
		bdl.putInt(FRAGMENT_INDEX_IN_PAGER, counter);
	    bdl.putString(JOURNEY_ID, journey_id);
		f.setArguments(bdl);
		return f;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		//super.onSaveInstanceState(outState);
	}

	private int item_index;
	private String journey_id;
	private ImageView images;
	private String urlString = "";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Applog.Error(TAG +"="+ "onCreateView");
		item_index = getArguments().getInt(FRAGMENT_INDEX_IN_PAGER);
		// journey_id = getArguments().getString(JOURNEY_ID);
		final View v = inflater.inflate(R.layout.passenger_request, container,
				false);

		/*
		 * CustomDialog.showDialog(this, pickup_req.getUserImage(),
		 * pickup_req.getUserName(), pickup_req.getPickupLocation(),
		 * pickup_req.getPickupTime(), pickup_req.getAdditionalMessage(), "15",
		 * currentJourneyId, pickup_req.getCorporateName());
		 */

		passengerName = (TextView) v.findViewById(R.id.passengerName);
		coperateName = (TextView) v.findViewById(R.id.passengerCoperateName);
		if (pickUpRequest != null) {
			if (!Utils.isEmptyOrNull(pickUpRequest.getCorporateName()))
				coperateName.setText(pickUpRequest.getCorporateName());
			if (!Utils.isEmptyOrNull(pickUpRequest.getUserName()))
				passengerName.setText(pickUpRequest.getUserName());
			// set the custom dialog components - text, image and button
			passengerImage = (CircularImageView) v
					.findViewById(R.id.passengerImage);
			if (!Utils.isEmptyOrNull(pickUpRequest.getUserImage())) {

				passengerImage.setDrawingCacheEnabled(true);
				new DownloadImageAsync((ImageView) passengerImage,
						getActivity()).execute(pickUpRequest.getUserImage());
			}

			pickupAddress = (TextView) v.findViewById(R.id.pickupAddress);

			if (!Utils.isEmptyOrNull(pickUpRequest.getPickupLocation()))
				pickupAddress.setText(pickUpRequest.getPickupLocation());

			pickupTime = (TextView) v.findViewById(R.id.pickupTime);

			if (!Utils.isEmptyOrNull(pickUpRequest.getPickupTime()))
				pickupTime.setText(Utils.getFormattedDate(pickUpRequest
						.getPickupTime()));

			additionalMsg = (TextView) v.findViewById(R.id.additionalMessage);

			if (Utils.isEmptyOrNull(pickUpRequest.getAdditionalMessage()))
				additionalMsg.setText("N/A");
			else
				additionalMsg.setText(pickUpRequest.getAdditionalMessage());

			remaingTime = (TextView) v.findViewById(R.id.remainingTime);

			remaingTime.setText(String.valueOf(remTime));

			cdt = new CountDownTimer(20000, 1000) {

				@Override
				public void onTick(long millisUntilFinished) {

					// int time =
					// Integer.parseInt(remaingTime.getText().toString());
					// time--;
					remaingTime.setText(String
							.valueOf(millisUntilFinished / 1000) + " sec");
					remTime--;
				}

				@Override
				public void onFinish() {
					cdt.cancel();
					// TODO Auto-generated method stub
					// Toast.makeText(getActivity(), "rejecting   "+item_index,
					// Toast.LENGTH_LONG).show();
					if (interf != null  && !isActionPerformed)
						interf.itemSelectedonPager(JOURNEY_REJECTION_QUEUE,
								pickUpRequest.getjourneyId(),item_index);

					/*
					 * cdt.cancel();
					 * 
					 * MainActivity.isRequestShown = false; // TODO
					 * Auto-generated method stub if (interf != null)
					 * interf.itemSelectedonPager(JOURNEY_REJECTED,
					 * pickUpRequest.getjourneyId());
					 */
					/*
					 * if (!isJourneyAccepted) { PreferencesHandler
					 * preferencesHandler = new PreferencesHandler(
					 * rejectRequestButton.getContext()); if
					 * (preferencesHandler.getOriginalDriverUserID() > 0)
					 * WebServiceModel.callRejected("rejected", String
					 * .valueOf(preferencesHandler .getOriginalDriverUserID()),
					 * currentJourneyId, (HttpResponseListener) activity);
					 * dialog.dismiss(); } else { //isJourneyAccepted = false; }
					 */

				}
			};
			if (remTime == 20)
				cdt.start();

			rejectRequestButton = (Button) v
					.findViewById(R.id.rejectRequestButton);
			rejectRequestButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					isActionPerformed = true;
					cdt.cancel();
					// TODO Auto-generated method stub
					// Toast.makeText(getActivity(), "rejecting   "+item_index,
					// Toast.LENGTH_LONG).show();
					if (interf != null)
						interf.itemSelectedonPager(JOURNEY_REJECTED,
								pickUpRequest.getjourneyId(),item_index);

					
				}
			});
			acceptRequestButton = (Button) v
					.findViewById(R.id.acceptRequestButton);
			acceptRequestButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					isActionPerformed = true;
					// TODO Auto-generated method stub
					// Toast.makeText(getActivity(), "accepting  "+item_index,
					// Toast.LENGTH_LONG).show();
					cdt.cancel();
					
					if (interf != null)
						interf.itemSelectedonPager(JOURNEY_ACCEPTED,
								pickUpRequest.getjourneyId(),item_index);

					// interf.itemSelectedonPager(item);

				}
			});

		}

		// images = (Button) v.findViewById(R.id.bttn1);
		// Button dd = (Button) v.findViewById(R.id.bttn1);
		/*
		 * final Runnable r = new Runnable() {
		 * 
		 * @Override public void run() { if (i == 0) { urlString =
		 * "http://54.232.207.226/atlas.limittraining.com.br/thumb/grupo-muscular-sequencia/1/0/0/bb91e662b4fd6ffde74757ff9bd6e108.jpg"
		 * ; } else { urlString =
		 * "http://54.232.207.226/atlas.limittraining.com.br/thumb/grupo-muscular-sequencia/1/0/0/7730258ca72a75365d0c4b92a1a68a0a.jpg"
		 * ; }
		 * 
		 * 
		 * AQuery aquery = new AQuery(images); aquery.image(urlString); i++;
		 * if(i >= 2){ i = 0; }
		 * 
		 * // //images.postDelayed(this, 1500); } };
		 */
		// images.postDelayed(r, 1500);

		return v;
	}

}
