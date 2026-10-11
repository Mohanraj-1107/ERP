//$Id$
package com.erp.core.exception;

public class RequestBodyNullException extends Exception{
	
	private static final long serialVersionUID = 1L;

	public RequestBodyNullException(String message) {
		super(message);
	}
	
	public RequestBodyNullException(String message,Throwable cause) {
		super(message,cause);
	}
}
