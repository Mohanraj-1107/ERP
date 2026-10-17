package com.erp.core.entity.customer;

import java.util.List;

public class UpdateCustomerRequest {

	private String customerName;

	private String customerEmail;

	private String mobileNo;

	private String gstNo;

	private List<AddressRequest> address;

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getGstNo() {
		return gstNo;
	}

	public void setGstNo(String gstNo) {
		this.gstNo = gstNo;
	}

	public List<AddressRequest> getAddress() {
		return address;
	}

	public void setAddress(List<AddressRequest> address) {
		this.address = address;
	}
}
