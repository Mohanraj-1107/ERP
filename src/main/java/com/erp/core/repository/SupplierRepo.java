package com.erp.core.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.erp.core.entity.supplier.Supplier;

@Repository
public interface SupplierRepo extends JpaRepository<Supplier, Long> {

	Optional<Supplier> findByOrg_OrgIdAndSupplierId(Long orgId, Long supplierId);

	Page<Supplier> findAllByOrg_OrgId(Long orgId, Pageable pageable);

	boolean existsByOrg_OrgIdAndSupplierEmail(Long orgId, String supplierEmail);

	List<Supplier> findByOrg_OrgId(Long orgId);
}
