package com.smarttaxi.driver.helpers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.NameValuePair;

import android.graphics.Bitmap;

import com.smarttaxi.driver.constants.APIConstants;

public class HttpRequestHelper {

	public static boolean isErrorCode(int statusCode){

		if(statusCode == 400 || statusCode == 401 || statusCode == 403 || statusCode == 404 || statusCode == 405 || statusCode == 500)
			return true;
		else
			return false;
	}
	
	public static HashMap<String, String> httpPost (String url, Vector<NameValuePair> params) throws Exception {		
		HttpURLConnection conn = (HttpURLConnection) (new URL(url).openConnection());
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		writeRequestParameters(conn, params);
			
		if(isErrorCode(conn.getResponseCode())){
			conn.disconnect();
			throw new Exception(conn.getResponseMessage());
		}
		
		HashMap<String, String> resposne = new HashMap<String, String>();
			
		resposne.put(APIConstants.KEY_HTTP_STATUS_CODE, ""+conn.getResponseCode());
		resposne.put(APIConstants.KEY_HTTP_RESPONSE_MSG, conn.getResponseMessage());
		resposne.put(APIConstants.KEY_HTTP_RESPONSE, getResponseString(conn));
		
		conn.disconnect();
		return resposne;
	}
	
	public static HashMap<String, String> httpPost2 (String url, Vector<NameValuePair> params, Bitmap bitmap) throws Exception {		
		HttpURLConnection conn = (HttpURLConnection) (new URL(url).openConnection());
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		writeRequestParameters2(conn, params, bitmap);
			
		if(isErrorCode(conn.getResponseCode())){
			conn.disconnect();
			throw new Exception(conn.getResponseMessage());
		}
		
		HashMap<String, String> resposne = new HashMap<String, String>();
			
		resposne.put(APIConstants.KEY_HTTP_STATUS_CODE, ""+conn.getResponseCode());
		resposne.put(APIConstants.KEY_HTTP_RESPONSE_MSG, conn.getResponseMessage());
		resposne.put(APIConstants.KEY_HTTP_RESPONSE, getResponseString(conn));
		
		conn.disconnect();
		return resposne;
	}


	public static HashMap<String, String> httpsGet (String url) throws Exception {
		trustAllHostsForHttpsURLConnection();
		HttpsURLConnection conn = (HttpsURLConnection) (new URL(url).openConnection());	
		
		if(isErrorCode(conn.getResponseCode()))
			throw new Exception(conn.getResponseMessage());
		
		HashMap<String, String> resposne = new HashMap<String, String>();
			
		resposne.put(APIConstants.KEY_HTTP_STATUS_CODE, ""+conn.getResponseCode());
		resposne.put(APIConstants.KEY_HTTP_RESPONSE_MSG, conn.getResponseMessage());
		resposne.put(APIConstants.KEY_HTTP_RESPONSE, getResponseString(conn));
		
		return resposne;
	}
	
	public static HashMap<String, String> httpsPost (String url, Vector<NameValuePair> params) throws Exception {		
		trustAllHostsForHttpsURLConnection();
		HttpsURLConnection conn = (HttpsURLConnection) (new URL(url).openConnection());
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		writeRequestParameters(conn, params);
			
		if(isErrorCode(conn.getResponseCode()))
			throw new Exception(conn.getResponseMessage());
		
		HashMap<String, String> resposne = new HashMap<String, String>();
			
		resposne.put(APIConstants.KEY_HTTP_STATUS_CODE, ""+conn.getResponseCode());
		resposne.put(APIConstants.KEY_HTTP_RESPONSE_MSG, conn.getResponseMessage());
		resposne.put(APIConstants.KEY_HTTP_RESPONSE, getResponseString(conn));
		
		return resposne;
	}
	
	public static HashMap<String, String> httpsDelete (String url) throws Exception {
		trustAllHostsForHttpsURLConnection();		
		HttpsURLConnection conn = (HttpsURLConnection) (new URL(url).openConnection());
		conn.setRequestMethod("DELETE");	
		
		if(isErrorCode(conn.getResponseCode()))
			throw new Exception(conn.getResponseMessage());
		
		HashMap<String, String> resposne = new HashMap<String, String>();
			
		resposne.put(APIConstants.KEY_HTTP_STATUS_CODE, ""+conn.getResponseCode());
		resposne.put(APIConstants.KEY_HTTP_RESPONSE, getResponseString(conn));
		
		return resposne;
	}
	
	private static void trustAllHostsForHttpsURLConnection() {
		
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] {
				new X509TrustManager() {
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return new java.security.cert.X509Certificate[] {};
						}
					public void checkClientTrusted(X509Certificate[] chain,
							String authType) throws CertificateException {
						
					}
					public void checkServerTrusted(X509Certificate[] chain,
							String authType) throws CertificateException {
						
					}
				}
		};
		
		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String getResponseString(HttpURLConnection conn) throws IOException {
		String response = "";
		Scanner inStream = new Scanner(conn.getInputStream());
		while (inStream.hasNextLine())
			response += (inStream.nextLine());
		
		return response;
	}
	
	
	private static void writeRequestParameters(HttpURLConnection conn,Vector<NameValuePair> params) throws IOException {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < params.size(); i++) {
			if (i > 0)
				stringBuilder.append("&");
			NameValuePair nameValuePair = params.elementAt(i);
			stringBuilder.append(nameValuePair.getName());
			stringBuilder.append("=");
			stringBuilder.append(URLEncoder.encode(
					nameValuePair.getValue(), "UTF-8"));
		}

		String parametersString = stringBuilder.toString();
		
		conn.setFixedLengthStreamingMode(parametersString.getBytes().length);
		conn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		
		PrintWriter out = new PrintWriter(conn.getOutputStream());
		out.print(parametersString);
		out.close();
	}
	
	private static void writeRequestParameters2(HttpURLConnection conn,Vector<NameValuePair> params, Bitmap bitmap) throws IOException {
		String BOUNDRY = "==================================";
		
		StringBuffer requestBody = new StringBuffer();
		for (int i = 0; i < params.size(); i++) {
			if (i > 0)
				requestBody.append("&");
			NameValuePair nameValuePair = params.elementAt(i);
			requestBody.append(nameValuePair.getName());
			requestBody.append("=");
			requestBody.append(URLEncoder.encode(
					nameValuePair.getValue(), "UTF-8"));
		}
		requestBody.append("&");
		requestBody.append("file");
		requestBody.append("=");
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
	    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
	    byte[] bitmapdata = stream.toByteArray();
		requestBody.append(new String(bitmapdata));

		String parametersString = requestBody.toString();
		
		conn.setFixedLengthStreamingMode(parametersString.getBytes().length);
//		conn.setRequestProperty("Content-Type",
//				"application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDRY);
		PrintWriter out = new PrintWriter(conn.getOutputStream());
		out.print(parametersString);
		out.close();
	}
	

}
