package com.smart.taxi.receiver;

import android.content.Context;

import com.google.android.gcm.GCMBroadcastReceiver;

public class GCMMyBoadcastReceiver extends GCMBroadcastReceiver {
	protected String getGCMIntentServiceClassName(Context contest) {
		return "com.smart.taxi.services.GCMIntentService";
	}
}