package com.erp.core.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
// no DigestUtils

import com.erp.core.entity.common.RefreshToken;
import com.erp.core.entity.organization.Organization;
import com.erp.core.exception.InvalidRefreshTokenException;
import com.erp.core.exception.RefreshTokenReuseException;
import com.erp.core.repository.RefreshTokenRepo;

@Service
public class RefreshTokenService {

	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	private final RefreshTokenRepo refreshTokenRepo;

	@Value("${app.refresh-token.bytes:64}")
	private int refreshTokenBytes;

	@Value("${app.refresh-token.expiry:604800000}")
	private long refreshTokenExpiryMs;

	public RefreshTokenService(RefreshTokenRepo refreshTokenRepo) {
		this.refreshTokenRepo = refreshTokenRepo;
	}

	public String generateRawToken() {
		byte[] bytes = new byte[refreshTokenBytes];
		SECURE_RANDOM.nextBytes(bytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}

	public String hash(String rawToken) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] bytes = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
			StringBuilder sb = new StringBuilder(bytes.length * 2);
			for (byte b : bytes) {
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		} catch (Exception e) {
			throw new IllegalStateException("Unable to hash refresh token", e);
		}
	}

	public RefreshToken issue(Organization org, String rawToken, String familyId) {
		RefreshToken rt = new RefreshToken();
		rt.setOrg(org);
		rt.setTokenHash(hash(rawToken));
		rt.setFamilyId(familyId == null ? UUID.randomUUID().toString() : familyId);
		rt.setExpiresAt(Instant.now().plusMillis(refreshTokenExpiryMs));
		return refreshTokenRepo.save(rt);
	}

	public RefreshToken validateAndGet(String rawToken) {
		if (rawToken == null || rawToken.isBlank()) {
			throw new InvalidRefreshTokenException("Refresh token is missing");
		}
		RefreshToken stored = refreshTokenRepo.findByTokenHash(hash(rawToken))
				.orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));

		if (stored.getRevokedAt() != null) {
			// Reuse detected: revoke the whole family.
			revokeFamily(stored.getFamilyId());
			throw new RefreshTokenReuseException("Refresh token reuse detected");
		}
		if (stored.getExpiresAt().isBefore(Instant.now())) {
			throw new InvalidRefreshTokenException("Refresh token expired");
		}
		return stored;
	}

	public RefreshToken rotate(RefreshToken oldToken, Organization org, String newRawToken) {
		oldToken.setRevokedAt(Instant.now());
		oldToken.setReplacedByHash(hash(newRawToken));
		refreshTokenRepo.save(oldToken);
		return issue(org, newRawToken, oldToken.getFamilyId());
	}

	public void revoke(String rawToken) {
		if (rawToken == null || rawToken.isBlank()) return;
		refreshTokenRepo.findByTokenHash(hash(rawToken)).ifPresent(rt -> {
			if (rt.getRevokedAt() == null) {
				rt.setRevokedAt(Instant.now());
				refreshTokenRepo.save(rt);
			}
		});
	}

	public void revokeAll(Long orgId) {
		List<RefreshToken> tokens = refreshTokenRepo.findAllByOrg_OrgId(orgId);
		Instant now = Instant.now();
		for (RefreshToken rt : tokens) {
			if (rt.getRevokedAt() == null) {
				rt.setRevokedAt(now);
				refreshTokenRepo.save(rt);
			}
		}
	}

	private void revokeFamily(String familyId) {
		List<RefreshToken> tokens = refreshTokenRepo.findAllByFamilyId(familyId);
		Instant now = Instant.now();
		for (RefreshToken rt : tokens) {
			if (rt.getRevokedAt() == null) {
				rt.setRevokedAt(now);
				refreshTokenRepo.save(rt);
			}
		}
	}
}
