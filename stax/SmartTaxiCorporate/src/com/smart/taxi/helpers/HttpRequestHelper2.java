package com.smart.taxi.helpers;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import android.graphics.Bitmap;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.entity.UrlEncodedFormEntity;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.entity.mime.MultipartEntity;
import ch.boye.httpclientandroidlib.entity.mime.content.ByteArrayBody;
import ch.boye.httpclientandroidlib.entity.mime.content.StringBody;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;

import com.smart.taxi.constants.APIConstants;
import com.smart.taxi.utils.Applog;


public class HttpRequestHelper2 {
	
	public static boolean isErrorCode(int statusCode){

		if(statusCode == 400 || statusCode == 401 || statusCode == 403 || statusCode == 404 || statusCode == 405 || statusCode == 500)
			return true;
		else
			return false;
	}
	
	public static String httpPost(String url, List<BasicNameValuePair> params)
			throws Exception {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		post.setEntity(new UrlEncodedFormEntity(params));
		HttpResponse response = client.execute(post);
		
		int responseCode = response.getStatusLine().getStatusCode();
		System.out.print(response);
		if(isErrorCode(responseCode))
			throw new Exception();
//		String responseMessage = response.getStatusLine().getReasonPhrase();
		return getResponseString(response);
	}
	
	public static String httpPostWithImage(String url, List<BasicNameValuePair> params, Bitmap image, String fileName)
			throws Exception {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		
		MultipartEntity multiPartEntity = getMultiPartEntity(params);
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		
		multiPartEntity.addPart(APIConstants.KEY_FILE, new ByteArrayBody(byteArray, fileName));
		
		post.setEntity(multiPartEntity);
		HttpResponse response = client.execute(post);
		int responseCode = response.getStatusLine().getStatusCode();
		if(isErrorCode(responseCode))
			throw new Exception();
//		String responseMessage = response.getStatusLine().getReasonPhrase();
		return getResponseString(response);
	}
	
	public static String httpGet(String url)
			throws Exception {
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		HttpResponse response = client.execute(get);
		int responseCode = response.getStatusLine().getStatusCode();
		if(isErrorCode(responseCode))
			throw new Exception();
//		String responseMessage = response.getStatusLine().getReasonPhrase();
		return getResponseString(response);
	}
	
	private static MultipartEntity getMultiPartEntity(List<BasicNameValuePair> params) {
		MultipartEntity multiPart = new MultipartEntity();
		for (BasicNameValuePair nameValuePair : params) {
			try {
				multiPart.addPart(nameValuePair.getName(), 
						new StringBody(nameValuePair.getValue()));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return multiPart;
	}
	
	private static String getResponseString(HttpResponse response) throws IllegalStateException, IOException {
		InputStream inputStream = response.getEntity().getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder stringBuilder = new StringBuilder();
		String line = null;
		while((line = reader.readLine()) != null){
			stringBuilder.append(line + "\n");Applog.Debug("line " +line); 
		}
		inputStream.close();
		return stringBuilder.toString();
	}

}
