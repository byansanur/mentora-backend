package com.mentora.user_service.service;

import com.mentora.common.event.UserUpdatedEvent;
import com.mentora.user_service.dto.ChangePasswordRequest;
import com.mentora.user_service.dto.UpdateProfileRequest;
import com.mentora.user_service.model.UserDevice;
import com.mentora.user_service.model.Users;
import com.mentora.user_service.repository.UserDeviceRepository;
import com.mentora.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final UserDeviceRepository userDeviceRepository;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String USER_TOPIC = "user-updates";

    public Users registerUser(Users userRequest) {
        Users newUser = new Users();
        newUser.setEmail(userRequest.getEmail());
        newUser.setFullName(userRequest.getFullName());
        String hashedPassword = passwordEncoder.encode(userRequest.getPasswordHash());
        newUser.setPasswordHash(hashedPassword);

        Users savedUser = userRepository.save(newUser);

        sendUserUpdateEvent(savedUser);
        return savedUser;
    }

    public Users login(String email, String rawPassword) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email tidak ditemukan"));

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new RuntimeException("Password salah");
        }

        return user;
    }

    public void trackUserDevice(String userId, String deviceId, String platform) {
        if (deviceId == null || deviceId.isBlank() || platform == null || platform.isBlank()) {
            return;
        }

        UserDevice device = userDeviceRepository.findByUserIdAndDeviceId(userId, deviceId)
                .orElse(new UserDevice());

        device.setUserId(userId);
        device.setDeviceId(deviceId);
        device.setPlatform(platform);
        device.setLastLogin(LocalDateTime.now());
        device.setActive(true);

        userDeviceRepository.save(device);
    }

    public List<UserDevice> getActiveSessions(String userId) {
        return userDeviceRepository.findByUserIdAndIsActiveTrue(userId);
    }

    public Users updateProfile(String userId, UpdateProfileRequest request) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        if (request.fullName() != null) user.setFullName(request.fullName());
        if (request.profilePictureUrl() != null) user.setProfilePictureUrl(request.profilePictureUrl());

        Users updatedUser = userRepository.save(user);

        sendUserUpdateEvent(updatedUser);

        return updatedUser;
    }

    public void changePassword(String userId, ChangePasswordRequest request) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Password lama tidak sesuai");
        }

        if (!request.newPassword().equals(request.confirmNewPassword())) {
            throw new RuntimeException("Konfirmasi password baru tidak cocok");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }


    public boolean isEmailExists(String mail) {
        return userRepository.findByEmail(mail).isPresent();
    }

    public Optional<Users> findById(String userId) {
        return userRepository.findById(userId);
    }

    public void save(Users users) {
        userRepository.save(users);
    }

    private void sendUserUpdateEvent(Users user) {
        UserUpdatedEvent event = new UserUpdatedEvent(
                user.getId(),
                user.getFullName(),
                user.getProfilePictureUrl()
        );

        // Kirim ke topic 'user-updates' dengan Key = UserId
        kafkaTemplate.send(USER_TOPIC, user.getId(), event);
    }

    public void syncAllOldUsersToKafka() {
        List<Users> allUsers = userRepository.findAll();
        for (Users user : allUsers) {
            sendUserUpdateEvent(user); // Method helper yang sudah kita buat sebelumnya
        }
        System.out.println("Berhasil mengirim " + allUsers.size() + " user ke Kafka untuk sinkronisasi.");
    }
}
