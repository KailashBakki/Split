package com.example.split.dto;

import com.example.split.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactRequest {
    private String description;
    private BigDecimal amount;
//    private User fromUser;
    private User fromUser;
    private User toUser;
    private String isPaid;
//    private String created_at;
}
