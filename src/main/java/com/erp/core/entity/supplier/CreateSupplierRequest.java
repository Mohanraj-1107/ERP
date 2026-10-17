package com.erp.core.entity.supplier;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public class CreateSupplierRequest {

	@NotBlank(message = "Supplier name is required")
	private String supplierName;

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email")
	private String supplierEmail;

	@NotBlank(message = "Mobile number is required")
	@Size(min = 10, max = 15, message = "Mobile number must be between 10 and 15 digits")
	private String mobileNo;

	@NotBlank(message = "GST number is required")
	private String gstNo;

	@NotNull(message = "Address is required")
	private List<AddressRequest> address = new java.util.ArrayList<>();

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
