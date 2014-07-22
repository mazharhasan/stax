package com.smarttaxi.driver.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smarttaxi.driver.R;

public class CabInfoWindow extends RelativeLayout{
	
	TextView txtDriverName;
	RatingComponent ratingComponentInfoWindow;

	public CabInfoWindow(Context context) {
		super(context);
		initUI();
	}
	
	public CabInfoWindow(Context context, AttributeSet attrs) {
		super(context, attrs);
		initUI();
	}
	
	public CabInfoWindow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initUI();
	}
	
	private void initUI() {
		LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = layoutInflater.inflate(R.layout.cab_info_window,null);
		
		txtDriverName = (TextView)v.findViewById(R.id.txtDriverName);
		ratingComponentInfoWindow = (RatingComponent)v.findViewById(R.id.ratingComponentInfoWindow);
		this.addView(v);
	}
	
	public void setDriverName(String driverName) {
		txtDriverName.setText(driverName);
	}
	
	public void setRating(float d) {
		ratingComponentInfoWindow.setRating(d);
	}
	
	public Bitmap getBitmap() {
		this.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), 
	            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
	
		Bitmap b = Bitmap.createBitmap( this.getMeasuredWidth(), this.getMeasuredHeight(), Bitmap.Config.ARGB_8888);                
		Canvas c = new Canvas(b);
		this.layout(0, 0, this.getMeasuredWidth(), this.getMeasuredHeight()); 
		this.draw(c);
		return b;
	}
	

}
