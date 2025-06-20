package com.example.auction_web.dto.response.statistical;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class RevenueCountResponse {
    private BigDecimal totalRevenue;
    private BigDecimal revenueOfYear;
    private double growthRate;
}
