package com.erp.core.entity.sale;

public record SaleItemResponse(
		long id,
		Long productId,
		String productName,
		double quantity,
		double unitPrice,
		double taxRate,
		double taxAmount,
		double totalAmount,
		double discountRate,
		double discountAmount
) {}
