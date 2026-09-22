package com.example.demo.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtRoleConverter jwtRoleConverter;

    public SecurityConfig(JwtRoleConverter jwtRoleConverter) {
        this.jwtRoleConverter = jwtRoleConverter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // REST API: do not create or use HTTP sessions.
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // CSRF is generally unnecessary for stateless Bearer-token APIs.
                .csrf(csrf -> csrf.disable())

                // CORS configuration.
                .cors(cors ->
                        cors.configurationSource(corsConfigurationSource())
                )

                // Security headers.
                .headers(headers ->
                        headers
                                .frameOptions(frame -> frame.deny())
                                .contentSecurityPolicy(csp ->
                                        csp.policyDirectives("default-src 'none'")
                                )
                )

                // Authorization rules.
                .authorizeHttpRequests(auth -> auth

                        // Public endpoint.
                        .requestMatchers("/actuator/health")
                        .permitAll()

                        // USER + ADMIN can read records.
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/records/**"
                        )
                        .hasAnyRole("USER", "ADMIN")

                        // USER + ADMIN can read modules.
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/modules/**"
                        )
                        .hasAnyRole("USER", "ADMIN")

                        // ADMIN only - records.
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/records/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/records/**"
                        )
                        .hasRole("ADMIN")

//                        .requestMatchers(
//                                HttpMethod.DELETE,
//                                "/api/v1/records/**"
//                        )
//                        .hasRole("ADMIN")

                        // ADMIN only - modules.
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/modules/**"
                        )
                        .hasRole("ADMIN")

//                        .requestMatchers(
//                                HttpMethod.PUT,
//                                "/api/v1/modules/**"
//                        )
//                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/v1/modules/**"
                        )
                        .hasRole("ADMIN")

                        // Everything else requires authentication.
                        .anyRequest()
                        .authenticated()
                )

                // JWT Resource Server.
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(jwtRoleConverter)
                        )
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
                "https://your-frontend-domain.com",
                "http://localhost:3000",
                "http://localhost:5173"
        ));

        configuration.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"
        ));

        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type"
        ));

        // Using Authorization: Bearer <token>, not cookies.
        configuration.setAllowCredentials(false);

        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}