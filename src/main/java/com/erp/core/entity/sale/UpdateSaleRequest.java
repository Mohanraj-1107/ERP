package com.erp.core.entity.sale;

import java.time.Instant;
import java.util.List;

import jakarta.validation.Valid;

public class UpdateSaleRequest {

	private Long customerId;

	private Instant purchasedDate;

	@Valid
	private List<SaleItemRequest> items;

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public Instant getPurchasedDate() {
		return purchasedDate;
	}

	public void setPurchasedDate(Instant purchasedDate) {
		this.purchasedDate = purchasedDate;
	}

	public List<SaleItemRequest> getItems() {
		return items;
	}

	public void setItems(List<SaleItemRequest> items) {
		this.items = items;
	}
}
