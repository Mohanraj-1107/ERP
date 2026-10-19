package com.erp.core.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.erp.core.entity.inventory.Inventory;
import com.erp.core.entity.inventory.InventoryResponse;
import com.erp.core.entity.product.Product;
import com.erp.core.exception.EntityNotFoundException;
import com.erp.core.repository.InventoryRepo;
import com.erp.core.repository.ProductRepo;

@Service
public class InventoryService {

	private final InventoryRepo inventoryRepo;
	private final ProductRepo productRepo;

	public InventoryService(InventoryRepo inventoryRepo, ProductRepo productRepo) {
		this.inventoryRepo = inventoryRepo;
		this.productRepo = productRepo;
	}

	public InventoryResponse getInventory(long orgId, long productId) {
		Inventory inventory = inventoryRepo
				.findByOrg_OrgIdAndProduct_ProductId(orgId, productId)
				.orElseThrow(() -> new EntityNotFoundException("Inventory not found"));
		return toResponse(inventory);
	}

	public InventoryResponse addStock(long orgId, long productId, double quantity) {
		Product product = findProduct(orgId, productId);
		validateQuantity(quantity);
		Inventory inventory = findOrCreateInventory(product);
		inventory.setQuantity(inventory.getQuantity() + quantity);
		return toResponse(inventoryRepo.save(inventory));
	}

	public InventoryResponse removeStock(long orgId, long productId, double quantity) {
		Product product = findProduct(orgId, productId);
		validateQuantity(quantity);
		Inventory inventory = inventoryRepo
				.findByOrg_OrgIdAndProduct_ProductId(orgId, productId)
				.orElseThrow(() -> new EntityNotFoundException("Inventory not found"));
		if (inventory.getQuantity() - quantity < 0) {
			throw new IllegalArgumentException("Cannot remove more stock than available");
		}
		inventory.setQuantity(inventory.getQuantity() - quantity);
		return toResponse(inventoryRepo.save(inventory));
	}

	public List<InventoryResponse> listLowStock(long orgId) {
		return inventoryRepo.findLowStock(orgId).stream()
				.map(this::toResponse)
				.toList();
	}

	public Page<InventoryResponse> listInventory(long orgId, Pageable pageable) {
		return inventoryRepo.findAllByOrg_OrgId(orgId, pageable)
				.map(this::toResponse);
	}

	private Product findProduct(long orgId, long productId) {
		return productRepo.findByOrg_OrgIdAndProductId(orgId, productId)
				.orElseThrow(() -> new EntityNotFoundException("Product not found"));
	}

	private Inventory findOrCreateInventory(Product product) {
		return inventoryRepo.findByOrg_OrgIdAndProduct_ProductId(product.getOrg().getOrgId(),
				product.getProductId())
				.orElseGet(() -> {
					Inventory inventory = new Inventory();
					inventory.setOrg(product.getOrg());
					inventory.setProduct(product);
					inventory.setQuantity(0);
					return inventoryRepo.save(inventory);
				});
	}

	private void validateQuantity(double quantity) {
		if (quantity <= 0) {
			throw new IllegalArgumentException("Quantity must be greater than zero");
		}
	}

	private InventoryResponse toResponse(Inventory inventory) {
		return new InventoryResponse(
				inventory.getInventoryId(),
				inventory.getProduct().getProductId(),
				inventory.getOrg().getOrgId(),
				inventory.getQuantity(),
				inventory.getCreatedOn(),
				inventory.getModifiedOn()
		);
	}
}
