package com.mentora.content_service.model.dto;

import com.mentora.content_service.model.Post;
import com.mentora.content_service.model.UserCache;

import java.time.LocalDateTime;

public record PostDetailResponse(
        String id,
        String userId,
        String authorName,
        String authorAvatar,
        Long categoryId,
        String content,
        String mediaUrl,
        String mediaType,
        long totalLikes,     // Data tambahan
        long totalComments,
        boolean isLikedByMe, // Data tambahan (konteks user yg melihat)
        LocalDateTime createdAt
) {
    // Constructor Mapper manual (atau bisa pakai library MapStruct)
    public static PostDetailResponse fromEntity(Post post, UserCache userCache, long likes, long totalComments, boolean isLiked) {
        return new PostDetailResponse(
                post.getId(),
                post.getUserId(),
                userCache != null ? userCache.getFullName() : "Unknown User",
                userCache != null ? userCache.getProfilePictureUrl() : null,
                post.getCategory().getId(),
                post.getContent(),
                post.getMediaUrl(),
                post.getMediaType().name(),
                likes,
                totalComments,
                isLiked,
                post.getCreatedAt()
        );
    }
}
