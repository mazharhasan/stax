package com.smart.taxi.utils;

import android.content.Context;
import android.widget.Toast;

public class Common {

	public static final long DURATION = 60000;
	public static void showToast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}


}
