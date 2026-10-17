package com.erp.core.entity.inventory;

import jakarta.validation.constraints.Positive;

public class AdjustStockRequest {

	@Positive(message = "Quantity must be greater than zero")
	private double quantity;

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
}
