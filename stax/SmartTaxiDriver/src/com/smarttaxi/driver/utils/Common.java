package com.smarttaxi.driver.utils;

import android.content.Context;
import android.widget.Toast;

public class Common {

	public static final long DURATION = 10000;
	public static void showToast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}


}
