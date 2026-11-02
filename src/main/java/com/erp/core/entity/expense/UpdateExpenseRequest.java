//$Id$
package com.erp.core.entity.expense;

import java.time.Instant;

public class UpdateExpenseRequest {
		
	private ExpenseStatus paymentStatus;
	
	private double totalAmount;
	
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
