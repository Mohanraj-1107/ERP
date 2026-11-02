//$Id$
package com.erp.core.entity.expense;

import java.time.Instant;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateExpenseRequest {
	
	@NotNull(message = "Payment status is required")
	private ExpenseStatus paymentStatus;
	
	@DecimalMin(value = "0.0", inclusive = false, message = "Expense price must be greater than 0")
	private double totalAmount;
	
	@NotNull(message = "Expense category is required")
	private ExpenseCategory expenseCategory;
	
	@NotBlank(message = "Description is required")
	private String description;
	
	@NotNull(message = "Expense date is required")
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
