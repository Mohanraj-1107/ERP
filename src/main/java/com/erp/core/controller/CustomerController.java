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
import com.erp.core.entity.customer.CreateCustomerRequest;
import com.erp.core.entity.customer.CustomerResponse;
import com.erp.core.entity.customer.UpdateCustomerRequest;
import com.erp.core.exception.InvalidRefreshTokenException;
import com.erp.core.service.CustomerService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/customers")
public class CustomerController {

	private final CustomerService customerService;

	public CustomerController(CustomerService customerService) {
		this.customerService = customerService;
	}

	private long currentOrgId() {
		Long orgId = OrgContext.getOrgId();
		if (orgId == null) {
			throw new InvalidRefreshTokenException("Access token is missing");
		}
		return orgId;
	}

	@PostMapping
	public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
		long orgId = currentOrgId();
		return ResponseEntity.ok(customerService.createCustomer(orgId, request));
	}

	@GetMapping("/{customerId}")
	public ResponseEntity<CustomerResponse> getCustomer(@PathVariable Long customerId) {
		long orgId = currentOrgId();
		return ResponseEntity.ok(customerService.getCustomer(orgId, customerId));
	}

	@GetMapping
	public ResponseEntity<Page<CustomerResponse>> listCustomers(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		long orgId = currentOrgId();
		Pageable pageable = PageRequest.of(page, size);
		return ResponseEntity.ok(customerService.listCustomers(orgId, pageable));
	}

	@PatchMapping("/{customerId}")
	public ResponseEntity<CustomerResponse> updateCustomer(
			@PathVariable Long customerId,
			@Valid @RequestBody UpdateCustomerRequest request) {
		long orgId = currentOrgId();
		return ResponseEntity.ok(customerService.updateCustomer(orgId, customerId, request));
	}

	@DeleteMapping("/{customerId}")
	public ResponseEntity<Void> deleteCustomer(@PathVariable Long customerId) {
		long orgId = currentOrgId();
		customerService.deleteCustomer(orgId, customerId);
		return ResponseEntity.noContent().build();
	}
}
