package com.mentora.communication_service.model;

import com.mentora.communication_service.enums.CallStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "call_logs")
@Data
public class CallLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String callerId; // ID User yang menelpon (Mentee)

    @Column(nullable = false)
    private String mentorId; // ID User yang ditelpon (Mentor)

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer durationSeconds = 0;

    @Enumerated(EnumType.STRING)
    private CallStatus status = CallStatus.COMPLETED;

    @PrePersist
    protected void onCreate() {
        this.startTime = LocalDateTime.now();
    }
}