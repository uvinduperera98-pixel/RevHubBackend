package com.revHub.security;

import com.revHub.dto.response.SecurityUserPrincipal;
import com.revHub.entity.User;
import com.revHub.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(
            JwtTokenProvider jwtTokenProvider,
            UserRepository userRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");

        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);

            if (!jwtTokenProvider.validateToken(token)) {
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"JWT token expired or invalid\"}");
                return;
            }

            try {
                String username = jwtTokenProvider.getUsernameFromJWT(token);
                User user = userRepository.findByUsername(username).orElse(null);

                // Check current account status and current roles on every authenticated request.
                // This also makes role changes/deactivation take effect without waiting for token expiry.
                if (user == null || !user.isActive()) {
                    SecurityContextHolder.clearContext();
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"Account is unavailable\"}");
                    return;
                }

                SecurityUserPrincipal principal = new SecurityUserPrincipal(
                        user.getUserId(), user.getUsername(), user.getFullName());

                List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                        .map(role -> role.getRoleName().trim().toUpperCase(Locale.ROOT))
                        .map(roleName -> new SimpleGrantedAuthority("ROLE_" + roleName))
                        .toList();

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(principal, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (RuntimeException ex) {
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Unable to authenticate token\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
