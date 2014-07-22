package com.smarttaxi.driver.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.smarttaxi.driver.utils.NetworkAvailability;

public class GpsLocationReceiver extends BroadcastReceiver 
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		NetworkAvailability.IsGpsEnabled(context);
	}
}