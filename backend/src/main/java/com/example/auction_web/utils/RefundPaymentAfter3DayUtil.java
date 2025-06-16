package com.example.auction_web.utils;

import com.example.auction_web.dto.request.BalanceHistoryCreateRequest;
import com.example.auction_web.dto.response.SessionWinnerResponse;
import com.example.auction_web.enums.ACTIONBALANCE;
import com.example.auction_web.exception.AppException;
import com.example.auction_web.exception.ErrorCode;
import com.example.auction_web.mapper.AuctionSessionMapper;
import com.example.auction_web.mapper.BalanceHistoryMapper;
import com.example.auction_web.mapper.BalanceUserMapper;
import com.example.auction_web.repository.AuctionSessionRepository;
import com.example.auction_web.repository.BalanceHistoryRepository;
import com.example.auction_web.repository.BalanceUserRepository;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;

public class RefundPaymentAfter3DayUtil {
    @NonFinal
    @Value("${email.username}")
    private String EMAIL_ADMIN;

    BalanceHistoryRepository balanceHistoryRepository;
    BalanceUserRepository balanceUserRepository;

    BalanceHistoryMapper balanceHistoryMapper;
    BalanceUserMapper balanceUserMapper;
    AuctionSessionMapper auctionSessionMapper;

    AuctionSessionRepository auctionSessionRepository;

    public void RefundPaymentAfter3DayPayment(SessionWinnerResponse sessionWinner) {
        var balanceUser = balanceUserMapper.toBalanceUserResponse(balanceUserRepository.findBalanceUserByUser_UserId(sessionWinner.getUser().getUserId()));
        var balanceUserAdmin = balanceUserMapper.toBalanceUserResponse(balanceUserRepository.findBalanceUserByUser_Email(EMAIL_ADMIN));

        var session = auctionSessionMapper.toAuctionItemResponse(auctionSessionRepository.findById(sessionWinner.getAuctionSession().getAuctionSessionId()).get());

        // Refund deposit
        balanceUserRepository.increaseBalance(balanceUser.getBalanceUserId(), session.getDepositAmount());
        addBalanceHistory(balanceUser.getBalanceUserId(), session.getDepositAmount(), "Hoàn tiền đặt cọc phiên " + session.getName(), ACTIONBALANCE.ADD);

        balanceUserRepository.minusBalance(balanceUserAdmin.getBalanceUserId(), session.getDepositAmount());
        addBalanceHistory(balanceUserAdmin.getBalanceUserId(), session.getDepositAmount(), "Hoàn tiền đặt cọc phiên " + session.getName(), ACTIONBALANCE.SUBTRACT);

        // Refund payment
        balanceUserRepository.increaseBalance(balanceUser.getBalanceUserId(), sessionWinner.getPrice());
        addBalanceHistory(balanceUser.getBalanceUserId(), sessionWinner.getPrice(), "Hoàn tiền thanh toán phiên " + session.getName(), ACTIONBALANCE.ADD);

        balanceUserRepository.minusBalance(balanceUserAdmin.getBalanceUserId(), sessionWinner.getPrice());
        addBalanceHistory(balanceUserAdmin.getBalanceUserId(), sessionWinner.getPrice(), "Hoàn tiền thanh toán phiên " + session.getName(), ACTIONBALANCE.SUBTRACT);
    }

    void addBalanceHistory(String BalanceUserId, BigDecimal amount, String Description, ACTIONBALANCE action) {
        var balanceUser = balanceUserRepository.findById(BalanceUserId)
                .orElseThrow(() -> new AppException(ErrorCode.BALANCE_USER_NOT_EXISTED));
        BalanceHistoryCreateRequest request = BalanceHistoryCreateRequest.builder()
                .amount(amount)
                .description(Description)
                .actionbalance(action)
                .build();
        var balanceHistory = balanceHistoryMapper.toBalanceHistory(request);
        balanceHistory.setBalanceUser(balanceUser);
        balanceHistoryRepository.save(balanceHistory);
    }
}
