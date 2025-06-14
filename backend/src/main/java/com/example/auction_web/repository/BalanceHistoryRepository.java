package com.example.auction_web.repository;

import com.example.auction_web.dto.response.BalanceSumaryResponse;
import com.example.auction_web.entity.BalanceHistory;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BalanceHistoryRepository extends JpaRepository<BalanceHistory, String> {
    List<BalanceHistory> findBalanceHistoriesByBalanceUser_BalanceUserId(String balanceUserId);
    List<BalanceHistory> findBalanceHistoriesByBalanceUser_User_UserIdOrderByCreatedAtDesc(String userId);

    @Query("""
        SELECT new com.example.auction_web.dto.response.BalanceSumaryResponse(
            DATE(b.createdAt), 
            b.actionbalance, 
            SUM(b.amount)
        )
        FROM BalanceHistory b
        WHERE b.balanceUser.balanceUserId = :balanceUserId
          AND b.createdAt BETWEEN :startDate AND :endDate
        GROUP BY DATE(b.createdAt), b.actionbalance
        ORDER BY DATE(b.createdAt) ASC
    """)
    List<BalanceSumaryResponse> getBalanceSummaryByDateRangeAndUser(
            @Param("balanceUserId") String balanceUserId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

}
