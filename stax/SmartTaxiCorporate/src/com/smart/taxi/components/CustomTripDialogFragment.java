package com.smart.taxi.components;


import java.util.Calendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;

import com.smarttaxi.client.R;
import com.smart.taxi.entities.TripDetails;
import com.smart.taxi.fragments.FindARideFragment;

public class CustomTripDialogFragment extends DialogFragment 
										implements OnClickListener,
										OnTimeChangedListener
										{
	String journeyType = "";
	private View view;
	private LinearLayout llPickupTimeSelector;
	
	private String numPassengers = "1", numBags = "0", numChildSeats = "0", time = "Now", selectedTime = "";
	boolean isCustomTime = false;
	private TimePicker pickupTimer;
	private NumberPicker pickNumPassenger;
	private NumberPicker pickNumBags;
	private NumberPicker pickNumChildSeats;
	private String defaultTimeText = "Right now (change)";
	private String timeText = "";
	private TextView btnChangeTime;
	public FindARideFragment parent;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    setDialogueView(inflater.inflate(R.layout.dialog_custom_trip, null));
	    builder.setView(getDialogueView());
	    View contents = getDialogueView();
	    llPickupTimeSelector = (LinearLayout)contents.findViewById(R.id.llPickupTimeSelector);
	    pickupTimer = (TimePicker)contents.findViewById(R.id.pickupTimer);
	    pickupTimer.setOnTimeChangedListener(this);
	    btnChangeTime = (CFTextView)contents.findViewById(R.id.btnSelectDateTime);
	    btnChangeTime.setText(defaultTimeText);
	    Button btnUpdateView = (Button) contents.findViewById(R.id.btnUpdatePickupTime);
	    Button btnPickMeUpHere = (Button) contents.findViewById(R.id.btnPickMeUpHere);
	    btnChangeTime.setOnClickListener(this);
	    btnUpdateView.setOnClickListener(this);
	    btnPickMeUpHere.setOnClickListener(this);
	    pickNumPassenger = (NumberPicker)contents.findViewById(R.id.pickerNumPessangers);
	    pickNumPassenger.setMaxValue(7);
	    pickNumPassenger.setMinValue(1);
	    pickNumPassenger.setValue((int) Double.valueOf(parent.getDefaultTripDetails().getNumPassengers()).intValue());
	    pickNumBags = (NumberPicker) contents.findViewById(R.id.pickerNumBaggages);
	    pickNumBags.setMaxValue(6);
	    pickNumBags.setValue((int) Double.valueOf(parent.getDefaultTripDetails().getNumBags()).intValue());
	    pickNumChildSeats = (NumberPicker)contents.findViewById(R.id.pickerNUmChildseats);
	    pickNumChildSeats.setMaxValue(4);
	    pickNumChildSeats.setValue((int) Double.valueOf(parent.getDefaultTripDetails().getNumChildSeats()).intValue());
	    selectedTime = String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)).concat(":").concat(String.valueOf(Calendar.getInstance().get(Calendar.MINUTE)));
	    final AlertDialog dialog = builder.create();
	    dialog.setOnShowListener(new DialogInterface.OnShowListener() {

	        @Override
	        public void onShow(DialogInterface dialogIn) {

	            Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
	            b.setOnClickListener(new View.OnClickListener() {

	                @Override
	                public void onClick(View view) {
	                	
	                	dialog.dismiss();
	                }
	            });
	        }
	    });
	    return dialog;
	}

	public View getDialogueView() {
		return view;
	}

	public void setDialogueView(View view) {
		this.view = view;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnSelectDateTime:
			llPickupTimeSelector.setVisibility(View.VISIBLE);
			break;
			
		case R.id.btnUpdatePickupTime:
			btnChangeTime.setText(timeText);
			llPickupTimeSelector.setVisibility(View.INVISIBLE);
			llPickupTimeSelector.setVisibility(View.GONE);
			break;
			
		case R.id.btnPickMeUpHere:
			updateOptions();
			Toast.makeText(getActivity(), "Trip options updated.", Toast.LENGTH_LONG).show();
			dismiss();
			break;
		default:
			break;
		}
		
	}

	private void updateOptions() {
		numBags = String.valueOf(pickNumBags.getValue());
		numChildSeats = String.valueOf(pickNumChildSeats.getValue());
		numPassengers = String.valueOf(pickNumPassenger.getValue());	
		Log.e("Num bags", numBags);
		Log.e("Num ppl", numPassengers);
		Log.e("Num child seats", numChildSeats);
		Log.e("isCustom time", time);
		Log.e("selected time", selectedTime);
		TripDetails details = parent.getDefaultTripDetails();
		details.setNumBags(numBags);
		details.setNumChildSeats(numChildSeats);
		details.setNumPassengers(numPassengers);
		details.setLaterRequest((time.contains("Later")));
		details.setCustomTrip(true);
		parent.setDefaultTripDetails(details);
	}

	public String getNumPassengers() {
		return numPassengers;
	}

	public void setNumPassengers(String numPassengers) {
		this.numPassengers = numPassengers;
	}

	public String getNumBags() {
		return numBags;
	}

	public void setNumBags(String numBags) {
		this.numBags = numBags;
	}

	public String getNumChildSeats() {
		return numChildSeats;
	}

	public void setNumChildSeats(String numChildSeats) {
		this.numChildSeats = numChildSeats;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Override
	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		Calendar currdate = Calendar.getInstance();
		Calendar receDate = Calendar.getInstance();
		receDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
		receDate.set(Calendar.MINUTE, minute);
		String timevalue = String.valueOf(hourOfDay).concat(":").concat(String.valueOf(minute));
		
		if(receDate.after(currdate))
		{
			selectedTime = timevalue;
			long diff = receDate.getTimeInMillis() - currdate.getTimeInMillis();
			long margin = 60*1000*15;
			if(diff > margin)
			{
				//CommonUtilities.displayAlert(getActivity(), "Your current time is more then 15 minutes", "Delayed request", "I understand", "Cancel", true);
				isCustomTime = true;
				time = "Later";
				
				Log.e("Time picker:", "Custom time is more then 15 minutes");
			}else{
				isCustomTime = false;
				time = "Now";
				long minutes = diff / (60*1000);
				Log.e("Minutes margin:", String.valueOf(minutes));
			}
		}else{
			time = defaultTimeText;
			return;
		}
		timeText = time.concat(" at ").concat(timevalue);
	}

	
}
