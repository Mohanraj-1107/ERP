//$Id$
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
import com.erp.core.entity.product.CreateProductRequest;
import com.erp.core.entity.product.ProductResponse;
import com.erp.core.entity.product.UpdateProductRequest;
import com.erp.core.service.ProductService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/products")
public class ProductController {

	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	private long currentOrgId() {
		Long orgId = OrgContext.getOrgId();
		if (orgId == null) {
			throw new com.erp.core.exception.InvalidRefreshTokenException("Access token is missing");
		}
		return orgId;
	}

	@PostMapping
	public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
		long orgId = currentOrgId();
		return ResponseEntity.ok(productService.createProduct(orgId, request));
	}

	@GetMapping("/{productId}")
	public ResponseEntity<ProductResponse> getProduct(@PathVariable Long productId) {
		long orgId = currentOrgId();
		return ResponseEntity.ok(productService.getProduct(orgId, productId));
	}

	@GetMapping
	public ResponseEntity<Page<ProductResponse>> listProducts(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		long orgId = currentOrgId();
		Pageable pageable = PageRequest.of(page, size);
		return ResponseEntity.ok(productService.listProducts(orgId, pageable));
	}

	@PatchMapping("/{productId}")
	public ResponseEntity<ProductResponse> updateProduct(
			@PathVariable Long productId,
			@Valid @RequestBody UpdateProductRequest request) {
		long orgId = currentOrgId();
		return ResponseEntity.ok(productService.updateProduct(orgId, productId, request));
	}

	@DeleteMapping("/{productId}")
	public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
		long orgId = currentOrgId();
		productService.deleteProduct(orgId, productId);
		return ResponseEntity.noContent().build();
	}
}
