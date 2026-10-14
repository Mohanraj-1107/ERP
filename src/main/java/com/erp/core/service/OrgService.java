//$Id$
package com.erp.core.service;

import java.time.Instant;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.erp.core.entity.common.AuthResult;
import com.erp.core.common.JwtUtils;
import com.erp.core.entity.organization.CreateOrgRequest;
import com.erp.core.entity.organization.GetOrgResponse;
import com.erp.core.entity.organization.LoginRequest;
import com.erp.core.entity.organization.LoginResponse;
import com.erp.core.entity.organization.Organization;
import com.erp.core.exception.DuplicateEntityException;
import com.erp.core.exception.InvalidPathVariableException;
import com.erp.core.exception.OrgNotFoundException;
import com.erp.core.exception.RequestBodyNullException;
import com.erp.core.repository.OrganizationRepo;

@Service
public class OrgService {

	private final OrganizationRepo orgRepo;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtils jwtUtils;
	private final RefreshTokenService refreshTokenService;

	@Value("${app.jwt.expiration}")
	private Integer accessTokenExpiry;

	@Value("${cookie.accesstoken.expiry}")
	private Integer accessTokenCookieExpiry;

	@Value("${cookie.refreshtoken.expiry}")
	private Integer refreshTokenCookieExpiry;

	public OrgService(OrganizationRepo orgRepo, PasswordEncoder passwordEncoder, JwtUtils jwtUtils,
			RefreshTokenService refreshTokenService) {
		this.orgRepo = orgRepo;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtils = jwtUtils;
		this.refreshTokenService = refreshTokenService;
	}

	public GetOrgResponse getOrgDetails(Long orgId) throws Exception {
		if (orgId == null) {
			throw new InvalidPathVariableException("OrdId is null");
		}
		Organization org = orgRepo.findById(orgId)
				.orElseThrow(() -> new InvalidPathVariableException("Org not found"));
		GetOrgResponse response = new GetOrgResponse.ResponseBuilder().setOrgId(org.getOrgId())
				.setAddress(org.getAddress()).setCreatedOn(org.getCreatedOn()).setOrgEmail(org.getOrgEmail())
				.setOrgName(org.getOrgName()).setMobileNo(org.getMobileNo()).build();
		return response;
	}

	public Organization createOrg(CreateOrgRequest orgRequest) throws Exception {
		if (orgRequest == null) {
			throw new RequestBodyNullException("Request body is null for org creation request");
		}
		Optional<Organization> available = orgRepo.findByOrgEmail(orgRequest.getOrgEmail());
		if (available.isPresent()) {
			throw new DuplicateEntityException("OrgEmail is already registered , Please enter different email");
		}
		Organization org = new Organization();
		org.setAddress(orgRequest.getAddress());
		org.setCreatedOn(Instant.now());
		org.setMobileNo(orgRequest.getMobileNo());
		org.setOrgEmail(orgRequest.getOrgEmail());
		org.setOrgName(orgRequest.getOrgName());
		org.setOrgPassword(passwordEncoder.encode(orgRequest.getOrgPassword()));
		orgRepo.save(org);
		return org;
	}

	public AuthResult login(LoginRequest loginRequest) throws Exception {
		if (loginRequest == null) {
			throw new RequestBodyNullException("Request body is null for login request");
		}
		Organization org = orgRepo.findByOrgEmail(loginRequest.getOrgEmail())
				.orElseThrow(() -> new OrgNotFoundException("No org found with provided email , kindly enter valid email"));
		LoginResponse response = new LoginResponse();
		if (!passwordEncoder.matches(loginRequest.getPassword(), org.getOrgPassword())) {
			response.setMessage("Login failed : Provided password is incorrect");
			response.setOrgId(null);
			response.setStatus("failed");
			return new AuthResult(response, null, null);
		}

		String accessToken = jwtUtils.generateAccessToken(org.getOrgId() + "");
		String refreshToken = refreshTokenService.generateRawToken();
		refreshTokenService.issue(org, refreshToken, null);

		response.setMessage("Login successfull");
		response.setOrgId(org.getOrgId());
		response.setStatus("success");
		return new AuthResult(response, accessToken, refreshToken);
	}

	public AuthResult refresh(String rawRefreshToken) throws Exception {
		com.erp.core.entity.common.RefreshToken storedToken = refreshTokenService.validateAndGet(rawRefreshToken);
		Organization org = storedToken.getOrg();
		String newRefreshToken = refreshTokenService.generateRawToken();
		refreshTokenService.rotate(storedToken, org, newRefreshToken);

		String accessToken = jwtUtils.generateAccessToken(org.getOrgId() + "");
		LoginResponse response = new LoginResponse();
		response.setMessage("Refresh successfull");
		response.setOrgId(org.getOrgId());
		response.setStatus("success");
		return new AuthResult(response, accessToken, newRefreshToken);
	}

	public void logout(String rawRefreshToken) {
		refreshTokenService.revoke(rawRefreshToken);
	}

	public void logoutAll(Long orgId) throws Exception {
		if (orgId == null) {
			throw new InvalidPathVariableException("OrgId is null");
		}
		refreshTokenService.revokeAll(orgId);
	}
}
