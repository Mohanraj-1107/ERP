package com.erp.core.service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp.core.entity.sale.CreateSaleRequest;
import com.erp.core.entity.sale.Sale;
import com.erp.core.entity.sale.SalesItems;
import com.erp.core.entity.sale.SaleResponse;
import com.erp.core.entity.sale.SalesStatus;
import com.erp.core.entity.sale.UpdateSaleRequest;
import com.erp.core.entity.sale.SaleItemRequest;
import com.erp.core.entity.sale.SaleItemResponse;
import com.erp.core.entity.customer.Customer;
import com.erp.core.entity.organization.Organization;
import com.erp.core.entity.product.Product;
import com.erp.core.exception.EntityNotFoundException;
import com.erp.core.exception.InvalidRefreshTokenException;
import com.erp.core.repository.InventoryRepo;
import com.erp.core.repository.OrganizationRepo;
import com.erp.core.repository.ProductRepo;
import com.erp.core.repository.SaleRepo;
import com.erp.core.repository.CustomerRepo;

@Service
public class SaleService {

	private final SaleRepo saleRepo;
	private final OrganizationRepo organizationRepo;
	private final CustomerRepo customerRepo;
	private final ProductRepo productRepo;
	private final InventoryRepo inventoryRepo;

	public SaleService(SaleRepo saleRepo, OrganizationRepo organizationRepo,
			CustomerRepo customerRepo, ProductRepo productRepo, InventoryRepo inventoryRepo) {
		this.saleRepo = saleRepo;
		this.organizationRepo = organizationRepo;
		this.customerRepo = customerRepo;
		this.productRepo = productRepo;
		this.inventoryRepo = inventoryRepo;
	}

	@Transactional
	public SaleResponse createSale(Long orgId, CreateSaleRequest request) {
		Organization org = organizationRepo.findById(orgId)
				.orElseThrow(() -> new EntityNotFoundException("Organization not found"));

		Customer customer = customerRepo.findByOrg_OrgIdAndCustomerId(orgId, request.getCustomerId())
				.orElseThrow(() -> new EntityNotFoundException("Customer not found for this organization"));

		if (request.getItems() == null || request.getItems().isEmpty()) {
			throw new IllegalArgumentException("At least one sale item is required");
		}

		Sale sale = new Sale();
		sale.setOrg(org);
		sale.setCustomer(customer);
		sale.setSalesNumber("SO-" + System.currentTimeMillis());
		sale.setPurchasedDate(Instant.now());
		sale.setStatus(SalesStatus.DRAFT);

		List<SalesItems> items = request.getItems().stream().map(itemReq -> {
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

			SalesItems item = new SalesItems();
			item.setSale(sale);
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

		// Compute totals after building items
		double subtotal = items.stream()
				.mapToDouble(i -> i.getQuantity() * i.getUnitPrice() - i.getDiscountAmount()).sum();
		double totalTax = items.stream().mapToDouble(SalesItems::getTaxAmount).sum();

		sale.setItems(items);
		sale.setSubtotalAmount(subtotal);
		sale.setTotalTaxAmount(totalTax);
		sale.setTotalAmount(subtotal + totalTax);

		Sale saved = saleRepo.save(sale);
		return toResponse(saved);
	}

	public SaleResponse getSale(Long orgId, Long saleId) {
		Sale sale = saleRepo.findByOrg_OrgIdAndSaleId(orgId, saleId)
				.orElseThrow(() -> new EntityNotFoundException("Sale not found"));
		return toResponse(sale);
	}

	public Page<SaleResponse> listSales(Long orgId, Pageable pageable) {
		return saleRepo.findAllByOrg_OrgId(orgId, pageable).map(this::toResponse);
	}

	@Transactional
	public SaleResponse updateSale(Long orgId, Long saleId, UpdateSaleRequest request) {
		Sale sale = saleRepo.findByOrg_OrgIdAndSaleId(orgId, saleId)
				.orElseThrow(() -> new EntityNotFoundException("Sale not found"));

		if (sale.getStatus() != SalesStatus.DRAFT) {
			throw new IllegalArgumentException("Only draft sales can be updated");
		}

		if (request.getCustomerId() != null) {
			Customer customer = customerRepo.findByOrg_OrgIdAndCustomerId(orgId, request.getCustomerId())
					.orElseThrow(() -> new EntityNotFoundException("Customer not found for this organization"));
			sale.setCustomer(customer);
		}

		if (request.getPurchasedDate() != null) {
			sale.setPurchasedDate(request.getPurchasedDate());
		}

		if (request.getItems() != null) {
			sale.getItems().clear();

			List<SalesItems> newItems = request.getItems().stream().map(itemReq -> {
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

				SalesItems item = new SalesItems();
				item.setSale(sale);
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

			sale.getItems().addAll(newItems);

			double subtotal = newItems.stream()
					.mapToDouble(i -> i.getQuantity() * i.getUnitPrice() - i.getDiscountAmount()).sum();
			double totalTax = newItems.stream().mapToDouble(SalesItems::getTaxAmount).sum();

			sale.setSubtotalAmount(subtotal);
			sale.setTotalTaxAmount(totalTax);
			sale.setTotalAmount(subtotal + totalTax);
		}

		Sale saved = saleRepo.save(sale);
		return toResponse(saved);
	}

	@Transactional
	public SaleResponse confirmSale(Long orgId, Long saleId) {
		Sale sale = saleRepo.findByOrg_OrgIdAndSaleId(orgId, saleId)
				.orElseThrow(() -> new EntityNotFoundException("Sale not found"));

		if (sale.getStatus() != SalesStatus.DRAFT) {
			throw new IllegalArgumentException("Only draft sales can be confirmed");
		}

		// Validate sufficient inventory for each item before confirming
		for (SalesItems item : sale.getItems()) {
			inventoryRepo.findByOrg_OrgIdAndProduct_ProductId(orgId, item.getProduct().getProductId())
					.ifPresentOrElse(
							inventory -> {
								if (inventory.getQuantity() < item.getQuantity()) {
									throw new IllegalArgumentException(
											"Insufficient inventory for product " + item.getProduct().getProductId()
													+ ". Available: " + inventory.getQuantity()
													+ ", Required: " + item.getQuantity());
								}
							},
							() -> {
								throw new EntityNotFoundException(
										"Inventory not found for product " + item.getProduct().getProductId()
												+ " in organization " + orgId);
							});
		}

		// Decrease inventory for each item
		for (SalesItems item : sale.getItems()) {
			decreaseInventory(orgId, item.getProduct().getProductId(), item.getQuantity());
		}

		sale.setStatus(SalesStatus.COMPLETED);
		Sale saved = saleRepo.save(sale);
		return toResponse(saved);
	}

	@Transactional
	public SaleResponse cancelSale(Long orgId, Long saleId) {
		Sale sale = saleRepo.findByOrg_OrgIdAndSaleId(orgId, saleId)
				.orElseThrow(() -> new EntityNotFoundException("Sale not found"));

		switch (sale.getStatus()) {
		case DRAFT:
			// Draft cancelled — nothing was sold, no inventory impact
			sale.setStatus(SalesStatus.CANCELLED);
			break;
		case COMPLETED:
			// Confirmed cancelled — reverse the stock that was decreased
			for (SalesItems item : sale.getItems()) {
				increaseInventory(orgId, item.getProduct().getProductId(), item.getQuantity());
			}
			sale.setStatus(SalesStatus.CANCELLED);
			break;
		case CANCELLED:
			throw new IllegalArgumentException("Sale is already cancelled");
		default:
			throw new IllegalArgumentException("Cannot cancel sale in status: " + sale.getStatus());
		}

		Sale saved = saleRepo.save(sale);
		return toResponse(saved);
	}

	@Transactional
	public void deleteSale(Long orgId, Long saleId) {
		Sale sale = saleRepo.findByOrg_OrgIdAndSaleId(orgId, saleId)
				.orElseThrow(() -> new EntityNotFoundException("Sale not found"));

		if (sale.getStatus() != SalesStatus.DRAFT) {
			throw new IllegalArgumentException("Only draft sales can be deleted");
		}

		saleRepo.delete(sale);
	}

	private void decreaseInventory(Long orgId, Long productId, double quantity) {
		inventoryRepo.findByOrg_OrgIdAndProduct_ProductId(orgId, productId)
				.ifPresentOrElse(
						inventory -> {
							double newQty = inventory.getQuantity() - quantity;
							if (newQty < 0) {
								throw new IllegalArgumentException(
										"Cannot confirm sale: insufficient inventory for product " + productId);
							}
							inventory.setQuantity(newQty);
							inventoryRepo.save(inventory);
						},
						() -> {
							throw new EntityNotFoundException(
									"Inventory not found for product " + productId + " in organization " + orgId);
						});
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

	private SaleResponse toResponse(Sale sale) {
		return new SaleResponse(
				sale.getSaleId(),
				sale.getOrg().getOrgId(),
				sale.getSalesNumber(),
				sale.getCustomer() != null ? sale.getCustomer().getCustomerId() : null,
				sale.getCustomer() != null ? sale.getCustomer().getCustomerName() : null,
				sale.getSubtotalAmount(),
				sale.getTotalAmount(),
				sale.getTotalTaxAmount(),
				sale.getPurchasedDate(),
				sale.getStatus(),
				sale.getItems() == null ? null
						: sale.getItems().stream().map(this::toItemResponse).toList(),
				sale.getCreatedOn(),
				sale.getModifiedOn()
		);
	}

	private SaleItemResponse toItemResponse(SalesItems item) {
		return new SaleItemResponse(
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
