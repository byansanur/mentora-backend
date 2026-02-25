package com.mentora.user_service.controller;

import com.mentora.common.dto.ApiResponse;
import com.mentora.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserService userService;

    @PostMapping("/admin/sync-kafka")
    public ResponseEntity<ApiResponse<?>> syncAllUsers() {
        userService.syncAllOldUsersToKafka();
        return ResponseEntity.ok(ApiResponse.success("Proses sinkronisasi massal sedang berjalan di background!", null));
    }
}
