package com.mentora.common.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({ "success", "message", "data", "meta" })
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private Object meta;

    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.meta = null;
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    public static <T> ApiResponse<List<T>> successPage(String message, Page<T> page) {
        return new ApiResponse<>(
                true,
                message,
                page.getContent(),
                PageMeta.from(page)
        );
    }
}