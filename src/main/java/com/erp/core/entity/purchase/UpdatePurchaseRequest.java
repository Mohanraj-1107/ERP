package com.erp.core.entity.purchase;

import java.time.Instant;
import java.util.List;

import jakarta.validation.Valid;

public class UpdatePurchaseRequest {

	private Long supplierId;

	private Instant purchasedDate;

	@Valid
	private List<PurchaseItemRequest> items;

	public Long getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Long supplierId) {
		this.supplierId = supplierId;
	}

	public Instant getPurchasedDate() {
		return purchasedDate;
	}

	public void setPurchasedDate(Instant purchasedDate) {
		this.purchasedDate = purchasedDate;
	}

	public List<PurchaseItemRequest> getItems() {
		return items;
	}

	public void setItems(List<PurchaseItemRequest> items) {
		this.items = items;
	}
}
