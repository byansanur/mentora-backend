package com.mentora.content_service.model.dto;

import com.mentora.content_service.model.Category;
import com.mentora.content_service.model.Post;
import com.mentora.content_service.model.UserCache;

import java.time.LocalDateTime;

public record PostFeedResponse(
        String id,
        String userId,
        String authorName,
        String authorAvatar,
        Category category,
        String content,
        String mediaUrl,
        String mediaType,
        LocalDateTime createdAt,
        long totalLikes,
        long totalComments,
        boolean isLikedByMe
) {
    public static PostFeedResponse fromEntity(Post post, UserCache userCache, long totalLikes, long totalComments, boolean isLikedByMe) {
        return new PostFeedResponse(
                post.getId(),
                post.getUserId(),
                userCache != null ? userCache.getFullName() : "Unknown User",
                userCache != null ? userCache.getProfilePictureUrl() : null,
                post.getCategory(),
                post.getContent(),
                post.getMediaUrl(),
                post.getMediaType().name(),
                post.getCreatedAt(),
                totalLikes,
                totalComments,
                isLikedByMe
        );
    }
}