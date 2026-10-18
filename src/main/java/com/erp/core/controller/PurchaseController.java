package com.erp.core.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.erp.core.common.OrgContext;
import com.erp.core.entity.purchase.CreatePurchaseRequest;
import com.erp.core.entity.purchase.PurchaseResponse;
import com.erp.core.entity.purchase.UpdatePurchaseRequest;
import com.erp.core.exception.InvalidRefreshTokenException;
import com.erp.core.service.PurchaseService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/purchases")
public class PurchaseController {

	private final PurchaseService purchaseService;

	public PurchaseController(PurchaseService purchaseService) {
		this.purchaseService = purchaseService;
	}

	private long currentOrgId() {
		Long orgId = OrgContext.getOrgId();
		if (orgId == null) {
			throw new InvalidRefreshTokenException("Access token is missing");
		}
		return orgId;
	}

	@PostMapping
	public ResponseEntity<PurchaseResponse> createPurchase(@Valid @RequestBody CreatePurchaseRequest request) {
		long orgId = currentOrgId();
		return ResponseEntity.ok(purchaseService.createPurchase(orgId, request));
	}

	@GetMapping("/{purchaseId}")
	public ResponseEntity<PurchaseResponse> getPurchase(@PathVariable Long purchaseId) {
		long orgId = currentOrgId();
		return ResponseEntity.ok(purchaseService.getPurchase(orgId, purchaseId));
	}

	@GetMapping
	public ResponseEntity<Page<PurchaseResponse>> listPurchases(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		long orgId = currentOrgId();
		Pageable pageable = PageRequest.of(page, size);
		return ResponseEntity.ok(purchaseService.listPurchases(orgId, pageable));
	}

	@PatchMapping("/{purchaseId}")
	public ResponseEntity<PurchaseResponse> updatePurchase(
			@PathVariable Long purchaseId,
			@Valid @RequestBody UpdatePurchaseRequest request) {
		long orgId = currentOrgId();
		return ResponseEntity.ok(purchaseService.updatePurchase(orgId, purchaseId, request));
	}

	@PostMapping("/{purchaseId}/complete")
	public ResponseEntity<PurchaseResponse> completePurchase(@PathVariable Long purchaseId) {
		long orgId = currentOrgId();
		return ResponseEntity.ok(purchaseService.completePurchase(orgId, purchaseId));
	}

	@PostMapping("/{purchaseId}/cancel")
	public ResponseEntity<PurchaseResponse> cancelPurchase(@PathVariable Long purchaseId) {
		long orgId = currentOrgId();
		return ResponseEntity.ok(purchaseService.cancelPurchase(orgId, purchaseId));
	}

	@DeleteMapping("/{purchaseId}")
	public ResponseEntity<Void> deletePurchase(@PathVariable Long purchaseId) {
		long orgId = currentOrgId();
		purchaseService.deletePurchase(orgId, purchaseId);
		return ResponseEntity.noContent().build();
	}
}
