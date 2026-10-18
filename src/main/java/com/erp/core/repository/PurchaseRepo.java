package com.erp.core.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.erp.core.entity.purchase.Purchase;

@Repository
public interface PurchaseRepo extends JpaRepository<Purchase, Long> {

	Optional<Purchase> findByOrg_OrgIdAndPurchaseId(Long orgId, Long purchaseId);

	Page<Purchase> findAllByOrg_OrgId(Long orgId, Pageable pageable);

	List<Purchase> findByOrg_OrgId(Long orgId);
}
