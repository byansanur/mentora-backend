package com.mentora.content_service.repository;

import com.mentora.content_service.model.UserCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCacheRepository extends JpaRepository<UserCache, String> {
}