package com.erp.core.entity.supplier;

import java.time.Instant;
import java.util.List;

public record SupplierResponse(
		long supplierId,
		long orgId,
		String supplierName,
		String supplierEmail,
		String mobileNo,
		String gstNo,
		List<AddressResponse> address,
		Instant createdOn,
		Instant modifiedOn
) {

}
