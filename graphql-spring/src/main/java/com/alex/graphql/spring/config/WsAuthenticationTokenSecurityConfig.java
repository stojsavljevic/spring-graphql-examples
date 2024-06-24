package com.alex.graphql.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.graphql.server.webmvc.AuthenticationWebSocketInterceptor;
import org.springframework.security.authentication.AuthenticationManager;

import com.alex.graphql.spring.authentication.WsAuthenticationTokenExtractor;

@Configuration
@Profile("!no-security")
public class WsAuthenticationTokenSecurityConfig {

	/**
	 * This bean enables WebSocket authentication using Bearer token in initial
	 * message when establishing connection.
	 * 
	 * @param authenticationManager
	 * @return
	 */
	@Bean
	AuthenticationWebSocketInterceptor getAuthenticationWebSocketInterceptor(
			AuthenticationManager authenticationManager) {
		return new AuthenticationWebSocketInterceptor(new WsAuthenticationTokenExtractor(), authenticationManager);
	}
}
