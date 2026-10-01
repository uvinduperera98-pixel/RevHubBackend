package com.revHub.security;

import com.revHub.dto.response.SecurityUserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
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

            if (jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.getUsernameFromJWT(token);

                // 1. Extract the extra claims you saved during login
                Long uid = jwtTokenProvider.getUserIdFromJWT(token);
                String fullName = jwtTokenProvider.getFullNameFromJWT(token);

                // 2. Wrap them into your rich principal record
                SecurityUserPrincipal principal = new SecurityUserPrincipal(uid, username, fullName);

                // 3. Pass the 'principal' object instead of the string!
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                principal,
                                null,
                                new ArrayList<>() // Or your user authorities
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } else {
                // JWT expired or invalid
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("JWT token expired or invalid");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
