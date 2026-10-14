package com.erp.core.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.erp.core.entity.common.RefreshToken;

@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {

	Optional<RefreshToken> findByTokenHash(String tokenHash);

	List<RefreshToken> findAllByFamilyId(String familyId);

	List<RefreshToken> findAllByOrg_OrgId(Long orgId);
}
