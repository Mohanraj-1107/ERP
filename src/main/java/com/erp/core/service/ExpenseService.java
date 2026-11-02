//$Id$
package com.erp.core.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.erp.core.entity.expense.CreateExpenseRequest;
import com.erp.core.entity.expense.Expense;
import com.erp.core.entity.expense.UpdateExpenseRequest;
import com.erp.core.entity.organization.Organization;
import com.erp.core.exception.EntityNotFoundException;
import com.erp.core.repository.ExpenseRepo;
import com.erp.core.repository.OrganizationRepo;

@Service
public class ExpenseService {
	
	private ExpenseRepo expenseRepo;
	
	private OrganizationRepo organizationRepo;
	
	public ExpenseService(OrganizationRepo organizationRepo,ExpenseRepo expenseRepo) {
		this.expenseRepo=expenseRepo;
		this.organizationRepo=organizationRepo;
	}
	
	public Page<Expense> getAllExpenses(Long orgId,Pageable pageable){
		return expenseRepo.findByOrg_OrgId(orgId,pageable);
	}
	
	public Expense createExpense(Long orgId,CreateExpenseRequest createExpenseRequest)throws Exception{
		Organization org = organizationRepo.findById(orgId)
				.orElseThrow(() -> new EntityNotFoundException("Organization not found"));
		if(createExpenseRequest.getPaymentStatus()==null) {
			throw new IllegalArgumentException("Payment status cannot be null");
		}
		if(createExpenseRequest.getTotalAmount()<=0D) {
			throw new IllegalArgumentException("Expense amount cannot be negative or zero");
		}
		if(createExpenseRequest.getExpenseCategory()==null) {
			throw new IllegalArgumentException("Expense category cannot be null");
		}
		if(createExpenseRequest.getExpenseDate()==null) {
			throw new IllegalArgumentException("Expense Date cannot be null");
		}
		Expense expense=new Expense();
		expense.setOrg(org);
		expense.setPaymentStatus(createExpenseRequest.getPaymentStatus());
		expense.setExpenseDate(createExpenseRequest.getExpenseDate());
		expense.setTotalAmount(createExpenseRequest.getTotalAmount());
		expense.setExpenseCategory(createExpenseRequest.getExpenseCategory());
		expense.setDescription(createExpenseRequest.getDescription());
		return expenseRepo.save(expense);
	}
	
	public Expense getExpense(Long orgId,Long expenseId)throws Exception{
		return expenseRepo.findByOrg_OrgIdAndExpenseId(orgId, expenseId)
				.orElseThrow(()->new EntityNotFoundException("Expense not found for id "+expenseId));
	}
	
	public Expense updateExpense(Long orgId,UpdateExpenseRequest updateExpenseRequest,Long expenseId){
		Organization org = organizationRepo.findById(orgId)
				.orElseThrow(() -> new EntityNotFoundException("Organization not found"));
		Expense expense=expenseRepo.findByOrg_OrgIdAndExpenseId(orgId,expenseId)
				.orElseThrow(()->new EntityNotFoundException("Expense not found for id "+expenseId));
		if(updateExpenseRequest.getDescription()!=null) {
			expense.setDescription(updateExpenseRequest.getDescription());
		}
		if(updateExpenseRequest.getExpenseCategory()!=null) {
			expense.setExpenseCategory(updateExpenseRequest.getExpenseCategory());
		}
		if(updateExpenseRequest.getTotalAmount()>0) {
			expense.setTotalAmount(updateExpenseRequest.getTotalAmount());
		}
		if(updateExpenseRequest.getPaymentStatus()!=null) {
			expense.setPaymentStatus(updateExpenseRequest.getPaymentStatus());
		}
		if(updateExpenseRequest.getExpenseDate()!=null) {
			expense.setExpenseDate(updateExpenseRequest.getExpenseDate());
		}
		return expenseRepo.save(expense);
	}
	
	public void deleteExpense(Long orgId,Long expenseId)throws Exception{
		Expense expense=expenseRepo.findByOrg_OrgIdAndExpenseId(orgId, expenseId)
				.orElseThrow(()->new EntityNotFoundException("Expense not found for id "+expenseId));
		expenseRepo.delete(expense);
	}
}
