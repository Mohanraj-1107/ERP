//$Id$
package com.erp.core.exception;

public class InvalidPathVariableException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public InvalidPathVariableException(String message) {
		super(message);
	}
	
	public InvalidPathVariableException(String message,Throwable cause) {
		super(message,cause);
	}
}
