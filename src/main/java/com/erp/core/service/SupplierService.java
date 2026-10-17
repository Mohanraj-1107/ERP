package com.erp.core.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.erp.core.entity.common.Address;
import com.erp.core.entity.supplier.AddressRequest;
import com.erp.core.entity.supplier.AddressResponse;
import com.erp.core.entity.supplier.CreateSupplierRequest;
import com.erp.core.entity.supplier.Supplier;
import com.erp.core.entity.supplier.SupplierResponse;
import com.erp.core.entity.supplier.UpdateSupplierRequest;
import com.erp.core.exception.DuplicateEntityException;
import com.erp.core.exception.EntityNotFoundException;
import com.erp.core.repository.OrganizationRepo;
import com.erp.core.repository.SupplierRepo;

@Service
public class SupplierService {

	private final SupplierRepo supplierRepo;
	private final OrganizationRepo organizationRepo;

	public SupplierService(SupplierRepo supplierRepo, OrganizationRepo organizationRepo) {
		this.supplierRepo = supplierRepo;
		this.organizationRepo = organizationRepo;
	}

	public SupplierResponse createSupplier(Long orgId, CreateSupplierRequest request) {
		organizationRepo.findById(orgId)
				.orElseThrow(() -> new EntityNotFoundException("Organization not found"));

		if (supplierRepo.existsByOrg_OrgIdAndSupplierEmail(orgId, request.getSupplierEmail())) {
			throw new DuplicateEntityException("Supplier email already exists for this organization");
		}

		Supplier supplier = new Supplier();
		supplier.setOrg(organizationRepo.findById(orgId)
				.orElseThrow(() -> new EntityNotFoundException("Organization not found")));
		supplier.setSupplierName(request.getSupplierName());
		supplier.setSupplierEmail(request.getSupplierEmail());
		supplier.setMobileNo(request.getMobileNo());
		supplier.setGstNo(request.getGstNo());
		supplier.setAddress(toAddressEntities(request.getAddress()));

		Supplier saved = supplierRepo.save(supplier);
		return toResponse(saved);
	}

	public SupplierResponse getSupplier(Long orgId, Long supplierId) {
		Supplier supplier = supplierRepo.findByOrg_OrgIdAndSupplierId(orgId, supplierId)
				.orElseThrow(() -> new EntityNotFoundException("Supplier not found"));
		return toResponse(supplier);
	}

	public Page<SupplierResponse> listSuppliers(Long orgId, Pageable pageable) {
		return supplierRepo.findAllByOrg_OrgId(orgId, pageable).map(this::toResponse);
	}

	public SupplierResponse updateSupplier(Long orgId, Long supplierId, UpdateSupplierRequest request) {
		Supplier supplier = supplierRepo.findByOrg_OrgIdAndSupplierId(orgId, supplierId)
				.orElseThrow(() -> new EntityNotFoundException("Supplier not found"));

		if (request.getSupplierName() != null) {
			supplier.setSupplierName(request.getSupplierName());
		}
		if (request.getSupplierEmail() != null) {
			if (supplierRepo.existsByOrg_OrgIdAndSupplierEmail(orgId, request.getSupplierEmail())
					&& !request.getSupplierEmail().equals(supplier.getSupplierEmail())) {
				throw new DuplicateEntityException("Supplier email already exists for this organization");
			}
			supplier.setSupplierEmail(request.getSupplierEmail());
		}
		if (request.getMobileNo() != null) {
			supplier.setMobileNo(request.getMobileNo());
		}
		if (request.getGstNo() != null) {
			supplier.setGstNo(request.getGstNo());
		}
		if (request.getAddress() != null) {
			supplier.setAddress(toAddressEntities(request.getAddress()));
		}

		Supplier saved = supplierRepo.save(supplier);
		return toResponse(saved);
	}

	public void deleteSupplier(Long orgId, Long supplierId) {
		Supplier supplier = supplierRepo.findByOrg_OrgIdAndSupplierId(orgId, supplierId)
				.orElseThrow(() -> new EntityNotFoundException("Supplier not found"));
		supplierRepo.delete(supplier);
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

	private SupplierResponse toResponse(Supplier supplier) {
		return new SupplierResponse(
				supplier.getSupplierId(),
				supplier.getOrg().getOrgId(),
				supplier.getSupplierName(),
				supplier.getSupplierEmail(),
				supplier.getMobileNo(),
				supplier.getGstNo(),
				supplier.getAddress() == null ? null
						: supplier.getAddress().stream().map(this::toAddressResponse).toList(),
				supplier.getCreatedOn(),
				supplier.getModifiedOn()
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
