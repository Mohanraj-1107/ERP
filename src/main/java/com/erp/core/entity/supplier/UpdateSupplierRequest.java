package com.erp.core.entity.supplier;

import java.util.List;

public class UpdateSupplierRequest {

	private String supplierName;

	private String supplierEmail;

	private String mobileNo;

	private String gstNo;

	private List<AddressRequest> address;

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getSupplierEmail() {
		return supplierEmail;
	}

	public void setSupplierEmail(String supplierEmail) {
		this.supplierEmail = supplierEmail;
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
