package com.mentora.content_service.model.dto;

import com.mentora.content_service.model.PostComment;
import com.mentora.content_service.model.UserCache;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record CommentResponse(
        Long id,
        String postId,
        String userId,
        String authorName,
        String authorAvatar,
        String content,
        LocalDateTime createdAt,
        List<CommentResponse> replies
) {
    public static CommentResponse fromEntity(PostComment comment, UserCache userCache) {
        return new CommentResponse(
                comment.getId(),
                comment.getPostId(),
                comment.getUserId(),
                userCache != null ? userCache.getFullName() : "Unknown User",
                userCache != null ? userCache.getProfilePictureUrl() : null,
                comment.getContent(),
                comment.getCreatedAt(),
                new ArrayList<>() // PENTING: Gunakan ArrayList agar bisa ditambah isinya nanti
        );
    }
}
