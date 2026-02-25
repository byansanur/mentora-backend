package com.mentora.content_service.controller;

import com.mentora.common.dto.ApiResponse;
import com.mentora.content_service.model.Post;
import com.mentora.content_service.model.dto.PostCreateRequest;
import com.mentora.content_service.model.dto.PostDetailResponse;
import com.mentora.content_service.model.dto.PostFeedResponse;
import com.mentora.content_service.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<ApiResponse<PostFeedResponse>> createPost(
            @Valid @RequestBody PostCreateRequest request,
            @RequestHeader("X-User-Id") String userId
    ) {
        PostFeedResponse createdPost = postService.createPost(request, userId);
        return ResponseEntity.ok(ApiResponse.success("Postingan berhasil dibuat", createdPost));
    }

    @GetMapping("/feed")
    public ResponseEntity<ApiResponse<List<PostFeedResponse>>> getFeedByUserInterests(
            @RequestParam List<Long> categoryIds,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader(value = "X-User-Id", required = false, defaultValue = "guest") String viewerId
    ) {
        Page<PostFeedResponse> response = postService.getFeed(categoryIds, page, size, viewerId);
        return ResponseEntity.ok(ApiResponse.successPage("Success", response));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDetailResponse>> getPostDetail(
            @PathVariable String postId,
            @RequestHeader(value = "X-User-Id", required = false, defaultValue = "guest") String viewerId
    ) {
        PostDetailResponse detail = postService.getPostDetail(postId, viewerId);
        return ResponseEntity.ok(ApiResponse.success("Detail post ditemukan", detail));
    }
}
