package com.mentora.content_service.model.dto;

import jakarta.validation.constraints.NotBlank;

public record CommentRequest(
        @NotBlank(message = "Post ID tidak boleh kosong")
        String postId,
        @NotBlank(message = "Komentar tidak boleh kosong")
        String content,
        Long parentId
) {
}
