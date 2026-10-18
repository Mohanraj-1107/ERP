package com.erp.core.entity.purchase;

public record PurchaseItemResponse(
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
