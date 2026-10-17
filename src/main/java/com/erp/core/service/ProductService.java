package com.erp.core.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.erp.core.entity.inventory.Inventory;
import com.erp.core.entity.organization.Organization;
import com.erp.core.entity.product.CreateProductRequest;
import com.erp.core.entity.product.Product;
import com.erp.core.entity.product.ProductResponse;
import com.erp.core.entity.product.UpdateProductRequest;
import com.erp.core.entity.product.Unit;
import com.erp.core.exception.DuplicateEntityException;
import com.erp.core.exception.EntityNotFoundException;
import com.erp.core.repository.InventoryRepo;
import com.erp.core.repository.OrganizationRepo;
import com.erp.core.repository.ProductRepo;

@Service
public class ProductService {

	private final ProductRepo productRepo;
	private final OrganizationRepo organizationRepo;
	private final InventoryRepo inventoryRepo;

	public ProductService(ProductRepo productRepo, OrganizationRepo organizationRepo,
			InventoryRepo inventoryRepo) {
		this.productRepo = productRepo;
		this.organizationRepo = organizationRepo;
		this.inventoryRepo = inventoryRepo;
	}

	public ProductResponse createProduct(Long orgId, CreateProductRequest request) {
		Organization org = organizationRepo.findById(orgId)
				.orElseThrow(() -> new EntityNotFoundException("Organization not found"));

		if (productRepo.existsByOrg_OrgIdAndProductCode(orgId, request.getProductCode())) {
			throw new DuplicateEntityException("Product code already exists for this organization");
		}
		if (productRepo.existsByOrg_OrgIdAndProductName(orgId, request.getProductName())) {
			throw new DuplicateEntityException("Product name already exists for this organization");
		}
		if (request.getPurchasePrice() <= 0 || request.getSellingPrice() <= 0) {
			throw new IllegalArgumentException("Prices must be greater than zero");
		}
		if (request.getTaxRate() < 0 || request.getTaxRate() > 100) {
			throw new IllegalArgumentException("Tax rate must be between 0 and 100");
		}
		if (request.getMinimumStock() < 0) {
			throw new IllegalArgumentException("Minimum stock cannot be negative");
		}

		Product product = new Product();
		product.setOrg(org);
		product.setProductCode(request.getProductCode());
		product.setProductName(request.getProductName());
		product.setPurchasePrice(request.getPurchasePrice());
		product.setSellingPrice(request.getSellingPrice());
		product.setTaxRate(request.getTaxRate());
		product.setMinimumStock(request.getMinimumStock());
		product.setUnit(request.getUnit() == null ? Unit.KG : request.getUnit());

		Product saved = productRepo.save(product);
		createInventory(saved);
		return toResponse(saved);
	}

	private void createInventory(Product product) {
		Inventory inventory = new Inventory();
		inventory.setOrg(product.getOrg());
		inventory.setProduct(product);
		inventory.setQuantity(0);
		inventoryRepo.save(inventory);
	}

	public ProductResponse getProduct(Long orgId, Long productId) {
		Product product = productRepo.findByOrg_OrgIdAndProductId(orgId, productId)
				.orElseThrow(() -> new EntityNotFoundException("Product not found"));
		return toResponse(product);
	}

	public Page<ProductResponse> listProducts(Long orgId, Pageable pageable) {
		return productRepo.findAllByOrg_OrgId(orgId, pageable).map(this::toResponse);
	}

	public ProductResponse updateProduct(Long orgId, Long productId, UpdateProductRequest request) {
		Product product = productRepo.findByOrg_OrgIdAndProductId(orgId, productId)
				.orElseThrow(() -> new EntityNotFoundException("Product not found"));

		if (request.getProductCode() != null) {
			if (productRepo.existsByOrg_OrgIdAndProductCode(orgId, request.getProductCode())
					&& !request.getProductCode().equals(product.getProductCode())) {
				throw new DuplicateEntityException("Product code already exists for this organization");
			}
			product.setProductCode(request.getProductCode());
		}
		if (request.getProductName() != null) {
			if (productRepo.existsByOrg_OrgIdAndProductName(orgId, request.getProductName())
					&& !request.getProductName().equals(product.getProductName())) {
				throw new DuplicateEntityException("Product name already exists for this organization");
			}
			product.setProductName(request.getProductName());
		}
		if (request.getPurchasePrice() != null) {
			if (request.getPurchasePrice() <= 0) throw new IllegalArgumentException("Purchase price must be greater than zero");
			product.setPurchasePrice(request.getPurchasePrice());
		}
		if (request.getSellingPrice() != null) {
			if (request.getSellingPrice() <= 0) throw new IllegalArgumentException("Selling price must be greater than zero");
			product.setSellingPrice(request.getSellingPrice());
		}
		if (request.getTaxRate() != null) {
			if (request.getTaxRate() < 0 || request.getTaxRate() > 100) throw new IllegalArgumentException("Tax rate must be between 0 and 100");
			product.setTaxRate(request.getTaxRate());
		}
		if (request.getMinimumStock() != null) {
			if (request.getMinimumStock() < 0) throw new IllegalArgumentException("Minimum stock cannot be negative");
			product.setMinimumStock(request.getMinimumStock());
		}
		if (request.getUnit() != null) {
			product.setUnit(request.getUnit());
		}

		Product saved = productRepo.save(product);
		return toResponse(saved);
	}

	public void deleteProduct(Long orgId, Long productId) {
		Product product = productRepo.findByOrg_OrgIdAndProductId(orgId, productId)
				.orElseThrow(() -> new EntityNotFoundException("Product not found"));
		inventoryRepo.deleteByProduct(product);
		productRepo.delete(product);
	}

	private ProductResponse toResponse(Product product) {
		return new ProductResponse(
				product.getProductId(),
				product.getOrg().getOrgId(),
				product.getProductCode(),
				product.getProductName(),
				product.getPurchasePrice(),
				product.getSellingPrice(),
				product.getTaxRate(),
				product.getMinimumStock(),
				product.getUnit(),
				product.getCreatedOn(),
				product.getModifiedOn()
		);
	}
}
