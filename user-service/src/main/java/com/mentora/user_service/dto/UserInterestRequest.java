package com.mentora.user_service.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UserInterestRequest {
    private Set<Long> categoryIds; // Contoh input: [1, 3, 5]
}
