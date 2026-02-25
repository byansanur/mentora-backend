package com.mentora.user_service.dto;

public record ChangePasswordRequest(
        String oldPassword,
        String newPassword,
        String confirmNewPassword
) {
}
