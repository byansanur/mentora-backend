package com.mentora.user_service.controller;

import com.mentora.common.dto.ApiResponse;
import com.mentora.user_service.dto.*;
import com.mentora.user_service.model.Users;
import com.mentora.user_service.repository.UserRepository;
import com.mentora.user_service.security.JwtUtils;
import com.mentora.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    @GetMapping("/hello")
    public String hello() {
        return "Halo! Ini respon dari User Service via Gateway.";
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Users userRequest) {
        if (userService.isEmailExists(userRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email sudah terdaftar!");
        }

        var newUser = userService.registerUser(userRequest);

        return ResponseEntity.ok("User berhasil didaftarkan dengan ID: " + newUser.getId());
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> loginUser(@RequestBody LoginRequest request) {
        try {
            Users loggedInUser = userService.login(request.email(), request.password());

            String token = jwtUtils.generateToken(
                    loggedInUser.getId(),
                    loggedInUser.getEmail(),
                    loggedInUser.getFullName()
            );

            userService.trackUserDevice(
                    loggedInUser.getId(),
                    request.deviceId(),
                    request.platform()
            );

            LoginResponse responseData = LoginResponse.fromEntity(token, loggedInUser);

            return ResponseEntity.ok(ApiResponse.success("Login berhasil", responseData));

        } catch (RuntimeException e) {
            // Jika password salah atau email tidak ditemukan
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(@PathVariable String userId) {
        return userService.findById(userId)
                .map(user -> ResponseEntity.ok(ApiResponse.success("Profil ditemukan", UserProfileResponse.fromEntity(user))))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @PathVariable String userId,
            @RequestBody UpdateProfileRequest request) {
        Users updatedUser = userService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Profil berhasil diperbarui", UserProfileResponse.fromEntity(updatedUser)));
    }

    @PutMapping("/{userId}/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @PathVariable String userId,
            @RequestBody ChangePasswordRequest request) {
        try {
            userService.changePassword(userId, request);
            return ResponseEntity.ok(ApiResponse.success("Password berhasil diubah", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{userId}/interests")
    public ResponseEntity<?> addInterests(@PathVariable String userId, @RequestBody UserInterestRequest request) {
        Optional<Users> userOpt = userService.findById(userId);

        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Users user = userOpt.get();

        // Mengisi Set<Long> interestCategoryIds yang ada di Entity User
        // Ini akan otomatis masuk ke tabel 'user_interests' berkat @ElementCollection
        user.getInterestCategoryIds().addAll(request.getCategoryIds());

        userService.save(user);

        return ResponseEntity.ok("Minat berhasil disimpan!");
    }

}
