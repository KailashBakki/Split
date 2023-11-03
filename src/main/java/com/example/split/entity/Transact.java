package com.example.split.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;


import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "transact")
public class Transact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactId;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private BigDecimal amount;
    @ManyToOne
    @JoinColumn(name = "from_user_id"/*, referencedColumnName = "userId"*/, nullable = false)
    private User fromUser;//Long

    @ManyToOne
    @JoinColumn(name = "to_user_id"/*, referencedColumnName = "userId"*/,nullable = false)
    private User toUser;//Long
    @Column(nullable = false)
    private boolean isPaid;

//    @CreationTimestamp
//    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_updated")//,nullable = false)
    private LocalDateTime last_updated;

//    @Column(nullable = false)
//    private boolean active;
//    @PrePersist
//    public void prePersist() { active = true; }

}
