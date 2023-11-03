package com.example.split.dto;

import com.example.split.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactResponse {
    private Long transactId;
    private String description;
    private BigDecimal amount;
    private User fromUser;
    private User toUser;
    private boolean isPaid;
    private LocalDateTime last_updated;
    //    private User fromUser;
    //    private User toUser;
}
