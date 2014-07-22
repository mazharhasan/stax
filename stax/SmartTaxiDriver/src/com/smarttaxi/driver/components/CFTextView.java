package com.smarttaxi.driver.components;


import com.smarttaxi.driver.interfaces.ICustomFontTextField;
import com.smarttaxi.driver.utils.Typefaces;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class CFTextView extends TextView implements ICustomFontTextField {
	private static Typeface mTypeface;
	private String fontName = "OpenSans-Regular.ttf";
	public CFTextView(Context context) {
		super(context);
		setupFont();
	}

	public CFTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setClickable(false);
		this.setFocusable(false);
		this.setFocusableInTouchMode(false);
		setupFont();
	}

	public CFTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setupFont();
	}
	
	private void setupFont() {
		if (mTypeface == null) {
	         mTypeface = Typefaces.get(getContext(), getFontName());
	     }
		//setTextAppearance(getContext(), R.style.FormLabels); 
		setTypeface(mTypeface);
	    this.refreshDrawableState();
	}
	
	@Override
	public void setTextSize(float fontSize)
	{
		super.setTextSize(fontSize);
	}
	
	public String getFontName() {
		
		return fontName;
	}

	public void setFontName(String fontName) {
		this.fontName = fontName;
		mTypeface = null;
		setupFont();
	}
	


}
