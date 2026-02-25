package com.mentora.user_service.dto;

import com.mentora.user_service.model.Users;

public record UserProfileResponse(
        String id,
        String fullName,
        String profilePictureUrl,
        String subscriptionType
) {
    public static UserProfileResponse fromEntity(Users users) {
        return new UserProfileResponse(
                users.getId(), users.getFullName(), users.getProfilePictureUrl(),
                users.getSubscriptionType().name()
        );
    }
}
