//$Id$
package com.erp.core.entity.organization;

import java.util.List;
import com.erp.core.entity.common.Address;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreateOrgRequest {
	
	@NotBlank(message="OrgName is required")
	@Size(min = 5, max =100, message = "orgname must be between 8 and 64 characters")
	private String orgName;
	
	@NotBlank(message="Email is required")
	@Email(message="Invalid Email")
	private String orgEmail;
	
	@NotBlank(message="Password is required")
	@Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
	private String orgPassword;
	
	@NotNull(message="Address is required")
	private List<Address> address;
	
	@NotBlank(message="Mobile No is required")
	@Pattern(
	        regexp = "^[6-9]\\d{9}$",
	        message = "Invalid mobile number"
	)
	private String mobileNo;
	
	public String getOrgName() { 
		return orgName;
	}
	public String getOrgEmail() {
		return orgEmail;
	}
	public String getOrgPassword() {
		return orgPassword;
	}
	public List<Address> getAddress() {
		return address;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public void setOrgEmail(String orgEmail) {
		this.orgEmail = orgEmail;
	}
	public void setOrgPassword(String orgPassword) {
		this.orgPassword = orgPassword;
	}
	public void setAddress(List<Address> address) {
		this.address = address;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
}
