//$Id$
package com.erp.core.filters;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import com.erp.core.entity.common.ErrorResponse;
import com.erp.core.entity.common.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Set;

@Component
@WebFilter("/api/v2")
@Order(1)
public class JwtFilter implements Filter{
	
	private static final String ACCESS_TOKEN="access_token";
	private static final String REFRESH_TOKEN="refresh_token";
	private final JwtUtils jwtUtil;
	
	private static final Set<String> publicUrls=Set.of(
			//TODO Need to add public API's - Format : api:method ,ex - v1/login:post
	);
	
	public JwtFilter(JwtUtils jwtUtil) {
		this.jwtUtil=jwtUtil;
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest=(HttpServletRequest)request;
		String requestUri=httpRequest.getRequestURI();
		String method=httpRequest.getMethod();
		
		if(isPublicRequest(requestUri,method)) {
			chain.doFilter(request, response);
			return;
		}
		String accessToken=null;
		String refreshToken=null;
		Cookie cookies[]=httpRequest.getCookies();
		for(Cookie cookie:cookies) {
			if(cookie.getName().equals(ACCESS_TOKEN)) {
				accessToken=cookie.getValue();
			}
			if(cookie.getName().equals(REFRESH_TOKEN)) {
				refreshToken=cookie.getValue();
			}
			if(accessToken!=null && refreshToken!=null) {
				break;
			}
		}
		PrintWriter out=response.getWriter();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		ErrorResponse errorResponse=null;
		if(accessToken==null) {
			errorResponse=new ErrorResponse("Unauthorized access , Access token is missing in request",
					HttpStatus.UNAUTHORIZED.value(),requestUri,Instant.now()
			);
			out.print(errorResponse.toString());
			return;
		}
		try {
			jwtUtil.validateJwtToken(accessToken);
		}catch (SecurityException e) {
			errorResponse=new ErrorResponse("Unauthorized access ,Invalid access token "+e.getMessage(),
					HttpStatus.UNAUTHORIZED.value(),requestUri,Instant.now()
			);
			out.print(errorResponse);
			return;
	    }catch (MalformedJwtException e) {
	    	errorResponse=new ErrorResponse("Unauthorized access ,MalformedJwtException access token "+e.getMessage(),
					HttpStatus.UNAUTHORIZED.value(),requestUri,Instant.now()
			); 
			out.print(errorResponse);
			return;
	    }catch(ExpiredJwtException e) {
	    	errorResponse=new ErrorResponse("Unauthorized access ,access token expired "+e.getMessage(),
					HttpStatus.UNAUTHORIZED.value(),requestUri,Instant.now()
			);
			out.print(errorResponse);
			return;
	    }catch(UnsupportedJwtException e) {
	    	errorResponse=new ErrorResponse("Unauthorized access ,token format is unsupported "+e.getMessage(),
					HttpStatus.UNAUTHORIZED.value(),requestUri,Instant.now()
			);
			out.print(errorResponse);
			return;
	    }catch (IllegalArgumentException e) {
	    	errorResponse=new ErrorResponse("Unauthorized access ,illegal argument exception "+e.getMessage(),
					HttpStatus.UNAUTHORIZED.value(),requestUri,Instant.now()
			);
			out.print(errorResponse);
			return;
	    }catch(Exception e) {
	    	errorResponse=new ErrorResponse("Unexpected error occurred while validating the access token "+e.getMessage(),
					HttpStatus.UNAUTHORIZED.value(),requestUri,Instant.now()
			);
			out.print(errorResponse);
			return;
	    }
		chain.doFilter(httpRequest, response);
	}
	
	public boolean isPublicRequest(String requestUri,String method) {
	    String key=requestUri+":"+method;
	    if(publicUrls.contains(key)) {
	    	 return true;
	    }
	    return false;
	}
}
