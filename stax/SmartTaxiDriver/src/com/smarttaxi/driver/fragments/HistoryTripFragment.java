package com.smarttaxi.driver.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smarttaxi.driver.R;
import com.smarttaxi.driver.constants.APIConstants;
import com.smarttaxi.driver.database.SqliteHelper;
import com.smarttaxi.driver.entities.CustomHttpException;
import com.smarttaxi.driver.entities.CustomHttpResponse;
import com.smarttaxi.driver.entities.Journey;
import com.smarttaxi.driver.entities.User;
import com.smarttaxi.driver.helpers.LoaderHelper;
import com.smarttaxi.driver.interfaces.HttpResponseListener;
import com.smarttaxi.driver.model.WebServiceModel;
import com.smarttaxi.driver.preferences.PreferencesHandler;
import com.smarttaxi.driver.utils.AppToast;

public class HistoryTripFragment extends Fragment implements
		HttpResponseListener {

	private long userID;
	private SqliteHelper sqliteHelper;
	private static View rootView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		getActivity().getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		rootView = inflater.inflate(R.layout.fragment_trip_history, container,
				false);

		sqliteHelper = new SqliteHelper(getActivity());

		load(rootView);
		return rootView;
	}

	private void load(View rootView) {
		PreferencesHandler preferencesHandler = new PreferencesHandler(
				getActivity());
		userID = preferencesHandler.getUserID();
		User user = sqliteHelper.getUser(userID);
		if (user == null)
			return;
		LoaderHelper.showLoader(getActivity(), "Loading...", "");
		WebServiceModel.GetTripHistory(user.getDriverID(), this);
	}

	private void bindTrips(ArrayList<Journey> list) {
		((ListView) rootView.findViewById(R.id.listJourney))
				.setAdapter(new journeysAdapter(getActivity(), list,
						R.layout.cell_trip_history));
	}

	private class journeysAdapter extends ArrayAdapter<Journey> {
		private ArrayList<Journey> list;
		private Activity context;
		private int layoutId;

		public journeysAdapter(Activity context, ArrayList<Journey> list,
				int layoutId) {
			super(context, R.id.txtPassengerName, list);

			this.layoutId = layoutId;
			this.list = list;
			this.context = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			viewHolder holder = null;
			Journey journey = getItem(position);
			if (row == null) {
				LayoutInflater inflater = (context).getLayoutInflater();
				row = inflater.inflate(layoutId, parent, false);

				holder = new viewHolder();
				holder.txtName = (TextView) row
						.findViewById(R.id.txtPassengerName);
				holder.txtPickUpLocation = (TextView) row
						.findViewById(R.id.txtPassLoc);
				holder.txtDropToLocation = (TextView) row
						.findViewById(R.id.txtDriverLoc);
				holder.imgAirportTax = (ImageView) row
						.findViewById(R.id.img_airport);
				holder.txtDriverAmount = (TextView) row
						.findViewById(R.id.txtDriverAmount);
				holder.txtPickUpDateTime = (TextView) row
						.findViewById(R.id.txtPickUpDateTime);
				holder.txtDropToDateTime = (TextView) row
						.findViewById(R.id.txtDropToDateTime);

				holder.txtCorporate = (TextView) row
						.findViewById(R.id.txtCorporate);
				holder.txtFareAmount = (TextView) row
						.findViewById(R.id.txtFareAmount);
				holder.txtTipAmount = (TextView) row
						.findViewById(R.id.txtTipAmount);

				row.setTag(holder);
			} else
				holder = (viewHolder) row.getTag();

			String name = journey.getUsername().toString().trim();
			name = name.replace(" ", ", ");

			holder.txtName.setText(name);
			holder.txtPickUpLocation.setText(journey.getPickFromAddress());
			holder.txtDropToLocation.setText(journey.getDropToAddress());
			double totalAmount = 0, tip, fare;
			try {
				fare = Double.parseDouble(journey.getPaymentAmout());
			} catch (Exception e) {
				fare = 0;
			}

			try {
				tip = Double.parseDouble(journey.getTipGiven());
				if (tip > 0) {
					fare = fare - tip;
				}
			} catch (Exception e) {
				tip = 0;
			}

			if (journey.getExtras()) {
				fare = fare - 15;
				holder.imgAirportTax.setVisibility(View.VISIBLE);
			} else {
				holder.imgAirportTax.setVisibility(View.GONE);
			}

			totalAmount = tip + fare;
			holder.txtDriverAmount.setText("$" + totalAmount);
			holder.txtPickUpDateTime.setText(journey.getPickFromTime());
			holder.txtDropToDateTime.setText(journey.getDropToTime());

			holder.txtCorporate.setText(journey.getCorporateName());
			holder.txtFareAmount.setText("$" + fare);
			holder.txtTipAmount.setText("$" + tip);
			return row;
		}

		@Override
		public Journey getItem(int position) {
			return list.get(position);
		}
	}

	static class viewHolder {
		TextView txtName, txtPickUpLocation, txtDropToLocation,
				txtDriverAmount, txtPickUpDateTime, txtDropToDateTime,
				txtCorporate, txtFareAmount, txtTipAmount;
		ImageView imgAirportTax;
	}

	@Override
	public void onResponse(CustomHttpResponse object) {
		if (object.getStatusCode() == APIConstants.SUCESS_CODE) {
			@SuppressWarnings("unchecked")
			ArrayList<Journey> list = (ArrayList<Journey>) object.getResponse();
			bindTrips(list);
		} else {
			AppToast.ShowToast(getActivity(), object.getResponseMsg());
		}
		LoaderHelper.hideLoader();
	}

	@Override
	public void onException(CustomHttpException exception) {
		// TODO Auto-generated method stub
		if (LoaderHelper.progressDialog != null) {
			if (LoaderHelper.isLoaderShowing()) {
				LoaderHelper.hideLoader();
				AppToast.ShowToast(getActivity(), exception.getMessage());
			}

		}
	}

}
