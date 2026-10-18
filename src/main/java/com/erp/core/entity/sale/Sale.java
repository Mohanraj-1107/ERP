package com.erp.core.entity.sale;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.erp.core.entity.customer.Customer;
import com.erp.core.entity.organization.Organization;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "sale")
public class Sale {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long saleId;

	@ManyToOne
	@JoinColumn(name = "orgId", nullable = false)
	@JsonIgnore
	private Organization org;

	@Column(nullable = false)
	private String salesNumber;

	@ManyToOne
	@JoinColumn(name = "customerId", nullable = false)
	private Customer customer;

	@Column(nullable = false)
	private double subtotalAmount;

	@Column(nullable = false)
	private double totalAmount;

	@Column(nullable = false)
	private double totalTaxAmount;

	@Column(nullable = false)
	private Instant purchasedDate;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SalesStatus status = SalesStatus.DRAFT;

	@OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SalesItems> items = new ArrayList<>();

	@CreationTimestamp
	@Column(updatable = false, nullable = false)
	private Instant createdOn;

	@UpdateTimestamp
	@Column(nullable = false)
	private Instant modifiedOn;
}
