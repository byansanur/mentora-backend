package com.mentora.user_service.dto;

public record UpdateProfileRequest(
        String fullName,
        String profilePictureUrl
) {
}
