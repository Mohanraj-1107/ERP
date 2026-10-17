package com.erp.core.entity.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import com.erp.core.entity.common.Address;

public class CreateCustomerRequest {

	@NotBlank(message = "Customer name is required")
	private String customerName;

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email")
	private String customerEmail;

	@NotBlank(message = "Mobile number is required")
	@Size(min = 10, max = 15, message = "Mobile number must be between 10 and 15 digits")
	private String mobileNo;

	@NotBlank(message = "GST number is required")
	private String gstNo;

	@NotNull(message = "Address is required")
	private List<Address> address = new java.util.ArrayList<>();

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

	public List<Address> getAddress() {
		return address;
	}

	public void setAddress(List<Address> address) {
		this.address = address;
	}
}
