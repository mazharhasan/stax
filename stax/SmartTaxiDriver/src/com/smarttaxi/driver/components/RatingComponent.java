package com.smarttaxi.driver.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.smarttaxi.driver.R;

public class RatingComponent extends LinearLayout{
	
	ImageView[] imgViewStars;

	public RatingComponent(Context context) {
		super(context);
		initUI();
	}
	
	public RatingComponent(Context context, AttributeSet attrs) {
		super(context, attrs);
		initUI();
	}
		
	private void initUI() {
		LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = layoutInflater.inflate(R.layout.rating_component,null);
		imgViewStars = new ImageView[5];
		imgViewStars[0] = (ImageView)v.findViewById(R.id.imgViewStar1);
		imgViewStars[1] = (ImageView)v.findViewById(R.id.imgViewStar2);
		imgViewStars[2] = (ImageView)v.findViewById(R.id.imgViewStar3);
		imgViewStars[3] = (ImageView)v.findViewById(R.id.imgViewStar4);
		imgViewStars[4] = (ImageView)v.findViewById(R.id.imgViewStar5);
		this.addView(v);
	}
	
	public void setRating(float rating) {
		if(rating < 0)
			rating = 0.0f;
		if(rating > 5)
			rating = 5.0f;
		
		int i = 0 ;
		int fullRatings = (int)rating;
		
		for(;i < fullRatings; i++) {
			imgViewStars[i].setImageResource(R.drawable.star_active);
		}
		if(i >= imgViewStars.length)
			return;
		
		if(rating % 1 != 0) {
			imgViewStars[i].setImageResource(R.drawable.star_half);
			i++;
		}
		
		if(i >= imgViewStars.length)
			return;
		
		for(;i < imgViewStars.length ; i++) {
			imgViewStars[i].setImageResource(R.drawable.star);
		}
	}

}
