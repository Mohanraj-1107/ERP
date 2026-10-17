package com.erp.core.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.erp.core.entity.customer.Customer;

@Repository
public interface CustomerRepo extends JpaRepository<Customer, Long> {

	Optional<Customer> findByOrg_OrgIdAndCustomerId(Long orgId, Long customerId);

	Page<Customer> findAllByOrg_OrgId(Long orgId, Pageable pageable);

	boolean existsByOrg_OrgIdAndCustomerEmail(Long orgId, String customerEmail);

	List<Customer> findByOrg_OrgId(Long orgId);
}
