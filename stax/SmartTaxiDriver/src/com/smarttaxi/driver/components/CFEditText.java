package com.smarttaxi.driver.components;

import com.smarttaxi.driver.interfaces.ICustomFontTextField;
import com.smarttaxi.driver.utils.Typefaces;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

public class CFEditText extends EditText implements ICustomFontTextField {
	private static Typeface mTypeface;
	private String fontName = "OpenSans-Regular.ttf";
	public CFEditText(Context context) {
		super(context);
		setupFont();
	}

	public CFEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupFont();
	}

	public CFEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setupFont();
	}
	
	private void setupFont() {
		// TODO Auto-generated method stub
		if (mTypeface == null) {
	         mTypeface = Typefaces.get(getContext(), getFontName());
	     }
		setTypeface(mTypeface);
	    this.refreshDrawableState();
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
