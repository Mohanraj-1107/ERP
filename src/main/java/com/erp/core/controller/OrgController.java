//$Id$
package com.erp.core.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.erp.core.entity.organization.GetOrgResponse;
import com.erp.core.service.OrgService;

@RestController
@RequestMapping("/v1/org")
public class OrgController {
	
	private OrgService orgService;
	
	public OrgController(OrgService orgService) {
		this.orgService=orgService;
	}
	
	@GetMapping("/{orgId}")
	public ResponseEntity<GetOrgResponse> getOrgDetails(Long orgId)throws Exception{
	   return ResponseEntity.ok(orgService.getOrgDetails(orgId));
	}
}
