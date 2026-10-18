package com.erp.core.service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp.core.entity.purchase.CreatePurchaseRequest;
import com.erp.core.entity.purchase.Purchase;
import com.erp.core.entity.purchase.PurchaseItems;
import com.erp.core.entity.purchase.PurchaseResponse;
import com.erp.core.entity.purchase.PurchaseStatus;
import com.erp.core.entity.purchase.UpdatePurchaseRequest;
import com.erp.core.entity.purchase.PurchaseItemRequest;
import com.erp.core.entity.purchase.PurchaseItemResponse;
import com.erp.core.entity.organization.Organization;
import com.erp.core.entity.supplier.Supplier;
import com.erp.core.entity.product.Product;
import com.erp.core.exception.EntityNotFoundException;
import com.erp.core.repository.InventoryRepo;
import com.erp.core.repository.OrganizationRepo;
import com.erp.core.repository.ProductRepo;
import com.erp.core.repository.PurchaseRepo;
import com.erp.core.repository.SupplierRepo;

@Service
public class PurchaseService {

	private final PurchaseRepo purchaseRepo;
	private final OrganizationRepo organizationRepo;
	private final SupplierRepo supplierRepo;
	private final ProductRepo productRepo;
	private final InventoryRepo inventoryRepo;

	public PurchaseService(PurchaseRepo purchaseRepo, OrganizationRepo organizationRepo,
			SupplierRepo supplierRepo, ProductRepo productRepo, InventoryRepo inventoryRepo) {
		this.purchaseRepo = purchaseRepo;
		this.organizationRepo = organizationRepo;
		this.supplierRepo = supplierRepo;
		this.productRepo = productRepo;
		this.inventoryRepo = inventoryRepo;
	}

	@Transactional
	public PurchaseResponse createPurchase(Long orgId, CreatePurchaseRequest request) {
		Organization org = organizationRepo.findById(orgId)
				.orElseThrow(() -> new EntityNotFoundException("Organization not found"));

		Supplier supplier = supplierRepo.findByOrg_OrgIdAndSupplierId(orgId, request.getSupplierId())
				.orElseThrow(() -> new EntityNotFoundException("Supplier not found for this organization"));

		if (request.getItems() == null || request.getItems().isEmpty()) {
			throw new IllegalArgumentException("At least one purchase item is required");
		}

		Purchase purchase = new Purchase();
		purchase.setOrg(org);
		purchase.setSupplier(supplier);
		purchase.setPurchaseNumber("PO-" + System.currentTimeMillis());
		purchase.setPurchasedDate(Instant.now());
		purchase.setStatus(PurchaseStatus.DRAFT);

		List<PurchaseItems> items = request.getItems().stream().map(itemReq -> {
			Product product = productRepo.findByOrg_OrgIdAndProductId(orgId, itemReq.getProductId())
					.orElseThrow(() -> new EntityNotFoundException("Product not found for this organization"));

			double quantity = itemReq.getQuantity();
			double unitPrice = itemReq.getUnitPrice();
			double taxRate = itemReq.getTaxRate();
			double discountRate = itemReq.getDiscountRate();

			double discountAmount = quantity * unitPrice * (discountRate / 100.0);
			double taxableAmount = (quantity * unitPrice) - discountAmount;
			double taxAmount = taxableAmount * (taxRate / 100.0);
			double totalAmount = taxableAmount + taxAmount;

			PurchaseItems item = new PurchaseItems();
			item.setPurchase(purchase);
			item.setProduct(product);
			item.setQuantity(quantity);
			item.setUnitPrice(unitPrice);
			item.setTaxRate(taxRate);
			item.setTaxAmount(taxAmount);
			item.setDiscountRate(discountRate);
			item.setDiscountAmount(discountAmount);
			item.setTotalAmount(totalAmount);
			return item;
		}).collect(Collectors.toList());

		// Compute totals after building items (avoids lambda mutation issue)
		double subtotal = items.stream()
				.mapToDouble(i -> i.getQuantity() * i.getUnitPrice() - i.getDiscountAmount()).sum();
		double totalTax = items.stream().mapToDouble(PurchaseItems::getTaxAmount).sum();

		purchase.setItems(items);
		purchase.setSubtotalAmount(subtotal);
		purchase.setTotalTaxAmount(totalTax);
		purchase.setTotalAmount(subtotal + totalTax);

		Purchase saved = purchaseRepo.save(purchase);
		return toResponse(saved);
	}

	public PurchaseResponse getPurchase(Long orgId, Long purchaseId) {
		Purchase purchase = purchaseRepo.findByOrg_OrgIdAndPurchaseId(orgId, purchaseId)
				.orElseThrow(() -> new EntityNotFoundException("Purchase not found"));
		return toResponse(purchase);
	}

	public Page<PurchaseResponse> listPurchases(Long orgId, Pageable pageable) {
		return purchaseRepo.findAllByOrg_OrgId(orgId, pageable).map(this::toResponse);
	}

	@Transactional
	public PurchaseResponse updatePurchase(Long orgId, Long purchaseId, UpdatePurchaseRequest request) {
		Purchase purchase = purchaseRepo.findByOrg_OrgIdAndPurchaseId(orgId, purchaseId)
				.orElseThrow(() -> new EntityNotFoundException("Purchase not found"));

		if (purchase.getStatus() != PurchaseStatus.DRAFT) {
			throw new IllegalArgumentException("Only draft purchases can be updated");
		}

		if (request.getSupplierId() != null) {
			Supplier supplier = supplierRepo.findByOrg_OrgIdAndSupplierId(orgId, request.getSupplierId())
					.orElseThrow(() -> new EntityNotFoundException("Supplier not found for this organization"));
			purchase.setSupplier(supplier);
		}

		if (request.getPurchasedDate() != null) {
			purchase.setPurchasedDate(request.getPurchasedDate());
		}

		if (request.getItems() != null) {
			purchase.getItems().clear();

			List<PurchaseItems> newItems = request.getItems().stream().map(itemReq -> {
				Product product = productRepo.findByOrg_OrgIdAndProductId(orgId, itemReq.getProductId())
						.orElseThrow(() -> new EntityNotFoundException("Product not found for this organization"));

				double quantity = itemReq.getQuantity();
				double unitPrice = itemReq.getUnitPrice();
				double taxRate = itemReq.getTaxRate();
				double discountRate = itemReq.getDiscountRate();

				double discountAmount = quantity * unitPrice * (discountRate / 100.0);
				double taxableAmount = (quantity * unitPrice) - discountAmount;
				double taxAmount = taxableAmount * (taxRate / 100.0);
				double totalAmount = taxableAmount + taxAmount;

				PurchaseItems item = new PurchaseItems();
				item.setPurchase(purchase);
				item.setProduct(product);
				item.setQuantity(quantity);
				item.setUnitPrice(unitPrice);
				item.setTaxRate(taxRate);
				item.setTaxAmount(taxAmount);
				item.setDiscountRate(discountRate);
				item.setDiscountAmount(discountAmount);
				item.setTotalAmount(totalAmount);
				return item;
			}).collect(Collectors.toList());

			purchase.getItems().addAll(newItems);

			double subtotal = newItems.stream()
					.mapToDouble(i -> i.getQuantity() * i.getUnitPrice() - i.getDiscountAmount()).sum();
			double totalTax = newItems.stream().mapToDouble(PurchaseItems::getTaxAmount).sum();

			purchase.setSubtotalAmount(subtotal);
			purchase.setTotalTaxAmount(totalTax);
			purchase.setTotalAmount(subtotal + totalTax);
		}

		Purchase saved = purchaseRepo.save(purchase);
		return toResponse(saved);
	}

	@Transactional
	public PurchaseResponse completePurchase(Long orgId, Long purchaseId) {
		Purchase purchase = purchaseRepo.findByOrg_OrgIdAndPurchaseId(orgId, purchaseId)
				.orElseThrow(() -> new EntityNotFoundException("Purchase not found"));

		if (purchase.getStatus() != PurchaseStatus.DRAFT) {
			throw new IllegalArgumentException("Only draft purchases can be completed");
		}

		for (PurchaseItems item : purchase.getItems()) {
			increaseInventory(orgId, item.getProduct().getProductId(), item.getQuantity());
		}

		purchase.setStatus(PurchaseStatus.COMPLETED);
		Purchase saved = purchaseRepo.save(purchase);
		return toResponse(saved);
	}

	@Transactional
	public PurchaseResponse cancelPurchase(Long orgId, Long purchaseId) {
		Purchase purchase = purchaseRepo.findByOrg_OrgIdAndPurchaseId(orgId, purchaseId)
				.orElseThrow(() -> new EntityNotFoundException("Purchase not found"));

		switch (purchase.getStatus()) {
		case DRAFT:
			// Draft cancelled — nothing was received, no inventory impact
			purchase.setStatus(PurchaseStatus.CANCELLED);
			break;
		case COMPLETED:
			// Completed cancelled — reverse the stock that was added
			for (PurchaseItems item : purchase.getItems()) {
				decreaseInventory(orgId, item.getProduct().getProductId(), item.getQuantity());
			}
			purchase.setStatus(PurchaseStatus.CANCELLED);
			break;
		case CANCELLED:
			throw new IllegalArgumentException("Purchase is already cancelled");
		default:
			throw new IllegalArgumentException("Cannot cancel purchase in status: " + purchase.getStatus());
		}

		Purchase saved = purchaseRepo.save(purchase);
		return toResponse(saved);
	}

	@Transactional
	public void deletePurchase(Long orgId, Long purchaseId) {
		Purchase purchase = purchaseRepo.findByOrg_OrgIdAndPurchaseId(orgId, purchaseId)
				.orElseThrow(() -> new EntityNotFoundException("Purchase not found"));

		if (purchase.getStatus() != PurchaseStatus.DRAFT) {
			throw new IllegalArgumentException("Only draft purchases can be deleted");
		}

		purchaseRepo.delete(purchase);
	}

	private void increaseInventory(Long orgId, Long productId, double quantity) {
		inventoryRepo.findByOrg_OrgIdAndProduct_ProductId(orgId, productId)
				.ifPresentOrElse(
						inventory -> {
							inventory.setQuantity(inventory.getQuantity() + quantity);
							inventoryRepo.save(inventory);
						},
						() -> {
							throw new EntityNotFoundException(
									"Inventory not found for product " + productId + " in organization " + orgId);
						});
	}

	private void decreaseInventory(Long orgId, Long productId, double quantity) {
		inventoryRepo.findByOrg_OrgIdAndProduct_ProductId(orgId, productId)
				.ifPresentOrElse(
						inventory -> {
							double newQty = inventory.getQuantity() - quantity;
							if (newQty < 0) {
								throw new IllegalArgumentException(
										"Cannot reverse stock: insufficient inventory for product " + productId);
							}
							inventory.setQuantity(newQty);
							inventoryRepo.save(inventory);
						},
						() -> {
							throw new EntityNotFoundException(
									"Inventory not found for product " + productId + " in organization " + orgId);
						});
	}

	private PurchaseResponse toResponse(Purchase purchase) {
		return new PurchaseResponse(
				purchase.getPurchaseId(),
				purchase.getOrg().getOrgId(),
				purchase.getPurchaseNumber(),
				purchase.getSupplier() != null ? purchase.getSupplier().getSupplierId() : null,
				purchase.getSupplier() != null ? purchase.getSupplier().getSupplierName() : null,
				purchase.getSubtotalAmount(),
				purchase.getTotalAmount(),
				purchase.getTotalTaxAmount(),
				purchase.getPurchasedDate(),
				purchase.getStatus(),
				purchase.getItems() == null ? null
						: purchase.getItems().stream().map(this::toItemResponse).toList(),
				purchase.getCreatedOn(),
				purchase.getModifiedOn()
		);
	}

	private PurchaseItemResponse toItemResponse(PurchaseItems item) {
		return new PurchaseItemResponse(
				item.getId(),
				item.getProduct() != null ? item.getProduct().getProductId() : null,
				item.getProduct() != null ? item.getProduct().getProductName() : null,
				item.getQuantity(),
				item.getUnitPrice(),
				item.getTaxRate(),
				item.getTaxAmount(),
				item.getTotalAmount(),
				item.getDiscountRate(),
				item.getDiscountAmount()
		);
	}
}
