//$Id$
package com.erp.core.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.erp.core.entity.organization.LoginRequest;
import com.erp.core.service.OrgService;

@RestController
@RequestMapping("/v1")
public class LoginController {
	
	private OrgService loginService;
	
	public LoginController(OrgService loginService) {
		this.loginService=loginService;
	}
	
	@PostMapping("/login")
	public ResponseEntity<Object> login(@RequestBody LoginRequest loginRequest)throws Exception {
		return ResponseEntity.ok(loginService.login(loginRequest));
	}
}
