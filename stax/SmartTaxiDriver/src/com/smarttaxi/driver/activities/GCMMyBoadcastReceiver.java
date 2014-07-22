package com.smarttaxi.driver.activities;

import android.content.Context;

import com.google.android.gcm.GCMBroadcastReceiver;

public class GCMMyBoadcastReceiver extends GCMBroadcastReceiver {
	protected String getGCMIntentServiceClassName(Context contest) {
		return "com.smarttaxi.driver.gcm.GCMIntentService";
	}
}