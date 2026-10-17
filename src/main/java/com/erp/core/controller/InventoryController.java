package com.erp.core.controller;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.erp.core.common.OrgContext;
import com.erp.core.entity.inventory.AdjustStockRequest;
import com.erp.core.entity.inventory.InventoryResponse;
import com.erp.core.exception.InvalidRefreshTokenException;
import com.erp.core.service.InventoryService;

@RestController
@RequestMapping("/v1/inventory")
public class InventoryController {

	private final InventoryService inventoryService;

	public InventoryController(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	private long currentOrgId() {
		Long orgId = OrgContext.getOrgId();
		if (orgId == null) {
			throw new InvalidRefreshTokenException("Access token is missing");
		}
		return orgId;
	}

	@GetMapping
	public ResponseEntity<Page<InventoryResponse>> listInventory(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		long orgId = currentOrgId();
		Pageable pageable = PageRequest.of(page, size);
		return ResponseEntity.ok(inventoryService.listInventory(orgId, pageable));
	}

	@GetMapping("/{productId}")
	public ResponseEntity<InventoryResponse> getInventory(@PathVariable Long productId) {
		long orgId = currentOrgId();
		return ResponseEntity.ok(inventoryService.getInventory(orgId, productId));
	}

	@GetMapping("/low-stock")
	public ResponseEntity<List<InventoryResponse>> listLowStock() {
		long orgId = currentOrgId();
		return ResponseEntity.ok(inventoryService.listLowStock(orgId));
	}

	@PostMapping("/{productId}/add")
	public ResponseEntity<InventoryResponse> addStock(
			@PathVariable Long productId,
			@Valid @RequestBody AdjustStockRequest request) {
		long orgId = currentOrgId();
		return ResponseEntity.ok(
				inventoryService.addStock(orgId, productId, request.getQuantity()));
	}

	@PostMapping("/{productId}/remove")
	public ResponseEntity<InventoryResponse> removeStock(
			@PathVariable Long productId,
			@Valid @RequestBody AdjustStockRequest request) {
		long orgId = currentOrgId();
		return ResponseEntity.ok(
				inventoryService.removeStock(orgId, productId, request.getQuantity()));
	}
}
