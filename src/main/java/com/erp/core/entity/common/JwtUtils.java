//$Id$
package com.erp.core.entity.common;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtils {
	@Value("${jwt.secret}")
	private String jwtSecret;
	
	@Value("${jwt.expiration}")
	private int jwtExpirationMs;
	
    private SecretKey key;
    
    private static final Logger LOGGER=LoggerFactory.getLogger(JwtUtils.class);
	
	@PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
	
	public String generateToken(String orgName) {
		
		return Jwts.builder()
				.setSubject(orgName)
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(key,SignatureAlgorithm.HS256)
				.compact();		
		
	}

	public String getOrgNameFromToken(String token) {
	        return Jwts.parserBuilder()
	                .setSigningKey(key).build()
	                .parseClaimsJws(token)
	                .getBody()
	                .getSubject();
	}
	
	public void  validateJwtToken(String token) throws Exception{
        	Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }
}
