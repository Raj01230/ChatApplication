package com.dollop.app.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

@Component
public class JwtUtil {
	@Value("${app.secret}")
	private String secret;

	public String getUsername(String token) {
		return getClaims(token).getSubject();
	}

	public String generateToken(String subject) {
		Map<String, Object> m = new HashMap<String, Object>();
		return generateToken(m, subject);
	}

	private Claims getClaims(String token) {
		return Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token).getBody();
	}

	private String generateToken(Map<String, Object> claims, String subject) {
//		System.err.println(new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(9)));
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(
//						? new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5))
						new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(9)))
				.setIssuer("SpringChatApplication").signWith(SignatureAlgorithm.HS256, secret.getBytes()).compact();
	}

	public Object getHeader(String token, String key) {
//		System.err.println(token);
		return this.getClaims(token).get(key);
	}

	public String generateToken(String subject, String id) {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("id", id);
		return generateToken(m, subject);
	}

	public Map<String, Object> extractClaimsEvenIfExpired(String token) {

		Claims body = Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token).getBody();

		try {
			// This will throw ExpiredJwtException if token is expired
			return body;
		} catch (io.jsonwebtoken.ExpiredJwtException e) {
			// Token expired, but you can still get claims from it
			return e.getClaims();
		} catch (SignatureException e) {
			// Signature invalid
			System.out.println("Invalid token signature");
		} catch (Exception e) {
			System.out.println("Some other error: " + e.getMessage());
		}

		return null;
	}
}
