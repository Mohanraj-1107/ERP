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
import com.erp.core.entity.sale.CreateSaleRequest;
import com.erp.core.entity.sale.SaleResponse;
import com.erp.core.entity.sale.UpdateSaleRequest;
import com.erp.core.exception.InvalidRefreshTokenException;
import com.erp.core.service.SaleService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/sales")
public class SaleController {

	private final SaleService saleService;

	public SaleController(SaleService saleService) {
		this.saleService = saleService;
	}

	private long currentOrgId() {
		Long orgId = OrgContext.getOrgId();
		if (orgId == null) {
			throw new InvalidRefreshTokenException("Access token is missing");
		}
		return orgId;
	}

	@PostMapping
	public ResponseEntity<SaleResponse> createSale(@Valid @RequestBody CreateSaleRequest request) {
		long orgId = currentOrgId();
		return ResponseEntity.ok(saleService.createSale(orgId, request));
	}

	@GetMapping("/{saleId}")
	public ResponseEntity<SaleResponse> getSale(@PathVariable Long saleId) {
		long orgId = currentOrgId();
		return ResponseEntity.ok(saleService.getSale(orgId, saleId));
	}

	@GetMapping
	public ResponseEntity<Page<SaleResponse>> listSales(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		long orgId = currentOrgId();
		Pageable pageable = PageRequest.of(page, size);
		return ResponseEntity.ok(saleService.listSales(orgId, pageable));
	}

	@PatchMapping("/{saleId}")
	public ResponseEntity<SaleResponse> updateSale(
			@PathVariable Long saleId,
			@Valid @RequestBody UpdateSaleRequest request) {
		long orgId = currentOrgId();
		return ResponseEntity.ok(saleService.updateSale(orgId, saleId, request));
	}

	@PostMapping("/{saleId}/confirm")
	public ResponseEntity<SaleResponse> confirmSale(@PathVariable Long saleId) {
		long orgId = currentOrgId();
		return ResponseEntity.ok(saleService.confirmSale(orgId, saleId));
	}

	@PostMapping("/{saleId}/cancel")
	public ResponseEntity<SaleResponse> cancelSale(@PathVariable Long saleId) {
		long orgId = currentOrgId();
		return ResponseEntity.ok(saleService.cancelSale(orgId, saleId));
	}

	@DeleteMapping("/{saleId}")
	public ResponseEntity<Void> deleteSale(@PathVariable Long saleId) {
		long orgId = currentOrgId();
		saleService.deleteSale(orgId, saleId);
		return ResponseEntity.noContent().build();
	}
}
