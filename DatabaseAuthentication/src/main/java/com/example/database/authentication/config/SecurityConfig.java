package com.example.database.authentication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    //h2 related config not required for other dbs
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((requests) -> {
            // Allow H2 console access
            requests.requestMatchers("/h2-console/**").permitAll();
            requests.anyRequest().authenticated();
        });
        // Disable CSRF for H2 console
        http.csrf(csrf->csrf.ignoringRequestMatchers("/h2-console/**"));
        // Allow frames from same origin for h2 console
        http.headers(headers->headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
        http.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    UserDetailsService userDetailsService(){

        UserDetails user1 = User.withUsername("user")
                .password("{noop}user@123")  // {noop} means plain text (no encoding) only for testing
                .roles("USER")
                .build();

        UserDetails admin = User.withUsername("admin")
                .password("{noop}admin@123")
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user1,admin);
    }
}


