package com.smart.taxi.components.renderers;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.smarttaxi.client.R;
import com.smart.taxi.components.CFTextView;
import com.smart.taxi.entities.Journey;

public class TripHistoryRenderer extends FrameLayout {
	private String label = "";
	private Journey journeyData;
	public TripHistoryRenderer(Context context) {
		super(context);
		LayoutInflater layoutInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.listitem_trip_history, this);
	}
	
	public synchronized void setData(Object data)
	{
		journeyData = (Journey) data;
		((CFTextView)findViewById(R.id.txtPassLoc)).setText(journeyData.getPickUpAddress());
		((CFTextView)findViewById(R.id.txtPassengerName)).setText(journeyData.getDriverName());
		((CFTextView)findViewById(R.id.txtFareAmount)).setText(journeyData.getPaymentAmout());
		((CFTextView)findViewById(R.id.txtPickUpDateTime)).setText(journeyData.getPickFromTime());
		((CFTextView)findViewById(R.id.txtTipAmount)).setText(journeyData.getTipGiven());
		((CFTextView)findViewById(R.id.txtDropToDateTime)).setText(journeyData.getDropToTime());
		((CFTextView)findViewById(R.id.txtDriverLoc)).setText(journeyData.getDropToAddress());
		((CFTextView)findViewById(R.id.txtCorporate)).setText(journeyData.getCabProviderName());
		((CFTextView)findViewById(R.id.txtDriverAmount)).setText(journeyData.getDriverAmount());
		
		//double amount = Double.valueOf(journeyData.getUserTip()) + Double.valueOf(journeyData.getPaymentAmout());
		//((CFTextView)findViewById(R.id.txtDriverAmount)).setText(String.valueOf(amount));
	}
	
	public String getLabel()
	{
		return label;
	}

	public Journey getJourneyData() {
		return journeyData;
	}
	
	
}
