//$Id$
package com.erp.core.exception;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.erp.core.entity.common.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
	 
	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	/*
	 * 400 Client Side Exceptions
	 */
	@ExceptionHandler(InvalidPathVariableException.class)
	public static ResponseEntity<ErrorResponse> handlerInvalidPathVariableException(InvalidPathVariableException e,HttpServletRequest request){
	      ErrorResponse response=new ErrorResponse(e.getMessage(),HttpStatus.BAD_REQUEST.value(),request.getRequestURI(),Instant.now());
	      logger.warn("Invalid path variable: {}", e.getMessage());
	      return ResponseEntity.badRequest().body(response);
	}
	
	@ExceptionHandler(RequestBodyNullException.class)
	public static ResponseEntity<ErrorResponse> handleRequestBodyNullException(RequestBodyNullException e,HttpServletRequest request){
	      ErrorResponse response=new ErrorResponse(e.getMessage(),HttpStatus.BAD_REQUEST.value(),request.getRequestURI(),Instant.now());
	      logger.warn("Invalid request body: {}", e.getMessage());
	      return ResponseEntity.badRequest().body(response);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public static ResponseEntity<ErrorResponse> handleRequestBodyNullException(MethodArgumentNotValidException e,HttpServletRequest request){
		List<String> errors = e.getBindingResult().getFieldErrors().stream()
				.map(fieldError -> fieldError.getDefaultMessage())
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
		String message = errors.isEmpty() ? "Validation failed" : String.join(", ", errors);
		ErrorResponse response=new ErrorResponse(message,HttpStatus.BAD_REQUEST.value(),request.getRequestURI(),Instant.now());
		logger.warn("Invalid request body: {}", message);
		return ResponseEntity.badRequest().body(response);
	}
	
	@ExceptionHandler(DuplicateEntityException.class)
	public static ResponseEntity<ErrorResponse> handleDuplicateEntityException(DuplicateEntityException e,HttpServletRequest request){
	      ErrorResponse response=new ErrorResponse(e.getMessage(),HttpStatus.BAD_REQUEST.value(),request.getRequestURI(),Instant.now());
	      logger.warn("Invalid request body: {}", e.getMessage());
	      return ResponseEntity.badRequest().body(response);
	}
	
	@ExceptionHandler(OrgNotFoundException.class)
	public static  ResponseEntity<ErrorResponse> handleOrgNotFoundException(OrgNotFoundException e,HttpServletRequest request){
		ErrorResponse response=new ErrorResponse(e.getMessage(),HttpStatus.UNAUTHORIZED.value(),request.getRequestURI(),Instant.now());
	    logger.warn("Invalid request body: {}", e.getMessage());
	    return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(response);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public static ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
		ErrorResponse response = new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value(), request.getRequestURI(), Instant.now());
		logger.warn("Invalid argument: {}", e.getMessage());
		return ResponseEntity.badRequest().body(response);
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public static ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e, HttpServletRequest request) {
		ErrorResponse response = new ErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND.value(), request.getRequestURI(), Instant.now());
		logger.warn("Entity not found: {}", e.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(response);
	}

	@ExceptionHandler(InvalidRefreshTokenException.class)
	public static ResponseEntity<ErrorResponse> handleInvalidRefreshTokenException(InvalidRefreshTokenException e, HttpServletRequest request) {
		ErrorResponse response = new ErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED.value(), request.getRequestURI(), Instant.now());
		logger.warn("Invalid refresh token: {}", e.getMessage());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(response);
	}

	@ExceptionHandler(RefreshTokenReuseException.class)
	public static ResponseEntity<ErrorResponse> handleRefreshTokenReuseException(RefreshTokenReuseException e, HttpServletRequest request) {
		ErrorResponse response = new ErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED.value(), request.getRequestURI(), Instant.now());
		logger.warn("Refresh token reuse detected: {}", e.getMessage());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(response);
	}

	@ExceptionHandler(Exception.class)
	public static ResponseEntity<ErrorResponse> handleGenericException(Exception e, HttpServletRequest request) {
		ErrorResponse response = new ErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), request.getRequestURI(), Instant.now());
		logger.error("Unexpected error: {}", e.getMessage(), e);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(response);
	}
}
