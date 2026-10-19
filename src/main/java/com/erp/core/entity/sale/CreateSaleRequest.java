package com.erp.core.entity.sale;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import java.util.List;

public class CreateSaleRequest {

	@NotNull(message = "Customer ID is required")
	private Long customerId;

	@NotEmpty(message = "At least one item is required")
	@Valid
	private List<SaleItemRequest> items;

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public List<SaleItemRequest> getItems() {
		return items;
	}

	public void setItems(List<SaleItemRequest> items) {
		this.items = items;
	}
}
