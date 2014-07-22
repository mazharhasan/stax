package com.smarttaxi.driver.interfaces;

import com.smarttaxi.driver.entities.CustomHttpException;
import com.smarttaxi.driver.entities.CustomHttpResponse;


public interface HttpResponseListener {

	public void onResponse(CustomHttpResponse object);
	public void onException(CustomHttpException exception);
	
}
