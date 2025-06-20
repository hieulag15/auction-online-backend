package com.example.auction_web.controller;

import com.example.auction_web.dto.request.PaymentSessionDTO;
import com.example.auction_web.dto.response.ApiResponse;
import com.example.auction_web.dto.response.BalanceHistoryResponse;
import com.example.auction_web.dto.response.BalanceSumaryResponse;
import com.example.auction_web.dto.response.statistical.RevenueCountResponse;
import com.example.auction_web.service.BalanceHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/balance-history")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class BalanceHistoryController {
    BalanceHistoryService balanceHistoryService;

    @GetMapping("/user/{userId}")
    public ApiResponse<List<BalanceHistoryResponse>> getAllBalanceHistoriesByUserId(@PathVariable String userId) {
        return ApiResponse.<List<BalanceHistoryResponse>>builder()
                .code(HttpStatus.OK.value())
                .result(balanceHistoryService.getAllBalanceHistoriesByUserId(userId))
                .build();
    }

    @PostMapping("/payment-session")
    public ApiResponse<String> paymentSession(@RequestBody PaymentSessionDTO request) {

            balanceHistoryService.paymentSession(request.getBuyerId(), request.getSellerId(), request.getSessionId(), request.getAddressId());
            return ApiResponse.<String>builder()
                    .code(HttpStatus.OK.value())
                    .result("Thanh toán thành công")
                    .build();

    }

    @PostMapping("/completed-payment-session")
    public ApiResponse<String> completedPaymentSession(@RequestBody PaymentSessionDTO request) {

            balanceHistoryService.comletedPaymentSession(request.getBuyerId(), request.getSellerId(), request.getSessionId());
            return ApiResponse.<String>builder()
                    .code(HttpStatus.OK.value())
                    .result("Hoàn tất thanh toán")
                    .build();

    }

    @PostMapping("/cancel-payment-session")
    public ApiResponse<String> cancelPaymentSession(@RequestBody PaymentSessionDTO request) {
        try {
            balanceHistoryService.cancelSession(request.getSellerId(), request.getSessionId());
            return ApiResponse.<String>builder()
                    .code(HttpStatus.OK.value())
                    .result("Hủy thanh toán thành công")
                    .build();
        } catch (Exception ex) {
            return ApiResponse.<String>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message(ex.getMessage())
                    .build();
        }
    }

    @GetMapping("/balance_history_admin")
    public ApiResponse<List<BalanceHistoryResponse>> getBalanceHistoryAdmin() {
        try {
           return ApiResponse.<List<BalanceHistoryResponse>>builder()
                   .code(HttpStatus.OK.value())
                   .result(balanceHistoryService.getAllBalanceHistoriesByBalanceUserAdmin())
                   .build();
        } catch (Exception ex) {
            return ApiResponse.<List<BalanceHistoryResponse>>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message(ex.getMessage())
                    .result(Collections.emptyList())
                    .build();
        }
    }

    @GetMapping("/balance_history_summary")
    public ApiResponse<List<BalanceSumaryResponse>> getSummary(@PathVariable String balanceUserId, @PathVariable String startTime, @PathVariable String endTime) {
        try {
            return ApiResponse.<List<BalanceSumaryResponse>>builder()
                    .code(HttpStatus.OK.value())
                    .result(balanceHistoryService.getBalanceSummary(balanceUserId, LocalDateTime.parse(startTime), LocalDateTime.parse(endTime)))
                    .build();
        } catch (Exception ex) {
            return ApiResponse.<List<BalanceSumaryResponse>>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message(ex.getMessage())
                    .result(Collections.emptyList())
                    .build();
        }
    }

    @GetMapping("/manager/total-revenue")
    public ApiResponse<RevenueCountResponse> getTotalRevenueByManager() {
        BigDecimal totalRevenue = balanceHistoryService.getTotalRevenueByManager();
        int currentYear = java.time.Year.now().getValue();
        BigDecimal revenueOfYear = balanceHistoryService.getTotalRevenueByManagerAndYear(currentYear);
        double growthRate = balanceHistoryService.getRevenueGrowthRateThisYear();
        RevenueCountResponse response = new RevenueCountResponse(totalRevenue, revenueOfYear, growthRate);
        return ApiResponse.<RevenueCountResponse>builder()
                .code(HttpStatus.OK.value())
                .result(response)
                .build();
    }
}
