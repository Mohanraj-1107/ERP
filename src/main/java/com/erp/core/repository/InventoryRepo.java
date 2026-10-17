package com.erp.core.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.erp.core.entity.inventory.Inventory;
import com.erp.core.entity.product.Product;

@Repository
public interface InventoryRepo extends JpaRepository<Inventory, Long> {

	Optional<Inventory> findByOrg_OrgIdAndProduct_ProductId(Long orgId, Long productId);

	Page<Inventory> findAllByOrg_OrgId(Long orgId, Pageable pageable);

	void deleteByProduct(Product product);

	/**
	 * Inventory items whose current quantity has dropped to or below the product's
	 * minimum stock level.
	 */
	@Query("select i from Inventory i where i.org.orgId = :orgId and i.quantity <= i.product.minimumStock")
	List<Inventory> findLowStock(@Param("orgId") Long orgId);
}
