package com.erp.core.entity.purchase;

import java.time.Instant;
import java.util.List;

public record PurchaseResponse(
		long purchaseId,
		long orgId,
		String purchaseNumber,
		Long supplierId,
		String supplierName,
		double subtotalAmount,
		double totalAmount,
		double totalTaxAmount,
		Instant purchasedDate,
		PurchaseStatus status,
		List<PurchaseItemResponse> items,
		Instant createdOn,
		Instant modifiedOn
) {}
