package com.mentora.user_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_devices")
@Data
public class UserDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String deviceId;

    @Column(nullable = false)
    private String platform; // ANDROID, IOS, WEB

    private String fcmToken; // Opsional: Untuk Push Notification nanti

    private LocalDateTime lastLogin;

    @Column(nullable = false)
    private boolean isActive = true; // Jika user logout, ini jadi false
}