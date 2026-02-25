package com.mentora.content_service.controller;

import com.mentora.common.dto.ApiResponse;
import com.mentora.content_service.model.dto.PostLikeRequest;
import com.mentora.content_service.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostInteractionController {

    private final PostLikeService postLikeService;

    @PostMapping("/like/{postId}")
    public ResponseEntity<ApiResponse<String>> toggleLike(
            @PathVariable String postId,
            @RequestHeader("X-User-Id") String userId
    ) {
        if (!postLikeService.isPostExistById(postId)) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("Postingan tidak ditemukan."));
        }

        String resultMessage = postLikeService.toggleLike(postId, userId);

        return ResponseEntity.ok(ApiResponse.success(resultMessage, null));
    }

    @GetMapping("/like/count/{postId}")
    public ResponseEntity<ApiResponse<Long>> getLikeCount(@PathVariable String postId) {
        var likesCount = postLikeService.likesCount(postId);
        return ResponseEntity.ok(ApiResponse.success("success", likesCount));
    }
}
