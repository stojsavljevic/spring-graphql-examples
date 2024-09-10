package com.alex.graphql.core.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.graphql.server.webmvc.AuthenticationWebSocketInterceptor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.alex.graphql.core.authentication.WsAuthenticationTokenExtractor;


@SuppressWarnings("deprecation")
@Profile("!no-security")
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.authorizeHttpRequests(
				(requests) -> requests
						.requestMatchers(new AntPathRequestMatcher("/manifest.json"))
						.permitAll()
						.anyRequest()
						.authenticated())
				.formLogin(withDefaults())
				.cors(cors -> cors.disable())
				.csrf(csrf -> csrf.disable());

		return http.build();
	}

    UserDetailsService userDetailsService() {
        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder().encode("admin"))
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(admin);
    }

	PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
	
	@Bean
	AuthenticationManager authenticationManager(){
	    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
	    authProvider.setUserDetailsService(userDetailsService());
	    authProvider.setPasswordEncoder(passwordEncoder());
	    return new ProviderManager(authProvider);
	}
	
	@Bean
	AuthenticationWebSocketInterceptor getAuthenticationWebSocketInterceptor(
			AuthenticationManager authenticationManager) {
		return new AuthenticationWebSocketInterceptor(new WsAuthenticationTokenExtractor(), authenticationManager);
	}
	
}
