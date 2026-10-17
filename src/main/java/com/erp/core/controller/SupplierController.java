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
import com.erp.core.entity.supplier.CreateSupplierRequest;
import com.erp.core.entity.supplier.SupplierResponse;
import com.erp.core.entity.supplier.UpdateSupplierRequest;
import com.erp.core.exception.InvalidRefreshTokenException;
import com.erp.core.service.SupplierService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/suppliers")
public class SupplierController {

	private final SupplierService supplierService;

	public SupplierController(SupplierService supplierService) {
		this.supplierService = supplierService;
	}

	private long currentOrgId() {
		Long orgId = OrgContext.getOrgId();
		if (orgId == null) {
			throw new InvalidRefreshTokenException("Access token is missing");
		}
		return orgId;
	}

	@PostMapping
	public ResponseEntity<SupplierResponse> createSupplier(@Valid @RequestBody CreateSupplierRequest request) {
		long orgId = currentOrgId();
		return ResponseEntity.ok(supplierService.createSupplier(orgId, request));
	}

	@GetMapping("/{supplierId}")
	public ResponseEntity<SupplierResponse> getSupplier(@PathVariable Long supplierId) {
		long orgId = currentOrgId();
		return ResponseEntity.ok(supplierService.getSupplier(orgId, supplierId));
	}

	@GetMapping
	public ResponseEntity<Page<SupplierResponse>> listSuppliers(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		long orgId = currentOrgId();
		Pageable pageable = PageRequest.of(page, size);
		return ResponseEntity.ok(supplierService.listSuppliers(orgId, pageable));
	}

	@PatchMapping("/{supplierId}")
	public ResponseEntity<SupplierResponse> updateSupplier(
			@PathVariable Long supplierId,
			@Valid @RequestBody UpdateSupplierRequest request) {
		long orgId = currentOrgId();
		return ResponseEntity.ok(supplierService.updateSupplier(orgId, supplierId, request));
	}

	@DeleteMapping("/{supplierId}")
	public ResponseEntity<Void> deleteSupplier(@PathVariable Long supplierId) {
		long orgId = currentOrgId();
		supplierService.deleteSupplier(orgId, supplierId);
		return ResponseEntity.noContent().build();
	}
}
