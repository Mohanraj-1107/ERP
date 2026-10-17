package com.erp.core.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.erp.core.entity.common.Address;
import com.erp.core.entity.customer.AddressRequest;
import com.erp.core.entity.customer.AddressResponse;
import com.erp.core.entity.customer.CreateCustomerRequest;
import com.erp.core.entity.customer.Customer;
import com.erp.core.entity.customer.CustomerResponse;
import com.erp.core.entity.customer.UpdateCustomerRequest;
import com.erp.core.exception.DuplicateEntityException;
import com.erp.core.exception.EntityNotFoundException;
import com.erp.core.repository.CustomerRepo;
import com.erp.core.repository.OrganizationRepo;

@Service
public class CustomerService {

	private final CustomerRepo customerRepo;
	private final OrganizationRepo organizationRepo;
	
	public CustomerService(CustomerRepo customerRepo, OrganizationRepo organizationRepo) {
		this.customerRepo = customerRepo;
		this.organizationRepo = organizationRepo;
	}

	public CustomerResponse createCustomer(Long orgId, CreateCustomerRequest request) {
		organizationRepo.findById(orgId)
				.orElseThrow(() -> new EntityNotFoundException("Organization not found"));

		if (customerRepo.existsByOrg_OrgIdAndCustomerEmail(orgId, request.getCustomerEmail())) {
			throw new DuplicateEntityException("Customer email already exists for this organization");
		}

		Customer customer = new Customer();
		customer.setOrg(organizationRepo.findById(orgId)
				.orElseThrow(() -> new EntityNotFoundException("Organization not found")));
		customer.setCustomerName(request.getCustomerName());
		customer.setCustomerEmail(request.getCustomerEmail());
		customer.setMobileNo(request.getMobileNo());
		customer.setGstNo(request.getGstNo());
		customer.setAddress(request.getAddress());

		Customer saved = customerRepo.save(customer);
		return toResponse(saved);
	}

	public CustomerResponse getCustomer(Long orgId, Long customerId) {
		Customer customer = customerRepo.findByOrg_OrgIdAndCustomerId(orgId, customerId)
				.orElseThrow(() -> new EntityNotFoundException("Customer not found"));
		return toResponse(customer);
	}

	public Page<CustomerResponse> listCustomers(Long orgId, Pageable pageable) {
		return customerRepo.findAllByOrg_OrgId(orgId, pageable).map(this::toResponse);
	}

	public CustomerResponse updateCustomer(Long orgId, Long customerId, UpdateCustomerRequest request) {
		Customer customer = customerRepo.findByOrg_OrgIdAndCustomerId(orgId, customerId)
				.orElseThrow(() -> new EntityNotFoundException("Customer not found"));

		if (request.getCustomerName() != null) {
			customer.setCustomerName(request.getCustomerName());
		}
		if (request.getCustomerEmail() != null) {
			if (customerRepo.existsByOrg_OrgIdAndCustomerEmail(orgId, request.getCustomerEmail())
					&& !request.getCustomerEmail().equals(customer.getCustomerEmail())) {
				throw new DuplicateEntityException("Customer email already exists for this organization");
			}
			customer.setCustomerEmail(request.getCustomerEmail());
		}
		if (request.getMobileNo() != null) {
			customer.setMobileNo(request.getMobileNo());
		}
		if (request.getGstNo() != null) {
			customer.setGstNo(request.getGstNo());
		}
		if (request.getAddress() != null) {
			customer.setAddress(toAddressEntities(request.getAddress()));
		}

		Customer saved = customerRepo.save(customer);
		return toResponse(saved);
	}

	public void deleteCustomer(Long orgId, Long customerId) {
		Customer customer = customerRepo.findByOrg_OrgIdAndCustomerId(orgId, customerId)
				.orElseThrow(() -> new EntityNotFoundException("Customer not found"));
		customerRepo.delete(customer);
	}

	private List<Address> toAddressEntities(List<AddressRequest> requests) {
		if (requests == null) {
			return null;
		}
		return requests.stream().map(this::toAddressEntity).collect(Collectors.toList());
	}

	private Address toAddressEntity(AddressRequest request) {
		Address address = new Address();
		address.setStreet(request.getStreet());
		address.setCity(request.getCity());
		address.setDistrict(request.getDistrict());
		address.setPincode(request.getPincode());
		address.setState(request.getState());
		address.setCountry(request.getCountry());
		return address;
	}

	private CustomerResponse toResponse(Customer customer) {
		return new CustomerResponse(
				customer.getCustomerId(),
				customer.getOrg().getOrgId(),
				customer.getCustomerName(),
				customer.getCustomerEmail(),
				customer.getMobileNo(),
				customer.getGstNo(),
				customer.getAddress() == null ? null
						: customer.getAddress().stream().map(this::toAddressResponse).toList(),
				customer.getCreatedOn(),
				customer.getModifiedOn()
		);
	}

	private AddressResponse toAddressResponse(Address address) {
		return new AddressResponse(
				address.getStreet(),
				address.getCity(),
				address.getDistrict(),
				address.getPincode(),
				address.getState(),
				address.getCountry()
		);
	}
}
