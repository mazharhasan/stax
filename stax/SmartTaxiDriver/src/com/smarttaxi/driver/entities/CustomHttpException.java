package com.smarttaxi.driver.entities;

public class CustomHttpException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5591672778020207828L;
	
	protected String methodName;
	protected String message;

	public CustomHttpException(String methodName, String message) {
		this.methodName = methodName;
		this.message = message;
	}
	
	
	public String getMethodName() {
		return methodName;
	}
	
	public String getMessage() {
		return message;
	}
	
	@Override
	public String toString() {
		return this.message;		
	}

}
