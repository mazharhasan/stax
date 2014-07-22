package com.smarttaxi.driver.fragments;

import com.smarttaxi.driver.entities.Pick;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.smarttaxi.driver.R;
public class PickupRequestFragment extends Fragment {

	Pick _request;
	public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

	public static final PickupRequestFragment newInstance(String message,
			int item, Pick req) {
		PickupRequestFragment f = new PickupRequestFragment();
		f._request =req;
		

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		// Pick a style based on the num.

	}

	/*private int item;
	private ImageView images;
	private String urlString = "";
	int i = 0;;*/

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.passenger_request, container,
				false);
		TextView vv = (TextView) v.findViewById(R.id.passengerName);
		vv.setText(_request.journey_id);
		Log.i("", _request.journey_id);

		return v;
	}

}