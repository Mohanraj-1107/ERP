//$Id$
package com.erp.core.entity.organization;

import org.springframework.stereotype.Component;

@Component
public class LoginRequest {
	
	private String orgEmail;
	private String password;
	
	public String getOrgEmail() {
		return orgEmail;
	}
	public void setOrgEmail(String orgEmail) {
		this.orgEmail = orgEmail;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
