package com.mentora.content_service.model;

import com.mentora.content_service.enums.MediaType;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Data
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // MICROSERVICE PATTERN:
    // Tidak ada @ManyToOne User user;
    // Kita hanya menyimpan ID-nya saja sebagai referensi.
    @Column(nullable = false)
    private String userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String mediaUrl;

    @Enumerated(EnumType.STRING)
    private MediaType mediaType = MediaType.NONE;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}