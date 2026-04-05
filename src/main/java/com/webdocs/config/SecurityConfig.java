package com.webdocs.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // ── HTTPS enforcement ──────────────────────────────────────────
            .requiresChannel(channel -> channel
                .anyRequest().requiresSecure()
            )

            // ── Security headers ───────────────────────────────────────────
            .headers(headers -> headers
                .httpStrictTransportSecurity(hsts -> hsts
                    .includeSubDomains(true)
                    .maxAgeInSeconds(31536000)
                    .preload(true)
                )
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives(
                        "default-src 'self'; " +
                        "script-src 'self' 'unsafe-inline' https://cdnjs.cloudflare.com https://cdn.jsdelivr.net; " +
                        "style-src 'self' 'unsafe-inline' https://cdnjs.cloudflare.com https://fonts.googleapis.com; " +
                        "font-src 'self' https://fonts.gstatic.com https://cdnjs.cloudflare.com; " +
                        "img-src 'self' data: https:; " +
                        "connect-src 'self' https://api.mynger.com https://api.deepdiary.mynger.com; " +
                        "frame-src 'self'; " +
                        "object-src 'none';"
                    )
                )
                .referrerPolicy(referrer -> referrer
                    .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                )
                .frameOptions(frame -> frame.sameOrigin())
            )

            // ── CSRF — enable for UI forms, disable for REST API paths ─────
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(
                    new AntPathRequestMatcher("/api/**"),
                    new AntPathRequestMatcher("/v3/api-docs/**"),
                    new AntPathRequestMatcher("/swagger-ui/**")
                )
            )

            // ── Authorization rules ────────────────────────────────────────
            // We use our own session-based AuthInterceptor for UI auth,
            // so Spring Security just allows all requests through (the interceptor
            // handles redirects to /login). API endpoints are open (JWT from Mynger).
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )

            // ── Disable Spring Security's own login form ───────────────────
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .logout(logout -> logout.disable());

        return http.build();
    }
}
