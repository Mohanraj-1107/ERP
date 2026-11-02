package com.erp.core.entity.inventory;

import java.time.Instant;

public record InventoryResponse(
		long inventoryId,
		long productId,
		String prouctName,
		String unit,
		long orgId,
		double quantity,
		Instant createdOn,
		Instant modifiedOn
) {

}
