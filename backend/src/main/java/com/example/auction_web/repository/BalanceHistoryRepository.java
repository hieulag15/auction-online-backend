package com.example.auction_web.repository;

import com.example.auction_web.dto.response.BalanceSumaryResponse;
import com.example.auction_web.entity.BalanceHistory;
import com.example.auction_web.enums.ACTIONBALANCE;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BalanceHistoryRepository extends JpaRepository<BalanceHistory, String> {
    List<BalanceHistory> findBalanceHistoriesByBalanceUser_BalanceUserId(String balanceUserId);
    List<BalanceHistory> findBalanceHistoriesByBalanceUser_User_UserIdOrderByCreatedAtDesc(String userId);

    @Query(value = """
SELECT DATE(b.created_at) AS date,
       b.balance_user_id,
       SUM(b.amount) AS total_amount
FROM balance_history b
WHERE b.balance_user_id = :balanceUserId
  AND b.created_at BETWEEN :startDate AND :endDate
GROUP BY DATE(b.created_at), b.balance_user_id
ORDER BY DATE(b.created_at)
""", nativeQuery = true)
    List<Object[]> getBalanceSummaryNative(
            @Param("balanceUserId") String balanceUserId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT COALESCE(SUM(b.amount), 0) FROM BalanceHistory b WHERE b.actionbalance = :action AND b.delFlag = false AND b.balanceUser.balanceUserId = :balanceUserId")
    BigDecimal getTotalRevenueByActionAndUser(@Param("action") ACTIONBALANCE action, @Param("balanceUserId") String balanceUserId);

    @Query("SELECT COALESCE(SUM(b.amount), 0) FROM BalanceHistory b " +
       "WHERE b.actionbalance = :action " +
       "AND b.delFlag = false " +
       "AND b.balanceUser.balanceUserId = :balanceUserId " +
       "AND FUNCTION('YEAR', b.createdAt) = :year")
    BigDecimal getTotalRevenueByActionAndUserAndYear(
        @Param("action") ACTIONBALANCE action,
        @Param("balanceUserId") String balanceUserId,
        @Param("year") int year);

}
