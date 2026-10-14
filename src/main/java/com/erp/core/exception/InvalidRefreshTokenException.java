package com.erp.core.exception;

public class InvalidRefreshTokenException extends RuntimeException {
	public InvalidRefreshTokenException(String message) {
		super(message);
	}
}
