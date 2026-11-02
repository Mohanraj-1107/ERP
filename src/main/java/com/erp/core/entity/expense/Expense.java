//$Id$
package com.erp.core.entity.expense;

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
import jakarta.persistence.Table;

@Table
@Entity
public class Expense {
    
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long expenseId;
	 
	@ManyToOne
	@JoinColumn(name = "orgId", nullable = false)
	@JsonIgnore
	private Organization org;
	
	@CreationTimestamp
	@Column(updatable = false, nullable = false)
	private Instant createdOn;
	
	@UpdateTimestamp
	@Column(nullable = false)
	private Instant modifiedOn;
	 
	@Enumerated(value=EnumType.ORDINAL)
	@Column(nullable=false)
	private ExpenseStatus paymentStatus=ExpenseStatus.PAYMENT_PENDING;
	
	private double totalAmount;
	
	@Enumerated(value=EnumType.ORDINAL) 
	@Column(nullable=false)
	private ExpenseCategory expenseCategory;

	private String description;
	
	private Instant expenseDate;
	
	
	public Instant getExpenseDate() {
		return expenseDate;
	}

	public void setExpenseDate(Instant expenseDate) {
		this.expenseDate = expenseDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getExpenseId() {
		return expenseId;
	}

	public Organization getOrg() {
		return org;
	}

	public void setOrg(Organization org) {
		this.org = org;
	}

	public Instant getCreatedOn() {
		return createdOn;
	}


	public Instant getModifiedOn() {
		return modifiedOn;
	}

	public ExpenseStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(ExpenseStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public ExpenseCategory getExpenseCategory() {
		return expenseCategory;
	}

	public void setExpenseCategory(ExpenseCategory expenseCategory) {
		this.expenseCategory = expenseCategory;
	}
}
