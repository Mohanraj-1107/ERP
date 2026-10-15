package com.erp.core.entity.product;

import java.time.Instant;

public record ProductResponse(
		long productId,
		long orgId,
		String productCode,
		String productName,
		double purchasePrice,
		double sellingPrice,
		double taxRate,
		double minimumStock,
		Unit unit,
		Instant createdOn,
		Instant modifiedOn
) {
}
