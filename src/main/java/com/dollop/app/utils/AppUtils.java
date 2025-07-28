package com.dollop.app.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class AppUtils {
	@Autowired
	private JwtUtil util;
	@Autowired
	private HttpServletRequest servletRequest;

	public String getTokenFromHeader() {
		String header = this.servletRequest.getHeader("Authorization");
		if (header != null && header.startsWith("Bearer ")) {
			header = header.substring(7);
		}

		return header;
	}

	public String getIdByToken() {
		String token = this.getTokenFromHeader();
		return util.getHeader(token, "id").toString();
	}

//	public String getRoleByToken() {
//		String token = this.getTokenFromHeader();
//		return util.getHeader(token, "role").toString();
//	}

//	public String getTokenTypeByToken() {
//		String token = this.getTokenFromHeader();
//		return util.getHeader(token, "tokenType").toString();
//	}

//	public String getOtpByToken() {
//		String token = this.getTokenFromHeader();
//		return util.getHeader(token, "otp").toString();
//	}
}
