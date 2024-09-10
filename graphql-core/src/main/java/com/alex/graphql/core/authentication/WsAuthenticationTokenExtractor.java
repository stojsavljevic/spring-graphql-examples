package com.alex.graphql.core.authentication;

import java.util.Map;

import org.springframework.graphql.server.support.AuthenticationExtractor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import reactor.core.publisher.Mono;

public class WsAuthenticationTokenExtractor implements AuthenticationExtractor {

	@Override
	public Mono<Authentication> getAuthentication(Map<String, Object> payload) {

		String authToken = (String) payload.get("Authorization");
		if (authToken != null) {
			System.out.println("Authentication token found: " + authToken);
			
			String[] credentials = authToken.split(":", 2);
			if (credentials.length == 2) {
				UsernamePasswordAuthenticationToken userPassAuthToken = new UsernamePasswordAuthenticationToken(
						credentials[0], credentials[1]);
				return Mono.just(userPassAuthToken);
			}
		}

		return Mono.empty();
	}
}
