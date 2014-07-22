package com.smarttaxi.driver.utils;

public class Response {
	private final Boolean status;
	private final String response;
	private final String message;
	public Response(){
		status=false;
		response = message = "";
	}
	public Response(Boolean status, String message, String response) {
		super();
		this.status = status;
		this.response = response;
		this.message = message;
	}
	
	public Response(Boolean status, String message) {
		super();
		this.status = status;
		this.message = message;
		response = "";
	}
	public Boolean getStatus() {
		return status;
	}

	public String getResponse() {
		return response;
	}

	public String getMessage() {
		return message;
	}

}
