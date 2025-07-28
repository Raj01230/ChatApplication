package com.dollop.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.dollop.app.filter.SecurityFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Autowired
	private UserDetailsService userDetailsService;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private AuthenticationEntryPoint authenticationEntryPoint;
	@Autowired
	private SecurityFilter securityFilter;

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService); // This is still how it's done
		provider.setPasswordEncoder(passwordEncoder);
		return provider;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http.cors(Customizer.withDefaults()) // Enable CORS
				.csrf(csrf -> csrf.disable()) // Disable CSRF for APIs
				.authorizeHttpRequests(auth -> auth// Preflight
						.requestMatchers("/auth/**").permitAll() // Public endpoints
						.anyRequest().authenticated() // Everything else
				).exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint) // Unauthorized access
																								// handler
				).sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // No session
				).addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class).build();
	}

//	@Bean
//	CorsConfigurationSource corsConfigurationSource() {
//		CorsConfiguration configuration = new CorsConfiguration();
//		configuration.setAllowedOrigins(List.of("http://localhost:4200")); // Angular origin
//		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
//		configuration.setAllowedHeaders(List.of("*"));
//		configuration.setAllowCredentials(true); // Important if you're using cookies or Authorization headers
//
//		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//		source.registerCorsConfiguration("/", configuration);
//		return source;
//	}
}
