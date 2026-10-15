package com.erp.core.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.erp.core.entity.product.Product;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {

	Optional<Product> findByOrg_OrgIdAndProductId(Long orgId, Long productId);

	Page<Product> findAllByOrg_OrgId(Long orgId, Pageable pageable);

	Optional<Product> findByOrg_OrgIdAndProductCode(Long orgId, String productCode);

	boolean existsByOrg_OrgIdAndProductCode(Long orgId, String productCode);

	boolean existsByOrg_OrgIdAndProductName(Long orgId, String productName);
}
