package com.smart.taxi.interfaces;

import android.accounts.NetworkErrorException;

import com.smart.taxi.entities.CustomHttpException;
import com.smart.taxi.utils.HttpAsyncTask;



public interface HttpRequestListener {

	public Object makeRequest(HttpAsyncTask httpAsyncTask) throws CustomHttpException, NetworkErrorException;
}
