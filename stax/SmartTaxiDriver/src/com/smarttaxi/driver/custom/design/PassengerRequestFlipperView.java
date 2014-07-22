package com.smarttaxi.driver.custom.design;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.ViewFlipper;

import com.smarttaxi.driver.R;

public class PassengerRequestFlipperView extends Activity implements
		AdapterView.OnItemSelectedListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.animation_2);

		//mFlipper = ((ViewFlipper) this.findViewById(R.id.flipper));
		//mFlipper.startFlipping();

		//Spinner s = (Spinner) findViewById(R.id.spinner);
	//	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
			//	android.R.layout.simple_spinner_item, mStrings);
		//adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//s.setAdapter(adapter);
		//s.setOnItemSelectedListener(this);
	}

	public void onItemSelected(AdapterView<?> parent, View v, int position,
			long id) {
		switch (position) {

		case 0:
			mFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
					R.anim.in_from_right));
			mFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
					R.anim.out_to_right));
			break;
		case 1:
			mFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
					R.anim.in_from_left));
			mFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
					R.anim.out_to_left));
			break;
		case 2:
			mFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
					android.R.anim.fade_in));
			mFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
					android.R.anim.fade_out));
			break;
		default:
			mFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
					R.anim.popup_hide));
			mFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
					R.anim.popup_show));
			break;
		}
	}

	public void onNothingSelected(AdapterView<?> parent) {
	}

	private String[] mStrings = { "Push up", "Push left", "Cross fade",
			"Hyperspace" };

	private ViewFlipper mFlipper;

}