package com.smart.taxi.fragments;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.smarttaxi.client.R;
import com.smart.taxi.activities.SplashActivity;
import com.smart.taxi.components.CFEditText;
import com.smart.taxi.utils.Utils;

public class PinAndJourneyTypeDialogue extends DialogFragment implements OnItemSelectedListener {
	
	CFEditText pin;
	CFEditText message;
	String journeyType = "";
	private View view;
	private String cabId;
	private FindARideFragment parent;
	private boolean isCorporateJourney = false;
	private Spinner journeyTypeSpinner;
	private LinearLayout llFileNumberContents;
	public PinAndJourneyTypeDialogue() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    setDialogueView(inflater.inflate(R.layout.dialoge_pin_and_journey_types, null)); 
	    builder.setView(getDialogueView());
	    ArrayAdapter<String> journeyOptions = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item);
	    if(SplashActivity.loggedInUser.getJourneyTypes().size() > 0)
	    {
	    	List<String> options = SplashActivity.loggedInUser.getJourneyTypes();
	    	for(int i = 0; i < options.size(); i++)
	    	{
	    		journeyOptions.add(options.get(i));
	    	}
	    }
	    journeyOptions.add("Enter file no.");
	    journeyOptions.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    journeyTypeSpinner = (Spinner)view.findViewById(R.id.spinnerJourneyType);
	    journeyTypeSpinner.setAdapter(journeyOptions);
	    journeyTypeSpinner.setOnItemSelectedListener(this);
	    
	    llFileNumberContents = (LinearLayout) view.findViewById(R.id.llFileNumberBox);
	    //final  ;//= (CFEditText)getView().findViewById(R.id.txtPin);
	    //(OnClickListener)getActivity()
	    // Add action buttons
	    builder.setPositiveButton("Proceed", null);
	    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                   PinAndJourneyTypeDialogue.this.getDialog().cancel();
	               }
	           }).setTitle("Confirm your credentials:");
	    final AlertDialog dialog = builder.create();
	    dialog.setOnShowListener(new DialogInterface.OnShowListener() {

	        @Override
	        public void onShow(DialogInterface dialogIn) {

	            Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
	            b.setOnClickListener(new View.OnClickListener() {

	                @Override
	                public void onClick(View view) {
	                    // TODO Do something
	                	CFEditText pinField = (CFEditText)getDialogueView().findViewById(R.id.txtPin);
	        			CFEditText messageField = (CFEditText)getDialogueView().findViewById(R.id.txtMessageForDriver);
	        			CFEditText fileNumField = (CFEditText)getDialogueView().findViewById(R.id.txtFileNumber);
	        			Spinner tripType = (Spinner)getDialogueView().findViewById(R.id.spinnerJourneyType);
	        			
	        			if(Utils.isEmptyOrNull(pinField.getText().toString()))
	        			{
	        				pinField.requestFocus();
	        				pinField.setError("Please provide a pin");
	        				return;
	        			}
	        			
	        			if(!pinField.getText().toString().equals(SplashActivity.loggedInUser.getPassword()))
	        			{
	        				pinField.requestFocus();
	        				pinField.setError("Incorrect pin");
	        				return;
	        			}
	        			
	        			if(tripType.getSelectedItemPosition() < 0)
	        			{
	        				return;
	        			}
	        			
	        			if(tripType.getSelectedItem().toString().equals("Enter file no.") && Utils.isEmptyOrNull(fileNumField.getText().toString()))
	        			{
	        				fileNumField.requestFocus();
	        				fileNumField.setError("Please enter a file number");
	        				return;
	        			}
	        			
	        			String journeyType = (llFileNumberContents.getVisibility() == View.VISIBLE)?
	        						fileNumField.getText().toString()
	        						:SplashActivity.loggedInUser.getJourneyTypes().get(tripType.getSelectedItemPosition());
	                    //Dismiss once everything is OK.
	        			parent.triggerPostPinOperations(pinField.getText().toString(), 
	                						messageField.getText().toString(), journeyType, "");
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
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		if(journeyTypeSpinner.getSelectedItem().toString().equals("Enter file no."))
		{
			llFileNumberContents.setVisibility(View.VISIBLE);
		}else{
			llFileNumberContents.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

	public void setCabId(String cabId) {
		this.cabId = cabId; 
		
	}

	public void setParent(FindARideFragment findARideActivity) {
		this.parent = findARideActivity;
		
	}

	public String getCabId() {
		// TODO Auto-generated method stub
		return this.cabId;
	}

	public void setIsCorporateJourney(boolean isCorporateJourney) {
		this.isCorporateJourney  = isCorporateJourney;
		
	}
}
