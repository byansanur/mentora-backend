package com.mentora.content_service.repository;

import com.mentora.content_service.model.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    // Query ajaib untuk cek status like
    Optional<PostLike> findByPostIdAndUserId(String postId, String userId);

    // Hitung total like (berguna untuk tampilan feed nanti)
    long countByPostId(String postId);

    // Hitung total like untuk BANYAK postingan sekaligus
    @Query("SELECT pl.postId AS postId, COUNT(pl.id) AS count FROM PostLike pl WHERE pl.postId IN :postIds GROUP BY pl.postId")
    List<PostCountProjection> countLikesByPostIds(@Param("postIds") List<String> postIds);

    // Cari postingan mana saja yang sudah di-like oleh user ini (dari daftar postIds)
    @Query("SELECT pl.postId FROM PostLike pl WHERE pl.postId IN :postIds AND pl.userId = :userId")
    List<String> findLikedPostIdsByUser(@Param("postIds") List<String> postIds, @Param("userId") String userId);
}
