//$Id$
package com.erp.core.service;

import java.time.Instant;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.erp.core.entity.organization.CreateOrgRequest;
import com.erp.core.entity.organization.GetOrgResponse;
import com.erp.core.entity.organization.Organization;
import com.erp.core.exception.InvalidPathVariableException;
import com.erp.core.exception.RequestBodyNullException;
import com.erp.core.repository.OrganizationRepo;

@Service
public class OrgService {
	
	private OrganizationRepo orgRepo;
	
	private PasswordEncoder passwordEncoder;

	public OrgService(OrganizationRepo orgRepo,PasswordEncoder passwordEncoder) {
		this.orgRepo=orgRepo;
		this.passwordEncoder=passwordEncoder;
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
	
	public void createOrg(CreateOrgRequest orgRequest) throws Exception{
		if(orgRequest==null) {
			throw new RequestBodyNullException("Request body is null for create org request");
		}
		Organization org=new Organization();
		org.setAddress(orgRequest.getAddress());
		org.setCreatedOn(Instant.now());
		org.setMobileNo(orgRequest.getMobileNo());
		org.setOrgEmail(orgRequest.getOrgEmail());
		org.setOrgName(orgRequest.getOrgName());
		org.setOrgPassword(passwordEncoder.encode(orgRequest.getOrgPassword()));
		orgRepo.save(org);
	}
}
