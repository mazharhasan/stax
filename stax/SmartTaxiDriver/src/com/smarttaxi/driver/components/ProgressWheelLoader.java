package com.smarttaxi.driver.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.smarttaxi.driver.R;


public class ProgressWheelLoader extends RelativeLayout {
	
	ProgressWheel progressWheel;
	
	public ProgressWheelLoader(Context context) {
		super(context);
		initUI();
	}
	
	public ProgressWheelLoader(Context context, AttributeSet attrs) {
		super(context, attrs);
		initUI();
	}

	public ProgressWheelLoader(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initUI();
	}
	
	private void initUI() {
		LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = layoutInflater.inflate(R.layout.progress_wheel_loader,null);
		progressWheel = (ProgressWheel)v.findViewById(R.id.progressWheel);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		this.addView(v,layoutParams);
	}
	
	public void setProgress(int value) {
		float percent = ((float)value)/100.0f;
		int angle = (int)(percent*360);
		progressWheel.setProgress(angle);
		progressWheel.setText(value + "%");
	}
	
	@Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        return true;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent ev){
        return true;
    }

}
