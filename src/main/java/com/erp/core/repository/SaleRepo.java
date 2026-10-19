package com.erp.core.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.erp.core.entity.sale.Sale;

@Repository
public interface SaleRepo extends JpaRepository<Sale, Long> {

	Optional<Sale> findByOrg_OrgIdAndSaleId(Long orgId, Long saleId);

	Page<Sale> findAllByOrg_OrgId(Long orgId, Pageable pageable);

	List<Sale> findByOrg_OrgId(Long orgId);
}
