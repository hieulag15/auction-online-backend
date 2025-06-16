package com.example.auction_web.utils.Quataz;

import com.example.auction_web.entity.ScheduleLog.NotificationLog;
import com.example.auction_web.entity.ScheduleLog.RefundPaymentAfter3DayPaymentLog;
import com.example.auction_web.repository.RefundPaymentAfter3DayPaymentLogRepository;
import com.example.auction_web.service.SessionWinnerService;
import com.example.auction_web.utils.Job.AuctionSessionStartJob;
import com.example.auction_web.utils.RefundPaymentAfter3DayUtil;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentAutoService {
    private final Scheduler scheduler;
    private final RefundPaymentAfter3DayPaymentLogRepository refundPaymentAfter3DayPaymentLogRepository;
    SessionWinnerService sessionWinnerService;
    RefundPaymentAfter3DayUtil refundPaymentAfter3DayUtil;

    public void scheduleRefundPaymentAfter3DayPayment(String sessionWinnerId, LocalDateTime date) {
        JobKey jobKey = new JobKey("refundPaymentAfter3DayPaymentJob-" + sessionWinnerId, "RefundPaymentGroup");
        TriggerKey triggerKey = new TriggerKey("refundPaymentAfter3DayPaymentTrigger-" + sessionWinnerId, "RefundPaymentGroup");

        try {
            // Kiểm tra nếu Job đã tồn tại
            if (scheduler.checkExists(jobKey)) {
                System.out.println("Job đã tồn tại: " + jobKey);
                return;
            }

            JobDetail jobDetail = JobBuilder.newJob(AuctionSessionStartJob.class)
                    .withIdentity(jobKey)
                    .usingJobData("sessionWinnerId", sessionWinnerId)
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .startAt(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()))
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);

            // Kiểm tra và lưu SessionLog nếu chưa tồn tại
            if (refundPaymentAfter3DayPaymentLogRepository.findRefundPaymentAfter3DayPaymentLogBySessionWinnerId(sessionWinnerId) == null) {
                RefundPaymentAfter3DayPaymentLog refundPaymentAfter3DayPaymentLog = new RefundPaymentAfter3DayPaymentLog();
                refundPaymentAfter3DayPaymentLog.setSessionWinnerId(sessionWinnerId);
                refundPaymentAfter3DayPaymentLog.setScheduledTime(date);
                refundPaymentAfter3DayPaymentLog.setStatus(NotificationLog.NotificationStatus.SCHEDULED);
                refundPaymentAfter3DayPaymentLogRepository.save(refundPaymentAfter3DayPaymentLog);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public void reschedulePendingRefundPaymentAfter3DayPayment() {
        List<RefundPaymentAfter3DayPaymentLog> pendingRefundPaymentAfter3DayPayment = refundPaymentAfter3DayPaymentLogRepository.findRefundPaymentAfter3DayPaymentLogsByStatus(NotificationLog.NotificationStatus.SCHEDULED);
        pendingRefundPaymentAfter3DayPayment.forEach(this::rescheduleRefundPaymentAfter3DayPayment);
    }

    private void rescheduleRefundPaymentAfter3DayPayment(RefundPaymentAfter3DayPaymentLog refundPaymentAfter3DayPaymentLog) {
        LocalDateTime notificationTime = refundPaymentAfter3DayPaymentLog.getScheduledTime();
        String sessionWinnerId = refundPaymentAfter3DayPaymentLog.getSessionWinnerId();

        if (notificationTime.isAfter(LocalDateTime.now()) && refundPaymentAfter3DayPaymentLog.getStatus() == NotificationLog.NotificationStatus.SCHEDULED) {
            scheduleRefundPaymentAfter3DayPayment(sessionWinnerId, notificationTime);
        } else if (notificationTime.isBefore(LocalDateTime.now()) && refundPaymentAfter3DayPaymentLog.getStatus() == NotificationLog.NotificationStatus.SCHEDULED) {
            refundPaymentAfter3DayPaymentLog.setStatus(NotificationLog.NotificationStatus.SENT);
            refundPaymentAfter3DayPaymentLogRepository.save(refundPaymentAfter3DayPaymentLog);

            var sessionWinner = sessionWinnerService.getSessionWinnerById(sessionWinnerId);
            refundPaymentAfter3DayUtil.RefundPaymentAfter3DayPayment(sessionWinner);
        } else {
            refundPaymentAfter3DayPaymentLog.setStatus(NotificationLog.NotificationStatus.FAILED);
            refundPaymentAfter3DayPaymentLogRepository.save(refundPaymentAfter3DayPaymentLog);
        }
    }

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent(ContextRefreshedEvent event) {
        reschedulePendingRefundPaymentAfter3DayPayment();
    }

}
