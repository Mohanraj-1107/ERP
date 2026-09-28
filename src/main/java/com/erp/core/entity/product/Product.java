//$Id$
package com.erp.core.entity.product;

import java.time.Instant;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.erp.core.entity.organization.Organization;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Product {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long productId;
	
	@ManyToOne
	@JoinColumn(name = "orgId", nullable = false)
	@JsonIgnore
	private Organization org;
	
	@Column(nullable=false,unique=true)
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
	private Unit unit;
	
	@CreationTimestamp
	@Column(updatable=false,nullable=false)
	private Instant createdOn;
	
	@UpdateTimestamp
	@Column(nullable=false)
	private Instant modifiedOn;

}
