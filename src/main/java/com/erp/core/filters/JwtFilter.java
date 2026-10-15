//$Id$
package com.erp.core.filters;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import com.erp.core.entity.common.ErrorResponse;
import com.erp.core.common.JwtUtils;
import com.erp.core.common.OrgContext;
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
import jakarta.servlet.http.HttpServletResponse;

import java.util.Set;

@Component
@WebFilter("/api/v1")
@Order(1)
public class JwtFilter implements Filter{
	
	private static final String ACCESS_TOKEN="access_token";
	private static final String REFRESH_TOKEN="refresh_token";
	
	private final JwtUtils jwtUtil;

	private static final Set<String> publicUrls=Set.of(
			// Public API's (full request URI, lowercased method)
			"/erp/api/v1/login:post",
			"/erp/api/v1/org:post",
			"/erp/api/v1/refresh:post",
			"/erp/api/v1/logout:post"
	);
	
	public JwtFilter(JwtUtils jwtUtil) {
		this.jwtUtil=jwtUtil;
	}

	private String toJson(ErrorResponse errorResponse) {
		return "{\"errorMessage\":\"" + escapeJson(errorResponse.errorMessage()) +
				"\",\"errorCode\":" + errorResponse.errorCode() +
				",\"path\":\"" + escapeJson(errorResponse.path()) +
				"\",\"timestamp\":\"" + errorResponse.timestamp() + "\"}";
	}

	private String escapeJson(String value) {
		if (value == null) return "";
		return value.replace("\\", "\\\\").replace("\"", "\\\"")
				.replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		try {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			String requestUri = httpRequest.getRequestURI();
			String method = httpRequest.getMethod();

			if (isPublicRequest(requestUri, method)) {
				chain.doFilter(request, response);
				return;
			}
			String accessToken = null;
			String refreshToken = null;
			ErrorResponse errorResponse = null;
			Cookie cookies[] = httpRequest.getCookies();
			if (cookies == null || cookies.length == 0) {
				PrintWriter out = setJsonResponseType(response);
				errorResponse = new ErrorResponse("Unauthorized access - Invalid access token",
						HttpStatus.UNAUTHORIZED.value(), requestUri, Instant.now()
				);
				out.print(toJson(errorResponse));
				return;
			}
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(ACCESS_TOKEN)) {
					accessToken = cookie.getValue();
				}
				if (cookie.getName().equals(REFRESH_TOKEN)) {
					refreshToken = cookie.getValue();
				}
				if (accessToken != null && refreshToken != null) {
					break;
				}
			}
			if (accessToken == null) {
				PrintWriter out = setJsonResponseType(response);
				errorResponse = new ErrorResponse("Unauthorized access - Access token is missing in request",
						HttpStatus.UNAUTHORIZED.value(), requestUri, Instant.now()
				);
				out.print(toJson(errorResponse));
				return;
			}
			try {
				jwtUtil.validateJwtToken(accessToken);
				OrgContext.setOrgId(Long.parseLong(jwtUtil.getOrgNameFromToken(accessToken)));
			} catch (SecurityException e) {
				PrintWriter out = setJsonResponseType(response);
				errorResponse = new ErrorResponse("Unauthorized access - Invalid access token " + e.getMessage(),
						HttpStatus.UNAUTHORIZED.value(), requestUri, Instant.now()
				);
				out.print(toJson(errorResponse));
				return;
			} catch (MalformedJwtException e) {
				PrintWriter out = setJsonResponseType(response);
				errorResponse = new ErrorResponse("Unauthorized access - MalformedJwtException access token " + e.getMessage(),
						HttpStatus.UNAUTHORIZED.value(), requestUri, Instant.now()
				);
				out.print(toJson(errorResponse));
				return;
			} catch (ExpiredJwtException e) {
				PrintWriter out = setJsonResponseType(response);
				errorResponse = new ErrorResponse("Unauthorized access - access token expired " + e.getMessage(),
						HttpStatus.UNAUTHORIZED.value(), requestUri, Instant.now()
				);
				out.print(toJson(errorResponse));
				return;
			} catch (UnsupportedJwtException e) {
				PrintWriter out = setJsonResponseType(response);
				errorResponse = new ErrorResponse("Unauthorized access - token format is unsupported " + e.getMessage(),
						HttpStatus.UNAUTHORIZED.value(), requestUri, Instant.now()
				);
				out.print(toJson(errorResponse));
				return;
			} catch (IllegalArgumentException e) {
				PrintWriter out = setJsonResponseType(response);
				errorResponse = new ErrorResponse("Unauthorized access - illegal argument exception " + e.getMessage(),
						HttpStatus.UNAUTHORIZED.value(), requestUri, Instant.now()
				);
				out.print(toJson(errorResponse));
				return;
			} catch (Exception e) {
				PrintWriter out = setJsonResponseType(response);
				errorResponse = new ErrorResponse("Unexpected error occurred while validating the access token " + e.getMessage(),
						HttpStatus.UNAUTHORIZED.value(), requestUri, Instant.now()
				);
				out.print(toJson(errorResponse));
				return;
			}
			chain.doFilter(httpRequest, response);
		} finally {
			OrgContext.clear();
		}
	}
	
	public boolean isPublicRequest(String requestUri,String method) {
	    String key=requestUri+":"+method.toLowerCase();
	    if(publicUrls.contains(key)) {
	    	 return true;
	    }
	    return false;
	}
	
	public PrintWriter setJsonResponseType(ServletResponse response) throws IOException, ServletException {
		if (response instanceof HttpServletResponse httpServletResponse) {
			httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
		}
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		return response.getWriter();
	}
}
