package com.dollop.app.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.dollop.app.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {
	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private ObjectMapper objectMapper; // Autowire ObjectMapper for JSON responses

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		System.err.println("token applied on: " + request.getHeader("Authorization"));
		String token = request.getHeader("Authorization");
		System.err.println("SecurityFilter applied on: " + request.getRequestURI());

		try {
			if (token != null && token.startsWith("Bearer ")) {
				token = token.substring(7);

//				String tokenType = (String) jwtUtil.getHeader(token, "tokenType");
//				if ("ACCESS_TOKEN".equals(tokenType)) {
				String username = jwtUtil.getUsername(token);

				if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
					UserDetails user = userDetailsService.loadUserByUsername(username);
					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
							username, user.getPassword(), user.getAuthorities());
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			}
			filterChain.doFilter(request, response);
		} catch (ExpiredJwtException e) {
			sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token expired!!");
		} catch (MalformedJwtException | SignatureException e) {
			sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
		} catch (Exception e) {
			e.printStackTrace();
			sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
		}
	}

	private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
		response.setStatus(status);
		response.setContentType("application/json");

		Map<String, Object> errorResponse = new HashMap<>();
		errorResponse.put("status", status);
		errorResponse.put("error", message);

		response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
		response.getWriter().flush();
	}
}
