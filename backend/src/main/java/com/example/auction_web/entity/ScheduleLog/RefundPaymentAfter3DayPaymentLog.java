package com.example.auction_web.entity.ScheduleLog;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class RefundPaymentAfter3DayPaymentLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String sessionWinnerId;

    private LocalDateTime scheduledTime;

    private LocalDateTime sentTime;

    @Enumerated(EnumType.STRING)
    private NotificationLog.NotificationStatus status;

    public enum NotificationStatus {
        SCHEDULED, SENT, FAILED
    }
}
