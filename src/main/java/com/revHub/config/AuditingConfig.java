package com.revHub.config;

import com.revHub.dto.response.SecurityUserPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
public class AuditingConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // If no user is logged in, return empty (or a system ID like 0L if you prefer)
            if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
                return Optional.empty();
            }

            Object principal = authentication.getPrincipal();

            // Check for our custom SecurityUserPrincipal record and grab ONLY the userId
            if (principal instanceof SecurityUserPrincipal userPrincipal) {
                return Optional.of(userPrincipal.username());
            }

            return Optional.empty();
        };
    }
}
