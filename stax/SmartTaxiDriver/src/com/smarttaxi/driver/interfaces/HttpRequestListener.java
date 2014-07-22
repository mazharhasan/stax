package com.smarttaxi.driver.interfaces;

import com.smarttaxi.driver.entities.CustomHttpException;
import com.smarttaxi.driver.utils.HttpAsyncTask;


public interface HttpRequestListener {

	public Object makeRequest(HttpAsyncTask httpAsyncTask) throws CustomHttpException;
}
