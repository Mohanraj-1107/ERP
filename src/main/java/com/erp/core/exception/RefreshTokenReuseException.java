package com.erp.core.exception;

public class RefreshTokenReuseException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public RefreshTokenReuseException(String message) {
		super(message);
	}
}
