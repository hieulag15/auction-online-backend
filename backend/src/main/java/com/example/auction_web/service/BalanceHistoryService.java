package com.example.auction_web.service;

import com.example.auction_web.dto.response.BalanceHistoryResponse;
import com.example.auction_web.dto.response.BalanceSumaryResponse;
import com.example.auction_web.dto.response.SessionWinnerResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface BalanceHistoryService {
    List<BalanceHistoryResponse> getAllBalanceHistoriesByBalanceUserId(String balanceUserId);
    List<BalanceHistoryResponse> getAllBalanceHistoriesByUserId(String userId);
    void paymentSession(String buyerId, String sellerId, String sessionId, String addressId);
    void comletedPaymentSession(String buyerId, String sellerId, String sessionId);
    void cancelSession(String sellerId, String sessionId);
    List<BalanceHistoryResponse> getAllBalanceHistoriesByBalanceUserAdmin();
    List<BalanceSumaryResponse> getBalanceSummary(String balanceUserId, LocalDateTime startDate, LocalDateTime endDate);
}
