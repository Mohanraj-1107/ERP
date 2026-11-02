//$Id$
package com.erp.core.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.erp.core.common.OrgContext;
import com.erp.core.entity.expense.CreateExpenseRequest;
import com.erp.core.entity.expense.Expense;
import com.erp.core.entity.expense.UpdateExpenseRequest;
import com.erp.core.service.ExpenseService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/expense")
public class ExpenseController {
	
	private ExpenseService expenseService=null;
	 
	public ExpenseController(ExpenseService expenseService) {
		this.expenseService=expenseService;
	}
	
	@GetMapping("")
	public ResponseEntity<Page<Expense>> getAllExpenses(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size){
		Long orgId=OrgContext.currentOrgId();
		Pageable pageable=PageRequest.of(page, size);
		return ResponseEntity.ok(expenseService.getAllExpenses(orgId,pageable ));
	}

	@PostMapping("")
	public ResponseEntity<Expense> createExpense(@RequestBody @Valid CreateExpenseRequest createExpenseRequest)throws Exception{
		Long orgId=OrgContext.currentOrgId();
		return ResponseEntity.ok(expenseService.createExpense(orgId, createExpenseRequest));
	}
	
	@GetMapping("/{expenseId}")
	public ResponseEntity<Object> getExpense(@PathVariable Long expenseId)throws Exception{
		Long orgId=OrgContext.currentOrgId();
		return ResponseEntity.ok(expenseService.getExpense(orgId, expenseId));
	}
	
	@PatchMapping("/{expenseId}")
	public ResponseEntity<Object> updateExpense(@RequestBody UpdateExpenseRequest updateExpenseRequest,@PathVariable Long expenseId)throws Exception{
		Long orgId=OrgContext.currentOrgId();
		return ResponseEntity.ok(expenseService.updateExpense(orgId,updateExpenseRequest,expenseId));
	}
	
	@DeleteMapping("/{expenseId}")
	public ResponseEntity<Object> deleteExpense()throws Exception{
		Long orgId=OrgContext.currentOrgId();
		expenseService.deleteExpense(orgId, orgId);
		return ResponseEntity.noContent().build();
	}
}
