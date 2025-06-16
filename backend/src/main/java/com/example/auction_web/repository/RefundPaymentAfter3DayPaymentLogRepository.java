package com.example.auction_web.repository;

import com.example.auction_web.entity.ScheduleLog.NotificationLog;
import com.example.auction_web.entity.ScheduleLog.RefundPaymentAfter3DayPaymentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefundPaymentAfter3DayPaymentLogRepository extends JpaRepository<RefundPaymentAfter3DayPaymentLog, String> {
    RefundPaymentAfter3DayPaymentLog findRefundPaymentAfter3DayPaymentLogBySessionWinnerId(String sessionWinnerId);
    List<RefundPaymentAfter3DayPaymentLog> findRefundPaymentAfter3DayPaymentLogsByStatus(NotificationLog.NotificationStatus status);
}
