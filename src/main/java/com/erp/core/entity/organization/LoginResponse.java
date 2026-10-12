//$Id$
package com.erp.core.entity.organization;

import org.springframework.stereotype.Component;

@Component
public class LoginResponse {
	
	private Long orgId;
	private String message;
	private String status;

	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
