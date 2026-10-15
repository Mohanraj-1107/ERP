package com.erp.core.entity.product;

import jakarta.validation.constraints.Size;

public class UpdateProductRequest {

	@Size(min = 1, max = 50, message = "Product code must be between 1 and 50 characters")
	private String productCode;

	@Size(min = 1, max = 100, message = "Product name must be between 1 and 100 characters")
	private String productName;

	private Double purchasePrice;
	private Double sellingPrice;
	private Double taxRate;
	private Double minimumStock;
	private Unit unit;

	public String getProductCode() { return productCode; }
	public void setProductCode(String productCode) { this.productCode = productCode; }
	public String getProductName() { return productName; }
	public void setProductName(String productName) { this.productName = productName; }
	public Double getPurchasePrice() { return purchasePrice; }
	public void setPurchasePrice(Double purchasePrice) { this.purchasePrice = purchasePrice; }
	public Double getSellingPrice() { return sellingPrice; }
	public void setSellingPrice(Double sellingPrice) { this.sellingPrice = sellingPrice; }
	public Double getTaxRate() { return taxRate; }
	public void setTaxRate(Double taxRate) { this.taxRate = taxRate; }
	public Double getMinimumStock() { return minimumStock; }
	public void setMinimumStock(Double minimumStock) { this.minimumStock = minimumStock; }
	public Unit getUnit() { return unit; }
	public void setUnit(Unit unit) { this.unit = unit; }
}
