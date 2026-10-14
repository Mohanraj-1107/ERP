package com.erp.core.entity.common;

import com.erp.core.entity.organization.LoginResponse;

public record AuthResult(
		LoginResponse response,
		String accessToken,
		String refreshToken
) {}
