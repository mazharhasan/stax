package com.smart.taxi.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.smart.taxi.entities.CustomHttpException;
import com.smart.taxi.entities.CustomHttpResponse;
import com.smart.taxi.interfaces.HttpResponseListener;

public class HttpService extends Service implements HttpResponseListener {

	public HttpService() {
		super();
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
	}

	@Override
	public void onResponse(CustomHttpResponse object) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onException(CustomHttpException exception) {
		// TODO Auto-generated method stub

	}

	@Override
	public IBinder onBind(Intent arg0) {
		Log.e("http service", "started");
		Toast.makeText(getBaseContext(), "HttpService started",
				Toast.LENGTH_SHORT).show();
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent i, int startId) {
		Log.e("Service","Started");
	}

}
