package com.mentora.content_service.service;

import com.mentora.content_service.model.PostComment;
import com.mentora.content_service.model.UserCache;
import com.mentora.content_service.model.dto.CommentRequest;
import com.mentora.content_service.model.dto.CommentResponse;
import com.mentora.content_service.repository.CommentRepository;
import com.mentora.content_service.repository.PostRepository;
import com.mentora.content_service.repository.UserCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserCacheRepository userCacheRepository;

    @Transactional
    public CommentResponse addComment(CommentRequest request, String userId) {
        // Validasi apakah postingannya ada?
        if (!postRepository.existsById(request.postId())) {
            throw new RuntimeException("Postingan tidak ditemukan");
        }

        PostComment comment = new PostComment();
        comment.setPostId(request.postId());
        comment.setUserId(userId);
        comment.setContent(request.content());

        if (request.parentId() != null) {
            if (!commentRepository.existsById(request.parentId())) {
                throw new RuntimeException("Komentar tidak ditemukan");
            }
            comment.setParentId(request.parentId());
        }

        PostComment savedComment = commentRepository.save(comment);
        UserCache userCache = userCacheRepository.findById(userId).orElse(null);

        return CommentResponse.fromEntity(savedComment, userCache);
    }

    public Page<CommentResponse> getCommentsByPostId(String postId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<PostComment> rootPage = commentRepository.findByPostIdAndParentIdIsNullOrderByCreatedAtDesc(postId, pageable);

        if (rootPage.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Long> rootIds = rootPage.getContent().stream()
                .map(PostComment::getId)
                .toList();

        List<PostComment> replies = commentRepository.findByParentIdInOrderByCreatedAtAsc(rootIds);

        // MENCEGAH N+1 QUERY: Kumpulkan semua unik User ID (Root & Replies) ---
        Set<String> userIds = new HashSet<>();
        rootPage.getContent().forEach(c -> userIds.add(c.getUserId()));
        replies.forEach(c -> userIds.add(c.getUserId()));

        // Get semua data profil UserCache sekaligus dalam 1 Query ---
        Map<String, UserCache> userCacheMap = userCacheRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(UserCache::getId, user -> user));

        List<CommentResponse> rootDtos = new ArrayList<>();
        Map<Long, CommentResponse> rootMap = new HashMap<>();

        // Mapping Root Comments (Sisipkan UserCache) ---
        for (PostComment root : rootPage.getContent()) {
            UserCache cache = userCacheMap.get(root.getUserId());
            CommentResponse dto = CommentResponse.fromEntity(root, cache);
            rootDtos.add(dto);
            rootMap.put(dto.id(), dto);
        }

        // Mapping Replies (Sisipkan UserCache) ---
        for (PostComment reply : replies) {
            CommentResponse parentDto = rootMap.get(reply.getParentId());
            if (parentDto != null) {
                UserCache cache = userCacheMap.get(reply.getUserId());
                parentDto.replies().add(CommentResponse.fromEntity(reply, cache));
            }
        }

        rootDtos.sort(
                Comparator.comparing((CommentResponse c) -> c.replies().isEmpty())
                        .thenComparing(CommentResponse::createdAt)
        );

        return new PageImpl<>(rootDtos, pageable, rootPage.getTotalElements());
    }

    public Long getCountByPostId(String postId) {
        return commentRepository.countByPostId(postId);
    }
}
