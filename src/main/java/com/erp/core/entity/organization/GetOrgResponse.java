//$Id$
package com.erp.core.entity.organization;

import java.time.Instant;
import java.util.*;
import com.erp.core.entity.common.Address;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
@SuppressWarnings("unused")
public class GetOrgResponse {
    
	@JsonProperty("org_id")
	private long orgId;
	
	@JsonProperty("org_name")
	private String orgName;
	
	@JsonProperty("org_email")
	private String orgEmail;
	
	@JsonProperty("org_address")
	private List<Address> address;
	
	@JsonProperty("mobile_no")
	private String mobileNo;
	
	@JsonProperty("created_on")
	private Instant CreatedOn;
	
	public GetOrgResponse(long orgId, String orgName, String orgEmail, List<Address> address, String mobileNo, Instant createdOn) {
		super();
		this.orgId = orgId;
		this.orgName = orgName;
		this.orgEmail = orgEmail;
		this.address = address;
		this.mobileNo = mobileNo;
		CreatedOn = createdOn;
	}
	
	public  static class ResponseBuilder{
		
		private long orgId;
		private String orgName;
		private String orgEmail;
		private List<Address> address;
		private String mobileNo;
		private Instant CreatedOn;
		
		public ResponseBuilder setOrgId(long orgId) {
			this.orgId = orgId;
			return this;
		}
		public ResponseBuilder setOrgName(String orgName) {
			this.orgName = orgName;
			return this;
		}
		public ResponseBuilder setOrgEmail(String orgEmail) {
			this.orgEmail = orgEmail;
			return this;
		}
		public ResponseBuilder setAddress(List<Address> address) {
			this.address = address;
			return this;
		}
		public ResponseBuilder setMobileNo(String mobileNo) {
			this.mobileNo = mobileNo;
			return this;
		}
		public ResponseBuilder setCreatedOn(Instant createdOn) {
			this.CreatedOn = createdOn;
			return this;
		}
		public GetOrgResponse build() {
			return new GetOrgResponse(this.orgId,this.orgName,this.orgEmail,this.address,this.mobileNo,this.CreatedOn);
		}
	}
}
