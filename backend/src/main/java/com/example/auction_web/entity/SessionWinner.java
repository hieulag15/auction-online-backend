package com.example.auction_web.entity;

import com.example.auction_web.entity.auth.User;
import com.example.auction_web.enums.SESSION_WIN_STATUS;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class SessionWinner {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String sessionWinnerId;

    // @ManyToOne
    // @JoinColumn(name = "userId", referencedColumnName = "userId")
    // User user;

    // @ManyToOne
    // @JoinColumn(name = "auctionSessionId", referencedColumnName = "auctionSessionId")
    // AuctionSession auctionSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "userId", nullable = false)
    User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auctionSessionId", referencedColumnName = "auctionSessionId", nullable = false, unique = true)
    AuctionSession auctionSession;

    LocalDateTime victoryTime;
    BigDecimal price;
    String status;
    Boolean delFlag;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.delFlag = false;
        this.status = SESSION_WIN_STATUS.PENDING_PAYMENT.toString();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
