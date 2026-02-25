package com.mentora.user_service.dto;

public record LoginRequest(
        String email,
        String password,
        String deviceId,
        String platform
) {
}
