package com.erp.core.entity.customer;

public record AddressResponse(
		String street,
		String city,
		String district,
		String pincode,
		String state,
		String country
) {

}
