package com.smarttaxi.driver.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.internal.am;
import com.smarttaxi.driver.R;
import com.smarttaxi.driver.constants.APIConstants;
import com.smarttaxi.driver.entities.CustomHttpException;
import com.smarttaxi.driver.entities.CustomHttpResponse;
import com.smarttaxi.driver.entities.Journey;
import com.smarttaxi.driver.helpers.LoaderHelper;
import com.smarttaxi.driver.interfaces.HttpResponseListener;
import com.smarttaxi.driver.model.WebServiceModel;
import com.smarttaxi.driver.utils.AppToast;

public class EarningList extends Activity implements HttpResponseListener {

	private double totalTip = 0, totalAirportTax = 0, totalEarning = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_earnings_list);

		Intent intent = this.getIntent();
		Bundle extras = intent.getExtras();
		if (extras == null || extras.containsKey("from") == false)
			finish();

		String from = extras.getString("from");
		String driver_id = extras.getString("driver_id");
		String to = extras.getString("to");
		((TextView) findViewById(R.id.txtFrom)).append(from);
		((TextView) findViewById(R.id.txtTO)).append(to);
		LoaderHelper.showLoader(this, "Calculating...", "");
		WebServiceModel.GetTripHistory(driver_id, to, from, this);

	}

	private void bindTrips(ArrayList<Journey> list) {
		((ListView) findViewById(R.id.listEarning))
				.setAdapter(new EarningAdapter(this, list,
						R.layout.cell_trip_history));

		for (Journey journey : list) {
			double totalAmount = 0, tip, fare;
			try {
				fare = Double.parseDouble(journey.getPaymentAmout());
			} catch (Exception e) {
				fare = 0;
			}

			try {
				tip = Double.parseDouble(journey.getTipGiven());
			} catch (Exception e) {
				tip = 0;
			}

			totalAmount = tip + fare;
			totalTip += tip;
			totalAirportTax += 0;
			totalEarning += totalAmount;
		}
		((TextView) findViewById(R.id.txtTotalEarning)).setText(String
				.valueOf(totalEarning));
		((TextView) findViewById(R.id.txtTotalTip))
				.setText("Total Airport Tax : $" + totalAirportTax);
		((TextView) findViewById(R.id.txtAirportTax)).setText("Total Tip: $"
				+ totalEarning);
	}

	private class EarningAdapter extends ArrayAdapter<Journey> {
		private ArrayList<Journey> list;
		private Activity context;
		private int layoutId;

		public EarningAdapter(Activity context, ArrayList<Journey> list,
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
			holder.txtName.setText(journey.getUsername());
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
			} catch (Exception e) {
				tip = 0;
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

	}

	@Override
	public void onResponse(CustomHttpResponse object) {
		if (object.getStatusCode() == APIConstants.SUCESS_CODE) {

			if (!((ArrayList<Journey>) object.getResponse()).isEmpty())
				bindTrips((ArrayList<Journey>) object.getResponse());
			else
				AppToast.ShowToast(this, object.getResponseMsg());
		} else
			AppToast.ShowToast(this, object.getResponseMsg());
		LoaderHelper.hideLoader();
	}

	@Override
	public void onException(CustomHttpException exception) {
		// TODO Auto-generated method stub
		if (LoaderHelper.isLoaderShowing()) {
			LoaderHelper.hideLoader();
			AppToast.ShowToast(this, exception.getMessage());

		}
	}

}
