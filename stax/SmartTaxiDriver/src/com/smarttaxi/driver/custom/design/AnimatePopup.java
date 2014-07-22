package com.smarttaxi.driver.custom.design;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.style.SuperscriptSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.smarttaxi.driver.R;


public class AnimatePopup  {
	
	private static Animation animShow, animHide;
	
	private static LayoutInflater mInflater;
	private static TransparentPanel popup;
	
	
    
  /*  public static void GetPopUp(Activity activity)
    {
    	
    	popup = new TransparentPanel(activity);
    	mInflater =LayoutInflater.from(activity);
    	
    	//LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = mInflater.inflate(R.layout.activity_journey_detail_popup,null);
    	
    	popup = (TransparentPanel) v.findViewById(R.id.popup_window);

    	//  Start out with the popup initially hidden.
    	popup.setVisibility(View.GONE);
    	
    	
    	animShow = AnimationUtils.loadAnimation( activity, R.anim.popup_show);
    	animHide = AnimationUtils.loadAnimation( activity, R.anim.popup_hide);
    	
    	final Button   showButton = (Button)v.findViewById(R.id.show_popup_button);
    	final Button   hideButton = (Button)v.findViewById(R.id.hide_popup_button);
    	
    	
    	showButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				popup.setVisibility(View.VISIBLE);
				popup.startAnimation( animShow );
				showButton.setEnabled(false);
				hideButton.setEnabled(true);
        }});
        
        hideButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				popup.startAnimation( animHide );
				showButton.setEnabled(true);
				hideButton.setEnabled(false);
				popup.setVisibility(View.GONE);
        }});


    	final TextView locationName = (TextView) v.findViewById(R.id.location_name);
        final TextView locationDescription = (TextView) v.findViewById(R.id.location_description);
        
        locationName.setText("Animated Popup");
        locationDescription.setText("Animated popup is created by http://www.androidbysravan.blogspot.com"
        							+ " Transparent layout is used on this example, and animation xml is also used"
        							+ " on this example. Have a Good day guys.");
        
        popup.setVisibility(View.VISIBLE);
        
        popup.addView(v);
        
	}*/
}