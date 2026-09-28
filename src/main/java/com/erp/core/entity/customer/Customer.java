//$Id$
package com.erp.core.entity.customer;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.Instant;
import java.util.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.erp.core.entity.common.Address;
import com.erp.core.entity.organization.Organization;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Customer {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long customerId;
	
	@ManyToOne
	@JoinColumn(name = "orgId", nullable = false)
	@JsonIgnore
	private Organization org;
	
	@Column(nullable=false)
	private String customerName;

	@Column(nullable=false)
	private String customerEmail;
	
	@Column(nullable=false)
	private String mobileNo;
	
	@Column(nullable=false)
	private String gstNo;
	
	@ElementCollection
	@CollectionTable(name = "customer_address", joinColumns = @JoinColumn(name = "customer_id"))
	private List<Address> address = new ArrayList<>();
	
	@CreationTimestamp
	@Column(updatable=false,nullable=false)
	private Instant createdOn;
	
	@UpdateTimestamp
	@Column(nullable=false)
	private Instant modifiedOn;
}