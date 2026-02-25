package com.mentora.user_service.repository;

import com.mentora.user_service.model.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {
    Optional<UserDevice> findByUserIdAndDeviceId(String userId, String deviceId);

    List<UserDevice> findByUserIdAndIsActiveTrue(String userId);
}