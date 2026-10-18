//$Id$
package com.erp.core.entity.product;

import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.erp.core.entity.organization.Organization;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@SuppressWarnings("unused")
@Getter
@Setter
@Entity
@Table(name = "product",
		uniqueConstraints = {
			@UniqueConstraint(name = "uk_product_org_code", columnNames = {"orgId", "productCode"}),
			@UniqueConstraint(name = "uk_product_org_name", columnNames = {"orgId", "productName"})
		})
public class Product {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long productId;
	
	@ManyToOne
	@JoinColumn(name = "orgId", nullable = false)
	@JsonIgnore
	private Organization org;
	
	@Column(nullable=false)
	private String productCode;
	
	@Column(nullable=false)
	private String productName;
	
	@Column(nullable=false)
	private double purchasePrice;
	
	@Column(nullable=false)
	private double sellingPrice;
	
	@Column(nullable=false)
	private double taxRate;
	
	private double minimumStock;
	
	@Enumerated(EnumType.STRING)
	private Unit unit=Unit.KG;
	
	@CreationTimestamp
	@Column(updatable=false,nullable=false)
	private Instant createdOn;
	
	@UpdateTimestamp
	@Column(nullable=false)
	private Instant modifiedOn;
	
	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}

	public Organization getOrg() {
		return org;
	}

	public void setOrg(Organization org) {
		this.org = org;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public double getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public double getSellingPrice() {
		return sellingPrice;
	}

	public void setSellingPrice(double sellingPrice) {
		this.sellingPrice = sellingPrice;
	}

	public double getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(double taxRate) {
		this.taxRate = taxRate;
	}

	public double getMinimumStock() {
		return minimumStock;
	}

	public void setMinimumStock(double minimumStock) {
		this.minimumStock = minimumStock;
	}

	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	public Instant getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Instant createdOn) {
		this.createdOn = createdOn;
	}

	public Instant getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(Instant modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
}
