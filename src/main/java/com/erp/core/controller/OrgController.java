//$Id$
package com.erp.core.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.erp.core.entity.organization.CreateOrgRequest;
import com.erp.core.entity.organization.GetOrgResponse;
import com.erp.core.entity.organization.Organization;
import com.erp.core.service.OrgService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/org")
public class OrgController {
	
	private OrgService orgService;
	
	public OrgController(OrgService orgService) {
		this.orgService=orgService;
	}
	
	@GetMapping("/{orgId}")
	public ResponseEntity<GetOrgResponse> getOrgDetails(@PathVariable Long orgId)throws Exception{
	   return ResponseEntity.ok(orgService.getOrgDetails(orgId));
	}
	
	@PostMapping("")
	public ResponseEntity<Organization> createOrg(@Valid @RequestBody CreateOrgRequest createOrgRequest)throws Exception{
		return ResponseEntity.ok(orgService.createOrg(createOrgRequest));
	}
}