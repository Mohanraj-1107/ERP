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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "supplier",
		uniqueConstraints = @UniqueConstraint(name = "uk_supplier_org_email", columnNames = {"orgId", "supplierEmail"}))
public class Supplier {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long supplierId;

	@ManyToOne
	@JoinColumn(name = "orgId", nullable = false)
	@JsonIgnore
	private Organization org;

	@Column(nullable = false)
	private String supplierName;

	@Column(nullable = false)
	private String supplierEmail;

	@Column(nullable = false)
	private String mobileNo;

	@Column(nullable = false)
	private String gstNo;

	@ElementCollection
	@CollectionTable(name = "supplier_address", joinColumns = @JoinColumn(name = "supplier_id"))
	private List<Address> address = new ArrayList<>();

	@CreationTimestamp
	@Column(updatable = false, nullable = false)
	private Instant createdOn;

	@UpdateTimestamp
	@Column(nullable = false)
	private Instant modifiedOn;

	public long getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(long supplierId) {
		this.supplierId = supplierId;
	}

	public Organization getOrg() {
		return org;
	}

	public void setOrg(Organization org) {
		this.org = org;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getSupplierEmail() {
		return supplierEmail;
	}

	public void setSupplierEmail(String supplierEmail) {
		this.supplierEmail = supplierEmail;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getGstNo() {
		return gstNo;
	}

	public void setGstNo(String gstNo) {
		this.gstNo = gstNo;
	}

	public List<Address> getAddress() {
		return address;
	}

	public void setAddress(List<Address> address) {
		this.address = address;
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
