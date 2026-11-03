package com.erp.core.common;

import com.erp.core.exception.InvalidRefreshTokenException;

public final class OrgContext {

	private static final ThreadLocal<Long> ORG_ID = new ThreadLocal<>();

	private OrgContext() {
	}

	public static void setOrgId(Long orgId) {
		ORG_ID.set(orgId);
	}

	public static Long getOrgId() {
		return ORG_ID.get();
	}

	public static void clear() {
		ORG_ID.remove();
	}
	
	public static long currentOrgId() {
		Long orgId = OrgContext.getOrgId();
		if (orgId == null) {
			throw new InvalidRefreshTokenException("Access token is missing");
		}
		return orgId;
	}
}
