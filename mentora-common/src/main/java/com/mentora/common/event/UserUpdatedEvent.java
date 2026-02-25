package com.mentora.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdatedEvent {
    private String userId;
    private String fullName;
    private String profilePictureUrl;
}