package com.mentora.content_service.repository;

import com.mentora.content_service.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, String> {

    List<Post> findByCategoryIdIn(List<Long> categoryIds);

    Page<Post> findByCategoryIdInOrderByCreatedAtDesc(List<Long> categoryIds, Pageable pageable);
}
