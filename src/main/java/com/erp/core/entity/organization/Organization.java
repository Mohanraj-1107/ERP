//$Id$
package com.erp.core.entity.organization;

import com.erp.core.entity.common.Address;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import java.time.Instant;
import java.util.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
public class Organization {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long orgId;
	
	@Column(nullable = false)
	private String orgName;
	
	@Column(nullable = false)
	private String orgEmail;
	
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String orgPassword; 
	
	@ElementCollection
	@CollectionTable(name = "organization_address", joinColumns = @JoinColumn(name = "org_id"))
	private List<Address> address = new ArrayList<>();
	
	@Column(nullable = false)
	private String mobileNo;
	
	@CreationTimestamp
	@Column(updatable=false)
	private Instant createdOn;
	
	public long getOrgId() {
		return orgId;
	}

	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getOrgEmail() {
		return orgEmail;
	}

	public void setOrgEmail(String orgEmail) {
		this.orgEmail = orgEmail;
	}

	public String getOrgPassword() {
		return orgPassword;
	}

	public void setOrgPassword(String orgPassword) {
		this.orgPassword = orgPassword;
	}

	public List<Address> getAddress() {
		return address;
	}

	public void setAddress(List<Address> address) {
		this.address = address;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public Instant getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Instant createdOn) {
		this.createdOn = createdOn;
	}
}