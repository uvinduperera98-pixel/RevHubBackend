package com.revHub.dto.response;

public record SecurityUserPrincipal(
        Long userId,
        String username,
        String fullName
) {}
