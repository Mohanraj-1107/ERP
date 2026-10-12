//$Id$
package com.erp.core.service;

import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.erp.core.entity.common.JwtUtils;
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

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class OrgService {
	
	private OrganizationRepo orgRepo;
	
	private PasswordEncoder passwordEncoder;
	
	private HttpServletResponse httpResponse;
	
	private JwtUtils jwtUtils;
	
	@Value("${cookie.accesstoken.expiry}")
	private Integer accessTokenExpiry;
	
	@Value("${cookie.refreshtoken.expiry}")
	private Integer refreshTokenExpiry;

	public OrgService(OrganizationRepo orgRepo,PasswordEncoder passwordEncoder,HttpServletResponse httpResponse,JwtUtils jwtUtils) {
		this.orgRepo=orgRepo;
		this.passwordEncoder=passwordEncoder;
		this.httpResponse=httpResponse;
		this.jwtUtils=jwtUtils;
	}
	
	public GetOrgResponse getOrgDetails(Long orgId)throws Exception{
	   if(orgId==null) {
		   throw new InvalidPathVariableException("OrdId is null");
	   }
	   Organization org=orgRepo.findById(orgId).orElseThrow(()->new InvalidPathVariableException("Org not found"));
	   GetOrgResponse response=new GetOrgResponse.ResponseBuilder()
			   						.setOrgId(org.getOrgId())
			   						.setAddress(org.getAddress())
			   						.setCreatedOn(org.getCreatedOn())
			   						.setOrgEmail(org.getOrgEmail())
			   						.setOrgName(org.getOrgName())
			   						.setMobileNo(org.getMobileNo())
			   						.build();
	   return response;
	}
	
	public Organization createOrg(CreateOrgRequest orgRequest) throws Exception{
		if(orgRequest==null) {
			throw new RequestBodyNullException("Request body is null for org creation request");
		}
		Optional<Organization> available=orgRepo.findByOrgEmail(orgRequest.getOrgEmail());
		if(available.isPresent()){
			throw new DuplicateEntityException("OrgEmail is already registered , Please enter different email");
		}
		Organization org=new Organization();
		org.setAddress(orgRequest.getAddress());
		org.setCreatedOn(Instant.now());
		org.setMobileNo(orgRequest.getMobileNo());
		org.setOrgEmail(orgRequest.getOrgEmail());
		org.setOrgName(orgRequest.getOrgName());
		org.setOrgPassword(passwordEncoder.encode(orgRequest.getOrgPassword()));
		orgRepo.save(org);
		return org;
	}
	
	public LoginResponse login(LoginRequest loginRequest)throws Exception{
		if(loginRequest==null) {
			throw new RequestBodyNullException("Request body is null for login request");
		}
		Organization org =orgRepo.findByOrgEmail(loginRequest.getOrgEmail()).orElseThrow(()->new OrgNotFoundException("No org found with provided email , kindly enter valid email"));
	    LoginResponse response=new LoginResponse();
	    if(!passwordEncoder.matches(loginRequest.getPassword(),org.getOrgPassword())) {
	    	response.setMessage("Login failed : Provided password is incorrect");
	    	response.setOrgId(null);
	    	response.setStatus("failed");
	    	return response;
	    }
	    String accessToken=jwtUtils.generateAccessToken(org.getOrgId()+"");
	    Cookie cookie =new Cookie("access_token",accessToken);
	    cookie.setHttpOnly(true);
	    cookie.setMaxAge(accessTokenExpiry);
	    httpResponse.addCookie(cookie);
    	response.setMessage("Login successfull");
    	response.setOrgId(org.getOrgId());
    	response.setStatus("success");	
	    return response;
	}
}
