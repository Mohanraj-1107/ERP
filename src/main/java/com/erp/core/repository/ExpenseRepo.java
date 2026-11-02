//$Id$
package com.erp.core.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.erp.core.entity.expense.Expense;

@Repository
public interface ExpenseRepo extends JpaRepository<Expense, Long>{
	
	public Page<Expense> findByOrg_OrgId(Long orgId,Pageable pageable);
	public Optional<Expense> findByOrg_OrgIdAndExpenseId(Long ordId,Long expenseId);
	
}
