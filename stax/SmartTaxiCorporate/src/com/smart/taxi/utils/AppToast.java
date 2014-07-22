package com.smart.taxi.utils;

import android.content.Context;
import android.widget.Toast;

public class AppToast {
	public static void ShowToast(Context context, String message){
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}
}
