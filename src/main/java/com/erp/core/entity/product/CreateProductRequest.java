package com.erp.core.entity.product;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateProductRequest {

	@NotBlank(message = "Product code is required")
	private String productCode;

	@NotBlank(message = "Product name is required")
	private String productName;

	@DecimalMin(value = "0.0", inclusive = false, message = "Purchase price must be greater than 0")
	private double purchasePrice;

	@DecimalMin(value = "0.0", inclusive = false, message = "Selling price must be greater than 0")
	private double sellingPrice;

	@DecimalMin(value = "0.0", message = "Tax rate cannot be negative")
	@DecimalMax(value = "100.0", message = "Tax rate cannot exceed 100")
	private double taxRate;

	@DecimalMin(value = "0.0", message = "Minimum stock cannot be negative")
	private double minimumStock = 0.0;

	private Unit unit = Unit.KG;

	public String getProductCode() { return productCode; }
	public void setProductCode(String productCode) { this.productCode = productCode; }
	public String getProductName() { return productName; }
	public void setProductName(String productName) { this.productName = productName; }
	public double getPurchasePrice() { return purchasePrice; }
	public void setPurchasePrice(double purchasePrice) { this.purchasePrice = purchasePrice; }
	public double getSellingPrice() { return sellingPrice; }
	public void setSellingPrice(double sellingPrice) { this.sellingPrice = sellingPrice; }
	public double getTaxRate() { return taxRate; }
	public void setTaxRate(double taxRate) { this.taxRate = taxRate; }
	public double getMinimumStock() { return minimumStock; }
	public void setMinimumStock(double minimumStock) { this.minimumStock = minimumStock; }
	public Unit getUnit() { return unit; }
	public void setUnit(Unit unit) { this.unit = unit; }
}
