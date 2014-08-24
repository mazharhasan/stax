package com.smart.taxi.activities;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Build;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.smart.taxi.constants.APIConstants;
import com.smart.taxi.entities.CustomHttpException;
import com.smart.taxi.entities.CustomHttpResponse;
import com.smart.taxi.helpers.HttpRequestHelper2;
import com.smart.taxi.helpers.JsonHelper;
import com.smart.taxi.interfaces.HttpRequestListener;
import com.smart.taxi.interfaces.HttpResponseListener;
import com.smart.taxi.utils.HttpAsyncTask;
import com.smart.taxi.utils.Utils;

public class CustomHttpClass implements HttpResponseListener, HttpRequestListener {
	
	public CustomHttpClass() {
		// TODO Auto-generated constructor stub
	}
	
	public static HttpAsyncTask runPostService(final HttpResponseListener sender,
			final String apiMethodName, 
			final List<BasicNameValuePair> args,
			final boolean requestBodyObject,
			final boolean isArray)
	{
		return runPostWithURL(sender, apiMethodName, args, requestBodyObject, isArray, false);
	}
	
	
	
	private static HttpAsyncTask runPostWithURL(final HttpResponseListener sender,
			final String apiMethodName, 
			final List<BasicNameValuePair> args,
			final boolean requestBodyObject,
			final boolean isArray, final boolean isDirect)
	{
		final String methodName = apiMethodName;
		final ArrayList<BasicNameValuePair> params = (ArrayList<BasicNameValuePair>) args;
		final String url = (isDirect)?methodName:getCompleteURLForMethod(methodName);
		HttpAsyncTask task = new HttpAsyncTask(sender, new HttpRequestListener() {

			@Override
			public Object makeRequest(HttpAsyncTask httpAsyncTask)
					throws CustomHttpException {
				if (httpAsyncTask.isCancelled())
					return null;
				
				try {
					Object results ;
					String response = HttpRequestHelper2.httpPost(url, params);

					if (Utils.isEmptyOrNull(response)) {
						throw new Exception();
					}

					String responseMessage = getMessageFromResponseHeader(response);
					JsonObject resultingObject = null;
					int responseCode = getCodeFromResponseHeader(response);
					boolean status = (responseCode == APIConstants.SUCESS_CODE);
					if (isErrorCode(responseCode)) {
						throw new Exception(responseMessage);
					} else if (status) {
						resultingObject = (requestBodyObject)?getJsonBody(response):new JsonObject();
					}
					results = resultingObject;
					CustomHttpResponse httpResponse = new CustomHttpResponse(results, methodName, responseCode, responseMessage);
					httpResponse.setRawJson(response);
					return httpResponse;
				} catch (Exception e) {
					e.printStackTrace();
					return new CustomHttpException(methodName, e.getMessage());
				}
			}
			
			


			private boolean isErrorCode(int statusCode) {
				if (statusCode == 400 || statusCode == 401 || statusCode == 403
						|| statusCode == 404 || statusCode == 405 || statusCode == 500
						|| statusCode == 1061 )
					return true;
				else
					return false;
			}

			private int getCodeFromResponseHeader(String response) {
				if (Utils.isEmptyOrNull(response))
					return -1;

				JsonObject mainObject = JsonHelper.parseToJsonObject(response);

				if (!mainObject.has(APIConstants.KEY_HEADER))
					return -1;

				JsonObject headerObject = JsonHelper.getJsonObject(mainObject,
						APIConstants.KEY_HEADER);

				if (!headerObject.has(APIConstants.KEY_CODE))
					return -1;

				return Integer.valueOf(JsonHelper.getString(headerObject,
						APIConstants.KEY_CODE));
			}

			private String getMessageFromResponseHeader(String response) {
				if (Utils.isEmptyOrNull(response))
					return "";

				JsonObject mainObject = JsonHelper.parseToJsonObject(response);

				if (!mainObject.has(APIConstants.KEY_HEADER))
					return "";
				JsonObject headerObject = JsonHelper.getJsonObject(mainObject,
						APIConstants.KEY_HEADER);

				if (!headerObject.has(APIConstants.KEY_MESSAGE))
					return "";

				return JsonHelper.getString(headerObject, APIConstants.KEY_MESSAGE);
			}
			
		});
		return executeAsyncTask(task);
	}
	
	private static HttpAsyncTask executeAsyncTask(HttpAsyncTask httpAsyncTask) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			return (HttpAsyncTask) httpAsyncTask.execute();
		} else {
			return (HttpAsyncTask) httpAsyncTask
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}
	
	public static JsonObject getJsonObjectFromBody(final String response,
			final String key) {
		if (Utils.isEmptyOrNull(response))
			return null;

		JsonObject mainObject = JsonHelper.parseToJsonObject(response);

		if (!mainObject.has(APIConstants.KEY_BODY))
			return null;
		JsonObject bodyObject = JsonHelper.getJsonObject(mainObject,
				APIConstants.KEY_BODY);

		if (!bodyObject.has(key))
			return null;

		return JsonHelper.getJsonObject(bodyObject, key);
	}
	
	public static JsonObject getJsonBody(final String response) {
		if (Utils.isEmptyOrNull(response))
			return null;

		JsonObject mainObject = JsonHelper.parseToJsonObject(response);

		if (!mainObject.has(APIConstants.KEY_BODY))
			return null;
		JsonObject bodyObject = JsonHelper.getJsonObject(mainObject,
				APIConstants.KEY_BODY);
		return bodyObject;
		//return JsonHelper.getJsonObject(bodyObject, key);
	}
	
	public static JsonArray getJsonArrayFromBody(String response, String key) {
		if (Utils.isEmptyOrNull(response))
			return null;

		JsonObject mainObject = JsonHelper.parseToJsonObject(response);

		if (!mainObject.has(APIConstants.KEY_BODY))
			return null;
		JsonObject bodyObject = JsonHelper.getJsonObject(mainObject,
				APIConstants.KEY_BODY);

		if (!bodyObject.has(key))
			return null;

		return JsonHelper.getJsonArray(bodyObject, key);
	}
	
	private static String getCompleteURLForMethod(String methodName) {
		return APIConstants.API_END_POINT + methodName;
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
	public Object makeRequest(HttpAsyncTask httpAsyncTask)
			throws CustomHttpException {
		// TODO Auto-generated method stub
		return null;
	}

	public static HttpAsyncTask runDirectPostService(final HttpResponseListener sender,
			final String apiMethodName, 
			final List<BasicNameValuePair> args,
			final boolean requestBodyObject,
			final boolean isArray)
	{
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		return runPostWithURL(sender, apiMethodName, params, requestBodyObject, isArray, true);
	}
	
	

}
