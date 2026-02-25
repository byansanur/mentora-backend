package com.mentora.content_service.controller;

import com.mentora.common.dto.ApiResponse;
import com.mentora.content_service.model.PostComment;
import com.mentora.content_service.model.dto.CommentRequest;
import com.mentora.content_service.model.dto.CommentResponse;
import com.mentora.content_service.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostCommentController {

    private final CommentService commentService;

    @PostMapping("/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> addComment(
            @Valid @RequestBody CommentRequest request,
            @RequestHeader("X-User-Id") String userId
    ) {
        CommentResponse newComment = commentService.addComment(request, userId);
        return ResponseEntity.ok(ApiResponse.success("Komentar berhasil ditambahkan", newComment));
    }

    @GetMapping("/comments/{postId}")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getComments(
            @PathVariable String postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<CommentResponse> comments = commentService.getCommentsByPostId(postId, page, size);
        return ResponseEntity.ok(ApiResponse.successPage("Daftar komentar dimuat", comments));
    }

    @GetMapping("/comments/count/{postId}")
    public ResponseEntity<ApiResponse<Long>> getCommentCount(
            @PathVariable String postId
    ) {
        return ResponseEntity.ok(ApiResponse.success("Success", commentService.getCountByPostId(postId)));
    }
}
