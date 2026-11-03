//$Id$
package com.erp.core.exception;

public class LoginFailedException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public LoginFailedException(String message) {
		super(message);
	}
	
	public LoginFailedException(String message,Throwable cause) {
		super(message,cause);
	}
}
