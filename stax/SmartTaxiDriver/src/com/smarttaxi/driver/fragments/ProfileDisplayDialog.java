package com.smarttaxi.driver.fragments;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.smarttaxi.driver.R;
import com.smarttaxi.driver.activities.MainActivity;
import com.smarttaxi.driver.components.CFTextView;
import com.smarttaxi.driver.entities.User;
import com.smarttaxi.driver.utils.Utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Spinner;

public class ProfileDisplayDialog extends DialogFragment {
	
	private View view;
	private Spinner journeyTypeSpinner;
	private User userInfo;
	public MainActivity parent;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy); 
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    setDialogueView(inflater.inflate(R.layout.dialog_direct_profile, null)); 
	    builder.setView(getDialogueView());
	    ArrayAdapter<String> journeyOptions = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item);
	    if(this.userInfo != null)
	    {
	    	if(userInfo.getJourneyTypes() != null)
	    	{
	    		for(int i = 0; i < userInfo.getJourneyTypes().size(); i++)
	    		{
	    			journeyOptions.add(userInfo.getJourneyTypes().get(i));
	    		}
	    	}
	    	if(!Utils.isEmptyOrNull(userInfo.getUserImage()) && !userInfo.getUserImage().equals("0"))
	    	{
	    		URL thumb_u;
				try {
					thumb_u = new URL(userInfo.getUserImage());
					Drawable thumb_d = Drawable.createFromStream(thumb_u.openStream(), "src");
		    		((ImageView)view.findViewById(R.id.profileImage)).setImageDrawable(thumb_d);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    
	    	}
	    	((CFTextView)view.findViewById(R.id.labelDirectProfileUserName)).setText(userInfo.getFullName());
	    	((CFTextView)view.findViewById(R.id.labelCorporateName)).setText(userInfo.getCorporateName());
	    	
	    }
	    journeyTypeSpinner = (Spinner)view.findViewById(R.id.spinnerJourneyType);
	    journeyTypeSpinner.setAdapter(journeyOptions);
	    //final  ;//= (CFEditText)getView().findViewById(R.id.txtPin);
	    //(OnClickListener)getActivity()
	    // Add action buttons
	    builder.setPositiveButton("Proceed", null);
	    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                   ProfileDisplayDialog.this.getDialog().cancel();
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
	                	parent.createDirectJourney(userInfo.getId(), userInfo.getJourneyTypes().get(journeyTypeSpinner.getSelectedItemPosition()));
	                	dialog.dismiss();
	                }
	            });
	        }
	    });
	    return dialog;
	}
	
	public View getDialogueView()
	{
		return view;
	}
	
	public void setDialogueView(View view) {
		this.view = view;
	}

	public void setUserInfo(User customer) {
		// TODO Auto-generated method stub
		this.userInfo = customer;
		
	}

}
