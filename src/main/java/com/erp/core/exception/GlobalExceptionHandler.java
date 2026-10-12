//$Id$
package com.erp.core.exception;

import java.time.Instant;
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
	      ErrorResponse response=new ErrorResponse(e.getMessage(),HttpStatus.BAD_REQUEST.value(),request.getRequestURI(),Instant.now());
	      logger.warn("Invalid request body: {}", e.getMessage());
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
		ErrorResponse response=new ErrorResponse(e.getMessage(),HttpStatus.BAD_REQUEST.value(),request.getRequestURI(),Instant.now());
	    logger.warn("Invalid request body: {}", e.getMessage());
	    return ResponseEntity.badRequest().body(response);
	}
}
