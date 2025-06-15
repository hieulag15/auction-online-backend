package com.example.auction_web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BalanceSumaryResponse {

    private LocalDate date;
    private String balanceUserId;
    private BigDecimal totalAmount;

    public BalanceSumaryResponse(LocalDate date, String balanceUserId, BigDecimal totalAmount) {
        this.date = date;
        this.balanceUserId = balanceUserId;
        this.totalAmount = totalAmount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getBalanceUserId() {
        return balanceUserId;
    }

    public void setBalanceUserId(String balanceUserId) {
        this.balanceUserId = balanceUserId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}

