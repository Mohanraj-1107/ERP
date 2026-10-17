package com.erp.core.entity.customer;

import java.time.Instant;
import java.util.List;

public record CustomerResponse(
		long customerId,
		long orgId,
		String customerName,
		String customerEmail,
		String mobileNo,
		String gstNo,
		List<AddressResponse> address,
		Instant createdOn,
		Instant modifiedOn
) {

}
