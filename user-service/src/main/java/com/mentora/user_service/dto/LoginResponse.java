package com.mentora.user_service.dto;

import com.mentora.user_service.model.Users;

public record LoginResponse(
        String token,
        String userId,
        String email,
        String fullName
) {
    public static LoginResponse fromEntity(String token, Users users) {
        return new LoginResponse(
                token,
                users.getId(),
                users.getEmail(),
                users.getFullName()
        );
    }
}
