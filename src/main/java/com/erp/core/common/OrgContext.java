package com.erp.core.common;

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
}
