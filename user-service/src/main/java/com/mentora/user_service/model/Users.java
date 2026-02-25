package com.mentora.user_service.model;

import com.mentora.user_service.enums.SubscriptionType;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false)
    private String passwordHash;

    private String profilePictureUrl;

    @Enumerated(EnumType.STRING)
    private SubscriptionType subscriptionType = SubscriptionType.FREE;

    private Integer dailyCallQuotaSeconds = 600;

    private LocalDate lastQuotaResetDate;

    // --- PERUBAHAN MICROSERVICE ---
    // Kita tidak mapping ke Entity Category, tapi hanya simpan Set of Long (ID Kategori)
    @ElementCollection
    @CollectionTable(
            name = "user_interests",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "category_id")
    private Set<Long> interestCategoryIds = new HashSet<>();

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
