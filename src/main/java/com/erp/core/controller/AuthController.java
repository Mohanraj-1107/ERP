//$Id$
package com.erp.core.controller;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erp.core.entity.common.AuthResult;
import com.erp.core.common.JwtUtils;
import com.erp.core.entity.organization.LoginRequest;
import com.erp.core.entity.organization.LoginResponse;
import com.erp.core.service.OrgService;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;

@RestController
@RequestMapping("/v1")
public class AuthController {

	private final OrgService orgService;
	private final JwtUtils jwtUtils;

	@Value("${app.jwt.expiration:900000}")
	private int accessTokenExpiryMs;

	@Value("${app.refresh-token.expiry:604800000}")
	private long refreshTokenExpiryMs;

	@Value("${cookie.secure:false}")
	private boolean cookieSecure;

	public AuthController(OrgService orgService, JwtUtils jwtUtils) {
		this.orgService = orgService;
		this.jwtUtils = jwtUtils;
	}

	@PostMapping("/login")
	public ResponseEntity<Object> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) throws Exception {
		AuthResult result = orgService.login(loginRequest);
		if (result.accessToken() != null) {
			response.addHeader("Set-Cookie", cookie("access_token", result.accessToken(), accessTokenExpiryMs, "/erp/api").toString());
		}
		if (result.refreshToken() != null) {
			response.addHeader("Set-Cookie", cookie("refresh_token", result.refreshToken(), refreshTokenExpiryMs, "/erp/api/v1").toString());
		}
		return ResponseEntity.ok(result.response());
	}

	@PostMapping("/refresh")
	public ResponseEntity<Object> refresh(@CookieValue(value = "refresh_token", required = false) String refreshToken,
			HttpServletResponse response) throws Exception {
		if (refreshToken == null || refreshToken.isBlank()) {
			throw new com.erp.core.exception.InvalidRefreshTokenException("Refresh token is missing");
		}
		AuthResult result = orgService.refresh(refreshToken);
		response.addHeader("Set-Cookie", cookie("access_token", result.accessToken(), accessTokenExpiryMs, "/erp/api").toString());
		response.addHeader("Set-Cookie", cookie("refresh_token", result.refreshToken(), refreshTokenExpiryMs, "/erp/api/v1").toString());
		return ResponseEntity.ok(result.response());
	}

	@PostMapping("/logout")
	public ResponseEntity<Object> logout(@CookieValue(value = "refresh_token", required = false) String refreshToken,
			HttpServletResponse response) {
		orgService.logout(refreshToken);
		response.addHeader("Set-Cookie", clearCookie("access_token", "/erp/api").toString());
		response.addHeader("Set-Cookie", clearCookie("refresh_token", "/erp/api/v1").toString());
		return ResponseEntity.ok(new LogoutResponse("success", "Logged out successfully"));
	}

	@PostMapping("/logout-all")
	public ResponseEntity<Object> logoutAll(@CookieValue(value = "access_token", required = false) String accessToken,
			HttpServletResponse response) throws Exception {
		if (accessToken == null || accessToken.isBlank()) {
			throw new com.erp.core.exception.InvalidRefreshTokenException("Access token is missing");
		}
		String subject = jwtUtils.getOrgNameFromToken(accessToken);
		orgService.logoutAll(Long.parseLong(subject));
		response.addHeader("Set-Cookie", clearCookie("access_token", "/erp/api").toString());
		response.addHeader("Set-Cookie", clearCookie("refresh_token", "/erp/api/v1").toString());
		return ResponseEntity.ok(new LogoutResponse("success", "All sessions revoked"));
	}

	private ResponseCookie cookie(String name, String value, long maxAgeMs, String path) {
		return ResponseCookie.from(name, value).httpOnly(true).secure(cookieSecure).sameSite("Lax").path(path)
				.maxAge(Duration.ofMillis(maxAgeMs)).build();
	}

	private ResponseCookie clearCookie(String name, String path) {
		return ResponseCookie.from(name, "").httpOnly(true).secure(cookieSecure).sameSite("Lax").path(path)
				.maxAge(Duration.ZERO).build();
	}

	public record LogoutResponse(String status, String message) {
	}
}
