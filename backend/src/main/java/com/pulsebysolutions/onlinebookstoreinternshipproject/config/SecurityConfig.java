package com.pulsebysolutions.onlinebookstoreinternshipproject.config;

import com.pulsebysolutions.onlinebookstoreinternshipproject.security.JWTFilter;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JWTFilter jwtAuthFilter
    ) throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable)

                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfiguration =
                            new org.springframework.web.cors.CorsConfiguration();

                    corsConfiguration.setAllowedOriginPatterns(
                            java.util.List.of("*")
                    );

                    corsConfiguration.setAllowedMethods(
                            java.util.List.of(
                                    "GET",
                                    "POST",
                                    "PUT",
                                    "DELETE",
                                    "OPTIONS",
                                    "PATCH"
                            )
                    );

                    corsConfiguration.addAllowedHeader("*");
                    corsConfiguration.setAllowCredentials(true);
                    corsConfiguration.setMaxAge(3600L);

                    return corsConfiguration;
                }))

                .exceptionHandling(exception -> exception

                        // 401 Unauthorized
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(401);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.getWriter().write(
                                    "{\"status\":401,\"message\":\"Unauthorized\"}"
                            );
                        })

                        // 403 Forbidden
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(403);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.getWriter().write(
                                    "{\"status\":403,\"message\":\"Forbidden\"}"
                            );
                        })
                )

                .authorizeHttpRequests(auth -> auth

                        // Error handling
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers("/error").permitAll()

                        // Authentication
                        .requestMatchers("/api/auth/**").permitAll()

                        // Books - USER + ADMIN
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/books/**"
                        ).hasAnyRole("USER", "ADMIN")

                        // Books - ADMIN only
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/books/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/books/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/books/**"
                        ).hasRole("ADMIN")

                        // Users
                        .requestMatchers(
                                "/api/users/**"
                        ).hasAnyRole("USER", "ADMIN")

                        // Images
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/images/**"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/images/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/images/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/images/**"
                        ).hasRole("ADMIN")

                        // Admin Management
                        .requestMatchers(
                                "/api/admins/**"
                        ).hasRole("ADMIN")

                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .build();
    }
}