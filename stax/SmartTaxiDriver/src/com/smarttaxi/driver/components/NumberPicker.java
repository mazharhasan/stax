package com.smarttaxi.driver.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smarttaxi.driver.R;

public class NumberPicker extends LinearLayout implements OnClickListener{
	
	TextView txtViewValue;
	Button btnAdd;
	Button btnSubtract;
	int value;
	int minValue;
	int maxValue;
	
	public NumberPicker(Context context) {
		super(context);
		init();
	}
	
	public NumberPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init() {
		LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = layoutInflater.inflate(R.layout.number_picker,null);
		
		txtViewValue = (TextView)v.findViewById(R.id.txtValue);
		btnAdd = (Button)v.findViewById(R.id.btnAdd);
		btnAdd.setOnClickListener(this);
		btnSubtract = (Button)v.findViewById(R.id.btnSubtract);
		btnSubtract.setOnClickListener(this);
		setValue(0);
		setMinValue(0);
		setMaxValue(10);
		this.addView(v);	
	}
	
	public void setValue(int value) {
		this.value = value;
		txtViewValue.setText(String.valueOf(value));	
	}
	
	public int getValue() {
		return this.value;
	}
	
	public void setMinValue(int value) {
		this.minValue = value;
	}
	
	public void setMaxValue(int value) {
		this.maxValue = value;
	}
	
	private void onAdd() {
		if(value == maxValue)
			return;
		setValue(++value);
	}
	
	private void onSubtract() {
		if(value == minValue)
			return;
		setValue(--value);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnAdd:
			v.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					onAdd();
				}
			});
			break;
		case R.id.btnSubtract:
			v.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					onSubtract();
				}
			});
			break;
		default:
			break;
		}

	}
	
	


}
