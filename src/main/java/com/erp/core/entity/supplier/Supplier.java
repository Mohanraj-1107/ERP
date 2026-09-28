//$Id$
package com.erp.core.entity.supplier;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.erp.core.entity.common.Address;
import com.erp.core.entity.organization.Organization;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Supplier {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long supplierId;
	
	@ManyToOne
	@JoinColumn(name = "orgId", nullable = false)
	@JsonIgnore
	private Organization org;
	
	@Column(nullable=false)
	private String SupplierName;

	@Column(nullable=false)
	private String SupplierEmail;
	
	@Column(nullable=false)
	private String mobileNo;
	
	@Column(nullable=false)
	private String gstNo;
	
	@ElementCollection
	@CollectionTable(name = "supplier_address", joinColumns = @JoinColumn(name = "supplier_id"))
	private List<Address> address = new ArrayList<>();
	
	@CreationTimestamp
	@Column(updatable=false,nullable=false)
	private Instant createdOn;
	
	@UpdateTimestamp
	@Column(nullable=false)
	private Instant modifiedOn;
}