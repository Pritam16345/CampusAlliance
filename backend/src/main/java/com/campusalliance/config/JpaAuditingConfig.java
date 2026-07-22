package com.campusalliance.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Enables Spring Data JPA Auditing and tells it how to find the current user.
 *
 * The AuditorAware bean is called every time JPA persists or updates an entity
 * that extends Auditable. It reads the logged-in user's email from the Spring
 * Security context and stores it in createdBy / updatedBy.
 *
 * For operations that happen outside a user request (e.g., app startup,
 * scheduled jobs), there's no authenticated user, so we fall back to "system".
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth == null || !auth.isAuthenticated()
                    || auth instanceof AnonymousAuthenticationToken) {
                return Optional.of("system");
            }

            // auth.getName() returns the email — that's what we set as the
            // principal in JwtAuthenticationFilter
            return Optional.of(auth.getName());
        };
    }
}
