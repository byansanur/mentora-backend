package com.mentora.content_service.model.dto;

public record PostLikeRequest(
        String postId,
        String userId
) {
}
