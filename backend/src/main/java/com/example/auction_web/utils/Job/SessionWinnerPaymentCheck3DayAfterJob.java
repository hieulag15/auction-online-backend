package com.example.auction_web.utils.Job;

import com.example.auction_web.WebSocket.service.NotificationStompService;
import com.example.auction_web.dto.request.BalanceHistoryCreateRequest;
import com.example.auction_web.dto.response.SessionWinnerResponse;
import com.example.auction_web.enums.ACTIONBALANCE;
import com.example.auction_web.enums.SESSION_WIN_STATUS;
import com.example.auction_web.exception.AppException;
import com.example.auction_web.exception.ErrorCode;
import com.example.auction_web.mapper.*;
import com.example.auction_web.repository.*;
import com.example.auction_web.repository.auth.UserRepository;
import com.example.auction_web.service.BalanceHistoryService;
import com.example.auction_web.service.SessionWinnerService;
import com.example.auction_web.service.auth.UserService;
import com.example.auction_web.utils.RefundPaymentAfter3DayUtil;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class SessionWinnerPaymentCheck3DayAfterJob implements Job {
    @Autowired
    private final SessionWinnerService sessionWinnerService;
    @Autowired
    private final BalanceHistoryService balanceHistoryService;
    RefundPaymentAfter3DayUtil refundPaymentAfter3DayUtil;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String sessionWinnerId = context.getJobDetail().getJobDataMap().getString("sessionWinnerId");
        var sessionWinner = sessionWinnerService.getSessionWinnerById(sessionWinnerId);

        if (sessionWinner.getStatus().equals(SESSION_WIN_STATUS.PAYMENT_SUCCESSFUL.toString())) {
            refundPaymentAfter3DayUtil.RefundPaymentAfter3DayPayment(sessionWinner);
        }
    }
}
