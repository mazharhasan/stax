package com.smart.taxi.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smart.taxi.R;

public class LeftSlideMenuItem extends RelativeLayout {

	public ImageView imgIcon;
	TextView txtName;
	
	public LeftSlideMenuItem(Context context) {
		super(context);
		init();
	}
	
	public LeftSlideMenuItem(Context context,AttributeSet attributeSet) {
		super(context, attributeSet);
		init();
		TypedArray a = context.getTheme().obtainStyledAttributes(attributeSet,R.styleable.leftSideMenuItem,
		        0, 0);
		Drawable icon = null;
		String text = "";

	   try {
		   text = a.getString(R.styleable.leftSideMenuItem_rel_title);
		   icon = a.getDrawable(R.styleable.leftSideMenuItem_rel_icon);
	   } finally {
	       a.recycle();
	   }
	   if(icon!=null)
		   imgIcon.setImageDrawable(icon);
	   
	   if(text!=null)
		   txtName.setText(text);
	}
	
	public LeftSlideMenuItem(Context context,AttributeSet attributeSet, int defStyle) {
		super(context, attributeSet,defStyle);
		
		init();
		TypedArray a = context.getTheme().obtainStyledAttributes(
				attributeSet,
		        R.styleable.leftSideMenuItem,
		        0, 0);
		int iconRes = 0;
		String text = "";

	   try {
		   text = a.getString(R.styleable.leftSideMenuItem_rel_title);
		   iconRes = a.getInteger(R.styleable.leftSideMenuItem_rel_icon, 0);
	   } finally {
	       a.recycle();
	   }
	   imgIcon.setImageResource(iconRes);
	   txtName.setText(text);

	}
	
	private void init() {
		LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = layoutInflater.inflate(R.layout.drawer_list_item,null);

		//imgIcon = (ImageView)v.findViewById(R.id.imgIcon);
		//txtName = (TextView)v.findViewById(R.id.txtNavTitle);
		//txtName.setTextAppearance(getContext(), R.style.TextStylePlain);
		this.addView(v);	
	}
	
	public void setTitle(String text) {
		txtName.setText(text);
	}
	
	
	public void setIconDrawable(Drawable icon) {
		imgIcon.setImageDrawable(icon);		
	}
	
	public void setIconBitmap(Bitmap bitmap) {
		imgIcon.setImageBitmap(bitmap);
	}
	
	public void setIconResource(int iconRes) {
		imgIcon.setImageResource(iconRes);
	}
	
}
