package com.edu.froliak.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/api/v1/messages/**").permitAll()
                                .requestMatchers("/api/v1/chatrooms/**/messages").permitAll()
                                .requestMatchers("/h2-console-message/**").permitAll()
                                .anyRequest().authenticated()
                );
        http.headers(AbstractHttpConfigurer::disable);
        return http.build();
    }
}