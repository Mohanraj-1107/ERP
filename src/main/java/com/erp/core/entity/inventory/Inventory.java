//$Id$
package com.erp.core.entity.inventory;

import java.time.Instant;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.erp.core.entity.organization.Organization;
import com.erp.core.entity.product.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class Inventory {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@ManyToOne
	@JoinColumn(name = "orgId", nullable = false)
	@JsonIgnore
	private Organization org;
	
	@ManyToOne
	@JoinColumn(name = "productId", nullable = false)
	private Product product;
	
	@Column(nullable=false)
	private double quantity;
	
	@CreationTimestamp
	@Column(updatable=false,nullable=false)
	private Instant createdOn;
	
	@UpdateTimestamp
	@Column(nullable=false)
	private Instant modifiedOn;
	
}
