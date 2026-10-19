package com.erp.core.entity.sale;

import java.time.Instant;
import java.util.List;

public record SaleResponse(
		long saleId,
		long orgId,
		String salesNumber,
		Long customerId,
		String customerName,
		double subtotalAmount,
		double totalAmount,
		double totalTaxAmount,
		Instant purchasedDate,
		SalesStatus status,
		List<SaleItemResponse> items,
		Instant createdOn,
		Instant modifiedOn
) {}
