package com.mentora.content_service.model.dto;

import jakarta.validation.constraints.*;

public record PostCreateRequest(
        @NotNull(message = "Category ID wajib dipilih")
        Long categoryId,

        @NotBlank(message = "Konten tidak boleh kosong")
        @Size(min = 10, max = 5000, message = "Konten minimal 10 karakter")
        String content,

        // Optional (bisa null)
        String mediaUrl,
        String mediaType // "IMAGE", "VIDEO", "NONE"
) {}
