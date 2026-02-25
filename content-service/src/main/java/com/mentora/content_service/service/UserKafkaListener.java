package com.mentora.content_service.service;

import com.mentora.common.event.UserUpdatedEvent;
import com.mentora.content_service.model.UserCache;
import com.mentora.content_service.repository.UserCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserKafkaListener {

    private final UserCacheRepository userCacheRepository;

    @KafkaListener(topics = "user-updates", groupId = "content-service-group")
    public void handleUserUpdated(UserUpdatedEvent event) {
        System.out.println("Menerima pesan sinkronisasi user: " + event.getFullName());

        UserCache userCache = new UserCache();
        userCache.setId(event.getUserId());
        userCache.setFullName(event.getFullName());
        userCache.setProfilePictureUrl(event.getProfilePictureUrl());

        // Simpan atau Update ke database lokal
        userCacheRepository.save(userCache);

        System.out.println("Berhasil memperbarui cache user ID: " + event.getUserId());
    }
}