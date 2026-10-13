//$Id$
package com.erp.core.entity.organization;

import org.springframework.stereotype.Component;
import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class LoginRequest {
	
	@JsonProperty("org_email")
	private String orgEmail;
	
	@JsonProperty("password")
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
