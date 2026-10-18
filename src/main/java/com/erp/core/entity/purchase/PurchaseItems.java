package com.erp.core.entity.purchase;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.erp.core.entity.product.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "purchase_items")
public class PurchaseItems {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne
	@JoinColumn(name = "productId", nullable = false)
	private Product product;

	@ManyToOne
	@JoinColumn(name = "purchaseId", nullable = false)
	@JsonIgnore
	private Purchase purchase;

	@Column(nullable = false)
	private double quantity;

	@Column(nullable = false)
	private double unitPrice;

	@Column(nullable = false)
	private double taxRate;

	private double taxAmount;

	private double totalAmount;

	private double discountRate;

	private double discountAmount;

	@CreationTimestamp
	@Column(updatable = false, nullable = false)
	private Instant createdOn;

	@UpdateTimestamp
	@Column(nullable = false)
	private Instant modifiedOn;
}
