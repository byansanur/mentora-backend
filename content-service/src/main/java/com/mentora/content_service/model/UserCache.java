package com.mentora.content_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "user_cache")
@Data
public class UserCache {
    @Id
    private String id; // ID ini akan sama dengan ID di User Service
    private String fullName;
    private String profilePictureUrl;
}