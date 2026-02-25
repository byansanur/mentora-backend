package com.mentora.content_service.repository;

import com.mentora.content_service.model.PostComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<PostComment, Long> {
    long countByPostId(String postId);

    List<PostComment> findByParentIdInOrderByCreatedAtAsc(List<Long> rootIds);

    Page<PostComment> findByPostIdAndParentIdIsNullOrderByCreatedAtDesc(String postId, Pageable pageable);

    // Hitung total komentar untuk BANYAK postingan sekaligus (Perhatikan entitasnya PostComment)
    @Query("SELECT c.postId AS postId, COUNT(c.id) AS count FROM PostComment c WHERE c.postId IN :postIds GROUP BY c.postId")
    List<PostCountProjection> countCommentsByPostIds(@Param("postIds") List<String> postIds);
}
