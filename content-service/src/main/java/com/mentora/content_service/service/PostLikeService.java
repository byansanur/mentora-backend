package com.mentora.content_service.service;

import com.mentora.content_service.model.PostLike;
import com.mentora.content_service.repository.PostLikeRepository;
import com.mentora.content_service.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;

    public boolean isPostExistById(String postId) {
        return postRepository.existsById(postId);
    }

    public Optional<PostLike> findByPostIdAndUserId(String postId, String userId) {
        return postLikeRepository.findByPostIdAndUserId(postId, userId);
    }

    @Transactional
    public String toggleLike(String postId, String userId) {
        Optional<PostLike> existingLike = postLikeRepository.findByPostIdAndUserId(postId, userId);

        if (existingLike.isPresent()) {
            postLikeRepository.delete(existingLike.get());
            return "Unliked";
        } else {
            PostLike newLike = new PostLike();
            newLike.setPostId(postId);
            newLike.setUserId(userId);
            postLikeRepository.save(newLike);
            return "Liked";
        }
    }

    public long likesCount(String postId) {
        return postLikeRepository.countByPostId(postId);
    }
}
