package com.smart.taxi.interfaces;

import com.smart.taxi.entities.CustomHttpException;
import com.smart.taxi.entities.CustomHttpResponse;



public interface HttpResponseListener {

	public void onResponse(CustomHttpResponse object);
	public void onException(CustomHttpException exception);
}
