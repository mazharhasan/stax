package com.smarttaxi.driver.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.smarttaxi.driver.utils.NetworkAvailability;

public class ConnectionChangeListener extends BroadcastReceiver {

	public static boolean isAvailableCalled=false;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
			if (!isAvailableCalled)
			{
				isAvailableCalled = true;
				NetworkAvailability.IsNetworkAvailable(context);
			}
			else
				isAvailableCalled = false;
	}
}