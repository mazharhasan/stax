package com.smart.taxi.entities;

public class CustomHttpResponse {

	String methodName;
	Object response;
	int statusCode;
	String responseMsg;
	private String rawJson;
	
	public CustomHttpResponse(Object response, String methodName, int statusCode, String responseMsg) {
		this.response = response;
		this.methodName = methodName;
		this.statusCode = statusCode;
		this.responseMsg = responseMsg;
	}
	
	
	public String getResponseMsg() {
		return responseMsg;
	}

	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}

	public int getStatusCode() {
		return statusCode;
	}


	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}


	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public Object getResponse() {
		return response;
	}
	public void setResponse(Object response) {
		this.response = response;
	}


	public String getRawJson() {
		return rawJson;
	}


	public void setRawJson(String rawJson) {
		this.rawJson = rawJson;
	}
}
