package com.smart.taxi.utils;

import android.os.AsyncTask;

import com.smart.taxi.entities.CustomHttpException;
import com.smart.taxi.entities.CustomHttpResponse;
import com.smart.taxi.interfaces.HttpProgressListener;
import com.smart.taxi.interfaces.HttpRequestListener;
import com.smart.taxi.interfaces.HttpResponseListener;



public class HttpAsyncTask extends AsyncTask<Object, Object, Object> {
	
	HttpResponseListener httpResponseListener;
	HttpRequestListener httpRequestListener;
	HttpProgressListener httpProgressListener;
	
	public HttpAsyncTask(HttpResponseListener httpResponseListener, HttpRequestListener httpRequestListener) {
		this.httpResponseListener = httpResponseListener;
		this.httpRequestListener = httpRequestListener;
	}
	
	public HttpAsyncTask(HttpResponseListener httpResponseListener,
			HttpRequestListener httpRequestListener,
			HttpProgressListener progressListener) {
		this.httpResponseListener = httpResponseListener;
		this.httpRequestListener = httpRequestListener;
		this.httpProgressListener = progressListener;
	}

	@Override
	protected Object doInBackground(Object... objects) {
		try {
			return httpRequestListener.makeRequest(this);
		} catch (Exception e) {			
			return e;
		}
	}
	
	public void updateProgress(Object value) {
		publishProgress(value);
	}
	
	@Override
	protected void onProgressUpdate(Object... progress) {
		if(httpProgressListener == null)
			return;
		httpProgressListener.onProgress(progress[0]);
	}
	
	@Override
	protected void onPostExecute(Object result) {
		if(httpResponseListener == null)
			return;
		
		if(result instanceof CustomHttpException) {
			httpResponseListener.onException((CustomHttpException)result);
			return;
		}
				
		if(result != null) {
			httpResponseListener.onResponse((CustomHttpResponse)result);
		}		
	}
	
}