//$Id$
package com.erp.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.erp.core.entity.organization.Organization;

@Repository
public interface OrganizationRepo extends JpaRepository<Organization,Long>{
	
}
