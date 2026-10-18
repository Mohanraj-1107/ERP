package com.erp.core.entity.purchase;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import java.util.List;

public class CreatePurchaseRequest {

	@NotNull(message = "Supplier ID is required")
	private Long supplierId;

	@NotEmpty(message = "At least one item is required")
	@Valid
	private List<PurchaseItemRequest> items;

	public Long getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Long supplierId) {
		this.supplierId = supplierId;
	}

	public List<PurchaseItemRequest> getItems() {
		return items;
	}

	public void setItems(List<PurchaseItemRequest> items) {
		this.items = items;
	}
}
