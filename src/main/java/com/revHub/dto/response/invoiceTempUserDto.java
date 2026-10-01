package com.revHub.dto.response;

public record invoiceTempUserDto(
        Long userId,
        String username,
        String fullName
) {}
