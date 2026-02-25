package com.mentora.content_service.service;

import com.mentora.content_service.enums.MediaType;
import com.mentora.content_service.model.Category;
import com.mentora.content_service.model.Post;
import com.mentora.content_service.model.UserCache;
import com.mentora.content_service.model.dto.PostCreateRequest;
import com.mentora.content_service.model.dto.PostDetailResponse;
import com.mentora.content_service.model.dto.PostFeedResponse;
import com.mentora.content_service.repository.*;
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
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final CategoryRepository categoryRepository;
    private final CommentRepository commentRepository;
    private final UserCacheRepository userCacheRepository;

    public Page<PostFeedResponse> getFeed(List<Long> categoryIds, int page, int size, String viewerId) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Post> postPage = postRepository.findByCategoryIdInOrderByCreatedAtDesc(categoryIds, pageable);

        if (postPage.isEmpty()) {
            return Page.empty(pageable);
        }

        List<String> postIds = postPage.getContent().stream()
                .map(Post::getId)
                .toList();

        List<String> userIds = postPage.getContent().stream()
                .map(Post::getUserId)
                .distinct() // Hindari query berulang untuk user yang sama
                .toList();

        Map<String, UserCache> userCacheMap = userCacheRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(UserCache::getId, user -> user));

        Map<String, Long> likesMap = postLikeRepository.countLikesByPostIds(postIds).stream()
                .collect(Collectors.toMap(PostCountProjection::getPostId, PostCountProjection::getCount));

        // Total Comments secara massal
        Map<String, Long> commentsMap = commentRepository.countCommentsByPostIds(postIds).stream()
                .collect(Collectors.toMap(PostCountProjection::getPostId, PostCountProjection::getCount));

        // Cek status "Liked By Me"
        Set<String> likedByMeSet = (viewerId != null && !viewerId.equals("guest"))
                ? new HashSet<>(postLikeRepository.findLikedPostIdsByUser(postIds, viewerId))
                : new HashSet<>();

        // Mapping ke DTO (Kirim juga data userCache ke method fromEntity)
        List<PostFeedResponse> feedResponses = postPage.getContent().stream().map(post -> {
            long likes = likesMap.getOrDefault(post.getId(), 0L);
            long comments = commentsMap.getOrDefault(post.getId(), 0L);
            boolean isLiked = likedByMeSet.contains(post.getId());

            // Ambil cache profil dari map
            UserCache userCache = userCacheMap.get(post.getUserId());

            return PostFeedResponse.fromEntity(post, userCache, likes, comments, isLiked);
        }).toList();

        return new PageImpl<>(feedResponses, pageable, postPage.getTotalElements());
    }

    @Transactional
    public PostFeedResponse createPost(PostCreateRequest request, String userIdFromHeader) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new RuntimeException("Category ID tidak ditemukan"));

        Post post = new Post();
        post.setUserId(userIdFromHeader);

        post.setCategory(category);

        post.setContent(request.content());
        post.setMediaUrl(request.mediaUrl());
        post.setMediaType(MediaType.valueOf(request.mediaType() == null ? "NONE" : request.mediaType()));

        Post savedPost = postRepository.save(post);
        UserCache userCache = userCacheRepository.findById(userIdFromHeader).orElse(null);

        return PostFeedResponse.fromEntity(savedPost, userCache, 0L, 0L, false);
    }

    public PostDetailResponse getPostDetail(String postId, String currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Postingan tidak ditemukan"));

        // Hitung statistik real-time
        long totalLikes = postLikeRepository.countByPostId(postId);
        long totalComments = commentRepository.countByPostId(postId);
        boolean isLikedByMe = postLikeRepository.findByPostIdAndUserId(postId, currentUserId).isPresent();
        UserCache userCache = userCacheRepository.findById(post.getUserId()).get();

        return PostDetailResponse.fromEntity(post, userCache, totalLikes, totalComments, isLikedByMe);
    }
}
