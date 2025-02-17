package com.Ahmed.Banking.models;


import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity
public class Transaction {

    @Id
    @GeneratedValue
    private Integer id;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @CreatedDate
    @Column(
        nullable = false,
        updatable = false
    )
    private LocalDateTime creationDate;

    @ManyToOne
    @JoinColumn(name="id_compte")
    private Compte compte;

}
