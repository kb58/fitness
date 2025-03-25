package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.example.demo.module.Role.ADMIN;
import static com.example.demo.module.Permission.*;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    UserDetailsService userDetailService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth

                        // Public Routes
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/getUser/**").permitAll()

                        // Admin Routes
                        .requestMatchers("/api/v1/admin/**").hasRole(ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/api/v1/admin/**").hasAuthority(ADMIN_READ.getPermission())
                        .requestMatchers(HttpMethod.PUT, "/api/v1/admin/**").hasAuthority(ADMIN_UPDATE.getPermission())
                        .requestMatchers(HttpMethod.POST, "/api/v1/admin/**", "/api/v1/post/createPost").hasAuthority(ADMIN_CREATE.getPermission())
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/admin/**").hasAuthority(ADMIN_DELETE.getPermission())

                        // Program Management - Admin and Trainer
                        .requestMatchers(HttpMethod.POST, "/api/programs/**").hasAnyAuthority(TRAINER_CREATE.getPermission(), ADMIN_CREATE.getPermission())
                        .requestMatchers(HttpMethod.GET, "/api/programs/**").hasAnyAuthority(TRAINER_READ.getPermission(), ADMIN_READ.getPermission(), USER_READ.getPermission())
                        .requestMatchers(HttpMethod.PUT, "/api/programs/**").hasAnyAuthority(TRAINER_UPDATE.getPermission(), ADMIN_UPDATE.getPermission())
                        .requestMatchers(HttpMethod.DELETE, "/api/programs/**").hasAuthority(ADMIN_DELETE.getPermission())

                        // Any other requests must be authenticated
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        provider.setUserDetailsService(userDetailService);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
